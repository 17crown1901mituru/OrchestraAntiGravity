# Method: organizations.locations.services.quotaInfos.get &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.services.quotaInfos/get

*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Path parameters](#body.PATH_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
*   [Authorization scopes](#body.aspect)
*   [IAM Permissions](#body.aspect_1)
*   [Try it!](#try-it)

Retrieve the QuotaInfo of a quota for a project, folder or organization.

### HTTP request

`GET https://cloudquotas.googleapis.com/v1beta/{name=organizations/*/locations/*/services/*/quotaInfos/*}`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Path parameters

 
| Parameters |
| --- |
| `name` | 
`string`

Required. The resource name of the quota info.

An example name: `projects/123/locations/global/services/compute.googleapis.com/quotaInfos/CpusPerProjectPerRegion`

 |

### Request body

The request body must be empty.

### Response body

If successful, the response body contains an instance of `[QuotaInfo](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.services.quotaInfos#QuotaInfo)`.

Requires the following OAuth scope:

*   `https://www.googleapis.com/auth/cloud-platform`

For more information, see the [Authentication Overview](https://docs.cloud.google.com/docs/authentication#authorization-gcp).

### IAM Permissions

Requires the following [IAM](https://cloud.google.com/iam/docs) permission on the `name` resource:

*   `cloudquotas.quotas.get`

For more information, see the [IAM documentation](https://cloud.google.com/iam/docs).
