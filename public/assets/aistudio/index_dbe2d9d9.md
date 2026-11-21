# MCP Reference: cloudquotas.googleapis.com &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/mcp

MCP endpoint for Cloud Quotas.

A [Model Context Protocol (MCP) server](https://modelcontextprotocol.io/docs/learn/server-concepts) acts as a proxy between an external service that provides context, data, or capabilities to a Large Language Model (LLM) or AI application. MCP servers connect AI applications to external systems such as databases and web services, translating their responses into a format that the AI application can understand.

### Server Setup

You must [enable MCP servers](https://docs.cloud.google.com/mcp/enable-disable-mcp-servers) and [set up authentication](https://docs.cloud.google.com/mcp/authenticate-mcp) before use. For more information about using Google and Google Cloud remote MCP servers, see [Google Cloud MCP servers overview](https://docs.cloud.google.com/mcp/overview).

### Server Endpoints

An MCP service endpoint is the network address and communication interface (usually a URL) of the MCP server that an AI application (the Host for the MCP client) uses to establish a secure, standardized connection. It is the point of contact for the LLM to request context, call a tool, or access a resource. Google MCP endpoints can be global or regional.

The Cloud Quotas API MCP server has the following global MCP endpoint:

*   https://cloudquotas.googleapis.com/mcp

An [MCP tool](https://modelcontextprotocol.io/legacy/concepts/tools) is a function or executable capability that an MCP server exposes to a LLM or AI application to perform an action in the real world.

### Tools

The cloudquotas.googleapis.com MCP server has the following tools:

 
| MCP Tools |
| --- |
| `[update_quota_adjuster_settings](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/update_quota_adjuster_settings)` | Updates the QuotaAdjusterSettings for the specified resource container (project, folder, or organization). Expected request structure: 'quota\_adjuster\_settings.name' (string, format: projects/{project}/locations/global/quotaAdjusterSettings, folders/{folder}/locations/global/quotaAdjusterSettings, or organizations/{organization}/locations/global/quotaAdjusterSettings), 'quota\_adjuster\_settings.enablement' (enum: ENABLED, DISABLED, or ENABLEMENT\_UNSPECIFIED), and 'quota\_adjuster\_settings.inherited' (boolean). |
| `[get_quota_adjuster_settings](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/get_quota_adjuster_settings)` | Gets the QuotaAdjusterSettings for the specified resource container (project, folder, or organization). Expected request structure: 'name' (string, format: projects/{project}/locations/global/quotaAdjusterSettings, folders/{folder}/locations/global/quotaAdjusterSettings, or organizations/{organization}/locations/global/quotaAdjusterSettings). |
| `[list_quota_infos](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/list_quota_infos)` | Lists QuotaInfos of all quotas for a given project, folder, or organization. Expected request structure: 'parent' (string, format: projects/{project}/locations/global/services/{service}, folders/{folder}/locations/global/services/{service}, or organizations/{organization}/locations/global/services/{service}), 'page\_size' is automatically fixed to 100. |
| `[create_quota_increase_request](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/create_quota_increase_request)` | Creates a new QuotaPreference that declares the desired value for a quota (initiating a Quota Increase Request). Expected request structure: 'quota\_preference.name' (string, format: projects/{project}/locations/global/quotaPreferences/{id}), 'quota\_preference.service' (string, e.g. compute.googleapis.com), 'quota\_preference.quota\_id' (string, e.g. CpusPerProjectPerRegion), 'quota\_preference.quota\_config.preferred\_value' (int64), 'quota\_preference.contact\_email' (string), and 'quota\_preference.justification' (string). |
| `[list_quota_preferences](https://docs.cloud.google.com/docs/quotas/reference/mcp/tools_list/list_quota_preferences)` | Lists QuotaPreferences of all quotas for a given project, folder, or organization. Expected request structure: 'parent' (string, format: projects/{project}/locations/global, folders/{folder}/locations/global, or organizations/{organization}/locations/global) and 'filter' (string, optional, e.g. 'service="compute.googleapis.com" AND quotaId="CPUS-per-project-region"'). Pagination is automatically fixed to 100 items per page. |

### Get MCP tool specifications

To get the MCP tool specifications for all tools in an MCP server, use the `tools/list` method. The following example demonstrates how to use `curl` to list all tools and their specifications currently available within the MCP server.

| Curl Request |
| --- |
|                       
curl \--location 'https://cloudquotas.googleapis.com/mcp' \\
--header 'content-type: application/json' \\
--header 'accept: application/json, text/event-stream' \\
--data '{
    "method": "tools/list",
    "jsonrpc": "2.0",
    "id": 1
}'
                     |
