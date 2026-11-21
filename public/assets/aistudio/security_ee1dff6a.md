# 確保安全性的最佳做法 &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/best-practices/security

本文件提供有關如何安全使用 Terraform for Google Cloud的指南和建議。Terraform 需要敏感的雲端基礎架構存取權才能運作。遵循安全性最佳做法，有助於降低相關風險並改善整體雲端安全性。

本指南並非 Terraform 的介紹。如要瞭解如何搭配使用 Terraform 和 Google Cloud，請參閱「[開始使用 Terraform](https://docs.cloud.google.com/docs/terraform/get-started-with-terraform?hl=zh-tw)」。

使用遠端狀態
------

Google Cloud 客戶建議使用 [Cloud Storage 狀態後端](https://www.terraform.io/docs/backends/types/gcs.html)。這種做法會鎖定狀態，讓團隊成員能進行協作。它也會將狀態和所有可能的機密資訊與版本控制分開。

請確認只有建構系統和擁有高度權限的管理員可以存取用於遠端狀態的資料夾。

為避免不小心將開發狀態提交至原始碼控制，請為 Terraform 狀態檔案使用 [gitignore](https://github.com/github/gitignore/blob/master/Terraform.gitignore)。

加密狀態
----

雖然 Google Cloud 儲存格會在靜止時加密，但您可以使用[客戶提供的加密金鑰](https://docs.cloud.google.com/storage/docs/encryption?hl=zh-tw#customer-supplied)，為資料多添一層保護。方法是使用 `GOOGLE_ENCRYPTION_KEY` 環境變數。雖然狀態檔案不應包含任何機密資料，但請一律加密狀態，做為額外的防護措施。

請勿在狀態中儲存密鑰
----------

Terraform 中有許多資源和資料供應器，會在狀態檔案中以純文字形式儲存機密值。盡可能避免在狀態中儲存機密。以下是一些以明文儲存機密資料的供應商範例：

*   [`vault_generic_secret`](https://registry.terraform.io/providers/hashicorp/vault/latest/docs/resources/generic_secret)
*   [`tls_private_key`](https://registry.terraform.io/providers/hashicorp/tls/latest/docs/resources/private_key)
*   [`google_service_account_key`](https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_service_account_key)
*   [`google_client_config`](https://registry.terraform.io/providers/hashicorp/google/latest/docs/data-sources/client_config)

標示敏感輸出內容
--------

請不要嘗試手動[加密機密值](https://www.terraform.io/docs/extend/best-practices/sensitive-state.html)，而是依賴 Terraform 內建的機密狀態管理功能。將機密值匯出至輸出內容時，請務必將值標示為「機密」[](https://www.terraform.io/docs/configuration/outputs.html#sensitive-suppressing-values-in-cli-output)。

確保權責劃分
------

如果您無法從沒有使用者存取權的自動化系統執行 Terraform，請透過分隔權限和目錄來實施職責分離。舉例來說，網路專案會對應至網路 Terraform 服務帳戶或使用者，而這類使用者的存取權僅限於這個專案。

執行申請前檢查
-------

在自動化管道中執行 Terraform 時，請使用 `gcloud terraform vet` 等工具，在套用前[檢查計畫輸出內容是否符合政策](https://docs.cloud.google.com/docs/terraform/policy-validation?hl=zh-tw)。這麼做可在安全性回歸發生前先行偵測。

執行持續稽核
------

執行 `terraform apply` 指令後，請執行自動安全性檢查。這些檢查可確保基礎架構不會進入不安全的狀態。以下工具適用於這類檢查：

*   [安全狀態分析](https://docs.cloud.google.com/security-command-center/docs/how-to-use-security-health-analytics?hl=zh-tw)
*   [InSpec](https://inspec.io/)
*   [Serverspec](https://serverspec.org/)

後續步驟
----

*   瞭解 [Terraform 在 Google Cloud](https://docs.cloud.google.com/docs/terraform/best-practices/general-style-structure?hl=zh-tw)上的一般樣式和結構最佳做法。
*   瞭解[使用 Terraform 根目錄模組時的最佳做法](https://docs.cloud.google.com/docs/terraform/best-practices/root-modules?hl=zh-tw)。
