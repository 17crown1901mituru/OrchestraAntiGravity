# Infrastructure as Code sur Google Cloud &nbsp;|&nbsp; Terraform &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/iac-overview?hl=fr

L'Infrastructure as Code (IaC) est le processus de provisionnement et de gestion de l'infrastructure de l'application logicielle à l'aide de _code_ plutôt que d'interfaces utilisateur graphiques ou de scripts de ligne de commande.

Le provisionnement de l'infrastructure d'application implique généralement la configuration et la gestion des machines virtuelles, des connexions à la base de données, du stockage et d'autres éléments d'infrastructure. La gestion manuelle de cette infrastructure prend du temps et est sujette aux erreurs, en particulier lorsque vous gérez des applications à grande échelle.

L'IaC vous permet de définir votre infrastructure à l'aide de fichiers de configuration, ce qui vous permet de la créer, de la modifier et de la gérer de manière sûre et reproductible. Vous pouvez définir des configurations de ressources que vous pouvez versionner, réutiliser et partager. L'IaC vous permet de spécifier l'état souhaité de votre infrastructure. Vous pouvez ensuite déployer la même configuration plusieurs fois pour créer des environnements de développement, de test et de production reproductibles.

L'IaC vous permet de traiter le provisionnement et la configuration de votre infrastructure de la même manière que vous gérez le code d'application. Vous pouvez stocker votre logique de configuration du provisionnement dans le contrôle source et profiter des pipelines d'intégration et de déploiement continus (CI/CD).

Avantages de l'IaC
------------------

