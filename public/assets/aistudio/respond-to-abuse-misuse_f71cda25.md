# Répondre aux notifications et avertissements d'utilisation abusive dans Google Cloud &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/respond-to-abuse-misuse

Pour assurer la sécurité des systèmes Google Cloud et de nos clients, nous nous efforçons de garantir que nos produits sont utilisés de la manière prévue et que notre plate-forme n'est pas utilisée de manière abusive ou incorrectement. Comme indiqué dans l'[Avis de confidentialité de Google Cloud](https://cloud.google.com/terms/cloud-privacy-notice?hl=fr), nous nous efforçons d'assurer la protection contre les violations définies dans les [Conditions d'utilisation](https://cloud.google.com/terms/?hl=fr) et le [Règlement sur l'utilisation acceptable](https://cloud.google.com/terms/aup?hl=fr).

Voici quelques exemples d'utilisation abusive :

*   Identifiants de compte de service potentiellement compromis
*   Clés API potentiellement compromises
*   Alertes sur les cryptomonnaies
*   Logiciels malveillants ou indésirables
*   Hameçonnage

Google Cloud dispose d'une équipe dédiée d'ingénieurs et d'experts en sécurité qui s'efforcent de protéger nos systèmes et nos clients. Lorsque Google prend connaissance d'une activité abusive, nous en informons les clients concernés et prenons des mesures pour éviter de futurs abus. Nous nous efforçons de faire en sorte que nos interventions n'aient pas d'impact sur votre travail critique. Pour en savoir plus, consultez les [Consignes concernant la suspension des projets](https://docs.cloud.google.com/resource-manager/docs/project-suspension-guidelines?hl=fr).

Cette page explique ce que vous pouvez faire si vous recevez une notification de notre part concernant un cas d'utilisation abusive.

Répondre à une notification d'utilisation abusive
-------------------------------------------------

Si vous recevez une notification ou un avertissement pour abus, vous devez rapidement corriger les violations mentionnées dans la notification. Vous devez également consulter les conditions d'utilisation et le Règlement sur l'utilisation acceptable.

Vous pouvez consulter vos [journaux d'utilisation abusiveGoogle Cloud](https://docs.cloud.google.com/logging/docs/view/logs-viewer-interface?hl=fr) et résoudre les problèmes liés à votre environnement à l'aide des outils de diagnostic inclus dansGoogle Cloud (comme Security Command Center).

Si vous n'arrivez pas à accéder à votre compte, consultez [Compte bloqué](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=fr#account-lockouts).

Exemples de problèmes et de réponses
------------------------------------

Cette section inclut des exemples décrivant comment résoudre les problèmes qui ont pu déclencher une alerte et y répondre. Si vous ne parvenez pas à résoudre le problème vous-même et que vous disposez d'une [formule Cloud Customer Care](https://docs.cloud.google.com/support?hl=fr), contactez le [service client](https://enterprise.google.com/supportcenter/managecases?hl=fr). Pour vous aider à résoudre les problèmes, vous pouvez également consulter le [forum de la communautéGoogle Cloud](https://www.googlecloudcommunity.com/gc/Google-Cloud/ct-p/google-cloud) .

### Identifiants de compte de service potentiellement compromis

Une alerte en cas de fuite d'identifiants indique que votre organisation a peut-être publié par inadvertance les identifiants de compte de service spécifiés dans des dépôts ou des sites Web publics.

Pour résoudre ce problème, procédez comme suit :

1.  Dans la console Google Cloud , examinez l'activité de votre compte.
    
    [Accéder à Google Dashboard](https://console.cloud.google.com/home/dashboard?hl=fr)
    
2.  Révoquez tous les identifiants des comptes de service piratés. Effectuez une rotation de tous les identifiants dans les projets concernés, car toutes les ressources accessibles au compte de service peuvent avoir été affectées. Pour obtenir des instructions, consultez [Gérer des identifiants Google Cloud dont la sécurité a été compromise](https://cloud.google.com/security/compromised-credentials?hl=fr).
    
3.  Supprimez toutes les machines virtuelles ou ressources non autorisées.
    
4.  Vérifiez que les identifiants de votre compte de service ne sont pas intégrés dans des dépôts publics, enregistrés dans des répertoires de téléchargement ni partagés involontairement d'autres façons.
    

Pour protéger votre organisation contre les identifiants piratés, consultez [Bonnes pratiques pour éviter les problèmes liés aux identifiants piratés](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=fr#best_practices_to_avoid_compromised_credentials).

### Clés API potentiellement compromises

Une alerte en cas de clé API compromise détectée indique que votre organisation a peut-être publié par inadvertance la clé API concernée dans des dépôts ou des sites Web publics.

Pour résoudre ce problème, procédez comme suit :

1.  Si cette clé est censée être publique, procédez comme suit :
    
    1.  Dans la console Google Cloud , examinez l'activité de facturation et de l'API sur votre compte. Vérifiez que l'utilisation et la facturation correspondent à vos attentes.
        
        [Accéder à Google Dashboard](https://console.cloud.google.com/home/dashboard?hl=fr)
        
    2.  Le cas échéant, ajoutez des [restrictions à votre clé API](https://docs.cloud.google.com/docs/authentication/api-keys?hl=fr#api_key_restrictions).
        
2.  Si cette clé n'est pas censée être publique, procédez comme suit :
    
    1.  Dans la console Google Cloud , générez une clé API. Pour obtenir des instructions, consultez [Regénérer les clés API](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=fr#regenerate_api_keys).
    2.  Vérifiez que vos clés API ne sont pas intégrées dans des dépôts publics, enregistrées dans des répertoires de téléchargement ni partagées involontairement d'autres façons.
    3.  Le cas échéant, ajoutez des [restrictions à votre clé API](https://docs.cloud.google.com/docs/authentication/api-keys?hl=fr#api_key_restrictions).
    4.  Si vous utilisez les API Google Maps, consultez les [Conseils de sécurité pour Google Maps Platform](https://developers.google.com/maps/api-security-best-practices?hl=fr).

Pour protéger votre organisation contre les identifiants piratés, consultez [Bonnes pratiques pour éviter les problèmes liés aux identifiants piratés](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=fr#best_practices_to_avoid_compromised_credentials).

### Minage de cryptomonnaie

Cette alerte indique qu'un projet est utilisé pour miner de la cryptomonnaie. Ce problème est généralement précédé d'une compromission, telle qu'une fuite d'identifiants de compte de service, qui accorde à une personne malintentionnée l'accès à votre projet Google Cloud .

Pour résoudre ce problème, procédez comme suit :

1.  Dans la console Google Cloud , examinez l'activité du projet.
    
    [Accéder à l'explorateur de journaux](https://console.cloud.google.com/logs/query?hl=fr)
    
2.  Mettez fin à toute activité de minage de cryptomonnaie non autorisée et prenez des mesures pour sécuriser votre compte et les projets concernés.
    
3.  Si vous avez des ressources suspendues, vous pouvez [faire appel](#submit-appeal) pour récupérer l'accès.
    

Pour protéger votre organisation contre les attaques de minage de cryptomonnaie, consultez [Atténuer les attaques de minage de cryptomonnaie sur Google Cloud](https://docs.cloud.google.com/docs/security/bps-for-protecting-against-cryptocurrency-attacks?hl=fr).

### Logiciels malveillants ou indésirables

Cette alerte indique que votre organisation inclut un projet qui héberge, distribue ou facilite la distribution de [logiciels malveillants, de logiciels indésirables ou de virus](https://developers.google.com/search/docs/advanced/security/malware?hl=fr). Pour résoudre ce problème, procédez comme suit :

1.  Supprimez tout contenu et mécanisme malveillant de vos projets.
    
    [Accéder à l'explorateur de journaux](https://console.cloud.google.com/logs/query?hl=fr)
    
2.  Vérifiez que votre projet n'a pas été piraté en consultant son utilisation et ses journaux.
    
3.  Si nécessaire, [arrêtez (supprimez) votre projet](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=fr#shutting_down_projects).
    
4.  Pour récupérer l'accès à vos ressources suspendues, [faites appel](#submit-appeal).
    

Pour protéger votre organisation contre les logiciels malveillants ou indésirables, consultez [Atténuer les attaques de rançongiciels à l'aide de Google Cloud](https://docs.cloud.google.com/docs/security/mitigating-ransomware-attacks?hl=fr).

Si un avertissement rouge s'affiche dans le navigateur pour votre site, cela signifie que le [programme de navigation sécurisée](https://safebrowsing.google.com/?hl=fr) de Google l'a identifié comme malveillant. La navigation sécurisée fonctionne indépendamment de Google Cloud. Vous pouvez [envoyer une demande d'examen](https://developers.google.com/search/docs/monitor-debug/security/malware?hl=fr#fixing-the-problem) de la page à l'aide de la Search Console. Pour en savoir plus, consultez [Google Search Console](http://search.google.com/search-console?hl=fr) et [Premiers pas avec la Search Console](https://developers.google.com/search/docs/monitor-debug/search-console-start?hl=fr).

### Hameçonnage

Cette alerte indique qu'un contenu [de hameçonnage ou d'ingénierie sociale trompeuse](https://developers.google.com/search/docs/monitor-debug/security/social-engineering?hl=fr) a été publié à partir de votre projet Google Cloud . Les pirates informatiques peuvent essayer de prendre le contrôle de votre site et de l'utiliser pour héberger du contenu trompeur.

Pour résoudre ce problème, procédez comme suit :

1.  Supprimez de vos projets tout le contenu et les mécanismes de hameçonnage.
2.  Vérifiez que votre projet n'a pas été piraté en consultant son utilisation et ses journaux.
3.  Si nécessaire, [arrêtez (supprimez) votre projet](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=fr#shutting_down_projects).
4.  Pour récupérer l'accès à vos ressources suspendues, [faites appel](#submit-appeal).

Si un avertissement rouge s'affiche dans le navigateur pour votre site, cela signifie que le [programme de navigation sécurisée](https://safebrowsing.google.com/?hl=fr) de Google l'a identifié comme malveillant. La navigation sécurisée fonctionne indépendamment de Google Cloud. Vous pouvez [envoyer une demande d'examen](https://developers.google.com/search/docs/monitor-debug/security/malware?hl=fr#fixing-the-problem) de la page à l'aide de la Search Console. Pour en savoir plus, consultez [Google Search Console](http://search.google.com/search-console?hl=fr) et [Premiers pas avec la Search Console](https://developers.google.com/search/docs/monitor-debug/search-console-start?hl=fr).

Faire appel
-----------

Vous pouvez faire appel auprès de Google Cloud après avoir reçu une notification d'avertissement ou de suspension et suivi les étapes de correction pour restaurer l'accès aux services.

Pour faire appel, dans la console Google Cloud , sélectionnez le projet et accédez à la page **Appels** du projet. Assurez-vous que votre réponse inclut les éléments suivants :

*   La cause du problème
*   Les mesures que vous avez prises pour résoudre le problème
*   Si le comportement était intentionnel ou non.
*   Votre [ID de compte de facturation](https://docs.cloud.google.com/billing/docs/how-to/find-billing-account-id?hl=fr).
*   Indique si votre projet a été compromis.

Si vous voyez un message d'erreur vous indiquant que vous ne disposez pas des autorisations nécessaires pour accéder à la page, vérifiez que vous êtes connecté en tant que propriétaire du projet et que vous disposez des [autorisations IAM appropriées pour modifier le projet](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=fr). Si vous êtes connecté à plusieurs comptes, déconnectez-vous de tous les autres comptes, puis réessayez de vous connecter.

Une fois votre demande d'appel envoyée, Google Cloud l'examine et vous répond en indiquant une résolution et une décision finale.

Signaler une utilisation abusive suspectée
------------------------------------------

La protection de vos données et de vos charges de travail est une [responsabilité partagée](https://docs.cloud.google.com/architecture/framework/security/shared-responsibility-shared-fate?hl=fr). Vous devez surveiller activement votre environnement Google Cloud et implémenter les contrôles de sécurité et les bonnes pratiques appropriés. Si vous pensez que vos servicesGoogle Cloud font l'objet d'une utilisation abusive, prenez immédiatement des mesures pour sécuriser les services et comptes concernés, et signalez l'utilisation abusive au [service client Google Cloud](https://support.cloud.google.com/portal/?hl=fr). Comme décrit dans les conditions d'utilisation, nous examinons les signalements de débits financiers non autorisés liés à un usage abusif ou à une compromission. Vous êtes financièrement responsable des frais résultant de ressources mal sécurisées.

Pour signaler un problème qui n'est pas lié à vos services, utilisez le formulaire [Signaler un abus présumé sur Google Cloud](https://support.google.com/code/contact/cloud_platform_report?hl=fr).

Bonnes pratiques pour vous protéger contre les utilisations abusives
--------------------------------------------------------------------

Pour vous protéger contre les utilisations abusives sur Google Cloud, tenez compte des éléments suivants :

*   Utilisez des mots de passe sécurisés et activez l'authentification à deux facteurs pour vos comptesGoogle Cloud . Pour en savoir plus, consultez [Authentification unique](https://docs.cloud.google.com/architecture/identity/single-sign-on?hl=fr).
    
*   Faites attention aux applications tierces auxquelles vous accordez l'accès à vos ressources Google Cloud et à la méthode d'authentification qu'elles utilisent. Pour en savoir plus sur la sécurisation des applications, consultez [Utiliser IAM en toute sécurité](https://docs.cloud.google.com/iam/docs/using-iam-securely?hl=fr) et [Méthodes d'authentification chez Google](https://docs.cloud.google.com/docs/authentication?hl=fr).
    
*   Surveillez les logiciels tiers pour vous assurer que votre projet n'est pas compromis par des failles dans les logiciels tiers que vous avez installés. Pour en savoir plus sur les bonnes pratiques de sécurité, consultez la section [Sécuriser les instances](https://support.google.com/cloud/answer/6262505?hl=fr#gcp-instance) des questions fréquentes sur la sécurité dans le cloud.
    
*   Si votre activité principale consiste à héberger des contenus ou services tiers, ou à faciliter la vente de biens et services entre tiers, veillez à respecter la Google Cloud Politique d'utilisation autorisée. Implémentez les éléments suivants :
    
    *   Publiez des règles qui définissent le contenu interdit sur votre plate-forme.
    *   Mettez en place un processus de réception des signalements (par exemple, un formulaire Web ou un alias d'adresse e-mail) pour recevoir les notifications de contenu illégal ou abusif (en plus d'un canal de communication surveillé pour Google).
    *   Examinez rapidement les alertes et prenez les mesures nécessaires. Supprimez le contenu si nécessaire.
*   Implémentez la journalisation et les contrôles de détection, puis [surveillez vos Google Cloud journaux](https://docs.cloud.google.com/logging/docs/alerting/monitoring-logs?hl=fr) pour détecter toute activité suspecte. Pour en savoir plus, consultez les ressources suivantes :
    
    *   [Cloud Audit Logging](https://docs.cloud.google.com/logging/docs/audit?hl=fr)
        
    *   [Journaux de la plate-formeGoogle Cloud](https://docs.cloud.google.com/logging/docs/api/platform-logs?hl=fr)
        
    *   [Présentation de Network Intelligence Center](https://docs.cloud.google.com/network-intelligence-center/docs/overview?hl=fr)
        
    *   [Présentation de la protection des données sensibles](https://docs.cloud.google.com/sensitive-data-protection/docs/sensitive-data-protection-overview?hl=fr)
        
*   Utilisez [Security Command Center](https://docs.cloud.google.com/security-command-center/docs/security-command-center-overview?hl=fr) pour identifier les failles dans votre environnement et les corriger.
    
*   Ajoutez des contacts aux [catégories de contacts essentiels](https://docs.cloud.google.com/resource-manager/docs/managing-notification-contacts?hl=fr#notification-categories) suivantes pour vos projets afin d'être averti dès que votre projet fait l'objet d'un avertissement :
    
    *   Sécurité
    *   Technique
    *   Suspension
    *   Tous
*   Assurez-vous que les e-mails de `google-cloud-compliance@google.com` ne sont pas envoyés dans le dossier de spam.
