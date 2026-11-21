# Manage API keys &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/api-keys

[Skip to main content](#main-content)

*   [Documentation](https://docs.cloud.google.com/docs)
    *   [Overview](https://docs.cloud.google.com/sdk/docs)
    *   [Guides](https://docs.cloud.google.com/sdk/docs/overview)
    *   [Reference](https://docs.cloud.google.com/sdk/docs/libraries-reference)
    *   [Resources](https://docs.cloud.google.com/sdk/docs/resources)
*   [Console](https://console.cloud.google.com/)

*   Discover
    
*   [Google Cloud SDK overview](https://docs.cloud.google.com/sdk/docs/overview)
*   [gcloud CLI overview](https://docs.cloud.google.com/sdk/gcloud)
*   [Cloud Client Libraries overview](https://docs.cloud.google.com/apis/docs/cloud-client-libraries)
*   [Client libraries and Cloud APIs explained](https://docs.cloud.google.com/apis/docs/client-libraries-explained)
*   Get started
    
*   [Install the gcloud CLI](https://docs.cloud.google.com/sdk/docs/install-sdk)

*   Google Cloud CLI Remote MCP Server
    
*   [Use the Google Cloud CLI remote MCP server](https://docs.cloud.google.com/sdk/use-gcloud-mcp)
*   Authenticate to Google Cloud
    
*   [Authentication methods at Google](https://docs.cloud.google.com/docs/authentication)

*   [Get an ID token](https://docs.cloud.google.com/docs/authentication/get-id-token)

*   [2-step verification requirement](https://docs.cloud.google.com/docs/authentication/mfa-requirement)
*   [Reauthentication](https://docs.cloud.google.com/docs/authentication/reauthentication)
*   [Authentication use cases](https://docs.cloud.google.com/docs/authentication/use-cases)
*   [Identity management products](https://docs.cloud.google.com/docs/authentication/identity-products)
*   Configure the gcloud CLI
    
*   [Initialize the gcloud CLI](https://docs.cloud.google.com/sdk/docs/initialize)
*   [Authenticate for the gcloud CLI](https://docs.cloud.google.com/sdk/docs/authenticate)
*   [Configure the gcloud CLI for use behind a proxy or firewall](https://docs.cloud.google.com/sdk/docs/proxy-settings)
*   [Manage gcloud CLI configurations](https://docs.cloud.google.com/sdk/docs/configurations)
*   [Manage gcloud CLI properties](https://docs.cloud.google.com/sdk/docs/properties)
*   [Enable accessibility features](https://docs.cloud.google.com/sdk/docs/enabling-accessibility-features)
*   Develop
    
*   [gcloud CLI cheat sheet](https://docs.cloud.google.com/sdk/docs/cheatsheet)
*   [Scripting gcloud CLI commands](https://docs.cloud.google.com/sdk/docs/scripting-gcloud)
*   [Client libraries best practices](https://docs.cloud.google.com/apis/docs/client-libraries-best-practices)
*   Manage packages in gcloud CLI
    
*   [Managing gcloud CLI components](https://docs.cloud.google.com/sdk/docs/components)
*   [Uninstalling the gcloud CLI](https://docs.cloud.google.com/sdk/docs/uninstall-cloud-sdk)

Manage API keys
---------------

This page describes how to create, edit, and restrict API keys. For information about how to use API keys to access Google APIs, see [Use API keys to access APIs](https://docs.cloud.google.com/docs/authentication/api-keys-use).

Introduction to API keys
------------------------

There are two types of API keys: standard API keys, and authorization keys. Both keys let you associate a request with a project for billing and quota purposes. However, they differ in the following way:

*   A standard API key doesn't authenticate a principal.
    
*   An authorization key authenticates as a service account. It operates in a similar fashion to a long-lived access token.
    

The [**Credentials** page](https://console.cloud.google.com/apis/credentials) in the Google Cloud console ensures that the correct type of API key is created for a selected API.

### Standard API keys

Standard API keys provide a way to associate a request with a project for billing and quota purposes. When you use a standard API key (an API key that has not been bound to a service account) to access an API, the API key doesn't identify a [principal](https://docs.cloud.google.com/docs/authentication#principal). Without a principal, the request can't use Identity and Access Management (IAM) to check whether the caller is authorized to perform the requested operation.

Standard API keys can be used with any API that accepts API keys, unless API restrictions have been added to the key. Standard API keys can't be used with services that don't accept API keys, including in [express mode](https://cloud.google.com/resources/cloud-express-faqs).

### Authorization keys

Authorization keys are API keys that are bound to a service account. When you use an authorization key to access an API, your request is processed as if you used the bound service account to make the request.

APIs that support authorization keys include [AI Platform](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/start/api-keys?usertype=standard) (`aiplatform.googleapis.com`) and the [Gemini API](https://ai.google.dev/api) (`generativelanguage.googleapis.com`).

When using authorization keys, keep the following in mind:

*   Requests authenticated by authorization keys aren't recorded in [service account usage metrics](https://docs.cloud.google.com/iam/docs/service-account-monitoring).
    
*   Binding keys to a service account is prevented by a default organization policy constraint. To change this, see [Enable authorization keys](#enable-key-binding).
    

### API key components

An API key has the following components, which let you manage and use the key:

String

The API key string is an encrypted string, for example, `AIzaSyDaGmWKa4JsXZ-HjGw7ISLn_3namBGewQe`. When you use an API key to access an API, you always use the key's string. API keys don't have an associated JSON file.

ID

The API key ID is used by Google Cloud administrative tools to uniquely identify the key. The key ID can't be used to access APIs. The key ID can be found in the URL of the key's edit page in the Google Cloud console. You can also get the key ID by using the Google Cloud CLI to list the keys in your project.

Display name

The display name is an optional, descriptive name for the key, which you can set when you create or update the key.

Bound service account

Authorization keys include the service account's email address.

Before you begin
----------------

Complete the following tasks to use the samples on this page.

### Set up authentication

Select the tab for how you plan to use the samples on this page:

### Console

When you use the Google Cloud console to access Google Cloud services and APIs, you don't need to set up authentication.

### gcloud

In the Google Cloud console, activate Cloud Shell.

[Activate Cloud Shell](https://console.cloud.google.com/?cloudshell=true)

At the bottom of the Google Cloud console, a [Cloud Shell](https://docs.cloud.google.com/shell/docs/how-cloud-shell-works) session starts and displays a command-line prompt. Cloud Shell is a shell environment with the Google Cloud CLI already installed and with values already set for your current project. It can take a few seconds for the session to initialize.

### C++

To use the C++ samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

1.  [Install](https://docs.cloud.google.com/sdk/docs/install) the Google Cloud CLI.
    
2.  If you're using an external identity provider (IdP), you must first [sign in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    
3.  If you're using a local shell, then create local authentication credentials for your user account:
    
    gcloud auth application-default login
    
    You don't need to do this if you're using Cloud Shell.
    
    If an authentication error is returned, and you are using an external identity provider (IdP), confirm that you have [signed in to the gcloud CLI with your federated identity](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud).
    

For more information, see [Set up ADC for a local development environment](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) in the Google Cloud authentication documentation.

### Java

To use the Java samples on this page in a local development environment, install and initialize the gcloud CLI, and then set up Application Default Credentials with your user credentials.

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

### Required roles

To get the permissions that you need to manage API keys, ask your administrator to grant you the following IAM roles on your project:

*   Create API keys:
    *   [API Keys Admin](https://docs.cloud.google.com/iam/docs/roles-permissions/serviceusage#serviceusage.apiKeysAdmin) (`roles/serviceusage.apiKeysAdmin`)
    *   [Service Usage Viewer](https://docs.cloud.google.com/iam/docs/roles-permissions/serviceusage#serviceusage.serviceUsageViewer) (`roles/serviceusage.serviceUsageViewer`)
*   Create authorization keys. Add the same roles as you use to create API keys, plus:
    *   [Service Account User](https://docs.cloud.google.com/iam/docs/roles-permissions/iam#iam.serviceAccountUser) (`roles/iam.serviceAccountUser`)
    *   [Service Account API Key Binding Admin](https://docs.cloud.google.com/iam/docs/roles-permissions/iam#iam.serviceAccountApiKeyBindingAdmin) (`roles/iam.serviceAccountApiKeyBindingAdmin`)

For more information about granting roles, see [Manage access to projects, folders, and organizations](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access).

You might also be able to get the required permissions through [custom roles](https://docs.cloud.google.com/iam/docs/creating-custom-roles) or other [predefined roles](https://docs.cloud.google.com/iam/docs/roles-overview#predefined).

### Enable authorization keys

Before you can [create an authorization key](#api-keys-bound-sa), you must do one of the following:

*   Update the `constraints/iam.managed.disableServiceAccountApiKeyCreation` organization policy constraint to restrict the services that users can create authorization keys for. When creating an authorization key, users must add an [API restriction](#adding-api-restrictions) that matches a service allowed by the constraint.
    
*   Disable the `constraints/iam.managed.disableServiceAccountApiKeyCreation` organization policy constraint.
    

Changing the organization policy requires an [organization resource](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy#organizations). Projects without an organization aren't supported.

To change the policy constraint, complete the following instructions.

### Console

1.  In the Google Cloud console, go to the **Organization policies** page.
    
    [Go to Organization policies](https://console.cloud.google.com/iam-admin/orgpolicies/list)
    
2.  Switch to the organization, folder, or project you want to change the policies for.
    
3.  In the **Filter** box, enter `Block service`, and then click the policy name **Block service account API key bindings**.
    
4.  Click **Manage policy**.
    
5.  In the **Policy source** section, select **Override parent's policy**.
    
6.  Click **Add a rule**.
    
7.  To disable the constraint, set **Enforcement** to **Off**.
    
    To add a service to the allowed list, set **Enforcement** to **On**.
    
    1.  Click **Edit**.
        
    2.  In the **Value type** section, select **User-defined**.
        
    3.  Enter the service that you want to allow creating API keys for.
        
8.  Click **Done**.
    
9.  Optional: Click **Test changes** to give you insight on how the proposed policy might cause compliance violations or disruptions.
    
10.  Click **Set policy**.
     

### gcloud

To add a service to the allowed list, do the following:

1.  Create a file named `spec.yaml` with the following content:
    
    name: SCOPE/SCOPE_ID/policies/iam.managed.disableServiceAccountApiKeyCreation
        spec:
          rules:
          - enforce: true
            parameters:
              allowedServices:
              - SERVICE_NAME
    
    Provide the following values:
    
    *   `SCOPE`: Either `organizations`, `folders`, or `projects`.
        
    *   `SCOPE_ID`: Depending on SCOPE, the ID of the organization, folder, or project to which the organization policy applies.
        
    *   `SERVICE_NAME`: The name of the service you want to allow—for example, `compute.googleapis.com`.
        
2.  Run the following `gcloud` command to allow binding of API keys to service accounts for the specified service:
    
    gcloud org-policies set-policy spec.yaml \
            --update-mask spec
    

To disable the constraint, do the following:

1.  Create a file named `spec.yaml` with the following content:
    
    name: SCOPE/SCOPE_ID/policies/iam.managed.disableServiceAccountApiKeyCreation
        spec:
          rules:
          - enforce: false
    
2.  Run the following `gcloud` command to disable the constraint:
    
    gcloud org-policies set-policy spec.yaml \
            --update-mask spec

Create an API key
-----------------

To create an API key, use one of the following options:

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click **Create credentials**, and then select **API key** from the menu.
    
3.  Add at least one API key restriction. For more information, see [Apply API key restrictions](https://docs.cloud.google.com/docs/authentication/api-keys#api_key_restrictions).
    
4.  Optional: To bind the API key to a service account and create an authorization key, select the **Authenticate API calls through a service account** checkbox and then click **Select a service account** to select the service account you want to bind to the key.
    
    For more information, see [Authorization keys](#api-keys-bound-sa).
    
5.  Click **Create**. The **API key created** dialog displays the string for your newly created key.
    

### gcloud

You use the [`gcloud services api-keys create` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/create) to create an API key.

 gcloud services api-keys create \
         --display-name=DISPLAY_NAME \
         --api-target=service=SERVICE_1 \
         --api-target=service=SERVICE_2

Replace the following values:

*   `DISPLAY_NAME`: A descriptive name for your key.
    
*   `SERVICE_1`, `SERVICE_2`...: The service names of the APIs that the key can be used to access.
    
    You can find the service name by searching for the API on the [API dashboard](https://console.cloud.google.com/apis/dashboard). Service names are strings like `bigquery.googleapis.com`.
    
    To bind the API key to a service account and create an authorization key for services such as Vertex AI and the Gemini API, use `gcloud beta` instead, with the `--service-account` flag:
    
    gcloud beta services api-keys create \
            --display-name=DISPLAY_NAME \
            --api-target=service=SERVICE_1 \
            --api-target=service=SERVICE_2 \
            --service-account=SERVICE_ACCOUNT_EMAIL_ADDRESS
    
    For more information, see [Authorization keys](#api-keys-bound-sa).
    

### C++

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/cpp/docs/reference/apikeys/latest).

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

You use the [`keys.create` method](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/create) to create an API key. This request returns a [long-running operation](#lro); you must poll the operation to get the information for the new key.

curl -X POST \
         -H "Authorization: Bearer $(gcloud auth print-access-token)" \
         -H "Content-Type: application/json; charset=utf-8" \
         -d '{
              "displayName" : "DISPLAY_NAME",
              "restrictions" : {
                "apiTargets": [
                  {
                    "service": "SERVICE_1"
                  },
                  {
                    "service" : "SERVICE_2"
                  },
                ]
              }
            }' \
         "https://apikeys.googleapis.com/v2/projects/PROJECT_ID/locations/global/keys"

Replace the following values:

*   `DISPLAY_NAME`: A descriptive name for your key.
    
*   `PROJECT_ID`: Your Google Cloud project ID or name.
    
*   `SERVICE_1`, `SERVICE_2`...: The service names of the APIs that the key can be used to access.
    

You can find the service name by searching for the API on the [API dashboard](https://console.cloud.google.com/apis/dashboard). Service names are strings like `bigquery.googleapis.com`.

Optional: To bind the API key to a service account and create an authorization key instead, use the following command:

curl -X POST \
         -H "Authorization: Bearer $(gcloud auth print-access-token)" \
         -H "Content-Type: application/json; charset=utf-8" \
         -d '{
              "displayName" : "DISPLAY_NAME",
              "restrictions" : {
                "apiTargets": [
                  {
                    "service": "SERVICE_1"
                  },
                  {
                    "service" : "SERVICE_2"
                  },
                ]
              },
              "serviceAccountEmail" : "SERVICE_ACCOUNT_EMAIL_ADDRESS"
            }' \
         "https://apikeys.googleapis.com/v2/projects/PROJECT_ID/locations/global/keys"

For more information, see [Authorization keys](#api-keys-bound-sa).

For more information about creating API keys using the REST API, see [Creating an API key](https://docs.cloud.google.com/api-keys/docs/create-manage-api-keys#create) in the API Keys API documentation.

Apply API key restrictions
--------------------------

Unrestricted API keys are insecure. To reduce security risks, you can restrict API keys in the following ways:

*   **[API restrictions](#adding-api-restrictions)**: Limit an API key to only be used with a specific set of APIs. API keys without API restrictions can be used with all APIs that accept keys generated by Google Cloud. API restrictions can be mandatory, depending on how you create an API key:
    
    *   In the Google Cloud console and the gcloud CLI, you must add at least one API restriction to create an API key.
        
    *   When you create API keys using the REST API, the keys are unrestricted unless you specify a restriction. To do so, add the [`restrictions`](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys#restrictions) object to your request body, containing an [`apiTargets`](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys#restrictions) array that specifies the restrictions you want to add.
        
*   **[Application restrictions](#adding-application-restrictions)**: Limit an API key so it can only be used by specific websites, IP addresses, or applications. API keys without application restrictions can be used from anywhere.
    

We recommend setting both API restrictions and application restrictions.

### Add API restrictions

API restrictions specify which APIs can be called using the API key.

To add API restrictions, use one of the following options:

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to restrict.
    
3.  In the **API restrictions** section, click **Restrict key**.
    
4.  Select all APIs that your API key will be used to access.
    
5.  Click **Save** to save your changes and return to the API key list.
    

### gcloud

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the keys in your project.
    
2.  Use the [`gcloud services api-keys update` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/update) to specify which services an API key can be used to access.
    
    Replace the following values:
    
    *   `KEY_ID`: The ID of the key that you want to restrict.
    *   `SERVICE_1`, `SERVICE_2`...: The service names of the APIs that the key can be used to access.
        
        You must provide all service names with the update command; the service names provided replace any existing services on the key.
        
    
    You can find the service name by searching for the API on the [API dashboard](https://console.cloud.google.com/apis/dashboard). Service names are strings like `bigquery.googleapis.com`.
    
    gcloud services api-keys update KEY\_ID \\
    --api-target\=service\=SERVICE\_1 \--api-target\=service\=SERVICE\_2
    

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [keys.list](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/list) method. The ID is listed in the `uid` field of the response.
    
    Replace `PROJECT_ID` with your Google Cloud project ID or name.
    
    curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/"
    
2.  Use the [keys.patch](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/patch) method to specify which services an API key can be used to access.
    
    This request returns a [long-running operation](#lro); you must poll the operation to know when the operation completes and get the operation status.
    
    Replace the following values:
    
    *   `SERVICE_1`, `SERVICE_2`...: The service names of the APIs that the key can be used to access.
        
        You must provide all service names with the request; the service names provided replace any existing services on the key.
        
        You can find the service name by searching for the API on the [API dashboard](https://console.cloud.google.com/apis/dashboard). Service names are strings like `bigquery.googleapis.com`.
        
    *   `PROJECT_ID`: Your Google Cloud project ID or name.
        
    *   `KEY_ID`: The ID of the key that you want to restrict.
        
    
    curl \-X PATCH \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    \--data '{
    "restrictions" : {
    "apiTargets": \[
      {
        "service": "SERVICE\_1"
      },
      {
        "service" : "SERVICE\_2"
      },
    \]
    }
    }' \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/KEY\_ID?updateMask=restrictions"
    

For more information about adding API restrictions to a key using the REST API, see [Adding API restrictions](https://docs.cloud.google.com/api-keys/docs/add-restrictions-api-keys#add-api-restrictions) in the API Keys API documentation.

### Add application restrictions

Application restrictions specify which websites, IP addresses, or apps can use an API key.

You can apply only one application restriction type at a time. Choose the restriction type based on your application type:

| Option | Application type | Notes |
| --- | --- | --- |
| [Websites](#websites) | Web applications | Specifies the websites that can use the key. |
| [IP addresses](#ip) | Applications called by specific servers | Specifies the servers or cron jobs that can use the key. This is the only restriction available for authorization keys. |
| [Android apps](#android) | Android applications | Specifies the Android application that can use the key. |
| [iOS apps](#ios) | iOS applications | Specifies the iOS bundles that can use the key. |

#### Websites

To control which websites can use your API keys, you can add one or more HTTP referrers as website restrictions. For example, adding `https://example.com` to an API key's website restrictions means that only calls from `https://example.com` can use that API key.

The HTTP referrers used in website restrictions have limited wildcard support. You can substitute a wildcard character (`*`) for a subdomain or path, but you can't use a wildcard character in the middle of a URL. For example, `*.example.com` is valid, and accepts all sites ending in `.example.com`. However, `mysubdomain*.example.com` isn't a valid restriction.

Port numbers can be included in website restrictions. If you include a port number, then only requests using that port are matched. If you don't specify a port number, then requests from any port number are matched.

The following table shows some example scenarios and browser restrictions:

| **Scenario** | **Restrictions** |
| --- | --- |
| Allow a specific URL | Add a URL with an exact path. For example:  
`www.example.com/path`  
`www.example.com/path/path`
Some browsers implement a [referrer policy](https://developer.chrome.com/blog/referrer-policy-new-chrome-default/) that sends only the origin URL for cross-origin requests. Users of these browsers can't use keys with page-specific URL restrictions.

 |
| Allow any URL in your site | You must set two URLs in the `allowedReferers` list.  

1.  URL for the domain, without a subdomain, and with a wildcard for the path. For example:  
    `example.com/*`
2.  A second URL that includes a wildcard for the subdomain and a wildcard for the path. For example:  
    `*.example.com/*`

 |
| Allow any URL in a single subdomain or naked domain | 

You must set two URLs in the `allowedReferers` list to allow an entire domain:

1.  URL for the domain, without a trailing slash. For example:  
    `www.example.com`  
    `sub.example.com`  
    `example.com`  
    
2.  A second URL for the domain that includes a wildcard for the path. For example:  
    `www.example.com/*`  
    `sub.example.com/*`  
    `example.com/*`

 |

To restrict your API key to specific websites, use one of the following options:

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to restrict.
    
3.  In the **Application restrictions** section, select **Websites**.
    
4.  For each restriction that you want to add, click **Add**, enter the restriction, and then click **Done**.
    
5.  Click **Save** to save your changes and return to the API key list.
    

### gcloud

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the keys in your project.
    
2.  Use the [`gcloud services api-keys update` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/update) to add website restrictions to an API key.
    
    Replace the following values:
    
    *   `KEY_ID`: The ID of the key that you want to restrict.
    *   `ALLOWED_REFERRER_1`: Your website restriction.
        
        You can add as many restrictions as needed; use commas to separate the restrictions. You must provide all referrer restrictions with the update command; the referrer restrictions provided replace any existing referrer restrictions on the key.
        
    
    gcloud services api-keys update KEY\_ID \\
     \--allowed-referrers\="ALLOWED\_REFERRER\_1"
    

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [keys.list](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/list) method. The ID is listed in the `uid` field of the response.
    
    Replace `PROJECT_ID` with your Google Cloud project ID or name.
    
    curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/"
    
2.  Use the [keys.patch](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/patch) method to add website restrictions to the API key.
    
    This request returns a [long-running operation](#lro); you must poll the operation to know when the operation completes and get the operation status.
    
    Replace the following values:
    
    *   `ALLOWED_REFERRER_1`: Your website restriction.
        
        You can add as many restrictions as needed; use commas to separate the restrictions. You must provide all referrer restrictions with the request; the referrer restrictions provided replace any existing referrer restrictions on the key.
        
    *   `PROJECT_ID`: Your Google Cloud project ID or name.
        
    *   `KEY_ID`: The ID of the key that you want to restrict.
        
    
    curl \-X PATCH \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    \--data '{
    "restrictions" : {
    "browserKeyRestrictions": {
      "allowedReferrers": \["ALLOWED\_REFERRER\_1"\]
    }
    }
    }' \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/KEY\_ID?updateMask=restrictions"
    

For more information about adding website restrictions to a key using the REST API, see [Adding browser restrictions](https://docs.cloud.google.com/api-keys/docs/add-restrictions-api-keys#adding_browser_restrictions) in the API Keys API documentation.

#### IP addresses

You can specify the external IP addresses of the callers (such as web servers or cron jobs) that are allowed to use your API keys. You can specify IP addresses in the following formats:

*   IPv4 (`198.51.100.1`)
    
*   IPv6 (`2001:db8::1`)
    
*   A subnet using CIDR notation (`198.51.100.0/24`, `2001:db8::/64`)
    

Internal IP addresses and `localhost` aren't supported.

To restrict your API key to specific external IP addresses, use one of the following options:

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to restrict.
    
3.  In the **Application restrictions** section, select **IP addresses**.
    
4.  For each IP address that you want to add, click **Add**, enter the address, and then click **Done**.
    
5.  Click **Save** to save your changes and return to the API key list.
    

### gcloud

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the keys in your project.
    
2.  Use the [`gcloud services api-keys update` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/update) to add server (IP address) restrictions to an API key.
    
    Replace the following values:
    
    *   `KEY_ID`: The ID of the key that you want to restrict.
    *   `ALLOWED_IP_ADDR_1`: Your allowed IP address.
        
        You can add as many IP addresses as needed; use commas to separate the addresses.
        
    
    gcloud services api-keys update KEY\_ID \\
    --allowed-ips\="ALLOWED\_IP\_ADDR\_1"
    

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [keys.list](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/list) method. The ID is listed in the `uid` field of the response.
    
    Replace `PROJECT_ID` with your Google Cloud project ID or name.
    
    curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/"
    
2.  Use the [keys.patch](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/patch) method to add server (IP address) restrictions to an API key.
    
    This request returns a [long-running operation](#lro); you must poll the operation to know when the operation completes and get the operation status.
    
    Replace the following values:
    
    *   `ALLOWED_IP_ADDR_1`: Your allowed IP address.
        
        You can add as many IP addresses as needed; use commas to separate the restrictions. You must provide all IP addresses with the request; the referrer restrictions provided replace any existing IP address restrictions on the key.
        
    *   `PROJECT_ID`: Your Google Cloud project ID or name.
        
    *   `KEY_ID`: The ID of the key that you want to restrict.
        
    
    curl \-X PATCH \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    \--data '{
    "restrictions" : {
      "serverKeyRestrictions": {
        "allowedIps": \["ALLOWED\_IP\_ADDR\_1"\]
      }
    }
    }' \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/KEY\_ID?updateMask=restrictions"
    

For more information about adding IP address restrictions to a key using the REST API, see [Adding server restrictions](https://docs.cloud.google.com/api-keys/docs/add-restrictions-api-keys#adding_server_restrictions) in the API Keys API documentation.

#### Android apps

You can restrict usage of an API key to specific Android apps. You must provide the package name and the 20-byte SHA-1 certificate fingerprint for each app.

When you use the API key in a request, you must specify the package name and certificate fingerprint by using the following HTTP headers:

*   `X-Android-Package`
*   `X-Android-Cert`

To restrict your API key to one or more Android apps, use one of the following options:

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to restrict.
    
3.  In the **Application restrictions** section, select **Android apps**.
    
4.  For each Android app that you want to add, click **Add**, enter the package name and SHA-1 certificate fingerprint, and then click **Done**.
    
5.  Click **Save** to save your changes and return to the API key list.
    

### gcloud

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the keys in your project.
    
2.  Use the [`gcloud services api-keys update` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/update) to specify the Android apps that can use an API key.
    
    Replace the following values:
    
    *   `KEY_ID`: The ID of the key that you want to restrict.
    *   `SHA1_FINGERPRINT` and `PACKAGE_NAME`: The app information for an Android app that can use the key.
        
        You can add as many apps as needed; use additional `--allowed-application` flags.
        
    
    gcloud services api-keys update KEY\_ID \\
    --allowed-application\=sha1\_fingerprint\=SHA1\_FINGERPRINT\_1,package\_name\=PACKAGE\_NAME\_1 \\
    --allowed-application\=sha1\_fingerprint\=SHA1\_FINGERPRINT\_2,package\_name\=PACKAGE\_NAME\_2
    

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [keys.list](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/list) method. The ID is listed in the `uid` field of the response.
    
    Replace `PROJECT_ID` with your Google Cloud project ID or name.
    
    curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/"
    
2.  Use the [keys.patch](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/patch) method to specify the Android apps that can use an API key.
    
    This request returns a [long-running operation](#lro); you must poll the operation to know when the operation completes and get the operation status.
    
    Replace the following values:
    
    *   `SHA1_FINGERPRINT_1` and `PACKAGE_NAME_1`: The app information for an Android app that can use the key.
        
        You can add the information for as many apps as needed; use commas to separate the [AndroidApplication](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys#AndroidApplication) objects. You must provide all applications with the request; the applications provided replace any existing allowed applications on the key.
        
    *   `PROJECT_ID`: Your Google Cloud project ID or name.
        
    *   `KEY_ID`: The ID of the key that you want to restrict.
        
    
    curl \-X PATCH \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    \--data '{
    "restrictions" : {
    "androidKeyRestrictions": {
      "allowedApplications": \[
        {
          "sha1Fingerprint": "SHA1\_FINGERPRINT\_1",
          "packageName": "PACKAGE\_NAME\_1"
        },
     \]
    }
    }
    }' \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/KEY\_ID?updateMask=restrictions"
    

For more information about adding Android app restrictions to a key using the REST API, see [Adding Android restrictions](https://docs.cloud.google.com/api-keys/docs/add-restrictions-api-keys#adding_android_restrictions) in the API Keys API documentation.

#### iOS apps

You can restrict usage of an API key to specific iOS apps by providing the bundle ID of each app.

When you use the API key in a request, you must specify the bundle ID by using the `X-Ios-Bundle-Identifier` HTTP header.

To restrict your API key to one or more iOS apps, use one of the following options:

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to restrict.
    
3.  In the **Application restrictions** section, select **iOS apps**.
    
4.  For each iOS app that you want to add, click **Add**, enter the bundle ID, and then click **Done**.
    
5.  Click **Save** to save your changes and return to the API key list.
    

### gcloud

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the keys in your project.
    
2.  Use the [`gcloud services api-keys update`](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/update) method to specify the iOS apps that can use the key.
    
    Replace the following values:
    
    *   `KEY_ID`: The ID of the key that you want to restrict.
    *   `ALLOWED_BUNDLE_ID`: The bundle ID of an iOS app that you want to be able to use this API key.
        
        You can add as many bundle IDs as needed; use commas to separate the IDs.
        
    
    gcloud services api-keys update KEY\_ID \\
    --allowed-bundle-ids\=ALLOWED\_BUNDLE\_ID\_1,ALLOWED\_BUNDLE\_ID\_2
    

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

1.  Get the ID of the key that you want to restrict.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [keys.list](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/list) method. The ID is listed in the `uid` field of the response.
    
    Replace `PROJECT_ID` with your Google Cloud project ID or name.
    
    curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/"
    
2.  Use the [keys.patch](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/patch) method to specify the iOS apps that can use an API key.
    
    This request returns a [long-running operation](#lro); you must poll the operation to know when the operation completes and get the operation status.
    
    Replace the following values:
    
    *   `ALLOWED_BUNDLE_ID`: The bundle ID of an iOS app that can use the key.
        
        You can add the information for as many apps as needed; use commas to separate the bundle IDs. You must provide all bundle IDs with the request; the bundle IDs provided replace any existing allowed applications on the key.
        
    *   `PROJECT_ID`: Your Google Cloud project ID or name.
        
    *   `KEY_ID`: The ID of the key that you want to restrict.
        
    
    curl \-X PATCH \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    \--data '{
    "restrictions" : {
    "iosKeyRestrictions": {
      "allowedBundleIds": \["ALLOWED\_BUNDLE\_ID\_1","ALLOWED\_BUNDLE\_ID\_2"\]
    }
    }
    }' \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/KEY\_ID?updateMask=restrictions"
    

For more information about adding iOS app restrictions to a key using the REST API, see [Adding iOS restrictions](https://docs.cloud.google.com/api-keys/docs/add-restrictions-api-keys#adding_ios_restrictions) in the API Keys API documentation.

### List API keys that don't have restrictions

You can list the API keys in an organization, folder, or project that don't have restrictions using Cloud Asset Inventory. To do so, complete the following steps:

1.  Go to the Cloud Asset Inventory library page in the Google Cloud console and check to see if it's enabled in the project where you're running Cloud Asset Inventory commands from. If it's not enabled, enable it.
    
    [Go to the **Library** page](https://console.cloud.google.com/apis/library/cloudasset.googleapis.com)
    
    If the Cloud Asset Inventory API is already enabled for the current project, you see a button labeled **Manage**. Otherwise, you see a button labeled **Enable**.
    
2.  Make sure your account has the [correct role to call the Cloud Asset Inventory API](https://docs.cloud.google.com/asset-inventory/docs/roles-permissions#roles). For individual permissions for each call type, see [Permissions](https://docs.cloud.google.com/asset-inventory/docs/roles-permissions#permissions).
    
3.  Run the following command to list API keys that don't have restrictions:
    
    gcloud asset list \
          --SCOPE \
          --asset-types=apikeys.googleapis.com/Key \
          --content-type=resource \
          --filter="FILTER"
    
    Replace the following:
    
    *   `SCOPE`: The scope (project, folder, or organization) for listing API keys. Use one of the following values:
        
        *   `project=PROJECT_ID`, where `PROJECT_ID` is the ID of the project.
            
        *   `folder=FOLDER_ID`, where `FOLDER_ID` is the ID of the folder.
            
        *   `organization=ORGANIZATION_ID`, where `ORGANIZATION_ID` is the ID of the organization.
            
    *   `FILTER`: Which API keys to list, based on the restrictions they're missing:
        
        *   `-resource.data.restrictions:*`: Show API keys that are missing all restrictions.
            
        *   `-resource.data.restrictions.apiTargets:*`: Show API keys that are missing API restrictions.
            
        *   `-resource.data.restrictions.browserKeyRestrictions:*`: Show API keys that are missing website restrictions.
            
        *   `-resource.data.restrictions.serverKeyRestrictions:*`: Show API keys that are missing IP address restrictions.
            
        *   `-resource.data.restrictions.androidKeyRestrictions:*`: Show API keys that are missing Android restrictions.
            
        *   `-resource.data.restrictions.iosKeyRestrictions:*`: Show API keys that are missing iOS restrictions.
            

Get project information from a key string
-----------------------------------------

You can determine which Google Cloud project an API key is associated with from its string.

Replace `KEY_STRING` with the key string you need project information for.

### gcloud

You use the [`gcloud services api-keys lookup` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/lookup) to get the project ID from a key string.

 gcloud services api-keys lookup KEY\_STRING
 

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### Python

To run this sample, you must install the [API Keys client library](https://docs.cloud.google.com/python/docs/reference/apikeys/latest).

### REST

You use the [`lookupKey` method](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/keys/lookupKey) to get the project ID from a key string.

curl \-X GET \\
\-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
\-H "Content-Type: application/json; charset=utf-8" \\
"https://apikeys.googleapis.com/v2/keys:lookupKey?keyString=KEY\_STRING"

Create a copy of an API key
---------------------------

If you need a new API key with the same restrictions as an existing API key, you can create a copy of the existing API key. This operation creates a new API key with a unique key string and ID, with the existing API key's restrictions.

The copy operation is available only in the Google Cloud console. To use other methods, follow the steps to [create an API key](#create), and then [apply the same API key restrictions](#adding-api-restrictions) to the newly generated API key.

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to copy.
    
    The API key's details page opens.
    
3.  Click **Create a copy**.
    
4.  Enter a name for the new API key and confirm that the restrictions are correct.
    
5.  Click **Create**.
    

Rotate an API key
-----------------

By periodically rotating your API keys, you can limit the impact of any compromised API keys.

When you rotate an API key, you create a new key with the same restrictions as the old key, and update your applications to use the new key. After all of your applications are updated, you delete the old key.

The rotation operation is available only in the Google Cloud console. To use other methods, follow the steps to [create an API key](#create), and then [apply the same API key restrictions](#adding-api-restrictions) to the newly generated API key. After updating your applications to use the new key, you delete the old key.

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click the name of the API key that you want to rotate to open its details page.
    
3.  Click **Rotate key**.
    
4.  Enter a name for the new API key and confirm that the restrictions are correct.
    
5.  Click **Create**.
    
6.  Copy the key string and update your applications to use the new string.
    
7.  After you have updated all applications to use the new key, return to the details page for the new key. In the **Previous key** section, click **Delete the previous key** to delete the old key.
    
    If you find that you deleted the old key prematurely, you can [undelete it](#undelete).
    

Undelete an API key
-------------------

If you delete an API key by mistake, you can undelete (restore) that key within 30 days of deleting the key. After 30 days, you cannot undelete the API key.

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
2.  Click **Restore deleted credentials**.
    
3.  Find the deleted API key that you want to undelete, and click **Restore**.
    
    Undeleting an API key may take a few minutes to propagate. After propagation, the undeleted API key is displayed in the API keys list.
    

### gcloud

1.  Get the ID of the key that you want to undelete.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list --show-deleted` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the deleted keys in your project.
    
2.  Use the [`gcloud services api-keys undelete` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/undelete) to undelete an API key.
    
    gcloud services api-keys undelete KEY\_ID
    
    Replace the following values:
    
    *   `KEY_ID`: The ID of the key that you want to undelete.

### Java

To run this sample, you must install the [`google-cloud-apikeys` client library](https://docs.cloud.google.com/java/docs/reference/google-cloud-apikeys/latest/overview).

### REST

1.  Get the ID of the key that you want to undelete.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [keys.list](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/list) method, with the `showDeleted` query parameter set to `true`. The key ID is listed in the `uid` field of the response.
    
    Replace `PROJECT_ID` with your Google Cloud project ID or name.
    
    curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys?showDeleted=true"
    
2.  Use the [undelete](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/projects.locations.keys/undelete) method to undelete the API key.
    
    curl \-X POST \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    "https://apikeys.googleapis.com/v2/projects/PROJECT\_ID/locations/global/keys/KEY\_ID:undelete"
    
    This request returns a [long-running operation](#lro); you must poll the operation to know when the operation completes and get the operation status.
    
    Replace the following values:
    
    *   `PROJECT_ID`: Your Google Cloud project ID or name.
    *   `KEY_ID`: The ID of the key that you want to restrict.

Determine the API key type
--------------------------

You can determine whether the API key is a standard or authorization key by inspecting the key.

### Console

1.  In the Google Cloud console, go to the **Credentials** page:
    
    [Go to Credentials](https://console.cloud.google.com/apis/credentials)
    
    If a service account identifier is displayed, the API key is an authorization key. If there is no service account identifier displayed, the API key is a standard API key.
    

### gcloud

1.  Get the ID of the key.
    
    The ID is not the same as the display name or the key string. You can get the ID by using the [`gcloud services api-keys list` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/list) to list the keys in your project.
    
2.  Use the [`gcloud services api-keys describe` command](https://docs.cloud.google.com/sdk/gcloud/reference/services/api-keys/describe) to describe the API key.
    
    gcloud services api-keys describe KEY\_ID
    
    If a service account identifier is displayed, the API key is an authorization key. If there is no service account identifier displayed, the API key is a standard API key.
    

Poll long-running operations
----------------------------

API Keys API methods use long-running operations. If you use the REST API to create and manage API keys, an operation object is returned from the initial method request. You use the operation name to poll the long-running operation. When the long-running request completes, polling the operation returns the data from the long-running request.

To poll a long-running API Keys API operation, you use the [`operations.get`](https://docs.cloud.google.com/api-keys/docs/reference/rest/v2/operations/get) method.

Replace `OPERATION_NAME` with the operation name returned by the long-running operation. For example, `operations/akmf.p7-358517206116-cd10a88a-7740-4403-a8fd-979f3bd7fe1c`.

curl \-X GET \\
    \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
    \-H "Content-Type: application/json; charset=utf-8" \\
    "https://apikeys.googleapis.com/v2/OPERATION\_NAME"

Limits on API keys
------------------

You can create up to 300 API keys per project. This limit is a system limit, and can't be changed using a quota increase request. If more API keys are needed, you must use more than one project.

You can add up to 1200 [application restrictions](#adding-application-restrictions) to an API key.

What's next
-----------

*   Learn about [best practices for keeping your API keys secure](https://docs.cloud.google.com/docs/authentication/api-keys-best-practices).
*   Learn more about the [API Keys API](https://docs.cloud.google.com/api-keys/docs/overview).

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
