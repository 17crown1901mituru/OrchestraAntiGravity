# Create a proof of concept foundation &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/enterprise/proof-of-concept

Create a proof of concept foundation
------------------------------------

A foundation includes fundamental settings that help you organize, manage, and maintain Google Cloud resources. Create a **Proof of concept** foundation to support experimental workloads with basic security in mind. This option guides you through the Organization, Billing, and Resource Hierarchy tasks.

Configure your foundation
-------------------------

To create a proof of concept foundation, do the following:

1.  Complete the [Organization](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-1) task.
    
    Configure an identity provider, verify your domain, and generate your organization.
    
2.  Sign in to the console as the super administrator user you created in the [Organization](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-1) task.
    
3.  Select the **Proof of concept** [foundation option](https://docs.cloud.google.com/docs/enterprise/cloud-setup#select-foundation-option).
    
4.  Make sure the organization you created is selected, and click **Continue to Billing**.
    
    The following groups are required to complete the remaining steps. The groups are created, and you are added as a member of each group.
    
    *   `gcp-organization-admins`
    *   `gcp-billing-admins`
5.  Select or create a billing account. For more information, see the [Billing](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-4) task.
    
6.  Click **Continue to Review and Deploy Foundation**.
    
7.  From the **Review and deploy your configuration** screen, review the following draft configurations:
    
    *   **Resource hierarchy**: Review the folder and projects.
        
    *   **Organization policies**: Review the list of recommended organization policies. For more information, see [Apply recommended organization policies](https://docs.cloud.google.com/docs/enterprise/advanced-production#apply-recommended-organization-policies).
        
8.  Deploy your foundation.
    
    For more information on deployment options, see [Deploy your configuration](https://docs.cloud.google.com/docs/enterprise/advanced-production#deploy).
    
9.  To enable billing on the management project, see [Link a billing account to your management project](https://docs.cloud.google.com/resource-manager/docs/manage-applications#billing).
    

What's next
-----------

For information on experimenting and building, see [Build your Google Cloud architecture](https://docs.cloud.google.com/docs/enterprise/manage-foundation#startbuilding).

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
