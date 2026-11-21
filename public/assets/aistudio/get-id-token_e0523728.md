# Get an ID token &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/get-id-token

This page describes some ways to acquire a Google-signed OpenID Connect (OIDC) ID token.

You need a Google-signed ID token for the following authentication use cases:

*   [Accessing a Cloud Run service](https://docs.cloud.google.com/run/docs/authenticating/service-to-service)
*   [Invoking a Cloud Run function](https://docs.cloud.google.com/functions/docs/securing/authenticating)
*   [Authenticating a user to an application secured by Identity-Aware Proxy (IAP)](https://docs.cloud.google.com/iap/docs/authentication-howto)
*   [Making a request to an API deployed with API Gateway or Cloud Endpoints](https://docs.cloud.google.com/api-gateway/docs/authentication-method)

For information about ID token contents and lifetimes, see [ID tokens](https://docs.cloud.google.com/docs/authentication/token-types#identity-tokens).

ID tokens have a specific service or application that they can be used for, specified by the value of their `aud` claim. This document uses the term _target service_ to refer to the service or application that the ID token can be used to authenticate to.

When you get the ID token, you can include it in an `Authorization` header in the request to the target service.

Methods for getting an ID token
-------------------------------

There are various ways to get an ID token. This page describes the following methods:

*   [Get an ID token from the metadata server](#metadata-server)
*   [Use a connecting service to generate an ID token](#connecting-service)
*   [Generate an ID token by impersonating a service account](#impersonation)
*   [Generate a generic ID token for development with Cloud Run and Cloud Run functions](#generic-dev)

If you need an ID token to be accepted by an application not hosted on Google Cloud, you can probably use these methods. However, you should determine what ID token claims the application requires.

### Get an ID token from the metadata server

When your code is running on a resource that can have a [service account attached to it](https://docs.cloud.google.com/iam/docs/attach-service-accounts#attaching-to-resources), the metadata server for the associated service can usually provide an ID token. The metadata server generates ID tokens for the attached service account. You cannot get an ID token based on user credentials from the metadata server.

You can get an ID token from the metadata server when your code is running on the following Google Cloud services:

*   [Compute Engine](https://docs.cloud.google.com/compute/docs/instances/verifying-instance-identity#request_signature)
*   [App Engine standard environment](https://docs.cloud.google.com/appengine/docs/standard/python3/runtime#metadata_server)
*   [App Engine flexible environment](https://docs.cloud.google.com/appengine/docs/flexible/python/runtime#metadata_server)
*   [Cloud Run functions](https://docs.cloud.google.com/functions/docs/securing/function-identity#metadata-server)
*   [Cloud Run](https://docs.cloud.google.com/run/docs/container-contract#metadata-server)
*   [Google Kubernetes Engine](https://docs.cloud.google.com/kubernetes-engine/docs/concepts/workload-identity#instance_metadata)
*   [Cloud Build](https://docs.cloud.google.com/build/docs/securing-builds/authorize-service-to-service-access)

To retrieve an ID token from the metadata server, you query the identity endpoint for the service account, as shown in this example.

### curl

Replace `AUDIENCE` with the URI for the target service, for example `http://www.example.com`.

curl -H "Metadata-Flavor: Google" \\
  'http://metadata/computeMetadata/v1/instance/service-accounts/default/identity?audience=AUDIENCE'

### PowerShell

Replace `AUDIENCE` with the URI for the target service, for example `http://www.example.com`.

$value = (Invoke-RestMethod \`
  -Headers @{'Metadata-Flavor' = 'Google'} \`
  -Uri "http://metadata/computeMetadata/v1/instance/service-accounts/default/identity?audience=AUDIENCE")
$value

### Java

To run this code sample, you must install the [Auth Client Library for Java](https://github.com/googleapis/google-auth-library-java).

### Go

### Node.js

To run this code sample, you must install the [Google Auth Library for Node.js](https://github.com/googleapis/google-auth-library-nodejs)

### Python

To run this code sample, you must install the [Google Auth Python Library](https://github.com/googleapis/google-cloud-python/tree/main/packages/google-auth).

### Ruby

To run this code sample, you must install the [Google Auth Library for Ruby](https://github.com/googleapis/google-auth-library-ruby).

### Use a connecting service to generate an ID token

Some Google Cloud services help you call other services. These connecting services might help determine when the call gets made, or manage a workflow that includes calling the service. The following services can automatically include an ID token, with the appropriate value for the `aud` claim, when they initiate a call to a service that requires an ID token:

Cloud Scheduler

Cloud Scheduler is a fully managed enterprise-grade cron job scheduler. You can configure Cloud Scheduler to include either an ID token or an access token when it invokes another service. For more information, see [Using authentication with HTTP Targets](https://docs.cloud.google.com/scheduler/docs/http-target-auth).

Cloud Tasks

Cloud Tasks lets you manage the execution of distributed tasks. You can configure a task to include either an ID token or an access token when it calls a service. For more information, see [Using HTTP Target tasks with authentication tokens](https://docs.cloud.google.com/tasks/docs/creating-http-target-tasks#token).

Pub/Sub

Pub/Sub enables asynchronous communication between services. You can configure Pub/Sub to include an ID token with a message. For more information, see [Authentication for push subscription](https://docs.cloud.google.com/pubsub/docs/push#authentication).

Workflows

Workflows is a fully managed orchestration platform that executes services in an order that you define: a _workflow_. You can define a workflow to include either an ID token or an access token when it invokes another service. For more information, see [Make authenticated requests from a workflow](https://docs.cloud.google.com/workflows/docs/authenticate-from-workflow).

### Generate an ID token by impersonating a service account

Service account impersonation allows a principal to generate short-lived credentials for a trusted service account. The principal can then use these credentials to authenticate as the service account.

Before a principal can impersonate a service account, it must have an IAM role on that service account that enables impersonation. If the principal is itself another service account, it might seem easier to simply provide the required permissions directly to that service account, and enable it to impersonate itself. This configuration, known as self-impersonation, creates a security vulnerability, because it lets the service account create an access token that can be refreshed in perpetuity.

Service account impersonation should always involve two principals: a principal that represents the caller, and the service account that is being impersonated, called the privilege-bearing service account.

To generate an ID token by impersonating a service account, you use the following general process.

For step-by-step instructions, see [Create an ID token](https://docs.cloud.google.com/iam/docs/create-short-lived-credentials-direct#sa-credentials-oidc).

1.  Identify or [create a service account](https://docs.cloud.google.com/iam/docs/service-accounts-create#creating) to be the privilege-bearing service account.
    
2.  Identify the required roles to invoke the target service. Grant these roles to the service account on the target service:
    
    *   For [Cloud Run services](https://docs.cloud.google.com/run/docs/authenticating/service-to-service#set-up-sa), grant the Cloud Run Invoker role (`roles/run.invoker`).
    *   For [Cloud Run functions](https://docs.cloud.google.com/functions/docs/securing/authenticating), grant the Cloud Functions Invoker role (`roles/cloudfunctions.invoker`).
    *   For other target services, see the product documentation for the service.
3.  Identify the principal that will perform the impersonation, and set up [Application Default Credentials (ADC)](https://docs.cloud.google.com/docs/authentication/provide-credentials-adc) to use the credentials for this principal.
    
    For development environments, the principal is usually the user account you provided to ADC by using the gcloud CLI. However, if you're running on a resource with a service account attached, the attached service account is the principal.
    
4.  Grant the principal the Service Account OpenID Connect Identity Token Creator role (`roles/iam.serviceAccountOpenIdTokenCreator`).
    
5.  Use the [IAM Credentials API](https://docs.cloud.google.com/iam/docs/reference/credentials/rest) to generate the ID token for the authorized service account.
    
    Replace the following:
    
    *   AUDIENCE: The URI for the target service—for example, `http://www.example.com`.
    *   SERVICE\_ACCOUNT\_EMAIL: The email address of the privilege-bearing service account.
    
    curl -X POST \
        -H "Authorization: Bearer $(gcloud auth print-access-token)" \
        -H "Content-Type: application/json" \
        -d '{"audience": "AUDIENCE", "includeEmail": "true"}' \
        https://iamcredentials.googleapis.com/v1/projects/-/serviceAccounts/SERVICE_ACCOUNT_EMAIL:generateIdToken
    

### Generate a generic ID token for development with Cloud Run and Cloud Run functions

You can use the gcloud CLI to get an ID token for your user credentials that can be used with any Cloud Run service or Cloud Run function that the caller has the required IAM permissions to invoke. This token will not work for any other application.

*   To generate a generic ID token, you use the [`gcloud auth print-identity-token` command](https://docs.cloud.google.com/sdk/gcloud/reference/auth/print-identity-token):
    
    gcloud auth print-identity-token
    

What's next
-----------

*   Understand [ID tokens](https://docs.cloud.google.com/docs/authentication/token-types#identity-tokens).
*   Use shell commands to [query the Compute Engine metadata server](https://docs.cloud.google.com/compute/docs/metadata/querying-metadata).
*   Learn more about [authentication methods](https://docs.cloud.google.com/docs/authentication).
