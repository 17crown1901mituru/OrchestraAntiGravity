# Zugriff auf regionale und multiregionale Google APIs über private Endpunkte &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/about-accessing-regional-google-apis-endpoints

Auf dieser Seite finden Sie eine Übersicht darüber, wie Sie [Private Service Connect-Endpunkte verwenden, um auf regionale und multiregionale Endpunkte](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de) für [unterstützte Google APIs](https://docs.cloud.google.com/vpc/docs/regional-service-endpoints?hl=de) zuzugreifen.

Verwenden Sie regionale oder multiregionale Endpunkte, wenn Sie dafür sorgen möchten, dass Daten bei der Übertragung in einer bestimmten Region oder Multiregion bleiben. Weitere Informationen finden Sie unter [Übersicht über denGoogle Cloud -API-Endpunkt](https://docs.cloud.google.com/docs/security/compliance/endpoints?hl=de).

Informationen zu anderen Private Service Connect-Konfigurationen finden Sie unter [Private Service Connect – Übersicht](https://docs.cloud.google.com/vpc/docs/private-service-connect?hl=de).

Features und Kompatibilität
---------------------------

In dieser Tabelle sind die Features zusammengefasst, die von Endpunkten unterstützt werden, die für den Zugriff auf regionale Endpunkte für Google APIs verwendet werden.

| Konfiguration | Details |
| --- | --- |
| **Nutzerkonfiguration (Endpunkt)** |
| Globale Reichweite | [Wenn der globale Zugriff aktiviert ist](https://docs.cloud.google.com/vpc/docs/about-accessing-regional-google-apis-endpoints?hl=de#global-access) |
| [Cloud Interconnect-Traffic](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#hybrid-access) |  |
| [Cloud VPN-Traffic](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#hybrid-access) |  |
| [Zugriff über VPC-Netzwerk-Peering](https://docs.cloud.google.com/vpc/docs/vpc-peering?hl=de) |  |
| [Verbindungsweitergabe über NCC](https://docs.cloud.google.com/vpc/docs/about-propagated-connections?hl=de) | Nur IPv4 |
| DNS-Konfiguration | [Manuelle DNS-Konfiguration](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#configure-dns) |
| IP-Version | IPv4 oder IPv6 |
| **Ersteller** |
| Unterstützte Dienste | [Unterstützte regionale Google APIs](https://docs.cloud.google.com/vpc/docs/regional-service-endpoints?hl=de) |

Spezifikationen
---------------

*   Öffentliche Hostnamen für regionale Endpunkte haben die folgenden Formate: `SERVICE.REGION.rep.DOMAIN`.
*   Öffentliche Hostnamen für multiregionale Endpunkte haben das folgende Format: `SERVICE.MULTI_REGION.rep.DOMAIN`.
    
    Wir empfehlen, die öffentlichen Hostnamen zu verwenden, um den Zieldienst in Ihrer Private Service Connect-Endpunktkonfiguration anzugeben.
    
*   Das Subnetz, das Sie beim Erstellen eines Endpunkts angeben, ist ein [reguläres Subnetz](https://docs.cloud.google.com/vpc/docs/subnets?hl=de#purpose). Die dem Endpunkt zugewiesene IP-Adresse ist eine regionale interne IP-Adresse.
    
*   Auf Endpunkte kann standardmäßig nur von Clients zugegriffen werden, die sich in derselben Region und demselben VPC-Netzwerk (oder freigegebene VPC-Netzwerk) wie der Endpunkt befinden. Informationen zum Bereitstellen von Endpunkten in anderen Regionen finden Sie unter [Globaler Zugriff](#global-access).
    
*   Informationen zum Erstellen dieser Konfiguration in einem freigegebene VPC-Netzwerk finden Sie unter [Gemeinsam genutzte VPC](#shared-vpc).
    

Architektur
-----------

Private Service Connect-Endpunkte mit einem regionalen oder multiregionalen Endpunktziel verweisen auf einen Dienstanhang, der von Google erstellt wurde, um Traffic an den Dienst weiterzuleiten.

*   **Regionale Endpunkte:** Der Traffic wird an den regionalen Dienstendpunkt in derselben Region wie der Private Service Connect-Endpunkt weitergeleitet.
*   **Multiregionale Endpunkte:** Der Private Service Connect-Endpunkt ist regional (erstellt in einer bestimmten Region, z. B. `us-central1`), aber das Ziel ist multiregional (z. B. `us`). Der Traffic vom regionalen Private Service Connect-Endpunkt wird per Load Balancing an ein regionales Frontend weitergeleitet. Das regionale Frontend kann sich in einer der Regionen befinden, die dem multiregionalen Ziel zugeordnet sind.

Clients in derselben Region wie der Endpunkt können Traffic an den Endpunkt senden. Sie können auch über [verbundene Netzwerke](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#hybrid-access) in derselben Region auf den Endpunkt zugreifen. Wenn Sie von anderen Regionen aus auf den Endpunkt zugreifen möchten, konfigurieren Sie den [globalen Zugriff](#global-access).

[](https://docs.cloud.google.com/static/vpc/images/psc-endpoint-service-rep.svg?hl=de)

**Abbildung 1.** Mit einem Endpunkt können Dienstnutzer Traffic vom VPC-Netzwerk des Nutzers über einen von Google verwalteten Dienstanhang an regionale Dienstendpunkte für unterstützte Google APIs senden (zum Vergrößern klicken).

Globaler Zugriff
----------------

Wenn Sie einen Endpunkt erstellen, können Sie den globalen Zugriff konfigurieren. Mit dem globalen Zugriff können Clients in anderen Regionen auf den Endpunkt zugreifen. Der Endpunkt ist auch über [verbundene Netzwerke](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#hybrid-access) zugänglich.

[](https://docs.cloud.google.com/static/vpc/images/psc-multi-region-rep.svg?hl=de)

**Abbildung 2.** Auf einen Endpunkt mit aktiviertem globalen Zugriff können Clients in einer anderen Region zugreifen, einschließlich Clients in verbundenen Netzwerken (zum Vergrößern anklicken).

Unterstützte Regionen und Dienste
---------------------------------

Eine Liste der unterstützten Regionen und Dienste finden Sie unter [Regionale Dienstendpunkte](https://docs.cloud.google.com/vpc/docs/regional-service-endpoints?hl=de).

DNS-Konfiguration und -Auflösung
--------------------------------

Wenn Sie einen Private Service Connect-Endpunkt erstellen, der auf einen regionalen oder multiregionalen Endpunkt abzielt, müssen Sie [einen DNS-Eintrag in einer privaten DNS-Zone erstellen](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#configure-dns), um Anfragen an ihn zu senden. Wenn Sie beispielsweise einen Private Service Connect-Endpunkt erstellen, der auf `storage.us-east1.rep.googleapis.com` verweist, müssen Sie einen privaten DNS-Eintrag für den Hostnamen `storage.us-east1.rep.googleapis.com` erstellen, der auf die IP-Adresse Ihres Private Service Connect-Endpunkts verweist.

Wenn ein Client nach `storage.us-east1.rep.googleapis.com` sucht und dieser Hostname nicht in einer privaten DNS-Zone vorhanden ist, wird die Abfrage an die öffentlichen DNS-Server von Google weitergeleitet. Die öffentlichen DNS-Server antworten mit der [öffentlichen IP-Adresse des regionalen oder multiregionalen Endpunkts](https://docs.cloud.google.com/docs/security/compliance/about-regional-endpoints?hl=de#public-access), _nicht_ mit der internen IP-Adresse des Private Service Connect-Endpunkts.

Wenn Sie von Clients in Hybridnetzwerken auf den Private Service Connect-Endpunkt zugreifen, ist eine zusätzliche Konfiguration erforderlich, damit die Hybridclients den Hostnamen des Private Service Connect-Endpunkts korrekt auflösen können. Weitere Informationen finden Sie unter [Zugriff auf Endpunkte über Hybridnetzwerke](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#hybrid-access).

[Dienstprojektadministratoren](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=de#svc_proj_admins) können Private Service Connect-Endpunkte in Dienstprojekten mit freigegebene VPC erstellen, die IP-Adressen aus [gemeinsam genutzten VPC-Netzwerken](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=de#shared_vpc_networks) verwenden.

Im Allgemeinen empfehlen wir, Weiterleitungsregeln und die entsprechenden Adressressourcen im selben Google Cloud -Projekt zu erstellen. Wenn Sie dasselbe Projekt verwenden, können Sie Probleme vermeiden, die beim Löschen eines Projekts auftreten können.

Wenn Sie einen Endpunkt in einem Dienstprojekt erstellen und eine reservierte statische IP-Adresse in der Konfiguration verwenden, verwenden oder erstellen Sie eine Adressressource im Dienstprojekt. Die Adressressource kann auf eine IP-Adresse aus einem Subnetz im Hostprojekt oder aus dem Dienstprojekt verweisen, wenn es Subnetze enthält.

Wir empfehlen _nicht_, eine Adressressource aus einem Hostprojekt zu verwenden, um einen Endpunkt in einem Dienstprojekt zu erstellen.

Sie müssen die gcloud CLI oder die API verwenden, um einen Endpunkt in einem Dienstprojekt zu erstellen. Für diese Konfiguration sind [zusätzliche Berechtigungen](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#roles-shared-vpc) erforderlich.

Weitere Informationen finden Sie unter [Regionalen Private Service Connect-Endpunkt in einem Dienstprojekt mit freigegebene VPC erstellen](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=de#shared-vpc).

Preise
------

Preisinformationen finden Sie unter [Virtual Private Cloud-Preise](https://cloud.google.com/vpc/pricing?hl=de#psc-forwarding-rule-regional-end-apis).

Kontingente
-----------

Weitere Informationen finden Sie unter [Kontingente und Limits](https://docs.cloud.google.com/vpc/docs/quota?hl=de#regional-per-project-regional-endpoints) im Abschnitt **Anzahl der regionalen Endpunkte pro Projekt und Region**.

*   Erstellen Sie einen Private Service Connect-Endpunkt, um [auf regionale oder multiregionale Google APIs zuzugreifen](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=de).
*   [Weitere Informationen zu Private Service Connect](https://docs.cloud.google.com/vpc/docs/private-service-connect?hl=de)
