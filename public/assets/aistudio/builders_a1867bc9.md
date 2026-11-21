# ビルダー &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/builders?hl=ja

ビルダー
----

汎用ビルダー
------

Buildpack には、 Google Cloud サービスで実行するように設計されたコンテナ イメージを作成するデフォルトの汎用ビルダーが含まれています。これには、Google Kubernetes Engine、GKE Enterprise、Cloud Run、App Engine、Cloud Run functions などがありますが、これらに限定されません。

Buildpack 汎用ビルダー:

1.  ソースコードの言語を自動的に検出します。Buildpack 汎用ビルダーは、一般的なプロジェクト構成ファイルをスキャンして、具体的な言語バージョンを判断します。たとえば、ビルダーがソースコードを Node.js として検出すると、`package.json` ファイルの `engines` フィールドを調べて、Node.js の具体的なバージョンを判断します。具体的なバージョンを判断できない場合、ビルダーは利用可能な最新バージョンを使用します。
2.  ソースコードがアプリケーション（サービス）か関数かを判断します。
3.  ソースコードを使用して、デプロイ可能なコンテナ イメージをビルドします。

汎用ビルダー自体は Container Registry に一般公開されるコンテナ イメージです。

| タグ | 場所 | オペレーティング システム |
| --- | --- | --- |
| `google-24`（プレビュー） | [gcr.io/buildpacks/builder:google-24](https://gcr.io/buildpacks/builder:google-24) | Ubuntu 24 |
| `latest` | [gcr.io/buildpacks/builder:latest](https://gcr.io/buildpacks/builder:latest) | Ubuntu 22。`latest` タグは、デフォルトで汎用ビルダー バージョン `google-22` に設定されています。 |
| `google-22` | [gcr.io/buildpacks/builder:google-22](https://gcr.io/buildpacks/builder:google-22) | Ubuntu 22 |
| `v1` | [gcr.io/buildpacks/builder:v1](https://gcr.io/buildpacks/builder:v1) | Ubuntu 18 |

### デフォルトのビルダー

最新のバージョンとオペレーティング システムを常に使用するには、`latest` タグを指定します。`latest` タグはデフォルトで汎用ビルダーの `google-22` バージョンとなり、`gcloud run deploy` コマンドを実行するときに使用されます。バージョンに固定する必要がある場合は、[特定のビルダーを使用する](https://docs.cloud.google.com/docs/buildpacks/use-a-specific-builder?hl=ja)方法の説明をご覧ください。

### `builder:google-24`（プレビュー）でサポートされている言語

| 言語 | サポート対象のバージョン | アプリケーション | 関数 |
| --- | --- | --- | --- |
| [Python](https://docs.cloud.google.com/docs/buildpacks/python?hl=ja) | 3.13.x | ✓ | ✓ |
|  | 3.14.x | ✓ | ✓ |
| [Node.js](https://docs.cloud.google.com/docs/buildpacks/nodejs?hl=ja) | 22.x.x | ✓ | ✓ |
|  | 24.x.x | ✓ | ✓ |
| [Go](https://docs.cloud.google.com/docs/buildpacks/go?hl=ja) | 1.x | ✓ | ✓ |
| [Java](https://docs.cloud.google.com/docs/buildpacks/java?hl=ja) | 17 | ✓ | ✓ |
|  | 21 | ✓ | ✓ |
|  | 25 | ✓ | ✓ |
| [Ruby](https://docs.cloud.google.com/docs/buildpacks/ruby?hl=ja) | 3.2.x | ✓ | ✓ |
|  | 3.3.x | ✓ | ✓ |
|  | 3.4.x | ✓ | ✓ |
| [PHP](https://docs.cloud.google.com/docs/buildpacks/php?hl=ja) | 8.2.x | ✓ | ✓ |
|  | 8.3.x | ✓ | ✓ |
|  | 8.4.x | ✓ | ✓ |
| .NET Core | 8.x.x | ✓ | ✓ |

### `builder:google-22` でサポートされる言語

| 言語 | サポート対象のバージョン | アプリケーション | 関数 |
| --- | --- | --- | --- |
| [Python](https://docs.cloud.google.com/docs/buildpacks/python?hl=ja) | 3.10.x | ✓ | ✓ |
|  | 3.11.x | ✓ | ✓ |
|  | 3.12.x | ✓ | ✓ |
|  | 3.13.x | ✓ | ✓ |
| [Node.js](https://docs.cloud.google.com/docs/buildpacks/nodejs?hl=ja) | 12.x.x | ✓ | ✓ |
|  | 14.x.x | ✓ | ✓ |
|  | 16.x.x | ✓ | ✓ |
|  | 18.x.x | ✓ | ✓ |
|  | 20.x.x | ✓ | ✓ |
|  | 22.x.x | ✓ | ✓ |
|  | 24.x.x | ✓ | ✓ |
| [Java](https://docs.cloud.google.com/docs/buildpacks/java?hl=ja) | 8 |  | ✓ |
|  | 11 | ✓ | ✓ |
|  | 17 | ✓ | ✓ |
|  | 21 | ✓ | ✓ |
| [Go](https://docs.cloud.google.com/docs/buildpacks/go?hl=ja) | 1.x | ✓ | ✓ |
| [Ruby](https://docs.cloud.google.com/docs/buildpacks/ruby?hl=ja) | 3.1.x | ✓ | ✓ |
|  | 3.2.x | ✓ | ✓ |
|  | 3.3.x | ✓ | ✓ |
|  | 3.4.x | ✓ | ✓ |
| [PHP](https://docs.cloud.google.com/docs/buildpacks/php?hl=ja) | 8.1.x | ✓ | ✓ |
|  | 8.2.x | ✓ | ✓ |
|  | 8.3.x | ✓ | ✓ |
|  | 8.4.x | ✓ | ✓ |
| .NET Core | 6.x | ✓ | ✓ |
|  | 7.x | ✓ | ✓ |
|  | 8.x | ✓ | ✓ |

### `builder:v1` でサポートされる言語

| 言語 | サポート対象のバージョン | アプリケーション | 関数 |
| --- | --- | --- | --- |
| [Python](https://docs.cloud.google.com/docs/buildpacks/python?hl=ja) | 3.7.x | ✓ | ✓ |
|  | 3.8.x | ✓ | ✓ |
|  | 3.9.x | ✓ | ✓ |
|  | 3.10.x | ✓ | ✓ |
|  | 3.11.x | ✓ | ✓ |
| [Node.js](https://docs.cloud.google.com/docs/buildpacks/nodejs?hl=ja) | 8.x.x | ✓ | ✓ |
|  | 12.x.x | ✓ | ✓ |
|  | 14.x.x | ✓ | ✓ |
|  | 16.x.x | ✓ | ✓ |
|  | 18.x.x | ✓ | ✓ |
|  | 20.x.x | ✓ | ✓ |
| [Java](https://docs.cloud.google.com/docs/buildpacks/java?hl=ja) | 8. |  | ✓ |
|  | 11 | ✓ | ✓ |
|  | 17 | ✓ | ✓ |
| [Go](https://docs.cloud.google.com/docs/buildpacks/go?hl=ja) | 1.x | ✓ | ✓ |
| [Ruby](https://docs.cloud.google.com/docs/buildpacks/ruby?hl=ja) | 2.5.x | ✓ | ✓ |
|  | 2.6.x | ✓ | ✓ |
|  | 2.7.x | ✓ | ✓ |
|  | 3.0.x | ✓ | ✓ |
|  | 3.1.x | ✓ | ✓ |
|  | 3.2.x | ✓ | ✓ |
|  | 3.3.x | ✓ | ✓ |
| [PHP](https://docs.cloud.google.com/docs/buildpacks/php?hl=ja) | 7.4.x | ✓ | ✓ |
|  | 8.0.x | ✓ | ✓ |
|  | 8.1.x | ✓ | ✓ |
|  | 8.2.x | ✓ | ✓ |
| .NET Core | 3.1.x | ✓ | ✓ |
|  | 6.x | ✓ | ✓ |
|  | 7.x | ✓ | ✓ |
|  | 8.x | ✓ | ✓ |

App Engine ビルダー
---------------

Buildpack は、App Engine の[第 2 世代ランタイム](https://docs.cloud.google.com/appengine/docs/standard/runtimes?hl=ja)のアプリケーションをコンテナ化するために使用されるビルダーも公開します。

App Engine ビルダーには、追加の Buildpack と App Engine 固有の構成が含まれています。たとえば、実行速度と自動更新のために最適化されます。ビルダーは、次のようにランタイム言語とオペレーティング システム別に存在します。

| 言語 | オペレーティング システム | 場所 | 利用可能なバージョン |
| --- | --- | --- | --- |
| Python | Ubuntu 18 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/python](http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/build/python) | Python 3.7、3.8、3.9、3.10、3.11 |
|  | Ubuntu 22 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/python](http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/build/python) | Python 3.10、3.11 |
| Node.js | Ubuntu 18 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/nodejs](http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/nodejs) | Node.js 10、12、14 |
|  | Ubuntu 22 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/nodejs](http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/nodejs) | Node.js 18、20 |
|  | Ubuntu 24 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-24-full/builder/nodejs](http://us-central1-docker.pkg.dev/serverless-runtimes/google-24-full/builder/nodejs) | Node.js 24 |
| Go | Ubuntu 18 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/go](http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/go) | Go 1.12、1.13、1.14、1.15、1.16、1.17、1.18、1.19、1.20 |
|  | Ubuntu 22 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/go](http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/go) | Go 1.16、1.17、1.18、1.19、1.20 |
| Java | Ubuntu 18 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/java](http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/java) | Java 8、11 |
|  | Ubuntu 22 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/java](http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/java) | Java 17、21 |
|  | Ubuntu 24 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-24-full/builder/java](http://us-central1-docker.pkg.dev/serverless-runtimes/google-24-full/builder/java) | Java 25 |
| PHP | Ubuntu 18 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/php](http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/php) | PHP 7.2、7.3、7.4、8.1 |
|  | Ubuntu 22 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/php](http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/php) | PHP 8.2 |
| Ruby | Ubuntu 18 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/ruby](http://us-central1-docker.pkg.dev/serverless-runtimes/google-18-full/builder/ruby) | Ruby 2.5、2.6、2.7、3.0、3.1 |
|  | Ubuntu 22 | [http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/ruby](http://us-central1-docker.pkg.dev/serverless-runtimes/google-22-full/builder/ruby) | Ruby 3.2 |

次のステップ
------

*   [特定のビルダーを使用する](https://docs.cloud.google.com/docs/buildpacks/use-a-specific-builder?hl=ja)
*   [アプリケーションを構築する](https://docs.cloud.google.com/docs/buildpacks/build-application?hl=ja)
*   [関数を作成する](https://docs.cloud.google.com/docs/buildpacks/build-function?hl=ja)

特に記載のない限り、このページのコンテンツは[クリエイティブ・コモンズの表示 4.0 ライセンス](https://creativecommons.org/licenses/by/4.0/)により使用許諾されます。コードサンプルは [Apache 2.0 ライセンス](https://www.apache.org/licenses/LICENSE-2.0)により使用許諾されます。詳しくは、[Google Developers サイトのポリシー](https://developers.google.com/site-policies?hl=ja)をご覧ください。Java は Oracle および関連会社の登録商標です。

最終更新日 2025-12-17 UTC。
