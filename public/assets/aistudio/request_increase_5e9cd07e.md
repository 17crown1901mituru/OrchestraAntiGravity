# Afficher et gérer les quotas &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/help/request_increase

Ce document explique comment afficher les valeurs des quotas dans la consoleGoogle Cloud . Vous pouvez également gérer les quotas depuis la consoleGoogle Cloud , l'API Cloud Quotas et Google Cloud CLI (gcloud CLI).

Limites
-------

Cloud Quotas présente les limites suivantes :

*   Dans la plupart des cas, les ajustements d'_augmentation_ de quota doivent être effectués [au niveau du projet](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=fr). Un nombre limité de produits acceptent les ajustements d'augmentation de quota [au niveau de l'organisation](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=fr#organizations). Pour savoir si un produit Google Cloud accepte les ajustements d'augmentation de quota au niveau de l'organisation, consultez la documentation de ce produit.
    
*   Vous pouvez demander des ajustements de _diminution_ de quota pour les quotas au niveau du projet, de l'organisation et du [dossier](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=fr#folders).
    

Avant de commencer
------------------

Les tâches décrites dans ce document nécessitent des rôles Identity and Access Management (IAM).

### Rôles requis

Pour obtenir les autorisations nécessaires pour demander des ajustements de quota, demandez à votre administrateur de vous accorder les rôles IAM suivants sur le projet, le dossier ou l'organisation :

*   Pour afficher les quotas : [Lecteur de quotas](https://docs.cloud.google.com/iam/docs/roles-permissions/servicemanagement?hl=fr#servicemanagement.quotaViewer) (`roles/servicemanagement.quotaViewer`)
*   Pour demander des ajustements de quota : [Administrateur de quotas](https://docs.cloud.google.com/iam/docs/roles-permissions/servicemanagement?hl=fr#servicemanagement.quotaAdmin) (`roles/servicemanagement.quotaAdmin`)

Pour en savoir plus sur l'attribution de rôles, consultez [Gérer l'accès aux projets, aux dossiers et aux organisations](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=fr).

Ces rôles prédéfinis contiennent les autorisations requises pour demander des ajustements de quota. Pour connaître les autorisations exactes requises, développez la section **Autorisations requises** :

#### Autorisations requises

Les autorisations suivantes sont requises pour demander des ajustements de quota :

*   `serviceusage.quotas.update`
*   `cloudquotas.quotas.update`

Vous pouvez également obtenir ces autorisations avec des [rôles personnalisés](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=fr) ou d'autres [rôles prédéfinis](https://docs.cloud.google.com/iam/docs/roles-overview?hl=fr#predefined).

Afficher les quotas dans la console Google Cloud
------------------------------------------------

Vous pouvez afficher vos valeurs de quota actuelles dans la [consoleGoogle Cloud](https://console.cloud.google.com/?hl=fr) en deux endroits :

*   Sur la page [**Quotas et limites du système**](https://console.cloud.google.com/quotas?project=_&hl=fr), qui répertorie l'utilisation et les valeurs de quota de votre projet
    
*   Dans le [**tableau de bord des API de la console Google Cloud**](https://console.cloud.google.com/apis/dashboard?hl=fr) , qui répertorie les informations de quota pour une API spécifique, y compris l'utilisation des ressources au fil du temps
    

### Afficher les quotas de votre projet

La page **IAM et administration \> Quotas et limites du système** affiche un tableau avec des colonnes configurables. Les colonnes **Service** et **Quota** fournissent des informations générales sur le quota décrit. Par exemple, le **Service** peut être l'**API Cloud Logging** et le **Quota** peut être les **Requêtes d'ingestion de journaux par minute**. Le champ **Quota** décrit également la manière dont le quota est évalué. Les quotas de débit sont évalués par minute, pour 100 secondes ou par jour. Les quotas sans l'une de ces instructions sont des quotas d'allocation.

Pour afficher les valeurs et l'utilisation des quotas de toutes les ressources de votre projet, procédez comme suit :

### Console

1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
2.  Pour vous concentrer sur les informations dont vous avez besoin, filtrez et triez la liste comme suit :
    
    *   Pour filtrer la liste, saisissez les propriétés et les valeurs dans le champ. Par exemple, pour afficher les quotas de l'API BigQuery, sélectionnez **Service**, puis **API BigQuery**.
        
    *   Pour trier la liste, cliquez sur l'en-tête de la colonne que vous souhaitez réorganiser. Par exemple, pour trier par ordre alphabétique en fonction du nom du quota, cliquez sur l'en-tête de la colonne **Quota**.
        
    
    Par défaut, la liste est triée de manière à afficher vos quotas les plus utilisés en premier, ce qui vous permet de voir les quotas que vous risquez de dépasser.
    
3.  Facultatif : Pour inspecter l'utilisation globale des quotas au niveau du dossier et de l'organisation, sélectionnez l'organisation ou le dossier dans le sélecteur de ressources.
    

Vous pouvez trouver des informations supplémentaires sur le quota à l'aide des colonnes **Métrique**, **Nom de limite** et **Ressource surveillée**. Ces colonnes fournissent les informations détaillées nécessaires pour représenter graphiquement un quota.

Pour obtenir des informations sur votre utilisation du quota, consultez les colonnes **Pourcentage d'utilisation actuel** et **Utilisation actuelle**. L'utilisation actuelle est calculée en fonction des critères suivants:

*   Pour les quotas de débit à la minute :
    
    *   Utilisation _moyenne_ par minute au cours des 10 dernières minutes.
*   Pour les quotas de débit quotidiens :
    
    *   Utilisation _totale_ du jour jusqu'à l'heure actuelle, selon l'heure normale du Pacifique.
*   Pour les quotas d'allocation :
    
    *   Valeur la plus récente. Par exemple, ce nombre peut afficher le nombre d'équilibreurs de charge utilisés par votre projet.
*   Pour les quotas simultanés :
    
    *   Valeur la plus récente. Par exemple, ce nombre peut afficher le nombre d'opérations "insert\_operations" en cours pour un service donné.

Pour en savoir plus sur les quotas d'un service spécifique, consultez la documentation associée à ce service. Par exemple, les utilisateurs de Compute Engine peuvent consulter la [présentation des quotas et des limites de Compute Engine](https://docs.cloud.google.com/compute/quotas-limits?hl=fr).

### Afficher et exporter l'utilisation des quotas au fil du temps

Vous pouvez afficher l'utilisation des quotas au fil du temps à l'aide de graphiques. La page "Quotas et limites du système" de la console fournit des graphiques pour chaque quota ou limite du système figurant dans le tableau "Quotas et limites du système". Une fois que vous avez affiché un graphique, vous pouvez l'exporter. Pour afficher et exporter un graphique, procédez comme suit :

### Console

1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
2.  Recherchez le quota ou la limite du système qui vous intéresse. Dans l'entrée correspondant à ce quota ou à cette limite système, cliquez sur **Afficher le graphique d'utilisation**. La vue graphique s'ouvre.
    
3.  Dans la vue "Graphique", définissez la période. La valeur par défaut est de sept jours. Pour la modifier, cliquez sur le menu de la période et sélectionnez la période de votre choix.
    
4.  Pour exporter le graphique, cliquez sur **Autres options du graphique**, puis sélectionnez **Télécharger** \> **Télécharger au format PNG**. Le graphique est téléchargé au format PNG.
    

Pour en savoir plus sur les graphiques, consultez [Configurer des alertes et la surveillance des quotas](https://docs.cloud.google.com/docs/quotas/set-up-quota-alerts?hl=fr).

### Afficher les quotas spécifiques aux API

Pour afficher des informations détaillées sur le quota d'une API spécifique, y compris son utilisation dans le temps, consultez la page sur le quota de l'API dans la console Google Cloud . Selon l'API, ces limites peuvent inclure des _requêtes par jour_, des _requêtes par minute_ et des _requêtes par minute et par utilisateur_. Certaines API définissent des limites très basses tant que vous n'avez pas [activé la facturation sur votre projet](https://docs.cloud.google.com/billing/docs/how-to/modify-project?hl=fr#enable_billing_for_a_project).

Si aucune valeur n'est définie pour un quota donné, la valeur `Unlimited` s'affiche.

Vous pouvez afficher les limites actuelles d'une API particulière dans la console Google Cloud , à deux endroits : sur la page **Quotas et limites du système** de la console Google Cloud ou sur la page **API et services**. Utilisez la vue de la console de votre choix :

### Console

*   Pour afficher les quotas spécifiques à une API sur la page **Quotas et limites du système** à l'aide d'un filtre :
    
    1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
        
        [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
        
    2.  Cliquez sur **Filtrer**, puis sélectionnez **Service** dans le menu qui s'affiche.
        
    3.  Sélectionnez ou saisissez le nom du service que vous souhaitez afficher.
        
        La page affiche des informations sur les quotas et les limites du système pour l'API spécifiée. Les chiffres qui apparaissent dans la colonne `Value` correspondent aux valeurs de quota par défaut, sauf si vous avez ajusté le quota.
        
*   Pour afficher les quotas spécifiques aux API sur la page **API et services** :
    
    1.  Accédez à la page **API et services** :
        
        [Accéder aux API et services](https://console.cloud.google.com/apis/dashboard?project=_&hl=fr)
        
    2.  Sélectionnez votre projet.
        
    3.  Cliquez sur le nom de l'API du service que vous souhaitez afficher.
        
    4.  Cliquez sur l'onglet **Quotas et limites du système**.
        
        La page affiche des informations sur les quotas et les limites du système pour l'API spécifiée. Les chiffres qui apparaissent dans la colonne `Value` correspondent aux valeurs de quota par défaut, sauf si vous avez ajusté le quota.
        

Pour afficher l'utilisation au fil du temps, cliquez sur **Afficher le graphique d'utilisation**.

### Filtrez les quotas

Pour filtrer la liste des quotas en fonction de propriétés spécifiques, procédez comme suit :

### Console

1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
2.  Cliquez sur **Filtrer** pour interroger vos quotas selon un nom ou une valeur de propriété spécifique.
    

Gérer vos quotas à l'aide de la console
---------------------------------------

Selon l'utilisation de vos ressources, vous pouvez ajuster les valeurs de quota de votre projet à la hausse ou à la baisse. Cette section décrit comment modifier les valeurs de quota fournies. Pour afficher les valeurs, dans la colonne **Détails**, cliquez sur **Tous les quotas**.

### Demander un ajustement de quota

Les demandes d'ajustement de quotas Cloud sont soumises à un examen. Si votre demande nécessite un examen, vous recevrez un e-mail confirmant qu'elle a bien été transmise. Si vous avez besoin d'aide, répondez à l'e-mail. Après examen de votre demande, vous recevrez une notification par e-mail indiquant si votre demande a été approuvée.

### Console

Pour ajuster une valeur de quota, suivez les étapes ci-dessous :

1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
    Si vous êtes invité à sélectionner un projet, choisissez celui que vous souhaitez ajuster.
2.  Recherchez la valeur de quota que vous souhaitez modifier dans la colonne **Quota**, puis cochez la case située à côté du quota concerné.
    
    Si nécessaire, utilisez le champ de recherche **Filtrer** pour rechercher votre quota.
    
3.  Cliquez sur **Modifier**. La boîte de dialogue **Modifications de quotas** s'affiche.
    
4.  Saisissez la valeur de quota souhaitée dans le champ **Nouvelle valeur**. Certaines valeurs de quota sont associées à une unité de mesure. Le cas échéant, sélectionnez l'unité souhaitée dans la liste adjacente. Cliquez sur **OK**.
    
    Facultatif : Si une case à cocher avec le texte "`I understand that this request will remove any overrides`" s'affiche, cela signifie que si vous ajustez la valeur du quota à un nombre égal ou supérieur à la valeur par défaut, le [quota de remplacement](https://docs.cloud.google.com/docs/quotas/view-manage?hl=fr#create_override) précédent sera supprimé. Si c'est ce que vous souhaitez, cochez la case et poursuivez.
    
5.  Pour augmenter la valeur de votre quota au-delà du nombre indiqué dans la boîte de dialogue, sélectionnez **Demander un quota supérieur**.
    
    1.  Dans le formulaire **Modifications de quotas**, saisissez la valeur de quota que vous souhaitez obtenir dans le champ **Nouvelle valeur**. Si un champ **Description de la requête** s'affiche, saisissez une description. Cliquez sur **OK**.
        
    2.  Si un bouton **Suivant** s'affiche, cliquez sur **Suivant**, puis renseignez vos coordonnées sur l'écran suivant.
        
6.  Cliquez sur **Envoyer la requête**.
    

Si vous constatez que vous ne pouvez pas demander un ajustement dans la console, demandez l'augmentation auprès de [Cloud Customer Care](https://docs.cloud.google.com/support?hl=fr).

Pour en savoir plus sur le fonctionnement du processus d'augmentation des quotas, consultez [À propos des ajustements de quotas](https://docs.cloud.google.com/docs/quotas/overview?hl=fr#about_increase_requests).

#### Regrouper des requêtes pour des valeurs de quota plus élevées

Vous pouvez regrouper des demandes de quota plus élevé en cochant la case à côté de chaque quota que vous souhaitez inclure. Toutefois, le traitement par lot des demandes peut augmenter le délai nécessaire à Google Cloud pour examiner votre demande.

Pour réduire le temps d'examen, regroupez les demandes d'ajustement de quota par produit et par zone géographique. Par exemple, si vous souhaitez demander des ajustements pour les quotas de réseau et de VM Compute Engine, créez une requête pour les quotas de réseau et une autre pour les quotas de VM Compute Engine.

### Afficher les demandes d'augmentation de quota

Vous pouvez consulter les demandes d'augmentation de quota en cours et passées dans la consoleGoogle Cloud .

Pour afficher les demandes d'augmentation de quota en attente, procédez comme suit :

### Console

1.  Assurez-vous de disposer des [autorisations nécessaires pour afficher les demandes d'augmentation de quota](https://docs.cloud.google.com/docs/quotas/permissions?hl=fr#permissions_increase).
    
2.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
3.  Une icône **En attente** s'affiche à côté des valeurs de quota en attente d'une décision concernant les demandes d'augmentation envoyées précédemment. Cliquez sur **En attente** pour afficher les détails des demandes en attente.
    

Les demandes en attente s'affichent également dans le formulaire **Modifications de quota** lorsqu'un ajustement de quota est sur le point d'être envoyé.

Pour afficher toutes les demandes d'augmentation de quota, y compris celles en attente et celles traitées :

### Console

1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
2.  Cliquez sur l'onglet **Augmenter les demandes**.
    
3.  Cliquez sur **Filtrer** pour interroger vos demandes d'augmentation de quota selon une propriété spécifique.
    

Lorsqu'une organisation ou un dossier est sélectionné, la page affiche toutes les demandes d'augmentation de quota pour tous les projets de l'organisation ou du dossier.

### Créer un remplacement de quota

Pour limiter l'utilisation d'une ressource particulière, créez un _remplacement de quota_ en remplaçant la valeur du quota par une valeur inférieure à celle par défaut. La création d'un remplacement de quota est parfois appelée _limitation de l'utilisation_.

Pour créer un remplacement de quota, suivez la procédure permettant de [modifier une valeur de quota](#requesting_higher_quota).

Les forçages de quota ne sont pas disponibles pour tous les services. Pour obtenir des informations spécifiques à un service, consultez la documentation sur les quotas et les limites du système pour votre service.

### Réinitialiser la valeur d'un quota

Pour réinitialiser la valeur du quota après avoir appliqué un remplacement, procédez comme suit :

### Console

1.  Dans la console Google Cloud , accédez à la page **IAM et administration > Quotas et limites du système** :
    
    [Accéder à la page Quotas et limites du système](https://console.cloud.google.com/iam-admin/quotas?hl=fr)
    
2.  Saisissez du texte dans le champ de recherche **Filtre** pour rechercher votre quota. Définissez **Comporte un remplacement : Vrai** pour n'afficher que les quotas comportant un remplacement.
    
3.  Sur la page "Quotas", **Remplacement** s'affiche à côté des limites de quota associées à un remplacement. Cliquez sur **Remplacer** pour afficher les détails de ce quota ainsi qu'une option pour réinitialiser la valeur.
    
4.  Pour confirmer qu'une réinitialisation supprime vos remplacements, cochez la case précédant l'option **Réinitialiser la valeur**, puis cliquez sur **Réinitialiser la valeur** pour réinitialiser le quota.
    

Pour réinitialiser un quota manuellement, suivez la même procédure que pour [demander une augmentation de limite de quota](#requesting_higher_quota). Assurez-vous que la **Nouvelle valeur** que vous définissez est égale à la **Valeur par défaut** affichée sous le nom du quota dans le formulaire **Modifications de quota**.

Gérer les quotas à l'aide de l'API Cloud Quotas
-----------------------------------------------

Vous pouvez utiliser l'API Cloud Quotas pour obtenir des informations sur les quotas actuels et définir les préférences de quotas pour les API et les services Google Cloud . Pour en savoir plus, consultez les ressources suivantes :

*   Pour obtenir une présentation, consultez [Présentation de l'API Cloud Quotas](https://docs.cloud.google.com/docs/quotas/api-overview?hl=fr).
*   Pour savoir comment configurer votre environnement de développement, consultez la section [Configurer l'API Cloud Quotas](https://docs.cloud.google.com/docs/quotas/development-environment?hl=fr).
*   Pour obtenir des exemples d'utilisation de l'API Cloud Quotas pour ajuster des quotas et automatiser les ajustements de quotas dans vos projets, dossiers ou organisations Google Cloud , consultez la section [Mettre en œuvre des cas d'utilisation courants](https://docs.cloud.google.com/docs/quotas/implement-common-use-cases?hl=fr).

Gérer les quotas à l'aide de la gcloud CLI
------------------------------------------

Pour utiliser la gcloud CLI, assurez-vous d'avoir [installé](https://docs.cloud.google.com/sdk/docs/install?hl=fr) et [initialisé](https://docs.cloud.google.com/sdk/docs/initialize?hl=fr) la dernière version de la gcloud CLI, y compris le composant de [commandes bêta](https://docs.cloud.google.com/sdk/docs/components?hl=fr#alpha_and_beta_components). Si vous utilisez Cloud Shell pour interagir avec Google Cloud, la gcloud CLI est installée automatiquement.

Pour plus d'informations, consultez les sections suivantes :

*   Pour obtenir des exemples de commandes `gcloud quotas info` et `gcloud quotas preferences`, consultez la section [Utiliser la gcloud CLI pour afficher et gérer les quotas](https://docs.cloud.google.com/docs/quotas/gcloud-cli-examples?hl=fr).
    
*   Pour obtenir la liste complète des commandes et des options `gcloud quotas`, consultez la section [Quotas gcloud](https://docs.cloud.google.com/sdk/gcloud/reference/quotas?hl=fr) de la documentation de référence de Google Cloud CLI.
    
    *   [gcloud quotas info](https://docs.cloud.google.com/sdk/gcloud/reference/quotas/info?hl=fr)
    *   [Préférences de quotas gcloud](https://docs.cloud.google.com/sdk/gcloud/reference/quotas/preferences?hl=fr)

Demander un quota de projet
---------------------------

Pour en savoir plus sur la demande de _quotas de projet_ supplémentaires, consultez l'article d'assistance [Demandes de quota de projet](https://support.google.com/cloud/answer/6330231?hl=fr).

Étapes suivantes
----------------

*   Configurez l'[outil d'ajustement de quota](https://docs.cloud.google.com/docs/quotas/quota-adjuster?hl=fr) pour qu'il surveille et envoie des demandes d'ajustement de quota à votre place.
*   Utilisez l'[API Cloud Quotas](https://docs.cloud.google.com/docs/quotas/api-overview?hl=fr) pour gérer vos quotas au niveau du projet.
*   Contrôlez l'accès à l'aide d'[IAM](https://docs.cloud.google.com/docs/quotas/permissions?hl=fr) ou de [VPC Service Controls](https://docs.cloud.google.com/docs/quotas/configure-vpc-service-controls?hl=fr).
*   Configurez votre [projet de quota](https://docs.cloud.google.com/docs/quotas/quota-project?hl=fr).
*   [Surveillez](https://docs.cloud.google.com/docs/quotas/monitor?hl=fr) et [résolvez les problèmes](https://docs.cloud.google.com/docs/quotas/troubleshoot?hl=fr) liés aux quotas.
