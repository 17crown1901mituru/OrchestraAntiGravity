# MCP Tools Reference: cloudquotas.googleapis.com &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/get_quota_adjuster_settings

Gets the QuotaAdjusterSettings for the specified resource container (project, folder, or organization). Expected request structure: 'name' (string, format: projects/{project}/locations/global/quotaAdjusterSettings, folders/{folder}/locations/global/quotaAdjusterSettings, or organizations/{organization}/locations/global/quotaAdjusterSettings).

The following sample demonstrate how to use `curl` to invoke the `get_quota_adjuster_settings` MCP tool.

| Curl Request |
| --- |
|                   
curl \--location 'https://cloudquotas.googleapis.com/mcp' \\
--header 'content-type: application/json' \\
--header 'accept: application/json, text/event-stream' \\
--data '{
  "method": "tools/call",
  "params": {
    "name": "get\_quota\_adjuster\_settings",
    "arguments": {
      // provide these details according to the tool's MCP specification
    }
  },
  "jsonrpc": "2.0",
  "id": 1
}'
                 |

Input Schema
------------

Virtual request message for GetQuotaAdjusterSettings via MCP facade.

### GetQuotaAdjusterSettingsRequest

| JSON representation |
| --- |
| 
{
  "name": string
}

 |

 
| Fields |
| --- |
| `name` | 
`string`

Required. Identifier. Name of the `quotaAdjusterSettings` configuration. Only a single setting per project is supported.

 |

Output Schema
-------------

The QuotaAdjusterSettings resource defines the settings for the Quota Adjuster.

### QuotaAdjusterSettings

| JSON representation |
| --- |
| 
{
  "name": string,
  "enablement": enum (`[Enablement](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/update_quota_adjuster_settings#Input.Schema.Enablement)`),
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

``enum (`[Enablement](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/update_quota_adjuster_settings#Input.Schema.Enablement)`)``

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

### Timestamp

| JSON representation |
| --- |
| 
{
  "seconds": string,
  "nanos": integer
}

 |

 
| Fields |
| --- |
| `seconds` | 
`string ([int64](https://developers.google.com/discovery/v1/type-format) format)`

Represents seconds of UTC time since Unix epoch 1970-01-01T00:00:00Z. Must be between -62135596800 and 253402300799 inclusive (which corresponds to 0001-01-01T00:00:00Z to 9999-12-31T23:59:59Z).

 |
| `nanos` | 

`integer`

Non-negative fractions of a second at nanosecond resolution. This field is the nanosecond portion of the duration, not an alternative to seconds. Negative second values with fractions must still have non-negative nanos values that count forward in time. Must be between 0 and 999,999,999 inclusive.

 |

### Enablement

The enablement status of the quota adjuster.

 
| Enums |
| --- |
| `ENABLEMENT_UNSPECIFIED` | The quota adjuster is in an unknown state. |
| `ENABLED` | The quota adjuster is enabled. |
| `DISABLED` | The quota adjuster is disabled. |

### Tool Annotations

Destructive Hint: ❌ | Idempotent Hint: ✅ | Read Only Hint: ✅ | Open World Hint: ❌
