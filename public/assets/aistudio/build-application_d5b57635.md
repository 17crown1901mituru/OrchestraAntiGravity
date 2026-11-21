# Buildpack を使用してアプリケーションをビルドする &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/build-application?hl=ja

このガイドでは、アプリケーションのソースコードで Buildpack を使用してコンテナ イメージを作成する方法について説明します。たとえば、Buildpack を使用して [Cloud Run](https://docs.cloud.google.com/run/docs/deploying?hl=ja) サービスのソースコードからコンテナ イメージをビルドします。

Buildpack を使用してコンテナ イメージをビルドする方法は 2 つあります。

*   `pack` CLI を使用して**ローカルでビルド**し、アプリケーションをローカルでテストします。また、デプロイ前に変更のプロトタイプを迅速に作成します。
*   [Cloud Build](https://docs.cloud.google.com/build/docs/overview?hl=ja) を使用して**リモートでビルドします**。Cloud Build を使用したビルドは、リソースを大量に消費するビルドプロセスを含む大規模なアプリケーションに有用です。また、[ソフトウェア サプライ チェーンの保護](https://docs.cloud.google.com/build/docs/view-build-security-insights?hl=ja)にも役立ちます。

ローカルビルド
-------

`pack` CLI を使用して、ローカルでアプリケーションからコンテナ イメージをビルドします。

### 始める前に

1.  [Docker Community Edition（CE）](https://docs.docker.com/engine/installation/)をワークステーションにインストールします。`pack` は Docker を OCI イメージ ビルダーとして使用します。
2.  [Pack CLI](https://buildpacks.io/docs/tools/pack/) をインストールします。
3.  GitHub からサンプル アプリケーションを取得するために [Git ソース管理](https://git-scm.com/downloads)ツールをインストールします。

### アプリケーションをローカルでビルドする

`pack build` コマンドを使用して、デフォルトのビルダー `--builder=gcr.io/buildpacks/builder` を指定し、コンテナ イメージをローカルでビルドします。

pack build --builder=gcr.io/buildpacks/builder IMAGE_NAME

`IMAGE_NAME` は、サービスのコンテナ イメージの名前に置き換えます。

[ビルドイメージと実行イメージを拡張](https://docs.cloud.google.com/docs/buildpacks/build-run-image?hl=ja)して、コンテナ イメージをカスタマイズすることもできます。

#### サンプル アプリケーションをローカルでビルドする

次の例は、ローカルでサンプルを作成する方法を示しています。

1.  ローカルマシンにサンプル リポジトリのクローンを作成します。
    
    git clone https://github.com/GoogleCloudPlatform/buildpack-samples.git
    
2.  アプリケーションのサンプルコードが含まれているディレクトリに移動します。
    
    ### Go
    
    cd buildpack-samples/sample-go
    
    ### Java
    
    cd buildpack-samples/sample-java-gradle
    
    ### Node.js
    
    cd buildpack-samples/sample-node
    
    ### PHP
    
    cd buildpack-samples/sample-php
    
    ### Python
    
    cd buildpack-samples/sample-python
    
    ### Ruby
    
    cd buildpack-samples/sample-ruby
    
    ### .NET
    
    cd buildpack-samples/sample-dotnet
    
3.  `pack` を使用してサンプル アプリケーション イメージをビルドします。
    
    ### Go
    
    pack build --builder=gcr.io/buildpacks/builder sample-go
    
    ### Java
    
    pack build --builder=gcr.io/buildpacks/builder sample-java-gradle
    
    ### Node.js
    
    pack build --builder=gcr.io/buildpacks/builder sample-node
    
    ### PHP
    
    pack build --builder=gcr.io/buildpacks/builder sample-php
    
    ### Python
    
    pack build --builder=gcr.io/buildpacks/builder sample-python
    
    ### Ruby
    
    pack build --builder=gcr.io/buildpacks/builder sample-ruby
    
    ### .NET
    
    pack build --builder=gcr.io/buildpacks/builder sample-dotnet
    
4.  `docker` を使用してイメージを実行します。
    
    ### Go
    
    docker run -p8080:8080 sample-go
    
    ### Java
    
    docker run -it -ePORT=8080 -p8080:8080 sample-java-gradle
    
    ### Node.js
    
    docker run -it -ePORT=8080 -p8080:8080 sample-node
    
    ### PHP
    
    docker run -it --rm -p 8080:8080 sample-php
    
    ### Python
    
    docker run -it -ePORT=8080 -p8080:8080 sample-python
    
    ### Ruby
    
    docker run -it -ePORT=8080 -p8080:8080 sample-ruby
    
    ### .NET
    
    docker run -it -ePORT=8080 -p8080:8080 sample-dotnet
    
5.  [localhost:8080](localhost:8080) を使用して、実行中のアプリケーションにアクセスします。

リモートビルド
-------

[Cloud Build](https://docs.cloud.google.com/build?hl=ja) を使用してアプリケーションをコンテナ イメージに作成し、イメージの保存とデプロイを行うコンテナ リポジトリとして [Artifact Registry](https://docs.cloud.google.com/artifact-registry?hl=ja) を使用します。

### 始める前に

*   Sign in to your Google Cloud account. If you're new to Google Cloud, [create an account](https://console.cloud.google.com/freetrial?hl=ja) to evaluate how our products perform in real-world scenarios. New customers also get $300 in free credits to run, test, and deploy workloads.
*   In the Google Cloud console, on the project selector page, select or create a Google Cloud project.
    
    **Roles required to select or create a project**
    
    *   **Select a project**: Selecting a project doesn't require a specific IAM role—you can select any project that you've been granted a role on.
    *   **Create a project**: To create a project, you need the Project Creator role (`roles/resourcemanager.projectCreator`), which contains the `resourcemanager.projects.create` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ja).
    
    [Go to project selector](https://console.cloud.google.com/projectselector2/home/dashboard?hl=ja)
    
*   [Verify that billing is enabled for your Google Cloud project](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled?hl=ja#confirm_billing_is_enabled_on_a_project).
    
*   Enable the Cloud Build and Artifact Registry APIs.
    
    **Roles required to enable APIs**
    
    To enable APIs, you need the Service Usage Admin IAM role (`roles/serviceusage.serviceUsageAdmin`), which contains the `serviceusage.services.enable` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ja).
    
    [Enable the APIs](https://console.cloud.google.com/flows/enableapi?apiid=cloudbuild.googleapis.com%2C+artifactregistry.googleapis.com&%3Bredirect=https%3A%2F%2Fcloud.google.com%2Fdocs%2Fbuildpacks%2Fbuild-application&hl=ja)
    
*   [Install](https://docs.cloud.google.com/sdk/docs/install?hl=ja) the Google Cloud CLI.
    
*   外部 ID プロバイダ（IdP）を使用している場合は、まず[連携 ID を使用して gcloud CLI にログインする](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud?hl=ja)必要があります。
    
*   gcloud CLI を[初期化](https://docs.cloud.google.com/sdk/docs/initializing?hl=ja)するには、次のコマンドを実行します。
    
    gcloud init
    

*   In the Google Cloud console, on the project selector page, select or create a Google Cloud project.
    
    **Roles required to select or create a project**
    
    *   **Select a project**: Selecting a project doesn't require a specific IAM role—you can select any project that you've been granted a role on.
    *   **Create a project**: To create a project, you need the Project Creator role (`roles/resourcemanager.projectCreator`), which contains the `resourcemanager.projects.create` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ja).
    
    [Go to project selector](https://console.cloud.google.com/projectselector2/home/dashboard?hl=ja)
    
*   [Verify that billing is enabled for your Google Cloud project](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled?hl=ja#confirm_billing_is_enabled_on_a_project).
    
*   Enable the Cloud Build and Artifact Registry APIs.
    
    **Roles required to enable APIs**
    
    To enable APIs, you need the Service Usage Admin IAM role (`roles/serviceusage.serviceUsageAdmin`), which contains the `serviceusage.services.enable` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ja).
    
    [Enable the APIs](https://console.cloud.google.com/flows/enableapi?apiid=cloudbuild.googleapis.com%2C+artifactregistry.googleapis.com&%3Bredirect=https%3A%2F%2Fcloud.google.com%2Fdocs%2Fbuildpacks%2Fbuild-application&hl=ja)
    
*   [Install](https://docs.cloud.google.com/sdk/docs/install?hl=ja) the Google Cloud CLI.
    
*   外部 ID プロバイダ（IdP）を使用している場合は、まず[連携 ID を使用して gcloud CLI にログインする](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud?hl=ja)必要があります。
    
*   gcloud CLI を[初期化](https://docs.cloud.google.com/sdk/docs/initializing?hl=ja)するには、次のコマンドを実行します。
    
    gcloud init
    

2.  使用する Google Cloud プロジェクトがコンテナ イメージ リポジトリにアクセスできることを確認します。
    
    [Artifact Registry の Docker リポジトリ](https://docs.cloud.google.com/artifact-registry/docs/docker/store-docker-container-images?hl=ja)へのアクセスを構成する手順は次のとおりです。
    
    1.  使用する Google Cloud プロジェクトと同じ場所に Docker リポジトリを新規作成します。
        
        gcloud artifacts repositories create REPO\_NAME \\
        --repository-format\=docker \\
        --location\=REGION \--description\="DESCRIPTION"
        
        次のように置き換えます。
        
        *   `REPO_NAME` は、Docker リポジトリに選択した名前に置き換えます。
        *   `REGION` は、使用する Google Cloud プロジェクトの場所、またはそれに最も近い[場所](https://docs.cloud.google.com/artifact-registry/docs/repositories/repo-locations?hl=ja)に置き換えます。
        *   `DESCRIPTION` は選択した説明に置き換えます。
        
        たとえば、「Docker repository」という説明を使用して `us-west2` に `docker` リポジトリを作成するには、次のコマンドを実行します。
        
        gcloud artifacts repositories create buildpacks-docker-repo \--repository-format\=docker \\
        --location\=us-west2 \--description\="Docker repository"
        
    2.  リポジトリが作成されたことを確認します。
        
        gcloud artifacts repositories list
        
        Docker リポジトリに選択した名前がリストに表示されます。
        

### アプリケーションをリモートでビルドする

[`gcloud builds submit`](https://docs.cloud.google.com/sdk/gcloud/reference/builds/submit?hl=ja) コマンドを使用して、コンテナ イメージをビルドしてリポジトリにアップロードします。

コマンドにコンテナ イメージを指定することも、構成ファイルを使用することもできます。

#### コマンドでビルドする

構成ファイルなしでビルドするには、`image` フラグを指定します。

gcloud builds submit \--pack image\=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/IMAGE\_NAME

次のように置き換えます。

*   `LOCATION` は、コンテナ リポジトリのリージョン名に置き換えます。例: `us-west2`
*   `PROJECT_ID` は、使用する Google Cloud プロジェクトの ID に置き換えます。
*   `REPO_NAME` は Docker リポジトリの名前に置き換えます。
*   `IMAGE_NAME` は、コンテナ イメージの名前に置き換えます。

例:

`gcloud builds submit --pack image=us-west2-docker.pkg.dev/my-project-id/my-buildpacks-docker-repo/app-image`

#### 構成ファイルを使用してビルドする

[構成ファイル](https://docs.cloud.google.com/build/docs/configuring-builds/create-basic-configuration?hl=ja)を使用すると、ビルドコマンドだけでイメージ リポジトリの構成の詳細を定義できます。構成ファイルは YAML ファイル形式にして、`pack` CLI を使用するビルドステップを含める必要があります。

1.  コンテナ イメージ リポジトリの URI を含む YAML ファイル（ファイル名: `cloudbuild.yaml`）を作成します。

  options:
    logging: CLOUD\_LOGGING\_ONLY
    pool: {}
  projectId: PROJECT\_ID
  steps:
  \- name: gcr.io/k8s-skaffold/pack
    entrypoint: pack
    args:
    \- build
    \- LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/IMAGE\_NAME
    \- \--builder
    \- gcr.io/buildpacks/builder:latest
    \- \--network
    \- cloudbuild
  images:
  \- LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/IMAGE\_NAME

次のように置き換えます。

*   `LOCATION` は、コンテナ リポジトリのリージョン名（`us-west2` など）に置き換えます。
*   `PROJECT_ID` は、使用する Google Cloud プロジェクトの ID に置き換えます。
*   `REPO_NAME` は Docker リポジトリの名前に置き換えます。
*   `IMAGE_NAME` は、コンテナ イメージの名前に置き換えます。

1.  アプリケーションをビルドします。
    
    構成ファイルの名前が `cloudbuild.yaml` の場合は、次のコマンドを実行します。
    
    gcloud builds submit .
    

### 例: サンプル アプリケーションをリモートでビルドする

次の例は、サンプルをリモートで作成し、コンテナ イメージが Artifact Registry のリポジトリに push されたことを確認する方法を示しています。

1.  ローカルマシンにサンプル リポジトリのクローンを作成します。
    
    git clone https://github.com/GoogleCloudPlatform/buildpack-samples.git
    
2.  アプリケーションのサンプルコードが含まれているディレクトリに移動します。
    
    ### Go
    
    cd buildpack-samples/sample-go
    
    ### Java
    
    cd buildpack-samples/sample-java-gradle
    
    ### Node.js
    
    cd buildpack-samples/sample-node
    
    ### PHP
    
    cd buildpack-samples/sample-php
    
    ### Python
    
    cd buildpack-samples/sample-python
    
    ### Ruby
    
    cd buildpack-samples/sample-ruby
    
    ### .NET
    
    cd buildpack-samples/sample-dotnet
    
3.  `gcloud` を使用して、アプリケーションのソースコードを Cloud Build に送信します。
    
    ### Go
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-go
    
    ### Java
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-java-gradle
    
    ### Node.js
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-node
    
    ### PHP
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-php
    
    ### Python
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-python
    
    ### Ruby
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-ruby
    
    ### .NET
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/sample-dotnet
    
4.  サンプル アプリケーションが `REPO_NAME` に正常に公開されたことを確認します。
    
    gcloud artifacts docker images list LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME
    
    次のように置き換えます。
    
    *   `LOCATION` は、コンテナ リポジトリのリージョン名に置き換えます。例: `us-west2`
    *   `PROJECT_ID` は、使用する Google Cloud プロジェクトの ID に置き換えます。
    *   `REPO_NAME` は Docker リポジトリの名前に置き換えます。

次のステップ
------

*   イメージを [Cloud Run](https://docs.cloud.google.com/run/docs/deploying?hl=ja) にデプロイする。
*   [環境変数を設定する](https://docs.cloud.google.com/docs/buildpacks/set-environment-variables?hl=ja)。
*   [ビルドイメージを構成する](https://docs.cloud.google.com/docs/buildpacks/build-run-image?hl=ja)。
*   [キャッシュ イメージを使用してビルドを高速化する](https://docs.cloud.google.com/docs/buildpacks/cache-images?hl=ja)。
