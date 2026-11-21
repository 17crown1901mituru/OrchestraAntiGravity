# Sample queries for the CUDs data model &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/cuds-example-queries

[Skip to main content](#main-content)

*   [Documentation](https://docs.cloud.google.com/docs)
    *   [Overview](https://docs.cloud.google.com/docs/get-started)
    *   [Guides](https://docs.cloud.google.com/docs/overview)
*   [Console](https://console.cloud.google.com/)

*   Discover
    
*   [Google Cloud overview](https://docs.cloud.google.com/docs/overview)
*   [Google Cloud's approach to change](https://docs.cloud.google.com/docs/cloud-approach-to-change)

*   [Well-Architected Framework](https://docs.cloud.google.com/docs/get-started/well-architected-framework)
*   Get started
    

*   [Authentication basics](https://docs.cloud.google.com/docs/get-started/authentication)
*   [Authorization and access control](https://docs.cloud.google.com/docs/get-started/authorization-access-control)
*   [Compare AWS and Azure services to Google Cloud](https://docs.cloud.google.com/docs/get-started/aws-azure-gcp-service-comparison)
*   Plan resource creation
    
*   [Resource planning overview](https://docs.cloud.google.com/docs/get-started/resource-planning-overview)
*   [Consider geographic distribution](https://docs.cloud.google.com/docs/get-started/consider-geographic-distribution)
*   [Organize resources](https://docs.cloud.google.com/docs/get-started/organize-resources)
*   [Consider maintenance](https://docs.cloud.google.com/docs/get-started/consider-resource-maintenance)
*   Interact with Google Cloud console
    
*   [Accessibility](https://docs.cloud.google.com/docs/accessibility)
*   [Keyboard shortcuts](https://docs.cloud.google.com/docs/shortcuts)
*   [Console appearance](https://docs.cloud.google.com/docs/get-started/console-appearance)
*   [Getting support](https://docs.cloud.google.com/docs/get-started/getting-support)
*   Explore billing and pricing options
    
*   [Learn about Cloud Billing](https://docs.cloud.google.com/docs/get-started/learn-about-billing)
*   [Google Cloud Free Program](https://docs.cloud.google.com/free/docs/free-cloud-features)
*   [Starter Tier](https://docs.cloud.google.com/docs/starter-tier)
*   Committed use discounts
    
    *   [Overview](https://docs.cloud.google.com/docs/cuds)
    *   Spend-based CUDs
        
        *   [Overview](https://docs.cloud.google.com/docs/cuds-multiprice)
        *   [Flexible Savings Plans](https://docs.cloud.google.com/docs/cuds-flexible-savings-plans)
        *   [Spend-based CUD data model changes](https://docs.cloud.google.com/docs/cuds-multiprice-datamodel)
        *   [Structure of CUD subscriptions export](https://docs.cloud.google.com/docs/cuds-subscriptions-export)
        *   [Verify your discounts after migration](https://docs.cloud.google.com/docs/cuds-verify-discounts)
        *   [Choose a commitment to buy](https://docs.cloud.google.com/docs/cuds-choose-correctly)
        *   [Sample queries for CUDs](https://docs.cloud.google.com/docs/cuds-example-queries)
        *   [Migrated CUD SKUs, offers, and consumption model IDs](https://docs.cloud.google.com/docs/cuds-migration)
        *   [Billing user interface improvements](https://docs.cloud.google.com/docs/cuds-billing-ui-improvements)
        
    *   [Attribution of commitments](https://docs.cloud.google.com/docs/cuds-attribution)
    *   [Recommendations](https://docs.cloud.google.com/docs/cuds-recommender)
    

Sample queries for the CUDs data model
--------------------------------------

Queries for CUD KPIs
--------------------

You can use these important KPI metrics to validate that your systems are functioning well with the data model:

1.  **Commitment savings ($)**: Describes the savings that resulted from your commitments. The metric uses the formula `(Cost of resources at on-demand rates - cost of resources with commitment discounts)`.
2.  **Commitment savings (%)**: Describes the savings percentage that resulted from your commitments. The metric uses the formula `(Commitment savings / costs of resources at on-demand rates)*100`.
3.  **Commitment utilization (%)**: Measures how effectively you use your commitments, expressed as a percentage. The metric uses the formula `(Commitment applied to eligible spend / total commitment)`.
4.  **Effective savings rate (%)**: Explains the return on investment (ROI) for commitment discounts. The metric uses the formula `(Commitment Savings / On-Demand Equivalent Spend)`.
    
    To gain better insight into your cost data, the following BigQuery sample queries show how to retrieve useful information for the following KPIs.
    

### Choose the correct sample query

To help you update your queries for the changes to the data model, we provide two versions of the KPI sample queries. Choose one of the following:

*   [Sample KPI queries using the data model](#format-after-opt-in)
*   [Sample KPI queries using the legacy data model](#format-before-opt-in)

### Sample KPI queries using the data model

Use this sample query if you _have_ adopted the data model.

These queries are only for Compute flexible CUDs. To query for other spend-based CUD products, you must change the following values:

*   `cud_fee_skus`
*   `consumption_model.id`

SET bigquery\_billing\_project \= billing\-project\-id;

WITH
  cost\_data AS (
    SELECT \*
    FROM
      project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN
    WHERE invoice.month \= 'month'
  ),
  cud\_fee\_skus AS (
    SELECT \* FROM UNNEST(
      \[
        '5515-81A8-03A2',
        'B22F-51BE-D599'\])
      fee\_sku\_id
  ),
  cud\_costs AS (
    SELECT
      invoice.month AS invoice\_month,
      subscription.instance\_id AS subscription\_instance\_id,
      IFNULL(
        (
          SELECT l.value
          FROM UNNEST(labels) l
          WHERE l.key \= 'goog-originating-service-id'
        ),
        service.id) AS service,
      SUM(cost) AS commitment\_cost,
      SUM(
        (
          SELECT SUM(credit.amount)
          FROM UNNEST(credits) credit
          WHERE credit.type \= 'FEE\_UTILIZATION\_OFFSET'
        )) AS fee\_utilization\_offset
    FROM
      cost\_data
    JOIN cud\_fee\_skus
      ON fee\_sku\_id \= sku.id
    GROUP BY 1, 2, 3
  ),
  cud\_savings AS (
    SELECT
      invoice.month AS invoice\_month,
      subscription.instance\_id AS subscription\_instance\_id,
      service.id AS service,
      SUM(cost \- cost\_at\_effective\_price\_default) AS cud\_savings\_amount,
      SUM(cost\_at\_effective\_price\_default) AS on\_demand\_costs
    FROM
      cost\_data
    WHERE
      consumption\_model.id IS NOT NULL
      AND consumption\_model.id IN ('D97B-0795-975B','70D7-D1AB-12A4')
    GROUP BY 1, 2, 3
  )
SELECT
  invoice\_month,
  subscription\_instance\_id,
  service,
  commitment\_cost,
  commitment\_cost + fee\_utilization\_offset + IFNULL(cud\_savings\_amount, 0)
    AS commitment\_savings,
  ABS(fee\_utilization\_offset) / commitment\_cost \* 100 AS cud\_utilization\_percent,
  (commitment\_cost + fee\_utilization\_offset + IFNULL(cud\_savings\_amount, 0))
    / IFNULL(on\_demand\_costs, 1) \* 100 AS effective\_savings\_rate
FROM cud\_costs
LEFT JOIN cud\_savings
  USING (invoice\_month, subscription\_instance\_id, service);

*   `month` is the current year and month in `YYYYMM` format, for example '202504'.

### Sample KPI queries using the legacy data model

Use these sample queries if you _aren't_ using the data model.

These queries are only for Compute flexible CUDs. To query for other spend-based CUD products, you must change the following values:

*   `cud_product`
*   `sku.description`
*   `credit.type`

#### CUD cost plus CUD savings

WITH
 cost\_data AS (
   SELECT \*
   FROM project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN
   WHERE invoice.month \= 'month'
 ),
 cud\_product\_data AS (
   SELECT \* FROM UNNEST(
     \[
       STRUCT(
         'Compute Engine Flexible CUDs' AS cud\_product,
         'Commitment - dollar based v1: GCE' AS cud\_fee\_regex,
         'GCE Commitments' AS cud\_credit\_regex)\])
 ),
 cud\_costs AS (
   SELECT
     invoice.month AS invoice\_month,
     cud\_product\_data.cud\_product,
     IFNULL(
       (
         SELECT l.value
         FROM UNNEST(labels) l
         WHERE l.key \= 'goog-originating-service-id'
       ),
       service.id) AS service,
     SUM(cost) AS cost
   FROM
     cost\_data
   JOIN cud\_product\_data
     ON
       REGEXP\_CONTAINS(
         sku.description, cud\_fee\_regex)
   GROUP BY 1, 2, 3
 ),
 cud\_credits AS (
   SELECT
     invoice.month AS invoice\_month,
     cud\_product,
     service.id AS service,
     SUM(credit.amount) AS spend\_cud\_credits
   FROM
     cost\_data, UNNEST(credits) AS credit
   JOIN cud\_product\_data
     ON
       REGEXP\_CONTAINS(
         credit.full\_name, cud\_credit\_regex)
   WHERE
     credit.type \= 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE'
   GROUP BY 1, 2, 3
 )
SELECT
 invoice\_month,
 cud\_product,
 cost As commitment\_cost,
 \-1 \* (cost + IFNULL(spend\_cud\_credits, 0)) AS commitment\_savings
FROM cud\_costs
LEFT JOIN cud\_credits
 USING (invoice\_month, cud\_product, service);

*   `month` is the current year and month in `YYYYMM` format, for example '202504'.

#### Commitment utilization

WITH
 cost\_data AS (
   SELECT \*
   FROM project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN
   WHERE invoice.month \= 'month'
 ),
 cud\_product\_data AS (
   SELECT \* FROM UNNEST(
     \[
       STRUCT(
         'Compute Engine Flexible CUDs' AS cud\_product,
         'Commitment - dollar based v1: GCE' AS cud\_fee\_regex,
         'GCE Commitments' AS cud\_credit\_regex)\])
),
 cud\_commitment\_amount AS (
   SELECT
     invoice.month AS invoice\_month,
     cud\_product\_data.cud\_product,
     SUM(usage.amount\_in\_pricing\_units / 100) AS commitment\_amount,
   FROM
     cost\_data
   JOIN cud\_product\_data
     ON
       REGEXP\_CONTAINS(
         sku.description, cud\_fee\_regex)
   GROUP BY 1, 2
 ),
 cud\_utilized\_commitment\_amount AS (
   SELECT
     invoice.month AS invoice\_month,
     cud\_product,
     ABS(SUM(credit.amount / currency\_conversion\_rate))
       AS utilized\_commitment\_amount
   FROM
     cost\_data, UNNEST(credits) AS credit
   JOIN cud\_product\_data
     ON
       REGEXP\_CONTAINS(
         credit.full\_name, cud\_credit\_regex)
   WHERE
     credit.type \= 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE'
   GROUP BY 1, 2
 )
SELECT
 invoice\_month,
 cud\_product,
 utilized\_commitment\_amount / commitment\_amount \*100 AS commitment\_utilization
FROM cud\_commitment\_amount
LEFT JOIN cud\_utilized\_commitment\_amount
 USING (invoice\_month, cud\_product);

*   `month` is the current year and month in `YYYYMM` format, for example '202504'.

#### Effective savings rate

WITH
 cost\_data AS (
   SELECT \*
   FROM project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN
   WHERE invoice.month \= 'month'
 ),
 cud\_product\_data AS (
   SELECT \* FROM UNNEST(
     \[
       STRUCT(
         'Compute Engine Flexible CUDs' AS cud\_product,
         'Commitment - dollar based v1: GCE' AS cud\_fee\_regex,
         'GCE Commitments' AS cud\_credit\_regex)\])
 ),
 eligible\_cud\_skus AS (
   SELECT sku\_id
   FROM example\_project.dataset.flex\_cud\_skus
 ),
 eligible\_cud\_spend AS (
   SELECT
     invoice.month AS invoice\_month,
     SUM(cost) AS cost,
     SUM(
       IFNULL(
         (
           SELECT SUM(credit.amount)
           FROM UNNEST(credits) AS credit
           WHERE
             credit.type IN (
               'COMMITTED\_USAGE\_DISCOUNT',
               'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE',
               'DISCOUNT',
               'FREE\_TIER')
         ),
         0)) AS costs\_ineligible\_for\_cud,
   FROM cost\_data
   JOIN eligible\_cud\_skus
     ON sku.id \= sku\_id
   GROUP BY 1
 ),
 cud\_costs AS (
   SELECT
     invoice.month AS invoice\_month,
     cud\_product\_data.cud\_product,
     IFNULL(
       (
         SELECT l.value
         FROM UNNEST(labels) l
         WHERE l.key \= 'goog-originating-service-id'
       ),
       service.id) AS service,
     SUM(cost) AS cost
   FROM
     cost\_data
   JOIN cud\_product\_data
     ON
       REGEXP\_CONTAINS(
         sku.description, cud\_fee\_regex)
   GROUP BY 1, 2, 3
 ),
 cud\_credits AS (
   SELECT
     invoice.month AS invoice\_month,
     SUM(credit.amount) AS spend\_cud\_credits
   FROM
     cost\_data, UNNEST(credits) AS credit
   WHERE
     credit.type \= 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE'
     AND REGEXP\_CONTAINS(credit.full\_name, 'GCE Commitments')
   GROUP BY 1
 ),
cud\_savings AS (
  SELECT
   invoice\_month,
   Cud\_product,
   spend\_cud\_credits as spend\_cud\_credits,
   \-1 \* (cost + IFNULL(spend\_cud\_credits, 0)) AS commitment\_savings
FROM cud\_costs
LEFT JOIN cud\_credits
 USING (invoice\_month)
)
SELECT
 Invoice\_month,
 commitment\_savings \* 100
   / (cost + costs\_ineligible\_for\_cud \- IFNULL(spend\_cud\_credits, 0))
   AS effective\_savings\_rate
FROM eligible\_cud\_spend
LEFT JOIN cud\_savings
 USING (invoice\_month);

*   `month` is the current year and month in `YYYYMM` format, for example '202504'.

Query and analyze historical Compute flexible CUDs
--------------------------------------------------

The following query lets you analyze your historical CUDs within a single query. It detects your opt-in date and handles data types present in both the old and new CUD models. To use this query, you must already be migrated to the new CUD model.

This query is only for Compute flexible CUDs. To query for other spend-based CUD products, you must change the following values:

*   `cud_product`
*   `sku.description`
*   `Credit.type`
*   `Credit.full_name`
*   `cud_fee_skus`
*   `consumption_model.id`

\-- This query calculates both legacy and new model CUD KPIs, splitting the data by a migration event.
-- The migration event is defined as the first time the consumption\_model.description is not 'Default'.
-- It calculates commitment cost, savings, utilization, and effective savings rate for both models.

WITH
 -- Determine the migration timestamp based on the first usage of a non-default consumption model
 migration\_hour AS (
   SELECT
     MIN(t.usage\_start\_time) AS smallest\_usage\_start\_time
   FROM
     \`project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN\` AS t
   WHERE
     t.consumption\_model.description != 'Default'
 ),
 -- Filter for cost data that occurred before the migration
 legacy\_cost\_data AS (
   SELECT
    \*
   FROM
     \`project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN\`
   WHERE
     usage\_start\_time < (
       SELECT
         smallest\_usage\_start\_time
       FROM
         migration\_hour
     )
 ),
 -- Filter for cost data that occurred at or after the migration
 new\_cost\_data AS (
   SELECT
    \*
   FROM
     \`project.dataset.gcp\_billing\_export\_resource\_v1\_NNNNNN\_NNNNNN\_NNNNNN\`
   WHERE
     usage\_start\_time >= (
       SELECT
         smallest\_usage\_start\_time
       FROM
         migration\_hour
     )
 ),
 -- Define CUD product metadata for matching fees and credits
 cud\_product\_data AS (
   SELECT
    \*
   FROM
     UNNEST(\[ STRUCT( 'Compute Engine Flexible CUDs' AS cud\_product, 'Commitment - dollar based v1: GCE' AS cud\_fee\_regex, 'GCE Commitments' AS cud\_credit\_regex)\])
 ),
 -- =================================================================================================
 -- Part 1: Legacy Model Calculations (before migration)
 -- =================================================================================================
 legacy\_commitment\_costs AS (
   SELECT
     usage\_start\_time,
     pd.cud\_product,
     IFNULL((
       SELECT
         l.value
       FROM
         UNNEST(labels) l
       WHERE
         l.key = 'goog-originating-service-id'
     ), service.id) AS service,
     SUM(cost) AS cost
   FROM
     legacy\_cost\_data
     JOIN cud\_product\_data AS pd ON REGEXP\_CONTAINS(sku.description, pd.cud\_fee\_regex)
   GROUP BY
     1,
     2,
     3
 ),
 legacy\_cud\_credits AS (
   SELECT
     usage\_start\_time,
     pd.cud\_product,
     service.id AS service,
     SUM(credit.amount) AS spend\_cud\_credits
   FROM
     legacy\_cost\_data,
     UNNEST(credits) AS credit
     JOIN cud\_product\_data AS pd ON REGEXP\_CONTAINS(credit.full\_name, pd.cud\_credit\_regex)
   WHERE
     credit.type = 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE'
   GROUP BY
     1,
     2,
     3
 ),
 legacy\_commitment\_savings AS (
   SELECT
     c.usage\_start\_time,
     c.cud\_product,
     c.service,
     SUM(c.cost) AS commitment\_cost,
     SUM(-1 \* (c.cost + IFNULL(cr.spend\_cud\_credits, 0))) AS commitment\_savings
   FROM
     legacy\_commitment\_costs AS c
     LEFT JOIN legacy\_cud\_credits AS cr USING (usage\_start\_time, cud\_product, service)
   GROUP BY
     1,
     2,
     3
 ),
 legacy\_commitment\_amount AS (
   SELECT
     usage\_start\_time,
     pd.cud\_product,
     SUM(usage.amount\_in\_pricing\_units / 100) AS commitment\_amount
   FROM
     legacy\_cost\_data
     JOIN cud\_product\_data AS pd ON REGEXP\_CONTAINS(sku.description, pd.cud\_fee\_regex)
   GROUP BY
     1,
     2
 ),
 legacy\_utilized\_commitment AS (
   SELECT
     usage\_start\_time,
     pd.cud\_product,
     ABS(SUM(credit.amount / currency\_conversion\_rate)) AS utilized\_commitment\_amount
   FROM
     legacy\_cost\_data,
     UNNEST(credits) AS credit
     JOIN cud\_product\_data AS pd ON REGEXP\_CONTAINS(credit.full\_name, pd.cud\_credit\_regex)
   WHERE
     credit.type = 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE'
   GROUP BY
     1,
     2
 ),
 legacy\_cud\_utilization AS (
   SELECT
     ca.usage\_start\_time,
     ca.cud\_product,
     SAFE\_DIVIDE(uc.utilized\_commitment\_amount, ca.commitment\_amount) \* 100 AS cud\_utilization\_percent
   FROM
     legacy\_commitment\_amount AS ca
     LEFT JOIN legacy\_utilized\_commitment AS uc USING (usage\_start\_time, cud\_product)
 ),
 eligible\_cud\_skus AS (
   SELECT
     sku\_id
   FROM
     UNNEST(\[ /\* Insert the full list of CUD eligible SKUs 'F35A-5D39-DA9D', '7E09-0800-D3BA', '1641-654E-D130', 'D616-27D3-51E1'\*/ \]) AS sku\_id
 ),
 eligible\_cud\_spend AS (
   SELECT
     usage\_start\_time,
     SUM(cost) AS cost,
     SUM(IFNULL((
       SELECT
         SUM(credit.amount)
       FROM
         UNNEST(credits) AS credit
       WHERE
         credit.type IN ('COMMITTED\_USAGE\_DISCOUNT', 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE', 'DISCOUNT', 'FREE\_TIER')
     ), 0)) AS costs\_ineligible\_for\_cud
   FROM
     legacy\_cost\_data
     JOIN eligible\_cud\_skus ON sku.id = eligible\_cud\_skus.sku\_id
   GROUP BY
     1
 ),
 total\_cud\_savings AS (
   SELECT
     c.usage\_start\_time,
     -1 \* (c.cost + IFNULL(cr.spend\_cud\_credits, 0)) AS commitment\_savings,
     cr.spend\_cud\_credits
   FROM (
     SELECT
       usage\_start\_time,
       SUM(cost) AS cost
     FROM
       legacy\_cost\_data
       JOIN cud\_product\_data pd ON REGEXP\_CONTAINS(sku.description, pd.cud\_fee\_regex)
     GROUP BY
       1
   ) AS c
     LEFT JOIN (
       SELECT
         usage\_start\_time,
         SUM(credit.amount) AS spend\_cud\_credits
       FROM
         legacy\_cost\_data,
         UNNEST(credits) AS credit
       WHERE
         credit.type = 'COMMITTED\_USAGE\_DISCOUNT\_DOLLAR\_BASE'
         AND REGEXP\_CONTAINS(credit.full\_name, 'GCE Commitments')
       GROUP BY
         1
     ) AS cr USING (usage\_start\_time)
 ),
 -- =================================================================================================
 -- Part 2: New Model Calculations (at or after migration)
 -- =================================================================================================
 new\_model\_commitment\_costs AS (
   SELECT
     usage\_start\_time, -- Changed from invoice.month
     subscription.instance\_id AS subscription\_instance\_id,
     IFNULL((
       SELECT
         l.value
       FROM
         UNNEST(labels) l
       WHERE
         l.key = 'goog-originating-service-id'
     ), service.id) AS service,
     SUM(cost) AS commitment\_cost,
     SUM((
       SELECT
         SUM(credit.amount)
       FROM
         UNNEST(credits) credit
       WHERE
         credit.type = 'FEE\_UTILIZATION\_OFFSET'
     )) AS fee\_utilization\_offset
   FROM
     new\_cost\_data
     JOIN (
       SELECT
        \*
       FROM
         UNNEST(\['5515-81A8-03A2', 'B22F-51BE-D599'\]) fee\_sku\_id
     ) AS cud\_fee\_skus ON fee\_sku\_id = sku.id
   GROUP BY
     1,
     2,
     3
 ),
 new\_model\_cud\_savings AS (
   SELECT
     usage\_start\_time, -- Changed from invoice.month
     subscription.instance\_id AS subscription\_instance\_id,
     service.id AS service,
     SUM(cost - cost\_at\_effective\_price\_default) AS cud\_savings\_amount,
     SUM(cost\_at\_effective\_price\_default) AS on\_demand\_costs
   FROM
     new\_cost\_data
   WHERE
     consumption\_model.id IS NOT NULL
     AND consumption\_model.id IN ('D97B-0795-975B', '70D7-D1AB-12A4')
   GROUP BY
     1,
     2,
     3
 ),
 -- =================================================================================================
 -- Final Combination
 -- =================================================================================================
 legacy\_kpis AS (
   SELECT
     cs.usage\_start\_time,
     'legacy' AS model\_version,
     CAST(NULL AS STRING) AS subscription\_instance\_id,
     cs.cud\_product,
     cs.service,
     cs.commitment\_cost,
     cs.commitment\_savings,
     u.cud\_utilization\_percent,
     NULL AS effective\_savings\_rate
   FROM
     legacy\_commitment\_savings AS cs
     LEFT JOIN legacy\_cud\_utilization AS u USING (usage\_start\_time, cud\_product)
   UNION ALL
   SELECT
     es.usage\_start\_time,
     'legacy' AS model\_version,
     CAST(NULL AS STRING) AS subscription\_instance\_id,
     NULL AS cud\_product,
     NULL AS service,
     NULL AS commitment\_cost,
     NULL AS commitment\_savings,
     NULL AS cud\_utilization\_percent,
     SAFE\_DIVIDE(s.commitment\_savings, (es.cost + es.costs\_ineligible\_for\_cud - IFNULL(s.spend\_cud\_credits, 0))) \* 100 AS effective\_savings\_rate
   FROM
     eligible\_cud\_spend AS es
     LEFT JOIN total\_cud\_savings AS s USING (usage\_start\_time)
 ),
 new\_kpis AS (
   SELECT
     ncc.usage\_start\_time,
     'new' AS model\_version,
     CAST(ncc.subscription\_instance\_id AS STRING) AS subscription\_instance\_id,
     CAST(NULL AS STRING) AS cud\_product,
     ncc.service,
     ncc.commitment\_cost,
     ncc.commitment\_cost + ncc.fee\_utilization\_offset + IFNULL(ncs.cud\_savings\_amount, 0) AS commitment\_savings,
     SAFE\_DIVIDE(ABS(ncc.fee\_utilization\_offset), ncc.commitment\_cost) \* 100 AS cud\_utilization\_percent,
     SAFE\_DIVIDE((ncc.commitment\_cost + ncc.fee\_utilization\_offset + IFNULL(ncs.cud\_savings\_amount, 0)), IFNULL(ncs.on\_demand\_costs, 1)) \* 100 AS effective\_savings\_rate
   FROM
     new\_model\_commitment\_costs AS ncc
     LEFT JOIN new\_model\_cud\_savings AS ncs USING (usage\_start\_time, subscription\_instance\_id, service)
 )
SELECT
\*
FROM
 legacy\_kpis
UNION ALL
SELECT
\*
FROM
 new\_kpis;

*   [Spend-based CUD program improvements](https://docs.cloud.google.com/docs/cuds-multiprice)
*   [Spend-based CUD data model changes](https://docs.cloud.google.com/docs/cuds-multiprice-datamodel)
*   [Verify your discounts after migration](https://docs.cloud.google.com/docs/cuds-verify-discounts)
*   [Choose the correct amount of CUD to buy](https://docs.cloud.google.com/docs/cuds-choose-correctly)
*   [Migrated CUD SKUs, offers, and consumption model IDs](https://docs.cloud.google.com/docs/cuds-migration)
*   [Billing user interface improvements](https://docs.cloud.google.com/docs/cuds-billing-ui-improvements)

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
