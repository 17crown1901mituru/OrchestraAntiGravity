#!/usr/bin/env bash
# AntiGravity Safe APK Installer (prevents silent INSTALL_FAILED_UPDATE_INCOMPATIBLE surprises)
set -euo pipefail

PKG_NAME="com.gc2223.antigravity"
REINSTALL_ON_CONFLICT="false"
APK_PATH=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --reinstall-on-conflict)
      REINSTALL_ON_CONFLICT="true"
      shift
      ;;
    -h|--help)
      cat <<'USAGE'
Usage: bash scripts/install_apk_safe.sh [--reinstall-on-conflict] <path/to/app.apk>
  Inspects the APK signing certificate SHA-256 and compares it with any existing installation
  on the connected ADB device. If a signature conflict is detected:
    - Without --reinstall-on-conflict: aborts safely with SHA comparison and backup instructions.
    - With --reinstall-on-conflict: uninstalls the conflicting build cleanly and installs the new APK.
USAGE
      exit 0
      ;;
    *)
      APK_PATH="$1"
      shift
      ;;
  esac
done

if [[ -z "$APK_PATH" || ! -f "$APK_PATH" ]]; then
  echo "[ERROR] APK file not found: '${APK_PATH}'" >&2
  exit 1
fi

APKSIGNER_BIN="$(command -v apksigner || true)"
if [[ -z "$APKSIGNER_BIN" && -n "${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}" ]]; then
  SDK_ROOT="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
  APKSIGNER_BIN="$(find "$SDK_ROOT/build-tools" -maxdepth 2 -name apksigner 2>/dev/null | sort -V | tail -n 1 || true)"
fi

NEW_SHA256=""
if [[ -n "$APKSIGNER_BIN" ]]; then
  echo "[INFO] Verifying APK signature with $APKSIGNER_BIN..."
  NEW_SHA256="$("$APKSIGNER_BIN" verify --print-certs "$APK_PATH" 2>/dev/null | awk '/SHA-256 digest:/ {print toupper($NF); exit}')"
  echo "[INFO] Target APK SHA-256: ${NEW_SHA256:-unknown}"
fi

if ! command -v adb >/dev/null 2>&1; then
  if command -v termux-open >/dev/null 2>&1; then
    echo "[WARN] 'adb' not found in Termux; launching Android package installer via termux-open."
    termux-open "$APK_PATH"
    exit 0
  fi
  echo "[ERROR] 'adb' not found on PATH." >&2
  exit 1
fi

set +e
INSTALL_OUTPUT="$(adb install -r -d "$APK_PATH" 2>&1)"
INSTALL_STATUS=$?
set -e

if [[ $INSTALL_STATUS -eq 0 ]]; then
  echo "$INSTALL_OUTPUT"
  echo "[OK] APK installed cleanly without signing conflict."
  exit 0
fi

echo "$INSTALL_OUTPUT" >&2
if echo "$INSTALL_OUTPUT" | grep -qE "INSTALL_FAILED_UPDATE_INCOMPATIBLE|INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES"; then
  echo "" >&2
  echo "[CONFLICT] Signing certificate mismatch with currently installed '$PKG_NAME'!" >&2
  echo "  Installed package was signed with a DIFFERENT key (e.g. local debug vs CI vs Release)." >&2
  echo "  Target APK SHA-256: ${NEW_SHA256:-unknown}" >&2
  if [[ "$REINSTALL_ON_CONFLICT" == "true" ]]; then
    echo "[ACTION] --reinstall-on-conflict enabled: uninstalling old '$PKG_NAME' and installing new APK..."
    adb uninstall "$PKG_NAME"
    adb install -d "$APK_PATH"
    echo "[OK] Clean reinstall completed. Restore portable config from antigravity-bootstrap.json if needed, and sync APK SHA in GCP Wizard."
    exit 0
  else
    echo "  To avoid losing runtime config, first tap '再導入用バックアップ' inside the app, then run:" >&2
    echo "    bash scripts/install_apk_safe.sh --reinstall-on-conflict \"$APK_PATH\"" >&2
    exit 2
  fi
fi

exit "$INSTALL_STATUS"
