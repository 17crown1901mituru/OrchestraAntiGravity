# FedRAMP and DoD compliance scope for Google Cloud and Google Workspace &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/fedramp-dod-compliance-scope

[Skip to main content](#main-content)

*   [Documentation](https://docs.cloud.google.com/docs)
    *   [Overview](https://docs.cloud.google.com/docs/security)
    *   [Guides](https://docs.cloud.google.com/docs/security/overview/whitepaper)
*   [Console](https://console.cloud.google.com/)

*   Security whitepapers
    
*   [Google security overview](https://docs.cloud.google.com/docs/security/overview/whitepaper)
*   [Privileged access in Google Cloud](https://docs.cloud.google.com/docs/security/privileged-access-management)
*   Infrastructure security
    
    *   [Infrastructure security design overview](https://docs.cloud.google.com/docs/security/infrastructure/design)
    *   [BeyondProd](https://docs.cloud.google.com/docs/security/beyondprod)
    *   [Binary Authorization for Borg](https://docs.cloud.google.com/docs/security/binary-authorization-for-borg)
    *   [Boot integrity](https://docs.cloud.google.com/docs/security/boot-integrity)
    *   [Data center physical-to-logical space](https://docs.cloud.google.com/docs/security/physical-to-logical-space)
    *   [Remote attestation](https://docs.cloud.google.com/docs/security/remote-attestation)
    *   [Production service protections](https://docs.cloud.google.com/docs/security/production-services-protection)
    *   [Titanium hardware security architecture](https://docs.cloud.google.com/docs/security/titanium-hardware-security-architecture)
    *   [Titan hardware chip](https://docs.cloud.google.com/docs/security/titan-hardware-chip)
    

*   Incident management
    

*   Security guidelines
    

*   [Security best practices for generative AI](https://docs.cloud.google.com/docs/security/genai-security-bps)
*   [Security best practices for GKE](https://docs.cloud.google.com/docs/security/gke-security-bps)
*   [Security best practices for VMware Engine](https://docs.cloud.google.com/docs/security/vmware-engine-security-bps)
*   [Vulnerability management with AI](https://docs.cloud.google.com/docs/security/vuln-management-ai)
*   [Mitigate OWASP Top 10:2025 on Google Cloud](https://docs.cloud.google.com/docs/security/owasp-top-ten-mitigation)
*   Regulatory compliance
    
*   [DoD compliance scope for Google Distributed Cloud](https://docs.cloud.google.com/docs/security/compliance/il6-gdc-compliance-scope)
*   FedRAMP
    
    *   [FedRAMP overview](https://docs.cloud.google.com/docs/security/compliance/fedramp-trust-center)
    *   [Google Cloud FedRAMP implementation guide](https://docs.cloud.google.com/docs/security/compliance/fedramp-implementation-guide)
    *   [FedRAMP and DoD compliance scope for Google Cloud and Google Workspace](https://docs.cloud.google.com/docs/security/compliance/fedramp-dod-compliance-scope)
    *   [Configure Gemini for Government for FedRAMP 20x Class B](https://docs.cloud.google.com/docs/security/compliance/config-fedramp20x-gemini-gov)
    *   [Configure Gemini for Government for FedRAMP High and DOD IL4](https://docs.cloud.google.com/docs/security/compliance/deploy-gemini-gov)
    
*   [Deploy the Antigravity App and CLI for US public sector](https://docs.cloud.google.com/docs/security/compliance/deploy-antigravity-us-public-sector)

*   [HIPAA compliance for Google Cloud and Google Workspace](https://docs.cloud.google.com/docs/security/compliance/hipaa)

*   Security products
    

*   Data security
    
    *   [Cloud Key Management Service](https://docs.cloud.google.com/kms/docs)
    *   [Confidential Computing](https://docs.cloud.google.com/confidential-computing/docs)
    *   [Secret Manager](https://docs.cloud.google.com/secret-manager/docs)
    *   [Sensitive Data Protection](https://docs.cloud.google.com/sensitive-data-protection/docs)
    *   [See additional products on overview page](https://docs.cloud.google.com/docs/security#expandable-6)
    

FedRAMP and DoD compliance scope for Google Cloud and Google Workspace
----------------------------------------------------------------------

Last reviewed 2026-10-21 UTC

[Google Cloud](https://cloud.google.com/why-google-cloud) and [Google Workspace](https://workspace.google.com/) services meet demanding US government compliance requirements that are verified through formal third-party authorizations, including the following:

*   [Federal Risk and Authorization Management Program (FedRAMP)](https://www.fedramp.gov/) High provisional authorization to operate (P-ATO).
*   Department of Defense (DoD) Cloud Computing [Security Requirements Guide (SRG)](https://www.cyber.mil/dccs/dccs-documents/) Impact Level 2 (IL2) and Impact Level 4 (IL4) provisional authorizations (PA) issued by the Defense Information Systems Agency (DISA).
*   Moreover, Google Cloud also maintains a DoD IL5 PA issued by DISA.

For more information, see the Google [FedRAMP](https://cloud.google.com/security/compliance/fedramp) and [DoD](https://cloud.google.com/security/compliance/disa) compliance offerings pages.

This document provides a detailed list of Google Cloud and Google Workspace services in scope for FedRAMP High, DoD IL2, DoD IL4, and DoD IL5 provisional authorizations. You can use all Google Cloud and Google Workspace services that are FedRAMP High authorized for FedRAMP Moderate deployments.

This document also tracks the Google Cloud regions that are suitable for FedRAMP Moderate, FedRAMP High, DoD IL2, DoD IL4, and DoD IL5 deployments.

Terminology
-----------

This document uses the following terminology:

*   **FedRAMP High**: FedRAMP High P-ATO
*   **FedRAMP Moderate**: FedRAMP Moderate deployment for a cloud region based on inheritance from a FedRAMP High P-ATO
*   **DoD IL2**: DoD SRG Impact Level 2 PA
*   **DoD IL4**: DoD SRG Impact Level 4 PA
*   **DoD IL5**: DoD SRG Impact Level 5 PA
*   **GSA review**: Service was submitted for FedRAMP authorization and is waiting for GSA approval
*   **DISA review**: Service was submitted for DoD authorization and is waiting for DISA approval
*   : Service or cloud region is authorized

Google Cloud services by audit scope
------------------------------------

_Last updated: October 2026_

Google's authorization strategy focuses on the top-level cloud services.

A component service or feature of an authorized top-level cloud service might not be authorized yet, in which case it's explicitly excluded from the authorization scope. Conversely, a commonly used feature of an authorized cloud service might be explicitly called out as authorized for emphasis and to proactively address questions about the authorization status for commonly used Google Cloud services. You can use all Google Cloud services that are FedRAMP High authorized for FedRAMP Moderate deployments.

This document also tracks changes to authorization status resulting from service rebranding, restructuring, and deprecation. Typically, authorization status is no longer tracked for deprecated services that are shut down and unavailable for provisioning.

Google Workspace services by audit scope
----------------------------------------

_Last updated: January 2026_

To comply with FedRAMP High and DoD IL4 requirements, you must use one of the following Google Workspace editions:

*   [Google Workspace Enterprise Plus](https://workspace.google.com/pricing.html)
*   [Frontline Plus](https://knowledge.workspace.google.com/admin/getting-started/editions/frontline-editions#frontline_plus)
*   [Business Continuity](https://knowledge.workspace.google.com/business-continuity/overview)
*   [Business Continuity Plus](https://knowledge.workspace.google.com/business-continuity/overview)

Note that not all services that are available within these editions are FedRAMP or IL authorized. To configure your domains to support your FedRAMP High or IL4 compliance requirements, follow the [Google Workspace FedRAMP configuration guide](https://knowledge.workspace.google.com/admin/compliance/google-workspace-fedramp-configuration-guide) or [Google Workspace IL4 configuration guide](https://knowledge.workspace.google.com/admin/compliance/google-workspace-il4-configuration-guide).

  

| Service | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
| --- | --- | --- | --- | --- |
| 
[AppSheet](https://about.appsheet.com/home/) (excluding [Tables](https://support.google.com/appsheet/answer/10105821))

 |  |  |  |  |
| 

[Contacts](https://support.google.com/a/users/answer/9310345)

 |  |  |  |  |
| 

[Drawings](https://support.google.com/docs/answer/179740)

 |  |  | 

DISA review

 |  |
| 

[Gemini app](https://support.google.com/a/answer/14571493#what)

 |  |  |  |  |
| 

[Gmail](https://workspace.google.com/products/gmail/)

 |  |  |  | 

DISA review

 |
| 

[Google Admin console: Admin SDK](https://developers.google.com/workspace/admin/reference-overview) (including Admin Settings API, Apps Email Audit API, Calendar Resource API, Directory API (which replaced Provisioning API), Domain Shared Contacts API, Email Settings API, Enterprise License Manager API, Groups Migration API, Groups Settings API, Reports API, and Reseller API)

 |  |  |  |  |
| 

[Google Admin console: Android and Chrome device management](https://support.google.com/chrome/a/answer/9490493)

 |  |  |  |  |
| 

[Google Calendar](https://workspace.google.com/products/calendar/)

 |  |  |  | 

DISA review

 |
| 

[Google Chat](https://workspace.google.com/products/chat/)

 |  |  |  |  |
| 

[Google Chrome sync](https://support.google.com/chrome/a/answer/6309115) (Google Workspace for Education domains only)

 |  |  |  |  |
| 

[Google Classroom](https://edu.google.com/intl/ALL_us/workspace-for-education/products/classroom/)

 |  |  |  |  |
| 

[Google Cloud Search](https://knowledge.workspace.google.com/cloud-search/what-is-google-cloud-search)

 |  |  |  |  |
| Service | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
| 

[Google Docs](https://workspace.google.com/products/docs/)

 |  |  |  | 

DISA review

 |
| 

[Google Drive](https://workspace.google.com/products/drive/)

 |  |  |  | 

DISA review

 |
| 

[Google Forms](https://workspace.google.com/products/forms/)

 |  |  |  |  |
| 

[Google Keep](https://workspace.google.com/products/keep/)

 |  |  |  |  |
| 

[Google Meet](https://workspace.google.com/products/meet/)

 |  |  |  | 

DISA review

 |
| 

      [Google Meet: Live stream](https://support.google.com/meet/answer/9308630)

 |  |  |  |  |
| 

[Google Sheets](https://workspace.google.com/products/sheets/)

 |  |  |  | 

DISA review

 |
| 

[Google Sites Classic](https://developers.google.com/workspace/sites) (replaced by Google Sites New)

 |  |  |  |  |
| 

[Google Sites New](https://workspace.google.com/products/sites/)

 |  |  | 

DISA review

 |  |
| 

[Google Slides](https://workspace.google.com/products/slides/)

 |  |  |  | 

DISA review

 |
| 

[Google Tasks](https://workspace.google.com/products/tasks/)

 |  |  |  |  |
| 

[Google Vault](https://workspace.google.com/products/vault/)

 |  |  |  |  |
| 

[Google Vids](https://workspace.google.com/products/vids/)

 |  |  |  |  |
| 

[Google Voice](https://workspace.google.com/products/voice/)

 |  |  |  |  |
| Service | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
| 

[Google Workspace Security Center](https://support.google.com/a/answer/7492003)

(for Google Workspace Enterprise and Google Workspace Enterprise for Education domains only) |  |  |  |  |
| 

[Google Workspace with Gemini](https://support.google.com/a/answer/13623623)

 |  |  |  |  |
| 

      [Gemini in Chat](https://support.google.com/chat/answer/15395284)

 |  |  |  |  |
| 

      [Gemini in Docs](https://workspace.google.com/products/docs/ai/)

 |  |  |  |  |
| 

      [Gemini in Drive](https://support.google.com/drive/answer/14217860)

 |  |  |  |  |
| 

      [Gemini in Gmail](https://workspace.google.com/products/gmail/ai/)

 |  |  |  |  |
| 

      [Gemini in Meet](https://support.google.com/meet/answer/14441737)

 |  |  |  |  |
| 

             Generative images in Meet backgrounds

 |  |  |  |  |
| 

             Live translated captions

 |  |  |  |  |
| 

             Studio look

 |  |  |  |  |
| 

      [Gemini in Sheets](https://workspace.google.com/resources/spreadsheet-ai/)

 |  |  |  |  |
| 

      [Gemini in Slides](https://workspace.google.com/resources/presentation-ai/)

 |  |  |  |  |
| 

[Secure LDAP](https://support.google.com/a/answer/9048516)

 |  |  |  |  |

_Last updated: October 2026_

Google Cloud regions hold FedRAMP and Department of Defense (DoD) provisional authorizations (PAs). The suitability of these regions depends on the following security impact levels (IL):

*   For an IL2 PA, DoD grants full reciprocity with a FedRAMP Moderate or High P-ATO.
*   Only US-based cloud regions are appropriate for FedRAMP High, DoD IL2, DoD IL4, and DoD IL5 deployments.
*   You can use any US-based cloud region that's approved for FedRAMP High for FedRAMP Moderate deployments.
*   Non-US cloud regions are only suitable for FedRAMP Moderate deployments, which can rely on the FedRAMP High P-ATO.

  

| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 
North America

 | 

Arcola, VA, USA

 | 

`us-east4`

 |  |  |  |  |  |
|  | 

Ashburn, VA, USA

 | 

`us-east4`

 |  |  |  |  |  |
|  | 

Atlanta, GA, USA

 | 

`us-east2`

 |  |  |  |  |  |
|  | 

Austin, TX, USA

 | 

`us-south1`

 |  |  |  |  |  |
|  | 

Chandler, AZ, USA

 | 

`us-west8`

 |  |  |  |  |  |
|  | 

Clarksville, TN, USA

 | 

`us-east7`

 |  |  |  |  |  |
|  | 

Columbus, OH, USA

 | 

`us-east5`

 |  |  |  |  |  |
|  | 

Council Bluffs, IA, USA

 | 

`us-central1`

 |  |  |  |  |  |
|  | 

Fort Wayne, IN, USA

 | 

`us-east5`

 |  |  |  |  |  |
|  | 

Gainesville, VA, USA

 | 

`us-east4`

 |  |  |  |  |  |
|  | 

Guanajuato, Mexico

 | 

`northamerica-south1`

 |  |  |  |  |  |
|  | 

Henderson, NV, USA

 | 

`us-west4`

 |  |  |  |  |  |
| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
|  | 

Hockley, TX, USA

 | 

`us-south1`

 |  |  |  |  |  |
|  | 

Las Vegas, NV, USA

 | 

`us-west4`

 |  |  |  |  |  |
|  | 

Lancaster, OH, USA

 | 

`us-east5`

 |  |  |  |  |  |
|  | 

Leesburg, VA, USA

 | 

`us-east4`

 |  |  |  |  |  |
|  | 

Lenoir, NC, USA

 | 

`us-east10`

 |  |  |  |  |  |
|  | 

Lincoln, NE, USA

 | 

`us-central1`

 |  |  |  |  |  |
|  | 

Los Angeles, CA, USA

 | 

`us-west2`

 |  |  |  |  |  |
|  | 

Markham, Ontario, Canada

 | 

`northamerica-northeast2`

 |  |  |  |  |  |
|  | 

Midlothian (Dallas), TX, USA

 | 

`us-south1`

 |  |  |  |  |  |
|  | 

Moncks Corner, SC, USA

 | 

`us-east1`

 |  |  |  |  |  |
|  | 

Montreal, Québec, Canada

 | 

`northamerica-northeast1`

 |  |  |  |  |  |
|  | 

New Albany, OH, USA

 | 

`us-east5`

 |  |  |  |  |  |
| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
|  | 

Omaha, NE, USA

 | 

`us-central1`

 |  |  |  |  |  |
|  | 

Papillion, NE, USA

 | 

`us-central1`

 |  |  |  |  |  |
|  | 

Phoenix, AZ, USA

 | 

`us-west8`

 |  |  |  |  |  |
|  | 

Pryor Creek, OK, USA

 | 

`us-central2`

 |  |  |  |  |  |
|  | 

Reno, NV, USA

 | 

`us-west5`

 |  |  |  |  |  |
|  | 

Saint-Laurent, Québec, Canada

 | 

`northamerica-northeast1`

 |  |  |  |  |  |
|  | 

Salt Lake City, UT, USA

 | 

`us-west3`

 |  |  |  |  |  |
|  | 

The Dalles, OR, USA

 | 

`us-west1`

 |  |  |  |  |  |
|  | 

Toronto, Ontario, Canada

 | 

`northamerica-northeast2`

 |  |  |  |  |  |
|  | 

Widows Creek, AL, USA

 | 

`us-east7`

 |  |  |  |  |  |
| 

South America

 | 

Osasco, São Paulo, Brazil

 | 

`southamerica-east1`

 |  |  |  |  |  |
|  | 

Santana de Parnaíba, São Paulo, Brazil

 | 

`southamerica-east1`

 |  |  |  |  |  |
| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
|  | 

Vinhedo, São Paulo, Brazil

 | 

`southamerica-east1`

 |  |  |  |  |  |
|  | 

Quilicura, Santiago, Chile

 | 

`southamerica-west1`

 |  |  |  |  |  |
|  | 

San Bernardo, Santiago, Chile

 | 

`southamerica-west1`

 |  |  |  |  |  |
| 

Europe

 | 

Berlin, Germany

 | 

`europe-west10`

 |  |  |  |  |  |
|  | 

Dalarna, Sweden

 | 

`europe-north2`

 |  |  |  |  |  |
|  | 

Eemshaven, Netherlands

 | 

`europe-west4`

 |  |  |  |  |  |
|  | 

Frankfurt, Hesse, Germany

 | 

`europe-west3`

 |  |  |  |  |  |
|  | 

Ghlin, Hainaut, Belgium

 | 

`europe-west1`

 |  |  |  |  |  |
|  | 

Hamina, Finland

 | 

`europe-north1`

 |  |  |  |  |  |
|  | 

London, United Kingdom

 | 

`europe-west2`

 |  |  |  |  |  |
|  | 

Madrid, Spain

 | 

`europe-southwest1`

 |  |  |  |  |  |
| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
|  | 

Middenmeer, Netherlands

 | 

`europe-west11`

 |  |  |  |  |  |
|  | 

Milan, Italy

 | 

`europe-west8`

 |  |  |  |  |  |
|  | 

Oslo, Norway

 | 

`europe-north3`

 |  |  |  |  |  |
|  | 

Paris, France

 | 

`europe-west9`

 |  |  |  |  |  |
|  | 

Turin, Italy

 | 

`europe-west12`

 |  |  |  |  |  |
|  | 

Warsaw, Poland

 | 

`europe-central2`

 |  |  |  |  |  |
|  | 

Zurich, Switzerland

 | 

`europe-west6`

 |  |  |  |  |  |
| 

Middle East and Africa

 | 

Dammam, Saudi Arabia

 | 

`me-central2`

 |  |  |  |  |  |
|  | 

Doha, Qatar

 | 

`me-central1`

 |  |  |  |  |  |
|  | 

Johannesburg, South Africa

 | 

`africa-south1`

 |  |  |  |  |  |
|  | 

Tel Aviv, Israel

 | 

`me-west1`

 |  |  |  |  |  |
| 

Asia Pacific

 | 

Bangkok, Thailand

 | 

`asia-southeast3`

 |  |  |  |  |  |
| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
|  | 

Changhua, Taiwan

 | 

`asia-east1`

 |  |  |  |  |  |
|  | 

Delhi, India

 | 

`asia-south2`

 |  |  |  |  |  |
|  | 

Inzai City, Chiba, Japan

 | 

`asia-northeast1`

 |  |  |  |  |  |
|  | 

Jakarta, Indonesia

 | 

`asia-southeast2`

 |  |  |  |  |  |
|  | 

Kōtō-ku, Tokyo, Japan

 | 

`asia-northeast1`

 |  |  |  |  |  |
|  | 

Lok Yang Way, Singapore

 | 

`asia-southeast1`

 |  |  |  |  |  |
|  | 

Loyang, Singapore

 | 

`asia-southeast1`

 |  |  |  |  |  |
|  | 

Melbourne, Victoria, Australia

 | 

`australia-southeast2`

 |  |  |  |  |  |
|  | 

Mumbai, India

 | 

`asia-south1`

 |  |  |  |  |  |
|  | 

Osaka, Japan

 | 

`asia-northeast2`

 |  |  |  |  |  |
|  | 

Seoul, South Korea

 | 

`asia-northeast3`

 |  |  |  |  |  |
|  | 

Sydney, NSW, Australia

 | 

`australia-southeast1`

 |  |  |  |  |  |
| Geographic area | Location | Cloud region | FedRAMP Moderate | FedRAMP High | DoD IL2 | DoD IL4 | DoD IL5 |
|  | 

Wenya, Singapore

 | 

`asia-southeast1`

 |  |  |  |  |  |

What's next
-----------

*   [Deployment guidance for Gemini for Government](https://docs.cloud.google.com/docs/security/compliance/deploy-gemini-gov)
*   [FedRAMP implementation guidance on Google Cloud](https://docs.cloud.google.com/docs/security/compliance/fedramp-implementation-guide)
*   [FedRAMP compliance offering](https://cloud.google.com/security/compliance/fedramp)
*   [DoD compliance offering](https://cloud.google.com/security/compliance/disa)
*   [Meet regulatory, compliance, and privacy needs](https://docs.cloud.google.com/architecture/framework/security/meet-regulatory-compliance-and-privacy-needs)
*   [Google Cloud trust center](https://cloud.google.com/trust-center)
*   [Google Cloud compliance resource center](https://cloud.google.com/compliance)

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-10-21 UTC.
