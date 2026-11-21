# AntiGravity Hub (`com.gc2223.antigravity`)

Google アカウント SSO を起点に、**Firebase**（Auth / Firestore / AI Logic / App Check）、**Cloudflare Workers**（`gc2223.com`）、**GitHub**（Repository Activity / Actions）、**GCP**（Always Free & `$0.10/日` 予算ガード）、および Android 端末上の **Termux `AntiGravityAgent` CLI** を統合管理する **Jetpack Compose 製 Android オーケストレーションアプリ**です。

---

## 主な機能

1. **Google SSO & Firebase セキュリティ連携 (`auth/`, `firebase/`)**
   - Android Jetpack `CredentialManager`（Google Sign-In）と Firebase Auth を連携したシングルサインオン。
   - 起動時に Firebase App Check（デバッグビルドは Debug プロバイダ、リリースは Play/reCAPTCHA プロバイダ）を初期化。
   - Firestore Security Rules（`firestore.rules`）により、認証済みユーザー本人のドキュメント（`/users/{userId}/...`）のみ読み書き可能。

2. **マルチターゲット・マルチモーダル LLM エージェント (`AgentChatScreen`, `WebContextCrawler`, `MultimodalSourceLoader`, `HubViewModel`)**
   - **URL・ドメイン指定クロール & 最新デバイス画面取得**: `google.com` 等のドメインまたは URL を入力し、単一 URL／ドメイン巡回（`sitemap.xml` ＆同一オリジンリンク探索）を実行して Markdown ソース化。さらに Pixel 9 Pro / Galaxy S25 Ultra / iPhone 16 Pro / Foldable 展開等の最新デバイスプロファイル（User-Agent・解像度比率）で Web 画面を描画・PNG キャプチャし、そのまま Gemini 解析へ添付可能（`gc2223.com/v1/screenshot` Browser Rendering にも対応）。
   - **Firebase AI Logic (`gemini-3.8-flash`)**: Markdown / テキスト、PDF、画像、動画、およびクロール・キャプチャした画面を統合解析し、ストリーミング相談または JSON Schema 指定の構造化ソリューション・手順・CLI スニペットを生成。
   - **Cloudflare Workers Gateway (`gc2223.com/v1/chat`)**: Firebase ID Token（`X-Firebase-Token`）および Bearer Token によるシームレス認証付きゲートウェイ推論（添付 Markdown / テキストのコンテキスト注入対応）。
   - 生成された設計や CLI スクリプトはそのまま Firestore のブループリント保管庫へ保存可能。

3. **日次コスト監視 & `$0.10/日` 自動メール通知 (`CostMonitorScreen`)**
   - GCP / Cloudflare / Firebase / GitHub の利用記録を日別に集計・可視化（デフォルトしきい値 `$0.10/日`）。
   - 日次合計がしきい値を超過すると、設定済みエンドポイント（デフォルト `/v1/cost-alert` Webhook）経由でメールアラートを送信し、履歴を Firestore `cost_alerts` に記録。

4. **Android Keystore 暗号化ボルト (`SettingsScreen`, `LocalSecretsStore`)**
   - Firebase Auth の UID ごとに生成した Android Keystore 鍵（**AES-256-GCM**）を用いて、以下の認証情報を端末内のみに暗号化保存：
     - GitHub PAT / 対象リポジトリ（`owner/repo`）/ Actions ワークフローファイル
     - Cloudflare API Token / Account ID / `gc2223.com` Gateway URL & Bearer Token
     - GCP Project ID / API Key・Token / Billing Account ID
     - Termux ローカル Daemon URL（デフォルト `http://127.0.0.1:8787`）/ ローカル Bearer Token
   - 設定画面から GitHub PAT・Cloudflare Token・Gateway `/health`・Termux `/status` の疎通確認を実行可能。

