# Use API keys to access APIs &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/api-keys-use

This page describes how to use API keys to access Google Cloud APIs and services that accept API keys.

Not all Google Cloud APIs accept API keys to authorize usage. Review the documentation for the service or API that you want to use to determine whether it accepts API keys.

For information about creating and managing API keys, including restricting API keys, see [Manage API keys](https://docs.cloud.google.com/docs/authentication/api-keys).

For information about using API keys with Google Maps Platform, see the [Google Maps Platform documentation](https://developers.google.com/maps/api-key-best-practices). For more information about the API Keys API, see the [API Keys API documentation](https://docs.cloud.google.com/api-keys/docs/overview).

Before you begin
----------------

Select the tab for how you plan to use the samples on this page:

### C#

To use the .NET samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

1.  [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
2.  If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
3.  If you're using a local shell, then create local authentication credentials for your user account:
    
    gcloud auth application-default login
    
    You don't need to do this if you're using Cloud Shell.
    
    If an authentication error is returned, and you are using an external identity provider (IdP), confirm that you have [signed in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    

For more information, see [Set up ADC for a local development environment](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) in the Google Cloud authentication documentation.

### C++

To use the C++ samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

1.  [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
2.  If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
3.  If you're using a local shell, then create local authentication credentials for your user account:
    
    gcloud auth application-default login
    
    You don't need to do this if you're using Cloud Shell.
    
    If an authentication error is returned, and you are using an external identity provider (IdP), confirm that you have [signed in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    

For more information, see [Set up ADC for a local development environment](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) in the Google Cloud authentication documentation.

### Go

To use the Go samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

1.  [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
2.  If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
3.  If you're using a local shell, then create local authentication credentials for your user account:
    
    gcloud auth application-default login
    
    You don't need to do this if you're using Cloud Shell.
    
    If an authentication error is returned, and you are using an external identity provider (IdP), confirm that you have [signed in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    

For more information, see [Set up ADC for a local development environment](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) in the Google Cloud authentication documentation.

### Node.js

To use the Node.js samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

1.  [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
2.  If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
3.  If you're using a local shell, then create local authentication credentials for your user account:
    
    gcloud auth application-default login
    
    You don't need to do this if you're using Cloud Shell.
    
    If an authentication error is returned, and you are using an external identity provider (IdP), confirm that you have [signed in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    

For more information, see [Set up ADC for a local development environment](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) in the Google Cloud authentication documentation.

### Python

To use the Python samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

1.  [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
2.  If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
3.  If you're using a local shell, then create local authentication credentials for your user account:
    
    gcloud auth application-default login
    
    You don't need to do this if you're using Cloud Shell.
    
    If an authentication error is returned, and you are using an external identity provider (IdP), confirm that you have [signed in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    

For more information, see [Set up ADC for a local development environment](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) in the Google Cloud authentication documentation.

### REST

To use the REST API samples on this page in a local development environment, you use the credentials you provide to the gcloud CLI.

[Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.

If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).

For more information, see [Authenticate for using REST](https://docs.cloud.google.com/docs/authentication/rest) in the Google Cloud authentication documentation.

Using an API key with REST
--------------------------

To include an API key with a REST API call, use the `x-goog-api-key` HTTP header, as shown in the following example:

curl -X POST \\
    -H "X-goog-api-key: API\_KEY" \\
    -H "Content-Type: application/json; charset=utf-8" \\
    -d @request.json \\
    "https://translation.googleapis.com/language/translate/v2"

If you can't use the HTTP header, you can use the `key` query parameter. However, this method includes your API key in the URL, exposing your key to theft through URL scans.

The following example shows how to use the `key` query parameter with a Cloud Natural Language API request for [`documents.analyzeEntities`](https://docs.cloud.google.com/natural-language/docs/reference/rest/v1/documents/analyzeEntities). Replace `API_KEY` with the key string of your API key.

POST https://language.googleapis.com/v1/documents:analyzeEntities?key=API\_KEY

Using an API key with client libraries
--------------------------------------

This example uses the Cloud Natural Language API, which accepts API keys, to demonstrate how you would provide an API key to the library.

When you use API keys in your applications, ensure that they are kept secure during both storage and transmission. Publicly exposing your API keys can lead to unexpected charges on your account. For more information, see [Best practices for managing API keys](https://docs.cloud.google.com/docs/authentication/api-keys-best-practices).

What's next
-----------

*   See an overview of [authentication methods](https://docs.cloud.google.com/docs/authentication).
*   Learn more about the [API Keys API](https://docs.cloud.google.com/api-keys/docs/overview).
