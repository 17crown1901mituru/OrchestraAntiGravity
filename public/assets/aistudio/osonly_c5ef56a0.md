# Configure the OS only runtime &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/osonly

The OS only runtime is a language-independent, general-purpose runtime for Cloud Run [source](https://docs.cloud.google.com/run/docs/deploying-source-code) deployments. It includes the base Ubuntu operating system (OS) without any additional language-specific runtime components. The OS only runtime lets you deploy Go applications from source, and binaries such as Dart and Go. You can also deploy your own application images that you built from scratch using this runtime.

The OS only runtime enables [automatic base image updates for Cloud Run](https://docs.cloud.google.com/run/docs/configuring/services/automatic-base-image-updates). This means your application automatically receives operating system-level security patches and updates, even when you don't use a specific [supported language runtime](https://docs.cloud.google.com/run/docs/runtime-support#support_schedule).

Supported stack
---------------

The OS only runtime is available on the [`google-24`](http://us-central1-docker.pkg.dev/serverless-runtimes/google-24-full/run%2Funiversal) stack. For more information about the available OS only runtime versions, and the support schedule, see the [Runtime lifecycle](https://docs.cloud.google.com/docs/buildpacks/runtime-support#os-only).

Compiled binaries
-----------------

The OS only runtime lets you deploy pre-compiled binaries. You can also target a base image using a runtime alias. For example, to use the [`google-24/osonly24`](http://us-central1-docker.pkg.dev/serverless-runtimes/google-24/runtimes/osonly24) base image, specify the alias, `osonly24`.

For compiled languages, make sure you build the binary targeting a Linux OS before you deploy. For example, to compile a Go application targeting `linux/amd64`, run the following command:

GOOS="linux" GOARCH=amd64 go build main.go

Deploy to Cloud Run
-------------------

Deploy your binary to Cloud Run by running the following the [`gcloud run deploy`](https://docs.cloud.google.com/sdk/gcloud/reference/run/deploy) command, and specify the base image (for example, `osonly24`):

gcloud beta run deploy SERVICE \
    --source . \
    --no-build \
    --base-image=osonly24 \
    --project PROJECT_ID \
    --allow-unauthenticated \
    --command COMMAND

Replace the following:

*   SERVICE: the name of your Cloud Run service.
*   PROJECT\_ID: the Google Cloud project ID.
*   COMMAND: the command that the container starts up with, for example, `./main`.

For more information on deploying from source without build, see [Deploy services from source code](https://docs.cloud.google.com/run/docs/deploying-source-code#deploy_from_source_without_build).