5. **GitHub アクティビティ・WorkManager 同期 & Email Worker 自己修復 (`sync/`, `SelfHealDialog`)**
   - **`GitHubActivityService`**: GitHub REST API から認証ユーザー、最近更新されたリポジトリ、対象リポジトリのメタデータ、直近コミット一覧、最新 Actions 実行結果（Run ID 含む）、アクティビティイベントを取得。
   - **Email Worker 宛先別ルーティング & 自律ハードニング (`worker/src/worker.js`)**: `ci-heal@`（エラー診断＆再実行）、`tf@`（Terraform `plan`/`apply` 起動）、`crawl@`（URL 調査＆返信）、`task@`（Workers AI によるタスク分類・実行）を統合処理。30分 TTL ワンタイム承認（`APPROVE APPLY: <id>` / `APPROVE RERUN: <id>`）、メールスレッド文脈記憶（`In-Reply-To` / `References`）、CI 修復ナレッジベース、時間別レート制限＆`SAFE_MODE` サーキットブレーカー、6時間毎 Cron CI 監視＋`GITHUB_PAT` 期限アラートを搭載。
   - **`CloudStatusSyncWorker` / `TermuxAgentSyncWorker` / `POST /run` & Pull キュー耐障害機構**: Termux `antigravity pull-task` はバッテリー残量・充電・ネットワーク状態（`X-Device-Battery` / `X-Device-Charging` / `X-Device-Network`）を送信し、10分リース・最大3回リトライ後の DLQ 退避（`dlq:`）・`/v1/termux/ack` 時の R2 ログ退避に対応。

