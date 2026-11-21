# Method: organizations.locations.quotaPreferences.create &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaPreferences/create

*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Path parameters](#body.PATH_PARAMETERS)
*   [Query parameters](#body.QUERY_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
*   [Authorization scopes](#body.aspect)
*   [IAM Permissions](#body.aspect_1)
*   [Try it!](#try-it)

Creates a new QuotaPreference that declares the desired value for a quota.

### HTTP request

`POST https://cloudquotas.googleapis.com/v1/{parent=organizations/*/locations/*}/quotaPreferences`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Path parameters

 
| Parameters |
| --- |
| `parent` | 
`string`

Required. Value for parent.

Example: `projects/123/locations/global`

 |

### Query parameters

 
| Parameters |
| --- |
| `quotaPreferenceId` | 
`string`

Optional. Id of the requesting object, must be unique under its parent. If client does not set this field, the service will generate one.

 |
| `ignoreSafetyChecks[]` | 

``enum (`[QuotaSafetyCheck](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/QuotaSafetyCheck)`)``

The list of quota safety checks to be ignored.

 |

### Request body

The request body contains an instance of `[QuotaPreference](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences#QuotaPreference)`.

### Response body

If successful, the response body contains a newly created instance of `[QuotaPreference](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences#QuotaPreference)`.

Requires the following OAuth scope:

*   `https://www.googleapis.com/auth/cloud-platform`

For more information, see the [Authentication Overview](https://docs.cloud.google.com/docs/authentication#authorization-gcp).

### IAM Permissions

Requires the following [IAM](https://cloud.google.com/iam/docs) permission on the `parent` resource:

*   `cloudquotas.quotas.update`

For more information, see the [IAM documentation](https://cloud.google.com/iam/docs).
