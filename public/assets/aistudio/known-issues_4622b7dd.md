# 既知の問題 &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/known-issues

Cloud Quota の既知の問題は次のとおりです。

ロールアウト中の割り当て値
-------------

Google Cloud は、リソースと API のデフォルトの割り当て値を増やすことがあります。これらの変更は段階的に行われるため、ロールアウト中は、 Google Cloud コンソールまたは Cloud Quotas API に表示される割り当て値に、新しい、増加した割り当て値は反映されません。ロールアウトが完了すると、反映されます。

割り当てのロールアウトが進行中の場合は、\[Cloud Quotas\] ページの上部に情報メッセージが表示され、進行中のロールアウトの影響を受ける割り当て値の横に、ローリング アップデート インジケーターが表示されます。詳しくは、[進行中のロールアウトを表示する](https://docs.cloud.google.com/docs/quotas/view-ongoing-rollouts?hl=ja)をご覧ください。

トラブルシューティングの手順については、[サービス ロールアウト中の割り当て値の超過](https://docs.cloud.google.com/docs/quotas/troubleshoot?hl=ja#exceeding_quota_values_during_a_service_rollout)をご覧ください。

Cloud Quotas API で `QuotaPreference` 値を更新するには、`contactEmail` フィールドが必要です。このメールアドレスはグループメールにすることはできません。

API で `QuotaPreference` を使用する例については、[一般的なユースケースの実装](https://docs.cloud.google.com/docs/quotas/implement-common-use-cases?hl=ja)をご覧ください。

Google Cloud コンソールで Cloud Quotas を使用する場合は、次の制限が適用されます。

### 使用量がない割り当ての調整のリクエスト

Google Cloud コンソールでは、使用実績のない割り当てに関する割り当て調整リクエストはサポートされていません。ただし、REST API または Google Cloud CLI を使用して割り当ての調整をリクエストすることは可能です。

たとえば、プロジェクトのクローンを作成し、`compute.googleapis.com/local_ssd_total_storage_per_vm_family` の値を増やす必要があることを事前に把握している場合などです。 Google Cloud コンソールにその割り当ては表示されませんが、API または gcloud CLI を使用して割り当ての調整をリクエストすることは可能です。詳細については、[進行中のロールアウトを表示する](https://docs.cloud.google.com/docs/quotas/view-ongoing-rollouts?hl=ja)をご覧ください。

### ユーザーあたりの割り当て使用量が表示されない

Google Cloud コンソールにユーザーあたりの割り当て使用量は表示されません。
