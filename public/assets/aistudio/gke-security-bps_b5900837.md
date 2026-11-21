# GKE security best practices &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/gke-security-bps

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
    

GKE security best practices
---------------------------

This document describes a typical Google Kubernetes Engine (GKE) architecture in Google Cloud. It also lists the security best practices that are applicable to GKE workloads.

Architecture
------------

The following diagram shows the Google Cloud services in a typical GKE deployment.

This diagram includes the following:

*   [GKE](https://docs.cloud.google.com/kubernetes-engine/docs/concepts/kubernetes-engine-overview) is a managed implementation of the Kubernetes open source container orchestration platform that lets you run containerized apps. GKE clusters include your application pods and Policy Controller. [Policy Controller](https://docs.cloud.google.com/kubernetes-engine/policy-controller/docs/overview) helps you enforce policies on your Kubernetes clusters.
    
*   [Artifact Registry](https://docs.cloud.google.com/artifact-registry/docs/overview) streamlines your container and app development and deployment process, improves collaboration, and helps improve the security and reliability of your apps.
    
*   [Cloud Audit Logs](https://docs.cloud.google.com/logging/docs/audit) tracks the actions that your users take in your environment, which enhances your troubleshooting, auditing, and incident response capabilities.
    
*   [Cloud Billing](https://docs.cloud.google.com/billing/docs/concepts) dashboards and alerts let you review usage and billing of GKE workloads.
    
*   [Cloud Build](https://docs.cloud.google.com/build/docs/overview) lets you build, test, and deploy a serverless CI/CD platform on Google Cloud.
    
*   [Cloud Identity](https://docs.cloud.google.com/identity/docs/overview) unifies identity, access, application, and management for Google Cloud.
    
*   [Cloud Key Management Service](https://docs.cloud.google.com/kms/docs/key-management-service) creates and manages encryption keys.
    
*   [Cloud Run functions](https://docs.cloud.google.com/run/docs/functions-with-run) automates tasks, triggers jobs, integrates with other services, and builds event-driven development pipelines.
    
*   [Cloud Service Mesh](https://docs.cloud.google.com/service-mesh/docs/overview) lets Kubernetes services communicate with each other.
    
*   [Cloud Storage](https://docs.cloud.google.com/storage/docs/introduction) stores the data that's required for your containers and apps to run.
    
*   [Cloud DNS](https://docs.cloud.google.com/dns/docs/overview) registers, manages, and serves your domain.
    
*   [Identity and Access Management (IAM)](https://docs.cloud.google.com/iam/docs/overview) controls who can perform specific actions on your GKE workload resources, such as creating, editing, or deleting them.
    
*   [Organization Policy Service](https://docs.cloud.google.com/resource-manager/docs/organization-policy/overview) centrally manages and enforces policies across your Google Cloud environment. Organization Policy helps to ensure consistent configuration and security compliance across the projects and resources within your organization.
    
*   [Pub/Sub](https://docs.cloud.google.com/pubsub/docs/overview) enables efficient communication and automation within your workflows.
    
*   [Resource Manager](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy) helps you group and manage the logical components of your GKE workloads.
    
*   [Secret Manager](https://docs.cloud.google.com/secret-manager/docs/overview) helps you protect the sensitive data and credentials that are used in GKE projects.
    
*   [Security Command Center](https://docs.cloud.google.com/security-command-center/docs/security-command-center-overview) helps you protect your cloud organization, your GKE workloads, and the data that you store on Google Cloud. Security Command Center provides the following:
    
    *   Centralized security management
    *   Threat detection and incident response
    *   Automated security assessments
    *   Compliance and regulatory reporting
    *   Security recommendations and best practices
*   [Virtual Private Cloud (VPC)](https://docs.cloud.google.com/vpc/docs/overview) isolates your GKE resources from the internet in a secure environment. This network configuration helps protect sensitive data and workloads from unauthorized access and potential cyberattacks.
    
*   [Cloud VPN](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview) or [Cloud Interconnect](https://docs.cloud.google.com/network-connectivity/docs/interconnect/concepts/overview) lets you establish a secure network connection between your on-premises infrastructure and your GKE environment. Cloud VPN or Cloud Interconnect helps enable seamless data transfer and communication between your private network and Google Cloud resources. Consider this integration for scenarios like accessing on-premises data for model training or deploying models to on-premises resources for inference.
    

Best practices for GKE workloads
--------------------------------

This section provides links to the best practices for workloads that use GKE.

*   [Recommended user groups and IAM roles](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/recommended-iam-groups)
*   Secure enterprise foundation best practices
    
    *   Authentication and authorization best practices
        
        *   [Disable automatic IAM grants for default service accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#disable-automatic-iam-grants-for-default-service-accounts)
        *   [Block the creation of external service account keys](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#block-the-creation-of-external-service-account-keys)
        *   [Block service account key uploads](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#block-service-account-key-uploads)
        *   [Configure separation of duties for organization policy administrators](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#configure-separation-of-duties-for-organization-policy-administrators)
        *   [Enable two-step verification for super admin accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-two-step-verification-for-super-admin-accounts)
        *   [Enforce two-step verification on the super admin organization unit](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enforce-two-step-verification-on-the-super-admin-organization-unit)
        *   [Create an exclusive email address for the primary super admin](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#create-an-exclusive-email-address-for-the-primary-super-admin)
        *   [Create redundant administrator accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#create-redundant-administrator-accounts)
        *   [Implement tags to efficiently assign IAM policies and organization policies](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#implement-tags-to-efficiently-assign-iam-policies-and-organization-policies)
        *   [Audit high-risk changes to IAM](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#audit-high-risk-changes-to-iam)
        *   [Block access to Cloud Shell for Cloud Identity managed user accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#block-access-to-cloud-shell-for-cloud-identity-managed-user-accounts)
        *   [Configure Context-Aware Access for Google consoles](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#configure-context-aware-access-for-google-consoles)
        *   [Block account self-recovery for super admin accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#block-account-self-recovery-for-super-admin-accounts)
        *   [Turn off unused Google services](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#turn-off-unused-google-services)
    *   Organization best practices
        
        *   [Restrict TLS versions supported by Google APIs](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#restrict-tls-versions-supported-by-google-apis)
        *   [Restrict authorized principals](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#restrict-authorized-principals)
        *   [Restrict resource service usage](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#restrict-resource-service-usage)
        *   [Restrict resource locations](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#restrict-resource-locations)
    *   Networking best practices
        
        *   [Block default network creation](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#block-default-network-creation)
        *   [Enable DNS Security Extensions](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-dns-security-extensions)
        *   [Enable the service scope restriction in Access Context Manager access policies](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-the-service-scope-restriction-in-access-context-manager-access-policies)
        *   [Restrict APIs within VPC Service Controls service perimeters](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#restrict-apis-within-vpc-service-controls-service-perimeters)
        *   [Use zonal DNS](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#use-zonal-dns)
    *   Logging, monitoring, and alerting best practices
        
        *   [Share audit logs from Cloud Identity](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#share-audit-logs-from-cloud-identity)
        *   [Use audit logs](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#use-audit-logs)
        *   [Enable VPC Flow Logs](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-vpc-flow-logs)
        *   [Enable Firewall Rules Logging](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-firewall-rules-logging)
        *   [Enable Data Access audit logs](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-data-access-audit-logs)
        *   [Enable auditing of administrator activity](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-auditing-of-administrator-activity)
        *   [Subscribe to security bulletins](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#subscribe-to-security-bulletins)
        *   [Enable Access Transparency logs](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-access-transparency-logs)
        *   [Export billing data for detailed analysis](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#export-billing-data-for-detailed-analysis)
    *   Key and secret management best practices
        
        *   [Encrypt data at rest in Google Cloud](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#encrypt-data-at-rest-in-google-cloud)
        *   [Use NIST-approved algorithms for encryption and decryption](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#use-nist-approved-algorithms-for-encryption-and-decryption)
        *   [Set the purpose for Cloud Key Management Service keys](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#set-the-purpose-for-cloud-kms-keys)
        *   [Ensure that CMEK settings are appropriate for secure BigQuery data warehouses](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#ensure-that-cmek-settings-are-appropriate-for-secure-bigquery-data-warehouses)
        *   [Rotate encryption key every 90 days](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#rotate-encryption-key-every-90-days)
        *   [Set up automatic secret rotation](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#set-up-automatic-secret-rotation)
        *   [Restrict customer-managed encryption keys location](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#restrict-customer-managed-encryption-keys-location)
        *   [Use CMEK for Google Cloud services](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#use-cmek-for-google-cloud-services)
        *   [Replicate secrets automatically](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#replicate-secrets-automatically)
    *   Security posture and analytics best practices
        
        *   [Enable Security Command Center at the organization level](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#enable-security-command-center-at-the-organization-level)
        *   [Configure alerts from Security Command Center](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations#configure-alerts-from-security-command-center)
*   Infrastructure best practices
    
    *   Compute best practices
        
        *   [Define VM instances that can enable IP forwarding](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#define-vm-instances-that-can-enable-ip-forwarding)
        *   [Disable VM-nested virtualization](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#disable-vm-nested-virtualization)
        *   [Restrict external IP addresses on VMs](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#restrict-external-ip-addresses-on-vms)
        *   [Define permitted external IP addresses for VM instances](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#define-permitted-external-ip-addresses-for-vm-instances)
        *   [Require VPC connector for Cloud Run functions](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#require-vpc-connector-for-cloud-run-functions)
    *   Container best practices
        
        *   [Restrict control plane access](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#restrict-control-plane-access)
        *   [Use least-privilege firewall rules](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#use-least-privilege-firewall-rules)
        *   [Use Google Groups for RBAC](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#use-google-groups-for-rbac)
        *   [Enable Shielded GKE Nodes](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#enable-shielded-gke-nodes)
        *   [Use Container-Optimized OS with containerd runtime](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#use-container-optimized-os-with-containerd-runtime)
        *   [Use Workload Identity Federation for GKE](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#use-workload-identity-federation-for-gke)
        *   [Enable GKE Sandbox](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#enable-gke-sandbox)
        *   [Disable the kubelet read-only port](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#disable-the-kubelet-read-only-port)
        *   [Use namespace and RBAC to restrict access to cluster resources](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#use-namespace-and-rbac-to-restrict-access-to-cluster-resources)
        *   [Restrict traffic among pods](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#restrict-traffic-among-pods)
        *   [Use admission controllers to enforce policies](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#use-admission-controllers-to-enforce-policies)
        *   [Restrict ability for workloads to self modify](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#restrict-ability-for-workloads-to-self-modify)
        *   [Monitor your cluster configurations](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#monitor-your-cluster-configurations)
        *   [Enforce Binary Authorization](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure#enforce-binary-authorization)
*   Data management best practices
    
    *   Storage best practices
        
        *   [Block public access to Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#block-public-access-to-cloud-storage-buckets)
        *   [Use uniform bucket-level access](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#use-uniform-bucket-level-access)
        *   [Protect HMAC keys for service accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#protect-hmac-keys-for-service-accounts)
        *   [Detect enumeration of Cloud Storage buckets by service accounts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#detect-enumeration-of-cloud-storage-buckets-by-service-accounts)
        *   [Ensure Cloud Storage bucket retention policy uses Bucket Lock](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#ensure-cloud-storage-bucket-retention-policy-uses-bucket-lock)
        *   [Set lifecycle rules for the SetStorageClass action](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#set-lifecycle-rules-for-the-setstorageclass-action)
        *   [Set permitted regions for storage classes](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#set-permitted-regions-for-storage-classes)
        *   [Enable lifecycle management for Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enable-lifecycle-management-for-cloud-storage-buckets)
        *   [Review and evaluate temporary holds on active objects](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#review-and-evaluate-temporary-holds-on-active-objects)
        *   [Enforce retention policies on Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enforce-retention-policies-on-cloud-storage-buckets)
        *   [Enforce classification tags for Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enforce-classification-tags-for-cloud-storage-buckets)
        *   [Enforce log buckets for Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enforce-log-buckets-for-cloud-storage-buckets)
        *   [Configure deletion rules for Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#configure-deletion-rules-for-cloud-storage-buckets)
        *   [Ensure isLive condition is False for deletion rules](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#ensure-islive-condition-is-false-for-deletion-rules)
        *   [Enforce versioning for Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enforce-versioning-for-cloud-storage-buckets)
        *   [Enforce owners for Cloud Storage buckets](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enforce-owners-for-cloud-storage-buckets)
        *   [Enable logging of key Cloud Storage activities](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management#enable-logging-of-key-cloud-storage-activities)
*   Agent and application best practices
    
    *   [Configure vulnerability scanning for artifacts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/agents-applications#configure-vulnerability-scanning-for-artifacts)
    *   [Create cleanup policies for artifacts](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/agents-applications#create-cleanup-policies-for-artifacts)
    *   [Configure runtime vulnerability scanning](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/agents-applications#configure-runtime-vulnerability-scanning)

What's next
-----------

*   Learn how to [deploy an enterprise developer platform on Google Cloud](https://docs.cloud.google.com/architecture/blueprints/enterprise-application-blueprint).

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
