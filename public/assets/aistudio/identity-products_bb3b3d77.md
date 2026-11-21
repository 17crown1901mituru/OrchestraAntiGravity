# Identity management products and features &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/identity-products

Google has various products and technologies that provide identity and access management capabilities. This page lists some of these products, to help you understand what these products offer and how they differ from one another.

If you need help with understanding a specific authentication use case, see [Authentication and authorization use cases](https://docs.cloud.google.com/docs/authentication/use-cases).

Product list
------------

*   [Chrome Enterprise Premium](#bce)
*   [Cloud Identity](#cloud-identity)
*   [Firebase Authentication](#firebase)
*   [Google Identity Services](#google-identity)
*   [Google Workspace](#workspace)
*   [Identity and Access Management (IAM)](#iam)
*   [Identity-Aware Proxy (IAP)](#iap)
*   [Identity Platform](#identity-platform)
*   [Workforce Identity Federation](#wfif)
*   [Workload Identity Federation for GKE](#wli)
*   [Workload Identity Federation](#wlif)

### Chrome Enterprise Premium

[Chrome Enterprise Premium](https://docs.cloud.google.com/chrome-enterprise-premium/docs) is a zero-trust solution that lets you provide secure access with integrated threat and data protection. You can provide an organization's workforce access to web applications securely from anywhere, without the need for a VPN. Chrome Enterprise Premium includes [IAP](#iap), Endpoint Verification, and Chrome Enterprise.

For more information about Chrome Enterprise Premium, see the [Chrome Enterprise Premium overview](https://docs.cloud.google.com/chrome-enterprise-premium/docs/overview).

### Cloud Identity

[Cloud Identity](https://docs.cloud.google.com/identity) is an Identity as a Service (IDaaS) solution that centrally manages users and groups. It's built in to both Google Cloud and [Google Workspace](#workspace). If you are not adopting Google Workspace, Cloud Identity is available as a standalone product.

For information about Cloud Identity, see [Overview of Cloud Identity](https://docs.cloud.google.com/identity/docs/overview).

Cloud Identity is not related to [Identity Platform](#identity-platform).

### Firebase Authentication

[Firebase Authentication](https://firebase.google.com/docs/auth) is the authentication solution provided by Firebase, a backend platform for building Web, Android, and iOS applications. Firebase Authentication includes authentication support for a wide array of user account types.

Firebase Authentication uses Identity Platform as its backend but serves a different audience:

*   Firebase Authentication is aimed at consumer applications, and offers a subset of features compared to Identity Platform.
*   Identity Platform is intended for building enterprise-focused SaaS applications. It lets you integrate with enterprise IdPs using inbound OIDC or SAML.

For more information about the differences between these products, see [Differences between Identity Platform and Firebase Authentication](https://docs.cloud.google.com/identity-platform/docs/product-comparison).

For information about Firebase Authentication, see [Where do I start with Firebase Authentication?](https://firebase.google.com/docs/auth/where-to-start)

For a comparison between end-user authentication options, see [Authenticate application users](https://docs.cloud.google.com/docs/authentication/use-cases#app-users).

### Google Identity Services

[Google Identity Services](https://developers.google.com/identity) is a suite of identity products that support user authentication using Google Accounts, for mobile apps and web platforms. Google Identity Services include the [Sign In With Google](https://developers.google.com/identity/one-tap/android) button, the One Tap sign-in module, and authentication libraries you can use to implement OAuth 2.0 flows in your application.

If you're creating applications that use Google Cloud APIs and resources exclusively, consider using [Identity Platform](#identity-platform), which is based on Google Identity Services, instead.

For a comparison between end-user authentication options, see [Authenticate application users](https://docs.cloud.google.com/docs/authentication/use-cases#app-users).

### Google Workspace

[Google Workspace](https://workspace.google.com/) is a suite of business productivity and collaboration tools based on Google identities (Google Accounts). Google Workspace includes the functionality provided by [Cloud Identity](#cloud-identity) for user management. [Google Accounts](https://www.google.com/account/about/) provide access to Google's products and services, including Google Cloud.

### Identity and Access Management (IAM)

[IAM](https://docs.cloud.google.com/iam) provides fine-grained access control for Google Cloud resources.

For information, see the [IAM overview](https://docs.cloud.google.com/iam/docs/overview).

### Identity-Aware Proxy (IAP)

[Identity-Aware Proxy](https://docs.cloud.google.com/iap) provides a centralized way to support authentication and authorization for your applications and virtual machines (VMs). IAP can be used for applications running in Google Cloud or on-premises.

For information, see [Identity-Aware Proxy overview](https://docs.cloud.google.com/iap/docs/concepts-overview).

For a comparison between end-user authentication options, see [Authenticate application users](https://docs.cloud.google.com/docs/authentication/use-cases#app-users).

### Identity Platform

[Identity Platform](https://docs.cloud.google.com/identity-platform) is a customer identity and access management (CIAM) platform that lets users sign in to your applications and services. Identity Platform supports a variety of ways to sign in, including email and password, Google, Facebook, and Apple. Identity Platform also supports SMS-based multi-factor authentication (MFA).

For information about authentication using Identity Platform, see [Authentication](https://docs.cloud.google.com/identity-platform/docs/concepts-authentication).

Identity Platform is not related to [Cloud Identity](#cloud-identity) or [Identity-Aware Proxy](#iap).

Firebase Authentication uses Identity Platform as its backend but serves a different audience:

*   Firebase Authentication is aimed at consumer applications, and offers a subset of features compared to Identity Platform.
*   Identity Platform is intended for building enterprise-focused SaaS applications. It lets you integrate with enterprise IdPs using inbound OIDC or SAML.

For more information about the differences between these products, see [Differences between Identity Platform and Firebase Authentication](https://docs.cloud.google.com/identity-platform/docs/product-comparison).

For a comparison between end-user authentication options, see [Authenticate application users](https://docs.cloud.google.com/docs/authentication/use-cases#app-users).

### Workforce Identity Federation

[Workforce Identity Federation](https://docs.cloud.google.com/iam/docs/workforce-identity-federation) is an IAM feature that lets you configure and secure granular access for your workforce—employees and partners—by federating identities from an external identity provider (IdP).

Workforce Identity Federation is not the same as [Workload Identity Federation](#wlif). Workforce Identity Federation and Workload Identity Federation both aggregate identities; Workforce Identity Federation aggregates human users, whereas Workload Identity Federation aggregates machine workloads.

### Workload Identity Federation for GKE

[Workload Identity Federation for GKE](https://docs.cloud.google.com/kubernetes-engine/docs/concepts/workload-identity) lets a Kubernetes service account in your GKE cluster act as an IAM service account. Workload Identity Federation for GKE is the recommended way for your workloads running on GKE to access Google Cloud services in a secure and manageable way.

Workload Identity Federation for GKE is not related to [Workload Identity Federation](#wlif).

### Workload Identity Federation

[Workload Identity Federation](https://docs.cloud.google.com/iam/docs/workload-identity-federation) lets you grant on-premises or multicloud workloads access to Google Cloud resources. It does so by federating identities from an external IdP, without requiring a service account key.

Workload Identity Federation is not related to [Workload Identity Federation for GKE](#wli).

Workload Identity Federation is not the same as [Workforce Identity Federation](#wfif). Workload Identity Federation and Workforce Identity Federation both aggregate identities; Workload Identity Federation aggregates machine workloads, while Workforce Identity Federation aggregates human users.

What's next
-----------

*   Review a list of [authentication and authorization use cases](https://docs.cloud.google.com/docs/authentication/use-cases).
