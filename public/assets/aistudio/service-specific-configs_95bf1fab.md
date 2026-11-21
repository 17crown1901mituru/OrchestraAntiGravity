# Cloud Run と Cloud Run functions のサービスを構成する &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/service-specific-configs?hl=ja

以下に挙げる環境変数を使用して、Cloud Run または Cloud Run functions にデプロイされたときのサービスの動作を構成します。

[コンテナ イメージでこれらの環境変数を設定する方法をご確認ください](https://docs.cloud.google.com/docs/buildpacks/set-environment-variables?hl=ja)。

特定の言語の構成の詳細については、以下をご覧ください。

*   [Go](https://docs.cloud.google.com/docs/buildpacks/go?hl=ja)
*   [Java](https://docs.cloud.google.com/docs/buildpacks/java?hl=ja)
*   [Node.js](https://docs.cloud.google.com/docs/buildpacks/nodejs?hl=ja)
*   [Python](https://docs.cloud.google.com/docs/buildpacks/python?hl=ja)
*   [Ruby](https://docs.cloud.google.com/docs/buildpacks/ruby?hl=ja)
*   [PHP](https://docs.cloud.google.com/docs/buildpacks/php?hl=ja)

Cloud Run と Cloud Run functions の環境変数
-------------------------------------

次の構成は、アプリケーションと機能の両方を対象としたサービスのビルドをサポートしています。Cloud Run functions の他の環境変数については、[Cloud Run functions の他の環境変数](#additional_environment_variables)をご覧ください。

### `GOOGLE_ENTRYPOINT`

コンテナの実行時に実行されるコマンドを指定します。これは、Dockerfile の[エントリポイント](https://docs.docker.com/engine/reference/builder/#entrypoint)と同じです。

例:

### Cloud Run ソースのデプロイ

*   Java: `java -jar target/myjar.jar`
*   PHP: `php -S 0.0.0.0:8080 index.php`
*   Python:

Python Buildpack は、[Cloud Run ソースのデプロイ](https://docs.cloud.google.com/run/docs/deploying-source-code?hl=ja)のデフォルトのエントリ ポイントを設定します。Python バージョン 3.13 以降では、Python Buildpack は `requirements.txt` ファイルのウェブサービス構成に基づいてエントリポイントを設定します。`requirements.txt` ファイルでウェブサーバーまたはフレームワークを指定しない場合、または Python バージョン 3.12 以前を使用する場合、Python Buildpack はデフォルトのエントリポイントを `gunicorn -b :8080 main:app` に設定します。詳細については、[Python アプリケーションのビルド](https://docs.cloud.google.com/docs/buildpacks/python?hl=ja#run-source-deploys)をご覧ください。

### その他すべてのデプロイ

*   Java: `java -jar target/myjar.jar`
*   PHP: `php -S 0.0.0.0:8080 index.php`
*   Python: `gunicorn -b :8080 main:app`

### `GOOGLE_RUNTIME`

ランタイムで強制的にオプトインします。ランタイム Buildpack イメージが複数のグループに表示される場合、最初のグループの Buildpack イメージがすべてのグループで使用されます。

Node.js の例: `nodejs` を指定すると、Node.js ランタイム Buildpack は強制的に有効になります。

### `GOOGLE_RUNTIME_VERSION`

インストールするランタイムのバージョンを指定します。.NET の場合は、.NET SDK のバージョンを指定します。

例:

*   Go: `1.14.1`
*   Java: `8`
*   Node.js: `13.7.0`
*   .NET: `3.1.301`

### `GOOGLE_BUILDABLE`

Go、Java、Dart、.NET ランタイムの場合: ビルド可能ユニットへのパスを指定します。

Go の例: `./maindir` を指定すると、`maindir` をルートとするパッケージがビルドされます。

### `GOOGLE_BUILD_ARGS`

Java（Maven と Gradle）と .NET ランタイムの場合: ビルドコマンドに引数を追加します。

Java の例: `-Pprod` を指定すると、`mvn clean package ... -Pprod` が実行されます。

### `GOOGLE_MAVEN_BUILD_ARGS`

Maven プラグインを使用する Java ランタイムの場合: デフォルトの Maven コマンドを、指定したビルドコマンドでオーバーライドします。

`GOOGLE_MAVEN_BUILD_ARGS` が指定されていない場合、デフォルトで次のコマンドが実行されます。

mvn clean package --batch-mode -DskipTests -Dhttp.keepAlive=false

例: `GOOGLE_MAVEN_BUILD_ARGS="clean package:` を指定すると、`mvn clean package` コマンドが実行されます。

### `GOOGLE_GRADLE_BUILD_ARGS`

Gradle プラグインを使用する Java ランタイムの場合: デフォルトの Gradle コマンドを、指定したビルドコマンドでオーバーライドします。

`GOOGLE_GRADLE_BUILD_ARGS` が指定されていない場合、デフォルトで次のコマンドが実行されます。

gradle clean assemble -x test --build-cache

例: `GOOGLE_GRADLE_BUILD_ARGS="clean assemble"` を指定すると、`gradle clean assemble` コマンドが実行されます。

### `GOOGLE_DEVMODE`

[Skaffold](https://skaffold.dev/): 開発モードの Buildpack を有効にします。ライブローカル開発を使用して、ソースコードの変更時にコンテナの自動再ビルドをトリガーします。Skaffold をインストールして `skaffold dev` を実行する必要があります。

*   サポートされる値: `true`、`True`、`1`

### `GOOGLE_CLEAR_SOURCE`

関数と Go または Java アプリケーションの場合: アプリケーションのビルド後にソースをクリアします。アプリケーションが Go テンプレートなどの静的ファイルに依存している場合、この変数を設定すると、アプリケーションが適切に動作しない可能性があります。

サポートされている値: `true`、`True`、`1`

Cloud Run functions の他の環境変数
---------------------------

以下の構成は、[Functions Framework](https://cloud.google.com/functions/docs/functions-framework?hl=ja) と Cloud Run functions を使用する関数としてビルドされたソースコードでのみ使用できます。これらの構成オプションの詳細については、[契約](https://github.com/GoogleCloudPlatform/functions-framework)をご覧ください。

### `GOOGLE_FUNCTION_TARGET`

*   リクエストに応じて呼び出すエクスポート済みの関数名を指定します。
*   **例:** `myFunction` は、Functions Framework で同じ名前の関数を呼び出すようにします。

### `GOOGLE_FUNCTION_SIGNATURE_TYPE`

*   関数が使用するシグネチャを指定します。
*   **例**: `http`、`event`、`cloudevent`

### `GOOGLE_FUNCTION_SOURCE`

*   言語に応じて、関数のソースを含むディレクトリまたはファイルの名前を指定します。
*   （一部の言語のみ: 言語別の[ドキュメント](https://github.com/GoogleCloudPlatform/functions-framework#languages)をご覧ください。）
*   **例:** `function.py`（Python）
