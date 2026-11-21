# Restrict TLS versions &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/restrict-tls-versions

This page describes how you can prevent access to Google Cloud resources by denying requests made using Transport Layer Security (TLS) 1.0 or 1.1. If you also want to prevent the use of TLS 1.2, use a TLS cipher restriction policy to allow only TLS 1.3 ciphers. For more information, see [Restrict TLS cipher suites](https://docs.cloud.google.com/docs/security/compliance/restrict-tls-cipher-suites).

Overview
--------

Google Cloud supports multiple [TLS protocol versions](https://docs.cloud.google.com/docs/security/encryption-in-transit#transport_layer_security). To meet compliance requirements, you might want to deny handshake requests from clients that use earlier TLS versions. To do this, you can use the `gcp.restrictTLSVersion` [organization policy constraint](https://docs.cloud.google.com/resource-manager/docs/organization-policy/understanding-constraints).

The `gcp.restrictTLSVersion` constraint can be applied to organizations, folders, or projects in the [resource hierarchy](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy). The constraint uses a [deny list](https://docs.cloud.google.com/resource-manager/docs/organization-policy/understanding-constraints#list_constraint), which denies explicit values and allows all others. An error will occur if you try to use an allow list.

Due to the behavior of [organization policy hierarchy evaluation](https://docs.cloud.google.com/resource-manager/docs/organization-policy/understanding-hierarchy), the TLS version restriction applies to the specified resource node and all of its children. For example, if you deny TLS version 1.0 for an organization, it is also denied for all folders and projects (children) that descend from that organization.

You can override the inherited TLS version restriction by updating the organization policy on a child resource. For example, if your organization policy denies TLS 1.0 at the organization level, you can remove the restriction for a child folder by setting a separate organization policy on that folder. If the folder has any children, the folder's policy will also be applied on each child resource due to policy inheritance.

Before you begin
----------------

*   To set, change, or delete an organization policy, you must first be granted the **Organization Policy Administrator** (`roles/orgpolicy.policyAdmin`) Identity and Access Management (IAM) role.

Restrict a TLS version
----------------------

To restrict one or more TLS versions, complete the following steps:

### Console

1.  Open the **Organization policies** page in the Google Cloud console.
    
    [Go to Organization policies](https://console.cloud.google.com/iam-admin/orgpolicies)
    
2.  Select the project picker at the top of the page.
    
3.  From the project picker, select the resource for which you want to set the organization policy.
    
4.  Select the **Restrict TLS Version** constraint from the list on the **Organization policies** page.
    
5.  To update the organization policy for this resource, click **Edit**.
    
6.  On the **Edit** page, select **Customize**.
    
7.  Under **Policy enforcement**, select an enforcement option:
    
    *   To merge and evaluate your organization policies together, select **Merge with parent**. For more information about inheritance and the resource hierarchy, see [Understanding hierarchy evaluation](https://docs.cloud.google.com/resource-manager/docs/organization-policy/understanding-hierarchy).
        
    *   To override policies inherited from a parent resource, select **Replace**.
        
8.  Click **Add rule**.
    
9.  Under **Policy values**, the default value is set to **Deny all**. Select **Custom** instead.
    
10.  Under **Policy type**, select **Deny**.
     
11.  Under **Custom values**, enter a TLS version to deny. The following values are valid custom values:
     
     *   `TLS_VERSION_1` for TLS 1.0
     *   `TLS_VERSION_1_1` for TLS 1.1
     
     If you are restricting more than one TLS version, click **Add value** and enter the value in the additional field.
     
12.  To finish and apply the organization policy, click **Save**.
     

### gcloud

Use the [`gcloud org-policies set-policy` command](https://docs.cloud.google.com/sdk/gcloud/reference/resource-manager/org-policies/set-policy) to set an organization policy on the resource:

gcloud org-policies set-policy POLICY\_PATH

POLICY\_PATH is the full path to your organization policy file, which should look like the following if using the YAML format:

name: RESOURCE_TYPE/RESOURCE_ID/policies/gcp.restrictTLSVersion
    spec:
      rules:
      - values:
        deniedValues:
        - TLS_VERSION_1
        - TLS_VERSION_1_1

Replace with the following:

*   `RESOURCE_TYPE` is `organizations`, `folders` or `projects`.
    
*   `RESOURCE_ID` is your organization ID, folder ID, project ID, or project number, depending on the type of resource specified in `RESOURCE_TYPE`.
    

Run the following command to verify that your policy has been applied:

gcloud org-policies describe gcp.restrictTLSVersion \--RESOURCE\_TYPE\=RESOURCE\_ID \--effective

Replace with the following:

*   `RESOURCE_TYPE` is `organization`, `folder` or `project`.
    
*   `RESOURCE_ID` is your organization ID, folder ID, project ID, or project number, depending on the type of resource specified in `RESOURCE_TYPE`.
    

Test the policy
---------------

The TLS version restriction policy constraint can be tested for any [in-scope service](#supported-services). The following example curl command validates the TLS version restriction for a Cloud Storage bucket.

  curl \-X GET \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
  "https://storage.googleapis.com/storage/v1/b/GCS\_BUCKET\_NAME/o" \\
  \--tlsvTLS\_VERSION \--tls-max TLS\_VERSION \--ciphers DEFAULT@SECLEVEL\=0

There is no space between `--tlsv` and the value. For example: `--tlsv1.1`

Replace with the following:

*   `GCS_BUCKET_NAME` is a Cloud Storage bucket name in your project, such as `mybucketname`.
    
*   `TLS_VERSION` is a TLS version like `1.0` or `1.1` denied in the configured policy.
    

The following example `curl` request shows GCS\_BUCKET\_NAME set to `mybucketname` and TLS\_VERSION set to `1.1`:

  curl \-X GET \-H "Authorization: Bearer $(gcloud auth print-access-token)" \\
  "https://storage.googleapis.com/storage/v1/b/mybucketname/o" \\
  \--tlsv1.1 \--tls-max 1.1 \--ciphers DEFAULT@SECLEVEL\=0

If the organization policy is configured to restrict `TLS_VERSION_X`, then any attempt to access resources with `TLS_VERSION_X` in the policy-restricted project in this example command will fail. An error message is returned that describes the reason for this failure.

Request is disallowed by organization's constraints/gcp.restrictTLSVersion
constraint for 'projects/PROJECT\_NUMBER' to use service
'SERVICE\_NAME.googleapis.com' by violated TLS version \`TLS\_VERSION\_X\`

This output includes the following values:

*   `PROJECT_NUMBER`: the project number hosting the resource referred in the earlier command.
*   `SERVICE_NAME`: the name of the [in-scope service](#supported-services) blocked by the TLS restriction policy.

Supported services
------------------

TLS version restriction is supported by any Google Cloud resource APIs that have a header signed by [Google Front End (GFE)](https://docs.cloud.google.com/docs/security/infrastructure/design#google-frontend-service).

Unsupported services
--------------------

The TLS version restriction organization policy constraint is _not_ applicable for the following services:

*   Apigee
*   App Engine (`*.appspot.com`)
*   Cloud Run functions (`*.cloudfunctions.net`),
*   Cloud Run (`*.run.app`)
*   Private Service Connect
*   Custom domains

To restrict TLS versions for these services, use [Cloud Load Balancing](https://docs.cloud.google.com/load-balancing/docs/https/setting-up-https-serverless) along with [SSL policies](https://docs.cloud.google.com/load-balancing/docs/use-ssl-policies). You can also use the predefined [`constraints/compute.requireSslPolicy` constraint](https://docs.cloud.google.com/load-balancing/docs/org-policy-constraints#restrict-tls-capabilities-custom-constraint) along with custom constraints for SSL policies to enforce TLS version and cipher suite restrictions for your load balancers.

What's next
-----------

*   Learn which [products are supported](https://docs.cloud.google.com/assured-workloads/docs/supported-products) for each control package.
