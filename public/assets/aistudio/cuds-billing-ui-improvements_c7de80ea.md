# Melhorias na interface do usuário de faturamento &nbsp;|&nbsp; Cloud Billing &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/cuds-billing-ui-improvements

No Google Cloud console, a interface do usuário do Cloud Billing tem as seguintes atualizações para oferecer suporte ao programa de CUDs:

*   **[Relatório de detalhamento de custos](#cost-breakdown-chart)**: o gráfico de detalhamento de custos agora representa visualmente o impacto líquido do desconto de CUDs com base em gastos, mostrando a economia em verde ou qualquer desperdício de compromisso subutilizado em laranja.
*   **[Filtro de economia](#savings-filter)**: nos **Relatórios** e no relatório da **Tabela de custos** o filtro **Créditos** agora se chama **Economia** e foi reestruturado com subcategorias mais claras para facilitar a análise. O relatório da tabela de custos incorpora colunas adicionais que refletem essas categorias de economia distintas.
*   **[Ferramenta de análise de CUD aprimorada](https://docs.cloud.google.com/billing/docs/how-to/analyze-cuds?hl=pt-br)**: uma nova e melhor página de análise de CUDs está disponível, oferecendo filtragem, agregação de dados e acompanhamento de utilização mais eficientes. Para o modelo de CUD com base em gastos, a ferramenta de análise agora oferece insights sobre padrões de utilização visualizados como gastos equivalentes sob demanda (para análise de economia e uso). A nova ferramenta oferece novos filtros por várias dimensões e agrega compromissos de máquina. Ela também tem um novo gráfico para acompanhar as porcentagens de utilização ao longo do tempo.
*   **[Nomes de exibição de descontos por compromisso de uso (CUDs)](#cud-display-names)**: todos os CUDs com base em gastos agora seguem um padrão de nomenclatura consistente `[Product] CUD` nos descontos por compromisso de uso (CUDs) para maior clareza.
*   **[Orçamentos](#budget-changes)**: os orçamentos acompanham o custo líquido após a aplicação das economias. É necessário revisar os limites de alerta de orçamento atuais, já que os custos rastreados podem ser menores, dependendo dos filtros configurados. Os custos totais rastreados por esses orçamentos podem diminuir à medida que os custos não creditados agora incluem economias quando o novo modelo é aplicado.
*   **[Tabela de preços](#customize-pricing-table)**: a tabela de preços tem novos campos para oferecer suporte a [modelos de consumo](https://docs.cloud.google.com/docs/cuds-multiprice?hl=pt-br#consumption-model-intro).
*   **[Exportações de metadados de CUD para o BigQuery](#manage-data-exports)**: foram adicionadas novas exportações de dados de faturamento para preços e metadados de CUD para o BigQuery.

Conferir suas economias
-----------------------

Na página **Visão geral** da conta de faturamento, o cálculo da economia inclui todos os descontos e créditos qualificados. O link _Ver detalhes_ agora leva ao relatório de **Detalhamento de custos**, mostrado na imagem a seguir.

Para mais informações, consulte [Noções básicas sobre o valor economizado com os relatórios de detalhamento de custos](https://docs.cloud.google.com/billing/docs/how-to/cost-breakdown?hl=pt-br).

### Filtrar suas economias

Nos **Relatórios**, o filtro **Créditos** agora se chama **Economia**, e as subcategorias foram reestruturadas como _Programas de economia_ e _Outras economias_ para facilitar a análise. Os filtros permitem que você veja seus custos antes e depois da aplicação de vários descontos. Ao ativar essas opções, é possível analisar quanto dinheiro você está economizando com cada tipo de desconto, mostrado na imagem a seguir.

Nos relatórios, o filtro **Economia** mostra apenas os tipos de crédito específicos que você incorreu nos Google Cloud custos. Se um tipo específico de crédito não se aplicar à sua conta do Cloud Billing, você não verá essa opção de crédito na lista.

A subcategoria _Programas de economia_ inclui as opções de desconto por compromisso de uso (CUD):

*   **Descontos de CUD com base em gastos**: descontos ganhos com o modelo de preços com base em gastos, mostrando a economia de custos que você tem com seus compromissos.
*   **Créditos de CUD legados com base em gastos**: créditos de descontos por compromisso de uso (CUDs) com base em gastos para SKUs que não fazem parte do modelo de preços com base em gastos ou para créditos de CUDs ganhos antes da entrada em vigor do modelo de preços com base em gastos.
*   **Créditos de CUD com base em recursos**: créditos nos custos de recursos do Compute Engine quando você se compromete a usar os recursos qualificados por um período especificado.

A subcategoria _Outras economias_ inclui créditos da camada sem custo financeiro, créditos promocionais e outros descontos e créditos, como os seguintes:

*   **Descontos por uso prolongado (SUDs)**: descontos ganhos automaticamente quando você executa recursos qualificados do Compute Engine durante o mês de faturamento.
*   **Descontos baseados em gastos**: descontos aplicados após atingir um limite contratual de gastos, normalmente ganhando descontos progressivamente maiores com base no gasto total durante um período definido.
*   **Créditos de assinatura** : esses créditos são aplicados a assinaturas de base + excedente, também conhecidas como assinaturas não UCS (serviço de compromisso não unificado).

Consulte os relatórios do Cloud Billing para [saber mais sobre as economias](https://docs.cloud.google.com/billing/docs/how-to/reports?hl=pt-br#credits).

Novas colunas e filtros no relatório da tabela de custos
--------------------------------------------------------

No relatório da tabela de custos, novas colunas e filtros mostram como o modelo de consumo afeta seus custos e permitem filtrar os dados de economia para maior flexibilidade, conforme mostrado na imagem a seguir.

Usar marcadores e agrupar dados no relatório da tabela de custos
----------------------------------------------------------------

No relatório da tabela de custos, é possível usar as configurações de **Configuração da tabela** para organizar, agregar e adicionar contexto comercial à visualização tabular dos custos de uma determinada fatura ou extrato. Essas configurações permitem selecionar um marcador para categorizar os dados, usar marcadores como uma dimensão de agrupamento e selecionar uma opção de agrupamento, conforme mostrado na imagem a seguir.

A caixa de diálogo de configuração da tabela tem as seguintes opções:

*   **Dados de marcador**: permite selecionar uma das chaves de marcador personalizadas. Ela adiciona uma nova coluna à tabela, mostrando o valor do marcador específico para cada item de custo. Essa é a melhor maneira de sobrepor seu próprio contexto comercial (como equipes, ambientes ou centros de custo) ao relatório.
*   **Agrupar por**: as opções de agrupamento controlam como as linhas da tabela são agregadas e aninhadas. É possível conferir uma lista simples ou criar uma visualização hierárquica e aninhada dos custos. As opções de "Agrupar por" incluem:
    
    *   **Sem agrupamento**: mostra cada item de custo como uma linha separada e individual.
    *   **Projeto > Serviço > SKU > Modelo de consumo**: cria uma hierarquia aninhada começando com o projeto no nível mais alto, permitindo que você veja os custos acumulados do nível mais granular (o modelo de consumo) até o nível mais alto.
    *   **Serviço > SKU > Modelo de consumo**: cria uma hierarquia aninhada começando com o serviço no nível mais alto.
    *   **Agrupamento personalizado**: selecione as dimensões de agrupamento para detalhar os totais dos diferentes tipos de economia ou créditos aplicáveis.

[Saiba mais sobre o relatório da tabela de custos](https://docs.cloud.google.com/billing/docs/how-to/cost-table?hl=pt-br).

Analisar suas economias com o relatório de detalhamento de custos
-----------------------------------------------------------------

Para o período do relatório, o relatório de detalhamento de custos mostra o custo de uso básico e como ele é afetado por créditos, ajustes e tributos até chegar ao custo total. O gráfico de cascata mostra cobranças em laranja, economias em verde e subtotais e totais em azul. Para serviços cobertos pelos novos CUDs com base em gastos, a barra _Descontos de CUD com base em gastos_ mostra o impacto líquido dos seus compromissos com base em gastos, conforme mostrado na imagem a seguir.

O relatório de detalhamento de custos inclui uma barra de economia para cada tipo de crédito _aplicável_. Se um tipo específico de crédito não se aplicar à sua conta do Cloud Billing, você não verá esse tipo de economia no gráfico. Na imagem de exemplo, o gráfico e a tabela de detalhamento de custos mostram os seguintes itens:

*   **Custo de uso**: esse é o ponto de partida: o custo bruto de todos os serviços usados durante o período, antes da aplicação de descontos ou créditos aplicados.
*   **Economia negociada**: essa barra verde representa um crédito para preços especiais ou descontos personalizados negociados no seu contrato.
*   **Descontos de CUD com base em gastos**: mostra o impacto líquido dos seus compromissos com base em gastos. Ele aparece como uma barra de crédito verde que representa suas economias, mas pode aparecer como uma barra de cobrança laranja no caso raro de compromissos subutilizados (desperdício).
*   **Descontos por uso prolongado (SUDs)**: esse crédito representa a economia automática que você recebe por executar recursos qualificados por uma parte significativa do mês, sem necessidade de compromisso.
*   **Descontos baseados em gastos**: esse crédito é para descontos automáticos baseados em volume que você recebe pelo uso intenso de determinados serviços.
*   **Subtotal / Total**: as barras azuis finais mostram o subtotal após a aplicação de todos os créditos, e o total final representa os custos líquidos após a adição de tributos.

[Saiba mais sobre o relatório de detalhamento de custos](https://docs.cloud.google.com/billing/docs/how-to/cost-breakdown?hl=pt-br).

Revisar seus orçamentos após atualizações de economia de custos
---------------------------------------------------------------

Com as atualizações recentes na forma como as economias de custos de CUDs com base em gastos são calculadas, os custos totais rastreados pelos seus orçamentos podem ser menores. As economias de compromisso são incluídas de forma mais consistente no cálculo do orçamento, oferecendo uma visão mais precisa do seu gasto líquido real.

Devido a essa mudança, faça as seguintes perguntas:

*   Devo diminuir os limites de alerta de orçamento para evitar perder mudanças importantes nos gastos?
*   Quais dos meus orçamentos atuais foram afetados e precisam de revisão?
*   Como posso criar um novo orçamento que reflita com precisão meus custos líquidos?

Recomendamos que você acesse o painel de orçamentos para revisar os orçamentos atuais. Como eles agora rastreiam um custo total menor que inclui economias de compromisso, seus limites de alerta originais podem ser muito altos e não acionados quando esperado. Ajustar os limites para baixo garante que você continue recebendo alertas de gastos oportunos com base nos seus custos reais.

Ao criar ou revisar um orçamento, o escopo de **Economia** é a seção principal a ser compreendida, conforme mostrado na imagem a seguir.

*   Essa seção controla quais créditos e descontos são subtraídos dos custos brutos para determinar o "custo total" final que o orçamento rastreia.
*   Como o orçamento rastreia o custo _depois_ que essas economias são aplicadas, o valor rastreado é o custo líquido. É por isso que o custo total rastreado pode ser menor do que antes, exigindo que você revise e possivelmente diminua os limites de alerta para corresponder a esse número mais preciso após a economia.

[Saiba mais sobre como gerenciar orçamentos](https://docs.cloud.google.com/billing/docs/how-to/budgets?hl=pt-br).

Nomes de exibição de descontos por compromisso de uso (CUDs)
------------------------------------------------------------

Para simplificar a forma como os compromissos são exibidos, todos os CUDs com base em gastos usam uma convenção de nomenclatura consistente. Isso ajuda a identificar a qual produto um compromisso específico se aplica diretamente na visualização em lista.

Os nomes de exibição de todas as ofertas de CUD no modelo com base em gastos usam o seguinte padrão de nomenclatura:

*   **Padrão de nomenclatura**: `[Product] CUD` : o nome do produto é listado primeiro, seguido por "CUD".
*   **Exemplos mostrados na captura de tela**:
    *   `AlloyDB for PostgreSQL CUD`
    *   `Cloud Run CUD`
    *   `Compute Flexible CUD`
    *   `Kubernetes Engine CUD`

Campos da tabela de preços para modelos de consumo
--------------------------------------------------

A página "Preços" oferece uma lista abrangente de todas as SKUs disponíveis e os preços associados à sua conta. Há novos campos nessa visualização, por exemplo **Descrição do modelo de consumo**, mostrado na imagem a seguir.

Ao usar o seletor de colunas no lado direito da tabela, você tem controle granular sobre o relatório de preços. Isso permite criar uma visualização adaptada às suas necessidades específicas, seja uma visão geral de alto nível com foco em nomes de produtos ou uma análise detalhada que inclua IDs e taxonomia específicos.

É possível escolher os pontos de dados a serem mostrados, por exemplo:

*   **Identificadores**: `SKU ID` para análise técnica.
*   **Descrições**: `SKU description` e `Consumption model description` para melhor legibilidade.
*   **Classificação**: `Product taxonomy` para entender como uma SKU é categorizada no ecossistema do Google Cloud.
*   **Detalhes de preços**: `Price reason` para entender por que um determinado preço está sendo aplicado (por exemplo, `DEFAULT_PRICE`).

Exportações de preços e metadados de CUD para o BigQuery
--------------------------------------------------------

Para uma análise personalizável dos seus preços e economias, ative as exportações de dados de faturamento para o BigQuery:

*   **Preços** (atualizado): essa exportação contém todas as SKUs da sua conta e os preços delas. É possível combinar esses dados com a exportação de uso detalhado para realizar cálculos ou análises de preços personalizados. [Saiba mais](https://docs.cloud.google.com/billing/docs/how-to/export-data-bigquery-tables/pricing-data?hl=pt-br)
*   **Exportação de descontos por compromisso de uso** (novo): essa exportação fornece todos os metadados relacionados aos seus CUDs _com base em gastos_. Ela permite analisar seu portfólio de compromissos, acompanhar a utilização ao longo do tempo e identificar oportunidades de otimização além do que está disponível nos relatórios padrão do console. [Saiba mais](https://docs.cloud.google.com/billing/docs/how-to/export-data-bigquery-tables/cud-export?hl=pt-br)

Além disso, há novos campos adicionados à **Exportação de dados de preços**. Para mais informações, consulte [Mudanças na exportação de preços](https://docs.cloud.google.com/docs/cuds-multiprice-datamodel?hl=pt-br#price-export-changes).

*   [Melhorias no programa de CUD com base em gastos](https://docs.cloud.google.com/docs/cuds-multiprice?hl=pt-br)
*   [Mudanças no modelo de dados de CUD com base em gastos](https://docs.cloud.google.com/docs/cuds-multiprice-datamodel?hl=pt-br)
*   [Verificar seus descontos após a migração](https://docs.cloud.google.com/docs/cuds-verify-discounts?hl=pt-br)
*   [Escolher a quantidade correta de CUD para comprar](https://docs.cloud.google.com/docs/cuds-choose-correctly?hl=pt-br)
*   [Consultas de amostra para o modelo de dados de CUDs](https://docs.cloud.google.com/docs/cuds-example-queries?hl=pt-br)
*   [SKUs, ofertas e IDs de modelo de consumo de CUD migrados](https://docs.cloud.google.com/docs/cuds-migration?hl=pt-br)
