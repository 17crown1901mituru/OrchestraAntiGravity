# REST Resource: organizations.locations.services.quotaInfos &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.services.quotaInfos

*   [Resource: QuotaInfo](#QuotaInfo)
    *   [JSON representation](#QuotaInfo.SCHEMA_REPRESENTATION)
*   [Methods](#METHODS_SUMMARY)

Resource: QuotaInfo
-------------------

QuotaInfo represents information about a particular quota for a given project, folder or organization.

| JSON representation |
| --- |
| 
{
  "name": string,
  "quotaId": string,
  "metric": string,
  "service": string,
  "isPrecise": boolean,
  "refreshInterval": string,
  "containerType": enum (`[ContainerType](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos#QuotaInfo.ContainerType)`),
  "dimensions": \[
    string
  \],
  "metricDisplayName": string,
  "quotaDisplayName": string,
  "metricUnit": string,
  "quotaIncreaseEligibility": {
    object (`[QuotaIncreaseEligibility](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos#QuotaInfo.QuotaIncreaseEligibility)`)
  },
  "isFixed": boolean,
  "dimensionsInfos": \[
    {
      object (`[DimensionsInfo](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos#QuotaInfo.DimensionsInfo)`)
    }
  \],
  "isConcurrent": boolean,
  "serviceRequestQuotaUri": string
}

 |

 
| Fields |
| --- |
| `name` | 
`string`

Resource name of this QuotaInfo. The ID component following "locations/" must be "global". For example, `projects/123/locations/global/services/compute.googleapis.com/quotaInfos/CpusPerProjectPerRegion`

 |
| `quotaId` | 

`string`

The id of the quota, which is unique within the service. For example, `CpusPerProjectPerRegion`

 |
| `metric` | 

`string`

The metric of the quota. It specifies the resources consumption the quota is defined for. For example, `compute.googleapis.com/cpus`

 |
| `service` | 

`string`

The name of the service in which the quota is defined. For example, `compute.googleapis.com`

 |
| `isPrecise` | 

`boolean`

Whether this is a precise quota. A precise quota is tracked with absolute precision. In contrast, an imprecise quota is not tracked with precision.

 |
| `refreshInterval` | 

`string`

The reset time interval for the quota. Refresh interval applies to rate quota only. For example, "minute" for per minute, "day" for per day, or "10 seconds" for every 10 seconds.

 |
| `containerType` | 

``enum (`[ContainerType](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos#QuotaInfo.ContainerType)`)``

The container type of the QuotaInfo.

 |
| `dimensions[]` | 

`string`

The dimensions the quota is defined on.

 |
| `metricDisplayName` | 

`string`

The display name of the quota metric

 |
| `quotaDisplayName` | 

`string`

The display name of the quota.

 |
| `metricUnit` | 

`string`

The unit in which the metric value is reported, e.g., "MByte".

 |
| `quotaIncreaseEligibility` | 

``object (`[QuotaIncreaseEligibility](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos#QuotaInfo.QuotaIncreaseEligibility)`)``

Whether it is eligible to request a higher quota value for this quota.

 |
| `isFixed` | 

`boolean`

Whether the quota value is fixed or adjustable

 |
| `dimensionsInfos[]` | 

``object (`[DimensionsInfo](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos#QuotaInfo.DimensionsInfo)`)``

The collection of dimensions info ordered by their dimensions from more specific ones to less specific ones.

 |
| `isConcurrent` | 

`boolean`

Whether the quota is a concurrent quota. Concurrent quotas are enforced on the total number of concurrent operations in flight at any given time.

 |
| `serviceRequestQuotaUri` | 

`string`

URI to the page where users can request more quota for the cloud service—for example, [https://console.cloud.google.com/iam-admin/quotas](https://console.cloud.google.com/iam-admin/quotas).

 |

 
| 
Methods
-------

 |
| --- |
| 

### `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.services.quotaInfos/get)`

 | Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| 

### `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.services.quotaInfos/list)`

 | Lists QuotaInfos of all quotas for a given project, folder or organization. |
