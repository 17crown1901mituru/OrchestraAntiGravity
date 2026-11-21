# Method: folders.locations.quotaAdjusterSettings.getQuotaAdjusterSettings &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings/getQuotaAdjusterSettings

*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Path parameters](#body.PATH_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
*   [Authorization scopes](#body.aspect)
*   [IAM Permissions](#body.aspect_1)
*   [Try it!](#try-it)

Gets the QuotaAdjusterSettings for the specified resource.

### HTTP request

`GET https://cloudquotas.googleapis.com/v1beta/{name=folders/*/locations/*/quotaAdjusterSettings}`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Path parameters

 
| Parameters |
| --- |
| `name` | 
`string`

Required. Name of the `quotaAdjusterSettings` configuration. Only a single setting per project is supported.

 |

### Request body

The request body must be empty.

### Response body

If successful, the response body contains an instance of `[QuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings#QuotaAdjusterSettings)`.

Requires the following OAuth scope:

*   `https://www.googleapis.com/auth/cloud-platform`

For more information, see the [Authentication Overview](https://docs.cloud.google.com/docs/authentication#authorization-gcp).

### IAM Permissions

Requires the following [IAM](https://cloud.google.com/iam/docs) permission on the `name` resource:

*   `cloudquotas.quotas.get`

For more information, see the [IAM documentation](https://cloud.google.com/iam/docs).
