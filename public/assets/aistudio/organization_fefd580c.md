# Organization guidelines &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/gcmvsp/organization

Organization guidelines
-----------------------

The following guidelines for the minimum viable security platform align with the organization security pillar.

After you implemented the basic guidelines, implement the following organization guidelines.

<table><tbody><tr><th>Item</th><td></td></tr><tr><th>Description</th><td><div><p>Ensure only identities from your organization are allowed in your Google Cloud environment. Use the <strong>Domain restricted sharing</strong> (<code translate="no" dir="ltr">iam.allowedPolicyMemberDomains</code>) or the <code translate="no" dir="ltr">iam.managed.allowedPolicyMembers</code> organization policy constraint to define one or more Cloud Identity or Google Workspace customer IDs whose principals can be added to Identity and Access Management (IAM) policies.</p><p>These constraints help prevent employees from granting access to external accounts outside of your organization's control that don't follow your security policies for multifactor authentication (MFA) or password management. This control is critical for preventing unauthorized access, ensuring that only trusted, managed corporate identities can be used.</p></div></td></tr><tr><th>Related information</th><td><ul><li><a href="https://docs.cloud.google.com/resource-manager/docs/organization-policy/restricting-domains">Restricting identities by domain</a></li></ul></td></tr><tr><th>Item ID</th><td>MVSP-CO-1.21</td></tr><tr><th>Mapping</th><td><p>Related NIST-800-53 controls:</p><ul><li>AC-3</li><li>AC-17</li><li>AC-20</li></ul><p>Related CRI profile controls:</p><ul><li>PR.AC-3.1</li><li>PR.AC-3.2</li><li>PR.AC-4.1</li><li>PR.AC-4.2</li><li>PR.AC-4.3</li><li>PR.AC-6.1</li><li>PR.PT-3.1</li><li>PR.PT-4.1</li></ul><p>Compliance Manager control:</p><ul><li><a href="https://docs.cloud.google.com/security-command-center/docs/compliance-manager-cloud-controls#enforce-domain-restricted-sharing">Enforce Domain Restricted Sharing</a></li></ul></td></tr></tbody></table>

Advanced level guidelines
-------------------------

After you implemented the intermediate guidelines, implement the following organization guidelines.

<table><tbody><tr><th>Item</th><td><h3 id="restrict-resource-locations" data-text="Restrict resource locations" tabindex="-1">Restrict resource locations</h3></td></tr><tr><th>Description</th><td><div><p>The Resource Location Restriction (<code translate="no" dir="ltr">gcp.resourceLocations</code>) constraint ensures that only your approved Google Cloud regions are used to store data. The value is specific to your systems and matches your organization's approved list of regions for data residency.</p><p>This constraint lets your organization enforce that your resources and data are only created and saved in specific, approved geographic regions.</p></div></td></tr><tr><th>Related information</th><td><ul><li><a href="https://docs.cloud.google.com/resource-manager/docs/organization-policy/defining-locations">Restricting resource locations</a></li></ul></td></tr><tr><th>Item ID</th><td>MVSP-CO-1.22</td></tr><tr><th>Mapping</th><td><p>Related NIST-800-53 controls:</p><ul><li>AC-3</li><li>AC-17</li><li>AC-20</li></ul><p>Related CRI profile controls:</p><ul><li>PR.AC-3.1</li><li>PR.AC-3.2</li><li>PR.AC-4.1</li><li>PR.AC-4.2</li><li>PR.AC-4.3</li><li>PR.AC-6.1</li><li>PR.PT-3.1</li><li>PR.PT-4.1</li></ul></td></tr></tbody></table>

<table><tbody><tr><th>Item</th><td><h3 id="restrict-resource-service-usage" data-text="Restrict resource service usage" tabindex="-1">Restrict resource service usage</h3></td></tr><tr><th>Description</th><td><div><p>The <code translate="no" dir="ltr">gcp.restrictServiceUsage</code> constraint ensures that only your approved Google Cloud services are used in the right places. For example, a production or highly sensitive folder has a small list of Google Cloud services that are approved to store data. A sandbox folder might have a larger list of services and accompanying data security controls to help prevent data exfiltration. The value is specific to your systems and matches your approved list of services and dependencies for specific folders and projects.</p><p>This constraint lets your organization create an allowlist of approved services, which helps prevent employees from using unvetted services.</p></div></td></tr><tr><th>Related information</th><td><ul><li><a href="https://docs.cloud.google.com/resource-manager/docs/organization-policy/restricting-resources">Restricting resource usage</a></li></ul></td></tr><tr><th>Item ID</th><td>MVSP-CO-1.23</td></tr><tr><th>Mapping</th><td><p>Related NIST-800-53 controls:</p><ul><li>AC-3</li><li>AC-17</li><li>AC-20</li></ul><p>Related CRI profile controls:</p><ul><li>PR.AC-3.1</li><li>PR.AC-3.2</li><li>PR.AC-4.1</li><li>PR.AC-4.2</li><li>PR.AC-4.3</li><li>PR.AC-6.1</li><li>PR.PT-3.1</li><li>PR.PT-4.1</li></ul><p>Compliance Manager control:</p><ul><li><a href="https://docs.cloud.google.com/security-command-center/docs/compliance-manager-cloud-controls#restrict-resource-service-usage">Restrict Resource Service Usage</a></li></ul></td></tr></tbody></table>

What's next
-----------

*   [Infrastructure guidelines](https://docs.cloud.google.com/docs/security/gcmvsp/infrastructure)
*   [Overview of minimum viable secure platform](https://docs.cloud.google.com/docs/security/gcmvsp)

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2026-11-10 UTC.