6. **Terraform & サステナブル全体オーケストレーション (`DeployKitScreen`, `OrchestrationHardeningPanel`, `SustainabilityTelemetryCard`, `GovernanceControlPlaneCard`, `ResilienceOperationsCard`)**
   - **Terraform IaC (`infra/main.tf`)**: GCP Cloud Run LLM 実行基盤（`REGION_CFE_SCORE` メタデータ付与）・`$3/月` 予算アラート＋**Pub/Sub 実遮断フック**・**Workload Identity Federation (OIDC キーレス認証)**・**GCP Secret Manager ＋ Pub/Sub ローテーションフック (`/v1/secret-rotation-hook`)**・Cloudflare R2（30日/90日DR/180日ナレッジポリシー出力）と KV / Email Routing ルールを一括管理。
   - **GreenOps & Frugal AI (`worker/src/worker.js`)**: 炭素強度が高い時間帯の低緊急タスク遅延（`GREEN_DEFER`）、1B 軽量モデル＋SHA-256 セマンティックキャッシュによる推論スキップ、R2 月次ログ圧縮（`audit/monthly/`）、週次依存鮮度チェック、14日間無操作時の自動 `ECO_SLEEP` 移行を搭載。
   - **Google SRE / SAIF / Policy-as-Code 一元ガバナンス**: 単一マニフェスト `governance-policy.json` を `ORCH_KV` (`/v1/policy/sync`) へ同期しレート制限・Termux 許可コマンド・SAIF 信頼度閾値（`0.85` 未満は承認キュー `saif_low_conf_exec` へ自動保留）を統一制御。W3C OpenTelemetry `traceparent` 横断追跡・SLI/SLO バーンレート監視・SHA-256 監査ハッシュ連鎖・Wrangler Canary 10%→100% 段階的ロールアウト（異常時自動ロールバック）・**System Integrity Matrix** を統合。
   - **監査鎖検証・カオス自己診断・LLM 境界防御・マルチノード・自己進化 PR・暗号化 DR**:
     - **能動検証 (`GET /v1/audit/verify`) & カオス GameDay (`POST /v1/chaos/drill`)**: R2 監査ログの SHA-256 連続性検証（`Chain: VERIFIED`）と、月次/手動の無害 Dry-Run 自己診断スコア化。
     - **LLM セキュリティ境界 & DLP**: `<untrusted_data>` 入力サンドボックス、Taint Tracking（外部データ由来の高権限実行を `tainted_external_exec` 承認キューへ強制隔離）、SSRF URL 検証、R2/KV/メール出力前の機密トークン自動マスキング。
     - **マルチノード・フェデレーション (`POST /v1/nodes/register`, `antigravity register-node`)**: Termux 端末や Linux/Docker ノードの capability に応じたタスク動的ルーティング。
     - **自己進化ポリシー PR (`POST /v1/policy/auto-tune`) & Golden Test ゲート**: 週次で承認履歴を解析し、12ケースの回帰テスト（`GOLDEN_EVAL_CASES` 100% 合格＋SAIF 下限 0.80 検証）を通過した場合のみ `governance-policy.json` 更新 Pull Request を自動作成。
     - **暗号化 DR バックアップ＆ローカル自己完結復元 (`POST /v1/dr/snapshot`, `POST /v1/dr/restore`, `scripts/bootstrap-dr.sh --local`)**: R2 へ **PBKDF2-SHA256**（100,000 回反復＋ランダムソルト）＋ **AES-256-GCM** 暗号化スナップショットを退避し、`DR_PASSPHRASE` 単体でのオフライン復号復旧にも対応。
     - **無料枠天井ガード (`Free-Tier Guard`)**: `ORCH_KV` の高頻度書き込み（レート制限・Heartbeat・推論キャッシュ等）を **Cloudflare Workers Cache API (`caches.default`)** へ逃がして 1,000 writes/日上限を回避し、Workers AI の 10,000 Neurons/日消費を計測して 80% で軽量モデル（1B）へ自動縮退（倹約AIモード）、95% でルールベースへフェイルオーバー。
     - **多重外部監視 (`Dead Man's Switch` & FCM / ローカル通知)**: 6時間 Cron / `POST /v1/health/deadman-ping` で外部監視 URL（`DEADMAN_PING_URL`）へ Ping し、緊急アラート・メール送信障害時は FCM フォールバック（`sendFcmFallback`）および Android 側の `UrgentAlertNotifier`（`POST_NOTIFICATIONS`）で端末へ通知。
   - **アプリ側制御・レジリエンス UI (`SustainabilityTelemetryCard`, `GovernanceControlPlaneCard`, `ResilienceOperationsCard`, `AuditRagQueryCard`)**: 無料枠消費ゲージ、監査チェーン検証、カオス自己診断、端末ノード登録、自己進化 PR 生成、暗号化 DR 退避／復元、Dead Man Ping、生体署名鍵登録、R2 監査 RAG 検索をワンタップで操作可能。
   - **生体認証＋ハードウェア鍵ゼロトラスト承認 (`BiometricHardwareSigner`, `/v1/approvals/register-key`, `/v1/approvals/challenge`)**: Android Keystore (StrongBox / TEE) 上に ECDSA P-256 鍵を生成し、承認時に `BiometricPrompt` でワンタイムチャレンジへ電子署名（DER→IEEE P1363変換）して Worker の Web Crypto `crypto.subtle.verify` で検証。
   - **DAG 並列オーケストレーション & GitHub Actions 月間枠防衛 (`.github/workflows/antigravity-orchestrator.yml`, `/v1/dag/*`)**: `dag-init` → `deploy-gateway-worker` ∥ `terraform-verify` → `health-audit` の並列 DAG 実行とチェックポイント再試行スキップ、さらに月間 2,000 分の 75%（1,500分）超過時に Federation ノードへ自動オフロード。
   - **Maker-Checker 敵対的Critic & JSON Schema 強制 (`runAdversarialCritic`)**: Workers AI 呼び出しで `json_schema` 構造化出力を強制し、`ci-heal@` / `tf@` / `tf_apply` 提案を別視点 AI が危険検証して承認カードへ警告表示（`criticRiskLevel` / `criticWarnings`）。
   - **Cloudflare GraphQL 実測テレメトリ & FinOps タグ集計**: 6時間毎 Cron で Cloudflare GraphQL Analytics API から KV/R2/CPU 実測値を同期して乖離補正し、ワークフローカテゴリ別（`ci-heal` / `tf` / `crawl` / `dr` / `control`）の内訳を可視化。
   - **Jetpack Glance ホームウィジェット・Quick Settings Kill-Switch・R2 監査 RAG (`StatusGlanceWidget`, `KillSwitchTileService`, `POST /v1/ask`)**: ホーム画面ウィジェットで稼働モード・承認件数・無料枠残量を常時表示し、クイック設定タイルからワンタップで `SAFE_MODE` を緊急遮断可能。また、`@cf/baai/bge-small-en-v1.5` Embeddings による監査ログ RAG 検索を搭載。

