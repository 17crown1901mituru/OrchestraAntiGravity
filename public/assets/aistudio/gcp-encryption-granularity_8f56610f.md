# Standardverschlüsselung inaktiver Daten &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/encryption/gcp-encryption-granularity

_Der Inhalt dieses Dokuments wurde im Mai 2024 zum letzten Mal aktualisiert und stellt den Stand zum Zeitpunkt der Erstellung dar. Die Sicherheitsrichtlinien und -systeme von Google können sich aber in Zukunft ändern, da wir den Schutz unserer Kundinnen und Kunden kontinuierlich verbessern._

Bei Google umfasst unsere umfassende Sicherheitsstrategie die Verschlüsselung ruhender Daten, um Kundendaten vor Angreifern zu schützen. Wir verschlüsseln alle Google Kundeninhalte mit einem oder mehreren Verschlüsselungsverfahren, ohne dass Sie etwas tun müssen. In diesem Dokument wird unser Ansatz für die Standardverschlüsselung ruhender Daten für die Google-Infrastruktur und Google Clouderläutert. Außerdem wird erläutert, wie wir diese Methode verwenden, um Kundeninhalte sicherer zu machen.

Dieses Dokument richtet sich an Sicherheitsarchitekten und Sicherheitsteams, die Google derzeit verwenden bzw. dies beabsichtigen. In diesem Dokument werden Grundkenntnisse in der [Verschlüsselung](https://wikipedia.org/wiki/Encryption) und der [kryptografischen Primitive](https://wikipedia.org/wiki/Cryptographic_primitive) vorausgesetzt. Weitere Informationen zu Kryptografie finden Sie unter [Einführung in die moderne Kryptografie](https://www.cs.umd.edu/%7Ejkatz/imc.html).

Die Verschlüsselung ruhender Daten dient zum Schutz der Daten, die auf einem Laufwerk (z. B. SSD) oder auf Sicherungsmedien gespeichert sind. Alle von Google gespeicherten Daten werden auf der Speicherebene mit dem AES-Algorithmus (Advanced Encryption Standard) AES-256 verschlüsselt. Wir verwenden eine gemeinsame kryptografische Bibliothek namens Tink, die unser gemäß FIPS 140-2 validiertes Modul [_BoringCrypto_](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=de) enthält, um die Verschlüsselung in Google Cloudeinheitlich zu implementieren.

Wir besitzen und verwalten die Schlüssel, die in der Standardverschlüsselung ruhender Daten verwendet werden. Wenn SieGoogle Cloudverwenden, können Sie mit dem Cloud Key Management Service eigene Verschlüsselungsschlüssel erstellen, mit denen Sie Ihren Daten eine Umschlagverschlüsselung hinzufügen können. Mit Cloud KMS können Sie Schlüssel erstellen, rotieren, verfolgen und löschen. Weitere Informationen finden Sie unter [Cloud Key Management Service im Detail](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=de).

Schlüssel in Google Cloud
-------------------------

In der folgenden Tabelle werden die verschiedenen Attribute von Schlüsseln in Google Cloudbeschrieben.

| Schlüsseltyp | Cloud KMS Autokey | Cloud KMS (vom Kunden verwaltet, manuell) | Google-owned and Google-managed encryption key (Google-Standardverschlüsselung) |
| --- | --- | --- | --- |
| 
Darf wichtige Metadaten ansehen

 | 

Ja

 | 

Ja

 | 

Nein

 |
| 

Eigentum an Schlüsseln[1](#footnote1)

 | 

Kunde

 | 

Kunde

 | 

Google

 |
| 

Kann [2](#footnote2)\-Schlüssel verwalten und [3](#footnote3)\-Schlüssel steuern

 | 

Die Erstellung und Zuweisung von Schlüsseln erfolgt automatisch. Die manuelle Steuerung durch den Kunden wird vollständig unterstützt.

 | 

Kunde, nur manuelle Steuerung

 | 

Google

 |
| 

Unterstützt gesetzliche Anforderungen für kundenverwaltete Schlüssel

 | 

Ja

 | 

Ja

 | 

Nein

 |
| 

Schlüsselfreigabe

 | 

Eindeutig für einen Kunden

 | 

Eindeutig für einen Kunden

 | 

Daten mehrerer Kunden werden in der Regel durch gemeinsame Schlüsselverschlüsselungsschlüssel (Key Encryption Keys, KEKs) geschützt.

 |
| 

Steuerung der Schlüsselrotation

 | 

Ja

 | 

Ja

 | 

[Nein](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=de#kek_management)

 |
| 

[CMEK-Organisationsrichtlinien](https://docs.cloud.google.com/kms/docs/cmek-org-policy?hl=de)

 | 

Ja

 | 

Ja

 | Nein |
| 

[Administrativen Zugriff und Datenzugriff auf Verschlüsselungsschlüssel protokollieren](https://docs.cloud.google.com/kms/docs/audit-logging?hl=de)

 | 

Ja

 | 

Ja

 | 

Nein

 |
| 

Logische Datentrennung durch Verschlüsselung

 | 

Ja

 | 

Ja

 | 

[Nein](https://docs.cloud.google.com/docs/security/encryption/default-encryption?hl=de#hardware)

 |
| 

Preise

 | 

[Variabel](https://docs.cloud.google.com/kms/pricing?hl=de)

 | [Variabel](https://docs.cloud.google.com/kms/pricing?hl=de) | 

Kostenlos

 |

1 Der Inhaber des Schlüssels gibt an, wer die Rechte an dem Schlüssel hat. Schlüssel, die Ihnen gehören, haben einen stark eingeschränkten oder keinen Zugriff durch Google.

2 Die Verwaltung von Schlüsseln umfasst die folgenden Aufgaben:

*   Schlüssel erstellen
*   Wählen Sie das Schutzniveau der Schlüssel aus.
*   Berechtigung für die Verwaltung der Schlüssel zuweisen.
*   Zugriff auf Schlüssel steuern
*   Nutzung von Schlüsseln steuern.
*   Rotationszeitraum von Schlüsseln festlegen und ändern oder eine Rotation von Schlüsseln auslösen.
*   Schlüsselstatus ändern
*   Schlüsselversionen löschen.
*   Schlüsselversionen, Schlüssel und Schlüsselbunde löschen

3 Schlüssel verwalten: Sie legen fest, welche Art von Schlüsseln verwendet wird und wie sie eingesetzt werden. Außerdem erkennen Sie Abweichungen und planen bei Bedarf Korrekturmaßnahmen. Sie können Ihre Schlüssel kontrollieren, die Verwaltung der Schlüssel aber an einen Drittanbieter delegieren.

Wie die Verschlüsselung ruhender Daten zum Schutz der Daten beiträgt
--------------------------------------------------------------------

Die Verschlüsselung ruhender Daten ist nur ein Teil einer umfassenden Sicherheitsstrategie. Die Verschlüsselung hat folgende Vorteile:

*   Trägt dazu bei, dass der Angreifer die Daten nicht ohne Zugriff auf die Verschlüsselungsschlüssel lesen kann, wenn Daten in die Hände von Angreifern fallen. Selbst wenn der Angreifer Zugriff auf die Speichergeräte hat, die Kundendaten enthalten, kann er diese weder verstehen noch entschlüsseln.
*   Reduziert die Angriffsfläche, indem die unteren Ebenen der Hardware- und Softwarestacks entfernt werden.
*   Fungiert als Nadelöhr, da zentral verwaltete Verschlüsselungsschlüssel einen zentralen Ort darstellen, an dem der Zugriff auf Daten erzwungen und geprüft werden kann.
*   Reduziert die Angriffsfläche, da Unternehmen nicht alle Daten schützen müssen, sondern sich auf ihre Schutzstrategien auf die Verschlüsselungsschlüssel konzentrieren können.
*   Bietet unseren Kunden einen wichtigen Datenschutzmechanismus. Wenn Daten im inaktiven Zustand verschlüsselt werden, beschränkt dies den Zugriff von Systemen und Entwicklern auf die Daten.

Was sind Kundendaten?
---------------------

Wie in den [Nutzungsbedingungen fürGoogle Cloud](https://cloud.google.com/terms/?hl=de) definiert, sind _Kundendaten_ Daten, die Kunden oder Endnutzer Google über die Dienste in ihrem Konto zur Verfügung stellen.

Kundeninhalte sind Daten, die Sie selbst generieren oder uns zur Verfügung stellen, z. B. Daten, die in Cloud-Storage-Buckets, Persistent-Disk-Volumes und Festplatten-Snapshots gespeichert sind, die von Compute Engine verwendet werden. Dieses Dokument konzentriert sich auf die Standardverschlüsselung ruhender Daten für diese Art von Kundendaten.

Kunden-Metadaten sind Daten über Ihre Kundeninhalte und umfassen automatisch generierte Projektnummern, Zeitstempel, IP-Adressen, die Byte-Größe eines Objekts in Cloud Storage oder den Maschinentyp in Compute Engine. Kunden-Metadaten sind in dem Maße geschützt, dass eine kontinuierliche Leistung und ein kontinuierlicher Betrieb gewährleistet sind. Dieses Dokument befasst sich nicht mit dem Schutz von Metadaten.

Zusammen bilden Kundeninhalte und Kundenmetadaten die Kundendaten.

Standardverschlüsselung von inaktiven Daten
-------------------------------------------

Google verschlüsselt alle gespeicherten inaktiven Kundeninhalte mit einem oder mehreren Verschlüsselungsverfahren, ohne dass Sie etwas tun müssen. In den folgenden Abschnitten werden die Mechanismen beschrieben, die wir zum Verschlüsseln von Kundeninhalten verwenden.

### Verschlüsselungsebenen

Google nutzt verschiedene Verschlüsselungsebenen, um den Schutz von Daten sicherzustellen. Die Verwendung mehrerer Verschlüsselungsebenen bietet zusätzlichen redundanten Datenschutz und gibt uns die Möglichkeit, basierend auf den Anwendungsanforderungen den jeweils optimalen Ansatz zu bestimmen.

Das folgende Diagramm zeigt die verschiedenen Verschlüsselungsebenen, die im Allgemeinen zum Schutz von Nutzerdaten in Google-Produktionsrechenzentren verwendet werden. Entweder werden für alle Nutzerdaten die Verschlüsselung des verteilten Dateisystems oder die Datenbank- und Dateispeicherverschlüsselung eingesetzt und für alle Daten in Google-Produktionsrechenzentren wird die Verschlüsselung auf Speichergerät eingerichtet.

### Verschlüsselung auf Infrastrukturebene

Alle Speichersysteme von Google verwenden eine ähnliche Verschlüsselungsarchitektur. Die Implementierungsdetails unterscheiden sich jedoch von System zu System. Daten werden zur Speicherung in logische Blöcke zerlegt. Jeder Block kann eine Größe von mehreren GB haben. Jeder Block wird auf Speicherebene mit einem individuellen Datenverschlüsselungsschlüssel (Data Encryption Key, DEK) verschlüsselt: Zwei Blöcke haben nicht denselben DEK, auch wenn sie demselben Kunden gehören oder auf demselben Gerät gespeichert sind.

Nach einer Aktualisierung eines Datenblocks wird dieser mit einem neuen Schlüssel verschlüsselt; der vorhandene Schlüssel wird nicht wiederverwendet. Diese Partitionierung der Daten mithilfe eines anderen Schlüssels beschränkt das Risiko eines potenziellen Missbrauchs des Datenverschlüsselungsschlüssels auf diesen Datenblock.

Google verschlüsselt Daten, bevor sie in ein Datenbankspeichersystem oder Hardwarelaufwerk geschrieben werden. Die Verschlüsselung ist von vornherein Bestandteil aller Speichersysteme, wird also nicht nachträglich hinzugefügt.

Jeder logische Datenblock hat eine eindeutige Kennung. Mithilfe von ACLs (Access Control Lists) wird sichergestellt, dass jeder Block nur von Google-Diensten entschlüsselt werden kann, die mit autorisierten Rollen betrieben werden, die nur zu diesem Zeitpunkt Zugriff erhalten. Diese Zugriffsbegrenzung verhindert den Zugriff auf die Daten ohne Autorisierung, wodurch die Datensicherheit und der Datenschutz erhöht werden.

Jeder Datenblock wird über unsere Speichersysteme verteilt und in verschlüsselter Form für Sicherung und Notfallwiederherstellung repliziert. Ein Angreifer, der auf Kundendaten zugreifen möchte, müsste zwei Dinge kennen und darauf zugreifen können: alle Speicherblöcke, die den gewünschten Daten entsprechen, und alle Verschlüsselungsschlüssel, die zu den Blöcken gehören.

Das folgende Diagramm zeigt, wie Daten in unsere Infrastruktur hochgeladen und dann zum Speichern in verschlüsselte Blöcke unterteilt werden.

Wir verwenden den AES-Algorithmus, um inaktive Daten zu verschlüsseln. Alle Daten auf Speicherebene werden von DEKs verschlüsselt, die standardmäßig AES-256 verwenden, mit Ausnahme einer kleinen Anzahl von [nichtflüchtigen Speichern](https://cloud.google.com/persistent-disk?hl=de), die vor 2015 erstellt wurden und AES-128 verwenden. AES ist weit verbreitet, da sowohl AES-256 als auch AES-128 vom [National Institute of Standards and Technology (NIST)](https://csrc.nist.gov/publications/detail/sp/800-131a/rev-2/final) zur Langzeitspeicherung empfohlen werden. AES ist oft Teil der Complianceanforderungen der Kunden.

Ein logischer Datenblock kann die Daten mehrerer Kunden enthalten. Wenn Sie eine logische Datentrennung durch Verschlüsselung erreichen möchten, müssen Sie [Cloud Key Management Service](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=de#cloud-kms-design-pillars) aktivieren.

### Verschlüsselung auf Speichergeräteebene

Zusätzlich zur [Verschlüsselung auf Speichersystemebene](#hardware) werden Daten auch auf Speichergeräteebene mit AES-256 für Festplattenlaufwerke (HDD) und Solid-State-Laufwerke (SSD) verschlüsselt, wobei ein separater Schlüssel auf Geräteebene verwendet wird (der sich von dem Schlüssel unterscheidet, der zur Verschlüsselung der Daten auf Speicherebene verwendet wird). Eine geringe Anzahl von Legacy-HDDs nutzt noch AES-128. Nutzerdaten, die auf von Google verwendeten SSDs gespeichert sind, werden ausschließlich mit AES-256 verschlüsselt.

### Verschlüsselung von Sicherungen

Unser Sicherungssystem sorgt dafür, dass die Daten während des Sicherungsvorgangs verschlüsselt bleiben. Mit diesem Ansatz wird die unnötige Freigabe von Klartextdaten vermieden.

Außerdem verschlüsselt das Sicherungssystem die meisten Sicherungsdateien weiter unabhängig mit ihrem eigenen DEK. Der DEK wird von einem Schlüssel abgeleitet, der in Keystore gespeichert ist und zum Zeitpunkt der Sicherung pro Datei einen zufällig generierten Seed erstellt. Ein weiterer DEK wird für alle Metadaten in Sicherungen verwendet. Dieser DEK wird ebenfalls im Keystore gespeichert.

### FIPS-Compliance bei inaktiven Daten

Google Cloud verwendet ein [gemäß FIPS 140-2 validiertes](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=de) Verschlüsselungsmodul ([Zertifikat 4407](https://csrc.nist.gov/projects/cryptographic-module-validation-program/certificate/4407)) in der Produktionsumgebung.

Schlüsselverwaltung
-------------------

Aufgrund der hohen Anzahl von Schlüsseln bei Google und der Notwendigkeit einer geringen Latenz und Hochverfügbarkeit werden DEKs in der Nähe der Daten gespeichert, die sie verschlüsseln. DEKs werden mit einem Schlüsselverschlüsselungsschlüssel (Key Encryption Key, KEK) verschlüsselt (verpackt). Dabei wird die Technik der [Umschlagverschlüsselung](https://docs.cloud.google.com/kms/docs/envelope-encryption?hl=de) verwendet. Diese KEKs sind nicht kundenspezifisch. Stattdessen sind für jeden Dienst ein oder mehrere KEKs vorhanden.

KEKs werden zentral in Keystore gespeichert, einem Repository, das speziell zum Speichern von Schlüsseln erstellt wurde. Mit weniger KEKs als DEKs und dem zentralen Keystore können Daten im großen Maßstab gespeichert und verschlüsselt sowie Datenzugriffe von einem zentralen Punkt aus verfolgt und gesteuert werden.

In Google Cloudkann jeder Kunde gemeinsam genutzte und nicht freigegebene Ressourcen haben. Ein Beispiel für eine freigegebene Ressource ist ein freigegebenes Basis-Image in Compute Engine. Bei gemeinsam genutzten Ressourcen beziehen sich mehrere Kunden auf eine einzelne Kopie, die mit einem einzigen DEK verschlüsselt wird. Nicht freigegebene Ressourcen werden in Datenblöcke unterteilt und mit Schlüsseln verschlüsselt, die von den Schlüsseln getrennt sind, die für andere Kunden verwendet werden. Diese Schlüssel unterscheiden sich auch von den Schlüsseln, die andere Teile derselben Daten desselben Kunden schützen. Es gibt Ausnahmen (z. B. Datastore, App Engine oder Pub/Sub), bei denen die Daten mehrerer Kunden mit demselben DEK verschlüsselt werden können.

### DEKs generieren

Das Speichersystem generiert DEKs mithilfe der allgemeinen kryptografischen Bibliothek von Google. Im Allgemeinen werden DEKS dann an Keystore gesendet, um mit dem KEK dieses Speichersystems verpackt zu werden, und die verpackten DEKs werden an das Speichersystem zurückgegeben, um zusammen mit den Datenblöcken aufbewahrt zu werden. Wenn ein Speichersystem verschlüsselte Daten abrufen muss, ruft es den verpackten DEK ab und sendet ihn an Keystore. Keystore überprüft dann, ob dieser Dienst zur Verwendung des KEK berechtigt ist. Ist dies der Fall, wird der Klartext-DEK entpackt und an den Dienst zurückgegeben. Der Dienst verwendet dann den DEK, um den Datenblock in Klartext zu entschlüsseln und seine Integrität zu überprüfen.

Alle Google Cloud -Speichersysteme entsprechen diesem Schlüsselverwaltungsmodell. Die meisten Systeme implementieren jedoch auch zusätzliche KEKs auf Speicherseite, um eine Hierarchie von Schlüsseln zu erstellen. Dadurch können die Systeme eine niedrige Latenz bieten und gleichzeitig den KEK auf höchster Ebene (im Schlüsselspeicher gespeichert) als Root of Trust verwenden.

### KEKs generieren

Die meisten KEKs für die Verschlüsselung von Datenblöcken werden in Keystore generiert. Die verbleibenden KEKs werden innerhalb der Speicherdienste generiert. Aus Konsistenzgründen werden alle KEKs mit der kryptografischen Bibliothek und einem von Google entwickelten Zufallszahlengenerator (Random Number Generator, RNG) generiert. Der RNG basiert auf NIST 800-90Ar1 CTR-DRBG und generiert einen AES-256-KEK. (In der Vergangenheit war dies AES-128. Einige dieser Schlüssel sind zum Entschlüsseln von Daten weiterhin aktiv.)

Bei Intel- und AMD-Prozessoren wird der RNG aus dem [RDRAND-Befehl](https://software.intel.com/en-us/articles/intel-digital-random-number-generator-drng-software-implementation-guide) und dem RNG des Linux-Kernels gespeist. Der RNG des Linux-Kernels wird wiederum aus mehreren unabhängigen Entropiequellen geseedet, einschließlich RDRAND und entropischen Ereignissen aus der Rechenzentrumsumgebung (z. B. detaillierte Messungen der Anzahl von Festplattensuchen und des Eingangs zwischen Paketen). Bei Arm-Prozessoren wird der RNG aus dem RNG des Linux-Kernels gespeist.

DEKs werden mit KEKs mit AES-256 oder AES-128 (je nachGoogle Cloud -Dienst) verpackt. Derzeit wird für alle KEKs fürGoogle Cloud -Dienste ein Upgrade auf AES-256 durchgeführt.

### KEK-Verwaltung

Keystore wurde ausschließlich zur Verwaltung von KEKs erstellt. KEKs, die von Speichersystemen verwendet werden, können standardmäßig nicht aus Keystore exportiert werden. Alle Ver- und Entschlüsselungen mit diesen Schlüsseln müssen innerhalb von Keystore erfolgen. Auf diese Weise werden Sicherheitslücken und Missbrauch vermieden. Darüber hinaus wird Keystore in die Lage versetzt, bei der Verwendung von Schlüsseln Audit-Trails zu erstellen.

Keystore ist in der Lage, KEKs in regelmäßigen Zeitintervallen mit der kryptografischen Bibliothek von Google automatisch zu rotieren, um neue Schlüssel zu erzeugen. Auch wenn häufig von einem einzigen Schlüssel die Rede ist, meinen wir tatsächlich einen Satz Schlüssel, mit dem die Daten geschützt werden: Ein Schlüssel ist für die Verschlüsselung aktiv und ein Satz historischer Schlüssel für die Entschlüsselung. Die Anzahl der Verlaufsschlüssel wird durch den Schlüsselrotationsplan bestimmt. KEKs werden für die Notfallwiederherstellung gesichert und können beliebig oft wiederhergestellt werden.

Die Verwendung von KEKs wird durch ACLs in Keystore für jeden Schlüssel mit einer Richtlinie pro Schlüssel verwaltet. Nur autorisierte Google-Dienste und Nutzer haben Zugriff auf die Schlüssel. Die Verwendung der einzelnen Schlüssel wird auf der Ebene des jeweiligen Vorgangs verfolgt, der diesen Schlüssel erfordert. Jeder Zugriff auf einen Schlüssel wird authentifiziert und protokolliert. Alle Datenzugriffe von Nutzern sind im Rahmen der allgemeinen Sicherheits- und Datenschutzrichtlinien von Google prüfbar.

### Prozess für den Zugriff auf verschlüsselte Datenblöcke

Wenn ein Google-Dienst auf einen verschlüsselten Datenblock zugreift, geschieht Folgendes:

1.  Der Dienst fordert die benötigten Daten vom Speichersystem an.
2.  Das Speichersystem ermittelt die Blöcke, in denen die Daten gespeichert sind (die Block-IDs), sowie den genauen Speicherort.
3.  Für jeden Block ruft das Speichersystem den verpackten DEK ab, der zusammen mit diesem Block aufbewahrt wird, und sendet ihn zum Entpacken an Keystore. In einigen Fällen wird dieser Vorgang vom Dienst durchgeführt.
4.  Das Speichersystem überprüft basierend auf einer Jobkennung und anhand der Block-ID, ob der ermittelte Job auf diesen Datenblock zugreifen darf. Keystore überprüft, ob das Speichersystem berechtigt ist, den mit dem Dienst verknüpften KEK zu verwenden und den jeweiligen DEK zu entpacken.
5.  Keystore führt eine der folgenden Aufgaben aus:
    *   Senden des entpackten DEK zurück an das Speichersystem, das den Datenblock entschlüsselt und an den Dienst übergibt.
    *   In seltenen Fällen wird der entpackte DEK an den Dienst übergeben. Das Speichersystem übergibt den verschlüsselten Datenblock an den Dienst, der ihn entschlüsselt und verwendet.

Dieser Prozess unterscheidet sich von dem Ablauf auf dedizierten Speichergeräten, auf denen das Gerät den DEK auf Geräteebene verwaltet und schützt.

Das folgende Diagramm zeigt diesen Vorgang. Um einen Datenblock zu entschlüsseln, ruft der Speicherdienst Keystore auf, um den entpackten DEK für diesen Datenblock abzurufen.

### Schlüsselhierarchie und "Root of Trust"

Keystore wird durch einen Root-Schlüssel mit dem Namen _keystore-master key_ geschützt, der alle KEKs im Keystore umfasst. Dieser Keystore-Masterschlüssel entspricht AES-256. Er wird in einem anderen Key Management Service namens Root Keystore gespeichert. (In der Vergangenheit war der Schlüsselspeicher-Masterschlüssel AES-128 und einige dieser Schlüssel sind zum Entschlüsseln von Daten weiterhin aktiv.) Für noch mehr Sicherheit wird Root Keystore nicht auf allgemeinen Produktionsmaschinen ausgeführt, sondern nur auf dedizierten Maschinen in den Google-Rechenzentren.

Root Keystore wiederum hat einen eigenen Root-Schlüssel, den _Root-Keystore-Masterschlüssel_, der auch AES-256 ist und in einer Peer-to-Peer-Infrastruktur gespeichert wird, die als _Root Keystore-Masterschlüsselverteiler_ bezeichnet wird und diese Schlüssel global repliziert. (Früher war der Root-Keystore-Masterschlüssel AES-128 und einige dieser Schlüssel sind zum Entschlüsseln von Daten weiterhin aktiv.) Der Root Keystore-Masterschlüsselverteiler speichert die Schlüssel im RAM auf denselben dedizierten Maschinen wie Root Keystore und verwendet Logging, um eine ordnungsgemäße Verwendung zu prüfen.

Neu gestartete Instanzen des Root Schlüsselspeicher-Masterschlüsselverteilers werden mit einer Liste von Hostnamen konfiguriert, die bereits Verteilerinstanzen ausführen. Verteilerinstanzen können dann den Root Schlüsselspeicher-Masterschlüssel von anderen ausgeführten Instanzen abrufen. Anders als die unter [Globale Verfügbarkeit und Replikation](#global) beschriebenen Mechanismen zur Notfallwiederherstellung ist der Root-Keystore-Masterschlüssel nur im RAM einer begrenzten Anzahl von speziell gesicherten Maschinen enthalten.

Um dem Szenario entgegenzuwirken, in dem alle Instanzen des Root Keystore-Masterschlüsselverteilers in einer Region gleichzeitig neu gestartet werden, wird der Root Keystore-Masterschlüssel auch auf sicheren Hardwaregeräten gesichert, die in physischen Tresoren in hochgesicherten Bereichen an mehreren geografisch verteilten Standorten aufbewahrt werden. Diese Sicherung würde nur dann benötigt, wenn alle Verteilerinstanzen in einer Region gleichzeitig ausfallen würden. Nur wenige Google-Mitarbeiter haben Zugriff auf diese Tresore.

Das folgende Diagramm zeigt die Schlüsselhierarchie für die Verschlüsselung. Die Verschlüsselungsschlüsselhierarchie schützt einen Block Daten mit einem DEK. Dieser DEK ist mit einem KEK in Keystore verpackt, der wiederum von Root Keystore und dem Masterschlüsselverteiler des Root-Schlüsselspeichers geschützt wird.

### Zusammenfassung der Schlüsselverwaltung

Die folgende Liste fasst die Schlüsselverwaltung bei Google zusammen:

*   Daten werden in Blöcke unterteilt und mit DEKs verschlüsselt.
*   DEKs werden mit KEKs verschlüsselt.
*   KEKs werden in Keystore gespeichert.
*   KMS wird auf mehreren Maschinen in Rechenzentren weltweit ausgeführt.
*   Keystore-Schlüssel werden mit dem Keystore-Masterschlüssel verpackt, der wiederum im Root Keystore gespeichert wird.
*   Root Keystore ist wesentlich kleiner als Keystore und wird nur auf dedizierten Maschinen eines Rechenzentrums ausgeführt.
*   Root Keystore-Schlüssel werden mit dem Root Keystore-Masterschlüssel verpackt, der im Root Keystore-Masterschlüsselverteiler gespeichert wird.
*   Der Root Keystore-Masterschlüsselverteiler ist eine Peer-to-Peer-Infrastruktur, die auf dedizierten Geräten weltweit gleichzeitig im RAM ausgeführt wird. Jedes Gerät erhält ihr Schlüsselmaterial von anderen ausgeführten Instanzen in der Region.
*   Sollten alle Instanzen des Verteilers in einer Region ausfallen, wird ein Masterschlüssel auf verschiedener sicherer Hardware in physischen Tresoren an einigen wenigen Google-Standorten aufbewahrt.

### Globale Verfügbarkeit und Replikation

Auf jeder Ebene sind Hochverfügbarkeit, niedrige Latenz und der globale Zugriff auf Schlüssel von entscheidender Bedeutung. Diese Merkmale sind erforderlich, damit Key Management Services in Google verwendet werden können.

Aus diesem Grund ist Keystore hoch skalierbar und wird in unseren Rechenzentren weltweit repliziert. Es wird auf regulären Maschinen in unserer Produktionsflotte ausgeführt. Instanzen von Keystore werden global ausgeführt, um den Betrieb von Google zu unterstützen. Somit ist die Latenz eines jeden Schlüsselvorgangs sehr niedrig.

Root Keystore wird in den einzelnen Rechenzentren auf mehreren Geräten ausgeführt, die jeweils für Sicherheitsvorgänge vorgesehen sind. Der Root Keystore-Masterschlüsselverteiler wird auf denselben Geräten ausgeführt, eins zu eins mit Root Keystore. Der Root Keystore-Masterschlüsselverteiler stellt einen Verteilungsmechanismus mithilfe eines [Gossip-Protokolls](https://dl.acm.org/citation.cfm?doid=41840.41841) bereit. In einem festen Zeitintervall wählt jede Instanz des Verteilers eine zufällige andere Instanz aus, um die Schlüssel zu vergleichen. Mögliche Differenzen werden anschließend abgeglichen. Bei diesem Modell gibt es keinen zentralen Knoten, von dem unsere gesamte Infrastruktur abhängt. Mit dieser Verteilungsmethode können wir Schlüsselmaterial mit Hochverfügbarkeit verwalten und schützen.

Allgemeine kryptografische Bibliothek von Google
------------------------------------------------

Die allgemeine kryptografische Bibliothek von Google ist [Tink](https://developers.google.com/tink?hl=de). Sie beinhaltet unser gemäß FIPS 140-2 validiertes Modul [BoringCrypto](https://cloud.google.com/security/compliance/fips-140-2-validated?hl=de). Tink steht allen Google Developers zur Verfügung. Dank der konsistenten Nutzung einer allgemeinen Bibliothek muss dieser streng kontrollierte und geprüfte Code nur von einem kleinen Team von Kryptografen implementiert und gepflegt werden. Es ist also nicht notwendig, dass jedes Team bei Google seine eigene Kryptografie entwickelt. Ein spezielles Google-Sicherheitsteam ist für die Wartung dieser allgemeinen kryptografischen Bibliothek für alle Produkte zuständig.

Die Tink-Verschlüsselungsbibliothek unterstützt eine Vielzahl von Verschlüsselungsschlüsseltypen und -modi. Diese werden regelmäßig überprüft, um sicherzugehen, dass sie auch gegen die neuesten Angriffe immun sind.

Derzeit verwenden wir die folgenden Verschlüsselungsalgorithmen für die Verschlüsselung ruhender Daten für DEKs und KEKs. Diese unterliegen Änderungen, da wir unsere Funktionen und Sicherheit kontinuierlich verbessern.

| **Kryptografische Primitive** | **Bevorzugte Protokolle** | **Sonstige unterstützte Protokolle** |
| --- | --- | --- |
| Symmetrische Verschlüsselung | AES-GCM (256 Bit) | 
*   AES-CBC und AES-CTR (128 und 256 Bit)

*   AES-EAX (128 und 256 Bit)

 |
| Symmetrische Signaturen (wenn mit AES-CBC und AES-CTR oben für Authentifizierung verwendet) | HMAC-SHA256 | 

*   HMAC-SHA512

*   HMAC-SHA1

 |

Die Bibliothek enthält weitere kryptografische Protokolle, die in der Vergangenheit unterstützt wurden. Die vorliegende Tabelle umfasst jedoch die in Google hauptsächlich verwendeten Protokolle.

Forschung und Innovation in der Kryptografie
--------------------------------------------

Wir beschäftigen ein Team erstklassiger Sicherheitsexperten, die mit der Verfolgung, Entwicklung und Verbesserung bestehender Verschlüsselungstechnologien beauftragt sind, damit Google immer auf dem neuesten Stand der Entwicklung im Bereich der Verschlüsselung bleibt. Unsere Entwickler beteiligen sich an Standardisierungsprozessen und der Pflege weit verbreiteter Verschlüsselungssoftware. [Wir veröffentlichen unsere Forschungsergebnisse](https://www.google.com/about/appsecurity/research/?hl=de) im Bereich der Verschlüsselungstechnologie regelmäßig, damit jeder – auch die Öffentlichkeit – von unserem Wissen profitieren kann.

Beispielsweise arbeiten wir in der Post-Quanten-Kryptografie-Forschung in den folgenden Bereichen:

*   **Standardisierung**: Wir haben das zustandslose Hash-basierte digitale Signaturverfahren mitentwickelt, das als [FIPS 205](https://csrc.nist.gov/pubs/fips/205/ipd) standardisiert ist. Wir sind Bearbeiter des Standards der Internationalen Organisation für Standardisierung (ISO) für [Post-Quanten-Kryptographie-Hash-basierte Signaturen](https://www.iso.org/standard/80492.html) und haben bei der IETF an den [Leitlinien für die Zustandsverwaltung](https://www.ietf.org/archive/id/draft-wiggers-hbs-state-00.txt) von Hash-basierten Signaturen mitgearbeitet.
    
*   **Aktivierung**: Wir haben in unser internes Protokoll für die Sicherheit der Transportschicht die [Post-Quanten-Kryptographie integriert](https://cloud.google.com/blog/products/identity-security/why-google-now-uses-post-quantum-cryptography-for-internal-comms/?hl=de). Wir haben die Unterstützung für Post-Quanten-Kryptografie in [Chrome](https://blog.chromium.org/2023/08/protecting-chrome-traffic-with-hybrid.html) aktiviert. Wir haben mehrere Post-Quanten-Kryptografie-Algorithmen in unserer [kryptografische Mediathek Tink](https://github.com/google/tink/tree/master/cc/experimental/pqcrypto). Dieser Code ist experimentell und soll die Community zu jedem Ansatz informieren.
    
*   **Publikationen**: Wir haben [_Organisationen zur Umstellung auf Post-Quanten-Kryptografie_](https://www.nature.com/articles/s41586-022-04623-2) in _Nature_ veröffentlicht. Dieser Artikel bietet einen Überblick über Herausforderungen bei der Post-Quanten-Kryptografie-Migration. Wir haben auch eine Forschungsarbeit über die Post-Quanten-Kryptographie in [unseren Sicherheitsschlüsseln](https://security.googleblog.com/2023/08/toward-quantum-resilient-security-keys.html) veröffentlicht.
    

Die symmetrische Verschlüsselung (mit AES‑128 oder höher) ist weiterhin resistent gegen Quantenangriffe.

Nächste Schritte
----------------

*   Informationen zu den Post-Quanten-Kryptografie-Ressourcen von Google finden Sie unter [Post-Quanten-Kryptografie](https://cloud.google.com/security/resources/post-quantum-cryptography?hl=de).
    
*   Informationen zur Verwendung eigener Verschlüsselungsschlüssel inGoogle Cloudfinden Sie unter [Kundenverwaltete Verschlüsselungsschlüssel (Customer-Managed Encryption Keys, CMEK)](https://docs.cloud.google.com/kms/docs/cmek?hl=de).
    
*   Allgemeine Informationen zur Sicherheit von Google Cloud finden Sie im [Abschnitt „Sicherheit“ auf der Google Cloud Website](https://cloud.google.com/security/?hl=de).
    
*   Informationen zur Compliance und zu Compliance-Zertifizierungen von Google Cloud erhalten Sie im [Abschnitt zur Compliance auf der Website von Google Cloud](https://cloud.google.com/security/compliance?hl=de) . Dort finden Sie auch den [öffentlichen SOC3-Prüfbericht](https://www.google.com/work/soc3.html?hl=de) von Google.
    
*   Informationen zur Verschlüsselung und Schlüsselverwaltung in Google Workspace finden Sie unter [Datenverschlüsselung in Google Workspace](https://storage.googleapis.com/gfw-touched-accounts-pdfs/google-encryption-whitepaper-gsuite.pdf). Viele der hier behandelten Themen werden auch dort behandelt. Dabei liegt der Fokus ausschließlich auf Google Workspace.
