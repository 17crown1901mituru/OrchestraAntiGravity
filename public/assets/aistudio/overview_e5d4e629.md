# Google Cloud の Buildpack &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/overview?hl=ja

Google Cloud の Buildpack は、アプリケーションのソースコードを取得してプロダクション レディなコンテナ イメージに変換する[オープンソース プロジェクト](https://github.com/GoogleCloudPlatform/buildpacks)です。 Google Cloud で公開される Buildpack は、[Cloud Native Buildpack の仕様](https://buildpacks.io/)を実装し、 Google Cloudにデプロイ可能なコンテナをビルドして構成するように設計されています。

Buildpack は通常、言語コンポーネント、ツールチェーン、アプリ コンポーネント（Python、`pip`、ウェブサーバーなど）の部分を処理します。Buildpack は、[ビルダー](https://docs.cloud.google.com/docs/buildpacks/builders?hl=ja)というコレクションにグループ化されます。このコレクションは、プロジェクトのソースコードを分析してビルドプランを作成し、デプロイ可能なコンテナ イメージを生成します。

Buildpack を使用したコンテナ化
--------------------

[Google Cloud サーバーレス](https://cloud.google.com/serverless?hl=ja) プロダクトでアプリケーション（サービス）または関数をデプロイして提供すると、Buildpack を使用して、実行可能なコンテナにコードがパッケージ化されます。Cloud Run では、ビルド済みのコンテナをデプロイすることも、[ソースコードをデプロイ](https://cloud.google.com/run/docs/deploying-source-code?hl=ja)して Cloud Run でコンテナビルドを管理することもできます。Cloud Run functions と App Engine では、コンテナ化プロセスがフルマネージドです。つまり、ソースコードをデプロイすると、コンテナ イメージのパッケージ化と変換がすべて自動的に行われます。

各コンテナ イメージは、ソースコード、システムとライブラリの依存関係、構成データ、静的アセットなど、デプロイの実行に必要なすべてのコンポーネントを使用してビルドされます。デフォルトでは、 Google Cloud のサーバーレス プロダクトは、デプロイ パイプライン用の Cloud Build や、コンテナ イメージのストレージと管理に使用される Container Registry または Artifact Registry など、同じ基盤となるサービスを使用します。

Google Cloud の Buildpack を使用する場合:

*   ビルダーと Buildpack は、ビルドプロセスを処理し、実行可能なコンテナ イメージを作成するように事前構成されています。
*   Buildpack は[複数のプログラミング言語](https://docs.cloud.google.com/docs/buildpacks/builders?hl=ja)をサポートし、ソースコードで使用されている言語を自動的に検出します。
*   [Buildpack をカスタマイズして拡張](https://docs.cloud.google.com/docs/buildpacks/build-run-image?hl=ja)すると、追加のシステム依存関係をインストールできます。ただし、デフォルト ビルダーは、プログラミング言語のパッケージ リポジトリから依存関係をインストールする場合や、言語の共通依存関係ファイルを使用するなど、プログラミング言語で必要となる一般的なタスクを処理できます。

Buildpack を使用してコードをコンテナ化する
--------------------------

Buildpack は、次のことを行うためにローカルまたはリモートで使用できます。

*   [アプリケーションまたはサービスを構築する](https://docs.cloud.google.com/docs/buildpacks/build-application?hl=ja)
*   [関数を作成する](https://docs.cloud.google.com/docs/buildpacks/build-function?hl=ja)

アプリを起動する標準的な方法を除外するプログラミング言語の場合、`Procfile` を使用して、コンテナの起動時に呼び出すプロセスを定義できます。`Procfile` は、任意の Buildpack タイプのデフォルトの開始プロセスをオーバーライドするために使用できますが、[Python](https://cloud.google.com/docs/buildpacks/python?hl=ja#application_entrypoint) など、一部のものでは必須になります。

次のステップ
------

*   [ビルダー](https://docs.cloud.google.com/docs/buildpacks/builders?hl=ja)
*   [アプリケーションを構築する](https://docs.cloud.google.com/docs/buildpacks/build-application?hl=ja)
*   [関数を作成する](https://docs.cloud.google.com/docs/buildpacks/build-function?hl=ja)
*   [Cloud Native Buildpack](https://buildpacks.io/docs/for-platform-operators/concepts/) について学習する