---

## パッケージ・ディレクトリ構成

```text
.
├── app/src/main/
│   ├── AndroidManifest.xml                  # INTERNET / ACCESS_NETWORK_STATE, networkSecurityConfig 設定
│   ├── res/xml/network_security_config.xml  # 全通信 HTTPS 必須（127.0.0.1 / localhost のみ HTTP 許可）
│   └── java/com/example/
│       ├── MainActivity.kt                  # App Check 初期化, Edge-to-Edge, Firebase Auth 状態ルーティング
│       ├── auth/
│       │   └── GoogleAuthHelper.kt          # CredentialManager Google Sign-In & Firebase Auth 連携
│       ├── data/
│       │   ├── CloudBridgeClient.kt         # gc2223.com / Cloudflare / Termux ローカルデーモン通信クライアント
│       │   ├── FirestoreRepository.kt       # ユーザー単位の Firestore CRUD & SnapshotListener
│       │   ├── GitHubActivityService.kt     # GitHub リポジトリアクティビティサマリー取得サービス
│       │   ├── LocalSecretsStore.kt         # Firebase UID 紐付け AES-256-GCM 暗号化ストレージ
│       │   ├── MultimodalSourceLoader.kt    # Markdown / PDF / 画像 / 動画読み込み & Gemini Content 生成
│       │   ├── OrchestrationExtendedClient.kt # ハードウェア公開鍵登録・チャレンジ取得・R2 RAG /v1/ask 通信
│       │   ├── OrchestrationHardeningClient.kt # 制御プレーン状態取得・承認・監査検証・Chaos/DR API 通信
│       │   ├── StackTemplates.kt            # Worker / Termux / GitHub Actions / GCP テンプレート集
│       │   └── SyncSnapshotStore.kt         # WorkManager 同期結果（Cloud / Termux 分離）・間隔設定保存
│       ├── firebase/
│       │   ├── AppCheckInit.kt              # Firebase App Check 初期化
│       │   └── FirestoreErrorHandler.kt     # Firestore 例外ハンドリング・リトライ補助
│       ├── model/
│       │   └── Models.kt                    # UI 状態・Firestore エンティティ・同期スナップショットモデル
│       ├── security/
│       │   └── BiometricHardwareSigner.kt   # StrongBox/TEE ECDSA P-256 鍵管理・BiometricPrompt 署名・DER->P1363 変換
│       ├── sync/
│       │   ├── BackgroundSyncScheduler.kt   # 定期 / 即時 WorkManager スケジューリング・状態 Flow
│       │   ├── CloudStatusSyncWorker.kt     # GitHub + Cloudflare ステータス取得 & 緊急ローカル通知・ウィジェット同期
│       │   ├── KillSwitchTileService.kt     # Quick Settings タイルからのワンタップ SAFE_MODE 緊急遮断トグル
│       │   ├── TermuxAgentSyncWorker.kt     # Termux AntiGravityAgent ローカルステータス取得 CoroutineWorker
│       │   └── UrgentAlertNotifier.kt       # SAFE_MODE / 障害時のローカル高優先度通知チャンネル制御
│       ├── widget/
│       │   ├── StatusGlanceWidget.kt        # Jetpack Glance ホームウィジェット（残枠・承認待ち・即時更新）
│       │   ├── StatusGlanceWidgetReceiver.kt # Glance AppWidgetReceiver
│       │   └── WidgetStateStore.kt          # SharedPreferences ベース軽量ウィジェット状態ストア
│       └── ui/
│           ├── components/                  # SustainabilityTelemetryCard / GovernanceControlPlaneCard / ResilienceOperationsCard / AuditRagQueryCard 等
│           ├── screens/                     # Auth / MainHub / AgentChat / StatusDashboard / Nodes / CostMonitor / DeployKit / Blueprints / Settings
│           ├── theme/                       # Material 3 カラー・タイポグラフィ・テーマ定義
│           └── viewmodel/
│               └── HubViewModel.kt          # UI StateFlow 管理・AI 生成・ゼロトラスト署名・コスト判定・同期制御
├── app/src/test/java/com/example/
│   ├── ExampleUnitTest.kt                   # 基本ユニットテスト
│   ├── ExampleRobolectricTest.kt            # Keystore/Secrets, SyncSnapshotStore, Workers, GitHubActivityService の検証
│   └── ExtendedOrchestrationTest.kt         # DER->P1363署名, WidgetStateStore, Hardware Auth/RAG API, KillSwitchTileService 検証
├── firebase-blueprint.json                  # Firestore スキーマ定義
├── firestore.rules                          # Firestore ゼロトラストセキュリティルール
├── firestore.test.js                        # Firestore ルール検証テスト
└── metadata.json                            # AI Studio プラットフォーム用メタデータ
```

