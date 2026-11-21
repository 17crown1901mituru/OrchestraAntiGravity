# Set environment variables to configure builds &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/set-environment-variables

[Skip to main content](#main-content)

*   [Documentation](https://docs.cloud.google.com/docs)
    *   [Overview](https://docs.cloud.google.com/docs/buildpacks)
    *   [Guides](https://docs.cloud.google.com/docs/buildpacks/overview)
    *   [Resources](https://docs.cloud.google.com/docs/buildpacks/release-notes)
*   [Console](https://console.cloud.google.com/)

*   Discover
    
*   [Product overview](https://docs.cloud.google.com/docs/buildpacks/overview)
*   [Stacks](https://docs.cloud.google.com/docs/buildpacks/stacks)
*   [Builders](https://docs.cloud.google.com/docs/buildpacks/builders)
*   [Base images](https://docs.cloud.google.com/docs/buildpacks/base-images)
*   Get started
    
*   [Build an application](https://docs.cloud.google.com/docs/buildpacks/build-application)
*   [Build a function](https://docs.cloud.google.com/docs/buildpacks/build-function)
*   Customize container images
    
*   [Set environment variables](https://docs.cloud.google.com/docs/buildpacks/set-environment-variables)
*   [Configure Cloud Run and Cloud Function services](https://docs.cloud.google.com/docs/buildpacks/service-specific-configs)
*   [Use a specific builder](https://docs.cloud.google.com/docs/buildpacks/use-a-specific-builder)
*   [Configure build and run images](https://docs.cloud.google.com/docs/buildpacks/build-run-image)
*   [Speed up builds with cache images](https://docs.cloud.google.com/docs/buildpacks/cache-images)
*   [About Procfiles](https://docs.cloud.google.com/docs/buildpacks/about-procfile)

Set environment variables to configure builds
---------------------------------------------

You can configure environment variables that are set during the build of your container image.

You can also customize your container image by [extending the build and run images](https://docs.cloud.google.com/docs/buildpacks/build-run-image).

Local builds
------------

For local builds, you must have the Pack CLI and Docker installed.

### Before you begin

1.  Install [Docker Community Edition (CE)](https://docs.docker.com/engine/installation/) on your workstation. Docker is used by `pack` as an OCI image builder.
2.  Install [Pack CLI](https://buildpacks.io/docs/tools/pack/).
3.  Install the [Git source control](https://git-scm.com/downloads) tool to fetch the sample application from GitHub.

### Set environment variables

To set environment variables for local builds, you append the `--env` flag to the `pack` command for each environment variable.

You can use one or more of the [environment variables that are supported by your runtime](https://docs.cloud.google.com/docs/buildpacks/service-specific-configs).

pack build SERVICE_NAME \
        --env ENVIRONMENT_VARIABLE

Replace:

*   `SERVICE_NAME` with the name of the service for your application or function.
*   `ENVIRONMENT_VARIABLE` with the environment variable that you want to set during build time.

Example

To set the `GOOGLE_ENTRYPOINT="gunicorn -p :8080 main:app"` environment variable in your container image, you run the following command:

pack build my-app \
        --builder gcr.io/buildpacks/builder:v1 \
        --env GOOGLE_ENTRYPOINT="gunicorn -p :8080 main:app"
        --env MY-LOCAL-ENV-VARIABLE

Remote builds
-------------

To set environment variables for remote builds, you use the [`project.toml` project descriptor](https://buildpacks.io/docs/app-developer-guide/using-project-descriptor/). The `project.toml` project descriptor is used by Cloud Build when your container image is built.

### Before you begin

*   Sign in to your Google Cloud account. If you're new to Google Cloud, [create an account](https://console.cloud.google.com/freetrial) to evaluate how our products perform in real-world scenarios. New customers also get $300 in free credits to run, test, and deploy workloads.
*   In the Google Cloud console, on the project selector page, select or create a Google Cloud project.
    
    **Roles required to select or create a project**
    
    *   **Select a project**: Selecting a project doesn't require a specific IAM role—you can select any project that you've been granted a role on.
    *   **Create a project**: To create a project, you need the Project Creator role (`roles/resourcemanager.projectCreator`), which contains the `resourcemanager.projects.create` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access).
    
    [Go to project selector](https://console.cloud.google.com/projectselector2/home/dashboard)
    
*   [Verify that billing is enabled for your Google Cloud project](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled#confirm_billing_is_enabled_on_a_project).
    
*   Enable the Cloud Build and Artifact Registry APIs, if any are not already enabled.
    
    **Roles required to enable APIs**
    
    To enable APIs, you need the `serviceusage.services.enable` permission. If you created the project, then you likely already have this permission through the Owner role (`roles/owner`). Otherwise, you can get this permission through the Service Usage Admin role (`roles/serviceusage.serviceUsageAdmin`). [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access).
    
    [Enable the APIs](https://console.cloud.google.com/apis/enableflow?apiid=cloudbuild.googleapis.com,artifactregistry.googleapis.com&redirect=https%3A//cloud.google.com/docs/buildpacks/build-application)
    
*   [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
*   If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
*   To [initialize](https://docs.cloud.google.com/sdk/docs/initializing) the gcloud CLI, run the following command:
    
    gcloud init
    

*   In the Google Cloud console, on the project selector page, select or create a Google Cloud project.
    
    **Roles required to select or create a project**
    
    *   **Select a project**: Selecting a project doesn't require a specific IAM role—you can select any project that you've been granted a role on.
    *   **Create a project**: To create a project, you need the Project Creator role (`roles/resourcemanager.projectCreator`), which contains the `resourcemanager.projects.create` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access).
    
    [Go to project selector](https://console.cloud.google.com/projectselector2/home/dashboard)
    
*   [Verify that billing is enabled for your Google Cloud project](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled#confirm_billing_is_enabled_on_a_project).
    
*   Enable the Cloud Build and Artifact Registry APIs, if any are not already enabled.
    
    **Roles required to enable APIs**
    
    To enable APIs, you need the `serviceusage.services.enable` permission. If you created the project, then you likely already have this permission through the Owner role (`roles/owner`). Otherwise, you can get this permission through the Service Usage Admin role (`roles/serviceusage.serviceUsageAdmin`). [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access).
    
    [Enable the APIs](https://console.cloud.google.com/apis/enableflow?apiid=cloudbuild.googleapis.com,artifactregistry.googleapis.com&redirect=https%3A//cloud.google.com/docs/buildpacks/build-application)
    
*   [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
*   If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
*   To [initialize](https://docs.cloud.google.com/sdk/docs/initializing) the gcloud CLI, run the following command:
    
    gcloud init
    

2.  Ensure that your Google Cloud project has access to a container image repository.
    
    To configure access to a [Docker repository in Artifact Registry](https://docs.cloud.google.com/artifact-registry/docs/docker/store-docker-container-images):
    
    1.  Create a new Docker repository in the same location of your Google Cloud project.
        
        gcloud artifacts repositories create REPO\_NAME \\
        --repository-format\=docker \\
        --location\=REGION \--description\="DESCRIPTION"
        
        Replace:
        
        *   `REPO_NAME` with the name that you choose for your Docker repository.
        *   `REGION` with the [location](https://docs.cloud.google.com/artifact-registry/docs/repositories/repo-locations) in or nearest to the location of your Google Cloud project.
        *   `DESCRIPTION` with a description of your choice.
        
        For example, to create a `docker` repository in `us-west2` with the description "Docker repository", you run:
        
        gcloud artifacts repositories create buildpacks-docker-repo \--repository-format\=docker \\
        --location\=us-west2 \--description\="Docker repository"
        
    2.  Verify that your repository was created:
        
        gcloud artifacts repositories list
        
        You should see name that you choose for your Docker repository in the list.
        

### Optional: Download a sample application

1.  Clone the sample repository to your local machine:
    
    git clone https://github.com/GoogleCloudPlatform/buildpack-samples.git
    
2.  Change to the directory that contains the application sample code:
    
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
    

### Build the application with environment variables

1.  In the root directory of your service, create or update your `project.toml` project descriptor to include the `[[build.env]]` section and any of the [supported environment variable](https://docs.cloud.google.com/docs/buildpacks/service-specific-configs):
    
    \[\[build.env\]\]
        name \= "ENVIRONMENT\_VARIABLE\_NAME"
        value \= "ENVIRONMENT\_VARIABLE\_VALUE"
    
    Replace:
    
    *   `ENVIRONMENT_VARIABLE_NAME` with the name of a [supported environment variable](https://docs.cloud.google.com/docs/buildpacks/service-specific-configs).
    *   `ENVIRONMENT_VARIABLE_VALUE` with the corresponding value for the specified environment variable.
    
    **Example**
    
    \[\[build.env\]\]
        name \= "GOOGLE\_ENTRYPOINT"
        value \= "gunicorn -p :8080 main:app"
    
2.  Use `gcloud` to submit the application source code to Cloud Build:
    
    gcloud builds submit --pack image=LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/IMAGE\_NAME
    
    Replace:
    
    *   `LOCATION` with the region name of your container repository. Example: `**us-west2**-docker.pkg.dev`
    *   `PROJECT_ID` with the ID of your Google Cloud project.
    *   `REPO_NAME` with the name of your Docker repository.
    *   `IMAGE_NAME` with the name of your container image.
    
    **Examples**: If you downloaded a sample application, you run the corresponding command:
    
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
    
3.  Verify that the sample application was successfully published to `REPO_NAME`:
    
    gcloud artifacts docker images list LOCATION\-docker.pkg.dev/project-id/REPO\_NAME
    
    Replace:
    
    *   `LOCATION` with the region name of your container repository. Example: `**us-west2**-docker.pkg.dev`
    *   `PROJECT_ID` with the ID of your Google Cloud project.
    *   `REPO_NAME` with the name of your Docker repository.

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
