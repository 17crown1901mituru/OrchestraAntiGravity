#!/data/data/com.termux/files/usr/bin/bash
# AntiGravity End-to-End Termux Operations & Android Build Script
# Runs: Tool checks -> Keystore/SHA-256 -> Cloudflare Wrangler Secrets/Deploy
#       -> GCP KMS/CMEK tfstate + Secret Manager -> Terraform -> Android Build (APK/AAB + Unit Tests)

set -euo pipefail

# ==============================================================================
# 0. Configurable Environment Variables (override before running as needed)
# ==============================================================================
GCP_PROJECT_ID="${GCP_PROJECT_ID:-gen-lang-client-0439741065}"
GCP_REGION="${GCP_REGION:-us-central1}"
ALLOWED_EMAIL="${ALLOWED_EMAIL:-17crown2223@gmail.com}"
TFSTATE_BUCKET="${TFSTATE_BUCKET:-antigravity-tfstate-prod-${GCP_PROJECT_ID}}"
GATEWAY_HEALTH_URL="${GATEWAY_HEALTH_URL:-https://gc2223.com/health}"

BUILD_TYPE="${BUILD_TYPE:-debug}"          # debug | release | all
RUN_TESTS="${RUN_TESTS:-true}"             # true | false
RUN_CF="${RUN_CF:-true}"                   # true | false
RUN_GCP_TF="${RUN_GCP_TF:-true}"           # true | false
TF_APPLY="${TF_APPLY:-false}"              # true to run terraform apply (default plan-only)
CLOUD_RUN_DELETION_PROTECTION="${CLOUD_RUN_DELETION_PROTECTION:-true}"
INSTALL_PKGS="${INSTALL_PKGS:-false}"      # true to auto-install Termux pkg dependencies
GRADLE_HEAP_MB="${GRADLE_HEAP_MB:-2048}"   # Safer heap size for mobile RAM in Termux

# Parse CLI flags
while [[ $# -gt 0 ]]; do
  case "$1" in
    --install-pkgs) INSTALL_PKGS="true"; shift ;;
    --debug)        BUILD_TYPE="debug"; shift ;;
    --release)      BUILD_TYPE="release"; shift ;;
    --all-builds)   BUILD_TYPE="all"; shift ;;
    --skip-tests)   RUN_TESTS="false"; shift ;;
    --skip-cf)      RUN_CF="false"; shift ;;
    --skip-tf)      RUN_GCP_TF="false"; shift ;;
    --apply)        TF_APPLY="true"; shift ;;
    -h|--help)
      cat <<'USAGE'
Usage: bash scripts/termux_ops_and_build.sh [options]
  --install-pkgs   Install required Termux packages (nodejs-lts, python, openjdk-17, aapt2, gradle, terraform, wrangler)
  --debug          Build Debug APK & run unit tests (default)
  --release        Build Release APK & AAB (requires KEYSTORE_PATH/STORE_PASSWORD/KEY_PASSWORD or my-upload-key.jks)
  --all-builds     Build both Debug and Release
  --skip-tests     Skip :app:testDebugUnitTest
  --skip-cf        Skip Cloudflare Wrangler secrets & worker deploy
  --skip-tf        Skip GCP KMS/CMEK, Secret Manager & Terraform steps
  --apply          Execute `terraform apply tfplan` (default is plan-only for safety)
USAGE
      exit 0
      ;;
    *)
      echo "[ERROR] Unknown option: $1" >&2
      exit 1
      ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "$REPO_ROOT"

log()  { printf '\n\033[1;36m[%s]\033[0m %s\n' "$(date +%H:%M:%S)" "$*"; }
warn() { printf '\033[1;33m[WARN]\033[0m %s\n' "$*"; }
err()  { printf '\033[1;31m[ERROR]\033[0m %s\n' "$*" >&2; }

# ==============================================================================
# 1. Termux Package Setup (Optional via --install-pkgs)
# ==============================================================================
if [[ "$INSTALL_PKGS" == "true" ]]; then
  if command -v pkg >/dev/null 2>&1; then
    log "Installing Termux packages..."
    pkg update -y
    pkg install -y nodejs-lts python curl jq git openssl-tool openjdk-17 aapt2 gradle tur-repo
    pkg install -y terraform
    if ! command -v wrangler >/dev/null 2>&1; then
      npm install -g wrangler
    fi
    if ! command -v gcloud >/dev/null 2>&1; then
      warn "gcloud not found. Installing Google Cloud SDK (non-interactive)..."
      curl -sSL https://sdk.cloud.google.com | bash -s -- --disable-prompts
      # shellcheck disable=SC1091
      [[ -f "$HOME/google-cloud-sdk/path.bash.inc" ]] && source "$HOME/google-cloud-sdk/path.bash.inc"
    fi
  else
    warn "'pkg' command not found (not running in Termux?); skipping Termux package install."
  fi
