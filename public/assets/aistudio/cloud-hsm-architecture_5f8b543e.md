# Cloud HSM 架構 &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/cloud-hsm-architecture

_本文上次更新於 2025 年 10 月，內容反映截至撰文時的情況。_我們會持續改善保護客戶的方式，因此 Google 的安全性政策和系統未來可能會改變。

Cloud HSM 是 Cloud Key Management Service (Cloud KMS) 架構的一部分，可提供後端服務，用於佈建及管理受硬體保護的金鑰。為協助您遵守企業和法規規範，Cloud HSM 可讓您在 FIPS 140-2 第 3 級認證的硬體安全性模組 (HSM) 中產生加密金鑰，並執行密碼編譯作業。

Cloud HSM 提供兩種硬體支援金鑰的保護等級：

*   **多租戶 Cloud HSM**：多租戶金鑰存放在 HSM 叢集上，可為多個 Google Cloud 客戶提供服務。
*   **單一租戶 Cloud HSM**：單一租戶金鑰會存放在 HSM 叢集的專屬分區中，每個分區僅供一位客戶使用。您可以建立及管理自己的單一租戶 Cloud HSM 執行個體，這些執行個體會以加密編譯方式與其他客戶隔離。Google Cloud 與多租戶 Cloud HSM 金鑰相比，單一租戶 Cloud HSM 執行個體會產生額外費用。詳情請參閱「[單一租戶 Cloud HSM](https://docs.cloud.google.com/kms/docs/single-tenant-hsm?hl=zh-tw)」。

本指南中提及的 Cloud HSM 同時適用於多租戶 Cloud HSM 和單一租戶 Cloud HSM。

本文說明 Cloud HSM 架構，包括硬體管理方式，以及金鑰認證和建立方式。如要進一步瞭解 Cloud KMS，請參閱「[Cloud Key Management Service 加密](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=zh-tw)」。

總覽
--

加密編譯作業包括：

*   靜態資料加密
*   保護[憑證授權單位服務](https://docs.cloud.google.com/certificate-authority-service?hl=zh-tw)的私密金鑰
*   保護資料加密金鑰，以便與加密資料一起儲存
*   產生及使用非對稱金鑰，用於數位簽章和驗證等密碼編譯作業

Cloud HSM 使用 Marvell LiquidSecurity HSM (型號 CNL3560-NFBE-2.0-G 和 CNL3560-NFBE-3.0-G)，韌體版本為 3.4 build 09 和 10。如要進一步瞭解我們的認證，請參閱[認證 #4399](https://csrc.nist.gov/projects/cryptographic-module-validation-program/Certificate/4399)。如要瞭解 HSM 裝置和硬體保護金鑰，請參閱「[金鑰認證](https://docs.cloud.google.com/kms/docs/attest-key?hl=zh-tw)」。

多租戶 Cloud HSM 為全代管服務，可保護工作負載，不必擔心管理 HSM 叢集會產生額外的費用。單一租戶 Cloud HSM 由執行個體管理員和 Google 共同管理。Cloud HSM 服務提供下列優勢：

*   全域可用性
*   一致且統合的 API
*   根據用量自動調度資源
*   集中管理與法規遵循

多租戶 Cloud HSM 適用於全球許多 Google Cloud 區域，包括涵蓋較大地理區域的多區域。單一租戶 Cloud HSM 適用於部分區域。您必須使用 Cloud KMS 服務端點，在 Cloud HSM 中建立及使用硬體保護的金鑰，保護您的資料，包括儲存在其他 Google Cloud 服務 (例如 BigQuery、Cloud Storage 和 Persistent Disk) 中的資料。

有了 Cloud HSM，您就能使用硬體保護的金鑰，不必自行管理 HSM 硬體。Google 擁有及管理 HSM 硬體，包括部署、設定、監控、修補和維護。使用 Cloud HSM 時，您的資料會與 Google Cloud中的其他租戶和服務嚴格隔離。Cloud HSM 資料層 API 是 Cloud Key Management Service API 的一部分，可讓您透過程式輔助方式管理硬體保護的金鑰。

只要 Google Cloud 服務支援[客戶自行管理的加密金鑰 (CMEK)](https://docs.cloud.google.com/kms/docs/cmek?hl=zh-tw)，Cloud HSM 就能支援硬體保護的 CMEK。例如，您可以使用自行管理的 Cloud HSM 金鑰，加密 Cloud Storage bucket 或 Cloud SQL 資料表中的資料。多租戶 Cloud HSM 也支援 Cloud KMS Autokey，可簡化 CMEK 的建立和管理作業。

Cloud HSM 管理
------------

在 Cloud HSM 中，HSM 叢集是由 Google 網站可靠性工程師 (SRE) 和技術人員維護，他們在每個資料中心位置工作。 Google CloudGoogle 會處理實體安全、邏輯安全、基礎架構、容量規劃、地理擴充和資料中心災難復原計畫。

### HSM 硬體抽象化

一般來說，應用程式會同時使用 PKCS#11 和叢集管理 API，直接與 HSM 通訊。如要進行這類通訊，您必須維護專用程式碼，以供使用或管理硬體保護金鑰的工作負載使用。

Cloud HSM 會透過 Cloud Key Management Service API 代理硬體保護金鑰的要求，將通訊從 HSM 抽象化。抽象化可減少 HSM 專屬程式碼的需求。Cloud HSM 繼承了與 Cloud KMS 的緊密整合。

與 Cloud KMS 緊密整合可大幅提升安全性。Cloud Key Management Service API 大幅縮減了可用的 HSM 介面範圍，降低客戶安全性漏洞的風險。舉例來說，攻擊者無法抹除整個 HSM。根據預設，系統會透過預設的 [30 天安全期](https://docs.cloud.google.com/kms/docs/destroy-restore?hl=zh-tw)，降低刪除個別金鑰的風險。您可以設定 `constraints/cloudkms.minimumDestroyScheduledDuration` 組織政策，針對新金鑰強制執行_排定刪除_的最短時間，並設定 `constraints/cloudkms.disableBeforeDestroy` 組織政策，只在金鑰版本停用時刪除。詳情請參閱「[控管金鑰版本刪除作業](https://docs.cloud.google.com/kms/docs/control-key-destruction?hl=zh-tw)」。

您可以使用 [Identity and Access Management (IAM)](https://docs.cloud.google.com/iam/docs?hl=zh-tw) 控管 HSM 資源的存取權。與自訂 HSM 解決方案相比，IAM 設定較不容易發生設定錯誤和錯誤。除了 IAM 控制項，管理單一租戶 Cloud HSM 執行個體也需要[至少兩位指定核准者](https://docs.cloud.google.com/kms/docs/single-tenant-hsm?hl=zh-tw#quorum)的雙重驗證 (2FA) 才能進行。建立執行個體時，您可以決定每次變更單一租戶 Cloud HSM 執行個體時，需要多少核准。核准程序需要使用您在 Google Cloud外部建立的私密金鑰素材簽署驗證。

下圖顯示 Cloud HSM 架構。

### 嚴格的地理區隔 (設計上)

在多租戶 Cloud HSM 中，您可以選擇讓金鑰在全球可用，或對需要限制的金鑰強制執行嚴格的地理位置限制。單一租戶 Cloud HSM 僅適用於個別區域。

HSM 通常會分成多個分區，因此單一實體裝置可以做為多個邏輯裝置運作。如果需要區分 HSM 管理和金鑰，可以使用分區來降低部署成本。下圖顯示三個區域中的多租戶 Cloud HSM 分區。

為了隔離每個區域和多區域的金鑰，每個 Cloud HSM 區域都會與個別的 HSM 區域包裝金鑰建立關聯 (請參閱「[建立金鑰](#creating-keys)」中的圖表)。為支援高可用性，包裝金鑰會複製到實際位於該區域的各個 HSM 分區。HSM 區域金鑰不會離開該位置的 HSM。複製功能可讓同一區域的 HSM 服務同一組客戶金鑰，並確保區域外的 HSM 無法服務這些金鑰。

Cloud HSM 也會使用包裝金鑰建立多區域。多區域的所有客戶金鑰，都會使用包裝金鑰進行包裝，該金鑰位於構成多區域的所有位置的分區中。這項服務在多區域使用相同的硬體，但在區域和多區域之間，提供與不同區域之間相同的強大隔離機制。

區域化機制規定，包裝金鑰只能複製到適當的分區。每項設定變更都必須經過 Cloud HSM 團隊多位成員核准，才會生效。資料中心技術人員無法存取 HSM 設定、執行階段或儲存空間。

在單一租戶 Cloud HSM 執行個體中建立金鑰時，專用分區上的包裝金鑰可確保金鑰不會在分區或區域外提供。

### 集中管理

在託管 HSM 的傳統資料中心，HSM 及其資源的管理與其他硬體保護金鑰的管理完全分開。Cloud HSM 與 Google Cloud緊密整合，方便您順暢管理 Cloud HSM 資源。例如，您可以管理以下項目：

*   您可以在 Cloud KMS 中管理硬體保護金鑰和其他金鑰，並在 [Cloud External Key Manager (Cloud EKM)](https://docs.cloud.google.com/kms/docs/ekm?hl=zh-tw) 中管理外部金鑰。
*   您可以在 IAM 中管理硬體保護金鑰的存取權。
*   使用硬體保護金鑰執行加密編譯作業的費用，會列在 Cloud Billing 報表中。
*   您可以在所有支援使用 CMEK 加密資源的服務中，以透明方式使用硬體保護金鑰。 Google Cloud CMEK 整合功能要求 CMEK 金鑰和加密資料必須位於相容的地理位置。由於 Cloud HSM 金鑰有嚴格的地理位置限制，因此 CMEK 資料的所有加密和解密作業也受到地理位置限制。
*   Cloud 稽核記錄一律會在 API 層記錄硬體保護金鑰的管理作業。您也可以選擇啟用資料存取記錄。詳情請參閱「[Cloud KMS 稽核記錄資訊](https://docs.cloud.google.com/kms/docs/audit-logging?hl=zh-tw)」。
*   Google 會直接與 HSM 製造商合作，確保每個 HSM 的硬體和軟體都維持在最新狀態，並即時找出及修正問題。如果 HSM 發生零時差攻擊，Google 可以選擇性停用受影響 HSM 叢集中的受影響程式碼路徑，直到攻擊問題修正為止。
*   您可以使用[金鑰使用情形追蹤資訊主頁](https://docs.cloud.google.com/kms/docs/view-key-usage?hl=zh-tw)，追蹤金鑰 (包括受硬體保護的金鑰) 和金鑰加密的資源。

開發人員和使用者體驗
----------

由於 Google 負責管理 HSM，Cloud HSM 為開發人員和使用者帶來顯著優勢。使用多租戶 Cloud HSM 時，您不必採取任何行動來維護 HSM 叢集。使用單一租戶 Cloud HSM 時，您必須負責使用 gcloud CLI 執行持續的叢集維護作業。

Cloud HSM 的使用者主要分為四種：

*   **使用者**：Cloud HSM 通常對產品和資源的使用者公開透明。舉例來說，如果您的 Google Cloud 資源受到 CMEK 保護，服務代理會視使用者需求，自動加密及解密資料。
*   **開發人員**：開發人員會使用 Cloud HSM 金鑰。如果您使用 Autokey，開發人員可以視需要要求新金鑰。如果您不使用 Autokey，Cloud KMS 管理員會建立及管理金鑰，供開發人員使用。
*   **Cloud KMS 管理員**：Cloud KMS 管理員負責建立及輪替 Cloud HSM 金鑰。如果專屬的組織管理員未處理這些責任，他們也可以管理 Autokey 設定或組織政策。
*   **單一租戶 Cloud HSM 管理員**：如果您使用單一租戶 Cloud HSM，則必須有值得信任的管理員團隊，負責核准單一租戶 Cloud HSM 執行個體的建立和維護作業。不同的 IAM 角色可控管哪些使用者有權提議、核准及執行執行個體變更。未經事先核准就無法套用變更，且必須使用 2FA 金鑰進行法定人數驗證。

### Google 規模的 HSM

如果依賴地端部署或資料中心內的硬體，硬體可能會造成效能瓶頸，或成為單點故障。Cloud HSM 具備極高的彈性，可因應無法預測的工作負載和硬體故障。Cloud HSM 後端在[每個區域](https://docs.cloud.google.com/kms/docs/locations?hl=zh-tw#hsm-regions)使用 HSM 集區，確保高可用性和擴充性。這個 HSM 集區也讓 Cloud HSM 具備高處理量。詳情請參閱「[監控及調整 Cloud KMS 配額](https://docs.cloud.google.com/kms/docs/monitor-adjust-quotas?hl=zh-tw)」。

所有客戶金鑰都會在 Cloud KMS 資料庫中，以區域包裝金鑰包裝的形式儲存，且只能由該區域的 HSM 解包，做為加密編譯作業的一部分。這種包裝方式有以下優點：

*   金鑰的耐用性與特定 HSM 或區域中的 HSM 子集無關。
*   每位 Cloud HSM 客戶都能體驗到，為金鑰提供服務的 Cloud HSM 叢集所具備的完整規模和可用性。
*   Cloud HSM 可處理的 HSM 金鑰數量遠遠超過 HSM 可儲存的數量。
*   新增或更換 HSM 的程序快速又安全。

### 統合式 API 設計

Cloud HSM 和 Cloud KMS 共用管理和資料層 API。與 HSM 通訊的內部詳細資料會從呼叫端中抽象化。

因此，您不需要變更程式碼，就能更新在 Cloud KMS 中使用軟體金鑰的現有應用程式，以支援受硬體保護的金鑰。而是更新要使用的金鑰資源名稱。

### PKCS#11 支援

您可以使用 Cloud Key Management Service API，將現有應用程式連結至 Cloud HSM，以便管理加密編譯金鑰。[PKCS#11 程式庫](https://docs.cloud.google.com/kms/docs/reference/pkcs11-library?hl=zh-tw)可讓您使用硬體保護的金鑰簽署二進位檔，並提供 TLS 網頁工作階段。

安全性與法規遵循
--------

Cloud HSM 已通過多項法規的認證，包括 [FedRAMP High](https://cloud.google.com/security/compliance/fedramp?hl=zh-tw)、[C5:2020](https://cloud.google.com/security/compliance/bsi-c5?hl=zh-tw) 和 [OSPAR](https://cloud.google.com/security/compliance/ospar?hl=zh-tw)。此外，Cloud HSM 可協助您對雲端工作負載強制執行法規遵循。

### 加密編譯金鑰認證

每次產生或匯入 Cloud HSM 金鑰時，HSM 都會產生[認證陳述式](https://docs.cloud.google.com/kms/docs/attest-key?hl=zh-tw)，並使用與分割區相關聯的簽署金鑰簽署。陳述式包含金鑰屬性的相關資訊。簽署金鑰由憑證鏈結支援，這些憑證鏈結的根憑證來自 Google 和 HSM 製造商。您可以下載認證聲明和憑證，驗證聲明的真實性，並驗證金鑰和產生/匯入金鑰的 HSM 屬性。

憑證鏈結可讓你檢查下列項目：

*   HSM 硬體和韌體是正版。
*   HSM 分區和 HSM 由 Google 管理。
*   HSM 處於 FIPS 作業模式。

您可以透過認證聲明的內容檢查下列事項：

*   金鑰無法擷取。
*   系統已為 CryptoKeyVersion 產生金鑰。
*   非對稱金鑰組中的公開金鑰，對應於受硬體保護的私密金鑰。
*   匯入的對稱金鑰內容與您包裝的值相符。

### 直接將金鑰安全匯入 HSM

您可以將現有金鑰安全匯入 Cloud HSM，在 Google Cloud外部備份金鑰內容，或簡化將特定工作負載遷移至 Google Cloud的程序。在金鑰匯入程序中，Google 無法直接存取解除包裝的金鑰內容。Cloud HSM 會提供 HSM 產生的包裝金鑰認證聲明，驗證沒有任何存取行為。

由於金鑰匯入功能允許使用者從不明來源匯入金鑰，可能會產生安全性和法規遵循風險，因此不同的 IAM 角色可精細控管哪些使用者能將金鑰匯入專案。匯入的金鑰可透過 HSM 在匯入時產生的認證陳述式來區分。

詳情請參閱「[將金鑰匯入 Cloud Key Management Service](https://docs.cloud.google.com/kms/docs/importing-a-key?hl=zh-tw)」。

### 嚴格的安全程序可保護 HSM 硬體

根據 FIPS 140-2 第 3 級規定，HSM 裝置內建機制，可協助防範實體竄改，並提供相關證據。

除了 HSM 硬體本身提供的保證外，Cloud HSM 的基礎架構也依據 [Google 基礎架構安全性設計總覽](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=zh-tw)進行管理。

下列記錄在案且可稽核的程序，可在佈建、部署及生產期間，保護每個 HSM 的完整性：

*   所有 HSM 設定都必須經過多位 Cloud HSM SRE 驗證，才能將 HSM 部署至資料中心。
*   HSM 投入服務後，只有多位 Cloud HSM SRE 才能發起及驗證設定變更。
*   HSM 只能接收由 HSM 製造商簽署的韌體。
*   HSM 硬體不會直接暴露在任何網路上。
*   伺服器會禁止執行未經授權的程序，以代管 HSM 硬體。

系統操作員的職責定義在標準作業程序中。系統操作員在履行職責時，無法存取、使用或擷取客戶金鑰內容。

### 服務和租戶隔離

Cloud HSM 架構可確保 HSM 不會受到其他服務或租戶的惡意或不慎干擾。

這個架構中的 HSM 只會接受來自 Cloud HSM 的要求，而 Cloud HSM 服務只會接受來自 Cloud KMS 服務的要求。Cloud KMS 服務會強制要求呼叫端具備適當的 IAM 權限，才能使用金鑰。未經授權的要求不會傳送至 HSM。

多租戶 Cloud HSM 金鑰也受限於密碼編譯作業的[配額](https://docs.cloud.google.com/kms/quotas?hl=zh-tw#cloud_hsm_quotas)。這些配額可防止不慎或惡意嘗試超載服務，保護您執行工作負載的能力。預設配額是根據觀察到的用量模式而定。配額遠低於服務容量，可視需求增加。

要求流程
----

本節說明 Cloud HSM 架構在實際應用中的運作方式，並列出不同類型要求的步驟。這些流程著重於 Cloud HSM 部分。如要進一步瞭解所有金鑰通用的步驟，請參閱「[Cloud Key Management Service 深入介紹](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=zh-tw)」。

### 建立金鑰

建立硬體保護金鑰時，Cloud Key Management Service API 不會建立金鑰內容，而是要求 HSM 建立。

HSM 只能在支援的位置建立金鑰。如果是多租戶 Cloud HSM，HSM 上的每個分區都包含與 Cloud KMS 位置對應的包裝金鑰。如果是單一租戶 Cloud HSM 執行個體，專屬分割區會使用專屬的包裝金鑰。包裝金鑰會由支援 Cloud KMS 位置或單一租戶 Cloud HSM 執行個體的所有分區共用。

下圖顯示硬體保護金鑰在 Cloud KMS 中的包裝方式。

金鑰建立程序如下：

1.  [Google Front End Service (GFE)](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=zh-tw#google-frontend-service) 會將金鑰建立要求轉送至與要求對應位置的 Cloud KMS 伺服器。
2.  Cloud Key Management Service API 會驗證呼叫端的身分、呼叫端在專案中建立金鑰的權限，以及呼叫端是否有足夠的寫入要求配額。如果呼叫端要求單一租戶 Cloud HSM 金鑰，API 也會驗證所選單一租戶 Cloud HSM 執行個體是否可用。
3.  Cloud Key Management Service API 會將要求轉送至 Cloud HSM。
4.  Cloud HSM 會直接與 HSM 互動。HSM：
    1.  建立金鑰，並使用特定位置或執行個體的包裝金鑰包裝。
    2.  為金鑰建立認證陳述式，並使用分割區簽署金鑰簽署。
5.  Cloud HSM 將包裝金鑰和認證傳回 Cloud KMS 後，Cloud Key Management Service API 會根據 [Cloud KMS 金鑰階層](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=zh-tw#software_key_hierarchy)包裝 HSM 包裝金鑰，然後將其寫入專案。

這項設計可確保金鑰無法在 HSM 外部解包或使用、無法從 HSM 擷取，且僅在指定位置以解包狀態存在。

### 密碼編譯作業

在 Cloud KMS 中執行加密編譯作業時，您不需要知道使用的是硬體保護或軟體保護金鑰。當 Cloud Key Management Service API 偵測到作業涉及受硬體保護的金鑰時，會將要求轉送至相同位置的 HSM。以下是密碼編譯作業的步驟：

1.  [GFE](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=zh-tw#google-frontend-service) 會將要求傳送至適當位置的 Cloud KMS 伺服器。Cloud Key Management Service API 會驗證呼叫端的身分、呼叫端存取金鑰和執行作業的權限，以及專案的加密編譯作業配額。
2.  Cloud Key Management Service API 會從資料存放區擷取包裝金鑰，並使用 Cloud KMS 主金鑰解密一個層級的加密。金鑰仍以 KMS 位置的 HSM 包裝金鑰包裝。
3.  Cloud Key Management Service API 會偵測[保護層級](https://docs.cloud.google.com/kms/docs/algorithms?hl=zh-tw#protection_levels)是否為 `HSM` 或 `HSM_SINGLE_TENANT`，並將部分解開的金鑰和加密編譯作業的輸入內容傳送至 Cloud HSM。
4.  Cloud HSM 會直接與 HSM 介接。HSM 會完成下列作業：
    1.  檢查包裝金鑰及其屬性是否未經修改。
    2.  解開金鑰並載入 HSM 儲存空間。
    3.  執行加密編譯作業並傳回結果。
5.  Cloud Key Management Service API 將結果傳回呼叫端。

使用硬體保護金鑰的加密編譯作業完全在設定位置的 HSM 內執行，且只有呼叫端能看到結果。

這張圖表顯示在 Cloud KMS 中建立硬體保護金鑰和軟體保護金鑰的差異。

### CMEK 整合

所有硬體保護金鑰都是 [CMEK](https://docs.cloud.google.com/kms/docs/cmek?hl=zh-tw)。如要將[啟用 CMEK 的服務](https://docs.cloud.google.com/kms/docs/using-other-products?hl=zh-tw#cmek_integrations)設為使用 Cloud HSM 金鑰，只要按照服務專屬操作說明，選擇保護層級為 `HSM` 或 `HSM_SINGLE_TENANT` 的金鑰即可。

呼叫端在已啟用 CMEK 的服務中讀取或寫入資料時，不需要直接取得使用金鑰的權限，也不必知道金鑰是否儲存在 HSM 中。

硬體保護金鑰和軟體保護金鑰使用相同的 CMEK 作業流程，但使用硬體保護金鑰時有以下例外狀況：

*   啟用 CMEK 的服務要求是在 Google 網路內啟動，不需要周遊 GFE。
*   Cloud Key Management Service API 會驗證啟用 CMEK 的服務帳戶是否具備使用金鑰的適當權限。Cloud Key Management Service API 不會驗證已啟用 CMEK 服務的使用者權限。

如要使用 Autokey 佈建 Cloud KMS 金鑰，必須使用 Cloud HSM。Autokey 可讓 Cloud KMS 服務代理依要求自動佈建硬體保護金鑰。詳情請參閱「[自動佈建 CMEK](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=zh-tw#autokey)」。

使用自己的 HSM
---------

Cloud HSM 使用的 HSM 由 Google 管理。不過，在某些情況下，貴組織可能會想使用自己的 HSM，儲存工作負載的硬體保護金鑰。舉例來說，使用自己的 HSM 有助於將工作負載遷移至 Google Cloud。

Google 僅在特定地區提供基礎架構 HSM 即服務，為Google Cloud中的安全加密編譯交易提供加密編譯金鑰作業。這些產品分別是 _Bare Metal Rack HSM_ 和 _Bare Metal HSM_。使用 Bare Metal Rack HSM 或 Bare Metal HSM 時，您需要自行購買及設定 HSM，然後將其運送至 Google 資料中心，以便由 Google 代管。您保有對 HSM 的完整邏輯存取權，且必須直接與 HSM 供應商合作，管理裝置及排解相關問題。Google 會提供實體和網路安全防護、機架空間、電力和網路整合，並收取費用。詳情請參閱「[Bare Metal Rack HSM](https://docs.cloud.google.com/kms/docs/bare-metal-rack-hsm?hl=zh-tw)」和「[Bare Metal HSM](https://docs.cloud.google.com/kms/docs/bare-metal-hsm?hl=zh-tw)」。

後續步驟
----

如要進一步瞭解，請參閱下列資源：

*   [Cloud HSM 說明文件](https://docs.cloud.google.com/kms/docs/hsm?hl=zh-tw)
*   [Cloud KMS 說明文件](https://docs.cloud.google.com/kms/docs?hl=zh-tw)
*   [Cloud Key Management Service 加密](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=zh-tw)
*   [Google 基礎架構安全性設計總覽](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=zh-tw)
*   [靜態資料加密](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=zh-tw)
