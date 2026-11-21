# Best practices for cross-configuration communication &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/best-practices/cross-config-communication

This page provides guidelines and recommendations for cross-configuration communication when using Terraform for Google Cloud.

This guide is not an introduction to Terraform. For an introduction to using Terraform with Google Cloud, see [Get started with Terraform](https://docs.cloud.google.com/docs/terraform/get-started-with-terraform).

A common problem that arises when using Terraform is how to share information across different Terraform configurations (possibly maintained by different teams). Generally, information can be shared between configurations without requiring that they be stored in a single configuration directory (or even a single repository).

The recommended way to share information between different Terraform configurations is by using remote state to reference other root modules. [Cloud Storage](https://www.terraform.io/docs/backends/types/gcs.html) or [Terraform Enterprise](https://www.terraform.io/docs/backends/types/terraform-enterprise.html) are the preferred state backends.

For querying resources that are not managed by Terraform, use data sources from the [Google provider](https://registry.terraform.io/providers/hashicorp/google/latest/docs). For example, the default Compute Engine service account can be retrieved [using a data source](https://registry.terraform.io/providers/hashicorp/google/latest/docs/data-sources/compute_default_service_account). Don't use data sources to query resources that are managed by another Terraform configuration. Doing so can create implicit dependencies on resource names and structures that normal Terraform operations might unintentionally break.

What's next
-----------

*   Learn about [best practices for version control](https://docs.cloud.google.com/docs/terraform/best-practices/version-control).
*   Learn about [best practices when working with Google Cloud resources](https://docs.cloud.google.com/docs/terraform/best-practices/working-with-resources).