fi

# Ensure gcloud is on PATH if installed in $HOME/google-cloud-sdk
if ! command -v gcloud >/dev/null 2>&1 && [[ -f "$HOME/google-cloud-sdk/path.bash.inc" ]]; then
  # shellcheck disable=SC1091
  source "$HOME/google-cloud-sdk/path.bash.inc"
fi

# ==============================================================================
# 2. Keystore Restoration & Release SHA-256 Inspection (App Check / Play Integrity)
# ==============================================================================
log "Checking Android keystores..."
if [[ ! -f "${REPO_ROOT}/debug.keystore" && -f "${REPO_ROOT}/debug.keystore.base64" ]]; then
  base64 -d "${REPO_ROOT}/debug.keystore.base64" > "${REPO_ROOT}/debug.keystore"
  log "Restored debug.keystore from debug.keystore.base64."
fi

RELEASE_KEYSTORE="${KEYSTORE_PATH:-${REPO_ROOT}/my-upload-key.jks}"
if command -v keytool >/dev/null 2>&1; then
  if [[ -f "$RELEASE_KEYSTORE" && -n "${STORE_PASSWORD:-}" ]]; then
    log "Release Keystore Fingerprints (register SHA-256 in Firebase App Check / Play Console):"
    keytool -list -v -keystore "$RELEASE_KEYSTORE" -alias "${KEY_ALIAS:-upload}" -storepass "$STORE_PASSWORD" \
      | grep -E "SHA1:|SHA256:" || true
  elif [[ -f "${REPO_ROOT}/debug.keystore" ]]; then
    log "Debug Keystore Fingerprints:"
    keytool -list -v -keystore "${REPO_ROOT}/debug.keystore" -alias androiddebugkey -storepass android \
      | grep -E "SHA1:|SHA256:" || true
  fi
fi

# Shared Bearer Token (used for both Cloudflare Worker and GCP Secret Manager out-of-band sync)
NEW_AGENT_TOKEN="${AGENT_BEARER_TOKEN:-$(openssl rand -hex 32)}"

# ==============================================================================
# 3. Cloudflare Worker Secrets & Deploy
# ==============================================================================
if [[ "$RUN_CF" == "true" ]]; then
  if [[ -d "${REPO_ROOT}/worker" && -f "${REPO_ROOT}/worker/wrangler.toml" ]]; then
    if command -v npx >/dev/null 2>&1 || command -v wrangler >/dev/null 2>&1; then
      log "Configuring Cloudflare Worker secrets & deploying..."
      pushd "${REPO_ROOT}/worker" >/dev/null

      WRANGLER_CMD=(npx --yes wrangler)
      command -v wrangler >/dev/null 2>&1 && WRANGLER_CMD=(wrangler)

      if [[ -z "${CLOUDFLARE_API_TOKEN:-}" ]]; then
        "${WRANGLER_CMD[@]}" whoami >/dev/null 2>&1 || "${WRANGLER_CMD[@]}" login
      fi

      printf '%s' "$NEW_AGENT_TOKEN" | "${WRANGLER_CMD[@]}" secret put AGENT_BEARER_TOKEN
      printf '%s' "$GCP_PROJECT_ID"  | "${WRANGLER_CMD[@]}" secret put FIREBASE_PROJECT_ID
      printf '%s' "$ALLOWED_EMAIL"   | "${WRANGLER_CMD[@]}" secret put ALLOWED_FIREBASE_EMAIL

      if [[ -n "${GITHUB_PAT:-}" ]]; then
        printf '%s' "$GITHUB_PAT" | "${WRANGLER_CMD[@]}" secret put GITHUB_PAT
      else
        warn "GITHUB_PAT env var not set; skipping automatic GITHUB_PAT secret update."
      fi
      if [[ -n "${RESEND_API_KEY:-}" ]]; then
        printf '%s' "$RESEND_API_KEY" | "${WRANGLER_CMD[@]}" secret put RESEND_API_KEY
      else
        warn "RESEND_API_KEY env var not set; skipping automatic RESEND_API_KEY secret update."
      fi

      "${WRANGLER_CMD[@]}" secret list
      "${WRANGLER_CMD[@]}" deploy
      popd >/dev/null
    else
      warn "wrangler/npx not found; skipping Cloudflare Worker step."
    fi
  else
    warn "worker/wrangler.toml not found in repo; skipping Cloudflare Worker step (generate/export templates from DeployKit if needed)."
  fi
fi

