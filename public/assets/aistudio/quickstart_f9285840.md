# gcloud beta terraform vet quickstart &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/policy-validation/quickstart

This quickstart shows you how to apply a constraint that enforces a domain restriction. You'll test that constraint and intentionally throw an error. Then you'll modify the constraint so that your domain passes.

Before you begin
----------------

*   You need a [Google Cloud project](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects).
*   You need the following [Identity and Access Management (IAM) permissions](https://docs.cloud.google.com/resource-manager/docs/access-control-proj) for that project:
    
    *   `resourcemanager.projects.getIamPolicy` – This permission can be granted with the Security Reviewer role for the organization.
    *   `resourcemanager.projects.get` – This permission can be granted with the Project Viewer role for the organization.

To get you started quickly, these instructions use a Cloud Shell that's pre-installed with Terraform, and with a [cloned Policy Library repository](https://docs.cloud.google.com/docs/terraform/policy_validation/create_policy_library#duplicate_the_sample_library). The instructions assume you already have a Google Cloud account.

1.  Go to the Cloud Shell and clone the policy library.
    
    [Clone policy library](https://console.cloud.google.com/cloudshell/open?cloudshell_git_repo=https://github.com/GoogleCloudPlatform/policy-library.git)
    
2.  Copy the sample IAM domain restriction constraint into the `policies/constraints` directory.
    
    cp samples/iam_service_accounts_only.yaml policies/constraints
    
3.  Examine the constraint you copied by printing it to the terminal.
    
    cat policies/constraints/iam_service_accounts_only.yaml
    
    The output looks like this:
    
    # This constraint checks that all IAM policy members are in the
        # "gserviceaccount.com" domain.
        apiVersion: constraints.gatekeeper.sh/v1alpha1
        kind: GCPIAMAllowedPolicyMemberDomainsConstraintV2
        metadata:
          name: service_accounts_only
          annotations:
            description: Checks that members that have been granted IAM roles belong to allowlisted
              domains.
        spec:
          severity: high
          match:
            target: # {"$ref":"#/definitions/io.k8s.cli.setters.target"}
            - "organizations/**"
          parameters:
            domains:
            - gserviceaccount.com
    
    Notice `gserviceaccount.com` at the bottom. This specifies that only members from the `gserviceaccount.com` domain can be present in an IAM policy.
    
4.  To verify that the policy works as expected, create the following Terraform `main.tf` file in the current directory. You can use nano, vim, or the Cloud Shell Editor to create `policy-library/main.tf`.
    
    terraform {
          required_providers {
            google = {
              source = "hashicorp/google"
              version = "~> 3.84"
            }
          }
        }
        
        resource "google_project_iam_binding" "sample_iam_binding" {
          project = "PROJECT_ID"
          role    = "roles/viewer"
        
          members = [
            "user:EMAIL_ADDRESS"
          ]
        }
    
    Replace the following:
    
    *   `PROJECT_ID`: your project ID.
    *   `EMAIL_ADDRESS`: a sample email address. This can be any valid email address. For example, `user@example.com`.
5.  Initialize Terraform and generate a Terraform plan using the following:
    
    terraform init
    
6.  Export the Terraform plan, if asked, click **Authorize** when prompted:
    
    terraform plan -out=test.tfplan
    
7.  Convert the Terraform plan to JSON:
    
    terraform show -json ./test.tfplan > ./tfplan.json
    
8.  Install the terraform-tools component:
    
    sudo apt-get install google-cloud-sdk-terraform-tools
    
9.  Enter the following command to validate that your Terraform plan complies with your policies:
    
    gcloud beta terraform vet tfplan.json --policy-library=. --format=json
    
    Since the email address you provided in the IAM policy binding does not belong to a service account, the plan violates the constraint you set up.
    
    \[
    {
      "constraint": "GCPIAMAllowedPolicyMemberDomainsConstraintV2.service\_accounts\_only",
      "constraint\_config": {
        "api\_version": "constraints.gatekeeper.sh/v1alpha1",
        "kind": "GCPIAMAllowedPolicyMemberDomainsConstraintV2",
        "metadata": {
          "annotations": {
            "description": "Checks that members that have been granted IAM roles belong to allowlisted domains.",
            "validation.gcp.forsetisecurity.org/originalName": "service\_accounts\_only",
            "validation.gcp.forsetisecurity.org/yamlpath": "policies/constraints/iam\_service\_accounts\_only.yaml"
          },
          "name": "service-accounts-only"
        },
        "spec": {
          "match": {
            "target": \[
              "organizations/\*\*"
            \]
          },
          "parameters": {
            "domains": \[
              "gserviceaccount.com"
            \]
          },
          "severity": "high"
        }
      },
      "message": "IAM policy for //cloudresourcemanager.googleapis.com/projects/PROJECT\_ID contains member from unexpected domain: user:user@example.com",
      "metadata": {
        "ancestry\_path": "organizations/ORG\_ID/projects/PROJECT\_ID",
        "constraint": {
          "annotations": {
            "description": "Checks that members that have been granted IAM roles belong to allowlisted domains.",
            "validation.gcp.forsetisecurity.org/originalName": "service\_accounts\_only",
            "validation.gcp.forsetisecurity.org/yamlpath": "policies/constraints/iam\_service\_accounts\_only.yaml"
          },
          "labels": {},
          "parameters": {
            "domains": \[
              "gserviceaccount.com"
            \]
          }
        },
        "details": {
          "member": "user:user@example.com",
          "resource": "//cloudresourcemanager.googleapis.com/projects/PROJECT\_ID"
        }
      },
      "resource": "//cloudresourcemanager.googleapis.com/projects/PROJECT\_ID",
      "severity": "high"
    }
    \]
    
10.  To allow another domain (your email), edit `policy-library/policies/constraints/iam_service_accounts_only.yaml` and append your email domain to the domains allowlist. In the following example, we've added `example.com`, but you'd enter the domain for your own email address:
     
     apiVersion: constraints.gatekeeper.sh/v1alpha1
         kind: GCPIAMAllowedPolicyMemberDomainsConstraintV1
         metadata:
           name: service_accounts_only
         spec:
           severity: high
           match:
             target: ["organizations/**"]
           parameters:
             domains:
               - gserviceaccount.com
               - example.com
     
11.  Now validate your Terraform plan again, and this should result in no violations found:
     
     gcloud beta terraform vet tfplan.json --policy-library=. --format=json
     
     Expected output:
     
     \[\]
     

Troubleshooting
---------------

If you receive the following error, `"Error 403: The caller does not have permission, forbidden"`, then you either didn't replace `PROJECT_ID` in `policy-library/main.tf` with the name of your project, or you don't have the necessary permissions on the project you specified.

After editing the project name and/or permissions (`resourcemanager.projects.getIamPolicy` and `resourcemanager.projects.get`), go back and export the Terraform plan again, and then convert the Terraform plan to JSON.
