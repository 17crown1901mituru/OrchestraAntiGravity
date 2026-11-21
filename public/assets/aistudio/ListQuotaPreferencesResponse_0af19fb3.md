# ListQuotaPreferencesResponse &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/ListQuotaPreferencesResponse

ListQuotaPreferencesResponse
----------------------------

 

*   [JSON representation](#SCHEMA_REPRESENTATION)

Message for response to listing QuotaPreferences

| JSON representation |
| --- |
| 
{
  "quotaPreferences": \[
    {
      object (`[QuotaPreference](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences#QuotaPreference)`)
    }
  \],
  "nextPageToken": string,
  "unreachable": \[
    string
  \]
}

 |

 
| Fields |
| --- |
| `quotaPreferences[]` | 
``object (`[QuotaPreference](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences#QuotaPreference)`)``

The list of QuotaPreference

 |
| `nextPageToken` | 

`string`

A token, which can be sent as `pageToken` to retrieve the next page. If this field is omitted, there are no subsequent pages.

 |
| `unreachable[]` | 

`string`

Locations that could not be reached.

 |

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2025-05-14 UTC.
