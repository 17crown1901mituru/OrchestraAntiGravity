# AntiGravity Hub (`com.gc2223.antigravity`)

Google アカウント SSO を起点に、**Firebase**（Auth / Firestore / AI Logic / App Check）、**Cloudflare Workers**（`gc2223.com`）、**GitHub**（Repository Activity / Actions）、**GCP**（Always Free & `$0.10/日` 予算ガード）、および Android 端末上の **Termux `AntiGravityAgent` CLI** を統合管理する **Jetpack Compose 製 Android オーケストレーションアプリ**です。

---

## 主な機能

1. **Google SSO & Firebase セキュリティ連携 (`auth/`, `firebase/`)**
   - Android Jetpack `CredentialManager`（Google Sign-In）と Firebase Auth を連携したシングルサインオン。
   - 起動時に Firebase App Check（デバッグビルドは Debug プロバイダ、リリースは Play/reCAPTCHA プロバイダ）を初期化。
   - Firestore Security Rules（`firestore.rules`）により、認証済みユーザー本人のドキュメント（`/users/{userId}/...`）のみ読み書き可能。

2. **マルチターゲット・マルチモーダル LLM エージェント (`AgentChatScreen`, `MultimodalSourceLoader`, `HubViewModel`)**
   - **Firebase AI Logic (`gemini-3.8-flash`)**: Markdown / テキスト、PDF、画像、動画（合計 20MB 未満のインラインメディア、Android Photo Picker / SAF 経由のパーミッションレス選択）を統合解析し、ストリーミング相談または JSON Schema 指定の構造化ソリューション・手順・CLI スニペットを生成。
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

5. **GitHub アクティビティサマリー & WorkManager バックグラウンド同期 (`sync/`, `GitHubActivityService`, `StatusDashboardScreen`)**
   - **`GitHubActivityService`**: GitHub REST API から認証ユーザー、最近更新されたリポジトリ、対象リポジトリのメタデータ（ブランチ・Open PR 数・Open Issue 数・Star 数）、直近コミット一覧、最新 Actions 実行結果、アクティビティイベント（Push / PR / Issue / Release 等）を取得。
   - **`CloudStatusSyncWorker`**: 暗号化ボルトの認証情報を使い、GitHub と Cloudflare（API Token 状態・デプロイ済み Workers・Gateway `/health` レイテンシ）をバックグラウンド同期（`NetworkType.CONNECTED` 制約付き）。
   - **`TermuxAgentSyncWorker`**: 端末内の Termux `AntiGravityAgent` CLI ローカル HTTP デーモン（`http://127.0.0.1:8787/status`、404 時は `/health` にフォールバック）をポーリングし、デーモン状態・PID・稼働時間・実行中タスク・最新コマンド・終了コード・本日リクエスト数を取得（ローカルループバック通信のため外部ネット接続制約なしで動作）。
   - **`SyncSnapshotStore`**: クラウド同期結果（`cloud_snapshot_$uid`）と Termux 同期結果（`termux_snapshot_$uid`）を別キーで永続化し、並列実行時の上書き競合を防止。
   - **`BackgroundSyncScheduler`**: 15 分 / 30 分 / 1 時間 / 6 時間間隔での定期同期および即時手動同期を制御。

6. **ゼロコスト構築デプロイキット (`DeployKitScreen`, `StackTemplates`)**
   - コピー・保存可能な構築テンプレートを内蔵：
     - Cloudflare Worker ゲートウェイ（SSO 検証 + Workers AI + `/v1/cost-alert` + `/v1/dispatch-github` + `/install.sh`）
     - `wrangler.toml` ルーティング設定
     - Termux `AntiGravityAgent` CLI + `~/.config/antigravity/state.json` 状態記録 + `antigravity serve` ローカル `/status` HTTP デーモン
     - GitHub Actions 自動デプロイワークフロー
     - GCP Always Free & `$3/月（$0.10/日相当）` 予算アラート設定コマンド

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
│       │   ├── StackTemplates.kt            # Worker / Termux / GitHub Actions / GCP テンプレート集
│       │   └── SyncSnapshotStore.kt         # WorkManager 同期結果（Cloud / Termux 分離）・間隔設定保存
│       ├── firebase/
│       │   ├── AppCheckInit.kt              # Firebase App Check 初期化
│       │   └── FirestoreErrorHandler.kt     # Firestore 例外ハンドリング・リトライ補助
│       ├── model/
│       │   └── Models.kt                    # UI 状態・Firestore エンティティ・同期スナップショットモデル
│       ├── sync/
│       │   ├── BackgroundSyncScheduler.kt   # 定期 / 即時 WorkManager スケジューリング・状態 Flow
│       │   ├── CloudStatusSyncWorker.kt     # GitHub + Cloudflare ステータス取得 CoroutineWorker
│       │   └── TermuxAgentSyncWorker.kt     # Termux AntiGravityAgent ローカルステータス取得 CoroutineWorker
│       └── ui/
│           ├── screens/                     # Auth / MainHub / AgentChat / StatusDashboard / Nodes / CostMonitor / DeployKit / Blueprints / Settings
│           ├── theme/                       # Material 3 カラー・タイポグラフィ・テーマ定義
│           └── viewmodel/
│               └── HubViewModel.kt          # UI StateFlow 管理・AI 生成・コスト判定・同期制御
├── app/src/test/java/com/example/
│   ├── ExampleUnitTest.kt                   # 基本ユニットテスト
│   └── ExampleRobolectricTest.kt            # Keystore/Secrets, SyncSnapshotStore, Workers, GitHubActivityService の検証
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
| UI | Jetpack Compose BOM `2024.09.00` / Material 3 / Navigation Compose `2.8.9` |
| Firebase | Firebase BOM `35.1.0` (`firebase-ai`, `firebase-firestore`, `firebase-auth`, `firebase-appcheck-*`) |
| 認証 | Jetpack Credentials (`1.5.0`) + Google Identity (`googleid:1.1.1`) |
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