L'utilisation de l'IaC pour configurer et gérer l'infrastructure de votre application est une bonne pratique pour un certain nombre de cas d'utilisation courants. [Google gère ses systèmes avec l'IaC](https://www.usenix.org/publications/loginonline/prodspec-and-annealing-intent-based-actuation-google-production) et la définit comme une [pratique standard](https://sre.google/workbook/configuration-design/?hl=fr) en interne.

L'IaC offre les avantages suivants :

*   Vous pouvez définir votre infrastructure en fonction de vos besoins et réutiliser la même configuration pour créer plusieurs environnements de manière cohérente.
*   Vous pouvez automatiser la création et la gestion de vos ressources cloud, y compris pour les environnements de déploiement et de test.
*   Traitez les modifications d'infrastructure de la même manière que les modifications d'application. Par exemple, vous pouvez vous assurer que les modifications apportées à la configuration sont examinées et validées automatiquement. La gestion des environnements de production par le biais de processus contrôlés par les modifications à l'aide d'IaC est une bonne pratique.
*   Vous pouvez conserver un historique de toutes les modifications de configuration. Les modifications peuvent être auditées et annulées.
*   Vous pouvez disposer d'une source d'informations unique pour votre infrastructure cloud.

Google Cloud est étroitement intégré à de nombreux outils IaC. Choisissez l'un des outils suivants en fonction de votre cas d'utilisation :

*   **Terraform**
    
    En général, pour configurer et gérer l'infrastructure Google Cloud à l'aide de code, utilisez le fournisseur Terraform pour Google Cloud.
    
    HashiCorp Terraform est un outil IaC qui vous permet de définir des ressources dans le cloud et sur site dans des fichiers de configuration lisibles par l'homme, que vous pouvez versionner, réutiliser et partager. Vous pouvez ensuite utiliser un workflow cohérent pour provisionner et gérer l'ensemble de votre infrastructure tout au long de son cycle de vie. Pour en savoir plus, consultez [Présentation de Terraform sur Google Cloud](https://docs.cloud.google.com/docs/terraform/terraform-overview?hl=fr).
    
*   **Infrastructure Manager**
    
    Si vous souhaitez automatiser le déploiement de votre configuration Terraform, utilisez Infrastructure Manager (Infra Manager).
    
    Infra Manager automatise le déploiement et la gestion des ressources d'infrastructureGoogle Cloud à l'aide de Terraform. Infra Manager vous permet de déployer des ressources de manière programmatique surGoogle Cloud. Vous pouvez ainsi utiliser ce service au lieu de gérer une autre chaîne d'outils pour travailler avec Terraform sur Google Cloud. Pour en savoir plus, consultez la [présentation d'Infra Manager](https://docs.cloud.google.com/infrastructure-manager/docs/overview?hl=fr).
    
*   **Terraform Cloud et Terraform Enterprise**
    
    Si vous avez besoin d'une gestion complète des modifications avec Terraform dans votre organisation, utilisez Terraform Cloud ou Terraform Enterprise.
    
    Terraform Cloud est une application Software as a Service (SaaS) qui exécute Terraform dans un environnement distant stable et stocke de manière sécurisée l'état et les secrets. Terraform Cloud s'intègre également à la CLI Terraform et se connecte aux systèmes de contrôle des versions (VCS) courants tels que GitHub, GitLab et Bitbucket. Lorsque vous connectez un espace de travail Terraform Cloud à un dépôt VCS, les nouveaux commits et modifications peuvent déclencher automatiquement des plans Terraform. Terraform Cloud propose également une API qui vous permet de l'intégrer à des workflows existants.
    
    Terraform Enterprise vous permet de configurer une distribution auto-hébergée de Terraform Cloud. Il offre des limites de ressources personnalisables et est idéal pour les organisations ayant des exigences strictes en termes de sécurité et de conformité.
    
    Pour en savoir plus, consultez la page [Éditions Terraform dans la documentation Hashicorp](https://developer.hashicorp.com/terraform/intro/terraform-editions).
    
*   **Cloud Development Kit pour Terraform**
    
    Si vous souhaitez générer une infrastructure avec un langage de programmation à usage général au lieu d'utiliser le langage de configuration Hashicorp (HCL), utilisez le kit de développement cloud pour Terraform (CDKTF).
    
    [CDKTF](https://developer.hashicorp.com/terraform/cdktf) vous permet de configurer Terraform à l'aide d'un langage de programmation pour définir et provisionner Google Cloud l'infrastructure, et d'utiliser votre chaîne d'outils existante pour des processus tels que les tests et la gestion des dépendances.
    
*   **Pulumi**
    
    [Pulumi](https://www.pulumi.com/docs/clouds/gcp/) est un autre outil que vous pouvez utiliser pour provisionner une infrastructure à l'aide de langages de programmation. Vous pouvez utiliser le fournisseur Google Cloud pour Pulumi afin de créer du code d'infrastructure à l'aide de langages de programmation tels que TypeScript, Python, Go, C#, Java ou YAML.
    
*   **Config Controller et Config Connector**
    
    Pour gérer les ressources Google Cloud avec Kubernetes, utilisez Config Controller et Config Connector.
    
    Config Controller et Config Connector vous permettent de configurer les services et les ressourcesGoogle Cloud à l'aide des outils Kubernetes. Vous pouvez utiliser des outils GitOps tels que [Config Sync](https://docs.cloud.google.com/anthos-config-management/docs/config-sync-overview?hl=fr) et les API Kubernetes. Vous pouvez également configurer et utiliser des primitives d'ingénierie de plate-forme telles que les webhooks d'admission et les opérateurs.
    
    Pour en savoir plus, consultez la [présentation de Config Controller](https://docs.cloud.google.com/anthos-config-management/docs/concepts/config-controller-overview?hl=fr) et la [présentation de Config Connector](https://docs.cloud.google.com/config-connector/docs/overview?hl=fr).
    
*   **Crossplane**
    
    Une autre option pour gérer les ressources Google Cloud avec Kubernetes consiste à utiliser Crossplane.
    
    Crossplane connecte votre cluster Kubernetes à des ressources externes non Kubernetes et permet aux équipes de plate-forme de créer des API Kubernetes personnalisées pour consommer ces ressources. Crossplane agit comme un [contrôleur Kubernetes](https://kubernetes.io/docs/concepts/architecture/controller/) pour surveiller l'état des ressources externes et permettre l'application de l'état. Une fois Crossplane installé dans un cluster Kubernetes, les utilisateurs ne communiquent qu'avec Kubernetes. Crossplane gère la communication avec les ressources externes telles que Google Cloud. Si un élément modifie ou supprime une ressource en dehors de Kubernetes, Crossplane annule la modification ou recrée la ressource supprimée.
    
    Pour en savoir plus, consultez la [documentation Crossplane](https://docs.crossplane.io/v1.18/).
    
*   **Ansible**
    
    Si vous souhaitez automatiser le provisionnement, la gestion de la configuration, le déploiement d'applications, l'orchestration et d'autres processus informatiques, utilisez Ansible. Pour en savoir plus, consultez [Ansible pourGoogle Cloud](https://docs.ansible.com/ansible/latest/collections/google/cloud/).
    

Étapes suivantes
----------------

*   En savoir plus sur [Terraform](https://docs.cloud.google.com/docs/terraform/terraform-overview?hl=fr)
*   Découvrez comment [créer un serveur Web de base sur Compute Engine à l'aide de Terraform](https://docs.cloud.google.com/docs/terraform/get-started-with-terraform?hl=fr).
*   Découvrez comment [stocker l'état Terraform dans un bucket Cloud Storage](https://docs.cloud.google.com/docs/terraform/resource-management/store-state?hl=fr).
