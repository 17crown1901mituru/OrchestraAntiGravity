# Autorisations de quota &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/permissions?hl=fr

Le rôle [Identity and Access Management (IAM)](https://docs.cloud.google.com/iam/docs/overview?hl=fr) prédéfini pour les autorisations est nommé "Administrateur de quotas". Ce rôle peut être attribué au niveau du projet, du dossier et de l'organisation.

*   Si il est accordé au niveau du projet, l'utilisateur sera autorisé à effectuer des opérations au niveau du projet.
*   Si l'autorisation est accordée au niveau du dossier, l'utilisateur est autorisé à effectuer des opérations au niveau du projet pour tous les projets de ce dossier.
*   Si ce rôle est accordé au niveau de l'organisation, l'utilisateur est autorisé à effectuer des opérations au niveau de l'organisation. Étant donné que les autorisations IAM sont héritées du niveau supérieur, cet utilisateur recevra également des autorisations au niveau du projet et du dossier.

Les utilisateurs disposant du rôle "Propriétaire de projet" peuvent attribuer le rôle "Administrateur de quotas" à d'autres utilisateurs au niveau du projet. Les utilisateurs dotés du rôle Propriétaire de l'organisation peuvent attribuer le rôle Administrateur de quotas au niveau de l'organisation.

Autorisations d'affichage des quotas
------------------------------------

Pour afficher le quota de votre projet dans la console Google Cloud ou y accéder de manière automatisée à l'aide de l'API Cloud Quotas, vous devez disposer des autorisations IAM suivantes :

*   `resourcemanager.projects.get`
*   `resourcemanager.folders.get` si vous souhaitez afficher le quota d'un [dossier](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=fr#folders) complet.
*   `resourcemanager.organizations.get` si vous souhaitez afficher le quota de l'ensemble d'une [organisation](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=fr#organizations).
*   [`monitoring.timeSeries.list`](https://docs.cloud.google.com/iam/docs/roles-permissions/monitoring?hl=fr#monitoring.timeSeries.list)
*   [`serviceusage.services.list`](https://docs.cloud.google.com/iam/docs/roles-permissions/serviceusage?hl=fr#serviceusage.services.list)
*   [`cloudquotas.quotas.get`](https://docs.cloud.google.com/iam/docs/roles-permissions/cloudquotas?hl=fr#cloudquotas.quotas.get)

Pour savoir quels [rôles](https://docs.cloud.google.com/iam/docs/roles-overview?hl=fr) incluent ces autorisations par défaut, consultez la [documentation de référence sur les autorisations IAM](https://docs.cloud.google.com/iam/docs/roles-permissions?hl=fr).

Vous pouvez également afficher les quotas Cloud de manière automatisée à l'aide de l'[API Service Usage](https://docs.cloud.google.com/service-usage/docs/reference/rest?hl=fr).

Autorisations de modifier les quotas
------------------------------------

Pour modifier votre quota au niveau du projet, du dossier ou de l'organisation, vous devez disposer des autorisations IAM suivantes :

*   [`serviceusage.quotas.update`](https://docs.cloud.google.com/iam/docs/roles-permissions/serviceusage?hl=fr#serviceusage.quotas.update)
*   [`cloudquotas.quotas.update`](https://docs.cloud.google.com/iam/docs/roles-permissions/cloudquotas?hl=fr#cloudquotas.quotas.update)

Elles sont incluses par défaut pour les [rôles](https://docs.cloud.google.com/iam/docs/roles-overview?hl=fr) suivants : Propriétaire, Éditeur, Administrateur de quotas et Administrateur Service Usage.

Autorisations d'affichage des demandes d'augmentation de quota
--------------------------------------------------------------

Pour afficher les demandes d'augmentation de quota dans la console Google Cloud , vous devez disposer des [autorisations IAM](https://docs.cloud.google.com/iam/docs/roles-permissions?hl=fr) suivantes :

*   `resourcemanager.projects.get`
*   [`serviceusage.services.list`](https://docs.cloud.google.com/iam/docs/roles-permissions/serviceusage?hl=fr#serviceusage.services.list)
*   [`serviceusage.quotas.get`](https://docs.cloud.google.com/iam/docs/roles-permissions/serviceusage?hl=fr#serviceusage.quotas.get)

Autorisations de création de règle d'alerte pour un quota
---------------------------------------------------------

Pour [configurer des alertes de quotas](https://docs.cloud.google.com/docs/quotas/set-up-quota-alerts?hl=fr), vous devez disposer de l'autorisation suivante :

*   `monitoring.alertPolicies.create`

Étapes suivantes
----------------

*   [Configurer VPC Service Controls](https://docs.cloud.google.com/docs/quotas/configure-vpc-service-controls?hl=fr)
*   [Utiliser des règles d'administration personnalisées](https://docs.cloud.google.com/docs/quotas/custom-constraints?hl=fr)
*   [Afficher et gérer les quotas](https://docs.cloud.google.com/docs/quotas/view-manage?hl=fr)
