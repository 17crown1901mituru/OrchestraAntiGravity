# Method: folders.locations.services.quotaInfos.list &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.services.quotaInfos/list

*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Path parameters](#body.PATH_PARAMETERS)
*   [Query parameters](#body.QUERY_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
*   [Authorization scopes](#body.aspect)
*   [IAM Permissions](#body.aspect_1)
*   [Try it!](#try-it)

Lists QuotaInfos of all quotas for a given project, folder or organization.

### HTTP request

`GET https://cloudquotas.googleapis.com/v1beta/{parent=folders/*/locations/*/services/*}/quotaInfos`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Path parameters

 
| Parameters |
| --- |
| `parent` | 
`string`

Required. Parent value of QuotaInfo resources. Listing across different resource containers (such as 'projects/-') is not allowed.

Example names: `projects/123/locations/global/services/compute.googleapis.com` `folders/234/locations/global/services/compute.googleapis.com` `organizations/345/locations/global/services/compute.googleapis.com`

 |

### Query parameters

 
| Parameters |
| --- |
| `pageSize` | 
`integer`

Optional. Requested page size. Server may return fewer items than requested. If unspecified, server will pick an appropriate default.

 |
| `pageToken` | 

`string`

Optional. A token identifying a page of results the server should return.

 |

### Request body

The request body must be empty.

### Response body

If successful, the response body contains an instance of `[ListQuotaInfosResponse](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/ListQuotaInfosResponse)`.

Requires the following OAuth scope:

*   `https://www.googleapis.com/auth/cloud-platform`

For more information, see the [Authentication Overview](https://docs.cloud.google.com/docs/authentication#authorization-gcp).

### IAM Permissions

Requires the following [IAM](https://cloud.google.com/iam/docs) permission on the `parent` resource:

*   `cloudquotas.quotas.get`

For more information, see the [IAM documentation](https://cloud.google.com/iam/docs).
