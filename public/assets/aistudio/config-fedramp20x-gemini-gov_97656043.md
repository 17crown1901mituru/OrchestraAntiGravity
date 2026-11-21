# Configure Gemini for Government for FedRAMP 20x Class B (Low) &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/config-fedramp20x-gemini-gov

Last reviewed 2026-06-11 UTC

This page describes how government agencies can deploy [Gemini for Government](https://cloud.google.com/blog/topics/public-sector/introducing-gemini-for-government-supporting-the-us-governments-transformation-with-ai) under [FedRAMP 20x](https://www.fedramp.gov/20x/) Class B (Low) criteria. The FedRAMP 20x Class B (Low) authorization covers publicly releasable information with minimal sensitivity. If your agency requires processing data at a FedRAMP Moderate or High level, see [Deployment guidance for Gemini for Government](https://docs.cloud.google.com/docs/security/compliance/deploy-gemini-gov).

To request access to detailed [Gemini for Government FedRAMP 20x Low](https://www.fedramp.gov/marketplace/products/FR2604952026/) documentation, email [fedramp@google.com](mailto:fedramp@google.com) and state your interest in obtaining the package materials.

FedRAMP 20x Low certified services
----------------------------------

The following services are listed as certified within the Gemini for Government FedRAMP 20x Low boundary:

*   Gemini Enterprise
*   Generative AI on Gemini Enterprise Agent Platform (formerly Generative AI on Vertex AI)
*   BigQuery
*   Cloud Storage
*   Looker (Google Cloud core)
*   Conversational Agents
*   Gemini Code Assist
*   NotebookLM Enterprise
*   Agent Search on Gemini Enterprise Agent Platform (formerly Agent Search)

This certification level is intended for unclassified information with minimal sensitivity.

FedRAMP 20x Low certified services and features
-----------------------------------------------

The following table lists the services and features that you can use within Gemini for Government for FedRAMP 20x Class B (Low).

<table><tbody><tr><th>Models</th><td><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/google-models#generally_available_models">Generally available Gemini models</a></td></tr><tr><th>Agents and galleries</th><td><ul><li><a href="https://docs.cloud.google.com/vertex-ai/generative-ai/docs/prompt-gallery">Prompt gallery</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/idea-generation">Idea Generation agent</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/research-assistant">Deep Research agent</a></li><li>No-code agent creation using <a href="https://docs.cloud.google.com/gemini/enterprise/docs/agent-designer">Agent Designer</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/agent-gallery">Agent Gallery</a></li></ul></td></tr><tr><th>Analytics and dependent features</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/view-analytics">View analytics data</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/conversational-analytics-api/data-agent-authored-context-looker">Data agent context for Looker data sources</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/conversational-analytics-api/data-agent-authored-context-bq">Data agent context for BigQuery data sources</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/conversational-analytics-api/overview#build-and-interact-with-a-data-agent">Data agents that you create</a> using the Conversational Analytics API</li><li><a href="https://docs.cloud.google.com/bigquery/docs/gemini-overview">Gemini in BigQuery</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/looker/overview">Gemini in Looker</a></li></ul></td></tr><tr><th>Data connectors and stores</th><td><ul><li>FedRAMP Authorized data stores such as <a href="https://docs.cloud.google.com/gemini/enterprise/docs/connect-cloud-storage">Cloud Storage</a> and <a href="https://docs.cloud.google.com/gemini/enterprise/docs/connect-bigquery">BigQuery</a></li><li>Google Workspace data sources such as Google Drive, Gmail, or Google Calendar</li><li>Google Cloud data stores such as Cloud SQL and Firestore</li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/assistant-actions-google">Google Calendar and Gmail actions</a></li><li>Third-party data sources such as Salesforce or Microsoft</li><li>People data sources such as <a href="https://docs.cloud.google.com/gemini/enterprise/docs/connect-people">Google Workspace</a>, <a href="https://docs.cloud.google.com/gemini/enterprise/docs/connect-entra-id">Microsoft Entra</a>, or <a href="https://docs.cloud.google.com/gemini/enterprise/docs/connect-people-custom">custom sources</a></li></ul></td></tr><tr><th>Grounding</th><td><ul><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/grounding/web-grounding-enterprise">Web Grounding for Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/grounding/grounding-with-google-maps">Grounding with Google Maps Platform</a></li><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/grounding/grounding-with-google-search">Grounding with Google Search</a></li><li>Grounding using Google Drive uploads</li><li>Grounding using Microsoft OneDrive uploads</li></ul></td></tr><tr><th>Generative features</th><td><ul><li><a href="https://ai.google.dev/gemini-api/docs/imagen">Generate images using Imagen</a></li><li><a href="https://ai.google.dev/gemini-api/docs/video">Generate video with Veo</a></li></ul></td></tr><tr><th>User, session, and UI features</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/configure-autocomplete">Autocomplete</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/configure-serving-controls">Serving controls</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/assistant-chat#upload_and_chat_about_files">Uploading documents</a> from local machines</li><li>Ability for end users to <a href="https://docs.cloud.google.com/gemini/enterprise/docs/answer-generation-models">select models</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/user-events">User events for Gemini Enterprise apps</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/configure-personalization">Personalization and memory in Gemini Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/configure-prompt-chips">Prompt chips</a></li></ul></td></tr><tr><th>Other features</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/use-knowledge-graph-search">Private Knowledge Graph in Gemini Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/notebooklm-enterprise/docs/overview">NotebookLM Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/context-cache/context-cache-overview">Implicit context caching</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/codeassist/overview">Gemini Code Assist</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/enable-model-armor">Model Armor</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/assistant-analyze#generate_an_image">Image generation using Nano Banana</a></li></ul></td></tr></tbody></table>

Configure your environment
--------------------------

Complete the following tasks within the Google Cloud organization of your agency.

1.  Create a [Google Cloud project](https://docs.cloud.google.com/gemini/enterprise/docs/before-you-begin) to host your Gemini Enterprise data stores and applications.
2.  Configure your APIs and solutions in [Gemini Enterprise](https://docs.cloud.google.com/gemini/enterprise/docs). Your subscription settings automatically enable Gemini for Government. Gemini for Government includes the Gemini Enterprise Standard Edition and prevents unexpected costs by stopping requests after a quota is reached, rather than billing for overages. You can manage these settings within the subscriptions console.
3.  Configure security features for Gemini for Government as described in the [Gemini Enterprise security overview](https://docs.cloud.google.com/gemini/enterprise/docs/security-overview). To help ensure compliance with FedRAMP 20x, configure the following based on your agency's policies:
    
    *   [Identity and Access Management (IAM)](https://docs.cloud.google.com/gemini/enterprise/docs/access-control)
    *   [Gemini Enterprise data security](https://docs.cloud.google.com/gemini/enterprise/docs/security-overview#data-security), particularly VPC Service Controls and customer managed encryption keys (CMEK)
    *   [Gemini Enterprise compliance](https://docs.cloud.google.com/gemini/enterprise/docs/security-overview#compliance), particularly access transparency and audit logging
4.  Optionally, review and apply additional security best practices for Google Cloud:
    
    *   [Google security overview](https://docs.cloud.google.com/docs/security/overview/whitepaper)
    *   [General best practices](https://docs.cloud.google.com/architecture/hybrid-multicloud-secure-networking-patterns/general-best-practices)
    *   [FedRAMP implementation guidance on Google Cloud](https://docs.cloud.google.com/docs/security/compliance/fedramp-implementation-guide)

Manage your compliance for FedRAMP 20x
--------------------------------------

To maintain your FedRAMP 20x Class B (Low) compliance posture for Gemini for Government, you can use [Compliance Manager](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-overview). Compliance Manager includes a [FedRAMP Low 20x framework](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-frameworks#fedramp-low-20x) that you can deploy in your environment and use for auditing.

1.  In the console, select the United States regional toggle, and adjust the view to **Global**. Locate the relevant domain address and select **Configure** to proceed.
2.  Activate [Security Command Center Premium](https://docs.cloud.google.com/security-command-center/docs/activate-premium-tier) to enable Compliance Manager.
3.  In Compliance Manager, deploy the FedRAMP 20x framework. For instructions, see [Deploy a framework](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-apply-framework#deploy-framework). Deploying the framework lets you [monitor for compliance](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-monitor-framework).
4.  Audit the FedRAMP 20x framework to compile compliance evidence. For instructions, see [Audit your environment with Compliance Manager](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-audit). An audit can take several hours to run.
    
    **Note**: The artifacts produced during an audit are provided to support your own audit preparation and process. They aren't guaranteed to satisfy all auditor or regulatory requirements. It is your responsibility to collaborate with your auditors to ensure compliance.
    
5.  Review the detailed audit assessments against each resource and rule. Optionally, download the evidence to support your FedRAMP package submission.
