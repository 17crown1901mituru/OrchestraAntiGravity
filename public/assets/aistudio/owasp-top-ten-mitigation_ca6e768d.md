# Reduzir os riscos do OWASP Top 10:2025 no Google Cloud &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/owasp-top-ten-mitigation

[Ir para o conteúdo principal](#main-content)

*   [Documentação](https://docs.cloud.google.com/docs)
    *   [Visão geral](https://docs.cloud.google.com/docs/security)
    *   [Guias](https://docs.cloud.google.com/docs/security/overview/whitepaper)
*   [Console](https://console.cloud.google.com/)

*   Artigos sobre segurança
    
*   [Visão geral da segurança do Google](https://docs.cloud.google.com/docs/security/overview/whitepaper)
*   [Acesso privilegiado no Google Cloud](https://docs.cloud.google.com/docs/security/privileged-access-management)
*   Segurança da infraestrutura
    
    *   [Visão geral do design de segurança da infraestrutura](https://docs.cloud.google.com/docs/security/infrastructure/design)
    *   [BeyondProd](https://docs.cloud.google.com/docs/security/beyondprod)
    *   [Autorização binária para o Borg](https://docs.cloud.google.com/docs/security/binary-authorization-for-borg)
    *   [Integridade da inicialização](https://docs.cloud.google.com/docs/security/boot-integrity)
    *   [Espaço físico–lógico do data center](https://docs.cloud.google.com/docs/security/physical-to-logical-space)
    *   [Atestado remoto](https://docs.cloud.google.com/docs/security/remote-attestation)
    *   [Proteções de serviços de produção](https://docs.cloud.google.com/docs/security/production-services-protection)
    *   [Arquitetura de segurança de hardware do Titanium](https://docs.cloud.google.com/docs/security/titanium-hardware-security-architecture)
    *   [Chip de hardware Titan](https://docs.cloud.google.com/docs/security/titan-hardware-chip)
    

*   Gestão de incidentes
    

*   Diretrizes de segurança
    

*   [Práticas recomendadas de segurança para IA generativa](https://docs.cloud.google.com/docs/security/genai-security-bps)
*   [Práticas recomendadas de segurança para o GKE](https://docs.cloud.google.com/docs/security/gke-security-bps)
*   [Práticas recomendadas de segurança para o VMware Engine](https://docs.cloud.google.com/docs/security/vmware-engine-security-bps)
*   [Gestão de vulnerabilidades com IA](https://docs.cloud.google.com/docs/security/vuln-management-ai)
*   [Reduzir os 10 principais riscos do OWASP:2025 no Google Cloud](https://docs.cloud.google.com/docs/security/owasp-top-ten-mitigation)
*   Conformidade regulamentar
    
*   [Escopo de compliance do Departamento de Defesa para o Google Distributed Cloud](https://docs.cloud.google.com/docs/security/compliance/il6-gdc-compliance-scope)
*   FedRAMP
    
    *   [Visão geral do FedRAMP](https://docs.cloud.google.com/docs/security/compliance/fedramp-trust-center)
    *   [Guia de implementação do FedRAMP do Google Cloud](https://docs.cloud.google.com/docs/security/compliance/fedramp-implementation-guide)
    *   [Escopo de conformidade com o FedRAMP e o DoD para o Google Cloud e o Google Workspace](https://docs.cloud.google.com/docs/security/compliance/fedramp-dod-compliance-scope)
    *   [Configurar o Gemini para o Setor Público para FedRAMP 20x Classe B](https://docs.cloud.google.com/docs/security/compliance/config-fedramp20x-gemini-gov)
    *   [Configurar o Gemini para o Setor Público para FedRAMP High e DOD IL4](https://docs.cloud.google.com/docs/security/compliance/deploy-gemini-gov)
    
*   [Implantar o app e a CLI do Antigravity para o setor público dos EUA](https://docs.cloud.google.com/docs/security/compliance/deploy-antigravity-us-public-sector)

*   [Conformidade com a HIPAA no Google Cloud e no Google Workspace](https://docs.cloud.google.com/docs/security/compliance/hipaa)

*   Produtos de segurança
    

Reduzir os riscos do OWASP Top 10:2025 no Google Cloud
------------------------------------------------------

Este documento identifica os serviços e as estratégias de mitigação que podem ajudar você a se proteger contra os ataques no nível do aplicativo descritos nos [10 principais da OWASP:2025](https://owasp.org/Top10/2025/) (em inglês). Google Cloud Criada pela [Open Web Application Security (OWASP) Foundation](https://owasp.org/) (em inglês), a lista OWASP Top 10:2025 contém os 10 principais riscos de segurança em um ciclo de vida de desenvolvimento de software (SDLC). Embora nenhum serviço possa garantir proteção total contra esses riscos, aplicar esses serviços quando eles fazem sentido na sua arquitetura pode contribuir para uma solução de segurança de várias camadas.

A [infraestrutura do Google](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=pt-br) foi projetada para ajudar você a criar, implantar e operar serviços com controles de segurança robustos. A segurança física e operacional, a criptografia de dados em repouso e em trânsito e muitas outras proteções básicas de infraestrutura são gerenciadas pelo Google. Você herda esses benefícios ao implantar seus aplicativos no Google Cloud, mas talvez seja necessário tomar outras medidas para proteger seu aplicativo contra ataques específicos.

Matriz de compliance
--------------------

Os serviços do Google Cloud listados na tabela a seguir podem ajudar na proteção contra os 10 principais riscos de segurança identificados pelo OWASP Top 10:2025:

| Categoria OWASP Top 10:2025 | Controles principais Google Cloud e do Wiz |
| --- | --- |
| 
[A01: controle de acesso corrompido](https://owasp.org/Top10/2025/A01_2025-Broken_Access_Control/)

 | 

[Access Context Manager](#access-context), [Agent Gateway](#agent-security), [Agent Identity](#agent-security), [Apigee](#apigee), [Google Cloud Armor](#cloud-armor), [Firebase App Check](#firebase-security), [Firebase Security Rules](#firebase-security), [Cloud Next Generation Firewall (Cloud NGFW)](#cloud-ngfw), [Identity-Aware Proxy (IAP)](#identity-aware-proxy), [Identity and Access Management (IAM)](#identity-access), [Organization Policy Service](#organization-policy), [VPC Service Controls](#vpc-service), [Wiz Cloud](#wiz-cloud)

 |
| 

[A02: configuração incorreta de segurança](https://owasp.org/Top10/2025/A02_2025-Security_Misconfiguration/)

 | 

[Access Context Manager](#access-context), [Inventário de recursos do Cloud](#cloud-asset), [Google Security Operations](#google-secops), [Política da Organização](#organization-policy), [Security Command Center Premium](#security-command), [VPC Service Controls](#vpc-service), [Wiz Cloud](#wiz-cloud)

 |
| 

[A03: falhas na cadeia de suprimentos de software](https://owasp.org/Top10/2025/A03_2025-Software_Supply_Chain_Failures/)

 | 

[Artifact Registry](#artifact-registry), [Assured Open Source Software (Assured OSS)](#assured-open), [Binary Authorization](#binary-authorization), [Cloud Build](#cloud-build-deploy), [Cloud Deploy](#cloud-build-deploy), [Cloud Workstations](#cloud-workstations), [CodeMender](#codemender), [Wiz Code](#wiz-code)

 |
| 

[A04: falhas criptográficas](https://owasp.org/Top10/2025/A04_2025-Cryptographic_Failures/)

 | 

[Apigee](#apigee), [Certificate Authority Service](#certificate-services), [Certificate Manager](#certificate-services), [Cloud Key Management Service](#cloud-key), [Cloud Load Balancing](#cloud-load), [Cloud Workstations](#cloud-workstations), [Confidential Computing](#confidential-computing), [Organization Policy](#organization-policy), [Secret Manager](#secret-manager), [Sensitive Data Protection](#sensitive-data), [Wiz Cloud](#wiz-cloud)

 |
| 

[A05: injeção](https://owasp.org/Top10/2025/A05_2025-Injection/)

 | 

[Apigee](#apigee), [Cloud Armor](#cloud-armor), [CodeMender](#codemender), [Regras de segurança do Firebase](#firebase-security), [Cloud Next Generation Firewall (Cloud NGFW)](#cloud-ngfw), [Model Armor](#model-armor), [Security Command Center Premium](#security-command), [VirusTotal](#virustotal), [Wiz Code](#wiz-code)

 |
| 

[A06: design não seguro](https://owasp.org/Top10/2025/A06_2025-Insecure_Design/)

 | 

[Apigee](#apigee), [CodeMender](#codemender), [Mandiant](#mandiant-ai), [Wiz Cloud](#wiz-cloud)

 |
| 

[A07: falhas de autenticação](https://owasp.org/Top10/2025/A07_2025-Authentication_Failures/)

 | 

[Access Context Manager](#access-context), [Identidade do agente](#agent-security), [Apigee](#apigee), [Cloud Armor](#cloud-armor), [Cloud Identity](#cloud-identity), [Firebase App Check](#firebase-security), [Firebase Authentication](#firebase-security), [Google Cloud Fraud Defense](#fraud-defense), [IAM (Privileged Access Manager, Federação de identidade da carga de trabalho e Workforce Identity Federation)](#identity-access), [IAP](#identity-aware-proxy), [Identity Platform](#identity-platform), [Security Command Center Premium](#security-command), [Secret Manager](#secret-manager), [Titan Security Keys](#cloud-identity), [Wiz Code](#wiz-code)

 |
| 

[A08: falhas de integridade de dados ou software](https://owasp.org/Top10/2025/A08_2025-Software_or_Data_Integrity_Failures/)

 | 

[Análise de artefatos](#artifact-registry), [Assured OSS](#assured-open), [Autorização binária](#binary-authorization), [Cloud Armor](#cloud-armor), [Cloud Build](#cloud-build-deploy), [Cloud Deploy](#cloud-build-deploy), [Firebase App Check](#firebase-security), [Security Command Center Premium](#security-command), [VirusTotal](#virustotal), [Wiz Defend](#wiz-defend)

 |
| 

[A09: falhas de geração de registros e alertas de segurança](https://owasp.org/Top10/2025/A09_2025-Security_Logging_and_Alerting_Failures/)

 | 

[Aprovação de acesso e Transparência no acesso](#access-approval), [Apigee](#apigee), [Google SecOps](#google-secops), [Cloud Logging](#cloud-observability), [Cloud Monitoring](#cloud-observability), [Proteção de dados sensíveis](#sensitive-data), [Wiz Defend](#wiz-defend)

 |
| 

[A10: tratamento inadequado de condições excepcionais](https://owasp.org/Top10/2025/A10_2025-Mishandling_of_Exceptional_Conditions/)

 | 

[Cloud Armor](#cloud-armor), [Cloud Load Balancing](#cloud-load), [Error Reporting](#cloud-observability), [Model Armor](#model-armor)

 |

Serviços doGoogle Cloud
-----------------------

As seções a seguir descrevem as práticas recomendadas do OWASP Top 10 para serviços principais do Google Cloud .

### Aprovação de acesso e Transparência no acesso

A [Transparência no acesso](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/overview?hl=pt-br) e a [Aprovação de acesso](https://docs.cloud.google.com/assured-workloads/access-approval/docs/overview?hl=pt-br) permitem verificar o acesso do provedor de nuvem. Com a Transparência no acesso, é possível registrar o motivo de cada acesso feito pela equipe do Google. Com a aprovação de acesso, é possível aprovar ou denegar as solicitações de acesso feitas pela equipe do Google que dá suporte ao serviço.

Aplica-se a A09: falhas de geração de registros e alertas de segurança.

Confira as práticas recomendadas a seguir:

*   Automatize o processo de aprovação de acesso. Para isso, configure o Access Approval para enviar os metadados de pedido de aprovação de acesso recebidos a um [tópico do Pub/Sub](https://docs.cloud.google.com/assured-workloads/access-approval/docs/review-approve-access-requests-google-keys?hl=pt-br#email-pubsub). Crie uma assinatura do Pub/Sub que envie o payload JSON para seu endpoint de webhook personalizado (como um serviço autenticado do Cloud Run, funções do Cloud Run ou um gateway de API empresarial) para processamento.
*   Trate os [registros de transparência no acesso](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/reading-logs?hl=pt-br) como telemetria de segurança crítica. Crie [métricas baseadas em registros](https://docs.cloud.google.com/logging/docs/logs-based-metrics?hl=pt-br) e [políticas de alertas](https://docs.cloud.google.com/logging/docs/alerting/log-based-alerts?hl=pt-br) no Monitoring para sinalizar seu centro de operações de segurança (SecOps) se a equipe do Google acessar recursos sensíveis sem um tíquete de suporte ativo e correspondente.
*   [Exporte os registros de Transparência no acesso diretamente](https://docs.cloud.google.com/chronicle/docs/ingestion/default-parsers/ingest-gcp-logs?hl=pt-br#export-logs) para o Google SecOps ou seu SIEM empresarial centralizado.
*   Crie uma política de compliance para auditar seus fluxos de registros regularmente e verificar se os eventos de acesso de emergência `auto_approved` estão relacionados a um incidente documentado de alta gravidade.
*   Para controle criptográfico, use as [Justificativas de acesso às chaves](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/key-access-justifications?hl=pt-br) para forçar o sistema a solicitar programaticamente uma aprovação de descriptografia de chave.

### Access Context Manager

O [Access Context Manager](https://docs.cloud.google.com/access-context-manager/docs/overview?hl=pt-br) é o mecanismo de acesso baseado no contexto do Google Cloud. Com o Access Context Manager, é possível definir níveis de acesso baseados em atributos (como intervalos de endereços IP do cliente, postura de segurança do dispositivo e localização geográfica) para IAP, VPC Service Controls e IAM.

Válido para:

*   A01: controle de acesso corrompido
*   A02: configuração incorreta de segurança
*   A07: falhas de autenticação

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Crie níveis de segurança reutilizáveis e hierárquicos para a política de acesso da sua organização. Crie [níveis de acesso básicos](https://docs.cloud.google.com/access-context-manager/docs/create-basic-access-level?hl=pt-br) para testar atributos padrão. Para condições complexas e multifatoriais, implante [níveis de acesso personalizados](https://docs.cloud.google.com/access-context-manager/docs/create-custom-access-level?hl=pt-br) para avaliar estados avançados de dispositivos e sinais de endpoints de terceiros.
*   Use a [Verificação de endpoints](https://docs.cloud.google.com/endpoint-verification/docs/overview?hl=pt-br) ou o [Chrome Enterprise Core](https://support.google.com/chrome/a/answer/9116814?hl=pt-br) para aplicar restrições no nível do dispositivo, como criptografia de disco completo, bloqueio de tela ativo e uma versão aprovada do sistema operacional.
*   Para ajudar a proteger repositórios de dados de alto valor com o VPC Service Controls, adicione [níveis de acesso](https://docs.cloud.google.com/vpc-service-controls/docs/use-access-levels?hl=pt-br) às regras de entrada do VPC Service Controls. Se uma chave de conta de serviço for vazada, um invasor não poderá usar apenas a chave para consultar o BigQuery ou o Cloud Storage de um endereço IP público não autorizado ou de uma máquina não confiável.
*   Para estender a proteção de confiança zero a aplicativos da Web e túneis administrativos de VM, anexe [níveis de acesso diretamente aos recursos protegidos pelo IAP](https://docs.cloud.google.com/iap/docs/cloud-iap-context-aware-access-howto?hl=pt-br).

Confira as práticas recomendadas a seguir para A02: configuração incorreta de segurança:

*   Implemente [políticas de acesso com escopo](https://docs.cloud.google.com/access-context-manager/docs/scoped-policies?hl=pt-br) vinculadas a pastas específicas para delegar o gerenciamento de políticas locais a equipes de projetos individuais e isolar as mudanças do restante da organização.
*   Para evitar que regras de entrada órfãs se tornem backdoors silenciosos, revise e remova regularmente intervalos de IP desativados, sub-redes de parceiros expiradas e atributos de dispositivos obsoletos.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Use vinculações de acesso do usuário para definir durações máximas de sessão rigorosas. Configure a [política de reautenticação](https://docs.cloud.google.com/access-context-manager/docs/session-controls-for-reauthentication?hl=pt-br) para exigir `SECURITY_KEY` (FIDO2 ou WebAuthn). Para aplicar restrições mais rígidas a ambientes de alto risco, configure [`scopedAccessSettings`](https://docs.cloud.google.com/access-context-manager/docs/manage-access-bindings?hl=pt-br#update_an_access_binding) para substituir as durações de sessão padrão em aplicativos sensíveis.

### Gateway de Agente e Identidade do Agente

O [Gateway de Agente](https://docs.cloud.google.com/gemini-enterprise-agent-platform/govern/gateways/agent-gateway-overview?hl=pt-br) e a [identidade do agente](https://docs.cloud.google.com/iam/docs/agent-identity-overview?hl=pt-br) oferecem aplicação dedicada de políticas de rede, gerenciamento do ciclo de vida de identidade e autenticação criptográfica para agentes de IA e fluxos de trabalho agênticos.

Válido para:

*   A01: controle de acesso corrompido
*   A07: falhas de autenticação

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Em ambientes com sistemas multiagente, ferramentas MCP externas ou pipelines autônomos, use o Agent Gateway como uma rede dedicada e um ponto de aplicação de políticas para ajudar a reduzir falhas no controle de acesso agêntico.
*   Configure [políticas de autorização granular](https://docs.cloud.google.com/iam/docs/agent-identity-overview?hl=pt-br#security-governance) para identidades de agentes e restrinja o acesso a ferramentas e a recuperação de dados apenas aos recursos necessários para o fluxo de trabalho específico do agente.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Para autenticar agentes autônomos e integrações de ferramentas sem incorporar chaves de API ou senhas estáticas, [gere e atribua uma identidade de agente](https://docs.cloud.google.com/iam/docs/create-and-deploy-agent?hl=pt-br) a cada agente.
*   Configure a identidade do agente para emitir um [certificado X.509](https://docs.cloud.google.com/iam/docs/agent-identity-overview?hl=pt-br#agent-credentials) como as credenciais do agente. Esses certificados ajudam a evitar o roubo de tokens. Assim, se um token de acesso for interceptado, ele não poderá ser usado em outros ambientes.

### Apigee

O [Apigee](https://docs.cloud.google.com/apigee/docs/api-platform/get-started/what-apigee?hl=pt-br) fornece mecanismos centralizados no nível do gateway usando [proxies de API](https://docs.cloud.google.com/apigee/docs/api-platform/fundamentals/structure-api-proxies?hl=pt-br) para aplicar padrões criptográficos, validar payloads assinados e criptografar dados de aplicativos em trânsito e em repouso. Ao atuar como um gateway de proxy reverso para o tráfego de API, a Apigee executa verificações de limite e estrutura para ajudar a validar payloads. A Apigee oferece políticas integradas de autenticação de API, OAuth e verificação de JSON Web Token (JWT) para estabelecer limites de identidade fortes. A Apigee inclui várias maneiras de executar a geração de registros, o monitoramento, o tratamento de erros e a geração de registros de auditoria.

Válido para:

*   A01: controle de acesso corrompido
*   A04: falhas criptográficas
*   A05: injeção
*   A06: design não seguro
*   A07: falhas de autenticação
*   A09: falhas de geração de registros e alertas de segurança

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Use [proxies de API](https://docs.cloud.google.com/apigee/docs/api-platform/deploy/ui-deploy-overview?hl=pt-br) para concluir o seguinte:
    
    *   Interceptar solicitações em que um invasor tenta acessar os registros de outro usuário manipulando variáveis de ID no caminho da solicitação de API.
        
    *   Impeça que clientes padrão executem métodos administrativos restritos ou operações de alto privilégio.
        
*   Para o plano de gerenciamento de API, aplique [controles de acesso](https://docs.apigee.com/api-platform/system-administration/understanding-roles?hl=pt-br), [autenticação](https://docs.cloud.google.com/apigee/docs/api-platform/security/google-auth/overview?hl=pt-br) e armazenamento de secrets usando [mapas de chave-valor criptografados](https://docs.cloud.google.com/apigee/docs/api-platform/develop/accessing-config-data?hl=pt-br#when-to-use-key-value-maps), o Secret Manager ou [secrets do Kubernetes](https://docs.cloud.google.com/apigee/docs/api-platform/develop/accessing-config-data?hl=pt-br#when-to-use-kubernetes-secrets) (somente implantações híbridas).
    
*   Use [políticas do OAuth](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/oauthv2-policy?hl=pt-br) e tokens [JWT](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/jwt-policies-overview?hl=pt-br) para verificar assinaturas. Mapeie endpoints e ações sensíveis para escopos OAuth granulares de alto privilégio (por exemplo, `delete:account` ou `write:billing`). Use a [política `OAuthV2`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/oauthv2-policy?hl=pt-br) para validar esses escopos no ponto de entrada da API e retorne um código de status HTTP `403 Forbidden` para qualquer cliente que não tenha as permissões corretas.
    
*   Ative a [Segurança avançada de API](https://docs.cloud.google.com/apigee/docs/api-security/aas-apigee-overview?hl=pt-br) para analisar o tráfego em busca de padrões comportamentais anômalos e iniciar [ações de segurança](https://docs.cloud.google.com/apigee/docs/api-security/security-actions?hl=pt-br).
    

Confira as práticas recomendadas para A04: falhas criptográficas:

*   Criptografe dados sensíveis no seu aplicativo e aplique uma validação criptográfica estrita antes que o tráfego chegue ao aplicativo de back-end. Configure seu ambiente do Apigee com [chaves de criptografia gerenciadas pelo cliente (CMEK)](https://docs.cloud.google.com/apigee/docs/api-platform/get-started/cmek-concepts?hl=pt-br) usando o Cloud KMS.
*   Use o [TLS unidirecional e bidirecional](https://docs.cloud.google.com/apigee/docs/api-platform/system-administration/options-configuring-tls?hl=pt-br) para criptografar informações sensíveis no nível do protocolo. Para integrações de negócios de servidor para servidor ou de alto risco, configure o [TLS mútuo (mTLS)](https://docs.cloud.google.com/apigee/docs/hybrid/v1.16/ingress-tls?hl=pt-br) no gateway de entrada do Apigee.
*   Use as políticas [`VerifyJWT` e `VerifyJWS`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/verify-jwt-policy?hl=pt-br) para exigir que os tokens recebidos tenham uma assinatura criptográfica válida antes que a solicitação seja processada. Use técnicas padrão do [OAuth](https://docs.cloud.google.com/apigee/docs/api-platform/tutorials/secure-calls-your-api-through-oauth-20-client-credentials?hl=pt-br) e considere implementar HMAC, hash de payload, validação de estado ou valor de uso único e Proof Key for Code Exchange (PKCE) para proteger criptograficamente cada solicitação.
*   [Mascare dados sensíveis](https://docs.cloud.google.com/apigee/docs/api-platform/security/data-masking?hl=pt-br) para que eles sejam criptografados e ocultados ao usar a [ferramenta de depuração da Apigee](https://docs.cloud.google.com/apigee/docs/api-platform/debug/trace?hl=pt-br).

Confira as práticas recomendadas a seguir para A05: injeção:

*   Implante políticas de proteção contra ameaças da Apigee para limpar parâmetros de entrada e bloquear tentativas de injeção de SQL, NoSQL e comandos na camada de gateway:
    *   [`JSONThreatProtection`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/json-threat-protection-policy?hl=pt-br)
    *   [`XMLThreatProtection`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/xml-threat-protection-policy?hl=pt-br)
    *   [`RegularExpressionProtection`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/regular-expression-protection?hl=pt-br)
    *   [`SOAPMessageValidation`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/message-validation-policy?hl=pt-br)

Confira as práticas recomendadas para A06: design não seguro:

*   Valide as solicitações recebidas com a [política `OASValidation`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/oas-validation-policy?hl=pt-br) para mensagens de solicitação ou resposta recebidas em relação à especificação OpenAPI.
*   Mitigue picos de tráfego e sobrecarga de back-end implementando a [política `SpikeArrest`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/spike-arrest-policy?hl=pt-br) e a [política `Quota`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/quota-policy?hl=pt-br).
*   Use [regras de tratamento de falhas](https://docs.cloud.google.com/apigee/docs/api-platform/fundamentals/fault-handling?hl=pt-br) para interceptar erros de back-end (como uma falha no banco de dados) e reescrevê-los em respostas HTTP genéricas.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Implemente a [validação de chave de API](https://docs.cloud.google.com/apigee/docs/api-platform/security/setting-api-key-validation?hl=pt-br) para suas APIs voltadas para desenvolvedores para que a Apigee possa verificar se a chave de API de um aplicativo cliente está presente, é válida e tem autorização para acessar o recurso de API solicitado.
*   Para ajudar a evitar o roubo de tokens de sessão e ataques de repetição, [implemente a demonstração de prova de posse (DPoP)](https://discuss.google.dev/t/navigating-the-industry-shift-in-client-authentication-for-apigee-mtls/289627?hl=pt-br). O DPoP vincula tokens à chave pública do remetente para reduzir a repetição de tokens.
*   Proteja a geração de tokens e os endpoints de login contra ataques automatizados de força bruta combinando limites de taxa [`SpikeArrest`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/spike-arrest-policy?hl=pt-br) com a [integração do reCAPTCHA Enterprise](https://discuss.google.dev/t/apigee-x-and-recaptcha-enterprise-best-friends-ever/84641?hl=pt-br).

Confira as práticas recomendadas a seguir para A09: falhas de geração de registros e alertas de segurança:

*   Transmita de forma assíncrona metadados de transações de API estruturadas para o Logging ou SIEMs de terceiros. Anexe sua [política `MessageLogging`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/policies/message-logging-policy?hl=pt-br) a `PostClientFlow`, que é executada depois que a resposta é entregue ao cliente.
*   Centralize os registros de auditoria da plataforma para rastrear modificações em proxies de API, credenciais e ambientes de implantação. Para evitar que modificações não autorizadas de proxy passem despercebidas, integre o Apigee aos registros de auditoria do Cloud. Para mais informações, consulte [Geração de registros de auditoria da Apigee](https://docs.cloud.google.com/apigee/docs/hybrid/v1.16/audit-logging?hl=pt-br) e [Geração de registros de auditoria do gerenciamento de APIs da Apigee](https://docs.cloud.google.com/apigee/docs/api-observation/audit-logging?hl=pt-br).
*   Configure [alertas de segurança avançada da API](https://docs.cloud.google.com/apigee/docs/api-security?hl=pt-br) no Monitoring para notificar as equipes de SecOps sobre campanhas de raspagem automatizadas, abuso de credenciais e regressões de pontuação de segurança.
*   Para limpar as variáveis fornecidas pelo usuário nos modelos de mensagens de registro, coloque-as na [função `escapeJSON()`](https://docs.cloud.google.com/apigee/docs/api-platform/reference/message-template-intro?hl=pt-br#character-escape-and-encoding-functions).
*   Se você transmitir metadados de registros para um SIEM externo, configure a política `MessageLogging` para usar Syslog por TLS (porta TCP `6514`) e criptografar seus dados em trânsito.

### Artifact Registry e Artifact Analysis

O [Artifact Registry](https://docs.cloud.google.com/artifact-registry/docs/overview?hl=pt-br) é um local único para sua organização gerenciar imagens de contêiner e pacotes de linguagem. O [Artifact Analysis](https://docs.cloud.google.com/artifact-analysis/docs/artifact-analysis?hl=pt-br) oferece verificação integrada de vulnerabilidades, geração de lista de materiais de software (SBOM) e armazenamento de metadados para artefatos armazenados no Artifact Registry.

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A08: falhas de integridade de dados ou software

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Reduza a superfície de ataque e evite a implantação de imagens legadas vulneráveis configurando [políticas de limpeza](https://docs.cloud.google.com/artifact-registry/docs/repositories/cleanup-policy-overview?hl=pt-br) para excluir imagens candidatas a lançamento sem versão, sem tag ou desatualizadas após um período de armazenamento predefinido.
*   Proteja-se contra ataques de confusão de dependências configurando [repositórios virtuais](https://docs.cloud.google.com/artifact-registry/docs/repositories/virtual-overview?hl=pt-br) com [prioridades de repositório](https://docs.cloud.google.com/artifact-registry/docs/repositories/virtual-overview?hl=pt-br#repo-priority) upstream que priorizam repositórios de artefatos internos em vez de registros públicos.
*   Aplique tags de imagem imutáveis ou implante estritamente por resumo criptográfico (`sha256:...`) para evitar ataques de mutação de tag.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Ative a [verificação automática de vulnerabilidades](https://docs.cloud.google.com/artifact-analysis/docs/container-scanning-overview?hl=pt-br) e a [geração de lista de materiais de software (SBOM)](https://docs.cloud.google.com/artifact-analysis/docs/sbom-overview?hl=pt-br) no Artifact Analysis para detectar CVEs críticos antes da implantação.
*   Integre os metadados do Artifact Analysis com atestados da [autorização binária](https://docs.cloud.google.com/binary-authorization/docs/overview?hl=pt-br) para bloquear a implantação de imagens que não atendem aos limites de segurança.

### Assured OSS

Com o [Assured OSS](https://docs.cloud.google.com/assured-open-source-software/docs/overview?hl=pt-br), você pode incorporar os pacotes de OSS que o Google valida e usa nos seus próprios fluxos de trabalho de desenvolvedor.

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A08: falhas de integridade de dados ou software

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Configure [repositórios remotos](https://docs.cloud.google.com/security-command-center/docs/aoss-remote-repository-access?hl=pt-br) para apontar para o Assured OSS.
*   Verifique se as bibliotecas de código aberto nos seus builds contêm uma assinatura do Google válida e um registro de procedência do build SLSA verificável. Configure verificações de qualidade no Cloud Build para verificar essas declarações antes de compilar binários de aplicativos.
*   Configure os gerenciadores de pacotes (como `pip.conf`, `settings.xml` ou `build.gradle`) nas imagens de base do Cloud Workstations para apontar apenas para seus repositórios internos do Assured OSS.
*   Use os [metadados gerados pelo Assured OSS](https://docs.cloud.google.com/security-command-center/docs/aoss-access-security-metadata?hl=pt-br) para determinar se uma CVE recém-divulgada em um pacote de código aberto é explorável no seu contexto de implantação específico.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Promova a integridade dos pacotes em todos os pipelines de build configurando [repositórios virtuais upstream](https://docs.cloud.google.com/assured-open-source-software/docs/overview?hl=pt-br#aoss-repository-options) no Artifact Registry para aplicar assinaturas criptográficas verificadas pelo Google.
*   Use o [nível Premium do Assured OSS (parte do Security Command Center Premium)](https://docs.cloud.google.com/security-command-center/docs/integrate-aoss-with-scc?hl=pt-br) para automatizar o provisionamento de repositórios, acessar pacotes JavaScript (npm) selecionados e receber acesso a metadados de pacotes e notificações de vulnerabilidade.

A [autorização binária](https://docs.cloud.google.com/binary-authorization/docs/overview?hl=pt-br) verifica a integridade dos contêineres para que apenas imagens de contêiner confiáveis sejam implantadas. É possível criar políticas para permitir ou negar implantações com base na presença ou ausência de atestados. A autorização binária aplica políticas no nível do cluster. Assim, é possível configurar políticas diferentes para ambientes distintos.

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A08: falhas de integridade de dados ou software

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Configure os pipelines de implantação para referenciar e aplicar imagens de contêiner pelo resumo criptográfico SHA-256 exclusivo e imutável (como `@sha256`).
*   Implante a [validação contínua da autorização binária](https://docs.cloud.google.com/binary-authorization/docs/overview-cv?hl=pt-br) nos clusters do GKE para monitorar os pods ativos em relação à política da plataforma e gerar alertas no Logging se os contêineres em execução não estiverem mais em conformidade.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Aplique a [geração automática de atestados](https://docs.cloud.google.com/binary-authorization/docs/attestations?hl=pt-br) nos pipelines do Cloud Build ou do GitHub Actions. Crie requisitos de atestado progressivo para que as imagens passem por portões de validação sequenciais à medida que se aproximam da produção.
*   Para incidentes de produção de alta gravidade, ative [implantações de emergência breakglass](https://docs.cloud.google.com/binary-authorization/docs/using-breakglass?hl=pt-br). Configure políticas de alertas do Monitoring em eventos de registro de auditoria de breakglass para notificar sua equipe de SecOps quando ocorrer um bypass de admissão.

### Serviço de CA e Certificate Manager

O [Certificate Authority Service (CA Service)](https://docs.cloud.google.com/certificate-authority-service/docs/ca-service-overview?hl=pt-br) simplifica a implantação e o gerenciamento de autoridades de certificação (CAs) privadas. O [Certificate Manager](https://docs.cloud.google.com/certificate-manager/docs/overview?hl=pt-br) oferece provisionamento, renovação e gerenciamento centralizados de certificados TLS para o Cloud Load Balancing e o Cloud CDN.

Aplicável a A04: falhas criptográficas.

Confira as práticas recomendadas a seguir:

*   Use o [CA Service](https://docs.cloud.google.com/certificate-authority-service/docs?hl=pt-br) para automatizar a emissão e o gerenciamento do ciclo de vida de certificados particulares. Implante CAs raiz e intermediárias com suporte do [Cloud HSM](https://docs.cloud.google.com/certificate-authority-service/docs/best-practices?hl=pt-br#hsm) para proteger chaves particulares e use [modelos de certificado](https://docs.cloud.google.com/certificate-authority-service/docs/issue-certificate-using-template?hl=pt-br) para aplicar políticas criptográficas, como tamanhos mínimos de chave e usos de chave estendidos permitidos.
*   Ative os [Registros de auditoria do Cloud](https://docs.cloud.google.com/certificate-authority-service/docs/audit-logging?hl=pt-br) para monitorar eventos administrativos de alto risco, como revogação de CA, atualizações de política ou um aumento repentino nas solicitações de certificado. Encaminhe alertas para o Google SecOps para detectar possíveis ameaças internas ou pipelines de CI/CD comprometidos.
*   Configure o Gerenciador de certificados para usar certificados gerenciados pelo Google pareados com [autorizações de DNS](https://docs.cloud.google.com/certificate-manager/docs/domain-authorization?hl=pt-br). O Certificate Manager valida a propriedade do domínio, emite o certificado X.509 e processa as renovações 30 dias antes do vencimento.
*   Anexe [mapeamentos de certificados](https://docs.cloud.google.com/certificate-manager/docs/maps?hl=pt-br) a proxies HTTPS de destino para ativar a seleção e a rotação dinâmicas de certificados sem exigir reinicializações de proxy ou reconfigurações do balanceador de carga.
*   Para microsserviços internos ou balanceamento de carga híbrido, configure [mapeamentos de certificados](https://docs.cloud.google.com/certificate-manager/docs/maps?hl=pt-br) para emitir certificados particulares diretamente de um pool de CAs do CA Service particular.
*   Configure mapas de certificados para corresponder às solicitações de indicação de nome do servidor (SNI) recebidas a certificados específicos.

### Inventário de recursos do Cloud

Com o [Inventário de recursos do Cloud](https://docs.cloud.google.com/asset-inventory/docs/asset-inventory-overview?hl=pt-br), é possível monitorar sua infraestrutura no Google Cloud para identificar infraestruturas de TI órfãs ou não autorizadas.

Aplica-se a A02: configuração incorreta de segurança.

Confira as práticas recomendadas a seguir:

*   [Configure notificações](https://docs.cloud.google.com/asset-inventory/docs/monitor-asset-changes?hl=pt-br) para alertar você sobre recursos em execução inesperados, que podem ser protegidos incorretamente ou usar software desatualizado.
*   Use o [analisador de políticas do IAM](https://docs.cloud.google.com/asset-inventory/docs/view-resource-policies?hl=pt-br) para descobrir controles de acesso mal configurados, como buckets de armazenamento públicos com a permissão `allUsers`, papéis de conta de serviço com privilégios excessivos ou identidades órfãs.
*   [Exporte snapshots de recursos para o BigQuery](https://docs.cloud.google.com/asset-inventory/docs/export-bigquery?hl=pt-br) e audite as configurações de infraestrutura ao longo do tempo. Mantenha um registro de conformidade de linha de base em ambientes com vários projetos.

### Cloud Armor

O [Cloud Armor](https://docs.cloud.google.com/armor/docs/cloud-armor-overview?hl=pt-br) é um firewall de aplicativos da Web (WAF) adaptável que você implanta na borda da rede Google Cloud para ajudar a se defender contra ataques DDoS e bloquear payloads de injeção de SQLi ou XSS. O Cloud Armor inclui [regras de WAF pré-configuradas](https://docs.cloud.google.com/armor/docs/waf-rules?hl=pt-br) para proteger contra as vulnerabilidades do OWASP Top 10, limitar a superfície de ataque dos endpoints de autenticação e bloquear credenciais comprometidas.

Válido para:

*   A01: controle de acesso corrompido
*   A05: injeção
*   A07: falhas de autenticação
*   A08: falhas de integridade de dados ou software
*   A10: tratamento inadequado de condições excepcionais

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Aplique [regras WAF pré-configuradas](https://docs.cloud.google.com/armor/docs/waf-rules?hl=pt-br), como `evaluatePreconfiguredWaf('lfi-stable')`, para bloquear inclusões de arquivos locais e ataques de travessia de caminhos.
*   Aplique controles de acesso geográficos (também conhecidos como _geocercas_) [configurando uma regra de política de segurança](https://docs.cloud.google.com/armor/docs/configure-security-policies?hl=pt-br) que corresponda ao tráfego de entrada com base no código do país de origem usando o atributo `origin.region_code`.
*   Bloqueie endereços IP maliciosos conhecidos usando um [feed de inteligência contra ameaças](https://docs.cloud.google.com/armor/docs/threat-intelligence?hl=pt-br).
*   Restrinja o acesso externo a URLs sensíveis (como `/admin`, `/login` ou `/config`) [escrevendo uma regra de correspondência](https://docs.cloud.google.com/armor/docs/configure-security-policies?hl=pt-br#path-traversal-and-normalization).
*   Ative a [normalização de caminho do Cloud Armor](https://docs.cloud.google.com/armor/docs/configure-security-policies?hl=pt-br#path-traversal-and-normalization) no balanceador de carga, forçando o Cloud Armor a decodificar e padronizar os URLs recebidos antes de avaliar as políticas de segurança.

Confira as práticas recomendadas a seguir para A05: injeção:

*   Detecte e bloqueie injeção de SQL (`sqli-v422-stable`), scripting em vários locais (`xss-v422-stable`), injeção de comandos PHP (`php-v422-stable`) e injeção de Java (`java-v422-stable`) na borda da rede.
*   [Ajuste as regras de WAF pré-configuradas para diferentes níveis de sensibilidade](https://docs.cloud.google.com/armor/docs/rule-tuning?hl=pt-br) e resolva [falsos positivos](https://docs.cloud.google.com/armor/docs/troubleshooting?hl=pt-br#false-positives) antes de configurar as regras para negar ativamente o tráfego.
*   Ative a [regra de execução remota de código (RCE)](https://docs.cloud.google.com/armor/docs/waf-rules?hl=pt-br#remote_code_execution_rce) (`rce-v422-stable`) e a [regra de inclusão de arquivo remoto (RFI)](https://docs.cloud.google.com/armor/docs/waf-rules?hl=pt-br#remote_file_inclusion_rfi) (`rfi-v422-stable`) pré-configuradas para detectar outras técnicas complexas de injeção de comandos.
*   Para ataques de injeção diferentes de SQL ou PHP, crie [regras personalizadas](https://docs.cloud.google.com/armor/docs/rules-language-reference?hl=pt-br#preconfigured-rules). Com as regras personalizadas, é possível bloquear solicitações quando palavras-chave ou padrões de escape específicos em protocolos são usados no caminho da solicitação ou na consulta.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   [Restrinja o acesso](https://docs.cloud.google.com/armor/docs/security-policy-overview?hl=pt-br) à autenticação e aos endpoints administrativos a endereços IP ou países autorizados.
*   Ative o `evaluatePreconfiguredWaf` para interceptar e bloquear solicitações projetadas para explorar vulnerabilidades de estado da sessão e sequestro de sessão.
*   Use a API [`securityPolicies.patchRule`](https://docs.cloud.google.com/compute/docs/reference/rest/v1/securityPolicies/patchRule?hl=pt-br) para bloquear solicitações recebidas que contenham um parâmetro comprometido na string de consulta ou nos cabeçalhos na borda da rede.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Restrinja os endpoints que aceitam objetos serializados de alto risco de fontes não confiáveis a um conjunto de endereços IP confiáveis com uma regra de negação semelhante à seguinte:
    
    request.path.contains('/endpoint') && !inIpRange(origin.ip, '192.0.2.1/32')
    
*   Implante [regras personalizadas](https://docs.cloud.google.com/armor/docs/rules-language-reference?hl=pt-br) para inspecionar palavras-chave do corpo da solicitação em busca de padrões de execução específicos do idioma e assinaturas de desserialização não seguras.
    

Confira as práticas recomendadas a seguir para A10: tratamento inadequado de condições excepcionais:

*   Ative a [proteção adaptativa do Google Cloud Armor](https://docs.cloud.google.com/armor/docs/adaptive-protection-overview?hl=pt-br) nas suas políticas de segurança para definir padrões de tráfego normais, configurar alertas sobre anomalias da camada 7 e gerar regras WAF segmentadas com assinaturas de ataque.
*   Configure [regras de limitação de taxa do Cloud Armor](https://docs.cloud.google.com/armor/docs/rate-limiting-overview?hl=pt-br) em endpoints críticos (por exemplo, `/login`, `/checkout` ou APIs de pesquisa). As regras de limitação de taxa restringem as solicitações com base no IP por cliente ou no cabeçalho HTTP (por exemplo, limitando os clientes a 100 solicitações por minuto) e retornam um código de status HTTP `429 Too Many Requests`.
*   [Defina a regra padrão de menor prioridade](https://docs.cloud.google.com/armor/docs/configure-security-policies?hl=pt-br#update-policies) na política de segurança do Cloud Armor como `Deny` (código de status: `403` ou `404`).

### Cloud Build e Cloud Deploy

O [Cloud Build](https://docs.cloud.google.com/build/docs/overview?hl=pt-br) e o [Cloud Deploy](https://docs.cloud.google.com/deploy/docs/overview?hl=pt-br) oferecem um pipeline integrado e seguro de integração contínua e entrega contínua (CI/CD) noGoogle Cloud. O Cloud Build cria artefatos com [provenance](https://docs.cloud.google.com/build/docs/securing-builds/generate-validate-build-provenance?hl=pt-br) SLSA verificável e declarações criptográficas. O Cloud Deploy gerencia rollouts progressivos, aprovações de destino e verificação automatizada no GKE e no Cloud Run.

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A08: falhas de integridade de dados ou software

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Defina `requestedVerifyOption: VERIFIED` no arquivo `cloudbuild.yaml` para exigir [origem verificável](https://docs.cloud.google.com/build/docs/securing-builds/generate-validate-build-provenance?hl=pt-br).
*   Implante [pools privados do Cloud Build](https://docs.cloud.google.com/build/docs/private-pools/private-pools-overview?hl=pt-br) que estejam em peering com sua rede VPC particular para builds empresariais sensíveis.
*   Configure [gatilhos de build](https://docs.cloud.google.com/build/docs/automating-builds/create-manage-triggers?hl=pt-br) para serem executados em contas de serviço dedicadas e gerenciadas pelo usuário. Conceda a essas contas de serviço apenas as permissões de permissão do IAM mínimas necessárias, como gravador do Artifact Registry (`roles/artifactregistry.writer`) e gravador de registros (`roles/logging.logWriter`).
*   Exigir aprovações manuais em gatilhos do Cloud Build que têm como destino ambientes de teste ou produção.
*   Restrinja as ferramentas de build de CI (como Cloud Build, GitHub Actions ou GitLab) à função [Administrador de versões do Cloud Deploy (`roles/clouddeploy.releaser`)](https://docs.cloud.google.com/deploy/docs/iam-roles-permissions?hl=pt-br#clouddeploy.releaser) para que os pipelines de build possam criar apenas versões.
*   Para exigir aprovações manuais, configure o [manifesto do pipeline de entrega](https://docs.cloud.google.com/deploy/docs/create-pipeline-targets?hl=pt-br) (`delivery-pipeline.yaml`) com `requireApproval: true` nos destinos de preparo e produção.
*   Configure [ambientes de execução](https://docs.cloud.google.com/deploy/docs/execution-environment?hl=pt-br#changing_from_the_default_to_custom_execution_service_account) com contas de serviço específicas do destino. Por exemplo, uma conta de serviço com permissões limitadas ao namespace de preparo e outra conta de serviço separada e auditada para produção.
*   [Implante hooks personalizados](https://docs.cloud.google.com/deploy/docs/hooks?hl=pt-br) para executar declarações de segurança fora da banda durante o ciclo de vida do lançamento. Use hooks de pré-implantação para verificar se os clusters de destino atendem aos níveis de conformidade e hooks de pós-implantação para iniciar verificações de vulnerabilidade automatizadas em endpoints de contêiner ativos.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Integre o Cloud Build ao Cloud KMS e ao Artifact Analysis para criar e assinar atestados criptográficos quando os testes de unidade e de análise estática forem concluídos com êxito.
*   Use resumos criptográficos imutáveis SHA-256 (por exemplo, `golang@sha256:...`) nas etapas do builder em `cloudbuild.yaml`.
*   Armazene as configurações de build em repositórios com controle de versões protegidos por regras de proteção de ramificação, como a exigência de revisões de duas pessoas em solicitações de envio. Restrinja as permissões de modificação de acionadores a administradores autorizados da plataforma usando contas de serviço gerenciado pelo usuário.
*   Promova [manifestos de implantação](https://docs.cloud.google.com/deploy/docs/using-skaffold/managing-manifests?hl=pt-br) idênticos e pré-renderizados e resumos de imagens de contêiner imutáveis em todas as etapas de destino sem permitir que os pipelines de CI alterem os manifestos entre a preparação e a produção.
*   Defina [tarefas de verificação de implantação](https://docs.cloud.google.com/deploy/docs/verify-deployment?hl=pt-br) automatizadas no manifesto `skaffold.yaml`. O Cloud Deploy executa esses contêineres de verificação depois que os pods são implantados para executar verificações de integridade dinâmicas, testes de integração e declarações de contrato de API.
*   Use [estratégias de implantação canário](https://docs.cloud.google.com/deploy/docs/deployment-strategies/canary?hl=pt-br). Se um teste de verificação do Skaffold falhar ou o Monitoring detectar limites de anomalia durante uma fase canário, o Cloud Deploy vai interromper o lançamento e reverter o tráfego para a última versão válida conhecida.
*   Configure clusters do GKE e o Cloud Run para [aplicar políticas de autorização binária](https://docs.cloud.google.com/deploy/docs/securing/binary-authorization?hl=pt-br). Quando o Cloud Deploy aplica os manifestos, o controlador de admissão de destino verifica criptograficamente os resumos das imagens de contêiner e rejeita artefatos não confiáveis.

### Cloud Identity e chaves de segurança Titan

O [Cloud Identity](https://cloud.google.com/identity?hl=pt-br) oferece gerenciamento centralizado de identidade, ciclo de vida de credenciais e acesso no Google Cloude no Google Workspace. As [chaves de segurança Titan](https://cloud.google.com/security/products/titan-security-key?hl=pt-br) são dispositivos de segurança resistentes a phishing e baseados em hardware que usam criptografia de chave pública com base nos padrões FIDO2 ou WebAuthn.

Aplicável a A07: falhas de autenticação.

Confira as práticas recomendadas a seguir:

*   Para ajudar a proteger contra ataques de phishing do tipo "pessoa no meio" (PITM, na sigla em inglês), [configure a verificação em duas etapas (2SV)](https://docs.cloud.google.com/docs/authentication/mfa-requirement?hl=pt-br) e defina o método permitido como **Somente chaves de segurança** (FIDO2, WebAuthn ou chaves de segurança Titan).
*   [Configure o Logon único (SSO) baseado em SAML 2.0 ou OIDC](https://docs.cloud.google.com/architecture/identity/single-sign-on?hl=pt-br) com seu provedor de identidade corporativa e o provisionamento automatizado.
*   Defina a [política de duração da sessão](https://knowledge.workspace.google.com/admin/security/set-session-length-for-google-cloud-services?hl=pt-br) com um limite máximo baixo para forçar os usuários a fazer a autenticação novamente periodicamente.Google Cloud
*   Registre as chaves de segurança Titan como [chaves de acesso](https://www.google.com/account/about/passkeys/?hl=pt-br) para ativar a autenticação sem senha, reduzindo significativamente os riscos de força bruta e comprometimento de credenciais.
*   Exija a autenticação de dois fatores com as chaves de segurança Titan para suas identidades privilegiadas (como proprietários de projetos, administradores de faturamento e equipes de SecOps) aplicando políticas de chaves de segurança no Cloud Identity. Inscreva usuários de alto risco no [Programa Proteção Avançada](https://knowledge.workspace.google.com/admin/security/protect-users-with-the-advanced-protection-program?hl=pt-br).

### Cloud KMS

O [Cloud KMS](https://docs.cloud.google.com/kms/docs/key-management-service?hl=pt-br) gerencia chaves criptográficas simétricas e assimétricas para serviços Google Cloud compatíveis e nos seus próprios aplicativos. Você pode [gerar, usar, alternar e destruir](https://docs.cloud.google.com/kms/docs/key-rotation?hl=pt-br) chaves criptográficas para criptografia simétrica, assinatura assimétrica, criptografia assimétrica e assinatura MAC.

Aplicável a A04: falhas criptográficas.

Confira as práticas recomendadas a seguir:

*   Use o [Autokey do Cloud KMS](https://docs.cloud.google.com/kms/docs/autokey-overview?hl=pt-br) para automatizar o provisionamento e a atribuição. Com ele, não é preciso provisionar keyrings, chaves e contas de serviço com antecedência. Em vez disso, as chaves e os keyrings são gerados sob demanda como parte da criação de recursos.
*   Use chaves do Cloud KMS para criptografar payloads sensíveis antes que eles sejam enviados para buckets de armazenamento ou bancos de dados. Use a API Cloud KMS ou as bibliotecas de cliente para usar as chaves do Cloud KMS na [criptografia do lado do cliente](https://docs.cloud.google.com/kms/docs/client-side-encryption?hl=pt-br).
*   [Verifique a integridade de dados de ponta a ponta](https://docs.cloud.google.com/kms/docs/data-integrity-guidelines?hl=pt-br) validando os checksums durante o trânsito.
*   Para conformidade estrita e cargas de trabalho regulatórias, armazene e execute suas operações criptográficas usando o [Cloud HSM](https://docs.cloud.google.com/kms/docs/hsm?hl=pt-br). O Cloud HSM armazena suas chaves em módulos de segurança de hardware validados pelo FIPS 140-3 nível 3.
*   [Configure programações automáticas de rotação de chaves](https://docs.cloud.google.com/kms/docs/key-rotation?hl=pt-br) em um período definido (por exemplo, a cada 90 dias).

### Cloud Load Balancing

O [Cloud Load Balancing](https://docs.cloud.google.com/load-balancing/docs/load-balancing-overview?hl=pt-br) é um serviço gerenciado, totalmente distribuído e definido por software que distribui o tráfego do usuário em várias instâncias e regiões de back-end.

Válido para:

*   A04: falhas criptográficas
*   A10: tratamento inadequado de condições excepcionais

Confira as práticas recomendadas para A04: falhas criptográficas:

*   [Configure e atribua políticas de SSL personalizadas](https://docs.cloud.google.com/load-balancing/docs/use-ssl-policies?hl=pt-br) ao front-end do balanceador de carga para restringir as negociações ao TLS 1.3 ou a perfis seguros do TLS 1.2 e desativar pacotes de criptografia fracos.

Confira as práticas recomendadas a seguir para A10: tratamento inadequado de condições excepcionais:

*   Configure o balanceador de carga de aplicativo externo com [páginas de resposta de erro personalizadas](https://docs.cloud.google.com/load-balancing/docs/https/custom-error-response?hl=pt-br) para interceptar códigos de falha de back-end e disponibilizar respostas de erro HTML ou JSON padronizadas.
*   Implante [serviços de back-end multirregionais com failover entre regiões](https://docs.cloud.google.com/load-balancing/docs/https/applb-failover-overview?hl=pt-br) para que o tráfego possa ser redirecionado para regiões secundárias se ocorrerem interrupções ou falhas não tratadas do sistema.

### Google Cloud Observability (Logging, Monitoring e Error Reporting)

O [Google Cloud Observability](https://docs.cloud.google.com/stackdriver/docs?hl=pt-br) oferece gerenciamento de registros de pilha completa com o [Logging](https://docs.cloud.google.com/logging/docs?hl=pt-br), métricas e alertas com o [Monitoring](https://docs.cloud.google.com/monitoring/docs?hl=pt-br) e rastreamento de falhas de aplicativos em tempo real com o [Error Reporting](https://docs.cloud.google.com/error-reporting/docs?hl=pt-br).

Válido para:

*   A09: falhas de geração de registros e alertas de segurança
*   A10: tratamento inadequado de condições excepcionais

Confira as práticas recomendadas a seguir para A09: falhas de geração de registros e alertas de segurança:

*   Ative os [registros de acesso aos dados](https://docs.cloud.google.com/logging/docs/audit?hl=pt-br#data-access) para repositórios de dados de alto valor (como Cloud Storage, BigQuery e Spanner) que armazenam dados sensíveis. Com os registros de acesso a dados, é possível auditar cada evento de leitura, gravação e consulta de dados sensíveis.
*   Aplique o [bloqueio de bucket](https://docs.cloud.google.com/storage/docs/bucket-lock?hl=pt-br) e as políticas de retenção para o bucket de registros personalizado e evite que invasores ou administradores não autorizados excluam registros para encobrir rastros.
*   Use [coletores agregados](https://docs.cloud.google.com/logging/docs/export/aggregated_sinks_overview?hl=pt-br) para reunir e encaminhar entradas de registro para um único repositório central para suas equipes de SecOps. Configure o interceptação de gravadores agregados para evitar o armazenamento de registros de alto volume, como os de acesso a dados, em mais de um lugar.
*   Configure [políticas de alerta com base em registros](https://docs.cloud.google.com/logging/docs/alerting/log-based-alerts?hl=pt-br) para indicadores críticos de comprometimento, como erros de permissão negada do IAM, criações inesperadas de chaves de API ou mudanças repentinas na configuração do firewall.
*   Implante [políticas de alertas com base em registros](https://docs.cloud.google.com/logging/docs/alerting/log-based-alerts?hl=pt-br) na Análise de registros ou no Monitoring. Especifique filtros exatos que segmentam eventos de alta gravidade, como modificações não autorizadas na política do IAM ou revogações de chaves do KMS, para que uma notificação de incidente seja gerada quando uma entrada de registro correspondente for ingerida.
*   Crie [métricas de contador com base em registros](https://docs.cloud.google.com/logging/docs/logs-based-metrics/counter-metrics?hl=pt-br) no Logging para converter entradas de registro correspondentes em dados de série temporal. Em seguida, crie uma política de alertas baseada em métricas no Monitoring que inicie um incidente quando a taxa exceder um limite específico (por exemplo, mais de 50 tentativas de login com falha em cinco minutos).
*   Configure políticas de alertas com base em registros que monitoram chamadas administrativas para a API Cloud Logging e alertam sobre modificações inesperadas de gravadores de exportação de registros ou exclusões de buckets.
*   Configure canais de notificação com modelos de documentação claros. Inclua links diretos para a [Análise de registros](https://docs.cloud.google.com/logging/docs/view/logs-explorer-interface?hl=pt-br), procedimentos operacionais padrão (SOPs) para o engenheiro de plantão e etapas de correção explícitas para ajudar a conter rapidamente o incidente.

Confira as práticas recomendadas a seguir para A10: tratamento inadequado de condições excepcionais:

*   Integre os [SDKs do Error Reporting](https://docs.cloud.google.com/error-reporting/docs/setup?hl=pt-br) diretamente ao código do aplicativo ou configure o Logging para analisar formatos de exceção JSON estruturados.
*   Configure [canais de notificação do Error Reporting](https://docs.cloud.google.com/error-reporting/docs/notifications?hl=pt-br) ou políticas de alerta do Monitoring para notificar suas equipes de SecOps quando uma nova classe de exceção aparecer.

### Cloud NGFW

O [Cloud NGFW](https://docs.cloud.google.com/firewall/docs/about-firewalls?hl=pt-br) é um serviço de firewall gerenciado que permite inspeção com estado e controle de aplicativos da camada 7 para tráfego norte-sul e leste-oeste.

Válido para:

*   A01: controle de acesso corrompido
*   A05: injeção

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Implemente a [microssegmentação de rede usando políticas globais de firewall de rede](https://docs.cloud.google.com/architecture/ccn-distributed-apps-design/ccn-ncc-vpn-ra?hl=pt-br#configure-ngfw-policies) e tags de recursos gerenciadas pelo IAM para isolar as camadas de aplicativos de back-end e restringir a comunicação leste-oeste entre sub-redes.
*   Use [listas de inteligência contra ameaças](https://docs.cloud.google.com/firewall/docs/ngfw_tiers?hl=pt-br#ngfw-tiers-standard) mantidas pelo Google em regras de firewall para bloquear conexões de entrada originadas de agentes maliciosos conhecidos, servidores C2 e botnets comprometidas.

Confira as práticas recomendadas a seguir para A05: injeção:

*   Configure o [serviço de detecção e prevenção de invasões](https://docs.cloud.google.com/firewall/docs/configure-intrusion-prevention?hl=pt-br) com um grupo de perfis de segurança que nega ameaças correspondentes a injeção de SQL, injeção de comandos do SO e assinaturas de exploração de execução remota de código.
*   Configure a [inspeção de TLS do Cloud NGFW](https://docs.cloud.google.com/firewall/docs/about-tls-inspection?hl=pt-br) para descriptografar o tráfego HTTPS de entrada e saída, aplique verificações de assinatura de injeção de IPS ao payload de texto simples e reencripte a sessão antes da entrega ao back-end.
*   Aplique [regras de firewall de saída baseadas em FQDN](https://docs.cloud.google.com/firewall/docs/quickstarts/configure-nwfwpolicy-fqdn-egress?hl=pt-br) em bancos de dados de back-end e sub-redes do Compute. Restrinja as conexões de saída a domínios externos aprovados e predefinidos para evitar que aplicativos vulneráveis criem shells inversos não autorizados.
*   Ative o [registro de regras de firewall](https://docs.cloud.google.com/firewall/docs/firewall-policy-rules-logging-overview?hl=pt-br) nos [perfis de prevenção contra ameaças](https://docs.cloud.google.com/firewall/docs/configure-security-profiles?hl=pt-br) e direcione esses registros para o Google SecOps a fim de correlacionar assinaturas de injeção de rede bloqueadas com a telemetria no nível do host, identificando cargas de trabalho segmentadas para aplicação de patch prioritária.

### Cloud Workstations

O [Cloud Workstations](https://docs.cloud.google.com/workstations/docs/overview?hl=pt-br) oferece ambientes de desenvolvimento gerenciados no Google Cloud com segurança integrada e personalizações.

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A04: falhas criptográficas

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Crie [imagens de contêiner de base personalizadas](https://docs.cloud.google.com/workstations/docs/customize-container-images?hl=pt-br) armazenadas no Artifact Registry que pré-instalam ferramentas de segurança, extensões de desenvolvedor confiáveis e tempos de execução de linguagem aprovados.
*   Implante seus [clusters de estação de trabalho](https://docs.cloud.google.com/workstations/docs/create-cluster?hl=pt-br) com entrada e saída de IP particular e dentro de um perímetro do VPC Service Controls.
*   Configure o Cloud Workstations para [rotear o tráfego da sessão pelo IAP](https://docs.cloud.google.com/workstations/docs/enable-iap?hl=pt-br). Exija que os desenvolvedores se autentiquem usando credenciais corporativas com a autenticação multifator (MFA) ativada e aplique papéis de menor privilégio (por exemplo, [Usuário das estações de trabalho em nuvem (`roles/workstations.user`)](https://docs.cloud.google.com/workstations/docs/access-control?hl=pt-br#workstations-user)).
*   Configure [configurações de estação de trabalho](https://docs.cloud.google.com/workstations/docs/overview?hl=pt-br#configuration_updates) com limites de tempo limite baixos (por exemplo, parada automática após duas horas de inatividade). Quando uma estação de trabalho é reiniciada, o Cloud Workstations extrai a imagem de contêiner mais recente com patch de segurança para que os desenvolvedores possam trabalhar em um ambiente limpo.

Confira as práticas recomendadas para A04: falhas criptográficas:

*   Configure as configurações da estação de trabalho para [criptografar discos permanentes anexados usando a CMEK](https://docs.cloud.google.com/workstations/docs/encrypt-workstation-resources-using-cmek?hl=pt-br).

### CodeMender

O [CodeMender](https://deepmind.google/blog/introducing-codemender-an-ai-agent-for-code-security/?hl=pt-br) é um agente de engenharia de IA autônomo e especializado. O CodeMender pode corrigir vulnerabilidades recém-descobertas e reescrever o código legado para resolver vulnerabilidades atuais. É possível [instalar e configurar o CodeMender](https://docs.cloud.google.com/gemini-enterprise-agent-platform/codemender/set-up-environment?hl=pt-br) na Gemini Enterprise Agent Platform.

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A05: injeção
*   A06: design não seguro

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Integre a [CLI do CodeMender](https://docs.cloud.google.com/gemini-enterprise-agent-platform/codemender/set-up-environment?hl=pt-br) em espaços de trabalho de desenvolvedores locais e pipelines de CI/CD para verificar módulos específicos, verificar a capacidade de exploração e detectar falhas de segurança antes do commit do código.
*   Importe [relatórios de análise de composição de software (SCA) e vulnerabilidade de dependência](https://docs.cloud.google.com/gemini-enterprise-agent-platform/codemender/import-findings?hl=pt-br) para o CodeMender e execute verificações de exploit de prova de conceito, filtrando falsos positivos antes da revisão do desenvolvedor.

Confira as práticas recomendadas a seguir para A05: injeção:

*   Execute a [geração automática de patches](https://docs.cloud.google.com/gemini-enterprise-agent-platform/codemender/fix-and-patch?hl=pt-br) em sandboxes locais isoladas para reescrever a lógica vulnerável (como a limpeza de entradas). Verifique se os testes de unidade foram aprovados e se as PoCs não são mais exploráveis antes de criar solicitações de pull.

Confira as práticas recomendadas para A06: design não seguro:

*   Refatore a lógica de código arquitetônico legado ou não seguro usando o mecanismo de patch iterativo do CodeMender, fornecendo restrições de programação explícitas para aplicar padrões de design seguros em todos os módulos do aplicativo.
*   Mantenha a [revisão humana no loop](https://docs.cloud.google.com/architecture/framework/perspectives/ai-ml/security?hl=pt-br) para solicitações de envio e diffs gerados pelo CodeMender para verificar se as mudanças propostas estão alinhadas às suas diretrizes de programação segura.

### Computação confidencial

A [Computação Confidencial](https://cloud.google.com/confidential-computing?hl=pt-br) ajuda a proteger os dados em uso, mantendo-os criptografados na memória enquanto são processados. Usando ambientes de execução confiável (TEEs) baseados em hardware, a computação confidencial ajuda a garantir que seus dados sensíveis e chaves criptográficas não possam ser acessados pelo hipervisor, pelo sistema operacional host ou pelos administradores de infraestrutura.

Aplicável a A04: falhas criptográficas.

Confira as práticas recomendadas a seguir:

*   Use [VMs confidenciais](https://docs.cloud.google.com/confidential-computing/confidential-vm/docs/confidential-vm-overview?hl=pt-br) ou [nós confidenciais do Google Kubernetes Engine](https://docs.cloud.google.com/kubernetes-engine/docs/how-to/confidential-gke-nodes?hl=pt-br) para cargas de trabalho altamente sensíveis (como PII, registros financeiros ou pesos de modelos de IA proprietários).
*   Quando várias organizações precisam reunir dados sensíveis para análise ou treinamento de IA (sem expor os dados brutos umas às outras), use o [Confidential Space](https://docs.cloud.google.com/confidential-computing/confidential-space/docs/confidential-space-overview?hl=pt-br) para aplicar o atestado criptográfico e o isolamento de dados.

### Firebase (Firebase Authentication, Firebase App Check e regras de segurança do Firebase)

O [Firebase](https://firebase.google.com/docs?hl=pt-br) oferece controles de segurança focados em desenvolvedores para identidade, atestação do cliente e acesso ao banco de dados. O [Firebase Authentication](https://firebase.google.com/docs/auth?hl=pt-br) lida com a identidade do usuário e o gerenciamento de sessões, o [App Check](https://firebase.google.com/docs/app-check?hl=pt-br) valida a integridade do app cliente, e as [regras de segurança do Firebase](https://firebase.google.com/docs/rules?hl=pt-br) aplicam o controle de acesso baseado em atributos e a validação de esquema para o Firestore e o Cloud Storage.

Válido para:

*   A01: controle de acesso corrompido
*   A05: injeção
*   A07: falhas de autenticação
*   A08: falhas de integridade de dados ou software

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   [Escopo de leituras e gravações](https://firebase.google.com/docs/rules/insecure-rules?hl=pt-br#open_access) no ID do usuário autenticado nas regras de segurança do Firebase. Nunca use regras padrão permissivas, como `allow read, write: if true;`.
*   Para funções administrativas, use os SDKs Admin do Firebase para definir [declarações personalizadas](https://firebase.google.com/docs/auth/admin/custom-claims?hl=pt-br) nos tokens de ID dos usuários e valide essas declarações nas regras de segurança em vez de permitir gravações de perfil do lado do cliente.
*   [Aplique o App Check](https://firebase.google.com/docs/app-check/enable-enforcement?hl=pt-br) nas regras de segurança do Firebase para bloquear o acesso não autenticado ou falsificado do cliente na camada de banco de dados.

Confira as práticas recomendadas a seguir para A05: injeção:

*   Aplique a [validação estrutural de payload](https://firebase.google.com/docs/firestore/security/rules-conditions?hl=pt-br) nas regras de segurança verificando os tipos de campos de documentos recebidos, os comprimentos de strings e os tamanhos de objetos para rejeitar payloads de gravação malformados ou maliciosos antes da ingestão do banco de dados.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Faça upgrade para o Firebase Authentication com o Identity Platform e ative proteções empresariais, como [MFA com TOTP](https://firebase.google.com/docs/auth/web/totp-mfa?hl=pt-br) e [funções de bloqueio](https://firebase.google.com/docs/auth/extend-with-blocking-functions?hl=pt-br).
*   Verifique os [tokens de ID do Firebase](https://firebase.google.com/docs/auth/admin/verify-id-tokens?hl=pt-br) no back-end usando o SDK Admin do Firebase antes de conceder acesso a dados sensíveis do aplicativo.
*   Use o [provedor de depuração](https://firebase.google.com/docs/app-check/web/debug-provider?hl=pt-br) para gerar tokens de depuração temporários e com escopo para seus desenvolvedores e pipelines de CI/CD em ambientes de staging.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Impor provedores de atestação com suporte de hardware para verificar a integridade do cliente. Configure o App Check para usar a [Integridade do Play no Android](https://firebase.google.com/docs/app-check/android/play-integrity-provider?hl=pt-br) e o [App Attest da Apple](https://firebase.google.com/docs/app-check/ios/app-attest-provider?hl=pt-br).
*   Implante o [middleware de verificação de token do App Check](https://firebase.google.com/docs/app-check/custom-resource-backend?hl=pt-br) nos back-ends da API do Cloud Run e do Kubernetes Engine para rejeitar solicitações originadas de chaves de API raspadas, scripts automatizados ou ambientes emulados.

### Fraud Defense

O [Fraud Defense](https://cloud.google.com/security/products/fraud-defense?hl=pt-br) é uma plataforma unificada de defesa contra fraudes e abusos, incluindo proteção de bots, contas e transações na Web. O [reCAPTCHA](https://cloud.google.com/security/products/recaptcha?hl=pt-br), uma oferta que faz parte do Fraud Defense, filtra bots e outras formas de automação e tráfego em massa, pontuando o nível de risco das tentativas de acesso.

Aplicável a A07: falhas de autenticação.

Confira as práticas recomendadas a seguir:

*   [Integre o reCAPTCHA ao seu WAF](https://docs.cloud.google.com/recaptcha/docs/waf-overview?hl=pt-br) atual, como o Google Cloud Armor, para emitir desafios automatizados ou bloquear o tráfego de bots de alto risco antes que as solicitações cheguem aos endpoints de autenticação.
*   [Proteja as contas](https://docs.cloud.google.com/recaptcha/docs/account-takeovers?hl=pt-br) nos endpoints de login, redefinição de senha e renovação de sessão. Obtenha pontuações de risco de sequestro de conta (ATO) com base nas velocidades de login do usuário e nas impressões digitais do dispositivo.
*   [Proteja-se contra fraudes de cobrança por SMS](https://docs.cloud.google.com/recaptcha/docs/sms-fraud-detection?hl=pt-br) em formulários de registro e autenticação de dois fatores avaliando perfis de risco de números de telefone antes de enviar mensagens SMS de saída.
*   Para reduzir falsos positivos e treinar modelos de avaliação de risco específicos do site, [anote e envie feedback de transações](https://docs.cloud.google.com/recaptcha/docs/samples/recaptcha-enterprise-annotate-assessment?hl=pt-br) regularmente.
*   [Verificar senhas](https://docs.cloud.google.com/recaptcha/docs/check-passwords?hl=pt-br) durante os fluxos de login do usuário e criação de conta para detectar se as credenciais enviadas aparecem em bancos de dados de violação de dados de terceiros na Web.

### Google SecOps

O [Google Security Operations](https://cloud.google.com/security/products/security-operations?hl=pt-br) é uma plataforma de operações de segurança que combina análise de telemetria de segurança (SIEM), orquestração, automação e resposta de segurança (SOAR) e inteligência contra ameaças da Mandiant em uma única plataforma.

Válido para:

*   A02: configuração incorreta de segurança
*   A09: falhas de geração de registros e alertas de segurança

Confira as práticas recomendadas a seguir para A02: configuração incorreta de segurança:

*   Ingira descobertas do Security Command Center no Google SecOps para combinar descobertas de configuração incorreta estática (por exemplo, `PUBLIC_BUCKET_ACL` ou `CMEK_DISABLED`) com telemetria de rede e firewall em tempo real.
*   Crie [playbooks de resposta SOAR automatizados](https://docs.cloud.google.com/chronicle/docs/soar/overview-and-introduction/soar-overview?hl=pt-br) para executar ações de contenção.
*   Use o [Gemini](https://docs.cloud.google.com/chronicle/docs/secops/gemini-secops?hl=pt-br) para acelerar a triagem de configurações incorretas e receber resumos sintetizados de recursos mal configurados, papéis do IAM anexados e orientações de correção detalhadas.

Confira as práticas recomendadas a seguir para A09: falhas de geração de registros e alertas de segurança:

*   Normalizar a telemetria de registros para o [modelo de dados unificado (UDM)](https://docs.cloud.google.com/chronicle/docs/event-processing/udm-overview?hl=pt-br) para permitir uma pesquisa e correlação multicloud rápidas e padronizadas sem a sobrecarga da análise de registros brutos.
*   Escreva [regras de detecção do YARA-L 2.0](https://docs.cloud.google.com/chronicle/docs/yara-l/yara-l-2-0-examples?hl=pt-br) para monitorar mudanças de configuração de alto risco, como desativação do login do SO, exclusão de gravadores do Logging ou modificações nos perímetros do VPC Service Controls.
*   Use as [detecções selecionadas da Applied Threat Intelligence](https://docs.cloud.google.com/chronicle/docs/detection/ati-curated-detections?hl=pt-br) para avaliar seus dados de eventos com base nos dados da Mandiant Threat Intelligence.
*   Use o Gemini no Google SecOps para gerar regras de detecção YARA-L com base em descrições de linguagem natural e resumir cronogramas de incidentes complexos e de várias etapas em resumos executivos.

### Identity-Aware Proxy

O [IAP](https://docs.cloud.google.com/iap/docs/concepts-overview?hl=pt-br) cria uma camada de autorização central para aplicativos acessados por HTTPS e conexões TCP administrativas. O IAP verifica a identidade e o contexto do usuário antes de conceder acesso aos recursos do Cloud Run, App Engine, Compute Engine, GKE e locais.

Válido para:

*   A01: controle de acesso corrompido
*   A07: falhas de autenticação

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Aplique [controles de acesso granulares](https://docs.cloud.google.com/iap/docs/cloud-iap-context-aware-access-howto?hl=pt-br) a aplicativos da Web, VMs, Google Cloud APIs e aplicativos do Google Workspace com base na identidade, na associação a grupos e no contexto da solicitação de um usuário.
*   [Integre com o Gateway de Agente](https://docs.cloud.google.com/iap/docs/agent-overview?hl=pt-br) para aplicar controles de acesso às identidades dos agentes.
*   Use o [encaminhamento de TCP do IAP](https://docs.cloud.google.com/iap/docs/using-tcp-forwarding?hl=pt-br) para estabelecer túneis HTTPS criptografados nas instâncias de back-end e remover endpoints SSH (porta `22`) e RDP (porta `3389`) voltados para a Internet.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Autentique usuários que acessam interfaces administrativas e aplicativos da Web pelo IAP usando identidades provisionadas no [IAM](https://docs.cloud.google.com/iam/docs/overview?hl=pt-br) ou no [Cloud Identity](https://cloud.google.com/identity?hl=pt-br).
*   [Verifique a declaração do JWT assinado](https://docs.cloud.google.com/iap/docs/signed-headers-howto?hl=pt-br) no cabeçalho `x-goog-iap-jwt-assertion` na camada de aplicativo. Valide a assinatura com as chaves públicas do Google e verifique se a declaração de público-alvo (`aud`) corresponde ao ID do serviço de back-end.
*   Para evitar que invasores burlem a autenticação do IAP, configure as [definições de entrada do Cloud Run](https://docs.cloud.google.com/run/docs/securing/ingress?hl=pt-br) para permitir apenas o tráfego interno e do Cloud Load Balancing, bloqueando o acesso público direto aos URLs do contêiner de back-end. Para VMs ou nós do GKE, configure as [regras de firewall da VPC](https://docs.cloud.google.com/firewall/docs/firewalls?hl=pt-br) para aceitar apenas o tráfego de entrada originado dos intervalos de IP do balanceador de carga.

### Identity and Access Management

Com o [Identity and Access Management (IAM)](https://docs.cloud.google.com/iam/docs/overview?hl=pt-br), é possível gerenciar o acesso refinado a serviços e recursos em Google Cloud. A IAM inclui recursos como:

*   O [Privileged Access Manager](https://docs.cloud.google.com/iam/docs/pam-overview?hl=pt-br), que gerencia a elevação de privilégios temporária e sob demanda para recursos sensíveis do Google Cloud
*   A [federação de identidade da carga de trabalho](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=pt-br) permite que as cargas de trabalho acessem os recursos do Google Cloud usando uma identidade federada.
*   A [federação de identidade de colaboradores](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=pt-br), que permite aos usuários acessar recursos do Google Cloud usando uma identidade federada.

Válido para:

*   A01: controle de acesso corrompido
*   A07: falhas de autenticação

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Use papéis predefinidos ou personalizados (não [papéis básicos](https://docs.cloud.google.com/iam/docs/roles-overview?hl=pt-br#basic)) para restringir permissões a necessidades específicas de recursos ou usuários.
*   Restrinja as permissões para conceder os papéis de usuário da conta de serviço (`roles/iam.serviceAccountUser`) e criador de token da conta de serviço (`roles/iam.serviceAccountTokenCreator`).
*   Use o [Recomendador do IAM](https://docs.cloud.google.com/policy-intelligence/docs/role-recommendations-overview?hl=pt-br) para analisar os registros de uso ativos da sua organização e remover contas com privilégios excessivos.
*   Escreva [condições do IAM](https://docs.cloud.google.com/iam/docs/conditions-overview?hl=pt-br) nas vinculações de função para adicionar autorização baseada no contexto e restringir o acesso por data, hora do dia ou endereço IP de origem.
*   Implante [políticas de limite de acesso de principal (PAB)](https://docs.cloud.google.com/iam/docs/principal-access-boundary-policies?hl=pt-br) para definir as organizações, pastas ou projetos que um conjunto de principais pode acessar. Se um invasor roubar uma sessão ativa ou se uma conta de serviço receber papéis amplos do IAM por engano, o PAB vai bloquear o acesso se o recurso especificado estiver fora do limite designado da identidade.
*   Anexe [políticas de negação do IAM](https://docs.cloud.google.com/iam/docs/deny-overview?hl=pt-br) no nível da organização ou da pasta para bloquear permissões de alto risco, como `iam.serviceAccountKeys.create` ou `resourcemanager.projects.delete`.
*   Ao configurar regras de negação do IAM, declare um grupo de segurança de breakglass dedicado na lista `exceptionPrincipals`. Use [tags de recursos nas condições de negação](https://docs.cloud.google.com/iam/docs/conditions-attribute-reference?hl=pt-br#resource-tags) (como `resource.matchTag('env', 'prod')`) para que ações destrutivas sejam bloqueadas em recursos de produção, permitindo flexibilidade operacional aos desenvolvedores em projetos de sandbox de desenvolvimento.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação aplicáveis ao Privileged Access Manager:

*   Converta papéis administrativos críticos (como Proprietário (`roles/owner`), Administrador da organização (`roles/resourcemanager.organizationAdmin`) e Administrador de segurança (`roles/iam.securityAdmin`)) de vinculações estáticas do IAM em [direitos do Privileged Access Manager](https://docs.cloud.google.com/iam/docs/pam-overview?hl=pt-br). Configure esses direitos para exigir uma justificativa operacional antes da elevação ser concedida.
*   Para ambientes de produção, [configure políticas de direitos do Privileged Access Manager](https://docs.cloud.google.com/iam/docs/pam-create-entitlements?hl=pt-br) com aprovadores obrigatórios, como um grupo central de SecOps ou líderes de equipe.
*   Configure a duração máxima dos direitos do Privileged Access Manager para a menor janela operacional realista (por exemplo, duas horas para manutenção padrão, 30 minutos para ações de emergência). Quando o timer expira, o Google Cloud remove a vinculação temporária da função do IAM.
*   Gerencie a infraestrutura do Terraform usando recursos não autoritativos do IAM (por exemplo, `google_project_iam_member` ou `google_folder_iam_member` em vez de `google_project_iam_policy` ou `google_project_iam_binding`). Essa prática evita substituir seus pipelines do Terraform ou dessincronizar vinculações temporárias de papéis do Privileged Access Manager enquanto um administrador está corrigindo um incidente.
*   [Ative os Registros de auditoria do Cloud no Privileged Access Manager](https://docs.cloud.google.com/iam/docs/audit-logging/audit-logging-pam?hl=pt-br) para registrar ações de direito e eventos de expiração. Ingira esses registros no Google SecOps para alertar sobre padrões de elevação suspeitos, como várias solicitações de elevação fora do horário comercial ou solicitações repetidas de geolocalizações inesperadas.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação que se aplicam à federação de identidade de colaboradores:

*   [Implante pools de identidade da força de trabalho](https://docs.cloud.google.com/iam/docs/manage-workforce-identity-pools-providers?hl=pt-br) usando SAML 2.0 ou OpenID Connect (OIDC) para federar provedores de identidade externos comGoogle Cloud.
*   Configure a [duração da sessão](https://docs.cloud.google.com/iam/docs/configuring-workforce-identity-federation?hl=pt-br#create-workforce-pool) no pool de identidades de colaboradores para limitar o ciclo de vida dos tokens de usuário federados.
*   Aplique [condições de atributo](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=pt-br#attribute-conditions) aos provedores de identidade da força de trabalho para reduzir a falsificação de tokens de IdP multitenant ou a representação entre organizações.
*   Mapeie associações de grupos externos para atribuir papéis do IAM a conjuntos principais de grupos federados (por exemplo, `principalSet://iam.googleapis.com/.../attribute.group/security-engineers`).

Confira as práticas recomendadas a seguir para A07: falhas de autenticação que se aplicam à federação de identidade da carga de trabalho:

*   [Crie pools de identidades e provedores de carga de trabalho](https://docs.cloud.google.com/iam/docs/manage-workload-identity-pools-providers?hl=pt-br) para cargas de trabalho externas. Use tokens OIDC de curta duração, trocando-os dinamicamente usando o Security Token Service por tokens de acesso temporários que expiram em minutos.
*   Aplique [condições de atributo](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=pt-br#conditions) aos provedores de identidade de carga de trabalho para que plataformas externas multitenant não possam se autenticar no seu pool usando repositórios ou contas não autorizados.
*   Vincule papéis do IAM diretamente a conjuntos de principais específicos filtrados por atributos mapeados personalizados.
*   Ao configurar o acesso à carga de trabalho, conceda papéis do IAM diretamente ao identificador `principalSet://` federado no recurso de destino.
*   Para aplicar a federação de identidade da carga de trabalho, defina a [restrição `constraints/iam.disableServiceAccountKeyCreation`](https://docs.cloud.google.com/organization-policy/restrict-service-accounts?hl=pt-br) na sua organização.

### Identity Platform

O [Identity Platform](https://cloud.google.com/security/products/identity-platform?hl=pt-br) é a plataforma de gerenciamento de identidade e acesso do cliente (CIAM) para clientes do Google Cloud . O Identity Platform oferece autenticação com suporte a vários protocolos usando SDKs e APIs. O Identity Platform é compatível com MFA, integração com serviços de autenticação de terceiros e rastreamento de atividades auditáveis.

Aplicável a A07: falhas de autenticação.

Confira as práticas recomendadas a seguir:

*   Ative a [MFA](https://docs.cloud.google.com/identity-platform/docs/how-to?hl=pt-br) em toda a sua base de usuários. Priorize métodos resistentes a phishing, como TOTP (apps autenticadores) ou WebAuthn (biometria e chaves de segurança).
*   Implante [funções de bloqueio do Cloud Run](https://docs.cloud.google.com/identity-platform/docs/blocking-functions?hl=pt-br) usando acionadores `beforeCreate` e `beforeSignIn` para executar um código de segurança personalizado antes que um usuário seja salvo ou receba um token. Essa prática permite bloquear domínios de e-mail descartáveis, restringir endereços IP ou exigir verificação de e-mail.
*   Integre com o [reCAPTCHA Enterprise](https://docs.cloud.google.com/identity-platform/docs/recaptcha-enterprise?hl=pt-br) para avaliar solicitações de login, inscrição e redefinição de senha em relação ao tráfego de bots, tentativas de preenchimento de credenciais e abuso automatizado.
*   Configure [políticas de senha](https://docs.cloud.google.com/identity-platform/docs/password-policy?hl=pt-br) para impor tamanhos mínimos de caracteres, exigir complexidades específicas de caracteres (como números e símbolos) e bloquear sequências previsíveis.
*   Se você estiver usando a MFA baseada em smartphone, configure as [regiões de SMS](https://docs.cloud.google.com/identity-platform/docs/admin/sms-regions?hl=pt-br) e ative a [defesa de SMS do reCAPTCHA](https://docs.cloud.google.com/identity-platform/docs/recaptcha-tfp?hl=pt-br) para limitar as mensagens de verificação aos códigos de país em que seus usuários-alvo residem.

### Soluções de consultoria de segurança de IA da Mandiant

As [soluções de consultoria de segurança de IA da Mandiant](https://cloud.google.com/security/solutions/mandiant-ai-consulting?hl=pt-br) podem avaliar as arquiteturas de software, os processos comerciais e as implantações de nuvem propostos no início do ciclo de vida de desenvolvimento. Ao aplicar a inteligência de ameaças de linha de frente ao design do sistema, os consultores da Mandiant ajudam a expor falhas lógicas ocultas, limites de confiança ausentes e riscos arquitetônicos antes que uma única linha de código seja escrita.

Aplica-se a A06: design não seguro.

Confira as práticas recomendadas a seguir:

*   Contrate consultores da Mandiant antes do início do desenvolvimento para concluir workshops de arquitetura e implementar controles de segurança desde o início.
*   Colabore com especialistas em modelagem de ameaças para mapear os diagramas de fluxo de dados do seu aplicativo. Defina onde os dados sensíveis cruzam os limites de confiança para identificar onde controles rígidos de autenticação, criptografia e validação precisam ser aplicados.
*   Use frameworks estruturados de modelagem de ameaças (como [STRIDE](https://en.wikipedia.org/wiki/STRIDE_model)) durante os workshops de arquitetura. Os consultores da Mandiant podem ajudar a priorizar falhas de design descobertas com base na capacidade de exploração e no impacto nos negócios no mundo real.
*   Estabeleça bases seguras de governança de IA para fluxos de trabalho agênticos e implantações de LLM, definindo limites claros de confiança entre agentes de IA, servidores MCP e fontes de dados de back-end corporativas.

### Model Armor

O [Model Armor](https://docs.cloud.google.com/model-armor/overview?hl=pt-br) foi criado para filtrar comandos, respostas e chamadas de ferramentas do MCP. O Model Armor inspeciona payloads de IA generativa para ajudar a detectar e bloquear injeção de comandos, tentativas de jailbreak, URLs maliciosos, conteúdo tóxico e vazamento de dados sensíveis.

Válido para:

*   A05: injeção
*   A10: tratamento inadequado de condições excepcionais

Confira as práticas recomendadas a seguir para A05: injeção:

*   Implante políticas do Model Armor inline na camada do gateway de API usando a [integração do Apigee](https://cloud.google.com/blog/products/identity-security/how-model-armor-can-help-protect-your-ai-apps?hl=pt-br) ou o Gateway de Agente para analisar comandos recebidos e respostas do modelo antes que o tráfego chegue aos mecanismos de inferência ou aos ambientes de execução de ferramentas.
*   [Configure as configurações mínimas](https://docs.cloud.google.com/security-command-center/docs/model-armor?hl=pt-br) no nível da organização ou da pasta para criar proteções de segurança básicas obrigatórias que as equipes de projetos individuais não podem ignorar.
*   Crie [templates personalizados do Model Armor](https://docs.cloud.google.com/model-armor/overview?hl=pt-br) com limites de confiança ajustados (por exemplo, `LOW_AND_ABOVE` ou `MEDIUM_AND_ABOVE`) para detecção de injeção de comando e jailbreak em endpoints públicos.
*   Ative a detecção de URLs maliciosos e a verificação de PDFs e arquivos no seu modelo do Model Armor para verificar URLs incorporados em bancos de dados de inteligência contra ameaças do Google. Descarte comandos que contenham malware ou vetores de phishing antes da execução.
*   Ative a [Proteção de Dados Sensíveis no modelo do Model Armor](https://docs.cloud.google.com/model-armor/sanitize-prompts-responses?hl=pt-br#basic_sdp_configuration) para inspecionar o tráfego de saída do modelo. Configure a desidentificação ou mascaramento automatizado para substituir os dados sensíveis detectados por marcadores de posição antes que a resposta saia do limite.

Confira as práticas recomendadas a seguir para A10: tratamento inadequado de condições excepcionais:

*   Configure o código do aplicativo para interceptar veredictos de `MATCH_FOUND` e retornar uma resposta genérica. Assim, o sistema não executa o comando nem expõe rastreamentos de exceção brutos por padrão.
*   Implemente uma arquitetura de falha fechada (segura) no código do aplicativo para rejeitar comandos de IA generativa recebidos se as chamadas da API Model Armor encontrarem tempos limite de rede, limites de taxa ou erros HTTP 5xx não tratados.

### Política da organização

A [política da organização](https://docs.cloud.google.com/organization-policy/overview?hl=pt-br) oferece controle centralizado e programático sobre os Google Cloud recursos da sua organização.

Válido para:

*   A01: controle de acesso corrompido
*   A02: configuração incorreta de segurança
*   A04: falhas criptográficas

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Aplique [`constraints/storage.publicAccessPrevention`](https://docs.cloud.google.com/storage/docs/public-access-prevention?hl=pt-br) para substituir políticas do IAM ou ACLs no nível do bucket que tentam conceder acesso a `allUsers` ou `allAuthenticatedUsers`.
*   Aplique [`constraints/iam.allowedPolicyMemberDomains`](https://docs.cloud.google.com/organization-policy/restrict-domains?hl=pt-br) para restringir vinculações de política do IAM estritamente aos seus IDs de cliente verificados do Google Workspace ou do Cloud Identity.
*   Imponha [`constraints/iam.disableServiceAccountKeyCreation`](https://docs.cloud.google.com/organization-policy/restrict-service-accounts?hl=pt-br) em pastas de produção para evitar que os usuários façam o download de chaves de contas de serviço, forçando as equipes de engenharia a adotar alternativas de curta duração, como a federação de identidade da carga de trabalho.
*   Aplique [`constraints/iam.automaticIamGrantsForDefaultServiceAccounts`](https://docs.cloud.google.com/organization-policy/restrict-service-accounts?hl=pt-br) para que Google Cloud não conceda automaticamente o papel permissivo de Editor (`roles/editor`) às contas de serviço padrão.
*   Para requisitos não cobertos por restrições predefinidas, implante [restrições personalizadas](https://docs.cloud.google.com/organization-policy/overview?hl=pt-br#custom-constraints) para aplicar configurações de recursos refinadas. Considere restringir a criação de VMs apenas a famílias de máquinas aprovadas, limitar os tamanhos de provisionamento de disco permanente ou exigir configurações específicas de tags de firewall de rede.

Confira as práticas recomendadas a seguir para A02: configuração incorreta de segurança:

*   Use [`constraints/compute.requireShieldedVm`](https://docs.cloud.google.com/compute/shielded-vm/docs/shielded-vm?hl=pt-br) para exigir a VM protegida, ajudando a proteger as VMs contra rootkits de kernel, bootkits e adulteração de firmware.
*   Aplique [`constraints/compute.requireOsLogin`](https://docs.cloud.google.com/compute/docs/oslogin/manage-oslogin-in-an-org?hl=pt-br#set-org-policy) para exigir que as instâncias do Linux usem o login do SO, que vincula o acesso SSH diretamente às identidades do IAM do usuário e à 2SV.
*   Aplique [`constraints/compute.disableSerialPortAccess`](https://docs.cloud.google.com/compute/docs/access/organization-policies?hl=pt-br#managed_constraints_legacy) para bloquear conexões interativas do console serial em todos os projetos.
*   Aplique [`constraints/compute.skipDefaultNetworkCreation`](https://docs.cloud.google.com/compute/docs/access/organization-policies?hl=pt-br#managed_constraints_legacy) para que a rede VPC padrão não seja criada, forçando as equipes a criar VPCs personalizadas com sub-redes dedicadas e políticas de firewall rigorosas.
*   Aplique [`constraints/sql.restrictPublicIp`](https://docs.cloud.google.com/sql/docs/mysql/recommender-disable-public-ip?hl=pt-br) para que as instâncias do Cloud SQL recebam apenas endereços IP internos privados RFC 1918 e use [`constraints/compute.vmExternalIpAccess`](https://docs.cloud.google.com/compute/docs/access/managed-constraints?hl=pt-br#available_managed_constraints) para restringir endereços IPv4 públicos em VMs.
*   Aplique [`constraints/gcp.resourceLocations`](https://docs.cloud.google.com/organization-policy/restrict-locations?hl=pt-br) para restringir a criação de recursos a regiões Google Cloud autorizadas.

Confira as práticas recomendadas para A04: falhas criptográficas:

*   Para exigir a CMEK, aplique [`constraints/gcp.restrictNonCmekServices`](https://docs.cloud.google.com/kms/docs/cmek-org-policy?hl=pt-br) na organização ou na pasta de nível superior, defina o tipo de política como `Deny` e liste os serviços Google Cloud compatíveis. Antes de aplicar a restrição, verifique se o agente de serviço de cada serviço de destino existe e se tem o papel de criptografador/descriptografador do Cloud KMS CryptoKey (`roles/cloudkms.cryptoKeyEncrypterDecrypter`) nos anéis de chave relevantes.
*   Aplique `constraints/gcp.restrictCmekCryptoKeyProjects` para restringir a seleção de chaves a projetos dedicados do Cloud KMS.

### Secret Manager

O [Secret Manager](https://docs.cloud.google.com/secret-manager/docs/overview?hl=pt-br) permite que aplicativos e pipelines acessem os valores dos secrets nomeados com base nas permissões concedidas com o IAM. Quando ativadas, as interações com o Secret Manager criam uma trilha de auditoria que pode ser usada para ajudar em casos de análise forense e conformidade.

Válido para:

*   A04: falhas criptográficas
*   A07: falhas de autenticação

Confira as práticas recomendadas para A04: falhas criptográficas:

*   [Criptografe secrets de alto valor usando CMEK](https://docs.cloud.google.com/secret-manager/docs/cmek?hl=pt-br) para controlar, girar ou revogar as chaves de criptografia primárias que encapsulam seus payloads de secrets.
*   [Use somas de verificação de integridade de dados](https://docs.cloud.google.com/secret-manager/docs/data-integrity?hl=pt-br) para manter e verificar a integridade dos dados do seu secret ao [adicionar](https://docs.cloud.google.com/secret-manager/docs/add-secret-version?hl=pt-br) e [acessar](https://docs.cloud.google.com/secret-manager/docs/access-secret-version?hl=pt-br) versões do secret.
*   [Replique secrets em várias regiões](https://docs.cloud.google.com/secret-manager/docs/choosing-replication?hl=pt-br) para alta disponibilidade e recuperação de desastres em zonas de implantação geográfica.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Remova valores sensíveis, como chaves de API, do código-fonte, dos arquivos `.env` e das configurações de build de contêineres e armazene as credenciais no Secret Manager. Busque valores descriptografados em tempo de execução usando [Google Cloud bibliotecas de cliente](https://docs.cloud.google.com/secret-manager/docs/reference/libraries?hl=pt-br), drivers CSI do GKE Secret Store ou vinculações de secrets do Cloud Run.
*   Aplique vinculações de política do IAM diretamente a segredos individuais específicos, concedendo aos microsserviços o papel de Acessador de Secrets do Secret Manager (`roles/secretmanager.secretAccessor`) apenas nos segredos específicos de que eles precisam.
*   Configure [programações de rotação automática](https://docs.cloud.google.com/secret-manager/docs/secret-rotation?hl=pt-br) no Secret Manager. Quando um intervalo de alternância começa, o Secret Manager publica uma notificação `SECRET_ROTATE` em um tópico designado do Pub/Sub. Configure uma função ou serviço do Cloud Run para ler a notificação, gerar um novo valor de secret, adicionar a nova versão ao Secret Manager e destruir a versão desativada.
*   Ative os [Registros de auditoria do Cloud no Secret Manager](https://docs.cloud.google.com/secret-manager/docs/audit-logging?hl=pt-br) para rastrear eventos de criação, destruição e acesso ao payload de versões secretas. Encaminhe esses registros para o Google SecOps e receba alertas sobre eventos de acesso suspeitos, como uma conta de serviço comprometida acessando segredos fora do horário operacional padrão ou tentando ler recursos secretos não aprovados.

### Security Command Center Premium

Com o [Security Command Center Premium](https://docs.cloud.google.com/security-command-center/docs/security-command-center-overview?hl=pt-br), é possível encontrar e corrigir configurações incorretas de segurança e ameaças ativas de tempo de execução, incluindo falhas de identificação e autenticação, no ambiente Google Cloude nos aplicativos da Web. O serviço [Web Security Scanner](https://docs.cloud.google.com/security-command-center/docs/concepts-web-security-scanner-overview?hl=pt-br) pode monitorar vulnerabilidades de aplicativos, incluindo vulnerabilidades de entidade externa XML (XXE), com verificações [projetadas para abranger os 10 principais controles da OWASP](https://docs.cloud.google.com/security-command-center/docs/concepts-web-security-scanner-overview?hl=pt-br#scan_findings).

Válido para:

*   A02: configuração incorreta de segurança
*   A05: injeção
*   A07: falhas de autenticação
*   A08: falhas de integridade de dados ou software

Confira as práticas recomendadas a seguir para A02: configuração incorreta de segurança:

*   [Aplique frameworks integrados](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-apply-framework?hl=pt-br) (como os comparativos de mercado do CIS ou o NIST) usando o Compliance Manager para avaliar suas configurações de nuvem em relação a frameworks regulatórios de segurança e comparativos de mercado do setor.
*   Ative o [Cloud Infrastructure Entitlement Management](https://docs.cloud.google.com/security-command-center/docs/ciem-overview?hl=pt-br) para gerenciar as identidades que têm acesso aos recursos nas suas implantações na nuvem e reduzir possíveis vulnerabilidades resultantes de configurações incorretas.
*   Analise e corrija os [resultados do Web Security Scanner](https://docs.cloud.google.com/security-command-center/docs/how-to-remediate-web-security-scanner-findings?hl=pt-br) para corrigir cabeçalhos de segurança de resposta HTTP configurados incorretamente, cabeçalhos de origem CORS inválidos e exibição de conteúdo misto.

Confira as práticas recomendadas a seguir para A05: injeção:

*   [Ative serviços](https://docs.cloud.google.com/security-command-center/docs/how-to-configure-security-command-center?hl=pt-br#built-in) como a Detecção de Ameaças a Máquinas Virtuais e a Detecção de Ameaças em Contêiner. Esses serviços verificam a memória do hipervisor e os eventos do kernel em busca de [scripts maliciosos](https://docs.cloud.google.com/security-command-center/docs/findings/threats/malicious-script-executed?hl=pt-br), [shells reversos](https://docs.cloud.google.com/security-command-center/docs/findings/threats/reverse-shell?hl=pt-br) e instalações de malware (usando os detectores [Binário adicionado executado](https://docs.cloud.google.com/security-command-center/docs/findings/threats/added-binary-executed?hl=pt-br) e [Biblioteca adicionada carregada](https://docs.cloud.google.com/security-command-center/docs/findings/threats/added-library-loaded?hl=pt-br)).
*   Configure o Web Security Scanner para monitorar aplicativos em execução em busca de falhas de [scripting em vários sites (XSS)](https://docs.cloud.google.com/security-command-center/docs/how-to-remediate-web-security-scanner-findings?hl=pt-br#xss) e [injeção de SQL (SQLi)](https://docs.cloud.google.com/security-command-center/docs/how-to-remediate-web-security-scanner-findings?hl=pt-br#sql_injection).
*   [Integre as descobertas do Security Command Center](https://docs.cloud.google.com/security-command-center/docs/how-to-export-data?hl=pt-br#continuous_exports) ao Google SecOps ou a SIEMs de terceiros para triagem automatizada e resposta a incidentes.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Monitore seus fluxos do Logging para detectar ataques baseados em credenciais usando os detectores [Força bruta: SSH](https://docs.cloud.google.com/security-command-center/docs/findings/threats/ssh-brute-force?hl=pt-br) e [Persistência: concessão anômala de IAM](https://docs.cloud.google.com/security-command-center/docs/findings/threats/iam-anomalous-grant?hl=pt-br).
*   Use os controles na nuvem [Usar autenticação multifator ou sem senha](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-cloud-controls?hl=pt-br#use-multifactor-or-passwordless-authentication), [Definir restrição de aplicativo em chaves de API](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-cloud-controls?hl=pt-br#restrict-applications-api-keys) e [Exigir rotação de chave de API](https://docs.cloud.google.com/security-command-center/docs/compliance-manager-cloud-controls?hl=pt-br#require-api-key-rotation) para detectar quando a MFA não está sendo usada e monitorar o uso da chave de API.
*   Corrija as descobertas de [vazamento de ID de sessão](https://docs.cloud.google.com/security-command-center/docs/how-to-remediate-web-security-scanner-findings?hl=pt-br#session_id_leak) configurando back-ends da Web para armazenar tokens de sessão em cookies HTTP com flags `HttpOnly` e `Secure`.

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Configure o Web Security Scanner para verificar endpoints da Web em busca de bugs de execução baseados em assinatura e gerar uma descoberta de gravidade alta [`STRUTS_INSECURE_DESERIALIZATION`](https://docs.cloud.google.com/security-command-center/docs/how-to-remediate-web-security-scanner-findings?hl=pt-br#struts_insecure_deserialization) se um aplicativo estiver executando uma versão vulnerável do Apache Struts.
*   Corrija a descoberta `STRUTS_INSECURE_DESERIALIZATION` fazendo upgrade da versão da biblioteca de framework vulnerável ou implantando o Assured OSS para extrair uma substituição verificada pelo Google.

### Proteção de Dados Sensíveis

Com a [Proteção de Dados Sensíveis](https://docs.cloud.google.com/sensitive-data-protection/docs/sensitive-data-protection-overview?hl=pt-br), você pode verificar dados potencialmente sensíveis armazenados em buckets, bancos de dados, comandos de IA generativa ou payloads de aplicativos de streaming para evitar vazamentos indesejados de informações. Se dados não permitidos forem identificados, a Proteção de dados sensíveis poderá [sinalizar ou editar](https://docs.cloud.google.com/sensitive-data-protection/docs/classification-redaction?hl=pt-br).

Válido para:

*   A04: falhas criptográficas
*   A09: falhas de geração de registros e alertas de segurança

Confira as práticas recomendadas para A04: falhas criptográficas:

*   Ative a [descoberta de dados sensíveis](https://docs.cloud.google.com/sensitive-data-protection/docs/data-profiles?hl=pt-br#data-profiles) para verificar continuamente seus recursos de armazenamento e banco de dados, gerar perfis de dados e informar métricas para relatórios de auditoria.
*   Use criptografia de preservação de formato, hash criptográfico ou tokenização baseada em chaves para [desidentificar dados sensíveis](https://docs.cloud.google.com/sensitive-data-protection/docs/inspect-sensitive-text-de-identify?hl=pt-br).
*   Implante [modelos de desidentificação](https://docs.cloud.google.com/sensitive-data-protection/docs/creating-templates-deid?hl=pt-br) reutilizáveis e gerenciados de forma centralizada para aplicar políticas de inspeção e mascaramento criptográfico consistentes em todas as equipes de desenvolvimento.
*   [Analise payloads de solicitações](https://docs.cloud.google.com/sensitive-data-protection/docs/discovery-for-vertex-ai?hl=pt-br) para ajudar a evitar que dados corporativos sensíveis ou PII vazem para pipelines de treinamento de IA generativa.

Confira as práticas recomendadas a seguir para A09: falhas de geração de registros e alertas de segurança:

*   Configure o coletor do Logging para enviar registros de aplicativos a um tópico do Pub/Sub. Anexe um assinante do Cloud Run que usa a [API Proteção de Dados Sensíveis](https://docs.cloud.google.com/sensitive-data-protection/docs/deidentify-sensitive-data?hl=pt-br) para verificar e desidentificar a carga útil do registro antes de gravar registros limpos no bucket final do Logging.
*   Use [filtros de exclusão](https://docs.cloud.google.com/kubernetes-engine/docs/how-to/exclude-logs?hl=pt-br) nos seus coletores do Logging para encaminhar apenas registros não estruturados de alto risco (como erros brutos de aplicativos, payloads de registro de usuários e registros de transações) pelo pipeline de sanitização.

### VirusTotal

A [API VirusTotal](https://www.virustotal.com/gui/home/upload) é uma plataforma de inteligência de ameaças e verificação de arquivos que analisa arquivos, URLs, domínios e endereços IP suspeitos para detectar malware, cavalos de troia e payloads maliciosos. Ao integrar a API VirusTotal aos pipelines de ingestão de arquivos, é possível verificar uploads não confiáveis antes que os arquivos sejam processados pelos sistemas de aplicativos.

Válido para:

*   A08: falhas de integridade de dados ou software
*   A05: injeção

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Implante [regras personalizadas de correspondência de assinaturas YARA-X](https://virustotal.github.io/yara/) para verificar estruturas de arquivos recebidos em busca de padrões binários e textuais maliciosos conhecidos, permitindo a detecção de variantes de malware mutantes.

Confira as práticas recomendadas a seguir para A05: injeção:

*   Use o [módulo de verificação privada do VirusTotal](https://docs.virustotal.com/docs/private-scanning) para verificar uploads sensíveis isoladamente e não compartilhar arquivos enviados com terceiros.
*   Implemente a limitação de taxa de API e o tratamento de exceções no código de ingestão para capturar códigos de status HTTP `429 Too Many Requests`.

### VPC Service Controls

Com o [VPC Service Controls](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=pt-br), é possível criar perímetros em torno dos seus recursos do Google Cloud para evitar a exfiltração de dados e reduzir os ataques de falsificação de solicitações do lado do servidor (SSRF, na sigla em inglês). O VPC Service Controls nega chamadas de API que cruzam limites de perímetro, a menos que sejam explicitamente permitidas por regras de entrada e saída.

Válido para:

*   A01: controle de acesso corrompido
*   A02: configuração incorreta de segurança

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Inclua serviços críticos (como Cloud Storage, BigQuery, Spanner e Agent Platform) em um [perímetro de serviço](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=pt-br) para restringir o acesso à API a redes VPC autorizadas e identidades confiáveis.
*   Configure [regras de perímetro de saída](https://docs.cloud.google.com/run/docs/securing/using-vpc-service-controls?hl=pt-br) em recursos sem servidor para bloquear a exfiltração de dados não autorizada causada por chamadas de API de saída para destinos externos fora do perímetro.
*   Restrinja o acesso à API entre perímetros e organizações usando regras explícitas de entrada e saída que especificam fontes de projetos aprovadas, APIs de destino e identidades de chamadores.

Confira as práticas recomendadas a seguir para A02: configuração incorreta de segurança:

*   Agrupe projetos em perímetros dedicados organizados por nível de segurança do ambiente (por exemplo, um perímetro de produção). Use [serviços acessíveis por VPC](https://docs.cloud.google.com/vpc-service-controls/docs/vpc-accessible-services?hl=pt-br) para restringir quais APIs internas do Google podem ser invocadas dentro do perímetro.
*   Roteie solicitações de API de saída de cargas de trabalho sem servidor pela rede VPC. Configure os serviços e as funções do Cloud Run para usar a saída VPC direta ou um conector de acesso VPC sem servidor com a entrada definida estritamente como "somente interno".
*   Vincule os [níveis de acesso do Access Context Manager](https://docs.cloud.google.com/vpc-service-controls/docs/use-access-levels?hl=pt-br) às regras de entrada do perímetro que avaliam uma combinação de sub-redes IP corporativas, declarações de identidade autenticadas e indicadores de integridade do dispositivo da Verificação de endpoints.
*   Mantenha um processo administrativo de breakglass pré-aprovado para resposta a incidentes de emergência e configure [alertas de monitoramento](https://docs.cloud.google.com/vpc-service-controls/docs/audit-logging?hl=pt-br) em eventos inesperados de violação de perímetro.

Serviços da Wiz
---------------

As seções a seguir descrevem as práticas recomendadas do OWASP Top 10 para serviços do [Wiz](https://www.wiz.io/) que se integram aoGoogle Cloud.

### Wiz Code

O [Wiz Code](https://www.wiz.io/platform/wiz-code) estende a segurança da nuvem aos fluxos de trabalho de desenvolvedores e pipelines de CI/CD. O Wiz Code correlaciona a telemetria de código para nuvem, verifica a infraestrutura como código (IaC), analisa dependências (SCA), detecta credenciais expostas e realiza testes estáticos de segurança de aplicativos (SAST).

Válido para:

*   A03: falhas na cadeia de suprimentos de software
*   A05: injeção
*   A07: falhas de autenticação

Confira as práticas recomendadas a seguir para A03: falhas na cadeia de suprimentos de software:

*   Integre a CLI do Wiz aos pipelines de CI/CD para impedir a mesclagem de solicitações de pull se elas introduzirem CVEs críticos, segredos expostos ou erros graves de configuração de IaC em ramificações protegidas.
*   Gere e exporte uma lista de materiais de software para cada build e mantenha a visibilidade contínua da cadeia de suprimentos no Wiz Cloud.
*   Implante extensões do Wiz Code IDE para fornecer feedback em tempo real aos desenvolvedores, detectando pacotes vulneráveis, chaves de API codificadas e erros de sintaxe antes do commit do código.
*   Integre o Wiz Code ao CodeMender em pipelines de CI/CD para gerar, testar e enviar solicitações de envio quando dependências vulneráveis de terceiros forem detectadas.

Confira as práticas recomendadas a seguir para A05: injeção:

*   Bloqueia a fusão de solicitações de pull se o scanner SAST detectar entrada de usuário não confiável fluindo para consultas de banco de dados ou comandos do SO sem higienização adequada.
*   Integre o plug-in do Wiz Code aos IDEs de desenvolvedores para fornecer alertas em tempo real se os desenvolvedores digitarem padrões de execução de comandos ou consulta SQL de concatenação não seguros.
*   Quando o Wiz Code sinaliza uma falha de injeção, encaminhe os rastreamentos de fluxo de dados para o CodeMender e crie um patch de correção verificado.

Confira as práticas recomendadas a seguir para A07: falhas de autenticação:

*   Implemente a verificação automatizada de secrets em IDEs de desenvolvedores, hooks locais de pré-commit e em pipelines de CI/CD para detectar credenciais expostas.
*   Para reduzir o roubo de credenciais, substitua as credenciais estáticas de longa duração por tokens dinâmicos de curta duração e acesso vinculado à identidade, como a [Federação de identidade da carga de trabalho](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=pt-br) ou a autenticação baseada em OIDC.
*   Implemente um playbook de resposta a incidentes automatizado para remover os segredos detectados do código, das variáveis de ambiente e dos registros de build.

### Wiz Cloud

O [Wiz Cloud](https://www.wiz.io/platform) analisa ambientes multicloud para identificar configurações incorretas de segurança, exposições de dados sensíveis e riscos de identidade. Usando o [Wiz Security Graph](https://www.wiz.io/lp/wiz-security-graph), o Wiz Cloud correlaciona fatores de risco em todas as camadas de infraestrutura para destacar caminhos de ataque críticos.

Válido para:

*   A01: controle de acesso corrompido
*   A02: configuração incorreta de segurança
*   A04: falhas criptográficas
*   A06: design não seguro

Confira as práticas recomendadas para A01: controle de acesso corrompido:

*   Rastreie e sinalize caminhos complexos de escalonamento de privilégios de várias etapas em funções e políticas do IAM para identificar onde os invasores podem se mover lateralmente ou aumentar os privilégios.
*   Mapeie as permissões de acesso ativas em contas de usuário, contas de serviço e agentes de IA para armazenamentos de dados críticos e revogue direitos com privilégios excessivos.
*   Integre descobertas de direitos de identidade com plataformas de orquestração para substituir vinculações permanentes de papéis administrativos por acesso just-in-time (JIT).
*   Monitore e visualize a movimentação de dados para detectar quando as PII de produção são copiadas ou sincronizadas em ambientes de teste ou desenvolvimento não seguros.

Confira as práticas recomendadas a seguir para A02: configuração incorreta de segurança:

*   Avalie e priorize o risco de configuração da nuvem correlacionando configurações incorretas com vários fatores de ataque usando o Wiz Security Graph.
*   Aplique frameworks de compliance integrados (como OWASP Top 10, CIS Benchmarks e NIST) para medir as configurações da nuvem em relação aos padrões do setor.
*   Integre o [scanner da CLI do Wiz](https://www.wiz.io/lp/wiz-cli) aos pipelines de CI/CD para revisar builds ou corrigir configurações incorretas de IaC antes da implantação.

Confira as práticas recomendadas para A04: falhas criptográficas:

*   Priorize a correção de bancos de dados e buckets de armazenamento que contenham credenciais de texto simples, chaves não hash ou dados sensíveis armazenados sem criptografia.
*   Execute a descoberta de dados do Wiz Cloud em diretórios de treinamento de IA, bancos de dados vetoriais e pipelines de RAG para verificar se os dados proprietários e as PII são editados antes da ingestão do LLM.
*   Verifique os ambientes para identificar recursos de dados não gerenciados e exclua dados redundantes para minimizar a superfície de ataque.

Confira as práticas recomendadas para A06: design não seguro:

*   Peça ao Wiz Red Agent para analisar interfaces arquitetônicas lógicas e simular caminhos de ataque para encontrar falhas de design inseguras antes da implantação em produção.
*   Transmita o contexto validado da cadeia de ataques do Wiz Red Agent para o CodeMender para identificar causas raiz e gerar solicitações de envio arquitetônicas testadas.

### Wiz Defend

O [Wiz Defend](https://www.wiz.io/platform/wiz-defend) oferece detecção e resposta na nuvem (CDR), proteção de tempo de execução de carga de trabalho e segurança de admissão do Kubernetes. O Wiz Defend monitora a atividade do plano de controle, detecta anomalias de execução, aplica políticas de admissão de contêineres e aciona a contenção automatizada.

Válido para:

*   A08: falhas de integridade de dados ou software
*   A09: falhas de geração de registros e alertas de segurança

Confira as práticas recomendadas para A08: falhas de integridade de dados ou software:

*   Configure as regras de admissão do Wiz Defend para inspecionar e rejeitar manifestos de implantação do Kubernetes que tentam executar contêineres com privilégios de raiz, solicitar namespaces de rede de host ou ativar `privileged: true`.
*   Configure webhooks de admissão de segurança críticos com [`failurePolicy: Fail` (fail-closed)](https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/#failure-policy) em produção para bloquear contêineres não confiáveis se o webhook estiver inacessível.

Confira as práticas recomendadas a seguir para A09: falhas de geração de registros e alertas de segurança:

*   Exporte os registros de auditoria Google Cloud usando um coletor de registros para um tópico do Pub/Sub para que o Wiz Defend possa ingerir e analisar atividades do plano de controle e eventos de carga de trabalho.
*   Implante o sensor de tempo de execução da Wiz em clusters do GKE e VMs do Compute Engine de alto valor para detectar ameaças de tempo de execução, explorações na memória e comprometimentos ativos.
*   Automatize playbooks de contenção para desativar imediatamente contas de serviço do IAM comprometidas ou isolar cargas de trabalho comprometidas.
*   Use o Wiz Blue Agent para investigar detecções de tempo de execução, correlacionando a telemetria de processos ativos e o contexto de identidade para determinar as causas raiz e os recursos afetados.

Manter a conformidade com o OWASP Top 10:2025
---------------------------------------------

O Wiz inclui uma [estrutura de conformidade com o OWASP Top 10 2025](https://www.wiz.io/academy/compliance/cloud-compliance-fast-track-guide) (link em inglês) que permite avaliar e monitorar sua postura. A estrutura de compliance do OWASP Top 10 2025 mapeia as políticas integradas do Wiz para as categorias de risco relevantes do OWASP e cria descobertas se um controle não estiver em conformidade. Você pode acompanhar sua pontuação de compliance ao longo do tempo. Se necessário, personalize a estrutura de conformidade do OWASP Top 10 2025 para atender aos requisitos da sua empresa.

A seguir
--------

Consulte o [catálogo de práticas recomendadas de segurança](https://docs.cloud.google.com/docs/security/security-best-practices-catalog?hl=pt-br) para mais informações.

Exceto em caso de indicação contrária, o conteúdo desta página é licenciado de acordo com a [Licença de atribuição 4.0 do Creative Commons](https://creativecommons.org/licenses/by/4.0/), e as amostras de código são licenciadas de acordo com a [Licença Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0). Para mais detalhes, consulte as [políticas do site do Google Developers](https://developers.google.com/site-policies?hl=pt-br). Java é uma marca registrada da Oracle e/ou afiliadas.

Última atualização 2026-11-11 UTC.