# ==============================================================================
# 4. GCP CMEK State Bucket + Secret Manager Out-of-Band + Terraform
# ==============================================================================
if [[ "$RUN_GCP_TF" == "true" ]]; then
  if command -v gcloud >/dev/null 2>&1; then
    log "Configuring GCP APIs, CMEK tfstate bucket, and Secret Manager..."
    if ! gcloud auth list --filter=status:ACTIVE --format='value(account)' | grep -q .; then
      gcloud auth login --no-launch-browser
    fi
    gcloud config set project "$GCP_PROJECT_ID"

    gcloud services enable \
      cloudkms.googleapis.com \
      storage.googleapis.com \
      secretmanager.googleapis.com \
      firebaseappcheck.googleapis.com \
      playintegrity.googleapis.com

    gcloud kms keyrings create antigravity-tfstate-ring --location="$GCP_REGION" 2>/dev/null || true
    gcloud kms keys create tfstate-cmek-key \
      --keyring=antigravity-tfstate-ring \
      --location="$GCP_REGION" \
      --purpose=encryption 2>/dev/null || true

    PROJECT_NUMBER="$(gcloud projects describe "$GCP_PROJECT_ID" --format='value(projectNumber)')"
    KMS_KEY_ID="projects/${GCP_PROJECT_ID}/locations/${GCP_REGION}/keyRings/antigravity-tfstate-ring/cryptoKeys/tfstate-cmek-key"

    gcloud kms keys add-iam-policy-binding tfstate-cmek-key \
      --keyring=antigravity-tfstate-ring \
      --location="$GCP_REGION" \
      --member="serviceAccount:service-${PROJECT_NUMBER}@gs-project-accounts.iam.gserviceaccount.com" \
      --role="roles/cloudkms.cryptoKeyEncrypterDecrypter" >/dev/null

    if ! gcloud storage buckets describe "gs://${TFSTATE_BUCKET}" >/dev/null 2>&1; then
      gcloud storage buckets create "gs://${TFSTATE_BUCKET}" \
        --location="$GCP_REGION" \
        --uniform-bucket-level-access \
        --default-encryption-key="$KMS_KEY_ID"
      gcloud storage buckets update "gs://${TFSTATE_BUCKET}" --versioning
    else
      log "GCS tfstate bucket gs://${TFSTATE_BUCKET} already exists."
    fi

    # Push bearer secret directly out-of-band (never stored in tfstate)
    gcloud secrets create antigravity-agent-bearer-master --replication-policy="automatic" 2>/dev/null || true
    printf '%s' "$NEW_AGENT_TOKEN" | gcloud secrets versions add antigravity-agent-bearer-master --data-file=- >/dev/null
    log "Added new secret version to antigravity-agent-bearer-master out-of-band."
  else
    warn "gcloud not found; skipping GCP CMEK & Secret Manager steps."
  fi

  if [[ -d "${REPO_ROOT}/infra" && -f "${REPO_ROOT}/infra/main.tf" ]]; then
    if command -v terraform >/dev/null 2>&1; then
      log "Running Terraform validate / plan in infra/..."
      pushd "${REPO_ROOT}/infra" >/dev/null

      if grep -qE '^[[:space:]]*backend "gcs"' main.tf; then
        terraform init -reconfigure \
          -backend-config="bucket=${TFSTATE_BUCKET}" \
          -backend-config="prefix=terraform/state"
      else
        terraform init -input=false
      fi

      terraform validate
      terraform plan -input=false -var="cloud_run_deletion_protection=${CLOUD_RUN_DELETION_PROTECTION}" -out=tfplan

      if [[ "$TF_APPLY" == "true" ]]; then
        log "Applying Terraform plan..."
        terraform apply -input=false tfplan
      else
        warn "Plan-only mode (pass --apply to execute terraform apply)."
      fi

      log "Checking Cloudflare provider v5 migration targets (informational):"
      grep -rnE 'cloudflare_email_routing_rule|cloudflare_workers_kv_namespace|cloudflare_r2_bucket' . || true
      popd >/dev/null
    else
      warn "terraform not found; skipping Terraform execution."
    fi
  else
    warn "infra/main.tf not found in repo; skipping Terraform execution (generate/export templates from DeployKit if needed)."
  fi
fi

# ==============================================================================
# 5. Android Build & Unit Tests (Termux ARM64-safe Gradle invocation)
# ==============================================================================
log "Preparing Android Gradle build (${BUILD_TYPE})..."

# Detect Android SDK root if unset
if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  for candidate in \
    "$HOME/android-sdk" \
    "$HOME/Android/Sdk" \
    "${PREFIX:-/data/data/com.termux/files/usr}/share/android-sdk" \
    "/opt/android-sdk"; do
    if [[ -d "$candidate/platforms" ]]; then
      export ANDROID_HOME="$candidate"
      export ANDROID_SDK_ROOT="$candidate"
      break
    fi
  done