---

## 技術スタック・主なライブラリ

| 分類 | 採用技術 / バージョン (`gradle/libs.versions.toml`) |
| --- | --- |
| 言語 / SDK | Kotlin `2.4.21` / AGP `9.1.1` / `minSdk = 24` / `targetSdk = 36` / `compileSdk = 36` |
| UI | Jetpack Compose BOM `2024.09.00` / Material 3 / Navigation Compose `2.8.9` / Jetpack Glance `1.1.1` |
| Firebase | Firebase BOM `35.1.0` (`firebase-ai`, `firebase-firestore`, `firebase-auth`, `firebase-appcheck-*`) |
| 認証・生体署名 | Jetpack Credentials (`1.5.0`) + Google Identity (`googleid:1.1.1`) / AndroidX Biometric (`1.1.0`) + StrongBox/TEE ECDSA |
| 非同期・定期実行 | Kotlin Coroutines `1.10.2` / WorkManager (`work-runtime-ktx:2.12.0`) |
| ネットワーク | OkHttp `4.10.0` / Logging Interceptor `4.10.0` |
| テスト | JUnit `4.13.2` / Robolectric `4.16.1` / `work-testing:2.12.0` / `kotlinx-coroutines-test` |

---

## セットアップとビルド・テスト方法

### 1. Firebase・シークレット設定
- `app/google-services.json` に Firebase プロジェクト構成を配置してください（未ログイン状態・オフライン機能テスト以外の Firebase 機能利用時に必要）。
- 任意の環境変数が必要な場合は `.env.example` を参照して設定してください（API キー類をソースコードへハードコードしないでください）。

### 2. ローカル単体テスト・Robolectric テストの実行

```bash
gradle :app:testDebugUnitTest
```

### 3. デバッグ APK のビルド

```bash
gradle :app:assembleDebug
```

### 4. Termux 側のローカルエージェントデーモン起動（任意）
アプリ内の **キット (`DeployKitScreen`)** から「Termux AntiGravityAgent CLI + ローカル `/status` デーモン」スクリプトを取得し、Android 端末の Termux 上で実行したあと以下を起動すると、ステータスダッシュボードにリアルタイム反映されます。

```bash
antigravity serve 8787
```

※ セキュア設定で「ローカル Bearer Token」を設定した場合は、Termux 側の環境変数 `ANTIGRAVITY_LOCAL_TOKEN` に同じトークンを設定してください。
