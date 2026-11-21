# Migrate from terraform-validator &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/policy-validation/migrate-from-terraform-validator

`gcloud beta terraform vet` is a replacement for the open-source [`terraform-validator`](https://github.com/GoogleCloudPlatform/terraform-validator/) project, with a few minor differences. If you are migrating your CI/CD pipeline to use `gcloud beta terraform vet`, you will need to make the following changes.

1\. Update the command and args
-------------------------------

*   Replace `terraform-validator validate` with `gcloud beta terraform vet`
*   Replace `--policy-path` with `--policy-library`

Basic example:

# Old
    terraform-validator validate ./tfplan.json --policy-path=/path/to/policy-library
    
    # New
    gcloud beta terraform vet ./tfplan.json --policy-library=/path/to/policy-library

With [service account impersonation](https://docs.cloud.google.com/sdk/gcloud/reference#--impersonate-service-account):

# Old
    GOOGLE_IMPERSONATE_SERVICE_ACCOUNT=account@project.iam.gserviceaccount.com
    terraform-validator validate ./tfplan.json --policy-path=/path/to/policy-library
    
    # New
    gcloud beta terraform vet ./tfplan.json --policy-library=/path/to/policy-library \
      --impersonate-service-account=account@project.iam.gserviceaccount.com

2\. (Optional) Upgrade constraint templates
-------------------------------------------

`terraform-validator` documentation historically gave instructions on how to write `v1alpha1` Constraint Framework policies; there is a newer format that we recommend for [writing new policies](https://github.com/GoogleCloudPlatform/policy-library/blob/main/docs/constraint_template_authoring.md). You can also [upgrade existing policies to use the new format](https://github.com/GoogleCloudPlatform/policy-library/blob/main/docs/constraint_template_authoring.md#updating-from-v1alpha1-templates)

For policies sourced from [github.com/GoogleCloudPlatform/policy-library](https://github.com/GoogleCloudPlatform/policy-library), we recommend staying in sync with the remote repository.
