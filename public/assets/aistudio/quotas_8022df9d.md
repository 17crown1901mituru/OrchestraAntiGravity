# Quotas et limites du système &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/quotas?hl=fr

Cloud Quotas utilise des quotas et des limites système pour maintenir une allocation équitable des ressources, éviter les surcharges de service et vous aider à gérer l'utilisation des ressources.

Les quotas et les limites du système sont similaires, mais diffèrent sur les points suivants :

*   Les _quotas_ ont des valeurs par défaut, mais vous pouvez généralement demander des ajustements.
*   Les _limites système_ sont des valeurs fixes qui ne peuvent pas être modifiées.

Google Cloud utilise des quotas pour garantir l'équité et réduire les pics d'utilisation et de disponibilité des ressources. Un quota limite la quantité d'une ressourceGoogle Cloud que votre projet Google Cloud peut utiliser. Les quotas s'appliquent à différents types de ressources, y compris les composants matériels, logiciels et réseau. Par exemple, ils peuvent limiter le nombre d'appels d'API à un service, le nombre d'équilibreurs de charge utilisés simultanément par votre projet ou le nombre de projets que vous pouvez créer. Ils protègent la communauté des utilisateurs deGoogle Cloud en empêchant la surcharge des services. Les quotas vous aident également à gérer vos propres ressources Google Cloud .

Le système Cloud Quotas permet d'effectuer les opérations suivantes :

*   Surveiller votre consommation de produits et services Google Cloud
*   Limiter votre consommation de ces ressources
*   [Demander des modifications de la valeur du quota](https://docs.cloud.google.com/docs/quotas/help/request_increase?hl=fr) et [automatiser les ajustements de quota](https://docs.cloud.google.com/docs/quotas/quota-adjuster?hl=fr)

Dans la plupart des cas, lorsque vous tentez d'utiliser une ressource plus que son quota ne le permet, le système bloque l'accès à la ressource et la tâche que vous essayez d'effectuer échoue.

Les quotas s'appliquent généralement au niveau du projet Google Cloud . Votre utilisation d'une ressource dans un projet n'affecte pas votre quota disponible dans un autre projet. Dans un projet Google Cloud , les quotas sont partagés entre toutes les applications et adresses IP.

Pour en savoir plus, consultez la [présentation des quotas Cloud](https://docs.cloud.google.com/docs/quotas/overview?hl=fr).

Les quotas de débit
-------------------

Les quotas suivants s'appliquent aux requêtes des API Cloud Quotas :

| Quota | Valeur |
| --- | --- |
| Requêtes de lecture par minute et par projet | 1200 |
| Requêtes de mise à jour par minute et par projet | 60 |
| Demandes d'augmentation de quota par jour et par projet | 300 |

Demander un ajustement de quota
-------------------------------

Pour ajuster la plupart des quotas, utilisez la console Google Cloud . Pour en savoir plus, consultez [Demander un ajustement de quota](https://docs.cloud.google.com/docs/quotas/help/request_increase?hl=fr).

Étapes suivantes
----------------

*   [Afficher et gérer les quotas](https://docs.cloud.google.com/docs/quotas/view-manage?hl=fr)
*   [Terminologie des quotas et des limites du système](https://docs.cloud.google.com/docs/quotas/terminology?hl=fr)
*   [Notes de version](https://docs.cloud.google.com/docs/quotas/release-notes?hl=fr)
