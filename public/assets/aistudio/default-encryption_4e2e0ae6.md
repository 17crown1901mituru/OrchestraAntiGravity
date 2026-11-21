# Criptografia padrão em repouso &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/encryption/default-encryption

_Este conteúdo foi atualizado pela última vez em maio de 2024 e representa o estado do momento em que foi escrito. Os sistemas e as políticas de segurança do Google podem mudar no futuro, à medida que a proteção dos clientes é aprimorada._

No Google, nossa estratégia de segurança abrangente inclui criptografia em repouso, que ajuda a proteger os dados do cliente contra invasores. Criptografamos todo o conteúdo do cliente do Google em repouso, sem nenhuma ação necessária da sua parte, utilizando um ou mais mecanismos de criptografia. Neste documento, descrevemos nossa abordagem com relação à criptografia em repouso padrão para a infraestrutura do Google e o Google Cloude como a usamos para manter as informações dos clientes mais seguras.

Este documento é destinado a arquitetos e equipes de segurança que estão usando ou considerando o Google. Neste documento, presumimos um conhecimento básico de [criptografia](https://wikipedia.org/wiki/Encryption) e [primitivos criptográficos](https://wikipedia.org/wiki/Cryptographic_primitive). Para mais informações sobre criptografia, consulte [Introdução à criptografia moderna](https://www.cs.umd.edu/%7Ejkatz/imc.html).

A criptografia em repouso é usada para ajudar a proteger os dados armazenados em um disco (incluindo unidades de estado sólido) ou mídia de backup. Todos os dados armazenados pelo Google são criptografados na camada de armazenamento pelo algoritmo padrão de criptografia avançada (AES, na sigla em inglês), AES-256. Usamos uma biblioteca criptográfica comum, a Tink, que inclui nosso módulo validado FIPS 140-2 (chamado [_BoringCrypto_](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=pt-br)) para implementar a criptografia de forma consistente em Google Cloud.

Somos proprietários e gerenciamos as chaves usadas na criptografia em repouso padrão. Se você usa Google Cloud, o Cloud Key Management Service permite criar suas próprias chaves de criptografia que podem ser usadas para adicionar criptografia de envelope aos seus dados. Com o Cloud KMS, é possível criar, girar, rastrear e excluir chaves. Para mais informações, consulte [Investigação do Cloud Key Management Service](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=pt-br).

Chaves em Google Cloud
----------------------

A tabela a seguir descreve as diferentes propriedades das chaves em Google Cloud.

| Tipo de chave | Cloud KMS Autokey | Cloud KMS gerenciado pelo cliente (manual) | Google-owned and Google-managed encryption key (criptografia padrão do Google) |
| --- | --- | --- | --- |
| 
Pode ver metadados importantes

 | 

Sim

 | 

Sim

 | 

Não

 |
| 

Propriedade das chaves[1](#footnote1)

 | 

Cliente

 | 

Cliente

 | 

Google

 |
| 

Pode gerenciar[2](#footnote2) e controlar[3](#footnote3) chaves

 | 

A criação e a atribuição de chaves são automatizadas. O controle manual do cliente é totalmente compatível.

 | 

Cliente, somente controle manual

 | 

Google

 |
| 

Suporte aos requisitos regulamentares para chaves gerenciadas pelo cliente

 | 

Sim

 | 

Sim

 | 

Não

 |
| 

Compartilhamento de chaves

 | 

Exclusivo para um cliente.

 | 

Exclusivo para um cliente.

 | 

Os dados de vários clientes geralmente são protegidos por chaves de criptografia de chaves (KEKs) compartilhadas.

 |
| 

Controle da rotação de chaves

 | 

Sim

 | 

Sim

 | 

[Não](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=pt-br#kek_management)

 |
| 

[Políticas da organização de CMEK](https://docs.cloud.google.com/kms/docs/cmek-org-policy?hl=pt-br)

 | 

Sim

 | 

Sim

 | Não |
| 

[Registrar o acesso administrativo e de dados às chaves de criptografia](https://docs.cloud.google.com/kms/docs/audit-logging?hl=pt-br)

 | 

Sim

 | 

Sim

 | 

Não

 |
| 

Separação lógica de dados por criptografia

 | 

Sim

 | 

Sim

 | 

[Não](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=pt-br#hardware)

 |
| 

Preços

 | 

[Varia](https://docs.cloud.google.com/kms/pricing?hl=pt-br)

 | [Varia](https://docs.cloud.google.com/kms/pricing?hl=pt-br) | 

Sem custo financeiro

 |

1 O proprietário da chave indica quem detém os direitos dela. As chaves que você possui têm acesso restrito ou nenhum acesso pelo Google.

2 O gerenciamento de chaves inclui as seguintes tarefas:

*   Criar chaves.
*   Escolha o nível de proteção das chaves.
*   Atribua autoridade para o gerenciamento das chaves.
*   Controlar o acesso às chaves.
*   Controlar o uso de chaves.
*   Definir e modificar o período de rotação de chaves ou acionar uma rotação de chaves.
*   Mudar o status da chave.
*   Destruir versões de chave.
*   Excluir versões de chaves, chaves e keyrings.

3 O controle de chaves significa definir controles sobre o tipo de chaves e como elas são usadas, detectar variância e planejar ações corretivas, se necessário. Você pode controlar suas chaves, mas delegar o gerenciamento delas a terceiros.

Como a criptografia em repouso ajuda a proteger os dados
--------------------------------------------------------

A criptografia em repouso é uma parte de uma estratégia de segurança mais ampla. A criptografia tem os seguintes benefícios:

*   Ajuda a garantir que, se os dados ficarem nas mãos de um invasor, o invasor não poderá ler os dados sem ter acesso às chaves de criptografia. Mesmo que os invasores recebam os dispositivos de armazenamento que contêm dados de clientes, eles não poderão entendê-los ou descriptografá-los.
*   Ajuda a reduzir a superfície de ataque cortando as camadas inferiores da pilha de hardware e software.
*   Atua como um estrangulamento porque as chaves de criptografia gerenciadas centralmente criam um único local em que o acesso aos dados é aplicado e pode ser auditado.
*   Ajuda a reduzir a superfície de ataque porque, em vez de ter que proteger todos os dados, as empresas podem concentrar as estratégias de proteção nas chaves de criptografia.
*   Oferece um importante mecanismo de privacidade para nossos clientes. Quando os dados são criptografados em repouso, eles limitam o acesso dos sistemas e engenheiros aos dados.

O que são os dados do cliente?
------------------------------

Conforme definido nos [Termos de Serviço doGoogle Cloud](https://cloud.google.com/terms/?hl=pt-br) , os _dados do cliente_ são dados que os clientes ou usuários finais fornecem ao Google pelos serviços das contas deles.

O conteúdo do cliente consiste nos dados que você gera por conta própria ou nos fornece, como dados armazenados nos buckets do Cloud Storage, volumes do Persistent Disk e snapshots de discos usados pelo Compute Engine. Este documento se concentra na criptografia em repouso padrão para esse tipo de dados do cliente.

Os metadados do cliente são dados sobre o conteúdo do cliente e incluem números de projeto gerados automaticamente, carimbos de data/hora, endereços IP, o tamanho em bytes de um objeto no Cloud Storage ou o tipo de máquina no Compute Engine. Os metadados do cliente são protegidos em um nível razoável para manter o desempenho e operações contínuas. Este documento não se concentra nas proteções de metadados.

Juntos, o conteúdo e os metadados do cliente compõem os dados do cliente.

O Google criptografa todo o conteúdo do cliente armazenado em repouso, sem que você precise fazer nada, usando um ou mais mecanismos de criptografia. As seções abaixo descrevem os mecanismos que usamos para criptografar o conteúdo do cliente.

### Camadas de criptografia

O Google utiliza diversas camadas de criptografia para proteger os dados. A utilização de várias camadas de criptografia adiciona uma proteção de dados redundante e nos permite selecionar a abordagem ideal com base nos requisitos do aplicativo.

O diagrama a seguir mostra as várias camadas de criptografia geralmente usadas para proteger os dados do usuário nos data centers de produção do Google. A criptografia do sistema de arquivos distribuída ou do banco de dados e de armazenamento de arquivos está em vigor para todos os dados do usuário, e a criptografia do dispositivo de armazenamento está em vigor para todos os dados nos data centers de produção do Google.

### Criptografia na camada de infraestrutura

Todos os sistemas de armazenamento do Google usam uma arquitetura de criptografia semelhante, embora os detalhes de implementação sejam diferentes para cada sistema. Essas informações são divididas em blocos lógicos para armazenamento. Cada bloco pode ter vários GBs de tamanho. Cada bloco é criptografado no nível de armazenamento com uma chave de criptografia de dados individual (DEK, na sigla em inglês): dois blocos não terão a mesma DEK, mesmo que sejam de propriedade do mesmo cliente ou armazenados na mesma máquina.

Se um bloco de dados for atualizado, ele será criptografado com uma nova chave, em vez de reusar a chave atual. Esse particionamento de dados, cada um usando uma chave diferente, limita o risco de uma possível chave de criptografia de dados ser comprometida apenas para esse bloco de dados.

O Google criptografa os dados antes que eles sejam gravados em um sistema de armazenamento de banco de dados ou disco de hardware. A criptografia é inerente a todos os nossos sistemas de armazenamento, e não adicionada posteriormente.

Cada bloco de dados lógicos tem um identificador exclusivo. As listas de controle de acesso (ACLs) ajudam a garantir que cada bloco seja descriptografado somente pelos serviços do Google que operam com papéis autorizados, que recebem acesso somente nesse momento. Essa limitação de acesso ajuda a impedir o acesso aos dados sem autorização, fortalecendo a segurança de dados e a privacidade.

Cada bloco é distribuído nos sistemas de armazenamento do Google e é replicado em formato criptografado para backup e recuperação de desastres. Um invasor que quiser acessar dados do cliente precisará saber e acessar dois itens: todos os blocos de armazenamento que correspondem aos dados desejados e todas as chaves de criptografia correspondentes aos blocos.

O diagrama a seguir mostra como os dados são enviados para nossa infraestrutura e depois divididos em blocos criptografados para armazenamento.

Usamos o algoritmo AES para criptografar dados em repouso. Todos os dados no nível de armazenamento são criptografados por DEKs, que usam AES-256 por padrão, exceto um pequeno número de [Persistent Disks](https://cloud.google.com/persistent-disk?hl=pt-br) que foram criados antes de 2015 e usam o AES-128. O AES é amplamente utilizado porque o AES-256 e o AES-128 são recomendados pelo [National Institute of Standards and Technology (NIST)](https://csrc.nist.gov/publications/detail/sp/800-131a/rev-2/final) para uso em armazenamentos de longo prazo e o AES geralmente é incluído como parte dos requisitos de conformidade do cliente.

Um bloco de dados lógico pode conter os dados de vários clientes. Se você quiser separar os dados de forma lógica usando criptografia, ative o [Cloud Key Management Service](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=pt-br#cloud-kms-design-pillars).

### Criptografia na camada do dispositivo de armazenamento

Além da [criptografia no nível do sistema de armazenamento](#hardware), os dados também são criptografados no nível do dispositivo de armazenamento com AES-256 para unidades de disco rígido (HDD) e unidades de estado sólido (SSD), usando uma chave separada no nível do dispositivo (que é diferente da usada para criptografar os dados no nível do armazenamento). Um número pequeno de HDDs legados usam o AES-128. Os SSDs usados pelo Google implementam exclusivamente o AES-256 para dados do usuário.

### Criptografia de backups

Nosso sistema de backup garante que os dados permaneçam criptografados durante todo o processo de backup. Essa abordagem evita uma exposição desnecessária de dados de texto simples.

Além disso, o sistema de backup criptografa a maioria dos arquivos de backup de forma independente com a própria DEK. A DEK é derivada de uma chave armazenada no Keystore e de uma semente gerada por arquivo aleatoriamente no momento do backup. Outra DEK é usada para todos os metadados em backups, que também são armazenados no Keystore.

### Conformidade com o FIPS para dados em repouso

O Google usa um módulo de criptografia [validado pelo FIPS 140-2](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=pt-br) [(certificado 4407)](https://csrc.nist.gov/projects/cryptographic-module-validation-program/certificate/4407) em nosso ambiente de produção.

Gerenciamento de chaves
-----------------------

Devido ao grande volume de chaves no Google e à necessidade de baixa latência e alta disponibilidade, as chaves de criptografia de dados (DEK, na sigla em inglês) são armazenadas próximas aos dados criptografados. As DEKs são criptografadas com (encapsulado por) uma chave de criptografia de chaves (KEK), usando uma técnica conhecida como [criptografia de envelope](https://docs.cloud.google.com/kms/docs/envelope-encryption?hl=pt-br). Essas KEKs não são específicas aos clientes. em vez disso, há uma ou mais KEKs para cada serviço.

Essas KEKs são armazenadas de maneira centralizada no Keystore, um repositório criado especificamente para armazenar chaves. Ter um número menor de KEKs do que DEKs e usar um Keystore central faz com que o armazenamento e a criptografia de dados em nossa escala seja gerenciável, o que nos permite rastrear e controlar o acesso aos dados de um ponto central.

No Google Cloud, cada cliente pode ter recursos compartilhados e não compartilhados. Um exemplo de recurso compartilhado é uma imagem de base compartilhada no Compute Engine. No caso de recursos compartilhados, vários clientes se referem a uma única cópia, criptografada por uma única DEK. Os recursos não compartilhados são divididos em blocos de dados e criptografados com chaves separadas das chaves usadas para outros clientes. Essas chaves são separadas até mesmo das chaves que protegem outras partes dos dados do mesmo cliente. Há exceções, como o Datastore, o App Engine ou o Pub/Sub, em que os dados de mais de um cliente podem ser criptografados com a mesma DEK.

### Como gerar DEKs

O sistema de armazenamento gera DEKs usando a biblioteca criptográfica comum do Google. Em geral, as DEKs são enviadas ao Keystore para encapsular com a KEK do sistema de armazenamento, e são transmitidas de volta ao sistema de armazenamento para serem mantidas com os blocos de dados. Quando um sistema de armazenamento precisa recuperar dados criptografados, ele recupera a DEK encapsulada e a envia ao keystore. Em seguida, o keystore verifica se esse serviço está autorizado a usar a KEK e, em caso positivo, desencapsula e retorna a DEK de texto simples ao serviço. O serviço, então, utiliza a DEK para descriptografar o bloco de dados em texto simples e verificar sua integridade.

Todos os sistemas de armazenamento do Google Cloud aderem a esse modelo de gerenciamento de chaves, mas a maioria dos sistemas também implementa outros níveis de KEKs de armazenamento para criar uma hierarquia de chaves. Isso permite que os sistemas ofereçam baixa latência e usem a KEK de nível mais alto (armazenada no Keystore) como a raiz de confiança.

### Como gerar KEKs

A maioria das KEKs para criptografia de blocos de dados são geradas dentro do keystore. O restante é gerado nos serviços de armazenamento. Para manter a consistência, todas as KEKs são geradas utilizando a biblioteca criptográfica comum do Google por meio de um gerador de números aleatórios (RNG, na sigla em inglês) criado pelo Google. Esse RNG tem como base NIST 800-90Ar1 CTR-DRBG e gera uma KEK AES-256. Antes, ele era AES-128, e algumas dessas chaves permanecem ativas para descriptografar dados.

Para processadores Intel e AMD, o RNG é propagado pela [instrução RDRAND](https://software.intel.com/en-us/articles/intel-digital-random-number-generator-drng-software-implementation-guide) e pelo RNG do kernel do Linux. Por sua vez, o RNG do kernel do Linux é propagado por várias fontes de entropia independentes, incluindo RDRAND e eventos entrópicos do ambiente do data center, como medições refinadas de buscas de disco e tempos de chegada entre pacotes. Para processadores Arm, o RNG é propagado pelo RNG do kernel do Linux.

As DEKs são encapsuladas com as KEKs usando AES-256 ou AES-128, dependendo do serviço doGoogle Cloud . No momento, estamos trabalhando para fazer upgrade de todas as KEKs dos serviços doGoogle Cloud para o AES-256.

### Gerenciamento de KEK

O Keystore foi criado com a finalidade única de gerenciar KEKs. Desde a concepção, as KEKs usadas por sistemas de armazenamento não podem ser exportadas do Keystore: Toda criptografia e descriptografia com essas chaves precisa ser feita no Keystore. Isso ajuda a evitar vazamentos e uso indevido, além de permitir que o Keystore crie uma trilha de auditoria quando as chaves forem usadas.

O KMS pode alternar automaticamente as KEKs em intervalos de tempo regulares utilizando a biblioteca criptográfica comum do Google para gerar novas chaves. Normalmente nos referimos a apenas uma única chave, mas realmente queremos que os dados sejam protegidos usando um conjunto de chaves: uma chave está ativa para criptografia e um conjunto de chaves históricas está ativo para descriptografia. O número de chaves históricas é determinado pelo cronograma de rotação de chaves. As KEKs são armazenadas em backup para fins de recuperação de desastres e são recuperáveis indefinidamente.

O uso de KEKs é gerenciado por ACLs no Keystore para cada chave, com uma política por chave. Apenas usuários e Serviços do Google autorizados têm permissão para acessar uma chave. O uso de cada chave é rastreado no nível da operação individual que exige essa chave. Portanto, cada vez que um usuário utiliza uma chave, o usuário é autenticado e registrado. Todo acesso aos dados dos usuários é auditável como parte das políticas gerais de segurança e privacidade do Google.

### Processo para acessar blocos de dados criptografados

Quando um serviço do Google acessa um bloco criptografado de dados, ocorre o seguinte:

1.  O serviço faz uma chamada para o sistema de armazenamento em busca dos dados necessários.
2.  O sistema de armazenamento identifica os blocos em que esses dados estão armazenados (os IDs dos blocos) e onde eles estão armazenados.
3.  Para cada bloco, o sistema de armazenamento reconhece a DEK encapsulada e armazenada com esse bloco (em alguns casos, isso é feito pelo serviço) e a envia para o keystore para desencapsulamento.
4.  O sistema de armazenamento verifica se o job identificado tem permissão para acessar esse bloco de dados com base em um identificador de job e usar o ID do bloco. O keystore verifica se o sistema de armazenamento está autorizado a usar a KEK associada ao serviço e descompactar essa DEK específica.
5.  O keystore realiza uma das seguintes ações:
    *   Envia a DEK desencapsulada para o sistema de armazenamento, que descriptografa o bloco de dados e o envia para o serviço.
    *   Em alguns casos raros, transmite a DEK desencapsulada para o serviço. O sistema de armazenamento transmite o bloco de dados criptografado para o serviço, que descriptografa o bloco de dados e o usa.

Esse processo é diferente em dispositivos de armazenamento dedicados, em que o dispositivo gerencia e protege a chave de criptografia de dados no nível do dispositivo.

O diagrama a seguir mostra esse processo. Para descriptografar um bloco de dados, o serviço de armazenamento chama o Keystore para recuperar a DEK desencapsulada para esse bloco.

### Hierarquia de chave de criptografia e raiz de confiança

O keystore é protegido por uma chave raiz chamada _chave-chave de keystore_, que envolve todas as KEKs no keystore. Essa chave-mestra de keystore é AES-256 e é armazenada em outro serviço de gerenciamento de chaves, chamado KeyStore raiz. Antes, a chave mestra do keystore era AES-128, e algumas dessas chaves permanecem ativas para descriptografar dados. Para ter mais segurança, o Root Keystore não é executado em máquinas de produção gerais, mas sim apenas em máquinas dedicadas em cada data center do Google.

O Keystore raiz, por sua vez, tem a própria chave raiz, chamada _chave mestre de keystore raiz_, que também é AES-256 e é armazenada em uma infraestrutura ponto a ponto, que é chamado _distribuidor de chaves mestras do keystore_, que replica essas chaves globalmente. Antes, a chave mestra do keystore raiz era AES-128, e algumas dessas chaves permanecem ativas para descriptografar dados. O distribuidor de chaves mestras do keystore raiz mantém apenas as chaves na RAM nas mesmas máquinas dedicadas que o Keystore raiz e usa a geração de registros para verificar o uso adequado.

Quando uma nova instância do distribuidor de chaves mestras do KMS raiz é iniciada, ela é configurada com uma lista de nomes de host que já executam instâncias do distribuidor. Então, as instâncias do distribuidor podem encontrar a chave mestra do keystore raiz de outras instâncias em execução. Além dos mecanismos de recuperação de desastres descritos em [Disponibilidade e replicação globais](#global) , a chave mestra do keystore raiz só existe na RAM em um número limitado de máquinas protegidas.

Para lidar com o cenário em que todas as instâncias do distribuidor de chave mestra do keystore em uma região são reiniciadas simultaneamente, a chave mestra do keystore raiz também é armazenada em backup em dispositivos de hardware seguros armazenados em cofres físicos de altamente áreas seguras em várias localizações geográficas distribuídas. Esse backup seria necessário apenas se todas as instâncias do distribuidor em uma região estivessem inativas ao mesmo tempo. Apenas alguns funcionários do Google podem acessar esses cofres.

O diagrama a seguir mostra a hierarquia de chaves de criptografia. A hierarquia da chave de criptografia protege um bloco de dados com uma DEK, encapsulada com uma KEK no Keystore, que, por sua vez, é protegido pelo Keystore raiz e pelo distribuidor de chaves mestras do keystore.

### Resumo do gerenciamento de chaves

A lista a seguir resume o gerenciamento de chaves no Google:

*   Os dados são separados em blocos e criptografados com DEKs.
*   As DEKs são criptografadas com KEKs.
*   As KEKs são armazenadas no Keystore.
*   O keystore é executado em diversas máquinas em data centers ao redor do mundo.
*   As chaves do Keystore são encapsuladas com a chave mestra do Keystore, armazenada no Keystore raiz.
*   O keystore raiz é muito menor que o keystore e é executado apenas em máquinas dedicadas em cada data center.
*   As chaves raiz do Keystore são encapsuladas com a chave mestra do keystore principal, que é armazenada no distribuidor de chave mestra do keystore raiz.
*   O distribuidor de chaves mestras do keystore raiz é uma infraestrutura ponto a ponto executada simultaneamente na RAM em máquinas dedicadas globalmente. Cada máquina recebe o material de chave de outras instâncias em execução na região.
*   Caso todas as instâncias do distribuidor em uma região fiquem inativas, uma chave mestre será armazenada em outro hardware seguro em cofres físicos de locais limitados do Google.

### Disponibilidade e replicação globais

Em todos os níveis, alta disponibilidade, baixa latência e acesso global às chaves são essenciais. Essas características são necessárias para que os serviços de gerenciamento de chaves sejam usados no Google.

Por esse motivo, o Keystore é altamente escalonável e é replicado milhares de vezes em nossos data centers em todo o mundo. Ele é executado em máquinas comuns em nossa frota de produção, e as instâncias do Keystore são executadas globalmente para dar suporte às operações do Google. É por isso que a latência de qualquer operação de chave única é muito baixa.

O keystore raiz é executado em diversas máquinas dedicadas a operações de segurança em cada data center. O distribuidor de chaves mestras do Keystore é executado nessas mesmas máquinas, um para um, com o Keystore raiz. O distribuidor de chaves mestras do Root Keystore fornece um mecanismo de distribuição usando um [protocolo gossiping](https://dl.acm.org/citation.cfm?doid=41840.41841). Em um intervalo de tempo fixo, cada instância do distribuidor escolhe uma outra instância aleatória para comparar as chaves dela e reconcilia qualquer diferença nas versões das chaves. Nesse modelo, não há um nó central de que toda nossa infraestrutura depende. Esse método de distribuição nos permite manter e proteger o material da chave com alta disponibilidade.

Biblioteca criptográfica pública do Google
------------------------------------------

A biblioteca criptográfica comum do Google é a [Tink](https://developers.google.com/tink?hl=pt-br), que usa nosso módulo validado pelo FIPS 140-2, o [BoringCrypto](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=pt-br). A Tink está disponível para todos os Google Developers. O uso consistente de uma biblioteca comum significa que apenas uma pequena equipe de criptografistas precisa implementar esse código controlado e revisado de modo rígido, tornando desnecessária cada equipe do Google para desenvolver a própria criptografia de forma independente. Uma equipe especial de segurança do Google é responsável pela manutenção dessa biblioteca em todos os produtos.

A biblioteca de criptografia Tink é compatível com diversos modos e tipos de chaves de criptografia, que são revisados regularmente para garantir que estejam atualizados com os últimos ataques.

No momento, usamos os algoritmos de criptografia a seguir para criptografia em repouso de DEKs e KEKs. Eles estão sujeitos a alterações, conforme melhoramos nossos recursos e nossa segurança.

| **Primitiva criptográfica** | **Protocolos preferidos** | **Outros protocolos compatíveis** |
| --- | --- | --- |
| Criptografia simétrica | AES-GCM (256 bits) | 
*   AES-CBC e AES-CTR (128 e 256 bits)

*   AES-EAX (128 e 256 bits)

 |
| Assinaturas simétricas (quando utilizadas com o AES-CBC e o AES-CTR acima para autenticação) | HMAC-SHA256 | 

*   HMAC-SHA512

*   HMAC-SHA1

 |

Existem outros protocolos criptográficos na biblioteca e historicamente eram compatíveis, mas esta tabela aborda os principais usos no Google.

Pesquisa e inovação em criptografia
-----------------------------------

Para acompanhar o ritmo da evolução da criptografia, o Google tem uma equipe de engenheiros de segurança de classe mundial, encarregados de acompanhar, desenvolver e aprimorar a tecnologia de criptografia. Nossos engenheiros participam de processos de padronização e manutenção de softwares de criptografia amplamente utilizados. [Publicamos regularmente nossa pesquisa](https://www.google.com/about/appsecurity/research/?hl=pt-br) no campo de criptografia para que todos, incluindo o público, possam se beneficiar do nosso conhecimento.

Por exemplo, em uma pesquisa de criptografia pós-quântica, estamos trabalhando nas seguintes áreas:

*   **Padronização**: cocriamos o esquema de assinatura digital baseado em hash sem estado, que é padronizado como [FIPS 205](https://csrc.nist.gov/pubs/fips/205/ipd). Somos editores da norma ISO (Organização Internacional de Padronização) sobre [assinaturas baseadas em hash de criptografia pós-quântica](https://www.iso.org/standard/80492.html) e contribuímos para as [orientações sobre o gerenciamento de estado](https://www.ietf.org/archive/id/draft-wiggers-hbs-state-00.txt) para assinaturas baseadas em hash na IETF.
    
*   **Ativação**: [implementamos a criptografia pós-quântica](https://cloud.google.com/blog/products/identity-security/why-google-now-uses-post-quantum-cryptography-for-internal-comms/?hl=pt-br) em nosso protocolo interno para Transport Layer Security. Ativamos o suporte à criptografia pós-quântica no [Chrome](https://blog.chromium.org/2023/08/protecting-chrome-traffic-with-hybrid.html). Adicionamos vários algoritmos de criptografia pós-quântica à nossa [biblioteca criptográfica Tink](https://github.com/google/tink/tree/master/cc/experimental/pqcrypto). Este código é experimental e foi criado para ajudar a informar a comunidade sobre cada abordagem.
    
*   **Publicações**: publicamos [_Transição de organizações para a criptografia pós-quântica_](https://www.nature.com/articles/s41586-022-04623-2) na _Nature_. Este documento apresenta uma visão geral sobre os desafios da migração de criptografia pós-quântica. Também publicamos um artigo de pesquisa sobre como usar a criptografia pós-quântica em [nossas chaves de segurança.](https://security.googleblog.com/2023/08/toward-quantum-resilient-security-keys.html).
    

Observe que a criptografia simétrica (usando AES-128 ou posterior) permanece resistente a ataques quânticos.

A seguir
--------

*   Para informações sobre os recursos de criptografia pós-quântica do Google, consulte [Criptografia pós-quântica](https://cloud.google.com/security/resources/post-quantum-cryptography?hl=pt-br).
    
*   Para informações sobre o uso das suas próprias chaves de criptografia noGoogle Cloud, consulte [Chaves de criptografia gerenciadas pelo cliente (CMEK)](https://docs.cloud.google.com/kms/docs/cmek?hl=pt-br).
    
*   Para informações gerais sobre a segurança do Google Cloud , consulte a [seção de segurança do site do Google Cloud](https://cloud.google.com/security/?hl=pt-br) .
    
*   Para informações sobre conformidade e os respectivos certificados do Google Cloud , consulte a [seção Compliance no site do Google Cloud](https://cloud.google.com/security/compliance?hl=pt-br) , que inclui o [relatório de auditoria pública de SOC3](https://www.google.com/work/soc3.html?hl=pt-br) do Google.
    
*   Para informações sobre criptografia e gerenciamento de chaves do Google Workspace, consulte [Como o Google Workspace usa criptografia para proteger seus dados](https://storage.googleapis.com/gfw-touched-accounts-pdfs/google-encryption-whitepaper-gsuite.pdf), que aborda grande parte do mesmo conteúdo incluído aqui, mas se concentra apenas no Google Workspace.
