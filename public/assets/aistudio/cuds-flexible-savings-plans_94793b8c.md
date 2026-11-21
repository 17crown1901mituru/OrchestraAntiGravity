# Flexible Savings Plans &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/cuds-flexible-savings-plans

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
    

Flexible Savings Plans
----------------------

  

Flexible Savings Plans (FSPs) are a type of spend-based committed use discount (CUD) that help you save money on your usage on Google Cloud. With an FSP, you commit to a minimum spend amount on a collection of eligible products (including first-party, open-source, and participating third-party models) for a monthly entitlement window, for the duration of the subscription term (1 year or 3 years). In exchange for your commitment, you receive discounts on eligible usage.

FSPs introduce two new features to the Spend-Based CUDs:

*   Subscriptions as part of the [eligible SKUs](#find_skus).
*   Monthly [entitlement windows](#entitlement-windows)

Unlike standard spend-based [committed use discounts (CUDs)](https://docs.cloud.google.com/docs/cuds) that use an hourly entitlement window, FSPs use a **monthly** entitlement window. This makes them ideal for workloads with spiky or unpredictable usage patterns.

Find eligible SKUs
------------------

Your Gemini Enterprise FSP applies to eligible Google Cloud generative AI models and product offerings, including first-party models (such as Gemini), open-source Model-as-a-Service offerings, and participating third-party (3P) foundation models available through Vertex AI and Cloud Marketplace. To see the complete list of SKUs in scope, see the [Gemini Enterprise Flexible Savings Plan Eligible SKUs](https://cloud.google.com/skus/sku-groups/gemini-enterprise-flexible-savings-plan-eligible-skus) page.

How Flexible Savings Plans work
-------------------------------

When you purchase a Flexible Savings Plan, you commit to a specific monthly spend amount for a 1-year or 3-year term. The system monitors your usage and applies the [discounted applicable consumption model rates](https://docs.cloud.google.com/docs/cuds-multiprice).

You won't be billed for your FSP if you used your monthly commitment amount on eligible products. Your FSP commitment draws down as you use eligible products.

### Scope and pricing

The following rules apply to FSP scope and pricing:

*   **Subscription term limit**: Subscriptions with a term greater than 1 month can't be discounted by an FSP. For example, if you have a 1-year subscription to a product, the FSP discount doesn't apply to it.
*   **Pay the better rate**: For all eligible products and subscriptions, you always pay the lower (better) rate. If you have a negotiated price for any product within this commitment, the lowest price will apply. An FSP never increases the price of any product beyond its standard on-demand rate. For details, see [Pay the better rate](#pay-the-better-rate).
*   **Discount-ineligible products**: Some products don't get FSP discounts but still draw down from the FSP. For those prices, see the [pricing table report](https://docs.cloud.google.com/billing/docs/how-to/pricing-table).

#### Gemini Enterprise pricing

A 1-year Flexible Savings Plan provides a 10% discount and a 3-year Flexible Savings Plan provides a 20% discount for all SKUs in the [Gemini Enterprise Flexible Savings Plan Eligible SKUs](https://cloud.google.com/skus/sku-groups/gemini-enterprise-flexible-savings-plan-eligible-skus) page, other than the following SKUs which don't get any incremental discounts from the FSP, but still draw down the FSP commitment:

| Product | Description | SKU ID |
| --- | --- | --- |
| Provisioned Throughput | Vertex AI: Provisioned Throughput 3 month | `13D9-6212-6E17` |
| Provisioned Throughput | Vertex AI: Provisioned Throughput 1 Year | `9192-74A6-DE45` |
| Provisioned Throughput | Agent Platform Models: Provisioned Throughput 3 month Non Global | `F878-80F5-63BF` |
| Provisioned Throughput | Agent Platform Models: Provisioned Throughput 1 Year NON GLOBAL | `2106-EA10-F9CC` |
| Provisioned Throughput | Vertex AI Open Source Model as a Service: Provisioned Throughput 3 months | `1E0D-E522-0E12` |
| Provisioned Throughput | Vertex AI Open Source Model as a Service: Provisioned Throughput 1 year | `6009-4CB8-85D6` |
| Full Fine-Tuning | 1P foundational models full fine tuning service 1 year commit duration | `88CB-4A51-012D` |
| Full Fine-Tuning | 1P foundational models full fine tuning service 3 year commit duration | `0280-9537-E40B` |
| Full Fine-Tuning | Inferencing Full Fine Tuned 1P models 1 year commitment | `74E1-4528-1F94` |
| Full Fine-Tuning | Inferencing Full Fine Tuned 1P models 3 year commitment | `26B4-6E81-10C0` |
| Speech-to-Text | Speech-to-Text On-Device Language Subscription | `9886-4062-830D` |
| Speech-to-Text | Speech-to-Text On-Device Serving Library Subscription | `BEF8-431D-9BF4` |
| Text-to-Speech | Text-to-Speech On-Device Serving Library Subscription | `3D6B-450F-BA51` |
| Text-to-Speech | Text-to-Speech On-Device Voice Subscription | `80AF-473F-9A8D` |
| Gemini Enterprise App | Gemini Business: Subscription - one month term | `D03F-9D37-4753` |
| Gemini Enterprise App | Gemini Business: Subscription - one year term | `FE0D-41B2-9575` |
| Gemini Enterprise App | Gemini Enterprise Business Edition: Subscription Prepay - one month term | `FA2D-55B2-023F` |
| Gemini Enterprise App | Gemini Enterprise Business Edition: Subscription Prepay - one year term | `FB43-B23B-BD15` |
| Gemini Enterprise App | Gemini Enterprise Standard: Subscription - one month term | `0532-C2F0-1DF0` |
| Gemini Enterprise App | Gemini Enterprise Standard: Subscription - one year term | `B3D3-B9E1-610F` |
| Gemini Enterprise App | Gemini Enterprise Plus: Subscription - one month term | `4EDF-A125-F89E` |
| Gemini Enterprise App | Gemini Enterprise Plus: Subscription - one year term | `5657-9AD2-26F6` |
| Gemini Enterprise App | Gemini Enterprise Frontline: Subscription - one month term | `C3B7-10E2-45EE` |
| Gemini Enterprise App | Gemini Enterprise Frontline: Subscription - one year term | `070B-47E2-47B3` |
| Gemini Enterprise App | Gemini Enterprise Standard Emerging: Subscription - one month term | `54F6-590C-84B5` |
| Gemini Enterprise App | Gemini Enterprise Standard Emerging: Subscription - one year term | `C853-D67D-D911` |
| Gemini Enterprise App | Gemini Notebook Enterprise: Subscription - one month term | `BF82-0F2F-7FA3` |
| Gemini Enterprise App | Gemini Notebook Enterprise: Subscription - one year term | `8597-66F8-5FF1` |

#### Third-party models and Private Offers

Gemini Enterprise Flexible Savings Plans support participating third-party (3P) foundation models offered through Vertex AI and Cloud Marketplace.

##### Interactions with Private Offers

The following rules govern how FSPs and Private Offers interact:

*   **Benefit application hierarchy**:
    1.  **Private Offer Discounts**: If you purchased an FSP-eligible 3P product through a Private Offer that gives you discounts on the partners list price, the discounted prices and Private Offer terms will apply. Additionally, your discounted spend will draw down your monthly FSP commitment.
    2.  **Private Offer CUD credits**: If you purchased an FSP-eligible 3P product through a Private Offer that gives you credits, the purchase of or installments for the Private Offer will draw down your monthly FSP commitment. Note that your usage of your purchased credits won't additionally draw down your monthly FSP commitment.

##### Enterprise Agreement commitment drawdown

Spend on third-party models under an FSP draws down against your organization's enterprise commitment, subject to the standard Cloud Marketplace drawdown rules and contract caps defined in your enterprise agreement.

### Entitlement windows

The entitlement window is the recurring period over which your usage is measured against your commitment amount. If you don't use your entire commitment amount within the entitlement window, you will billed for the remaining amount. Underutilized FSP commitments don't carry forward.

FSPs support a **monthly** entitlement window. This means you have the calendar month to consume your committed spend. However, the billing system evaluates your usage and applies the FSP discounts on an hourly basis.

For example, if you have a $100 monthly commitment, the system checks your usage every hour. If you have eligible usage in hour 1, the discount is applied and counts toward your $100 commitment. This continues throughout the month until you reach your $100 limit or the month ends. If you exceed $100 of usage before the end of the month, subsequent usage is charged at the default consumption model rate.

### Overutilized commitments (exceeding your commitment)

If your eligible usage in a given month exceeds the monthly amount you committed to, the FSP covers usage up to your limit, and the remainder is billed at either the standard on-demand rate or your negotiated price for any product within this commitment.

*   **Mechanism**: The system applies the FSP discounted rate to the usage equal to your monthly commitment amount.
*   **Cost**: Any usage _beyond_ that limit in the month is charged at either the standard **on-demand price** (the "Default" consumption model) or your negotiated price for any product within the commitment. You don't receive any FSP discount on the overage.

### Underutilized commitments (not meeting your commitment)

If your eligible usage in a given month is less than your committed amount, you are still responsible for the full cost of the commitment.

*   **No rollover**: Unused commitment does _NOT_ carry over to the next month.
*   **Cost**: You pay for the full monthly commitment amount you purchased. You are charged a commitment fee for the unused portion of the commitment at the end of the month.

### How discounts are applied

Within an FSP's scope, discounts are applied in the following order:

1.  **Usage time**: Costs in an earlier usage hour are applied first.
2.  **Discount level**: Usage with the steepest discount percentage relative to the on-demand list price within a given usage hour is applied next. This greedy selection ensures that your commitment dollars deliver the highest savings rate first.
3.  **Proportional application**: If usage across multiple resources shares the same discount percentage within an hour, the commitment is applied proportionally to all costs within that discount level and usage hour.

If you have a subscription that is covered by an FSP, the subscription benefits apply to your usage first. Your subscription cost counts toward meeting your FSP spend commitment, but the FSP doesn't apply an additional discount to the subscription price.

#### Pay the better rate

The lower of the default consumption model price, 3P Private Offers, and the FSP consumption model price is applied.

### Start times and proration

After you purchase an FSP, the commitment activates based on the time of purchase:

*   **Purchased at :00 to :49 (first 50 minutes of the hour)**: Starts at the beginning of the next hour.
*   **Purchased at :50 to :59 (last 10 minutes of the hour)**: Skips an hour and starts at the beginning of the hour after next.

All activation and entitlement window boundaries operate in **Pacific Time (PT)** (`America/Los_Angeles`).

#### Monthly proration for mid-month purchases

When you purchase an FSP mid-month with immediate activation, your initial entitlement period is prorated for the remainder of that calendar month:

1.  **Prorated initial month**: You receive a prorated spend entitlement for the period from your activation timestamp through the end of the current calendar month (11:59:59 PM PT on the last day of the month). Your commitment fee and underutilization threshold for that partial month are scaled proportionally to the fraction of elapsed hours in the month.
2.  **Subsequent calendar months**: Starting on the first day of the next calendar month at 12:00 AM PT, each entitlement period aligns with a full calendar month for the remainder of your 1-year or 3-year term
3.  **Prorated last month**: You receive a prorated spend entitlement for the period from the start of the current calendar month to the date your term ends. Your commitment fee and underutilization threshold for that partial month are scaled proportionally to the fraction of elapsed hours in the month.

For example, suppose you purchase a 1-year FSP with a $3,000 monthly commitment that activates at 12:00 AM PT on April 16 (where April has 30 days total, leaving 15 days remaining). Your commitment for April is prorated to $1,500 ($3,000 × 15/30).

### Cancellations, migrations, and corrections

After purchase, you **cannot cancel** or modify an FSP. You must pay the agreed-upon monthly amount for the duration of the commitment. If you accidentally purchased a commitment or made a mistake configuring your commitment, contact [Cloud Billing support](https://docs.cloud.google.com/billing/docs/support) for help.

### Attribution of discounts and fees

FSPs use proportional attribution, which is the same attribution model used for other spend-based committed use discounts (CUDs). Costs are attributed every hour. Fees are applied to the projects in your Cloud Billing account in proportion to the amount of total eligible usage consumed by each project. For details, see [Attribution of committed use discount fees and credits](https://docs.cloud.google.com/docs/cuds-attribution).

Purchase a Flexible Savings Plan
--------------------------------

You can purchase FSPs directly [using the Google Cloud console (self-service)](https://docs.cloud.google.com/docs/cuds-multiprice#steps_to_purchase).

To purchase a Flexible Savings Plan, you need the offer ID associated with the commitment term. To find offer IDs, see [Identify the Offer you want to purchase](https://docs.cloud.google.com/marketplace/docs/commitment-api-purchasing).

When configuring your FSP, you specify:

*   The **subscription term** (1 year or 3 years).
*   The **commitment amount** per monthly entitlement window.
*   A **name** for your FSP. This name must be unique across all your FSPs and committed use discounts (CUDs).

View your Flexible Savings Plans
--------------------------------

You can view your active and historical Flexible Savings Plans on the [**Committed use discounts (CUDs)** page](https://console.cloud.google.com/billing/reports/commitments) in the Google Cloud console.

The page provides two levels of detail (**Aggregated view** and **Instance view**) to help you manage your commitments. For more information about these views, see [View your commitments](https://docs.cloud.google.com/billing/docs/how-to/cuds-list-overview).

From the **Instance view**, you can see the breakdown of entitlement windows.

Monitor and analyze your Flexible Savings Plans
-----------------------------------------------

You can monitor and manage your FSPs using standard Cloud Billing FinOps tools:

*   **Cost reports**: View your FSP commitment fees and on-demand costs. You can isolate costs for fees and usage covered by your FSPs.
*   **Cost table report**: Reconcile your invoice by viewing the detailed breakdown of your FSP charges.
*   **Cloud Billing export to BigQuery**: [Set up Cloud Billing data export to BigQuery](https://docs.cloud.google.com/billing/docs/how-to/export-data-bigquery-setup) to programmatically track your active FSPs, historical performance, and utilization metrics. You can use the [sample queries](#sample-queries) to analyze this data.

Sample queries
--------------

Use the following sample queries to analyze your Flexible Savings Plans (FSPs) by using your Cloud Billing export to BigQuery data.

### Calculate commitment savings

Use the following queries to calculate your commitment savings in dollars and as a percentage for open and closed entitlement windows.

#### Open window

This query calculates gross savings and the gross savings rate for the entitlement window that is open: on-demand cost minus the FSP-discounted cost of the usage that the FSP covered. Underutilization fees aren't included because they're charged only after the window closes. To filter for specific consumption models, replace `'%'` in `LIKE ANY ('%')` with the target consumption model IDs.

WITH
  fsp\_windows AS (
    SELECT DISTINCT
      IF(
        STARTS\_WITH(subscription.instance\_id, 'subscriptions/'),
        subscription.instance\_id,
        CONCAT('subscriptions/', subscription.instance\_id)) AS fsp\_instance\_id,
      subscription.display\_name AS fsp\_display\_name,
      subscription.entitlement\_period,
      consumption\_model.id AS consumption\_model\_id,
      consumption\_model.description AS consumption\_model\_description,
      entitlement\_window.start\_time AS window\_start\_time,
      entitlement\_window.end\_time AS window\_end\_time
    FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`,
      UNNEST(entitlement\_schedule) AS entitlement\_window
    WHERE
      DATE(\_PARTITIONTIME) \= (
        SELECT MAX(DATE(\_PARTITIONTIME)) FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`)
      AND consumption\_model.id LIKE ANY ('%')
      AND CURRENT\_TIMESTAMP() \>= entitlement\_window.start\_time
      AND CURRENT\_TIMESTAMP() < entitlement\_window.end\_time
  ),
  fsp\_cost AS (
    SELECT
      COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) AS fsp\_instance\_id,
      IFNULL(consumption\_model.applied\_subscription\_instance\_id, '') \= '' AS is\_fee\_line,
      usage\_start\_time,
      currency,
      SUM(CAST(cost AS NUMERIC)) AS cost,
      SUM(CAST(cost\_at\_effective\_price\_default AS NUMERIC)) AS cost\_at\_on\_demand\_price
    FROM \`PROJECT.DATASET.gcp\_billing\_export\_resource\_v1\_BILLING\_ACCOUNT\_ID\`
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE\_SUB(CURRENT\_DATE('America/Los\_Angeles'), INTERVAL 93 DAY)
      AND COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) IN (SELECT fsp\_instance\_id FROM fsp\_windows)
    GROUP BY ALL
  ),
  fsp\_window\_totals AS (
    SELECT
      fsp\_windows.fsp\_instance\_id,
      fsp\_windows.fsp\_display\_name,
      fsp\_windows.entitlement\_period,
      fsp\_windows.consumption\_model\_id,
      fsp\_windows.consumption\_model\_description,
      fsp\_windows.window\_start\_time,
      fsp\_windows.window\_end\_time,
      fsp\_cost.currency,
      IFNULL(SUM(IF(fsp\_cost.is\_fee\_line, 0, fsp\_cost.cost\_at\_on\_demand\_price)), 0)
        AS on\_demand\_cost,
      IFNULL(SUM(IF(fsp\_cost.is\_fee\_line, 0, fsp\_cost.cost)), 0) AS usage\_cost
    FROM fsp\_windows
    LEFT JOIN fsp\_cost
      ON
        fsp\_cost.fsp\_instance\_id \= fsp\_windows.fsp\_instance\_id
        AND fsp\_cost.usage\_start\_time \>= fsp\_windows.window\_start\_time
        AND fsp\_cost.usage\_start\_time < fsp\_windows.window\_end\_time
    GROUP BY ALL
  )
SELECT
  fsp\_instance\_id,
  fsp\_display\_name,
  entitlement\_period,
  consumption\_model\_id,
  consumption\_model\_description,
  DATETIME(window\_start\_time, 'America/Los\_Angeles') AS window\_start\_time\_pt,
  DATETIME(window\_end\_time, 'America/Los\_Angeles') AS window\_end\_time\_pt,
  currency,
  on\_demand\_cost \- usage\_cost AS gross\_commitment\_savings,
  SAFE\_DIVIDE(on\_demand\_cost \- usage\_cost, on\_demand\_cost) AS gross\_commitment\_savings\_rate
FROM fsp\_window\_totals
ORDER BY fsp\_instance\_id, window\_start\_time\_pt

Replace the following:

*   `PROJECT`: The ID of the Google Cloud project that contains your Cloud Billing export dataset.
*   `DATASET`: The name of the BigQuery dataset containing your Cloud Billing export tables.
*   `BILLING_ACCOUNT_ID`: Your Cloud Billing Account API ID, with hyphens replaced by underscores.

#### Closed window

This query calculates net commitment savings and the net savings rate for completed entitlement windows after underutilization fees (`FEE_UTILIZATION_OFFSET`) are applied. In `fsp_windows` and `fsp_cost`, update `DATE '2026-07-01'` and `DATE '2026-10-01'` to match your target window range, and replace `'%'` in `LIKE ANY ('%')` if you want to filter for specific consumption models.

WITH
  fsp\_windows AS (
    SELECT DISTINCT
      IF(
        STARTS\_WITH(subscription.instance\_id, 'subscriptions/'),
        subscription.instance\_id,
        CONCAT('subscriptions/', subscription.instance\_id)) AS fsp\_instance\_id,
      subscription.display\_name AS fsp\_display\_name,
      subscription.entitlement\_period,
      consumption\_model.id AS consumption\_model\_id,
      consumption\_model.description AS consumption\_model\_description,
      entitlement\_window.start\_time AS window\_start\_time,
      entitlement\_window.end\_time AS window\_end\_time
    FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`,
      UNNEST(entitlement\_schedule) AS entitlement\_window
    WHERE
      DATE(\_PARTITIONTIME) \= (
        SELECT MAX(DATE(\_PARTITIONTIME)) FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`)
      AND consumption\_model.id LIKE ANY ('%')
      AND entitlement\_window.start\_time \>= TIMESTAMP(DATE '2026-07-01', 'America/Los\_Angeles')
      AND entitlement\_window.end\_time <= TIMESTAMP(DATE '2026-10-01', 'America/Los\_Angeles')
      AND entitlement\_window.end\_time <= CURRENT\_TIMESTAMP()
  ),
  fsp\_cost AS (
    SELECT
      COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) AS fsp\_instance\_id,
      IFNULL(consumption\_model.applied\_subscription\_instance\_id, '') \= '' AS is\_fee\_line,
      usage\_start\_time,
      currency,
      SUM(CAST(cost AS NUMERIC)) AS cost,
      SUM(CAST(cost\_at\_effective\_price\_default AS NUMERIC)) AS cost\_at\_on\_demand\_price,
      SUM(
        (
          SELECT SUM(CAST(credit.amount AS NUMERIC))
          FROM UNNEST(credits) AS credit
          WHERE credit.type \= 'FEE\_UTILIZATION\_OFFSET'
        )) AS fee\_utilization\_offset
    FROM \`PROJECT.DATASET.gcp\_billing\_export\_resource\_v1\_BILLING\_ACCOUNT\_ID\`
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE '2026-07-01'
      AND COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) IN (SELECT fsp\_instance\_id FROM fsp\_windows)
    GROUP BY ALL
  ),
  fsp\_window\_totals AS (
    SELECT
      fsp\_windows.fsp\_instance\_id,
      fsp\_windows.fsp\_display\_name,
      fsp\_windows.entitlement\_period,
      fsp\_windows.consumption\_model\_id,
      fsp\_windows.consumption\_model\_description,
      fsp\_windows.window\_start\_time,
      fsp\_windows.window\_end\_time,
      fsp\_cost.currency,
      IFNULL(SUM(IF(fsp\_cost.is\_fee\_line, 0, fsp\_cost.cost\_at\_on\_demand\_price)), 0)
        AS on\_demand\_cost,
      IFNULL(SUM(fsp\_cost.cost + IFNULL(fsp\_cost.fee\_utilization\_offset, 0)), 0) AS actual\_fsp\_cost
    FROM fsp\_windows
    LEFT JOIN fsp\_cost
      ON
        fsp\_cost.fsp\_instance\_id \= fsp\_windows.fsp\_instance\_id
        AND fsp\_cost.usage\_start\_time \>= fsp\_windows.window\_start\_time
        AND fsp\_cost.usage\_start\_time < fsp\_windows.window\_end\_time
    GROUP BY ALL
  )
SELECT
  fsp\_instance\_id,
  fsp\_display\_name,
  entitlement\_period,
  consumption\_model\_id,
  consumption\_model\_description,
  DATETIME(window\_start\_time, 'America/Los\_Angeles') AS window\_start\_time\_pt,
  DATETIME(window\_end\_time, 'America/Los\_Angeles') AS window\_end\_time\_pt,
  currency,
  on\_demand\_cost \- actual\_fsp\_cost AS net\_commitment\_savings,
  SAFE\_DIVIDE(on\_demand\_cost \- actual\_fsp\_cost, on\_demand\_cost) AS net\_commitment\_savings\_rate
FROM fsp\_window\_totals
ORDER BY fsp\_instance\_id, window\_start\_time\_pt

Replace the following:

*   `PROJECT`: The ID of the Google Cloud project that contains your Cloud Billing export dataset.
*   `DATASET`: The name of the BigQuery dataset containing your Cloud Billing export tables.
*   `BILLING_ACCOUNT_ID`: Your Cloud Billing Account API ID, with hyphens replaced by underscores.

### Calculate commitment utilization

Commitment utilization measures how effectively you use your commitments.

#### Open window

This query calculates in-progress utilization for the entitlement window that is open: FSP-covered usage so far at FSP prices in USD divided by the commitment amount for the window. To filter for specific consumption models, replace `'%'` in `LIKE ANY ('%')` with the target consumption model IDs.

WITH
  fsp\_windows AS (
    SELECT DISTINCT
      IF(
        STARTS\_WITH(subscription.instance\_id, 'subscriptions/'),
        subscription.instance\_id,
        CONCAT('subscriptions/', subscription.instance\_id)) AS fsp\_instance\_id,
      subscription.display\_name AS fsp\_display\_name,
      subscription.entitlement\_period,
      consumption\_model.id AS consumption\_model\_id,
      consumption\_model.description AS consumption\_model\_description,
      entitlement\_window.start\_time AS window\_start\_time,
      entitlement\_window.end\_time AS window\_end\_time,
      entitlement\_window.commitment\_amount AS window\_commitment\_amount\_usd
    FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`,
      UNNEST(entitlement\_schedule) AS entitlement\_window
    WHERE
      DATE(\_PARTITIONTIME) \= (
        SELECT MAX(DATE(\_PARTITIONTIME)) FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`)
      AND consumption\_model.id LIKE ANY ('%')
      AND CURRENT\_TIMESTAMP() \>= entitlement\_window.start\_time
      AND CURRENT\_TIMESTAMP() < entitlement\_window.end\_time
  ),
  fsp\_cost AS (
    SELECT
      COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) AS fsp\_instance\_id,
      IFNULL(consumption\_model.applied\_subscription\_instance\_id, '') \= '' AS is\_fee\_line,
      usage\_start\_time,
      SUM(CAST(cost AS NUMERIC) / CAST(currency\_conversion\_rate AS NUMERIC)) AS cost\_usd
    FROM \`PROJECT.DATASET.gcp\_billing\_export\_resource\_v1\_BILLING\_ACCOUNT\_ID\`
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE\_SUB(CURRENT\_DATE('America/Los\_Angeles'), INTERVAL 93 DAY)
      AND COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) IN (SELECT fsp\_instance\_id FROM fsp\_windows)
    GROUP BY ALL
  ),
  fsp\_window\_totals AS (
    SELECT
      fsp\_windows.fsp\_instance\_id,
      fsp\_windows.fsp\_display\_name,
      fsp\_windows.entitlement\_period,
      fsp\_windows.consumption\_model\_id,
      fsp\_windows.consumption\_model\_description,
      fsp\_windows.window\_start\_time,
      fsp\_windows.window\_end\_time,
      fsp\_windows.window\_commitment\_amount\_usd,
      IFNULL(SUM(IF(fsp\_cost.is\_fee\_line, 0, fsp\_cost.cost\_usd)), 0) AS usage\_cost\_usd
    FROM fsp\_windows
    LEFT JOIN fsp\_cost
      ON
        fsp\_cost.fsp\_instance\_id \= fsp\_windows.fsp\_instance\_id
        AND fsp\_cost.usage\_start\_time \>= fsp\_windows.window\_start\_time
        AND fsp\_cost.usage\_start\_time < fsp\_windows.window\_end\_time
    GROUP BY ALL
  )
SELECT
  fsp\_instance\_id,
  fsp\_display\_name,
  entitlement\_period,
  consumption\_model\_id,
  consumption\_model\_description,
  DATETIME(window\_start\_time, 'America/Los\_Angeles') AS window\_start\_time\_pt,
  DATETIME(window\_end\_time, 'America/Los\_Angeles') AS window\_end\_time\_pt,
  window\_commitment\_amount\_usd,
  usage\_cost\_usd AS covered\_usage\_cost\_usd,
  SAFE\_DIVIDE(usage\_cost\_usd, window\_commitment\_amount\_usd) AS commitment\_utilization\_rate
FROM fsp\_window\_totals
ORDER BY fsp\_instance\_id, window\_start\_time\_pt

Replace the following:

*   `PROJECT`: The ID of the Google Cloud project that contains your Cloud Billing export dataset.
*   `DATASET`: The name of the BigQuery dataset containing your Cloud Billing export tables.
*   `BILLING_ACCOUNT_ID`: Your Cloud Billing Account API ID, with hyphens replaced by underscores.

#### Closed window

This query calculates final utilization for entitlement windows that have ended from the FSP fee line item: the `FEE_UTILIZATION_OFFSET` credit, which represents the utilized part of the commitment, divided by the commitment amount for the window. In `fsp_windows` and `fsp_cost`, update `DATE '2026-07-01'` and `DATE '2026-10-01'` to match your target window range, and replace `'%'` in `LIKE ANY ('%')` if you want to filter for specific consumption models.

WITH
  fsp\_windows AS (
    SELECT DISTINCT
      IF(
        STARTS\_WITH(subscription.instance\_id, 'subscriptions/'),
        subscription.instance\_id,
        CONCAT('subscriptions/', subscription.instance\_id)) AS fsp\_instance\_id,
      subscription.display\_name AS fsp\_display\_name,
      subscription.entitlement\_period,
      consumption\_model.id AS consumption\_model\_id,
      consumption\_model.description AS consumption\_model\_description,
      entitlement\_window.start\_time AS window\_start\_time,
      entitlement\_window.end\_time AS window\_end\_time,
      entitlement\_window.commitment\_amount AS window\_commitment\_amount\_usd
    FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`,
      UNNEST(entitlement\_schedule) AS entitlement\_window
    WHERE
      DATE(\_PARTITIONTIME) \= (
        SELECT MAX(DATE(\_PARTITIONTIME)) FROM \`PROJECT.DATASET.cud\_subscriptions\_export\`)
      AND consumption\_model.id LIKE ANY ('%')
      AND entitlement\_window.start\_time \>= TIMESTAMP(DATE '2026-07-01', 'America/Los\_Angeles')
      AND entitlement\_window.end\_time <= TIMESTAMP(DATE '2026-10-01', 'America/Los\_Angeles')
      AND entitlement\_window.end\_time <= CURRENT\_TIMESTAMP()
  ),
  fsp\_cost AS (
    SELECT
      COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) AS fsp\_instance\_id,
      usage\_start\_time,
      SUM(
        (
          SELECT SUM(CAST(credit.amount AS NUMERIC))
          FROM UNNEST(credits) AS credit
          WHERE credit.type \= 'FEE\_UTILIZATION\_OFFSET'
        )
        / CAST(currency\_conversion\_rate AS NUMERIC)) AS fee\_utilization\_offset\_usd
    FROM \`PROJECT.DATASET.gcp\_billing\_export\_resource\_v1\_BILLING\_ACCOUNT\_ID\`
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE '2026-07-01'
      AND COALESCE(
        NULLIF(consumption\_model.applied\_subscription\_instance\_id, ''),
        subscription.instance\_id) IN (SELECT fsp\_instance\_id FROM fsp\_windows)
    GROUP BY ALL
  ),
  fsp\_window\_totals AS (
    SELECT
      fsp\_windows.fsp\_instance\_id,
      fsp\_windows.fsp\_display\_name,
      fsp\_windows.entitlement\_period,
      fsp\_windows.consumption\_model\_id,
      fsp\_windows.consumption\_model\_description,
      fsp\_windows.window\_start\_time,
      fsp\_windows.window\_end\_time,
      fsp\_windows.window\_commitment\_amount\_usd,
      IFNULL(SUM(fsp\_cost.fee\_utilization\_offset\_usd), 0) AS fee\_utilization\_offset\_usd
    FROM fsp\_windows
    LEFT JOIN fsp\_cost
      ON
        fsp\_cost.fsp\_instance\_id \= fsp\_windows.fsp\_instance\_id
        AND fsp\_cost.usage\_start\_time \>= fsp\_windows.window\_start\_time
        AND fsp\_cost.usage\_start\_time < fsp\_windows.window\_end\_time
    GROUP BY ALL
  )
SELECT
  fsp\_instance\_id,
  fsp\_display\_name,
  entitlement\_period,
  consumption\_model\_id,
  consumption\_model\_description,
  DATETIME(window\_start\_time, 'America/Los\_Angeles') AS window\_start\_time\_pt,
  DATETIME(window\_end\_time, 'America/Los\_Angeles') AS window\_end\_time\_pt,
  window\_commitment\_amount\_usd,
  ABS(fee\_utilization\_offset\_usd) AS utilized\_commitment\_usd,
  SAFE\_DIVIDE(ABS(fee\_utilization\_offset\_usd), window\_commitment\_amount\_usd)
    AS commitment\_utilization\_rate
FROM fsp\_window\_totals
ORDER BY fsp\_instance\_id, window\_start\_time\_pt

Replace the following:

*   `PROJECT`: The ID of the Google Cloud project that contains your Cloud Billing export dataset.
*   `DATASET`: The name of the BigQuery dataset containing your Cloud Billing export tables.
*   `BILLING_ACCOUNT_ID`: Your Cloud Billing Account API ID, with hyphens replaced by underscores.

### Size your purchase amount

Use closed, completed periods only to size your purchase amount. Open periods usually contain little uncovered cost and can skew the result. The sizing query evaluates historical on-demand usage that no active commitment covered during those periods. Because historical usage doesn't reflect scheduled commitment ramps, future activations, or expiring commitments, factor in your upcoming entitlement schedules when sizing a purchase.

#### Find eligible SKUs and discount rates

This query returns FSP-eligible SKUs, their on-demand and FSP prices per pricing tier, and the FSP discount rate so that you can validate the prices that the next query applies. SKUs with a 0% discount, such as Provisioned Throughput, are kept because they still draw down the commitment. Every consumption model whose name contains "Flexible Savings Plan" is included. To include only specific consumption models, replace `'%'` in `LIKE ANY ('%')` with their IDs, such as only the quarterly 1-year model.

WITH
  fsp\_tier\_prices AS (
    SELECT
      pricing.sku.id AS sku\_id,
      pricing.sku.description AS sku\_description,
      consumption\_model\_price.consumption\_model\_id,
      consumption\_model\_price.consumption\_model\_display\_name,
      on\_demand\_tier.start\_usage\_amount AS tier\_start\_usage\_amount,
      CAST(
        COALESCE(
          (
            SELECT tier.usd\_amount
            FROM UNNEST(pricing.billing\_account\_price.tiered\_rates) AS tier
            WHERE tier.start\_usage\_amount \= on\_demand\_tier.start\_usage\_amount
          ),
          on\_demand\_tier.usd\_amount) AS NUMERIC) AS on\_demand\_price\_usd,
      CAST(
        COALESCE(
          (
            SELECT tier.usd\_amount
            FROM UNNEST(consumption\_model\_price.billing\_account\_price.tiered\_rates) AS tier
            WHERE tier.start\_usage\_amount \= on\_demand\_tier.start\_usage\_amount
          ),
          (
            SELECT tier.usd\_amount
            FROM UNNEST(consumption\_model\_price.list\_price.tiered\_rates) AS tier
            WHERE tier.start\_usage\_amount \= on\_demand\_tier.start\_usage\_amount
          )) AS NUMERIC) AS fsp\_price\_usd
    FROM \`PROJECT.DATASET.cloud\_pricing\_export\` AS pricing,
      UNNEST(pricing.consumption\_model\_prices) AS consumption\_model\_price,
      UNNEST(pricing.list\_price.tiered\_rates) AS on\_demand\_tier
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE\_SUB(CURRENT\_DATE(), INTERVAL 7 DAY)
      AND DATE(\_PARTITIONTIME) \= (
        SELECT MAX(DATE(\_PARTITIONTIME))
        FROM \`PROJECT.DATASET.cloud\_pricing\_export\`
        WHERE DATE(\_PARTITIONTIME) \>= DATE\_SUB(CURRENT\_DATE(), INTERVAL 7 DAY)
      )
      AND consumption\_model\_price.consumption\_model\_display\_name LIKE '%Flexible Savings Plan%'
      AND consumption\_model\_price.consumption\_model\_id LIKE ANY ('%')
  )
SELECT
  sku\_id,
  sku\_description,
  consumption\_model\_id,
  consumption\_model\_display\_name,
  tier\_start\_usage\_amount,
  on\_demand\_price\_usd,
  fsp\_price\_usd,
  SAFE\_DIVIDE(on\_demand\_price\_usd \- fsp\_price\_usd, on\_demand\_price\_usd) AS fsp\_discount\_rate
FROM fsp\_tier\_prices
ORDER BY consumption\_model\_display\_name, sku\_id, tier\_start\_usage\_amount

Replace the following:

*   `PROJECT`: The ID of the Google Cloud project that contains your Cloud Billing export dataset.
*   `DATASET`: The name of the BigQuery dataset containing your Cloud Billing export tables.

#### Apply discount rates to uncovered usage

This query applies each SKU's FSP price ratio per pricing tier, using the same pricing logic as the preceding query, to uncovered on-demand usage in each past period. It returns the potential FSP cost per period and a suggested commitment per window based on the lowest stable period, also called the low watermark. Running the preceding query isn't a prerequisite because the pricing step is built in.

Before running the query, edit the following inputs in place in `sizing_periods` and `uncovered_cost`:

*   **Consumption models**: In `fsp_tier_price_ratios`, choose consumption models in `LIKE ANY ('%')` that match the period length, such as the quarterly FSP consumption models with 3-month periods.
*   **Period length**: In `sizing_periods`, use `1` for monthly FSPs and `3` for quarterly FSPs.
*   **First period start**: Update `DATE '2026-07-01'`, which appears twice in `sizing_periods` and in the `_PARTITIONTIME` filter of `uncovered_cost`. Keep both dates the same.
*   **Number of periods**: Update the `3` in `GENERATE_ARRAY(0, 3)`, such as `3` monthly periods or `2` to `4` quarterly periods. All periods must have ended.

WITH
  fsp\_tier\_price\_ratios AS (
    SELECT
      pricing.sku.id AS sku\_id,
      consumption\_model\_price.consumption\_model\_id,
      consumption\_model\_price.consumption\_model\_display\_name,
      on\_demand\_tier.start\_usage\_amount AS tier\_start\_usage\_amount,
      SAFE\_DIVIDE(
        CAST(
          COALESCE(
            (
              SELECT tier.usd\_amount
              FROM UNNEST(consumption\_model\_price.billing\_account\_price.tiered\_rates) AS tier
              WHERE tier.start\_usage\_amount \= on\_demand\_tier.start\_usage\_amount
            ),
            (
              SELECT tier.usd\_amount
              FROM UNNEST(consumption\_model\_price.list\_price.tiered\_rates) AS tier
              WHERE tier.start\_usage\_amount \= on\_demand\_tier.start\_usage\_amount
            )) AS NUMERIC),
        CAST(
          COALESCE(
            (
              SELECT tier.usd\_amount
              FROM UNNEST(pricing.billing\_account\_price.tiered\_rates) AS tier
              WHERE tier.start\_usage\_amount \= on\_demand\_tier.start\_usage\_amount
            ),
            on\_demand\_tier.usd\_amount) AS NUMERIC)) AS fsp\_price\_ratio
    FROM \`PROJECT.DATASET.cloud\_pricing\_export\` AS pricing,
      UNNEST(pricing.consumption\_model\_prices) AS consumption\_model\_price,
      UNNEST(pricing.list\_price.tiered\_rates) AS on\_demand\_tier
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE\_SUB(CURRENT\_DATE(), INTERVAL 7 DAY)
      AND DATE(\_PARTITIONTIME) \= (
        SELECT MAX(DATE(\_PARTITIONTIME))
        FROM \`PROJECT.DATASET.cloud\_pricing\_export\`
        WHERE DATE(\_PARTITIONTIME) \>= DATE\_SUB(CURRENT\_DATE(), INTERVAL 7 DAY)
      )
      AND consumption\_model\_price.consumption\_model\_display\_name LIKE '%Flexible Savings Plan%'
      AND consumption\_model\_price.consumption\_model\_id LIKE ANY ('%')
  ),
  sizing\_periods AS (
    SELECT period\_start\_date, period\_end\_date
    FROM (
      SELECT
        period\_start\_date,
        LEAD(period\_start\_date) OVER (ORDER BY period\_start\_date) AS period\_end\_date
      FROM UNNEST(GENERATE\_ARRAY(0, 3)) AS period\_index,
        UNNEST(\[DATE\_ADD(DATE '2026-07-01', INTERVAL period\_index \* 1 MONTH)\]) AS period\_start\_date
    )
    WHERE period\_end\_date IS NOT NULL
  ),
  uncovered\_cost AS (
    SELECT
      DATE(usage\_start\_time, 'America/Los\_Angeles') AS usage\_date,
      sku.id AS sku\_id,
      IFNULL(price.tier\_start\_amount, 0) AS tier\_start\_usage\_amount,
      SUM(CAST(cost AS NUMERIC) / CAST(currency\_conversion\_rate AS NUMERIC)) AS cost\_usd
    FROM \`PROJECT.DATASET.gcp\_billing\_export\_resource\_v1\_BILLING\_ACCOUNT\_ID\`
    WHERE
      DATE(\_PARTITIONTIME) \>= DATE '2026-07-01'
      AND IFNULL(consumption\_model.applied\_subscription\_instance\_id, '') \= ''
      AND sku.id IN (SELECT sku\_id FROM fsp\_tier\_price\_ratios)
    GROUP BY ALL
  ),
  period\_cost AS (
    SELECT
      fsp\_tier\_price\_ratios.consumption\_model\_id,
      sizing\_periods.period\_start\_date,
      SUM(uncovered\_cost.cost\_usd) AS uncovered\_on\_demand\_cost\_usd,
      SUM(uncovered\_cost.cost\_usd \* fsp\_tier\_price\_ratios.fsp\_price\_ratio) AS potential\_fsp\_cost\_usd
    FROM uncovered\_cost
    JOIN sizing\_periods
      ON
        uncovered\_cost.usage\_date \>= sizing\_periods.period\_start\_date
        AND uncovered\_cost.usage\_date < sizing\_periods.period\_end\_date
    JOIN fsp\_tier\_price\_ratios
      ON
        fsp\_tier\_price\_ratios.sku\_id \= uncovered\_cost.sku\_id
        AND fsp\_tier\_price\_ratios.tier\_start\_usage\_amount \= uncovered\_cost.tier\_start\_usage\_amount
    GROUP BY ALL
  ),
  fsp\_consumption\_models AS (
    SELECT DISTINCT consumption\_model\_id, consumption\_model\_display\_name
    FROM fsp\_tier\_price\_ratios
  )
SELECT
  fsp\_consumption\_models.consumption\_model\_id,
  fsp\_consumption\_models.consumption\_model\_display\_name,
  sizing\_periods.period\_start\_date,
  sizing\_periods.period\_end\_date,
  IFNULL(period\_cost.uncovered\_on\_demand\_cost\_usd, 0) AS uncovered\_on\_demand\_cost\_usd,
  IFNULL(period\_cost.potential\_fsp\_cost\_usd, 0) AS potential\_fsp\_cost\_usd,
  IFNULL(period\_cost.uncovered\_on\_demand\_cost\_usd \- period\_cost.potential\_fsp\_cost\_usd, 0)
    AS potential\_savings\_usd,
  MIN(IFNULL(period\_cost.potential\_fsp\_cost\_usd, 0))
    OVER (PARTITION BY fsp\_consumption\_models.consumption\_model\_id)
    AS suggested\_commitment\_per\_window\_usd
FROM fsp\_consumption\_models
CROSS JOIN sizing\_periods
LEFT JOIN period\_cost
  ON
    period\_cost.consumption\_model\_id \= fsp\_consumption\_models.consumption\_model\_id
    AND period\_cost.period\_start\_date \= sizing\_periods.period\_start\_date
ORDER BY consumption\_model\_display\_name, period\_start\_date

Replace the following:

*   `PROJECT`: The ID of the Google Cloud project that contains your Cloud Billing export dataset.
*   `DATASET`: The name of the BigQuery dataset containing your Cloud Billing export tables.
*   `BILLING_ACCOUNT_ID`: Your Cloud Billing Account API ID, with hyphens replaced by underscores.

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
