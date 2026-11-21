# Create a production foundation &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/enterprise/production

A foundation includes fundamental settings that help you organize, manage, and maintain Google Cloud resources. Create a **Production** foundation to support production-ready workloads. This process includes logging, monitoring, and Security Command Center settings.

Configure your foundation
-------------------------

To create a production foundation, do the following:

1.  Complete the [Organization](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-1) task.
    
    Verify your domain and generate your organization.
    
2.  Sign in to the console as the super administrator user you created in the [Organization](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-1) task.
    
3.  Select the **Production** [foundation option](https://docs.cloud.google.com/docs/enterprise/cloud-setup#select-foundation-option).
    
4.  Make sure the organization you created is selected, and click **Continue to Billing**.
    
    1.  Optional: to set up your identity service, click **Set up Identity**.
        
        For more information, see [Configure an identity provider and verify your domain](https://docs.cloud.google.com/docs/enterprise/advanced-production#configure-identity).
        
    2.  Click **Create Groups**.
        
        The following groups are required to complete the remaining steps. The groups are created, and you are added as a member of each group.
        
        *   `gcp-organization-admins`
        *   `gcp-billing-admins`
        *   `gcp-security-admins`
        *   `gcp-logging-monitoring-admins`
        
        If you signed up for a free trial, and did not complete the [Organization](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-1) task, the system might not create groups. Instead, the system assigns your user the roles required to complete the remaining steps.
        
    3.  Select or create a billing account. For more information, see the [Billing](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-4) task.
        
    4.  Click **Continue to Resource Hierarchy**.
        
5.  On the **Choose resource hierarchy** page, do the following:
    
    1.  Select one of the following hierarchies:
        
        *   **Simple, environment-oriented**: Best for companies with centralized environments.
        *   **Simple, team-oriented**: Best for companies with autonomous teams.
        
        For more information, see [Hierarchy and access](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-5) task.
        
    2.  Optional: modify the name for each of the following environments in your hierarchy:
        
        *   **Production**
        *   **Non-Production**
        *   **Development**
    3.  Click **Confirm hierarchy and continue**.
        
6.  On the **Review and deploy your configuration** screen, the configurations that you specified are combined with default values for security, logging, and monitoring. Review the following draft configurations:
    
    1.  **Resource Hierarchy & Access**: Review the folder and projects.
        
        To make changes to your resource hierarchy, click **Edit in task**.
        
    2.  **Security**: Review the following:
        
        *   Security Command Center is automatically configured. For more information, see [Centralize vulnerability and threat reporting](https://docs.cloud.google.com/docs/enterprise/advanced-production#centralize-vulnerability-threat-reporting).
            
        *   Recommended organization policies are automatically configured. For more information, see [Apply recommended organization policies](https://docs.cloud.google.com/docs/enterprise/advanced-production#apply-recommended-organization-policies).
            
    3.  **Logging & Monitoring**: Review the following:
        
        *   **Log Router configuration**: audit logs are stored in logs bucket.
        *   **Monitoring configuration**: metrics from all projects are monitored in a central location.
        
        For more information, see [Central logging and monitoring](https://docs.cloud.google.com/docs/enterprise/advanced-production#checklist-section-7).
        
7.  To deploy your foundation, do one of the following:
    
    *   **Download as Terraform**: Use this option if you want to automate resource management using a Terraform deployment workflow. You can download and deploy using this method multiple times.
        
    *   **Deploy directly**: Use this option if you don't have an existing Terraform deployment workflow, and want a quick deployment method. You can deploy using this method only once.
        
    
    For more information, see [Deploy your configuration](https://docs.cloud.google.com/docs/enterprise/advanced-production#deploy).
    
8.  To enable billing on the management project, see [Link a billing account to your management project](https://docs.cloud.google.com/resource-manager/docs/manage-applications#billing).
    

What's next
-----------

For information on experimenting and building, see [Build your Google Cloud architecture](https://docs.cloud.google.com/docs/enterprise/manage-foundation#startbuilding).