fi

if [[ -z "${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}" ]]; then
  err "ANDROID_HOME / ANDROID_SDK_ROOT is not set and no SDK directory was auto-detected."
  err "Install Android SDK (platforms;android-36 & build-tools;36.0.0) in Termux and export ANDROID_HOME=\$HOME/android-sdk."
  exit 1
fi

GRADLE_CMD=""
if command -v gradle >/dev/null 2>&1; then
  GRADLE_CMD="gradle"
elif [[ -x "${REPO_ROOT}/gradlew" ]]; then
  GRADLE_CMD="${REPO_ROOT}/gradlew"
else
  err "Neither system 'gradle' nor './gradlew' was found."
  exit 1
fi

GRADLE_ARGS=(
  "--no-daemon"
  "-Dorg.gradle.jvmargs=-Xmx${GRADLE_HEAP_MB}m -Dfile.encoding=UTF-8"
)

# On Termux ARM64, override Google's x86_64 Maven aapt2 binary with Termux's native ARM64 aapt2
ARCH="$(uname -m)"
if [[ "$ARCH" == "aarch64" || "$ARCH" == "arm64" ]]; then
  if command -v aapt2 >/dev/null 2>&1; then
    AAPT2_BIN="$(command -v aapt2)"
    log "ARM64 detected: using native aapt2 at ${AAPT2_BIN}"
    GRADLE_ARGS+=("-Pandroid.aapt2FromMavenOverride=${AAPT2_BIN}")
  else
    err "ARM64 device detected, but 'aapt2' binary is missing. Run: pkg install -y aapt2"
    exit 1
  fi
fi

GRADLE_TASKS=()
if [[ "$RUN_TESTS" == "true" ]]; then
  GRADLE_TASKS+=(":app:testDebugUnitTest")
fi

case "$BUILD_TYPE" in
  debug)
    GRADLE_TASKS+=(":app:assembleDebug")
    ;;
  release)
    if [[ ! -f "$RELEASE_KEYSTORE" || -z "${STORE_PASSWORD:-}" || -z "${KEY_PASSWORD:-}" ]]; then
      err "Release build requires keystore at '$RELEASE_KEYSTORE' plus STORE_PASSWORD and KEY_PASSWORD env vars."
      exit 1
    fi
    GRADLE_TASKS+=(":app:assembleRelease" ":app:bundleRelease")
    ;;
  all)
    GRADLE_TASKS+=(":app:assembleDebug")
    if [[ -f "$RELEASE_KEYSTORE" && -n "${STORE_PASSWORD:-}" && -n "${KEY_PASSWORD:-}" ]]; then
      GRADLE_TASKS+=(":app:assembleRelease" ":app:bundleRelease")
    else
      warn "Skipping release build in --all-builds because release keystore or STORE_PASSWORD/KEY_PASSWORD is missing."
    fi
    ;;
  *)
    err "Invalid BUILD_TYPE='$BUILD_TYPE'. Expected debug | release | all."
    exit 1
    ;;
esac

log "Executing: ${GRADLE_CMD} ${GRADLE_TASKS[*]} ${GRADLE_ARGS[*]}"
"$GRADLE_CMD" "${GRADLE_TASKS[@]}" "${GRADLE_ARGS[@]}"

log "Build artifacts:"
find "${REPO_ROOT}/app/build/outputs" -type f \( -name "*.apk" -o -name "*.aab" \) 2>/dev/null | while read -r artifact; do
  if command -v sha256sum >/dev/null 2>&1; then
    printf '  %s\n    sha256: %s\n' "$artifact" "$(sha256sum "$artifact" | awk '{print $1}')"
  else
    printf '  %s\n' "$artifact"
  fi
done

# ==============================================================================
# 6. Post-Deployment Endpoint Health Verification
# ==============================================================================
if command -v curl >/dev/null 2>&1 && [[ -n "$GATEWAY_HEALTH_URL" ]]; then
  log "Probing gateway health: ${GATEWAY_HEALTH_URL}"
  if HEALTH_RESP="$(curl -fsS --max-time 10 "$GATEWAY_HEALTH_URL" 2>/dev/null)"; then
    if command -v jq >/dev/null 2>&1; then
      printf '%s\n' "$HEALTH_RESP" | jq . || printf '%s\n' "$HEALTH_RESP"
    else
      printf '%s\n' "$HEALTH_RESP"
    fi
  else
    warn "Health probe to ${GATEWAY_HEALTH_URL} did not succeed (may be offline or pre-deploy)."
  fi
fi

log "All requested operations & build steps completed."
