# REST Resource: folders.locations.quotaAdjusterSettings &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings

*   [Resource: QuotaAdjusterSettings](#QuotaAdjusterSettings)
    *   [JSON representation](#QuotaAdjusterSettings.SCHEMA_REPRESENTATION)
    *   [Enablement](#QuotaAdjusterSettings.Enablement)
*   [Methods](#METHODS_SUMMARY)

The QuotaAdjusterSettings resource defines the settings for the Quota Adjuster.

| JSON representation |
| --- |
| 
{
  "name": string,
  "enablement": enum (`[Enablement](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings#QuotaAdjusterSettings.Enablement)`),
  "updateTime": string,
  "etag": string,
  "inherited": boolean,
  "inheritedFrom": string
}

 |

 
| Fields |
| --- |
| `name` | 
`string`

Identifier. Name of the configuration, in the formats below:

*   For a project: projects/PROJECT\_NUMBER/locations/global/quotaAdjusterSettings
*   For a folder: folders/FOLDER\_NUMBER/locations/global/quotaAdjusterSettings
*   For an organization: organizations/ORGANIZATION\_NUMBER/locations/global/quotaAdjusterSettings

 |
| `enablement` | 

``enum (`[Enablement](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings#QuotaAdjusterSettings.Enablement)`)``

Optional. The configured value of the enablement at the given resource.

 |
| `updateTime` | 

``string (`[Timestamp](https://protobuf.dev/reference/protobuf/google.protobuf/#timestamp)` format)``

Output only. The timestamp when the QuotaAdjusterSettings resource was last updated.

Uses RFC 3339, where generated output will always be Z-normalized and use 0, 3, 6 or 9 fractional digits. Offsets other than "Z" are also accepted. Examples: `"2014-10-02T15:01:23Z"`, `"2014-10-02T15:01:23.045123456Z"` or `"2014-10-02T15:01:23+05:30"`.

 |
| `etag` | 

`string`

Optional. The current ETag of the QuotaAdjusterSettings. If an ETag is provided on update and does not match the current server's ETag in the QuotaAdjusterSettings, the request is blocked and returns an ABORTED error. See [https://google.aip.dev/134#etags](https://google.aip.dev/134#etags) for more details on ETags.

 |
| `inherited` | 

`boolean`

Optional. Indicates whether the setting is inherited or explicitly specified.

 |
| `inheritedFrom` | 

`string`

Output only. The resource container from which the setting is inherited. This refers to the nearest ancestor with enablement set (either ENABLED or DISABLED). The value can be an organizations/{organization\_id}, folders/{folder\_id}, or can be 'default' if no ancestor exists with enablement set. The value will be empty when enablement is directly set on this container.

 |

### Enablement

The enablement status of the quota adjuster.

 
| Enums |
| --- |
| `ENABLEMENT_UNSPECIFIED` | The quota adjuster is in an unknown state. |
| `ENABLED` | The quota adjuster is enabled. |
| `DISABLED` | The quota adjuster is disabled. |

 
| 
Methods
-------

 |
| --- |
| 

### `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)`

 | Gets the QuotaAdjusterSettings for the specified resource. |
| 

### `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)`

 | Updates the QuotaAdjusterSettings for the specified resource. |
