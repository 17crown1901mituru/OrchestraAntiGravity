# Basic Terraform commands &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/basic-commands

To apply your Terraform configuration in a Google Cloud project, complete the steps in the following sections.

Prepare Cloud Shell
-------------------

1.  Launch [Cloud Shell](https://shell.cloud.google.com/).
2.  Set the default Google Cloud project where you want to apply your Terraform configurations.
    
    You only need to run this command once per project, and you can run it in any directory.
    
    export GOOGLE\_CLOUD\_PROJECT=PROJECT\_ID
    
    Environment variables are overridden if you set explicit values in the Terraform configuration file.
    

Prepare the directory
---------------------

Each Terraform configuration file must have its own directory (also called a _root module_).

1.  In [Cloud Shell](https://shell.cloud.google.com/), create a directory and a new file within that directory. The filename must have the `.tf` extension—for example `main.tf`. In this tutorial, the file is referred to as `main.tf`.
    
    mkdir DIRECTORY && cd DIRECTORY && touch main.tf
    
2.  If you are following a tutorial, you can copy the sample code in each section or step.
    
    Copy the sample code into the newly created `main.tf`.
    
    Optionally, copy the code from GitHub. This is recommended when the Terraform snippet is part of an end-to-end solution.
    
3.  Review and modify the sample parameters to apply to your environment.
4.  Save your changes.
5.  Initialize Terraform. You only need to do this once per directory.
    
    terraform init
    
    Optionally, to use the latest Google provider version, include the `-upgrade` option:
    
    terraform init -upgrade
    

Apply the changes
-----------------

1.  Review the configuration and verify that the resources that Terraform is going to create or update match your expectations:
    
    terraform plan
    
    Make corrections to the configuration as necessary.
    
2.  Apply the Terraform configuration by running the following command and entering `yes` at the prompt:
    
    terraform apply
    
    Wait until Terraform displays the "Apply complete!" message.
    
3.  [Open your Google Cloud project](https://console.cloud.google.com/) to view the results. In the Google Cloud console, navigate to your resources in the UI to make sure that Terraform has created or updated them.

Reformat
--------

To reformat your Terraform configuration in the standard style, enter the following command:

terraform fmt

Validate
--------

To check whether your configuration is valid, enter the following command:

terraform validate

Delete changes
--------------

Remove resources previously applied with your Terraform configuration by running the following command and entering `yes` at the prompt:

terraform destroy

Specify the project ID
----------------------

If you run the `export GOOGLE_CLOUD_PROJECT` command, most resources can infer the `project_id`.

Some resources, such as `project_iam_*`, cannot infer the project ID. As a workaround, some samples use the [`data "google_project"`](https://registry.terraform.io/providers/hashicorp/google/latest/docs/data-sources/project) data source. You can replace this data source with the project ID string or a variable.

For a sample that uses this workaround, see [sql\_instance\_iam\_condition](https://github.com/terraform-google-modules/terraform-docs-samples/blob/main/cloud_sql/instance_iam_condition/main.tf).

What's next
-----------

*   [Learn more about Terraform's CLI features](https://www.terraform.io/cli/commands).
