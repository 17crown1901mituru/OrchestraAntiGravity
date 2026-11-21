# Troubleshoot gcloud beta terraform vet &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/policy_validation/troubleshooting

Why is a violation I expected not throwing an error?
----------------------------------------------------

If you test your validation logic and find that the constraint isn't throwing an error when it should be, this might be a result of one or more of the following:

*   **Is your policy-library set up correctly?** Verify that your policy library contains a `policies/constraints` directory, which contains the constraint you are expecting to cause a violation.
*   **Is the Terraform resource that contains the violation a supported resource?** `gcloud beta terraform vet` can only check for violations for resources that are supported in its version. Re-run your command with `--verbosity=debug` and look for a message like: `unsupported resource: google_resource_name`. Or you can check whether your resource is in the list of [supported resources](https://docs.cloud.google.com/docs/cloud-asset-inventory/overview#supported_resource_types).
*   **Is your constraint targeting the correct Terraform resource?**
    
    1.  Check the `kind` field of the constraint. It should be something like: `GCPAppengineLocationConstraintV1`
    2.  Search the `policies/templates` directory for a policy that has the same value for `spec.crd.spec.names.kind`
    3.  In the `rego` field, look for something like: `asset.asset_type == "appengine.googleapis.com/Application"`. This is the [CAI Asset Type](https://docs.cloud.google.com/asset-inventory/docs/supported-asset-types) that the constraint targets.
    4.  Make sure that the CAI Asset Type is in the list of [supported resources](https://docs.cloud.google.com/docs/cloud-asset-inventory/overview#supported_resource_types).

Why am I getting an error saying that no project is defined?
------------------------------------------------------------

Resource Ancestry is used to build an accurate CAI Asset Name. If `gcloud beta terraform vet` can't automatically determine the ancestry for a CAI Asset, it will return an error saying: `project: required field is not set`. You can provide a default project with the `--project` flag or by setting one using [`gcloud config`](https://docs.cloud.google.com/sdk/gcloud/reference/config).

Why am I getting an error saying `getting resource ancestry for project PROJECT_ID: googleapi: Error 403: The caller does not have permission, forbidden`?
----------------------------------------------------------------------------------------------------------------------------------------------------------

Run the command with `--verbosity=debug` and look for a message like `Terraform is using this identity:`. It should be followed by an email address, which is the account being used for API requests.

*   If there is no email address, then [make sure that your authentication is working properly](https://docs.cloud.google.com/sdk/gcloud/reference/auth).
*   If there is an email address, but it's not the service account that you wanted to impersonate, then [make sure that your service account impersonation is set up correctly](https://docs.cloud.google.com/sdk/gcloud/reference#--impersonate-service-account)
*   If the correct email address is showing, make sure that it has the following permissions on the project:
    *   `getIamPolicy`
    *   `resourcemanager.projects.get`
