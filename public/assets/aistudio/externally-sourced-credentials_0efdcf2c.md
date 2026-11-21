# Authenticate with client libraries &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/external/externally-sourced-credentials

This page describes how you can use client libraries to access Google APIs.

Client libraries make it easier to access [Google Cloud APIs](https://docs.cloud.google.com/apis/docs/overview) using a supported language. You can use Google Cloud APIs directly by making raw requests to the server, but client libraries provide simplifications that significantly reduce the amount of code you need to write. This is especially true for authentication, because the client libraries support [Application Default Credentials (ADC)](https://docs.cloud.google.com/docs/authentication/application-default-credentials).

If you accept credential configurations (JSON, files, or streams) from an external source (for example, a customer), review the [security requirements when using credential configurations from an external source](#external-credentials).

Use Application Default Credentials with client libraries
---------------------------------------------------------

To use Application Default Credentials to authenticate your application, you must first [set up ADC](https://docs.cloud.google.com/docs/authentication/provide-credentials-adc) for the environment where your application is running. When you use the client library to create a client, the client library automatically checks for and uses the credentials you have provided to ADC to authenticate to the APIs your code uses. Your application does not need to explicitly authenticate or manage tokens; these requirements are managed automatically by the authentication libraries.

For a local development environment, you can set up ADC [with your user credentials](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment) or [with service account impersonation](https://docs.cloud.google.com/docs/authentication/use-service-account-impersonation#adc) by using the gcloud CLI. For production environments, you set up ADC by [attaching a service account](https://docs.cloud.google.com/docs/authentication/set-up-adc-attached-service-account).

### Example client creation

The following code samples create a client for the Cloud Storage service. Your code is likely to need different clients; these samples are meant only to show how you can create a client and use it without any code to explicitly authenticate.

Before you can run the following samples, you must complete the following steps:

*   [Set up ADC for your environment](https://docs.cloud.google.com/docs/authentication/provide-credentials-adc)
*   [Install the Cloud Storage client library](https://docs.cloud.google.com/storage/docs/reference/libraries)

### Go

### Java

### Node.js

### PHP

### Python

### Ruby

Use API keys with client libraries
----------------------------------

You can use an API keys only with client libraries for APIs that accept API keys. In addition, the API key must not have an API restriction that prevents it from being used for the API.

For more information about API keys created in express mode, see the [Google Cloud express mode FAQ](https://cloud.google.com/resources/cloud-express-faqs).

This example uses the Cloud Natural Language API, which accepts API keys, to demonstrate how you would provide an API key to the library.

When you use API keys in your applications, ensure that they are kept secure during both storage and transmission. Publicly exposing your API keys can lead to unexpected charges on your account. For more information, see [Best practices for managing API keys](https://docs.cloud.google.com/docs/authentication/api-keys-best-practices).

Security requirements when using credential configurations from an external source
----------------------------------------------------------------------------------

Typically, you generate credential configurations by using gcloud CLI commands or by using the Google Cloud console. For example, you can use the gcloud CLI to generate a local ADC file or a login configuration file. Similarly, you can use the Google Cloud console to create and download a service account key.

For some use cases, however, credential configurations are provided to you by an external entity; these credential configurations are intended to be used to authenticate to Google APIs.

Some types of credential configurations include endpoints and file paths, which the authentication libraries use to acquire a token. When you accept credential configurations from an external source, you must validate the configuration before using it. If you don't validate the configuration, a malicious actor could use the credential to compromise your systems and data.

### Validate credential configurations from external sources

How you need to validate your external credentials depends on what types of credential your application accepts.

#### Validate service account keys

If your application accepts _only_ service account keys, use a credential loader specific to service account keys, as shown in the following examples. The type-specific credential loader parses only the fields present for service account keys, which don't expose any vulnerabilities.

### C#

var saCredential = CredentialFactory.FromStream<ServiceAccountCredential>(stream);

### C++

auto cred = google::cloud::MakeServiceAccountCredentials(json)

### Java

ServiceAccountCredentials credentials =
          ServiceAccountCredentials.fromStream(credentialsStream);

### Node.js

const keys = JSON.parse(json_input)
    const authClient = JWT.fromJSON(keys);

### PHP

$cred = new Google\Auth\Credentials\ServiceAccountCredentials($scope, $jsonKey);

### Python

cred = service_account.Credentials.from_service_account_info(json_data)

### Ruby

creds = Google::Auth::ServiceAccountCredentials.make_creds(json_key_io: json_stream)

If you can't use a type-specific credential loader, validate the credential by confirming that the value for the `type` field is `service_account`. If the value for the `type` field is any other value, don't use the service account key.

#### Validate other credential configurations

If your application accepts _any_ type of credential besides a service account key, you must perform additional verification. Examples of other types of credential configurations include [ADC credential files](https://docs.cloud.google.com/docs/authentication/application-default-credentials#personal), [Workload Identity Federation credential files](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-other-clouds#create-cred-config), or [Workforce Identity Federation login configuration files](https://docs.cloud.google.com/iam/docs/workforce-obtaining-short-lived-credentials#create-login-config).

The following table lists the fields you need to validate, if they are present in your credentials. Not all of these fields are present for all credential configurations.

| Field | Purpose | Expected value |
| --- | --- | --- |
| `service_account_impersonation_url` | The authentication libraries use this field to access an endpoint to generate an access token for the service account being impersonated. | `https://iamcredentials.googleapis.com/v1/projects/-/serviceAccounts/service account email:generateAccessToken` |
| `token_url` | The authentication libraries send an external token to this endpoint to exchange it for a [federated access token](https://docs.cloud.google.com/docs/authentication/token-types#fed-access-tokens). | `https://sts.googleapis.com/v1/token` |
| `credential_source.file` | The authentication libraries read an external token from the file at the location specified by this field and send it to the `token_url` endpoint. | The path for a file containing an external token. You should recognize this path. |
| `credential_source.url` | An endpoint that returns an external token. The authentication libraries send a request to this URL and send the response to the `token_url` endpoint. | 
One of the following items:

*   A well-known endpoint provided by your cloud provider.
*   An endpoint that you have explicitly set up to provide tokens.

 |
| `credential_source.executable.command` | If the `GOOGLE_EXTERNAL_ACCOUNT_ALLOW_EXECUTABLES` environment variable is set to `1`, the authentication libraries run this command or executable file. | An executable file or command that returns an external token. You should recognize this command and validate that it is safe. |
| `credential_source.aws.url` | The authentication libraries issue a request to this URL to retrieve an AWS security token. | 

Either one of these exact values:

*   `http://169.254.169.254/latest/meta-data/iam/security-credentials`
*   `http://[fd00:ec2::254]/latest/meta-data/iam/security-credentials`

 |
| `credential_source.aws.region_url` | The authentication libraries issue a request to this URL to retrieve the active AWS region. | 

Either one of these exact values:

*   `http://169.254.169.254/latest/meta-data/placement/availability-zone`
*   `http://[fd00:ec2::254]/latest/meta-data/placement/availability-zone`

 |
| `credential_source.aws.imdsv2_session_token_url` | The authentication libraries issue a request to this URL to retrieve the AWS session token. | 

Either one of these exact values:

*   `http://169.254.169.254/latest/api/token`
*   `http://[fd00:ec2::254]/latest/api/token`

 |

What's next
-----------

*   Learn more about [Application Default Credentials](https://docs.cloud.google.com/docs/authentication/application-default-credentials).
*   Learn more about [API keys](https://docs.cloud.google.com/docs/authentication/api-keys).
*   See an overview of [Authentication methods](https://docs.cloud.google.com/docs/authentication).
