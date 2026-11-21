# 為美國公部門部署 Antigravity 應用程式和 Antigravity CLI &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/deploy-antigravity-us-public-sector

搭配 Gemini 平台使用時，您可以在 FedRAMP High、IL4、IL5、ITAR 和 CJIS 資料邊界環境中部署 Antigravity 應用程式和 Antigravity CLI。

請按照下列操作說明設定 [Antigravity 應用程式](https://antigravity.google/docs/overview?hl=zh-tw)和 [Antigravity CLI](https://antigravity.google/product/antigravity-cli/?hl=zh-tw)，以支援美國公部門組織和企業客戶所需的法規標準和類似指南。如果未按照本頁步驟操作，可能會導致設定不符合規範。

第三方評估機構 (3PAO) 已驗證這些部署操作說明，確認 Antigravity 應用程式和 Antigravity CLI 只能與一個美國境內的雲端服務 API 區域端點通訊。`aiplatform.us.rep.googleapis.com` API 端點[已支援](https://docs.cloud.google.com/assured-workloads/docs/supported-products?hl=zh-tw) Assured Workloads 資料邊界，適用於 FedRAMP 高影響等級、IL4、IL5、ITAR 和 CJIS。詳情請參閱「[Coalfire 對 Antigravity 應用程式和 CLI 的評估](https://services.google.com/fh/files/misc/antigravity_attestation_10-26-2026.pdf?hl=zh-tw)」。

必要條件
----

*   為法規遵循架構[建立 Assured Workloads 資料夾](https://docs.cloud.google.com/assured-workloads/docs/create-folder?hl=zh-tw)。

部署 Antigravity 應用程式和 Antigravity CLI
------------------------------------

請完成下列步驟，部署 Antigravity 應用程式和 Antigravity CLI，並設定本機環境和用戶端，以符合貴組織的安全期望：

1.  在本機[下載 Antigravity 應用程式和 Antigravity CLI](https://antigravity.google/download?hl=zh-tw)。
2.  在 Assured Workloads 資料夾中建立專案，並啟用 `aiplatform.googleapis.com` API。如需操作說明，請參閱「[Google Cloud 專案和 API 設定](https://antigravity.google/docs/enterprise?hl=zh-tw#google-cloud-project-and-api-setup)」一節。
3.  請根據使用者和用戶端的需求，設定 Antigravity 的驗證機制。您可以使用 Cloud Identity (與外部身分提供者或系統建立聯盟) 和 Google Cloud 服務帳戶，向 Antigravity 進行驗證。
4.  使用[應用程式預設憑證](https://antigravity.google/docs/enterprise?hl=zh-tw#application-default-credentials-adc-in-antigravity-cli)登入 Antigravity，如下所示：
    
    *   使用 Antigravity 桌面用戶端時：
        
        1.  選取 Assured Workloads 資料夾中的專案。
        2.  選擇美國境內的地點。
    *   使用 Antigravity CLI 時：
        
        1.  使用 Google Cloud CLI 設定配額專案：
            
            gcloud auth application-default set-quota-project QUOTA_PROJECT_ID
            
            將 `QUOTA_PROJECT_ID` 替換為 Assured Workloads 資料夾中的專案 ID。
            
        2.  請設定下列環境變數：
            
            export AGY_ADC_AUTH=true
                export GOOGLE_CLOUD_LOCATION=us
                export AGY_CLI_DISABLE_AUTO_UPDATE=true
            

連結至支援的模型
--------

FedRAMP High、IL4、IL5、ITAR 和 CJIS 支援下列模型：

*   Gemini 3.6 Flash
*   Gemini 3.7 Flash
*   Gemini 3.8 Flash
*   Gemini 4.0 Argon

如要連線至支援美國公部門法規遵循要求的模型版本，請選取「US」做為基礎端點 URI。您必須選取「US」，確保提示和回覆符合美國公部門客戶的特定要求。如需操作說明，請參閱「[區域端點和網路許可清單](https://antigravity.google/docs/enterprise?hl=zh-tw#regional-endpoints-and-network-allowlists)」。

停用選用 API
--------

Antigravity 應用程式和 Antigravity CLI 會呼叫其他後端 API，但並非所有 API 都符合美國公部門規定。這些 API 對產品的核心功能並非必要。為確保部署作業符合法規，請停用從本機環境叫用下列 API 的功能：

*   `aiplatform.*.rep.googleapis.com` 中所有非美國多區域端點 (`aiplatform.us.rep.googleapis.com` 除外)
*   `antigravity.google`
*   `*.antigravity.google`
*   `antigravity.google.com`
*   `*.antigravity.google.com`
*   `antigravity-cli-auto-updater-974169037036.us-central1.run.app`
*   `antigravity-hub-auto-updater-974169037036.us-central1.run.app`
*   `antigravity-unleash.goog`
*   `businessaicode.googleapis.com`
*   `chromewebstore.google.com`
*   `cloudcode-pa.googleapis.com`
*   `aicode.googleapis.com`
*   `daily-cloudcode-pa.googleapis.com`
*   `play.googleapis.com`

如要在本機環境中停用這些端點，請套用下列控制項：

*   **網路和 Proxy 規則**：在企業網路 Proxy、防火牆或 DNS 政策中，封鎖傳送至所列網域的傳出要求，但仍允許傳送至 `aiplatform.us.rep.googleapis.com` 端點的流量。
*   **端點使用組織政策**：在資料夾或專案層級設定 `constraints/gcp.restrictEndpointUsage` 組織政策限制，拒絕 `in:non-us-aiplatform-endpoints` 值組 (會封鎖全球和非美國 Gemini Enterprise Agent Platform 端點)，以及 `businessaicode.googleapis.com`、`businessaicode.us.rep.googleapis.com` 和 `businessaicode.eu.rep.googleapis.com` 端點。如要查看 Agent Platform 支援的多區域端點，請參閱「[多區域端點](https://docs.cloud.google.com/gemini-enterprise-agent-platform/resources/locations?hl=zh-tw#multi-region-endpoints)」。如需設定這項限制和精選值組的操作說明，請參閱「[限制端點用量](https://docs.cloud.google.com/docs/security/compliance/restrict-endpoint-usage?hl=zh-tw)」。
*   **專案 API 管理**：如果 Assured Workloads 專案已啟用 `businessaicode.googleapis.com` 和 `cloudaicompanion.googleapis.com` API，請停用這些 API。`cloudcode-pa.googleapis.com` 端點是 Google 內部用戶端端點，無法透過 Service Usage 啟用或停用，必須使用網路或 Proxy 規則封鎖。詳情請參閱「[停用服務](https://docs.cloud.google.com/service-usage/docs/enable-disable?hl=zh-tw#disabling)」。
*   **自動更新 CLI**：設定 `AGY_CLI_DISABLE_AUTO_UPDATE=true` 環境變數，防止 Antigravity CLI 在啟動時查詢 `antigravity-cli-auto-updater-974169037036.us-central1.run.app` 端點。

支援 SSL/TLS 檢查和憑證
----------------

Antigravity 應用程式和 CLI 會自動查詢及使用主機作業系統的信任根存放區，例如 Linux 上的 `/etc/ssl/certs`、Windows 上的 Windows 憑證存放區，以及 macOS 上的鑰匙圈。

如果您將企業的 CA 憑證套裝組合部署至用戶端 OS，Antigravity 會自動信任並在受檢查的 SSL/TLS 環境中運作。您不需要自訂 CA 覆寫。

如果您使用企業 Proxy，Go 語言的 HTTP 傳輸支援系統的標準 Proxy 環境變數。請在執行 Antigravity 的機器上設定下列變數：

*   `HTTP_PROXY`
*   `HTTPS_PROXY`
*   `NO_PROXY`

詳情請參閱「[設定](https://antigravity.google/docs/settings/?hl=zh-tw)」一節。
