# Set up ADC for a cloud-based development environment &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/set-up-adc-cloud-dev-environment

Set up ADC for a cloud-based development environment
----------------------------------------------------

When you use a Google Cloud cloud-based development environment such as Cloud Shell or Cloud Code, the tool uses the credentials you provided when you signed in, and manages any authorizations required. You cannot use the gcloud CLI to configure ADC in these environments. If you need to use a different user account to configure ADC, or configure ADC using a service account, use a local development environment or a Google Cloud compute resource as your development environment.

In Cloud Workstations, you can provide credentials to ADC in one of the following ways:

*   **Sign in using the [Workstations UI](https://docs.cloud.google.com/workstations/docs/authentication#ui-sign-in):** When you sign in to your workstation through the Google Cloud console, Cloud Workstations makes your user credentials available through the workstation's [metadata server](https://docs.cloud.google.com/compute/docs/metadata/overview#metadata_server_endpoints). Cloud Client Libraries, the gcloud CLI, and other tools that support ADC automatically retrieve these credentials from the metadata server without requiring a local credential file or the `GOOGLE_APPLICATION_CREDENTIALS` environment variable.
    
    Because ADC checks `GOOGLE_APPLICATION_CREDENTIALS` and local credential files before querying the metadata server, ensure neither is set if you want ADC to use your workstation sign-in credentials.
    
*   **Authenticate manually:** Run `gcloud auth application-default login` in your workstation terminal to generate a [local ADC credential file](https://docs.cloud.google.com/docs/authentication/application-default-credentials#personal).
    

What's next
-----------

*   Get more information about [Cloud Workstations authentication](https://docs.cloud.google.com/workstations/docs/authentication).
*   Get more information about [Cloud Code](https://docs.cloud.google.com/code/docs).
*   Get more information about [Cloud Shell](https://docs.cloud.google.com/shell/docs).
*   Learn more about [how ADC finds credentials](https://docs.cloud.google.com/docs/authentication/application-default-credentials).
*   [Authenticate for using Cloud Client Libraries](https://docs.cloud.google.com/docs/authentication/client-libraries).
*   [Authenticate for using REST](https://docs.cloud.google.com/docs/authentication/rest).
*   Explore [authentication methods](https://docs.cloud.google.com/docs/authentication).

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-11 UTC.
