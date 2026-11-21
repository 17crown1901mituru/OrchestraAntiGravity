# Method: projects.locations.quotaAdjusterSettings.updateQuotaAdjusterSettings &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings

*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Path parameters](#body.PATH_PARAMETERS)
*   [Query parameters](#body.QUERY_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
*   [Authorization scopes](#body.aspect)
*   [IAM Permissions](#body.aspect_1)
*   [Try it!](#try-it)

Updates the QuotaAdjusterSettings for the specified resource.

### HTTP request

`PATCH https://cloudquotas.googleapis.com/v1/{quotaAdjusterSettings.name=projects/*/locations/*/quotaAdjusterSettings}`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Path parameters

 
| Parameters |
| --- |
| `quotaAdjusterSettings.name` | 
`string`

Identifier. Name of the configuration, in the formats below:

*   For a project: projects/PROJECT\_NUMBER/locations/global/quotaAdjusterSettings
*   For a folder: folders/FOLDER\_NUMBER/locations/global/quotaAdjusterSettings
*   For an organization: organizations/ORGANIZATION\_NUMBER/locations/global/quotaAdjusterSettings

 |

### Query parameters

 
| Parameters |
| --- |
| `updateMask` | 
``string (`[FieldMask](https://protobuf.dev/reference/protobuf/google.protobuf/#field-mask)` format)``

Optional. The list of fields to update.

This is a comma-separated list of fully qualified names of fields. Example: `"user.displayName,photo"`.

 |
| `validateOnly` | 

`boolean`

Optional. If set to true, checks the syntax of the request but doesn't update the quota adjuster settings value. Note that although a request can be valid, that doesn't guarantee that the request will be fulfilled.

 |

### Request body

The request body contains an instance of `[QuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaAdjusterSettings#QuotaAdjusterSettings)`.

### Response body

If successful, the response body contains an instance of `[QuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaAdjusterSettings#QuotaAdjusterSettings)`.

Requires the following OAuth scope:

*   `https://www.googleapis.com/auth/cloud-platform`

For more information, see the [Authentication Overview](https://docs.cloud.google.com/docs/authentication#authorization-gcp).

### IAM Permissions

Requires the following [IAM](https://cloud.google.com/iam/docs) permission on the `name` resource:

*   `cloudquotas.quotas.update`

For more information, see the [IAM documentation](https://cloud.google.com/iam/docs).
