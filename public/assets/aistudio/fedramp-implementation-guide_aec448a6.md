# Google Cloud での FedRAMP 実装ガイダンス &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/fedramp-implementation-guide

Last reviewed 2026-02-10 UTC

このドキュメントでは、 Google Cloud が FedRAMP コンプライアンスのニーズをどのようにサポートしているかについて説明し、FedRAMP 要件を満たすようにサービスを構成するためのリソースを紹介します。このドキュメントは、Google Cloudでの FedRAMP の実装とコンプライアンスを担当するセキュリティ、コンプライアンス、IT の担当者を対象としています。

[責任の共有モデル](https://docs.cloud.google.com/assured-workloads/docs/shared-responsibility?hl=ja)において、コンプライアンスとセキュリティの要件を理解し、Google Cloud 環境を適切に構成するのはお客様の責任です。FedRAMP サポートを実装する際は、FedRAMP に関する責任について独立した法的助言を求めることを強くおすすめします。

FedRAMP について
------------

[FedRAMP（Federal Risk and Authorization Management Program: 米国連邦政府によるリスクおよび認証管理プログラム）](https://www.fedramp.gov/)フレームワークは、米国連邦政府がすべての政府機関でクラウド製品とクラウド サービスのセキュリティ評価、認証、継続的モニタリングを標準化するために確立されました。2022 年、米国連邦議会は FedRAMP を「政府全体で利用可能なプログラムであり、各機関によって使用される非機密情報を処理するクラウド コンピューティング プロダクトおよびサービスに対する、標準化された再利用可能なセキュリティ評価と認証のアプローチを提供するもの」として明文化しました。

2025 年、FedRAMP は [FedRAMP 20x](https://www.fedramp.gov/20x/) イニシアチブの一環として、プログラムの大幅な改訂を開始しました。これらの変更は、連邦情報システムの最小限のセキュリティ要件を満たすために、商業的なセキュリティ ベスト プラクティスの自動モニタリングと適用を採用することを目的としています。FedRAMP は、費用がかかり非効率的な手作業でのドキュメント作成から、業界主導のデータドリブンなセキュリティ レポートに移行しています。Google が FedRAMP 20x イニシアチブをサポートする方法については、[FedRAMP を 20 倍に加速: Google Cloud がコンプライアンスを自動化する方法](https://cloud.google.com/blog/topics/public-sector/accelerating-fedramp-20x-how-google-cloud-is-automating-compliance?hl=ja)をご覧ください。

FedRAMP は、[米国国立標準技術研究所（NIST）SP 800-53](https://csrc.nist.gov/pubs/sp/800/53/r5/upd1/final) 標準に基づいており、FedRAMP のコントロールとコントロールの強化によって補完されています。特定のオンプレミス プライベート クラウド以外のすべての連邦政府機関のクラウドのデプロイとサービスモデルは、[NIST FIPS 199](https://csrc.nist.gov/pubs/fips/199/final) ガイドラインに基づいて、適切なリスク影響レベル（Low、Moderate、または High）で FedRAMP 要件を遵守している必要があります。対応するベースラインの [NIST SP 800-53 コントロール](https://csrc.nist.gov/projects/cprt/catalog#/cprt/framework/version/SP_800_53_5_2_0/home)の数は、影響レベルが上がるほど増加します。たとえば、FedRAMP Moderate ベースラインには 325 個のコントロールが、FedRAMP High ベースラインには 421 個のコントロールがあります。

FedRAMP は、1 回限りの資格や承認ではなく、評価と認証のプログラムです。クラウド サービス パッケージにおけるセキュリティ対策の有効性を確保し、進化する脅威の状況やシステム環境の変化に適応するための継続的なモニタリングが含まれます。

Google Cloud FedRAMP 認証
-----------------------

FedRAMP Board（旧称 Joint Authorization Board（JAB））は、FedRAMP の主要な管理機関です。FedRAMP Board は、米国国防総省（DoD）、米国国土安全保障省（DHS）、米国連邦政府一般調達局（GSA）の CIO で構成されています。

FedRAMP Board は、 Google Cloud および基盤となる Google Common Infrastructure（GCI）に対して FedRAMP High の暫定運用認証（P-ATO）を発行しています。FedRAMP High 認証は、FedRAMP コンプライアンスで最も高い基準です。

FedRAMP 20x の改善に伴い、GSA は Google Cloudの承認機関となりました。は、 FedRAMP High の承認を得るために追加のサービスを GSA に定期的に提出しています。FedRAMP Moderate のコントロール ベースラインは、FedRAMP High のコントロール ベースラインのサブセットです。したがって、FedRAMP High 認証を取得すれば、FedRAMP Moderate のすべてのコントロール要件を包括的にカバーできます。

Google Cloud FedRAMP コンプライアンスの詳細については、[FedRAMP コンプライアンス](https://cloud.google.com/security/compliance/fedramp?hl=ja)をご覧ください。

監査対象のサービス
---------

Google Cloud は、150 以上のクラウド サービスを対象とする包括的な FedRAMP High P-ATO を維持しています。この範囲により、 Google Cloud 上に幅広いアプリケーションを構築し、基盤となる Google Cloud プラットフォームからセキュリティ管理を継承して FedRAMP ATO を取得できます。たとえば、 Google Cloudのデプロイで、ML モデルや AI エージェント、生成 AI、マルチモーダル AI などの AI サービスを使用できます。

Google Cloud FedRAMP 監査の範囲の詳細については、[FedRAMP と DoD のコンプライアンス範囲](https://docs.cloud.google.com/docs/security/compliance/fedramp-dod-compliance-scope?hl=ja)と [Google Cloud FedRAMP Marketplace](https://marketplace.fedramp.gov/products/FR1805751477) をご覧ください。

### AI と LLM

Google Cloud は、ML モデルと AI アプリケーションを含むワークロードの FedRAMP コンプライアンス要件を満たすうえで役立ちます。Google Cloud FedRAMP 認証済みのサービス（[Gemini Enterprise Agent Platform の生成 AI](https://docs.cloud.google.com/vertex-ai/generative-ai/docs?hl=ja) や [Gemini Enterprise Agent Platform Inference: バッチとオンライン](https://docs.cloud.google.com/vertex-ai/docs/predictions/get-predictions?hl=ja)など）を使用すると、[Model Garden](https://cloud.google.com/model-garden?hl=ja) で利用可能な 200 以上のファーストパーティ、サードパーティ、オープンソースの大規模言語モデル（LLM）を操作できます。詳細については、[Model Garden でサポートされているモデル](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/model-garden/available-models?hl=ja)をご覧ください。

個々の LLM は FedRAMP の下で個別に認証されておらず、FedRAMP Marketplace にもそれらの認証の記録はありません。代わりに、Marketplace には、Agent Platform の生成 AI や Agent Platform Inference: Batch and Online といったクラウド サービスの認証が掲載されています。これらは、Google が承認のために提出するものです。ただし、LLM のデプロイに使用される基盤となるクラウド インフラストラクチャは、継続的モニタリングに使用されるインフラストラクチャを含め、FedRAMP の準拠要件に準拠する必要があります。この要件は、Google の自社 LLM（[Gemini モデル ファミリー](https://ai.google.dev/gemini-api/docs/models?hl=ja)など）や Anthropic のパートナー モデルなど、[プロビジョンド スループット](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/provisioned-throughput/supported-models?hl=ja)をサポートするすべての Google マネージド モデルで満たされています。したがって、FedRAMP High 認証を受けた Agent Platform サービスを使用することで、FedRAMP High 環境でこれらのサポート対象モデルを操作できます。

Google は、Google マネージド インフラストラクチャでホストされているより多くの LLM に対して、モニタリング プロビジョニングを継続的に実装しています。Google は、オープンソース モデルをデプロイするために提供されるインフラストラクチャとカスタムビルド コンテナの認証については責任を負いますが、オープンソース モデルのセキュリティはお客様の責任となります。

セルフデプロイされた LLM の詳細については、[セルフデプロイ モデルの概要](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/model-garden/self-deployed-models?hl=ja)をご覧ください。LLM を独自の Google Cloud テナント インフラストラクチャにデプロイする場合は、FedRAMP ATO 評価で個々の LLM ではなく、そのインフラストラクチャが対象となるようにします。たとえば、LLM のデプロイ用にプロビジョニングするGoogle Cloud インフラストラクチャについて、継続的モニタリング要件を満たす必要があります。ATO を取得するには、第三者評価機関（3PAO）と Google と連携します。

FedRAMP ATO の取得
---------------

進行中の [FedRAMP 20x の変更](https://www.fedramp.gov/20x/faqs/)に基づき、FedRAMP 認証を取得するための経路は [Rev. 5 Agency ATO](https://www.fedramp.gov/rev5/agency-authorization/) となります。ATO を取得するまでの手順を完了するには、Google と 3PAO と連携する必要があります。Agency ATO プロセスについては、Agency Authorization Playbook などの重要なリソースへのリンクを含め、[FedRAMP のウェブサイト](https://www.fedramp.gov/rev5/agency-authorization/)をご覧ください。

Google Cloud サービスを使用して FedRAMP High のコンプライアンス義務を満たす場合は、[FedRAMP High 用の Data Boundary](https://docs.cloud.google.com/assured-workloads/docs/supported-products?hl=ja)を使用する必要があります。FedRAMP Moderate のコントロール ベースラインは、FedRAMP High のコントロール ベースラインのサブセットです。したがって、Google Cloudにデプロイされたソリューションの FedRAMP Moderate ATO を取得する場合は、FedRAMP High 認証を受けた Google Cloud サービスはすべて FedRAMP Moderate の認証境界内で利用できます。FedRAMP Moderate ATO の場合、FedRAMP High ATO の場合に評価する必要があるコントロールよりも、評価するコントロールは少なくなります。

FedRAMP ATO を取得するために、Google は秘密保持契約（NDA）に基づいて以下のGoogle Cloud FedRAMP High コンプライアンス ドキュメントを提供できます。

*   **Customer Responsibility Matrix（CRM）**: FedRAMP High のコントロール ベースラインで NIST SP 800-53 コントロールを実装する際の責任について詳細に説明したドキュメント。
*   **System Security Plan（SSP）**: セキュリティの認証境界とシステムのアーキテクチャ。このドキュメントでは、FedRAMP High のコントロール ベースラインに適用される NIST SP 800-53 コントロール要件と Google Cloud コントロール実装の詳細についても詳しく説明されています。

Google の[セールスチーム](https://cloud.google.com/contact?hl=ja)または Google Cloud の営業担当者が、このドキュメントへのアクセス方法をご案内します。 Google Cloud連邦政府機関の場合は、[FedRAMP パッケージ アクセス リクエスト フォーム](https://www.fedramp.gov/rev5/documents-templates/)を使用して、Google の FedRAMP パッケージをリクエストすることもできます。

ガイダンスと自動化
---------

このセクションで説明するように、Google は FedRAMP コンプライアンス義務の履行を支援するガイダンス ドキュメントと自動化ソリューションを提供しています。

### Control Mapping Guides

包括的な Google Cloud FedRAMP High CRM とは異なり、Control Mapping Guides（CMG）はサービスごとに特化しています。これらのガイドでは、 Google Cloud サービスの詳細なコントロール範囲について説明されています。これにより、FedRAMP High の要件を満たすようにサービスを構成できます。CMG では、ユーザーによる技術的な構成が必要な関連する NIST SP 800-53 コントロールに焦点が当てられています。また、CMG では、特定のサービス（およびサポート対象のGoogle Cloud と Google Workspace サービス）について、責任を明確にするために必要な手順も説明しています。

CMG は、BigQuery、データポータル Pro、Agent Platform の生成 AI、Agent Platform のエージェント検索、Cloud Logging、Compute Engine、Identity and Access Management（IAM）など、一部の Google Cloud サービス向けに提供されています。このドキュメントへのアクセス権を取得するには、秘密保持契約（NDA）に基づいて、[セールスチーム](https://cloud.google.com/contact?hl=ja)または Google Cloudの担当者にお問い合わせください。

### FedRAMP High 実装ガイド

FedRAMP High 実装ガイドは、FedRAMP High の範囲内のサービス固有の API を対象としています。これには、影響を受けるサービス機能や、保護されたデータの保存に適したデータ フィールドが含まれます。たとえば、FedRAMP High の要件を満たすサービス固有の API や、特定の Google Cloudサービスを FedRAMP High ワークロードで使用する際に使用する追加の構成の詳細が提供されています。これらの構成はデフォルトでは適用されないため、ユーザーが管理する必要があります。

FedRAMP High 実装ガイドは、Apigee、BigQuery、Cloud Key Management Service、Logging、Google Kubernetes Engine（GKE）、Agent Platform の生成 AI、Agent Platform のエージェント検索、Cloud Storage など、一部の Google Cloudサービス向けに提供されています。このドキュメントへのアクセス権を取得するには、NDA に基づいて、[セールスチーム](https://cloud.google.com/contact?hl=ja)またはGoogle Cloud の担当者にお問い合わせください。

データ所在地と SA-9(5) への準拠
--------------------

Google Cloudにアプリケーションをデプロイする場合、FedRAMP のデータ所在地の要件は FedRAMP High のコントロール ベースラインでのみ義務付けられます。これらの要件を適用するには、[FedRAMP High 用の Assured Workloads データ境界](https://docs.cloud.google.com/assured-workloads/docs/supported-products?hl=ja)を選択する必要があります。FedRAMP Moderate 認証を取得し、FedRAMP Moderate 用の Assured Workloads データ境界を選択した場合、要件が適用されないため、データ所在地の制御は行われません。

Google Cloud は、リージョン サービスに対して[データ所在地に関する契約上のコミットメント](https://cloud.google.com/terms/data-residency?hl=ja)を提供しており、特定のデータ ロケーションを使用するようにサービスを構成できます。これらのコミットメントにより、FedRAMP High データが米国のリージョンに保存され、米国に留まり、米国外の別のリージョンに移動されないことが保証されます。[FedRAMP High データ](https://www.fedramp.gov/understanding-baselines-and-impact-levels/)の例としては、法執行機関、緊急サービス、金融サービス、医療および公衆衛生システム、または 16 の[重要インフラ分野](https://www.cisa.gov/topics/critical-infrastructure-security-and-resilience/critical-infrastructure-sectors)のいずれかに属するデータがあります。

一部の Google Cloud サービスは、設計上リージョンに依存しないグローバル サービスであり、サービスがデプロイされるリージョンを指定できません。この設計方針は、グローバル サービスが正しく動作するために必要です。これらのリージョンに依存しないサービスまたはグローバル サービスの一部は、FedRAMP High データの処理、転送、保存に関わりません。リージョンに依存しないサービスまたはグローバル サービスのデータ所在地機能は制限されています。

2020 年 7 月、FedRAMP は FedRAMP High ベースライン SA-9(5) コントロールの更新をリリースし、影響度の高いデータ情報サービスの地理的位置を米国または米国の管轄下にある地域に制限しました。この更新後、一部の Google Cloud サービスが [FedRAMP Marketplace](https://marketplace.fedramp.gov/products/FR1805751477) でアスタリスク付きで次の注意書きが設定されました。「Services that are denoted with an asterisk (\*) do not meet the SA-9(5) requirement. Please review the JAB P-ATO letter for more information.（アスタリスク（\*）が付いているサービスは SA-9（5）の要件を満たしていません。詳しくは、JAB P-ATO レターをご確認ください）」

FedRAMP Marketplace で SA-9(5) の要件を満たしていないと注意書きが付けられた一部の Google Cloud サービスは、更新された SA-9(5) の証拠を添えた GSA への再提出がまだのため、3PAO による審査がまだ完了していません。Google は、FedRAMP Marketplace から SA-9(5) の注意書きを削除することを目的とした再提出作業を積極的に進めています。これらのサービスのステータスの詳細については、FedRAMP High CRM ドキュメントの **SA-9(5)** タブをご覧ください。

SA-9（5）に関する注意書きが付いた FedRAMP High 認証済みサービスの再評価プロセスが進行中ですが、Google は、FedRAMP High データの地理的制限に対処するために、[RMF ガイドライン](https://csrc.nist.gov/pubs/sp/800/37/r2/final)に記載されている緩和策を実装することをおすすめしています。 Google Cloudたとえば、このセクションの残りの部分で説明するように、データ暗号化を使用して FedRAMP High データに対する単独のコントロールを確立できます。

### デジタル主権とデータ所在地

Google は、物理的な場所に関係なくデータを保護するコンセプトであるデジタル主権を重視しています。このアプローチは、[Assured Workloads](https://docs.cloud.google.com/assured-workloads/docs/overview?hl=ja) と[ソフトウェア定義コミュニティ クラウド](https://cloud.google.com/blog/products/identity-security/software-defined-community-cloud-new-way-government-cloud?hl=ja)に基づいています。データ所在地を重視する従来の物理的な主権とは対照的に、 Google Cloud のデジタル主権管理はより高度なデータ保護を提供します。

デジタル主権により、データ保護に対する権限がユーザーに付与され、クラウド プロバイダや第三者による評価機関の保証に依存する必要がなくなります。デジタル主権とは、データ暗号鍵の独占的な所有権を通じて、データへのアクセスを単独で管理できることを意味します。

[RMF ガイドライン](https://csrc.nist.gov/pubs/sp/800/37/r2/final)に沿って、Google は、ネットワーク インフラストラクチャ内での転送中または米国以外のクラウド リージョンでに一時的に保存される可能される場合に FedRAMP High データにアクセスされるリスクに対処するために、緩和策を実装することをおすすめします。アクセス制限の主な手段は、転送中および保存時のデータ暗号化です。

FedRAMP High データを保護し、承認済みユーザーのみがアクセスできるようにするには、保存データの暗号化に顧客管理の暗号鍵を使用します。 Google Cloud は、転送中のデータの暗号化も提供します。以降のセクションでは、 Google Cloudで使用できるデータ暗号化テクノロジーについて説明します。データ暗号化は、FedRAMP High データが転送中に読み取られたり、保存時に他のテナントや Google の担当者にアクセスされたりするのを防ぐのに役立ちます。

### 顧客管理の暗号鍵

[（Cloud KMS）](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=ja) の[顧客管理の暗号鍵（CMEK）](https://docs.cloud.google.com/kms/docs/cmek?hl=ja)を使用すると、 Google Cloudの保存データを保護する鍵をお客様自身が所有して管理できます。鍵を使用できるGoogle Cloud サービスには、[_CMEK 統合_](https://docs.cloud.google.com/kms/docs/cmek?hl=ja)があります。これらの CMEK は、直接管理することも、 [Cloud KMS Autokey](https://docs.cloud.google.com/kms/docs/autokey-overview?hl=ja) を使用して管理することもできます。CMEK 統合をサポートするサービスは、Cloud KMS 鍵を使用してデータ暗号鍵（DEK）を暗号化またはラップします。DEK を鍵暗号鍵（KEK）でラップすることを、 [_エンベロープ暗号化_](https://docs.cloud.google.com/kms/docs/envelope-encryption?hl=ja)と言います。詳細については、[CMEK の使用に関する推奨事項](https://docs.cloud.google.com/kms/docs/cmek-recommended-practices?hl=ja)をご覧ください。CMEK をサポートするサービスの一覧については、[互換性のあるサービス](https://docs.cloud.google.com/kms/docs/compatible-services?hl=ja#cmek_integrations)をご覧ください。

[Cloud External Key Manager（Cloud EKM）](https://docs.cloud.google.com/kms/docs/ekm?hl=ja)を使用すると、 Google Cloud の外部で管理する暗号鍵を使用して、 Google Cloud内のデータを保護できます。保存データは、[サポートされる CMEK 統合サービス](https://docs.cloud.google.com/kms/docs/ekm?hl=ja#supported_services)で保護することも、Cloud Key Management Service API を直接呼び出して保護することもできます。

Google は、Cloud KMS の暗号鍵のセキュリティに関して、[以下を保証](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=ja#security-of-key-materials)します。

*   復号された鍵マテリアルは、API インターフェースまたは別のユーザー インターフェースからエクスポートまたは表示することはできません。
*   Google の担当者は、暗号化されていない顧客鍵のマテリアルにアクセスできません。さらに、鍵マテリアルはキーストアの KMS マスター鍵で暗号化され、KMS マスター鍵には Google の担当者もアクセスできません。
*   [ハードウェア セキュリティ モジュール（HSM）](https://docs.cloud.google.com/docs/security/cloud-hsm-architecture?hl=ja#strict_security_procedures_safeguard_hsm_hardware)では、Cloud KMS API ジョブが鍵マテリアルに復号された状態でアクセスすることはありません。 Google Cloud で利用可能な HSM は FIPS 140 検証済みです。
*   Google のシステム オペレータは、標準的な運用手順で定義されているとおり、職務中に顧客鍵マテリアルにアクセス、使用、抽出することはできません。

FedRAMP 認証には [FIPS 140 認定](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=ja)が必要です。たとえば、[SC-13 暗号保護](https://csrc.nist.gov/projects/cprt/catalog#/cprt/framework/version/SP_800_53_5_2_0/home?element=SC-13)コントロールでは、FIPS 140 認定済みの暗号または NSA 承認済みの暗号の使用が義務付けられています。Google は、保存データと転送中データの暗号化に FIPS 140 認定済みの暗号モジュールを提供しています。

### 保存時のデータの暗号化

Google Cloud は、デフォルトで[保存データを暗号化](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=ja)します。Google Cloud は、FIPS 140 認定の AES-256 対称ブロック暗号を使用して、ストレージ サービスに透過的なサーバーサイド暗号化を提供します。Cloud KMS で管理し、クラウドベースまたは外部の HSM に保存する独自の暗号鍵を作成することもできます。

[Cloud HSM](https://docs.cloud.google.com/kms/docs/hsm?hl=ja) を使用すると、暗号鍵をホストし、FIPS 140 認定取得済みの HSM のクラスタで暗号オペレーションを実行できます。Cloud HSM は Cloud KMS をフロントエンドとして使用し、CMEK 統合機能と Cloud KMS が提供するその他の機能へのアクセスを提供します。 Google Cloudは強力な [FIPS 140 認定](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=ja)暗号化を使用しているため、暗号化されたデータにアクセスできるのは、CMEK を所有しているユーザーのみです。

Google Cloud は、仮想マシンにアタッチされた[ディスクの暗号化](https://docs.cloud.google.com/compute/docs/disks/disk-encryption?hl=ja)に顧客管理の鍵もサポートしています。また、 Google Cloud は[クライアントサイド暗号化](https://docs.cloud.google.com/storage/docs/encryption/client-side-keys?hl=ja)をサポートしています。これにより、データをクラウドに送信する前に、独自のアプリケーション環境内でデータを暗号化できます。

### 転送中のデータの暗号化

Google Cloud は、次のように転送中のデータ暗号化をサポートしています。

*   透過的暗号化は、データセンターのリージョン間とアベイラビリティ ゾーン間の Google 管理のネットワーク トラフィックのネットワーク バックボーン全体で行われます。この暗号化は、[メディア アクセス制御セキュリティ（MACsec）](https://1.ieee802.org/security/802-1ae/)を使用して、物理データリンク層（ネットワーキング スタックのレイヤ 2）で実装されます。
*   Virtual Private Cloud（VPC）ネットワークとピアリングされた VPC ネットワーク内の [VM 間トラフィック](https://docs.cloud.google.com/load-balancing/docs/ssl-certificates/encryption-to-the-backends?hl=ja)は、透過的に暗号化されます。
*   アプリケーション層では、Google は転送中のデータの暗号化に [Transport Layer Security（TLS）](https://docs.cloud.google.com/load-balancing/docs/ssl-policies-concepts?hl=ja)を使用できるようにしています。また、サービス エンドポイントは、API 呼び出しを行うときに安全な HTTPS 接続を作成するために TLS をサポートしています。
*   VPC とオンプレミス インフラストラクチャ間の接続は、Cloud VPN を使用して利用できます。Cloud VPN は、インターネット経由または直接のプライベート回線経由で[安全な暗号化トンネル](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=ja)を作成します。

転送中のデータの暗号化には、エンドユーザーと Google の間の転送中のデータの暗号化と、Google ネットワーク内の転送中のデータの暗号化が含まれます。詳細については、[Google Cloudでの転送データの暗号化](https://docs.cloud.google.com/docs/security/encryption-in-transit?hl=ja)をご覧ください。

次のステップ
------

FedRAMP 認証を取得して Google Cloudのデプロイを開始するには、次を確認してください。

*   [FedRAMP と DoD のコンプライアンス範囲](https://docs.cloud.google.com/architecture/security/fedramp-dod-compliance-scope?hl=ja)
*   [Google Cloud で FedRAMP と DoD に準拠したネットワークを構成する](https://docs.cloud.google.com/architecture/configure-networks-fedramp-dod-google-cloud?hl=ja)
*   [Google Cloudでの FedRAMP コンプライアンスの提供](https://cloud.google.com/security/compliance/fedramp?hl=ja)
*   [規制、コンプライアンス、プライバシーのニーズに対応する](https://docs.cloud.google.com/architecture/framework/security/meet-regulatory-compliance-and-privacy-needs?hl=ja)
*   [Google Cloud における責任の共有と運命の共有](https://docs.cloud.google.com/architecture/framework/security/shared-responsibility-shared-fate?hl=ja)
*   [Assured Workloads による Data Boundary](https://cloud.google.com/security/products/assured-workloads?hl=ja)
*   [コントロール パッケージ別のサポート対象サービス](https://docs.cloud.google.com/assured-workloads/docs/supported-products?hl=ja)
*   [FedRAMP Marketplace entry for Google Cloud](https://marketplace.fedramp.gov/products/FR1805751477)
*   [FedRAMP 20x Initiative](https://www.fedramp.gov/20x/)
*   [FedRAMP Rev 5 Agency Authorization](https://www.fedramp.gov/rev5/agency-authorization/)
*   [FedRAMP documents and templates](https://www.fedramp.gov/rev5/documents-templates/)
*   [NIST SP 800-53: Security and Privacy Controls for Information Systems and Organizations](https://csrc.nist.gov/Projects/risk-management/sp800-53-controls/release-search#/800-53)
*   [NIST SP 800-37: Risk Management Framework for Information Systems and Organizations: A System Life Cycle Approach for Security and Privacy](https://csrc.nist.gov/pubs/sp/800/37/r2/final)
*   [NIST FIPS 199: Standards for Security Categorization of Federal Information and Information Systems](https://csrc.nist.gov/pubs/fips/199/final)
