# Set up ADC for a containerized development environment &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/set-up-adc-containerized-environment

Set up ADC for a containerized development environment
------------------------------------------------------

Authentication for containerized applications running on Cloud Run, Google Kubernetes Engine, or GKE Enterprise is handled differently between local testing environments and Google Cloud environments.

### Test containerized applications locally

To test your containerized application on your local workstation, you can configure your container to authenticate with your [local ADC file](https://docs.cloud.google.com/docs/authentication/application-default-credentials#personal). For more information, see [Configure ADC with your Google Account](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment#google-idp).

To test your implementation, use a local Kubernetes implementation such as [`minikube` and the `gcp-auth` addon](https://minikube.sigs.k8s.io/docs/handbook/addons/gcp-auth/).

### Run containerized applications on Google Cloud

You set up authentication for Google Cloud containerized environments differently depending on the environment:

*   For Cloud Run, see [call Google Cloud APIs with the service identity](https://docs.cloud.google.com/run/docs/securing/service-identity#call-apis-with-service-identity).
*   For GKE, see [Access Google Cloud APIs from GKE workloads](https://docs.cloud.google.com/kubernetes-engine/docs/how-to/workload-identity).
*   For GKE Enterprise, see the [authentication overview](https://docs.cloud.google.com/anthos/multicluster-management/gateway) and [Use fleet Workload Identity Federation for GKE](https://docs.cloud.google.com/anthos/multicluster-management/fleets/workload-identity).
*   For Knative serving, see [Using Workload Identity Federation for GKE for Knative serving](https://docs.cloud.google.com/anthos/run/docs/securing/workload-identity).

What's next
-----------

*   Learn more about [how ADC finds credentials](https://docs.cloud.google.com/docs/authentication/application-default-credentials).
*   [Authenticate for using Cloud Client Libraries](https://docs.cloud.google.com/docs/authentication/client-libraries).
*   [Authenticate for using REST](https://docs.cloud.google.com/docs/authentication/rest).
*   Explore [authentication methods](https://docs.cloud.google.com/docs/authentication).

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
