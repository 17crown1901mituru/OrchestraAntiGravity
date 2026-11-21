# Cloud Quotas API &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/reference/rest?hl=fr

Cloud Quotas API provides Google Cloud service consumers with management and observability for resource usage, quotas, and restrictions of the services they consume.

*   [REST Resource: v1beta.folders.locations.quotaAdjusterSettings](#v1beta.folders.locations.quotaAdjusterSettings)
*   [REST Resource: v1beta.folders.locations.quotaPreferences](#v1beta.folders.locations.quotaPreferences)
*   [REST Resource: v1beta.folders.locations.services.quotaInfos](#v1beta.folders.locations.services.quotaInfos)
*   [REST Resource: v1beta.organizations.locations.quotaAdjusterSettings](#v1beta.organizations.locations.quotaAdjusterSettings)
*   [REST Resource: v1beta.organizations.locations.quotaPreferences](#v1beta.organizations.locations.quotaPreferences)
*   [REST Resource: v1beta.organizations.locations.services.quotaInfos](#v1beta.organizations.locations.services.quotaInfos)
*   [REST Resource: v1beta.projects.locations.quotaAdjusterSettings](#v1beta.projects.locations.quotaAdjusterSettings)
*   [REST Resource: v1beta.projects.locations.quotaPreferences](#v1beta.projects.locations.quotaPreferences)
*   [REST Resource: v1beta.projects.locations.services.quotaInfos](#v1beta.projects.locations.services.quotaInfos)
*   [REST Resource: v1.folders.locations.quotaAdjusterSettings](#v1.folders.locations.quotaAdjusterSettings)
*   [REST Resource: v1.folders.locations.quotaPreferences](#v1.folders.locations.quotaPreferences)
*   [REST Resource: v1.folders.locations.services.quotaInfos](#v1.folders.locations.services.quotaInfos)
*   [REST Resource: v1.organizations.locations.quotaAdjusterSettings](#v1.organizations.locations.quotaAdjusterSettings)
*   [REST Resource: v1.organizations.locations.quotaPreferences](#v1.organizations.locations.quotaPreferences)
*   [REST Resource: v1.organizations.locations.services.quotaInfos](#v1.organizations.locations.services.quotaInfos)
*   [REST Resource: v1.projects.locations.quotaAdjusterSettings](#v1.projects.locations.quotaAdjusterSettings)
*   [REST Resource: v1.projects.locations.quotaPreferences](#v1.projects.locations.quotaPreferences)
*   [REST Resource: v1.projects.locations.services.quotaInfos](#v1.projects.locations.services.quotaInfos)

Service: cloudquotas.googleapis.com
-----------------------------------

To call this service, we recommend that you use the Google-provided [client libraries](https://cloud.google.com/apis/docs/client-libraries-explained). If your application needs to use your own libraries to call this service, use the following information when you make the API requests.

### Discovery document

A [Discovery Document](https://developers.google.com/discovery/v1/reference/apis) is a machine-readable specification for describing and consuming REST APIs. It is used to build client libraries, IDE plugins, and other tools that interact with Google APIs. One service may provide multiple discovery documents. This service provides the following discovery documents:

*   [https://cloudquotas.googleapis.com/$discovery/rest?version=v1](https://cloudquotas.googleapis.com/$discovery/rest?version=v1)
*   [https://cloudquotas.googleapis.com/$discovery/rest?version=v1beta](https://cloudquotas.googleapis.com/$discovery/rest?version=v1beta)

### Service endpoint

A [service endpoint](https://cloud.google.com/apis/design/glossary#api_service_endpoint) is a base URL that specifies the network address of an API service. One service might have multiple service endpoints. This service has the following service endpoint and all URIs below are relative to this service endpoint:

*   `https://cloudquotas.googleapis.com`

REST Resource: [v1beta.folders.locations.quotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings)
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)` | `GET /v1beta/{name=folders/*/locations/*/quotaAdjusterSettings}`  
Gets QuotaAdjusterSettings for the specified folder. |
| `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)` | `PATCH /v1beta/{quotaAdjusterSettings.name=folders/*/locations/*/quotaAdjusterSettings}`  
Updates QuotaAdjusterSettings for the specified folder. |

REST Resource: [v1beta.folders.locations.quotaPreferences](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences)
--------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[create](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences/create)` | `POST /v1beta/{parent=folders/*/locations/*}/quotaPreferences`  
Creates a new QuotaPreference that declares the desired value for a quota. |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences/get)` | `GET /v1beta/{name=folders/*/locations/*/quotaPreferences/*}`  
Gets details of a single QuotaPreference. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences/list)` | `GET /v1beta/{parent=folders/*/locations/*}/quotaPreferences`  
Lists QuotaPreferences in a given project, folder or organization. |
| `[patch](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.quotaPreferences/patch)` | `PATCH /v1beta/{quotaPreference.name=folders/*/locations/*/quotaPreferences/*}`  
Updates the parameters of a single QuotaPreference. |

REST Resource: [v1beta.folders.locations.services.quotaInfos](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.services.quotaInfos)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.services.quotaInfos/get)` | `GET /v1beta/{name=folders/*/locations/*/services/*/quotaInfos/*}`  
Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/folders.locations.services.quotaInfos/list)` | `GET /v1beta/{parent=folders/*/locations/*/services/*}/quotaInfos`  
Lists QuotaInfos of all quotas for a given project, folder or organization. |

REST Resource: [v1beta.organizations.locations.quotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaAdjusterSettings)
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)` | `GET /v1beta/{name=organizations/*/locations/*/quotaAdjusterSettings}`  
Gets QuotaAdjusterSettings for the specified organization. |
| `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)` | `PATCH /v1beta/{quotaAdjusterSettings.name=organizations/*/locations/*/quotaAdjusterSettings}`  
Updates QuotaAdjusterSettings for the specified organization. |

REST Resource: [v1beta.organizations.locations.quotaPreferences](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[create](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences/create)` | `POST /v1beta/{parent=organizations/*/locations/*}/quotaPreferences`  
Creates a new QuotaPreference that declares the desired value for a quota. |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences/get)` | `GET /v1beta/{name=organizations/*/locations/*/quotaPreferences/*}`  
Gets details of a single QuotaPreference. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences/list)` | `GET /v1beta/{parent=organizations/*/locations/*}/quotaPreferences`  
Lists QuotaPreferences in a given project, folder or organization. |
| `[patch](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences/patch)` | `PATCH /v1beta/{quotaPreference.name=organizations/*/locations/*/quotaPreferences/*}`  
Updates the parameters of a single QuotaPreference. |

REST Resource: [v1beta.organizations.locations.services.quotaInfos](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.services.quotaInfos)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.services.quotaInfos/get)` | `GET /v1beta/{name=organizations/*/locations/*/services/*/quotaInfos/*}`  
Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.services.quotaInfos/list)` | `GET /v1beta/{parent=organizations/*/locations/*/services/*}/quotaInfos`  
Lists QuotaInfos of all quotas for a given project, folder or organization. |

REST Resource: [v1beta.projects.locations.quotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaAdjusterSettings)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)` | `GET /v1beta/{name=projects/*/locations/*/quotaAdjusterSettings}`  
Gets QuotaAdjusterSettings for the specified project. |
| `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)` | `PATCH /v1beta/{quotaAdjusterSettings.name=projects/*/locations/*/quotaAdjusterSettings}`  
Updates QuotaAdjusterSettings for the specified project. |

REST Resource: [v1beta.projects.locations.quotaPreferences](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaPreferences)
----------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[create](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaPreferences/create)` | `POST /v1beta/{parent=projects/*/locations/*}/quotaPreferences`  
Creates a new QuotaPreference that declares the desired value for a quota. |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaPreferences/get)` | `GET /v1beta/{name=projects/*/locations/*/quotaPreferences/*}`  
Gets details of a single QuotaPreference. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaPreferences/list)` | `GET /v1beta/{parent=projects/*/locations/*}/quotaPreferences`  
Lists QuotaPreferences in a given project, folder or organization. |
| `[patch](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.quotaPreferences/patch)` | `PATCH /v1beta/{quotaPreference.name=projects/*/locations/*/quotaPreferences/*}`  
Updates the parameters of a single QuotaPreference. |

REST Resource: [v1beta.projects.locations.services.quotaInfos](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.services.quotaInfos)
----------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.services.quotaInfos/get)` | `GET /v1beta/{name=projects/*/locations/*/services/*/quotaInfos/*}`  
Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/projects.locations.services.quotaInfos/list)` | `GET /v1beta/{parent=projects/*/locations/*/services/*}/quotaInfos`  
Lists QuotaInfos of all quotas for a given project, folder or organization. |

REST Resource: [v1.folders.locations.quotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaAdjusterSettings)
----------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)` | `GET /v1/{name=folders/*/locations/*/quotaAdjusterSettings}`  
Gets the QuotaAdjusterSettings for the specified resource. |
| `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)` | `PATCH /v1/{quotaAdjusterSettings.name=folders/*/locations/*/quotaAdjusterSettings}`  
Updates the QuotaAdjusterSettings for the specified resource. |

REST Resource: [v1.folders.locations.quotaPreferences](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences)
------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[create](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences/create)` | `POST /v1/{parent=folders/*/locations/*}/quotaPreferences`  
Creates a new QuotaPreference that declares the desired value for a quota. |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences/get)` | `GET /v1/{name=folders/*/locations/*/quotaPreferences/*}`  
Gets details of a single QuotaPreference. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences/list)` | `GET /v1/{parent=folders/*/locations/*}/quotaPreferences`  
Lists QuotaPreferences in a given project, folder or organization. |
| `[patch](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.quotaPreferences/patch)` | `PATCH /v1/{quotaPreference.name=folders/*/locations/*/quotaPreferences/*}`  
Updates the parameters of a single QuotaPreference. |

REST Resource: [v1.folders.locations.services.quotaInfos](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos)
------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos/get)` | `GET /v1/{name=folders/*/locations/*/services/*/quotaInfos/*}`  
Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/folders.locations.services.quotaInfos/list)` | `GET /v1/{parent=folders/*/locations/*/services/*}/quotaInfos`  
Lists QuotaInfos of all quotas for a given project, folder or organization. |

REST Resource: [v1.organizations.locations.quotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaAdjusterSettings)
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)` | `GET /v1/{name=organizations/*/locations/*/quotaAdjusterSettings}`  
Gets the QuotaAdjusterSettings for the specified resource. |
| `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)` | `PATCH /v1/{quotaAdjusterSettings.name=organizations/*/locations/*/quotaAdjusterSettings}`  
Updates the QuotaAdjusterSettings for the specified resource. |

REST Resource: [v1.organizations.locations.quotaPreferences](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaPreferences)
------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[create](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaPreferences/create)` | `POST /v1/{parent=organizations/*/locations/*}/quotaPreferences`  
Creates a new QuotaPreference that declares the desired value for a quota. |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaPreferences/get)` | `GET /v1/{name=organizations/*/locations/*/quotaPreferences/*}`  
Gets details of a single QuotaPreference. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaPreferences/list)` | `GET /v1/{parent=organizations/*/locations/*}/quotaPreferences`  
Lists QuotaPreferences in a given project, folder or organization. |
| `[patch](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.quotaPreferences/patch)` | `PATCH /v1/{quotaPreference.name=organizations/*/locations/*/quotaPreferences/*}`  
Updates the parameters of a single QuotaPreference. |

REST Resource: [v1.organizations.locations.services.quotaInfos](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.services.quotaInfos)
------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.services.quotaInfos/get)` | `GET /v1/{name=organizations/*/locations/*/services/*/quotaInfos/*}`  
Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/organizations.locations.services.quotaInfos/list)` | `GET /v1/{parent=organizations/*/locations/*/services/*}/quotaInfos`  
Lists QuotaInfos of all quotas for a given project, folder or organization. |

REST Resource: [v1.projects.locations.quotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaAdjusterSettings)
------------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[getQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaAdjusterSettings/getQuotaAdjusterSettings)` | `GET /v1/{name=projects/*/locations/*/quotaAdjusterSettings}`  
Gets the QuotaAdjusterSettings for the specified resource. |
| `[updateQuotaAdjusterSettings](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings)` | `PATCH /v1/{quotaAdjusterSettings.name=projects/*/locations/*/quotaAdjusterSettings}`  
Updates the QuotaAdjusterSettings for the specified resource. |

REST Resource: [v1.projects.locations.quotaPreferences](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaPreferences)
--------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[create](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaPreferences/create)` | `POST /v1/{parent=projects/*/locations/*}/quotaPreferences`  
Creates a new QuotaPreference that declares the desired value for a quota. |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaPreferences/get)` | `GET /v1/{name=projects/*/locations/*/quotaPreferences/*}`  
Gets details of a single QuotaPreference. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaPreferences/list)` | `GET /v1/{parent=projects/*/locations/*}/quotaPreferences`  
Lists QuotaPreferences in a given project, folder or organization. |
| `[patch](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaPreferences/patch)` | `PATCH /v1/{quotaPreference.name=projects/*/locations/*/quotaPreferences/*}`  
Updates the parameters of a single QuotaPreference. |

REST Resource: [v1.projects.locations.services.quotaInfos](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.services.quotaInfos)
--------------------------------------------------------------------------------------------------------------------------------------------------------------

 
| Methods |
| --- |
| `[get](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.services.quotaInfos/get)` | `GET /v1/{name=projects/*/locations/*/services/*/quotaInfos/*}`  
Retrieve the QuotaInfo of a quota for a project, folder or organization. |
| `[list](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.services.quotaInfos/list)` | `GET /v1/{parent=projects/*/locations/*/services/*}/quotaInfos`  
Lists QuotaInfos of all quotas for a given project, folder or organization. |
