# Google Cloud 安全最佳实践目录 &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/security-best-practices-genai

您可以使用 Google Cloud 安全最佳实践和指南，在Google Cloud上发现和实现工作负载和支持服务的安全功能。

安全最佳实践是 Google 针对金融服务等行业中的现有监管和安全实践提供的补充指南。 Google Cloud 最佳实践和指南侧重于基础工作负载安全控制。

这些安全最佳实践旨在帮助首席信息安全官 (CISO)、安全从业人员以及风险和合规专员在 Google Cloud中采用和部署工作负载，同时专注于安全性和合规性。我们的建议符合[美国国家标准与技术研究院 (NIST) 800-53](https://csrc.nist.gov/pubs/sp/800/53/r5/upd1/final) 和[网络风险研究所 (CRI)](https://cyberriskinstitute.org/) 框架的要求。

这些最佳实践还支持[命运共同体模型](https://docs.cloud.google.com/architecture/framework/security/shared-responsibility-shared-fate?hl=zh-cn)，我们致力于与各行业合作，为各种工作负载构建更安全、更具弹性的云基础设施。命运共同体模型包括部署、运营和风险转移。因此，这些建议侧重于工作负载部署和操作，尤其是与合规性相关的方面。

我们知道，实现合规性和安全性并非易事。如需更多帮助，请[与 Google Cloud安全团队](https://cloud.google.com/contact/security?hl=zh-cn)联系。

安全最佳实践的结构
---------

安全最佳实践以_控制措施_的形式进行组织，您可以查看和实施这些控制措施。每项控制措施都旨在解决 AI 技术栈的不同层级的问题。这些级别如下：

*   **安全的企业基础：**核心层，用于身份验证、访问管理、组织、网络、密钥管理、Secret Manager、日志记录、监控、提醒、安全分析和智能体运维。
*   **AI 基础设施：**容器、计算和 TPU 层。
*   **研究和模型：**用于模型开发和主动模型保护的层。
*   **数据管理和上下文：**数据仓库、存储、数据库和敏感数据管理层。
*   **工具和推理平台：**Gemini Enterprise Agent Platform 的层，包括 Model Garden、模型构建器和智能体构建器。
*   **智能体和应用：**Gemini Enterprise、Google Workspace、AI 应用和其他软件控制层。

下图展示了这些层级如何相互叠加。

控制措施的结构如下：

*   [**推荐的 Identity and Access Management (IAM) 角色**](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/recommended-iam-groups?hl=zh-cn)：建议为组织中的用户群组分配的 IAM 角色。
    
*   [**安全的企业基础控制措施**](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/secure-enterprise-foundations?hl=zh-cn)：这些最佳实践可让您为Google Cloud中的工作负载创建安全的基础。
    
*   [**基础设施控制措施：**](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/infrastructure?hl=zh-cn)这些最佳实践可让您将安全控制措施应用于计算、容器和加速器。
    
*   [**数据管理控制**](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/data-management?hl=zh-cn)：通过这些最佳实践，您可以对数据仓库和数据存储区应用安全控制。
    
*   [**工具和推理控制：**](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/tools-inference?hl=zh-cn)这些最佳实践可让您将安全控制措施应用于 Gemini Enterprise Agent Platform 组件。
    
*   [**智能体和应用控制措施：**](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/agents-applications?hl=zh-cn)这些最佳实践可让您对使用生成式 AI 的应用应用安全控制措施。
    

每项建议都是可审核的，并确保满足安全控制基准。

控制措施实施级别
--------

控制措施实施级别分为**必需**、**推荐**或**可选**。这些级别有助于确定我们强烈建议您执行的关键活动、我们强烈建议您考虑的活动，以及您可能需要根据具体要求和目标考虑的活动。

下表介绍了这些级别。

| 实施级别 | 说明 |
| --- | --- |
| 
必需

 | 

为您的 Google Cloud 环境实施这些准则。

 |
| 

推荐

 | 

根据具体应用场景实施这些准则。例如，如果您的环境包含敏感数据，建议的最佳实践可能是监控生成式 AI 工作负载中的敏感数据。

 |
| 

可选

 | 

根据您的应用场景和风险偏好，考虑其他准则。

 |

后续步骤
----

*   查看[推荐的用户群组和 Identity and Access Management 角色](https://docs.cloud.google.com/docs/security/security-best-practices-catalog/recommended-iam-groups?hl=zh-cn)。
