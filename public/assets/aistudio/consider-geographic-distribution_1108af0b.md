# Tenir compte de la répartition géographique &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/consider-geographic-distribution

Avant de créer des ressources, réfléchissez à la manière dont vous prévoyez de les répartir géographiquement pour répondre aux exigences spécifiques de votre entreprise. Les administrateurs et les architectes de votre organisation prennent généralement des décisions concernant la répartition géographique et les rendent disponibles pour les personnes qui déploient des ressources. Par exemple, votre entreprise peut avoir un processus [IaC (Infrastructure as Code)](https://docs.cloud.google.com/docs/terraform/iac-overview?hl=fr) qui attribue automatiquement des zones géographiques lorsque vous déployez des ressources.

Ce document offre un aperçu de l'impact de la zone géographique sur vos charges de travail.

Répartir les ressources pour garantir la disponibilité
------------------------------------------------------

Vous pouvez répartir géographiquement les ressources pour répondre à vos exigences uniques, comme dans les exemples suivants :

*   Latence : assurez-vous de disposer de ressources dans des zones à proximité de vos utilisateurs.
*   Disponibilité : créez des ressources redondantes dans plusieurs régions en cas de défaillance régionale.

Régions et zones
----------------

Lorsque vous créez des ressources, vous pouvez sélectionner les catégories géographiques suivantes :

*   Les _régions_ correspondent à des espaces géographiques indépendants qui contiennent des zones. Par exemple, `asia-east1` (Taïwan).
    
*   Les _zones_ sont des zones isolées les unes des autres au sein d'une région. Par exemple, la zone `a` de la région `asia-east1` (Taïwan) s'appelle `asia-east1-a`.
    

Considérez une zone comme un domaine de défaillance unique au sein d'une région. Pour déployer des applications tolérantes aux pannes et à haute disponibilité, et être mieux protégé contre les défaillances inattendues, déployez vos applications sur plusieurs zones d'une région. Pour en savoir plus, consultez la page [Zones géographiques et régions](https://docs.cloud.google.com/docs/geography-and-regions?hl=fr).

Chaque ressource possède sa propre dynamique d'emplacement. Par exemple, consultez les détails suivants sur Compute Engine et Cloud Storage :

*   [Régions et zones Compute Engine](https://docs.cloud.google.com/compute/docs/regions-zones?hl=fr)
*   [Emplacements des buckets Cloud Storage](https://docs.cloud.google.com/storage/docs/bucket-locations?hl=fr)

### Sélectionner des zones géographiques en fonction des interactions avec les ressources

Lorsque vous créez votre plan de distribution des ressources, tenez compte de la communication des ressources entre les zones et les régions. Les capacités d'interaction avec les ressources sont déterminées par les types de ressources suivants :

*   Vous pouvez accéder à des _ressources globales_ depuis n'importe quelle autre ressource des régions et des zones. Exemples : images disque, instantanés de disque et réseaux.
    
*   Les _ressources régionales_ sont déployées de façon redondante dans plusieurs zones d'une même région. Les ressources régionales ne sont accessibles que depuis des ressources situées dans la même région. Il peut s'agir, par exemple, d'applications App Engine ou de [groupes d'instances gérés régionaux](https://docs.cloud.google.com/compute/docs/instance-groups?hl=fr#types_of_managed_instance_groups).
    
*   Les services _multirégionaux_ sont distribués de manière redondante au sein des régions et entre elles. Ces services contribuent à optimiser la disponibilité, les performances et l'efficacité des ressources. Pour obtenir la liste des services disposant d'un ou de plusieurs emplacements multirégionaux, consultez la section [Produits disponibles par emplacement](https://cloud.google.com/about/locations?hl=fr#regional-products).
    
*   Les _ressources zonales_ ne sont accessibles que par les ressources situées dans la même zone. Une instance de machine virtuelle (VM) Compute Engine est un exemple de ressource zonale.
    

Par exemple, considérons les ressources suivantes :

*   Globalement: réseau accessible à toutes les ressources.
*   Dans chaque région : adresses IP qui ne fournissent un accès externe aux ressources que d'une seule région.
*   Dans chaque zone : disques pouvant se connecter à des VM situées dans la même zone
