# 身份验证基础知识 &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/authentication

在与 Google Cloud API 和服务互动之前，您需要证明自己是您所声称的身份。这种证明身份的过程称为身份验证。

如需向 Google Cloud进行身份验证，您必须提供_凭据_作为身份证明。例如，如需使用某项服务，您可能需要使用密码和动态验证码等凭据进行身份验证。

Google Cloud 将经过身份验证的用户称为_主账号_。当您尝试访问 Google Cloud 项目或存储桶等资源时， Google Cloud会检查您的主账号对所请求资源的访问权限级别。此过程称为[_授权_](https://docs.cloud.google.com/docs/get-started/authorization-access-control?hl=zh-cn)，由一个名为 Identity and Access Management (IAM) 的系统处理。

这些概念同样适用于代表您执行自动化任务的代码，这些代码称为_工作负载_。工作负载必须提供凭据来证明其身份并以主账号身份进行身份验证，之后 Google Cloud 才能确定工作负载对所请求资源的访问权限级别。

主账号类型
-----

您可以作为不同类型的主账号进行身份验证。您甚至可以在任务的不同阶段或不同的开发环境中使用不同的主账号类型。

主要主账号类型及其进行身份验证所需的凭据包括以下内容：

*   **用户账号**：这些是供人类用户执行交互式工作的 [Google 账号](https://docs.cloud.google.com/iam/docs/principals-overview?hl=zh-cn#google-account)，例如临时性管理任务、 Google Cloud 服务的非程序化配置、测试、实验和可观测性。
    
    您可以使用用户凭据（例如密码和一次性验证码）以用户账号身份进行身份验证。
    
*   **服务账号**：这些是特定于 Google Cloud 的账号，工作负载可以使用这些账号来访问服务或资源。您通常不会直接以服务账号身份进行身份验证。而是将服务账号附加到 Compute Engine 虚拟机等资源，或使用[服务账号模拟](https://docs.cloud.google.com/docs/authentication/use-service-account-impersonation?hl=zh-cn)。
    
*   **联合主账号**：这些身份引用了外部身份提供方中的用户账号或服务账号。 Google Cloud支持两种联合身份，这两种身份的名称相似：
    
    *   **[**员工身份联合**](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=zh-cn)**：允许人类用户使用由外部身份提供方管理的身份登录 Google Cloud 。如果贵组织已设置单点登录 (SSO)，您可以使用此类身份向 Google Cloud进行身份验证。
        
        您的身份提供方必须支持 [OpenID Connect (OIDC)](https://openid.net/connect/) 或 [SAML 2.0](http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-tech-overview-2.0.html)，才能使用员工身份联合。
        
    *   **[工作负载身份联合](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=zh-cn)**：使在 Google Cloud 外部运行的工作负载能够对 Google Cloud 资源进行操作。
        
        您可以将工作负载身份联合与以下工作负载搭配使用：使用 [X.509 客户端证书](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-x509-certificates?hl=zh-cn)进行身份验证；在 [Amazon Web Services (AWS) 或 Azure](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-other-clouds?hl=zh-cn) 上运行；使用本地 [Active Directory](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-active-directory?hl=zh-cn)；使用部署服务（例如 [GitHub 和 GitLab](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-deployment-pipelines?hl=zh-cn)）；以及使用支持 [OpenID Connect (OIDC) 或安全声明标记语言 (SAML) V2.0](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-other-providers?hl=zh-cn) 的任何身份提供方。
        

如需详细了解 Google Cloud中的这些主账号类型和其他受支持的主账号类型，请参阅[主账号类型](https://docs.cloud.google.com/iam/docs/principals-overview?hl=zh-cn#principal-types)。

为 Google Cloud 组织设置身份验证时，您可能需要将现有系统和工作流集成到 Google Cloud中：

*   如果您有想要使用的现有身份提供方，则需要设置[员工身份联合](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=zh-cn)。
    
*   如果您有在 Google Cloud 之外运行的工作负载需要访问Google Cloud 资源，则需要设置[工作负载身份联合](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=zh-cn)。
    

我们还建议您执行以下操作，以帮助保护 Google Cloud环境的安全：

*   确保为用户[启用多重身份验证](https://docs.cloud.google.com/docs/authentication/mfa-requirement?hl=zh-cn)。
    
*   检查是否需要更改[重新验证设置](https://docs.cloud.google.com/docs/authentication/reauthentication?hl=zh-cn)。
    
*   创建用于[管理 API 密钥](https://docs.cloud.google.com/docs/authentication/api-keys-best-practices?hl=zh-cn)的政策。
    
*   创建用于[管理服务账号](https://docs.cloud.google.com/iam/docs/best-practices-for-managing-service-account-keys?hl=zh-cn)的政策。
    

对人员和工作负载进行身份验证
--------------

您向 Google Cloud 进行身份验证的方式取决于您使用的 API 和服务，以及您与这些 API 和服务互动的方式。

### 验证是否是真人

在执行手动交互式工作时（例如处理临时性管理任务、设置资源、更改配置、进行实验和浏览日志），您可以使用用户账号的凭据进行身份验证。

### 对工作负载进行身份验证

无论您是在本地设备、Google Cloud、本地还是其他云端开发和运行代码，对工作负载进行身份验证的最灵活、最可移植的方式是通过一种称为_应用默认凭证 (ADC)_ 的机制来提供凭据。

实现 ADC 的库（例如 [Google Cloud 客户端库](https://docs.cloud.google.com/apis/docs/client-libraries-explained?hl=zh-cn)）会检查其运行环境中的已知凭据位置。这意味着，如果您更改代码的运行位置，则无需更改代码本身，只需更改用于相应环境的凭据即可。

例如，在本地开发时，您可以设置环境，以便 ADC 使用您的用户凭据进行身份验证。当您的代码准备好投入生产环境时，您可以将其原封不动地部署到 Compute Engine 虚拟机实例，并将环境设置为使用短期服务账号凭据进行身份验证。

在以下情况下，您无法使用 ADC 进行身份验证：

*   对 gcloud CLI 进行身份验证时。
    
*   使用 [API 密钥](https://docs.cloud.google.com/docs/authentication/api-keys-use?hl=zh-cn)时。API 密钥只能与特定 API 搭配使用。
    

如需了解如何为特定环境设置 ADC，请参阅[设置应用默认凭证](https://docs.cloud.google.com/docs/authentication/provide-credentials-adc?hl=zh-cn)。

后续步骤
----

*   详细了解 [Google Cloud的不同身份验证方法](https://docs.cloud.google.com/docs/authentication?hl=zh-cn)。
    
*   如需了解如何在 Google Cloud中控制主账号可以访问的内容，请参阅[授权和访问权限控制](https://docs.cloud.google.com/docs/get-started/authorization-access-control?hl=zh-cn)。
