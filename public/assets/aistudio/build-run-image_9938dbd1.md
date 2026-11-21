# ビルドイメージと実行イメージを構成する &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/build-run-image?hl=ja

ビルドイメージと実行イメージを構成する
-------------------

Buildpack は、デフォルトの[ビルダー](https://buildpacks.io/docs/for-platform-operators/concepts/builder/)を使用します。このビルダーは、サービスのビルドを実行するために必要なコンポーネント（ビルドイメージと実行イメージを含む）がすべて含まれています。

*   **ビルドイメージ**: ビルダー イメージが使用し、Buildpack の[ライフサイクル](https://buildpacks.io/docs/for-platform-operators/concepts/lifecycle/)が実行されるビルド環境を構築します。ここでは、コンテナ化するアプリケーションまたは関数が準備されます。
*   **実行イメージ**: サービスのコンテナ イメージをビルドするために使用されるベースイメージ。これは、ビルドしたアプリケーションまたは関数をホストするイメージです。

どちらのイメージも必要に応じてカスタマイズできます。たとえば、イメージをカスタマイズして、サービスのビルドに必要なパッケージを追加したり、サービスの実行時に利用できるようにシステム パッケージをインストールできます。

始める前に
-----

`pack` CLI を使用して、ローカルでサービスからコンテナ イメージをビルドします。

### 始める前に

1.  [Docker Community Edition（CE）](https://docs.docker.com/engine/installation/)をワークステーションにインストールします。`pack` は Docker を OCI イメージ ビルダーとして使用します。
2.  [Pack CLI](https://buildpacks.io/docs/tools/pack/) をインストールします。
3.  GitHub からサンプル アプリケーションを取得するために [Git ソース管理](https://git-scm.com/downloads)ツールをインストールします。

ビルドイメージと実行イメージをカスタマイズする
-----------------------

Buildpack はビルダー イメージを使用して、サービスからコンテナ イメージをビルドします。ビルドプロセスが完了すると、アプリケーションまたは関数が実行イメージに挿入されます。ビルドプロセスの詳細については、[Buildpack のコンセプト](https://buildpacks.io/docs/for-platform-operators/concepts/)をご覧ください。

### ビルダー イメージの拡張

デフォルトのビルダー イメージをカスタマイズするには:

1.  デフォルトのビルダー イメージからカスタム `builder.Dockerfile` を作成します。ベースイメージをサポートする[ビルダーのバージョン](https://docs.cloud.google.com/docs/buildpacks/builders?hl=ja)のタグを指定する必要があります。たとえば、`:v1` ベースイメージ タグは `:google-22` ビルダータグではサポートされません。
    
    例:
    
    FROM gcr.io/buildpacks/builder
        USER root
        RUN apt-get update && apt-get install -y --no-install-recommends \
          subversion && \
          apt-get clean && \
          rm -rf /var/lib/apt/lists/*
        USER cnb
    
2.  `builder.Dockerfile` ファイルからカスタム ビルダー イメージをビルドします。
    
    docker build -t BUILDER_IMAGE_NAME -f builder.Dockerfile .
    
    `BUILDER_IMAGE_NAME` は、カスタム ビルダー イメージに選択した名前に置き換えます。
    
3.  カスタム ビルダー イメージを使用して [`pack build` コマンド](https://buildpacks.io/docs/tools/pack/cli/pack_build/)を実行し、アプリケーションや関数の新しいコンテナ イメージをビルドします。
    
    pack build SERVICE_IMAGE_NAME --builder BUILDER_IMAGE_NAME
    
    次のように置き換えます。
    
    *   `SERVICE_IMAGE_NAME` は、アプリケーションまたは関数イメージに選択した名前に置き換えます。
    *   `BUILDER_IMAGE_NAME` は、カスタム ビルダー イメージの名前に置き換えます。

### 実行イメージの拡張

デフォルトの実行コンテナ イメージをカスタマイズするには:

1.  デフォルトの実行イメージからカスタム `run.Dockerfile` を作成します。実行イメージをサポートする[ビルダーのバージョン](https://docs.cloud.google.com/docs/buildpacks/builders?hl=ja)のタグを指定する必要があります。たとえば、`:v1` 実行イメージタグは、`:google-22` ビルダータグでサポートされていません。
    
    例:
    
    FROM gcr.io/buildpacks/gcp/run
        USER root
        RUN apt-get update && apt-get install -y --no-install-recommends \
          imagemagick && \
          apt-get clean && \
          rm -rf /var/lib/apt/lists/*
        USER 33:33
    
2.  `run.Dockerfile` ファイルからカスタム実行イメージをビルドします。
    
    docker build -t RUN_IMAGE_NAME -f run.Dockerfile .
    
    `RUN_IMAGE_NAME` は、カスタム実行イメージに選択した名前に置き換えます。
    
3.  カスタム実行イメージを使用して [`pack build` コマンド](https://buildpacks.io/docs/tools/pack/cli/pack_build/)を実行し、アプリケーションや関数の新しいコンテナ イメージをビルドします。
    
    pack build SERVICE_IMAGE_NAME --builder gcr.io/buildpacks/builder:v1 --run-image RUN_IMAGE
    
    次のように置き換えます。
    
    *   `SERVICE_IMAGE_NAME` は、アプリケーションまたは関数イメージに選択した名前に置き換えます。
    *   `RUN_IMAGE_NAME` は、カスタム実行イメージの名前に置き換えます。

特に記載のない限り、このページのコンテンツは[クリエイティブ・コモンズの表示 4.0 ライセンス](https://creativecommons.org/licenses/by/4.0/)により使用許諾されます。コードサンプルは [Apache 2.0 ライセンス](https://www.apache.org/licenses/LICENSE-2.0)により使用許諾されます。詳しくは、[Google Developers サイトのポリシー](https://developers.google.com/site-policies?hl=ja)をご覧ください。Java は Oracle および関連会社の登録商標です。

最終更新日 2026-11-11 UTC。
