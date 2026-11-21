# Confidential Space Sicherheit &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/confidential-space

In diesem Dokument werden die Sicherheitskontrollen von Confidential Space und die Vorgehensweise bei der Beseitigung einer Vielzahl von Bedrohungen beschrieben. Confidential Space ermöglicht es Parteien, vertrauliche Daten (z. B. regulierte Daten oder personenidentifizierbare Informationen) mit einer Arbeitslast zu teilen und gleichzeitig die Vertraulichkeit und Inhaberschaft der Daten zu gewährleisten. Confidential Space erleichtert die Isolation, sodass Daten nur für die Arbeitslast und die ursprünglichen Inhaber der Daten sichtbar sind.

Sie können Confidential Space für Szenarien verwenden, wenn Sie keine Vertrauensstellung mit dem Arbeitslastoperator oder zwischen den ursprünglichen Parteien herstellen können, die die vertraulichen Daten erstellt haben. Beispielsweise können Finanzinstitute den Confidential Space verwenden, um gemeinsam Betrug zu erkennen oder Geldwäsche-Aktivitäten zu analysieren. Confidential Space ermöglicht Analysen aller Kundendaten, wobei Kundenidentitäten privat bleiben.

Komponenten eines Confidential Space-Systems
--------------------------------------------

Confidential Space ist eine [vertrauenswürdige Ausführungsumgebung (Trusted Execution Environment, TEE)](https://en.wikipedia.org/wiki/Trusted_execution_environment), die Secrets nur für autorisierte Arbeitslasten freigibt. Ein Attestierungsprozess und ein gehärtetes Betriebssystem-Image schützen die Arbeitslast und die Daten, die die Arbeitslast verarbeitet, vor einem nicht vertrauenswürdigen Operator.

Ein Confidential Space-System verfügt über drei Kernkomponenten:

*   **Arbeitslast:** Ein containerisiertes Image mit einem gehärteten Betriebssystem, das in einer [Confidential VM](https://docs.cloud.google.com/confidential-computing/confidential-vm/docs/confidential-vm-overview?hl=de)\-Instanz ausgeführt wird, die Hardwareisolation und Remote-Attestierungsfunktionen bietet.
*   **[Google Cloud Attestation](https://github.com/google/go-tpm-tools/tree/main/server)**:Ein OIDC-Tokenanbieter (OpenID Connect). Mit diesem Dienst prüfen Sie die Attestierungsquoten der Confidential VM-Instanz und geben Authentifizierungstokens frei. Die Tokens enthalten Identifizierungsattribute für die Arbeitslast.
*   **Eine geschützte Ressource:** Eine verwaltete Cloud-Ressource wie ein Cloud Key Management Service oder ein Cloud Storage-Bucket. Die Ressource wird durch eine Zulassungsrichtlinie geschützt, die Zugriff auf autorisierte [föderierte Identitätstokens](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=de)gewährt. Bei einem Zwischenschritt werden mit Workload Identity-Pools die OIDC-Tokens in föderierte Identitätstokens umgewandelt, die IAM nutzen kann.

Das System sorgt dafür, dass nur autorisierten Arbeitslasten Zugriff auf geschützte Ressourcen gewährt wird. Darüber hinaus schützt Confidential Space vor und nach der Attestierung die Arbeitslast vor Inspektion und Manipulation.

In einem Confidential Space-System gibt es drei Parteien:

*   **Den Arbeitslastautor**, der ein Container-Image erstellt, das eine Anwendung mit Zugriff auf die geschützten Ressourcen enthält. Der Autor hat keinen Zugriff auf die Daten oder die Ergebnisse. Darüber hinaus steuert der Autor keinen Zugriff auf die Daten oder die Ergebnisse.
*   **Den Arbeitslastoperator**, der die Arbeitslast in einem Google Cloud -Projekt ausführt. Der Operator hat in der Regel uneingeschränkte Administratorberechtigungen für das Projekt. Der Operator kann Google Cloud Ressourcen wie Compute Engine-Instanzen, Laufwerke und Netzwerkregeln verwalten und mit jeder Google Cloud API interagieren, die darauf reagiert. Der Operator hat keinen Zugriff auf die Daten oder Ergebnisse und kann den Code oder die Ausführungsumgebung nicht beeinflussen oder ändern. Darüber hinaus steuert der Operator den Zugriff auf die Daten oder die Ergebnisse nicht.
*   **Die Ressourceninhaber (oder Datenmitbearbeiter)**, denen die geschützte Ressource gehört. Ein Ressourceninhaber kann auf seine eigenen Daten zugreifen, Berechtigungen für seine eigenen Daten festlegen und auf die Ergebnisse zugreifen. Er kann nicht auf die Daten anderer Ressourceninhaber zugreifen oder den Code selbst ändern.

Confidential Space unterstützt ein Vertrauensmodell, bei dem der Arbeitslastautor, der Arbeitslastoperator und die Ressourceninhaber voneinander getrennte, sich gegenseitig nicht vertrauende Parteien sind.

Das folgende Diagramm zeigt die Systemkomponenten und Parteien. Die Arbeitslast befindet sich in einem anderen Projekt als die geschützte Ressource.

Beispiele für eine sichere Datenverarbeitung
--------------------------------------------

Mit Confidential Space können Sie die Privatsphäre der Nutzer schützen und gleichzeitig Daten teilen. In der folgenden Tabelle werden drei Anwendungsfälle beschrieben.

| Anwendungsfall | Beispielszenario |
| --- | --- |
| Funktionales Verschlüsselungsmodell | In einem funktionalen Verschlüsselungsmodell möchte Alice das Ergebnis ihrer vertraulichen Daten für Bob freigeben, ohne ihr gesamtes Dataset offenlegen zu müssen. Alice verschlüsselt ihr Dataset und schützt den Datenverschlüsselungsschlüssel (Data Encryption Key, DEK) in Cloud KMS in ihrem Projekt. Alice schreibt ein Programm, das den Arbeitslast implementiert, und gibt die Binärdatei für Bob frei. Alice konfiguriert KMS so, dass das Programm Zugriff auf den DEK erhält. Die Arbeitslast wird im Confidential Space von Bob ausgeführt, das Dataset von Alice entschlüsselt und verarbeitet und das Ergebnis in Cloud Storage von Bob geschrieben. |
| Multi-Party Computation | In der Multi-Party Computation möchten Alice und Bob das Ergebnis miteinander teilen und gleichzeitig die Vertraulichkeit der Eingabe-Datasets beibehalten. Ähnlich wie das funktionale Verschlüsselungsmodell verschlüsseln Alice und Bob ihre jeweiligen Datasets und schützen die DEKs in den Cloud KMS-Instanzen in ihren Projekten. Sie erstellen ein Programm, das die Ergebnisse bestimmt, und führen es in einem Confidential Space aus. Alice und Bob konfigurieren KMS, um dem Programm Zugriff auf die DEKs zu gewähren. Das Programm liest und verarbeitet beide Datasets und schreibt das Ergebnis in einen freigegebenen Cloud Storage-Bucket. |
| Schlüsselfreigaben | In einem komplexeren Schema wird die Verwendung von _Schlüsselfreigaben_ verwendet. Eine Schlüsselfreigabe ist ein privater Schlüssel, der auf Alice, Bob und andere Inhaber aufgeteilt wird, sodass die Kenntnis einzelner Freigaben keinen Zugriff auf das verschlüsselte Dataset gewährt. In diesem Schema wird die Vertrauensstellung auf mehrere Inhaber aufgeteilt. Der private Schlüssel wird nur in einer eingeschränkten Confidential VM-Instanz durch eine autorisierte Arbeitslast zusammengestellt. |

In diesen Beispielen hat nur die Arbeitslast Zugriff auf das verschlüsselte Dataset und kann es verarbeiten. Mit Confidential Space wird sichergestellt, dass niemand ungeprüfte Vorgänge für Daten ausführen kann, die ihm nicht gehören. Dateninhaber steuern, wie ihre Daten verwendet werden und welche Arbeitslasten berechtigt sind, darauf zu reagieren.

Integrität und Vertraulichkeit einer Arbeitslast schützen
---------------------------------------------------------

Zum Schutz der Arbeitslast vor einem nicht vertrauenswürdigen Arbeitslastoperator implementiert Confidential Space die folgenden Sicherheitskontrollen:

*   Ein [Attestierungsprozess](#attestation-process) erkennt Änderungen am Arbeitslast-Image oder seiner Confidential VM-Instanz. Mit dieser Steuerung können Sie die Integritätsprüfung der Arbeitslast schützen.
*   Ein [gehärtetes Basis-Image](#hardened-image) hilft dabei, die Angriffsfläche zu reduzieren, und verhindert, dass der Arbeitslastoperator zur Laufzeit auf die Instanz zugreift oder sie manipuliert. Mit diesem Steuerelement können Sie die Integrität und Vertraulichkeit einer Arbeitslast nach der Attestierung schützen. Zusammen tragen diese Sicherheitskontrollen zum Schutz der Arbeitslast, ihrer Secrets und der verarbeiteten Daten bei.

### Attestierungsprozess

Der Attestierungsprozess beruht auf [Shielded VM](https://docs.cloud.google.com/shielded-vm?hl=de)\-Messungen von Measured Boot und erweiterten Laufzeiten. Dieser Prozess erfasst Messungen der Startsequenz in einem geschützten "extend-only"-Register im vTPM-Gerät (virtual Trusted Platform Module).

Die Messwerte beziehen sich auf frühe Startkomponenten, den geladenen Kernel und das Container-Image. Darüber hinaus enthalten sie Umgebungsattribute wie ein Flag, das angibt, dass es sich bei der Instanz um eine [Confidential VM](https://docs.cloud.google.com/confidential-computing?hl=de) handelt. Das vTPM signiert diese Messungen (oder Anführungszeichen) mit einem zertifizierten Attestierungsschlüssel, der von Google Cloud Attestation vertrauenswürdig ist.

Das folgende Diagramm zeigt die Komponenten eines Confidential Space-Systems und die Interaktion der einzelnen Komponenten am Attestierungsprozess.

Der Attestierungsprozess hängt von den folgenden Komponenten ab:

*   **Gast-Firmware**:Eine unveränderliche Komponente, die ein vertrauenswürdiger Teil vonGoogle Cloudist.
*   **Attestiertes Confidential Space-Image:** Ein gehärtetes Image, das auf [Container-Optimized OS](https://docs.cloud.google.com/container-optimized-os/docs?hl=de) basiert und vom angehängten Bootlaufwerk gelesen wird.
*   **Early Boot-Komponenten:** Bootloader und Kernel, die mit dem vTPM interagieren, um die Bootkomponenten in einem Platform Configuration Register (PCR) zu messen.
*   **[Launcher](https://github.com/google/go-tpm-tools/tree/main/launcher)**:Eine Komponente, die die Arbeitslastbinärdatei aus dem Image-Repository herunterlädt und den Container und seine Konfiguration in einem PCR misst. Der Launcher liest seine Konfiguration vom [Metadatenserver](https://docs.cloud.google.com/compute/docs/metadata/querying-metadata?hl=de) der Instanz aus.
    
*   **Code zur Attestierungsverarbeitung:** Code, der für die Vorbereitung eines PCR-Angebots und die Rückgabe des Angebots, des Attestierungsschlüssels und des vollständigen Ereignislogs zuständig ist.
    
*   **[Google Cloud Attestation](https://github.com/google/go-tpm-tools/tree/main/server)**:Ein Dienst, der das Angebot prüft, das Ereignislog wiederholt, das OIDC-Token ausgibt und das Token mit den Attributen für die Richtlinie für den Arbeitslastzugriff zurückgibt.
    

### Gehärtetes Bild

Das Confidential Space-Image ist ein minimales Betriebssystem für einen einzigen Zweck. Das Image führt den Container Launcher aus, der wiederum einen einzelnen Container startet. Das Confidential Space-Image baut auf den [vorhandenen Sicherheitsfunktionen](https://docs.cloud.google.com/container-optimized-os/docs/concepts/security?hl=de) von Container-Optimized OS auf und bietet folgende Vorteile:

*   **Verschlüsselte Laufwerkpartitionen mit Integritätsschutz:** Das Confidential Space-Image enthält die folgenden Partitionen:
    *   Eine `root-fs`\-Partition und eine OEM-Partition, die die Launcher-Binärdatei enthält. Diese Partitionen sind unveränderlich und werden durch [`dm-verity`](https://gitlab.com/cryptsetup/cryptsetup/-/wikis/DMVerity) geschützt.
    *   Eine temporäre, beschreibbare Partition, in der die heruntergeladene Arbeitslastbinärdatei gespeichert wird. [`dm-crypt`](https://gitlab.com/cryptsetup/cryptsetup/-/wikis/DMcrypt) verschlüsselt diese Partition und schützt ihre Integrität.
    *   Eine `tmp-fs`\-Partition, die RAM zugeordnet ist. In einer Confidential VM-Instanz ist der Arbeitsspeicher der VM verschlüsselt. Außerdem ist das System `swap-fs` deaktiviert. Dadurch wird verhindert, dass ein falsch konfiguriertes Betriebssystem Daten auf dem Laufwerk speichert.
*   **Authentifizierte, verschlüsselte Netzwerkverbindungen**:Der Launcher verwendet TLS, um Google Cloud Attestation zu authentifizieren und seine Kommunikationslinks zu schützen.
*   **Verschiedene Startmessungen**:Diese Messwerte umfassen Kernel-Befehlszeilenargumente, die `dm-verity`\-Konfiguration für `root-fs` und das Arbeitslastbinärprogramm.
*   **Deaktivierter Remotezugriff und cloudspezifische Tools:** Diese Tools umfassen _sshd_ und [OS Login](https://docs.cloud.google.com/compute/docs/oslogin?hl=de).
    
*   **Reduzierte Statusübergänge:** Der Launcher führt beispielsweise einen einzelnen Container aus und wird anschließend beendet.
    

Bedrohungsmodell und Risikominderungen
--------------------------------------

In diesem Abschnitt werden die Bedrohungsmodelle beschrieben, mit denen Confidential Space minimiert wird, sowie die neuen Risiken von Confidential Space.

Die folgenden Angriffe werden in diesem Dokument nicht behandelt.

*   Angriffe auf Softwarelieferketten, die für UEFI-Firmware (Guest Unified Extensible Interface Firmware), den Confidential Space Bootloader und den Kernel, die Containerlaufzeit und Drittanbieterabhängigkeiten für die Arbeitslast gelten. Daten-Worker gehen davon aus, dass Ressourceninhaber das Container-Image geprüft und geprüft haben, bevor die Ressourceninhaber ihre Ressource mit einer Zulassungsrichtlinie teilen.
*   Angriffe auf Google Cloud, z. B. VM-Escape-Zeichen.

### Mögliche Angriffe

Confidential Space ist zum Schutz vor drei möglichen Angriffen vorgesehen:

*   **Operator für böswillige Arbeitslasten:** Ein Operator für schädliche Arbeitslasten kann Laufwerkinhalte ändern, Netzwerkverbindungen abfangen und den TEE zur Laufzeit manipulieren. Ein böswilliger Operator kann die Angriffsfläche erweitern oder die Laufzeitumgebung einschränken. Ein böswilliger Operator kann beispielsweise eine serielle Konsole hinzufügen, um neue Angriffsvektoren einzuführen. Ein weiteres Beispiel ist ein schädlicher Operator, der Ressourcen wie das Begrenzen der Speichergröße eines Gasts, das Ändern des Speicherplatzes oder das Ändern von Firewallregeln einschränken kann. Diese Einschränkungen können E/A-Fehler auslösen, die zu schlecht getesteten Fehlerfällen führen können.
*   **Ein schädlicher Attestierungsclient**:Dieser Angreifer stellt eine Verbindung zu Google Cloud Attestation her und sendet fehlerhafte, aber signierte Ereignisprotokollnachrichten.
*   **Inhaber schädlicher Ressourcen:** Ein böswilliger Ressourceninhaber hat die volle Kontrolle über das verschlüsselte Dataset, das die Arbeitslast nutzt. Dieser Angreifer könnte fehlerhafte Eingaben oder verzerrte Daten anzeigen und versuchen, Parsing-Sicherheitslücken in der Arbeitslast auszulösen oder seine Datenschutzkontrollen zu umgehen.

### Angriffsfläche

In der folgenden Tabelle werden die Angriffsflächen beschrieben, die Angreifern zur Verfügung stehen.

| Angreifer | Ziel | Angriffsfläche | Risiken |
| --- | --- | --- | --- |
| Arbeitslastoperator | TEE, Arbeitslast | Datenträger-Lesevorgänge | 
Alles, was vom Laufwerk gelesen wird, wird vom Angreifer gesteuert.

Dienste wie nichtflüchtige Speicher mit [mehreren Autoren](https://docs.cloud.google.com/compute/docs/disks/sharing-disks-between-vms?hl=de) und dynamische Laufwerkanhänge bedeuten, dass ein Angreifer den Inhalt von Laufwerken dynamisch und nach Bedarf ändern kann.

 |
| Arbeitslastoperator | TEE, Arbeitslast | Datenträger-Schreibvorgänge | Alles, was auf das Laufwerk geschrieben wird, ist für einen Angreifer sichtbar. Weitere Informationen finden Sie unter [Laufwerk-Snapshots](https://docs.cloud.google.com/compute/docs/disks/create-snapshots?hl=de) und [Importfunktionen](https://docs.cloud.google.com/compute/docs/disks/restore-snapshot?hl=de). |
| Arbeitslastoperator | TEE, Arbeitslast | Metadatenserver | Die vom Metadatenserver gelesenen Instanzattribute liegen im Kontrolle des Angreifers, einschließlich Startskript und Umgebungsvariablen. |
| Arbeitslastoperator | TEE, Arbeitslast | Netzwerk | Externe Netzwerkverbindungen zum Image-Repository oder zu Google Cloud Attestation können abgefangen werden. Dieser Angriff erfolgt über eine private VPC mit einer öffentlichen [Cloud Router](https://docs.cloud.google.com/network-connectivity/docs/router?hl=de)\-Instanz. |
| Attestierungsclient | Google Cloud-Attestierung | Ereignisprotokoll- und Attestierungsnachrichten | Google Cloud Attestation hat eine komplexe, kryptografische Hochlogik, die defensiv schreibt. |
| Ressourceninhaber | Arbeitslast | Verschlüsseltes Dataset | 

Ein Angreifer kann die Eingabe-Datasets der Arbeitslast manipulieren, was bedeutet, dass verschlüsselte Daten nicht unbedingt vertrauenswürdige Daten sind.

 |

### Google Cloud Infrastruktur

Google Cloud umfasst den Compute Engine-Hypervisor, das vTPM für die Confidential VM, die Gast-UEFI-Firmware und eine gehostete Google Cloud Attestation-Instanz. Vertrauliches Schlüsselmaterial wie vTPM- und OIDC-Signaturschlüssel sind sicher geschützt.

Die Google-Infrastruktur ist so konzipiert, dass die Daten jedes Kunden von den Daten anderer Kunden und Nutzer logisch isoliert werden, selbst wenn sie auf demselben physischen Server gespeichert sind. Der Administratorzugriff für Supportmitarbeiter und Entwickler ist für Kunden [eingeschränkt](https://docs.cloud.google.com/docs/security/overview/whitepaper?hl=de#administrative_access_for_google_employees), geprüft und [transparent](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/overview?hl=de). Darüber hinaus trägt die Inline-Speicherverschlüsselung in Confidential VM-Instanzen dazu bei, die Vertraulichkeit des Instanzspeichers zu schützen. Die Inline-Speicherverschlüsselung macht eine direkte Prüfung oder ein versehentliches Speicher-Logging (Kernel-Absturzlog) unwirksam. Weitere Informationen zum Schutz unserer Plattform finden Sie in der [Google-Sicherheitsübersicht](https://docs.cloud.google.com/docs/security/overview/whitepaper?hl=de).

### Bedrohungen und Risikominderungen

Ein verschlüsseltes Dateisystem mit Integritätsschutz ist darauf ausgelegt, die Risiken von Laufwerksangriffen zu minimieren. Außerdem wird der Code nach dem Lesen vom Laufwerk gemessen und die Daten werden nie noch einmal vom Laufwerk gelesen. Secrets werden niemals als Klartext auf dem Laufwerk oder auf einem externen Gerät wie einer seriellen Konsole offengelegt.

Risiken durch Netzwerkangriffe werden durch authentifizierte, Ende-zu-Ende-verschlüsselte Kanäle verringert. Der externe Netzwerkzugriff wie SSH ist im Image deaktiviert. Ein Attestierungsprotokoll trägt zum Schutz der Startsequenz und aller vom Metadatenserver gelesenen Konfigurationen bei. Schließlich wird davon ausgegangen, dass für Confidential Space-Arbeitslasten differenzielle Datenschutzeinstellungen verwendet werden, um das Risiko von verzerrten Datasets zu verringern.

In den folgenden Tabellen werden die Bedrohungen und Risikominderungen beschrieben:

*   [Angriffe auf den Measured Boot-Vorgang](#attack-measured-boot)
    
*   [Angriffe auf den Container Launcher](#attacks-container-image)
    
*   [Angriffe auf Google Cloud Attestation](#attacks-attestation-service)
    
*   [Angriffe auf Arbeitslasten](#attacks-workloads)
    

#### Angriffe auf den Measured Boot-Vorgang

In dieser Tabelle werden potenzielle Bedrohungen und Strategien zur Schadensbegrenzung im Zusammenhang mit dem Measured Bootvorgang beschrieben.

| Infos zu | Risikominderung | Implementierung von Abhilfemaßnahmen |
| --- | --- | --- |
| 
Ein Angreifer startet eine Shielded VM mit alter Firmware, die keinen Measured Boot unterstützt.

Bei Erfolg kann ein Angreifer beliebige Messungen wiedergeben und die Remote-Attestierung fehlschlagen.

 | 

Diese Bedrohung wird durch die Google Cloud -Steuerungsebene gemindert.

Confidential Space fügt ein vTPM-Gerät und eine aktuelle UEFI-Firmware hinzu. Außerdem ermöglicht Confidential Space Measured Boot und Measured Boot kann nicht deaktiviert werden.

 | Innerhalb der Google Cloud Infrastruktur |
| 

Ein Angreifer überschreibt die UEFI-Firmware im physischen Arbeitsspeicher des Gastes, startet den Gast neu, setzt die vTPM-Registrierungen zurück und führt die geänderte Firmware aus.

Bei Erfolg kann ein Angreifer beliebige Messungen wiedergeben und die Remote-Attestierung nicht ausführen.

 | Diese Bedrohung wird vom Hypervisor behoben. Beim Gastneustart lädt der Hypervisor eine saubere Kopie der UEFI-Firmware in den Gastspeicher. Frühere Änderungen im Gastspeicher werden verworfen. Darüber hinaus ist der Gastneustart das einzige Ereignis, das das vTPM zurückgesetzt. | In Google Cloud und durch die Aktivierung von Confidential Computing |
| Ein Angreifer ändert nicht gemessene Konfigurationsdateien, was sich negativ auf die Programmausführung auswirkt. | 

Diese Bedrohung wird durch den Attestierungsprozess minimiert. Alle ausführbaren Binärdateien und die zugehörigen Konfigurationsdateien werden vor der Ausführung vollständig gemessen.

Insbesondere werden Secure-Boot-Variablen, die Grub-Konfiguration und Kernel-Befehlszeilenargumente gemessen.

Die [Sicherheitsprüfung](#security-tests) hat festgestellt, dass im Attestierungsprozess keine Messungen verpasst wurden.

 | Im Confidential Space-Image |
| Ein Angreifer löst in Komponenten beim frühen Start die Speicherbeschädigung aus und übernimmt die Codeausführung. | 

Komponenten für den frühen Start werden in der C-Sprache geschrieben. Diese Komponenten verarbeiten nicht vertrauenswürdige Nutzerdaten und können anfällig für Speicherfehler sein. Ein aktuelles Beispiel finden Sie unter [BootHole](https://eclypsium.com/2020/07/29/theres-a-hole-in-the-boot/).

Dieses Risiko wird durch den Attestierungsprozess behoben: Komponenten für einen frühen Start müssen alle nutzergesteuerten Daten messen, bevor sie verarbeitet werden. Ein BootHole-Angriff verwendet einen geänderten `grub.cfg`, um die Codeausführung zu erhalten und Secure Boot abzuwehren.

In einem Confidential Space-System kann dieser Angriff die Attestierung jedoch nicht bestehen, da `grub.cfg`\-Messungen nicht der erwarteten Konfiguration entsprechen.

Ein ähnliches Risiko entsteht durch eine komplexe Dateisystemlogik. Frühere Sicherheitslücken wie [Sequoia](https://blog.qualys.com/vulnerabilities-threat-research/2021/07/20/sequoia-a-local-privilege-escalation-vulnerability-in-linuxs-filesystem-layer-cve-2021-33909) zeigen, dass Dateisystemtreiber komplexe Datenstrukturen verarbeiten und anfällig für Speicherfehler sind.

Dieses Risiko wird mit dem Integritätsschutz auf Blockebene `dm-verity` und `dm-crypt` und durch das Deaktivieren von automatischen Bereitstellungen verringert.

 | Im Confidential Space-Image |
| Ein Angreifer ändert die Binärdateien des frühen Bootvorgangs auf dem Laufwerk, nachdem sie gelesen und gemessen wurden, bevor sie gelesen und ausgeführt werden (Laufwerk [TOCTOU](https://en.wikipedia.org/wiki/Time-of-check_to_time-of-use)). | 

Frühe Boot-Komponenten wurden für Bare-Metal-Maschinen erstellt und erwarten möglicherweise nicht die dynamische Umgebung der Cloud. Startkomponenten können davon ausgehen, dass der Laufwerksinhalt während der Ausführung nicht geändert werden kann. Dies ist eine falsche Annahme in Bezug auf Cloud-Umgebungen.

Dieses Risiko wird mithilfe einer defensiven Programmierung minimiert: Der Inhalt des Laufwerks ist nach Verwendung des Musters „Read, Measure“, „Execute“ schreibgeschützt.

Die Seite [Sicherheitsprüfung](#security-tests) für das Confidential Space-Image wurden in den frühen Startkomponenten wie UEFI [Shim](https://en.wikipedia.org/wiki/Shim_\(computing\)) oder [GNU GRUB](https://en.wikipedia.org/wiki/GNU_GRUB) keine CTCT-Probleme erkannt.

 | Im Confidential Space-Image |
| Ein Angreifer ändert Gerätetreiber und Nutzermodusdienste auf dem Laufwerk, nachdem der Kernel geladen wurde. | 

Diese Bedrohung wird durch ein Root-Dateisystem mit Integritätsschutz behoben.

`Root-fs` im Confidential Space-Image ist durch `dm-verity` geschützt. Die Konfiguration (`root-hash`) wird in einem Kernel-Befehlsargument übergeben und dann von Google Cloud Attestation gemessen und bestätigt.

 | Im Confidential Space-Image |

#### Angriffe auf den Container Launcher

In dieser Tabelle werden potenzielle Bedrohungen und Abhilfestrategien im Zusammenhang mit dem Launcher beschrieben.

| Infos zu | Risikominderung | Implementierung von Abhilfemaßnahmen |
| --- | --- | --- |
| Ein Angreifer fängt die Netzwerkverbindung des Launchers oder des Image-Repositorys ab. | 
Die Verbindung zum Image-Repository wird durch eine authentifizierte, verschlüsselte TLS-Verbindung geschützt.

Ein Angreifer kann die Image-URL ändern und die Arbeitslastbinärdatei steuern. Diese Aktionen werden jedoch im Attestierungslog angezeigt.

Das Image-Repository wird nicht über eine Zugriffsliste gesteuert. Es wird daher davon ausgegangen, dass das Image für jeden sichtbar ist. Sie müssen dafür sorgen, dass das Container-Image der Arbeitslast keine Secrets enthält.

 | Im Confidential Space-Image |
| Ein Angreifer ändert das Arbeitslast-Image auf dem Laufwerk, nachdem es heruntergeladen und gemessen wurde. | 

Diese Bedrohung wird durch eine beschreibbare Partition abgemildert, die verschlüsselt ist und deren Integrität geschützt ist.

Das Arbeitslast-Image und seine temporären Daten werden durch `dm-crypt` durch sitzungsspezifische Schlüssel pro Start geschützt.

Der Laufwerksverschlüsselungsprozess `dm-crypt` ermöglicht Rollback-Angriffe, bei denen ein Angreifer Laufwerksinhalte durch zuvor erfasste Geheimtexte und Tags ersetzt. Diese Angriffe gelten in den Confidential Space-Einstellungen als sehr komplex.

 | Im Confidential Space-Image |
| Ein Angreifer ändert die Launcher-Konfiguration auf dem Metadatenserver und steuert die URL des Image-Repositorys. | Der Attestierungsprozess erkennt unsichere Konfigurationen, die nicht authentifizierte Images laden. | Innerhalb von Google Cloud Attestation |

#### Angriffe auf Google Cloud Attestation

In dieser Tabelle werden potenzielle Bedrohungen und Risikominderungen für Google Cloud Attestation beschrieben.

| Bedrohung | Risikominderung | Implementierung von Abhilfemaßnahmen |
| --- | --- | --- |
| Ein Angreifer fängt die Netzwerkverbindung für den Launcher oder Google Cloud Attestation ab und liest das geheime Token aus dem Kabel. | 
Diese Bedrohung wird durch eine authentifizierte, verschlüsselte TLS-Verbindung behoben. Diese Verbindung trägt zum Schutz des Tokens vor einem passiven Abhören bei.

Ein Angreifer kann die Identität des Dienstes nicht übernehmen, da er den TLS-Schlüssel fehlt. Selbst bei erfolgreicher Ausführung kann der Angreifer keine gültigen OIDC-Tokens erstellen.

Ein Angreifer kann keine Identität als gültiger Client annehmen, da die Clientidentität durch das Attestierungsprotokoll gewährleistet wird.

 | Innerhalb des Netzwerks zwischen Ihrer Arbeitslast und dem Attestierungsdienst. |
| Ein Angreifer verwendet Parsing-Abweichungen, was zu nicht erkannten Änderungen im Attestierungsprozess führt. | 

Diese Bedrohung kann auftreten, da das Messwertereignislog serialisiert und an Google Cloud Attestation gesendet wird, wo es geparst und verarbeitet wird.

Eine Parsing-Abweichung tritt auf, wenn der Dienst und das Laufzeitbetrieb nicht mit der Semantik des Logs übereinstimmen.

Wenn beispielsweise im Feld `cmdline` eine Liste von Argumenten vorhanden ist, die durch ein Komma getrennt sind, wird ein String wie `a=1, b=2 c='3,d=e'` möglicherweise mit einem anderen Wert im `,d`\-Teilstring geparst. vom Kernel und vom Dienst.

Dieses Risiko wird durch eine Parsing-Engine behoben, die das Betriebssystemverhalten korrekt widerspiegelt. Insbesondere wird `cmdline` als vollständiger String verarbeitet und nicht in Schlüssel/Wert-Paare aufgeteilt.

 | Im Confidential Space-Image |
| Ein Angreifer verwendet alle Dienstressourcen, wodurch der Dienst bei einem DoS-Angriff (Denial of Service) ausfällt. Der Dienst wird für andere Confidential Space-Nutzer unterbrochen. | 

Dieses Zuverlässigkeitsrisiko wird durch einen verteilten elastischen Dienst behoben, der nach Bedarf einfach repliziert und hochskaliert werden kann.

Der Code verhindert eine Ressourcenüberlastung durch böswillige Clients.

 | Innerhalb der Arbeitslast |

#### Angriffe auf Arbeitslasten

In dieser Tabelle werden potenzielle Bedrohungen und Strategien zur Schadensminderung im Zusammenhang mit Arbeitslasten beschrieben.

| Infos zu | Risikominderung | Implementierung von Abhilfemaßnahmen |
| --- | --- | --- |
| Ein Angreifer liest Laufzeit-Secrets aus der beschreibbaren Partition. | 
Diese Bedrohung wird mit einem verschlüsselten Dateisystem behoben. Das beschreibbare Dateisystem wird mit `dm-crypt` bereitgestellt. Swap ist deaktiviert und der Speicherinhalt wird nicht an die Festplatte gesendet.

Als gestaffelte Sicherheitsebenen sind OIDC-Tokens begrenzt und kurzlebig.

 | Im Confidential Space-Image |
| Ein Angreifer liest Laufzeit-Secrets aus der [seriellen Konsole](https://docs.cloud.google.com/compute/docs/troubleshooting/viewing-serial-port-output?hl=de). | Diese Bedrohung wird im Confidential Space-Image abgeschwächt, da Anmeldedaten und Tokens nicht an die serielle Konsole ausgegeben werden. Darüber hinaus ist Cloud Logging deaktiviert. | Im Confidential Space-Image |
| Ein Angreifer aktualisiert autorisierte SSH-Schlüssel mit dem Paket [OSLogin](https://docs.cloud.google.com/compute/docs/oslogin?hl=de) und stellt dann eine Verbindung zur ausgeführten Instanz her. | Diese Bedrohung wird im Image des Confidential Space abgeschwächt, da die Standarddienste von `systemd` maskiert werden, einschließlich `sshd`. | Im Confidential Space-Image |
| Ein Angreifer aktualisiert die Startskripts auf dem Metadatenserver, die automatisch vom [Gast-Agent](https://github.com/GoogleCloudPlatform/guest-agent) geladen werden. | Diese Bedrohung wird im Image des Confidential Space abgeschwächt, da das Gast-Agent-Paket deaktiviert ist. | Im Confidential Space-Image |
| Ein Angreifer überträgt fehlerhafte Pakete mithilfe des Agents [OS Config](https://docs.cloud.google.com/compute/docs/os-config-management?hl=de) an die VM. | Diese Bedrohung wird im Confidential Space-Image behoben, da der OS Config-Agent deaktiviert ist. | Im Confidential Space-Image |
| Ein Angreifer übergibt ein fehlerhaftes und verschlüsseltes Dataset an die Arbeitslast. | Diese Bedrohung wird durch einen defensiven Parsing-Code in der Arbeitslast behoben. | Innerhalb der Arbeitslast |
| Ein Angreifer übergibt ein verzerrtes oder mit einem Cache versehenes Dataset an die Arbeitslast und versucht, von anderen Parteien über die Datasets zu erfahren. | Diese Bedrohung wird durch die Implementierung differenzieller Datenschutzkontrollen in der Arbeitslast behoben. | Innerhalb der Arbeitslast |

Sicherheitstests
----------------

Confidential Space hat einen umfassenden Sicherheitsüberprüfungsprozess bei Google durchlaufen. Dieser Sicherheitsüberprüfungsprozess umfasste die folgenden Tests und Audits:

*   **Negative End-to-End-Integrationstests**
    
    Diese Tests prüfen, ob die Attestierung bei schlechten Messungen fehlschlägt, z. B. wenn der Code in einer unerwarteten Umgebung einer vertraulichen VM-Instanz ausgeführt oder ein geänderter Arbeitslastcontainer gestartet wird.
    
*   **Manueller Audit des Measured Boot-Prozesses**
    
    In dieser Übersicht geht es um die Identifizierung fehlender Messungen und die Meldung von Programmfehlern mit doppeltem Lesen. Mithilfe von Tests wurde geprüft, ob der Code auf Best Practices für die Sicherheit geschrieben ist. Wenn ein Fehler aufgetreten ist, wurde der Code geschlossen (abgeschaltet).
    
*   **Manueller Audit des Build-Prozesses für das Confidential Space-Image und die Launcher-Logik:**
    
    Der Schwerpunkt dieser Prüfung liegt auf der Reduzierung des Pakets und der Angriffsfläche.
    
*   **Manuelle Prüfung der Google Cloud-Attestierung**
    
    Bei dieser Überprüfung wurde die Implementierung des richtigen Attestierungsprotokolls im Mittelpunkt gestellt und die Vermeidung von Parsing-Problemen vermieden.
    
*   **Kryptografieprüfung durch Experten für Cybersicherheit**
    
    Im Mittelpunkt dieser Prüfung stehen die Attestierungsprotokoll-, Dateisystemverschlüsselungs- und Integritätslösungen.
    
*   **Datenschutzprüfung durch Datenschutzexperten**
    
    Der Schwerpunkt dieser Prüfung liegt auf den differenziellen Datenschutzeinstellungen in Arbeitslasten, die von Google erstellt wurden.
    
*   **Kontinuierliche Fuzz-Tests**
    
    Bei diesen Tests wurden sicherheitsrelevante Komponenten wie der vTPM und Google Cloud Attestation behandelt.
    

Die [NCC-Gruppe](https://www.nccgroup.com/), eine externe Stiftungsorganisation, hat Penetrationstests im System durchgeführt. NCC hat [Confidential Space](https://www.nccgroup.com/research/public-report-google-confidential-space-security-assessment/) als sichere Computing-Plattform geprüft.

Nächste Schritte
----------------

*   Informationen zum Einstieg in Confidential Space finden Sie unter [Vertrauliche Daten mit Confidential Space analysieren](https://docs.cloud.google.com/confidential-computing/confidential-vm/docs/analyze-with-confidential-space?hl=de).
*   Weitere Informationen zum Schutz aktiver Daten finden Sie unter [Confidential Computing](https://docs.cloud.google.com/confidential-computing?hl=de).
    
*   Weitere Informationen zur Sicherheit der Google-Infrastruktur finden Sie unter [Übersicht über das Sicherheitsdesign der Infrastruktur von Google](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=de).
