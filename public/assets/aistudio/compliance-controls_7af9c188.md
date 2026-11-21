# Matrice de contrôle de la conformité de Stellar&nbsp;Engine &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/stellar-engine/compliance-controls

Stellar Engine est un accélérateur d'autorisation d'exploitation (ATO, Authorization to Operate) qui mappe directement les ressources Terraform implémentées aux familles de contrôles de conformité. Il est conçu pour réduire considérablement la charge de documentation des organismes du secteur public et des secteurs réglementés.

Pour obtenir la liste des frameworks compatibles avec [Assured Workloads](https://docs.cloud.google.com/assured-workloads/docs?hl=fr), consultez [Scénarios de déploiement courants](https://docs.cloud.google.com/docs/security/compliance/stellar-engine?hl=fr#deployment-scenarios).

Mappage NIST SP 800-53r5
------------------------

Stellar Engine fournit un modèle complet de plan de sécurité système (SSP, System Security Plan) et une matrice de traçabilité des contrôles de sécurité (SCTM, Security Control Traceability Matrix) qui mappent les ressources implémentées aux contrôles NIST SP 800-53r5.

Pour assurer une couverture complète des packages ATO, Stellar Engine fournit un mappage pour les familles de contrôles critiques suivantes :

*   **Contrôle des accès (AC)** : appliqué à l'aide de liaisons de rôles [Identity and Access Management](https://docs.cloud.google.com/iam/docs?hl=fr) avec le principe du moindre privilège, de verrouillages de comptes de service et de limites d'accès strictes au niveau des dossiers.
*   **Protection des systèmes et des communications (SC)** : assurée à l'aide de topologies [VPC partagé](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=fr) en étoile, de pare-feu de périmètre (y compris la prise en charge du pare-feu de nouvelle génération Palo Alto), de la ségrégation des communications et de l'intégration à [VPC Service Controls](https://docs.cloud.google.com/vpc-service-controls/docs?hl=fr).
*   **Identification et authentification (IA)** : gérées à l'aide de la hiérarchie IAM et de l'intégration aux fournisseurs d'identité au niveau de l'organisation, en appliquant des mécanismes d'authentification modernes.
*   **Audit et responsabilité (AU)** : assurés par le biais de récepteurs de journaux d'audit globaux, d'un stockage centralisé des journaux (par exemple, à l'aide de [BigQuery](https://docs.cloud.google.com/bigquery/docs?hl=fr) ou [Cloud Storage](https://docs.cloud.google.com/storage/docs?hl=fr)) et de règles de conservation immuables pour garantir la traçabilité.
*   **Gestion de la configuration (CM)** : gérée par l'infrastructure as code (IaC) elle-même, ce qui garantit des déploiements cohérents et permet la détection automatisée des dérives.
*   **Planification des imprévus (CP)** : prise en charge par la gestion de l'état localisé avec la gestion des versions d'objet activée dans les buckets Cloud Storage, ce qui permet une récupération rapide de l'état de la configuration.

Livrables pour l'accélération de l'ATO
--------------------------------------

Vous pouvez utiliser Stellar Engine pour accéder aux artefacts de conformité suivants :

*   **Feuille de calcul de mappage de la conformité (SCTM)** : modèles préconfigurés faisant correspondre directement les contrôles aux configurations Terraform.
*   **Modèles de plan de sécurité système (SSP)** : récits de référence pour les enclaves FedRAMP High et DoD IL5.
*   **Framework de chemin d'autorisation** : instructions détaillées sur l'utilisation des livrables Stellar Engine pour demander l'approbation de l'ATO.

Conformité intégrée et responsabilité partagée
----------------------------------------------

En intégrant directement les exigences de conformité dans l'IaC, Stellar Engine permet de s'assurer que les contrôles de base sont implémentés de manière cohérente et automatique, ce qui réduit le risque de dérive ou d'erreur humaine.

### Responsabilité partagée

Bien que Stellar Engine puisse accélérer le processus ATO, vous ou votre fournisseur de logiciels indépendant (ISV, Independent Software Vendor) restez responsable du chiffrement au niveau de l'application (par exemple, des données authentifiées supplémentaires (AAD, Additional Authenticated Data)), de l'intégration du fournisseur d'identité, de la rotation des clés opérationnelle et d'autres mesures de protection spécifiques à l'application. Pour en savoir plus sur le modèle de responsabilité partagée, consultez le [Google Cloud guide d'implémentation FedRAMP](https://docs.cloud.google.com/docs/security/compliance/fedramp-implementation-guide?hl=fr) ou le [modèle de responsabilité partagée](https://docs.cloud.google.com/assured-workloads/docs/shared-responsibility?hl=fr).

### Résidence des données

Stellar Engine s'appuie sur la [(`gcp.resourceLocations`) contrainte de règle d'administration](https://docs.cloud.google.com/organization-policy/restrict-locations?hl=fr) pour limiter la création de ressources aux régions autorisées. Cette contrainte est implémentée par défaut dans Stellar Engine et Assured Workloads, et constitue une exigence fondamentale pour FedRAMP High et DoD IL5.

Étape suivante
--------------

*   Contactez [Google Cloud l'organisation Public Sector Professional Services](https://docs.cloud.google.com/docs/security/compliance/stellar-engine?hl=fr#professional-services) pour obtenir de l'aide sur la configuration de votre déploiement et accéder aux artefacts ATO et aux packages de documentation.
