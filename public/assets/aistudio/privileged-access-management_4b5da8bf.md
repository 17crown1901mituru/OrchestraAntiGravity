# Acesso privilegiado em Google Cloud &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/privileged-access-management

_Este conteúdo foi atualizado pela última vez em fevereiro de 2025 e representa o status quo no momento em que foi escrito. As políticas e os sistemas de segurança da Google podem mudar no futuro, à medida que melhoramos continuamente a proteção dos nossos clientes_.

Este documento descreve as funcionalidades e os produtos que ajudam a controlar o acesso que o pessoal da Google tem aos seus dados de clientes. Conforme definido nos [Google Cloud Termos de Utilização](https://cloud.google.com/terms?hl=pt), os _dados do cliente_ são dados que os clientes ou os utilizadores finais fornecem à Google através dos serviços nas respetivas contas.

Vista geral do acesso privilegiado
----------------------------------

Normalmente, apenas tem acesso aos seus dados de clientes e aos Google Cloud serviços que ativa. Em alguns casos, o pessoal da Google pode precisar de aceder aos seus dados para ajudar a fornecer um serviço contratado (por exemplo, precisa de apoio técnico ou de recuperar de uma indisponibilidade). Este tipo de acesso é conhecido como _acesso privilegiado_.

Os funcionários com privilégios elevados que recebem temporariamente ou adquirem autorizações elevadas representam um risco interno mais elevado. A nossa abordagem ao acesso privilegiado foca-se na redução do número de vetores de ataque possíveis. Por exemplo, usamos os seguintes controlos de segurança:

*   Esquemas de autenticação redundantes
*   Caminhos de acesso a dados limitados
*   Ações de registo e alerta nos nossos sistemas
*   Autorizações regulamentadas

Esta abordagem ajuda-nos a controlar e detetar ataques internos, limitar o impacto dos incidentes e reduzir o risco para os seus dados.

A estratégia de gestão de acesso privilegiado Google Cloud limita a capacidade de o pessoal da Google ver ou modificar os dados dos clientes. Na Google Cloud, os limites de acesso privilegiado são parte integrante do funcionamento dos nossos produtos.

Para mais informações sobre quando o pessoal da Google pode aceder aos seus dados, consulte a [Alteração ao Tratamento de Dados do Cloud](https://cloud.google.com/terms/data-processing-addendum?hl=pt).

Filosofia de acesso privilegiado
--------------------------------

A filosofia de acesso privilegiado da Google usa os seguintes princípios orientadores:

*   **As restrições de acesso têm de se basear em funções e aprovações de várias partes**: O pessoal da Google tem o acesso ao sistema negado por predefinição. Quando o acesso é concedido, é temporário e não é superior ao necessário para desempenhar a respetiva função. O acesso aos dados dos clientes, às operações críticas nos sistemas de produção e às modificações do código fonte é controlado por sistemas de validação manuais e automáticos. O pessoal da Google não pode aceder aos dados do cliente sem que outra pessoa aprove o pedido. O pessoal só pode aceder aos recursos necessários para desempenhar as suas funções e tem de apresentar uma justificação válida para aceder aos dados dos clientes. Para mais informações, consulte o artigo [Como a Google protege os respetivos serviços de produção](https://docs.cloud.google.com/docs/security/production-services-protection?hl=pt).
    
*   **As cargas de trabalho têm de ter proteção ponto a ponto**: com a [encriptação em trânsito](https://docs.cloud.google.com/docs/security/encryption-in-transit?hl=pt), a [encriptação em repouso](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=pt) e a [computação confidencial](https://docs.cloud.google.com/confidential-computing/docs/confidential-computing-overview?hl=pt) para encriptação em utilização, Google Cloud pode fornecer encriptação ponto a ponto das cargas de trabalho dos clientes.
    
*   **O registo e a auditoria são contínuos**: o acesso do pessoal da Google aos dados dos clientes é registado, e os sistemas de deteção de ameaças realizam auditorias em tempo real, alertando a equipa de segurança quando as entradas de registo correspondem a indicadores de ameaças. As equipas de segurança internas avaliam os alertas e os registos para identificar e investigar atividades anómalas, limitando o âmbito e o impacto de qualquer incidente. Para mais informações sobre a resposta a incidentes, consulte o [processo de resposta a incidentes de dados](https://docs.cloud.google.com/docs/security/incident-response?hl=pt).
    
*   **O acesso tem de ser transparente e incluir controlos do cliente**: pode usar [chaves de encriptação geridas pelo cliente (CMEK)](https://docs.cloud.google.com/kms/docs/cmek?hl=pt) para gerir as suas próprias chaves de encriptação e controlar o acesso às mesmas. Além disso, a [transparência de acesso](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/overview?hl=pt) garante que todo o acesso privilegiado tem uma [justificação empresarial que é registada](https://docs.cloud.google.com/logging/docs/audit/reading-access-transparency-logs?hl=pt#justification_reason_codes). A [aprovação de acesso](https://docs.cloud.google.com/assured-workloads/access-approval/docs/overview?hl=pt) permite-lhe aprovar ou recusar pedidos de acesso de pessoal da Google a determinados conjuntos de dados.
    

Acesso do pessoal da Google aos dados do cliente
------------------------------------------------

Por predefinição, o pessoal da Google não tem acesso aos Google Cloud dados do cliente.

Para obter acesso, o pessoal da Google tem de cumprir as seguintes condições:

*   Ser membro de listas de controlo de acesso (ACLs) relevantes.
*   Leia e confirme as políticas de acesso aos dados da Google regularmente.
*   Use um dispositivo fidedigno.
*   Inicie sessão com a autenticação multifator através de uma [chave de segurança Titan](https://cloud.google.com/security/products/titan-security-key?hl=pt), o que minimiza o risco de roubo de credenciais através de phishing.
*   Aceder a ferramentas que avaliam a justificação fornecida (por exemplo, o pedido de apoio técnico ou o ID do problema), a função do utilizador e o contexto.
*   Se for exigido pelas ferramentas, obtenha a aprovação da autorização junto de outro membro qualificado do pessoal da Google.
*   Se se inscreveu na aprovação de acesso, obtenha a sua aprovação.

As diferentes funções do pessoal requerem diferentes níveis de acesso. Por exemplo, as funções de apoio técnico têm acesso limitado aos dados de clientes que estão diretamente relacionados com um pedido de apoio técnico. As funções de engenharia podem exigir privilégios de sistema adicionais para resolver problemas mais complexos relacionados com a fiabilidade do serviço ou a implementação de serviços.

Quando a Google trabalha com terceiros (como fornecedores de apoio ao cliente) para fornecer serviços Google, avaliamos os terceiros para garantir que fornecem o nível adequado de segurança e privacidade.A Google publica uma [lista](https://cloud.google.com/terms/subprocessors?hl=pt#1) de todos os subprocessadores que são usados para ajudar a fornecer o serviço. Google Cloud

### Motivos para o pessoal da Google aceder aos dados dos clientes

Embora a Google Cloud seja concebida para automatizar, minimizar ou eliminar a necessidade de o pessoal da Google aceder aos dados dos clientes, ainda existem alguns casos em que o pessoal da Google pode aceder aos dados dos clientes. Estes casos incluem apoio técnico iniciado pelo cliente, indisponibilidade ou falha de ferramentas, solicitações legais de terceiros e revisões iniciadas pela Google.

#### Apoio técnico iniciado pelo cliente

O acesso do pessoal da Google aos dados dos clientes em serviços que usam a Transparência de acesso é normalmente o resultado de eventos iniciados pelo cliente, como entrar em contacto com o apoio ao cliente. Quando contacta o pessoal do serviço de apoio ao cliente para resolver um problema, o pessoal do serviço de apoio ao cliente só obtém acesso a dados pouco confidenciais. Por exemplo, se perdeu o acesso a um contentor, o pessoal do apoio ao cliente só tem acesso a dados pouco confidenciais, como o nome do contentor.

#### Indisponibilidade ou falha na ferramenta

Durante indisponibilidades ou falhas de ferramentas, o pessoal da Google pode aceder aos dados do cliente para fazer uma cópia de segurança ou uma recuperação, conforme necessário. Nestes casos, o pessoal da Google usa ferramentas que podem aceder diretamente aos dados dos clientes para maximizar a eficiência e resolver o problema atempadamente. Estas ferramentas registam este acesso e as justificações fornecidas pelos engenheiros. O acesso também é auditado e registado pela equipa de resposta de segurança da Google. Os serviços [suportados Google Cloud geram](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/supported-services?hl=pt) registos da Transparência de acesso que são visíveis para si durante uma indisponibilidade. Durante uma indisponibilidade, os engenheiros não podem ignorar a lista de autorizações para o recurso; no entanto, podem aceder aos dados sem a sua aprovação.

#### Solicitações legais de terceiros

As solicitações legais de terceiros são [raras](https://cloud.google.com/blog/products/identity-security/google-clouds-semi-annual-transparency-report-now-available?hl=pt) e apenas a equipa jurídica pode gerar uma justificação de acesso legal válida. A equipa jurídica revê a solicitação para garantir que cumpre os requisitos legais e as políticas da Google, envia-lhe uma notificação quando legalmente permitido e considera as objeções à divulgação dos dados na medida em que a lei o permite. Para mais informações, consulte o artigo [Solicitações governamentais relativas aos dados dos clientes na nuvem (PDF)](https://services.google.com/fh/files/blogs/government_access_technical_whitepaper.pdf?hl=pt).

#### Revisão iniciada pela Google

As revisões iniciadas pela Google também são raras. Quando ocorrem, devem garantir que os dados dos clientes estão seguros e protegidos e que não foram comprometidos. Os principais motivos para estas revisões são preocupações de segurança, fraude, abuso ou auditorias de conformidade. Por exemplo, se os detetores de mineração de bitcoins automatizados detetarem que uma MV está a ser usada para mineração de bitcoins, a Google revê o problema e confirma que o software malicioso num dispositivo de MV está a esgotar a capacidade da MV. A Google remove o software malicioso para que a utilização da VM volte ao normal.

Como a Google controla e monitoriza o acesso aos dados dos clientes
-------------------------------------------------------------------

Os controlos internos da Google incluem o seguinte:

*   Sistemas de controlo abrangentes em toda a infraestrutura para impedir o acesso não autorizado
*   Deteção e correção de acesso não autorizado através de controlos contínuos
*   Monitorização, alertas de violações e auditorias regulares por uma equipa de auditoria interna e auditores externos independentes

Para saber mais sobre como a Google protege a infraestrutura física, consulte a [vista geral do design de segurança da infraestrutura da Google](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=pt).

### Controlos ao nível da infraestrutura

A Google criou a sua infraestrutura com a segurança como prioridade. Uma vez que a infraestrutura global da Google é bastante homogénea, a Google pode usar uma infraestrutura automatizada para implementar controlos e limitar o acesso privilegiado. As secções seguintes descrevem alguns dos controlos que ajudam a implementar os nossos princípios de acesso com privilégios.

#### Autenticação forte para todos os acessos

A Google tem requisitos de autenticação rigorosos para o acesso de utilizadores (como um funcionário) e funções (como um serviço) aos dados. As tarefas executadas no nosso ambiente de produção usam estas identidades para aceder a repositórios de dados ou métodos de chamadas de procedimentos remotos (RPC) de outros serviços. Podem ser executados vários trabalhos com a mesma identidade. A nossa infraestrutura restringe a capacidade de implementar ou modificar tarefas que tenham uma identidade específica aos responsáveis pela execução do serviço, geralmente os nossos engenheiros de fiabilidade do site (SREs). Quando uma tarefa é iniciada, é aprovisionada com credenciais criptográficas. A tarefa usa estas credenciais para comprovar a sua identidade quando faz pedidos a outros serviços (através da [segurança da camada de transporte da aplicação (ALTS)](https://docs.cloud.google.com/docs/security/encryption-in-transit/application-layer-transport-security?hl=pt)).

#### Acesso sensível ao contexto

Para alcançar a segurança de confiança zero, a infraestrutura da Google usa o contexto para autenticar e autorizar utilizadores e dispositivos. As decisões de acesso não se baseiam exclusivamente em credenciais estáticas nem no facto de as decisões terem origem numa intranet empresarial. O contexto completo de um pedido (como a identidade do utilizador, a localização, a propriedade e a configuração do dispositivo, e as políticas de acesso detalhadas) é avaliado para determinar a validade do pedido e proteger contra tentativas de phishing e software malicioso de roubo de credenciais.

Ao usar o contexto, cada pedido de autenticação e autorização tem de usar palavras-passe fortes com tokens de segurança ou outros protocolos de autenticação de dois fatores. Os utilizadores autenticados e os dispositivos fidedignos recebem acesso limitado e temporário aos recursos necessários. Os inventários de máquinas são mantidos em segurança e o estado de cada dispositivo de ligação (por exemplo, atualizações do SO, patches de segurança, certificados de dispositivos, software instalado, análises de vírus e estado de encriptação) é avaliado quanto a potenciais riscos de segurança.

Por exemplo, o Chrome Enterprise Premium ajuda a garantir que as credenciais dos funcionários não são roubadas nem utilizadas indevidamente e que os dispositivos de ligação não são comprometidos. Ao transferir os controlos de acesso do perímetro da rede para o contexto de utilizadores e dispositivos individuais, o Chrome Enterprise Premium também permite que o pessoal da Google trabalhe de forma mais segura a partir de praticamente qualquer localização sem precisar de uma VPN.

A nossa infraestrutura é contentorizada através de um sistema de gestão de clusters denominado Borg. A [autorização binária para o Borg](https://docs.cloud.google.com/docs/security/binary-authorization-for-borg?hl=pt) garante que o software de produção é revisto e aprovado antes da implementação, especialmente quando o nosso código pode aceder a dados confidenciais. A autorização binária para o Borg ajuda a garantir que as implementações de código e configuração cumprem determinadas normas e alerta os proprietários de serviços quando estes requisitos não são cumpridos. Ao exigir que o código cumpra determinadas normas e práticas de gestão de alterações antes de aceder aos dados do utilizador, a autorização binária para o Borg reduz o potencial de o pessoal da Google (ou uma conta comprometida) agir sozinho para aceder aos dados do utilizador de forma programática.

### Aceda aos ficheiros de registo

A infraestrutura da Google regista o acesso aos dados e as alterações ao código. Os tipos de registo incluem o seguinte:

*   **Registos de clientes**: disponíveis através dos [registos de auditoria do Cloud](https://docs.cloud.google.com/logging/docs/audit?hl=pt).
*   **Registos de acesso administrativo**: disponíveis através da [Transparência de acesso](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/overview?hl=pt).
    
*   **Registos de integridade da implementação**: registos internos sobre exceções que são monitorizadas por uma equipa de segurança central dedicada à auditoria do acesso aos dados dos clientes. A monitorização de exceções ajuda a proteger dados confidenciais e a melhorar a fiabilidade da produção. A monitorização de exceções ajuda a garantir que o código-fonte não revisto ou não enviado não é executado em ambientes privilegiados, quer seja acidentalmente ou como resultado de um ataque deliberado.
    

### Deteção e resposta a incidentes

Para detetar e responder a suspeitas de violações de acesso, a Google usa equipas de investigação internas especializadas e controlos manuais e automáticos que combinam aprendizagem automática, pipelines de processamento de dados avançados e incidentes de informações sobre ameaças.

#### Desenvolvimento de sinais

A base das capacidades de deteção e resposta da Google é a inteligência contra ameaças, que é reforçada pela análise contínua de incidentes anteriores, tráfego de rede, dados internos, registos de acesso ao sistema, padrões de comportamento anómalos, os resultados de exercícios de segurança ofensiva e muitos mais alertas proprietários. Estes dados são analisados por equipas dedicadas que produzem uma base de dados dinâmica de sinais, ou _indicadores de ameaças_, que incluem todos os produtos Google. As equipas de engenharia usam indicadores de ameaças para desenvolver sistemas de deteção especializados para monitorizar sistemas internos quanto a atividade maliciosa, alertar o pessoal adequado e implementar respostas automáticas (por exemplo, revogar o acesso a um recurso).

#### Deteção de ameaças

As ameaças são detetadas principalmente através da análise de registos e da correspondência de entradas de registos com indicadores de ameaças. Como resultado da autenticação forte, a Google pode distinguir entre eventos humanos, eventos de serviços e eventos de roubo de identidade de serviços nos registos para dar prioridade às investigações sobre o acesso humano real. As atividades que envolvem o acesso a dados do utilizador, código fonte e informações confidenciais são registadas, e é necessária uma justificação empresarial ou uma exceção. As ameaças podem incluir um indivíduo a tentar tomar medidas unilaterais em sistemas confidenciais ou a tentar aceder aos dados do utilizador sem um motivo empresarial válido. Estes tipos de atividades têm procedimentos de alerta definidos.

#### Investigação de incidentes

Quando são detetadas violações de políticas, as equipas de segurança separadas das equipas de engenharia e operações principais fornecem supervisão independente e realizam uma investigação inicial. As equipas de segurança concluem as seguintes tarefas:

*   Reveja os detalhes do incidente e determine se o acesso foi intencional, não intencional, acidental, causado por um erro ou uma configuração incorreta, ou o resultado de controlos inadequados (por exemplo, um atacante externo roubar e usar as credenciais de um funcionário comprometido).
*   Se o acesso for não intencional ou acidental (por exemplo, se o pessoal da Google não tiver conhecimento ou violar por engano os protocolos de acesso), as equipas podem tomar medidas imediatas para corrigir o problema (por exemplo, recuperando a propriedade intelectual).
*   Se for detetado um comportamento malicioso, a equipa de segurança encaminha o incidente e recolhe informações adicionais, incluindo dados e registos de acesso ao sistema, para determinar o âmbito e o impacto do incidente.
*   Consoante os resultados dessa consulta, a equipa de segurança envia incidentes para investigação, documentação e resolução adicionais ou, em casos extremos, encaminha o incidente para autoridades externas ou para as autoridades policiais.

#### Remediação

A equipa de segurança usa incidentes anteriores para identificar e resolver vulnerabilidades e melhorar as capacidades de deteção. Todos os incidentes são documentados e os metadados são extraídos para identificar táticas, técnicas e procedimentos específicos para cada exploração. A equipa usa esses dados para desenvolver novos indicadores de ameaças, reforçar as proteções existentes ou fazer pedidos de funcionalidades para melhorias de segurança.

Os seguintes Google Cloud serviços oferecem-lhe opções para alcançar visibilidade e controlo sobre o acesso da Google aos seus dados.

| Google Cloud serviço | Descrição |
| --- | --- |
| 
Aprovação de acesso

 | 

Se tiver dados altamente confidenciais ou restritos, a aprovação de acesso permite-lhe exigir a sua aprovação antes de um administrador autorizado da Google poder aceder aos seus dados para lhe prestar apoio técnico. Os pedidos de acesso aprovados são registados com registos da Transparência de acesso associados ao pedido de aprovação. Depois de aprovar um pedido, o acesso tem de ser devidamente privilegiado no Google antes de ser permitido. Para ver uma lista dosGoogle Cloud serviços que suportam a aprovação de acesso, consulte os [serviços suportados](https://docs.cloud.google.com/assured-workloads/access-approval/docs/supported-services?hl=pt).

 |
| 

Transparência de acesso

 | 

Os registos da Transparência de acesso registam o acesso administrativo por parte do pessoal autorizado da Google quando este presta apoio técnico à sua organização ou mantém a disponibilidade do serviço. Para ver uma lista dos Google Cloud serviços que suportam a transparência de acesso, consulte a secção [Serviços suportados](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/supported-services?hl=pt).

 |
| 

Assured Workloads

 | 

Use o [Assured Workloads](https://docs.cloud.google.com/assured-workloads/docs/overview?hl=pt) se a sua empresa precisar de apoio técnico regional dedicado, programas regulamentares certificados (por exemplo, FedRAMP ou ITAR) ou programas como os controlos soberanos para a UE. O Assured Workloads oferece aos Google Cloud utilizadores um fluxo de trabalho de ativação para criar e monitorizar o ciclo de vida dos pacotes de controlos de que necessita.

 |
| 

Cloud KMS

 | 

Use o [Cloud KMS](https://docs.cloud.google.com/kms/docs/key-management-service?hl=pt) com o Cloud EKM para controlar as suas chaves de encriptação. O Cloud KMS com o Cloud EKM permite-lhe encriptar dados com chaves de encriptação armazenadas e geridas num sistema de gestão de chaves de terceiros implementado fora da infraestrutura da Google. O EKM na nuvem permite-lhe manter a separação entre os dados em repouso e as chaves de encriptação, enquanto continua a usar o poder da computação na nuvem e das estatísticas.

 |
| 

Computação confidencial

 | 

Use a computação confidencial para encriptar os dados em utilização. Google Cloud inclui os seguintes serviços que ativam a computação confidencial:

*   **VM confidencial**: ative a encriptação de dados em utilização para cargas de trabalho que usam VMs
*   **Nós confidenciais do Google Kubernetes Engine**: ative a encriptação de dados em utilização para cargas de trabalho que usam contentores
*   **Dataflow confidencial**: ative a encriptação de dados em utilização para análise de streaming e aprendizagem automática
*   **Dataproc confidencial**: ative a encriptação de dados em utilização para o tratamento de dados
*   **Espaço confidencial**: ative a encriptação de dados em utilização para análise de dados conjunta e aprendizagem automática

Estes serviços permitem-lhe reduzir o [limite de confiança](https://en.wikipedia.org/wiki/Trust_boundary) para que menos recursos tenham acesso aos seus dados confidenciais. Para mais informações, consulte o artigo [Implemente a computação confidencial no Google Cloud](https://docs.cloud.google.com/architecture/confidential-computing-analytics-ai?hl=pt#implement-confidential).

 |
| 

Justificações de acesso a chaves

 | 

Use [justificações de acesso a chaves](https://docs.cloud.google.com/assured-workloads/key-access-justifications/docs/overview?hl=pt) para a soberania e a deteção de dados.

As Justificações de acesso a chaves fornecem uma justificação sempre que as suas chaves alojadas externamente são usadas para desencriptar dados. As Justificações de acesso às chaves requerem o Cloud KMS com o Cloud HSM ou o Cloud KMS com o Cloud EKM para avançar o controlo que tem sobre os seus dados. Tem de aprovar o acesso antes de o pessoal da Google poder desencriptar os seus dados em repouso.

 |

O que se segue?
---------------

*   Para saber mais sobre o nosso compromisso de proteger a privacidade dos dados dos clientes, consulte os [Google Cloud princípios de privacidade comuns](https://cloud.google.com/privacy/common-privacy-principles?hl=pt).
*   Para saber mais sobre os princípios essenciais dos controlos que impedem o acesso administrativo não autorizado, consulte o artigo [Vista geral dos controlos de acesso administrativo](https://docs.cloud.google.com/assured-workloads/cloud-provider-access-management/docs/administrative-access?hl=pt).
    
*   Para ver a lista de justificações empresariais para as quais o pessoal da Google pode pedir acesso aos dados de clientes, consulte os [códigos de motivos de justificação](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/reading-logs?hl=pt#justification-reason-codes).
