# Erweiterte Produktionsgrundlage schaffen &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/enterprise/advanced-production

[Zum Hauptinhalt springen](#main-content)

*   [Dokumentation](https://docs.cloud.google.com/docs)
    *   [Übersicht](https://docs.cloud.google.com/docs/get-started)
    *   [Leitfäden](https://docs.cloud.google.com/docs/overview)
*   [Console](https://console.cloud.google.com/)

*   Erste Schritte
    
*   [Google Cloud](https://docs.cloud.google.com/docs/overview)
*   [Herangehensweise von Google Cloud an Veränderungen](https://docs.cloud.google.com/docs/cloud-approach-to-change)

*   [Well-Architected Framework](https://docs.cloud.google.com/docs/get-started/well-architected-framework)
*   Los gehts
    

*   [Grundlagen der Authentifizierung](https://docs.cloud.google.com/docs/get-started/authentication)
*   [Autorisierung und Zugriffssteuerung](https://docs.cloud.google.com/docs/get-started/authorization-access-control)
*   [AWS- und Azure-Dienste mit Google Cloud vergleichen](https://docs.cloud.google.com/docs/get-started/aws-azure-gcp-service-comparison)
*   Ressourcenerstellung planen
    
*   [Ressourcenplanung – Übersicht](https://docs.cloud.google.com/docs/get-started/resource-planning-overview)
*   [Geografische Verteilung berücksichtigen](https://docs.cloud.google.com/docs/get-started/consider-geographic-distribution)
*   [Ressourcen organisieren](https://docs.cloud.google.com/docs/get-started/organize-resources)
*   [Wartung berücksichtigen](https://docs.cloud.google.com/docs/get-started/consider-resource-maintenance)
*   Mit der Google Cloud Console interagieren
    
*   [Bedienungshilfen](https://docs.cloud.google.com/docs/accessibility)
*   [Tastenkombinationen](https://docs.cloud.google.com/docs/shortcuts)
*   [Darstellung der Konsole](https://docs.cloud.google.com/docs/get-started/console-appearance)
*   [Support erhalten](https://docs.cloud.google.com/docs/get-started/getting-support)
*   Abrechnungs- und Preisoptionen ansehen
    
*   [Informationen zu Cloud Billing](https://docs.cloud.google.com/docs/get-started/learn-about-billing)
*   [Kostenloses Programm von Google Cloud](https://docs.cloud.google.com/free/docs/free-cloud-features)
*   [Starter-Stufe](https://docs.cloud.google.com/docs/starter-tier)
*   Rabatte für zugesicherte Nutzung
    
    *   [Übersicht](https://docs.cloud.google.com/docs/cuds)
    *   Ausgabenbasierte CUDs
        
        *   [Übersicht](https://docs.cloud.google.com/docs/cuds-multiprice)
        *   [Flexible Sparpläne](https://docs.cloud.google.com/docs/cuds-flexible-savings-plans)
        *   [Änderungen am Datenmodell für ausgabenbasierte Rabatte für zugesicherte Nutzung](https://docs.cloud.google.com/docs/cuds-multiprice-datamodel)
        *   [Struktur des Exports von Abos mit Rabatten für zugesicherte Nutzung](https://docs.cloud.google.com/docs/cuds-subscriptions-export)
        *   [Rabatte nach der Migration prüfen](https://docs.cloud.google.com/docs/cuds-verify-discounts)
        *   [Zusicherung zum Kauf auswählen](https://docs.cloud.google.com/docs/cuds-choose-correctly)
        *   [Beispielabfragen für CUDs](https://docs.cloud.google.com/docs/cuds-example-queries)
        *   [Migrierte CUD-SKUs, Angebote und IDs für das Nutzungsmodell](https://docs.cloud.google.com/docs/cuds-migration)
        *   [Verbesserungen an der Abrechnungs-Benutzeroberfläche](https://docs.cloud.google.com/docs/cuds-billing-ui-improvements)
        
    *   [Attribution von Zusicherungen](https://docs.cloud.google.com/docs/cuds-attribution)
    *   [Empfehlungen](https://docs.cloud.google.com/docs/cuds-recommender)
    

Erweiterte Produktionsgrundlage schaffen
----------------------------------------

Eine **Grundlage** umfasst grundlegende Einstellungen, mit denen Sie Google Cloud Ressourcen organisieren, verwalten und pflegen können. Erstellen Sie eine **erweiterte Produktionsgrundlage**, um detaillierte Einstellungen in jeder Phase des Google Cloud Einrichtungsprozesses zu steuern, einschließlich detaillierter Sicherheits- und Netzwerkeinstellungen.

In diesem Dokument werden die Schritte und Hintergrundinformationen beschrieben, die Sie zum Konfigurieren einer erweiterten Produktionsgrundlage benötigen. Dazu gehören die folgenden Phasen:

*   [Organisation, Administratoren und Abrechnung einrichten](#establish-organization-administrators-billing): Richten Sie den obersten Knoten in Ihrer Hierarchie ein, erstellen Sie anfängliche Administratornutzer und weisen Sie Zugriff zu. Verbinden Sie Ihre Zahlungsmethode.
    
*   [Anfängliche Architektur erstellen:](#create-initial-architecture) Wählen Sie einen anfänglichen Ordner und eine Projektstruktur aus, wenden Sie Sicherheitseinstellungen an, konfigurieren Sie Logging und Monitoring und richten Sie Ihr Netzwerk ein.
    
*   [Einstellungen bereitstellen](#deploy-your-settings): Die anfänglichen Architekturauswahlen werden in Terraform-Konfigurationsdateien kompiliert. Sie können die Dateien über die Google Cloud Console bereitstellen oder herunterladen, um sie mit Ihrem eigenen Workflow anzupassen und zu iterieren. Nach der Bereitstellung können Sie eine Supportstufe auswählen.
    

Organisation, Administratoren und Abrechnung einrichten
-------------------------------------------------------

### Organisation

Eine Organisationsressource in Google Cloud stellt Ihr Unternehmen dar und dient als Knoten auf oberster Ebene in Ihrer Hierarchie. Um Ihre Organisation zu erstellen, richten Sie einen Google Identity-Dienst ein und verknüpfen ihn mit Ihrer Domain. Wenn Sie diesen Vorgang abschließen, wird automatisch eine Organisationsressource erstellt.

Eine Übersicht über die Organisationsressource finden Sie hier:

*   [Organisationsressourcen verwalten](https://docs.cloud.google.com/resource-manager/docs/creating-managing-organization?hl=de)
*   [Best Practices für die Planung von Konten und Organisationen](https://docs.cloud.google.com/architecture/identity/best-practices-for-planning?hl=de)

### Wer führt diese Aufgabe aus?

Diese Aufgabe wird von den folgenden beiden Administratoren ausgeführt:

*   Ein Identitätsadministrator, der für die Zuweisung des rollenbasierten Zugriffs zuständig ist. Sie weisen dieser Person die Rolle Cloud Identity-Super Admin zu. Weitere Informationen zum „Super Admin“ finden Sie im Hilfeartikel [Vordefinierte Administratorrollen](https://support.google.com/cloudidentity/answer/2405986?hl=de).
    
*   Ein Domainadministrator mit Zugriff auf den Domainhost des Unternehmens. Diese Person bearbeitet im Rahmen der Domainbestätigung Ihre Domaineinstellungen, z. B. DNS-Konfigurationen.
    

### Was Sie in dieser Aufgabe tun

*   Richten Sie Cloud Identity ein, falls Sie das noch nicht getan haben. Dort erstellen Sie ein [verwaltetes Nutzerkonto](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=de#managed_user_account) für Ihren Super Admin-Nutzer.
*   Verknüpfen Sie Cloud Identity mit Ihrer Domain (z. B. _example.com_).
*   Bestätigen Sie Ihre Domain. Bei diesem Vorgang wird der Stammknoten Ihrer Ressourcenhierarchie erstellt, die sogenannte [Organisationsressource](https://docs.cloud.google.com/resource-manager/docs/creating-managing-organization?hl=de).

### Warum wir diese Aufgabe empfehlen

Sie müssen Folgendes als Teil Ihrer Google Cloud -Grundlage konfigurieren:

*   Ein Google-Identitätsdienst zur zentralen Verwaltung von Identitäten.
*   Eine Organisationsressource, um den Stammknoten Ihrer Hierarchie und die Zugriffssteuerung festzulegen.

### Optionen für Google Identity-Dienste

Sie verwenden einen oder beide der folgenden Google Identity-Dienste, um Anmeldedaten für Google Cloud Nutzer zu verwalten:

*   Cloud Identity: Nutzer und Gruppen zentral verwalten. Sie können Identitäten zwischen Google und anderen Identitätsanbietern verbinden. Weitere Informationen finden Sie unter [Cloud Identity – Übersicht](https://docs.cloud.google.com/identity/docs/overview?hl=de).
*   Google Workspace: Hier können Nutzer und Gruppen verwaltet und auf Produkte für Produktivität und Zusammenarbeit wie Gmail und Google Drive zugegriffen werden. Weitere Informationen finden Sie unter [Google Workspace](http://workspace.google.com/?hl=de).

Ausführliche Informationen zur Identitätsplanung finden Sie unter [Onboarding-Prozess für Ihre Unternehmensidentitäten planen](https://docs.cloud.google.com/architecture/identity/overview-assess-and-plan?hl=de).

### Hinweise

Informationen zum Verwalten eines Super Admin-Kontos finden Sie unter [Best Practices für Super Admin-Konten](https://docs.cloud.google.com/resource-manager/docs/super-admin-best-practices?hl=de).

### Identitätsanbieter konfigurieren und Domain bestätigen

Die Schritte in dieser Aufgabe hängen davon ab, ob Sie ein neuer oder bestehender Kunde sind. Wählen Sie die Option aus, die Ihren Anforderungen entspricht:

*   **Neuer Kunde:** Richten Sie Cloud Identity ein, bestätigen Sie Ihre Domain und erstellen Sie Ihre Organisation.
    
*   **Bestehender Google Workspace-Kunde**: Verwenden Sie Google Workspace als Identitätsanbieter für Nutzer, die auf Google Workspace und Google Cloudzugreifen. Wenn Sie Nutzer erstellen möchten, die nur auf Google Cloudzugreifen, aktivieren Sie Cloud Identity.
    
*   **Vorhandener Cloud Identity-Kunde:** Prüfen Sie Ihre Domain, ob die Organisation erstellt wurde und ob Cloud Identity aktiviert ist.
    

### Neukunden

### Neukunde: Cloud Identity einrichten und Organisation erstellen

Um Ihre Organisationsressource zu erstellen, richten Sie zunächst Cloud Identity ein, das Ihnen bei der Verwaltung von Benutzern und Gruppen hilft, die auf Google Cloud Ressourcen zugreifen.

In dieser Aufgabe richten Sie die [kostenlose Version von Cloud Identity](https://support.google.com/cloudidentity/answer/7295541?hl=de) ein. Nach der Ersteinrichtung können Sie die Cloud Identity Premiumversion aktivieren. Weitere Informationen finden Sie unter [Features und Versionen von Cloud Identity vergleichen](https://support.google.com/cloudidentity/answer/7431902?hl=de).

1.  Geben Sie die Person an, die in Ihrer Organisation als Cloud Identity-Administrator fungiert (auch "Super Admin" genannt).
    
    Geben Sie den Nutzernamen des Administrators im folgenden Format an: admin-_Name_@beispiel.de. Beispiel: admin-maria@beispiel.de. Geben Sie diesen Nutzernamen an, wenn Sie Ihren ersten Administrator erstellen.
    
2.  Rufen Sie die [Cloud Identity-Registrierungsseite](https://workspace.google.com/signup/gcpidentity/welcome?hl=de) auf, um die Einrichtung abzuschließen und das Super Admin-Konto zu erstellen.
    
    Wenn beim Einrichten des Administratorkontos ein Fehler auftritt, siehe ['Google-Konto existiert bereits' Fehler](https://support.google.com/a/answer/1275816?hl=de).
    

#### Domain bestätigen und Organisationsressource erstellen

Cloud Identity verlangt von Ihnen den Nachweis, dass Sie der Inhaber Ihrer Domain sind. Nach Abschluss der Bestätigung wird Ihre Google Cloud [Organisationsressource](https://docs.cloud.google.com/resource-manager/docs/creating-managing-organization?hl=de) automatisch für Sie erstellt.

1.  Achten Sie darauf, dass Sie beim [Konfigurieren Ihres Identitätsanbieters](#configure-identity) ein Super Admin-Konto erstellt haben.
    
2.  Bestätigen Sie Ihre Domain in Cloud Identity. Beachten Sie beim Durchlaufen des Bestätigungsvorgangs Folgendes:
    
    *   Klicken Sie nicht auf „Neue Nutzer erstellen“, wenn Sie dazu aufgefordert werden. Neue Nutzer erstellen Sie in einer späteren Aufgabe.
    *   Wenn Sie Ihre Domain nicht registrieren können, lesen Sie den Hilfeartikel [Ich kann meine Domain nicht für einen Google-Dienst registrieren](https://support.google.com/a/answer/80610?hl=de).
    *   Die Verifizierung kann mehrere Stunden dauern.
    
    Schritte zur Verifizierung Ihrer Domain finden Sie unter [Domain verifizieren](https://support.google.com/cloudidentity/answer/7331243?hl=de).
    
3.  Wenn Sie die Schritte zur Domainverifizierung abgeschlossen haben, klicken Sie auf **Setup up Google Cloud console now**.
    
4.  Melden Sie sich in der Google Cloud -Konsole als Super Admin mit der von Ihnen angegebenen E-Mail-Adresse an. Beispiel: admin-maria@beispiel.de.
    
5.  Gehen Sie zu **Google Cloud Einrichtung: Organisation**. Ihre Organisation wird automatisch erstellt.
    
    [Gehen Sie zu „Organisation“](https://console.cloud.google.com/cloud-setup/organization?hl=de).
    
6.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    

#### Zusätzliche Cloud Identity-Nutzerlizenzen anfordern

Die Cloud Identity Gratisversion umfasst ein Kontingent an Nutzerlizenzen. Eine Anleitung zum Aufrufen und Anfordern von Lizenzen finden Sie im Hilfeartikel [Nutzerobergrenze in der Cloud Identity-Gratisversion](https://support.google.com/cloudidentity/answer/7295541?hl=de).

### Workspace-Kunde

### Bestehender Google Workspace-Kunde: Domain bestätigen und Cloud Identity aktivieren

Wenn Sie bereits Google Workspace-Kunde sind, bestätigen Sie Ihre Domain, prüfen Sie, ob Ihre Organisationsressource automatisch erstellt wurde, und aktivieren Sie optional Cloud Identity.

1.  Weitere Informationen zum Bestätigen Ihrer Domain in Google Workspace finden Sie im Hilfeartikel [Domain bestätigen](https://support.google.com/a/answer/60216?hl=de). Beachten Sie beim Durchlaufen des Bestätigungsvorgangs Folgendes:
    
    *   Klicken Sie nicht auf „Neue Nutzer erstellen“, wenn Sie dazu aufgefordert werden. Neue Nutzer erstellen Sie in einer späteren Aufgabe.
    *   Wenn Sie Ihre Domain nicht registrieren können, lesen Sie den Hilfeartikel [Ich kann meine Domain nicht für einen Google-Dienst registrieren](https://support.google.com/a/answer/80610?hl=de).
    *   Die Verifizierung kann mehrere Stunden dauern.
2.  Melden Sie sich in der Google Cloud -Konsole als Super Admin an.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Organisation**.
    
    [Gehen Sie zu „Organisation“](https://console.cloud.google.com/cloud-setup/organization?hl=de).
    
4.  Wählen Sie **Ich bin bereits Google Workspace-Kunde** aus.
    
5.  Prüfen Sie, ob Ihr Organisationsname in der Liste **Organisation** angezeigt wird.
    
6.  Wenn Sie Nutzer erstellen möchten, die auf Google Cloudzugreifen, aber keine Google Workspace-Lizenzen erhalten, gehen Sie so vor:
    
    1.  Aktivieren Sie [Cloud Identity](https://support.google.com/cloudidentity/answer/7384506?hl=de) in Google Workspace.
        
    2.  Wenn Sie Cloud Identity einrichten, [deaktivieren Sie die automatische Google Workspace-Lizenzierung](https://support.google.com/cloudidentity/answer/7338389?hl=de).
        

### Cloud Identity-Kunde

### Bestehender Cloud Identity-Kunde: Domain bestätigen

Wenn Sie bereits Cloud Identity-Kunde sind, prüfen Sie, ob Sie Ihre Domain bestätigt haben und ob Ihre Organisationsressource automatisch erstellt wurde.

1.  Informationen dazu, ob Ihre Domain bestätigt ist, finden Sie unter [Domain bestätigen](https://support.google.com/cloudidentity/answer/7331243?hl=de). Beachten Sie beim Durchlaufen des Bestätigungsvorgangs Folgendes:
    
    *   Klicken Sie nicht auf „Neue Nutzer erstellen“, wenn Sie dazu aufgefordert werden. Neue Nutzer erstellen Sie in einer späteren Aufgabe.
    *   Wenn Sie Ihre Domain nicht registrieren können, lesen Sie den Hilfeartikel [Ich kann meine Domain nicht für einen Google-Dienst registrieren](https://support.google.com/a/answer/80610?hl=de).
    *   Die Verifizierung kann mehrere Stunden dauern.
2.  Melden Sie sich in der Google Cloud -Konsole als Super Admin an.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Organisation**.
    
    [Gehen Sie zu „Organisation“](https://console.cloud.google.com/cloud-setup/organization?hl=de).
    
4.  Wählen Sie **Ich bin bereits Cloud Identity-Kunde** aus.
    
5.  Prüfen Sie, ob Ihr Organisationsname in der Liste **Organisation** angezeigt wird.
    
6.  Prüfen Sie, ob Cloud Identity in der [Google Admin-Konsole unter „Abos“](https://admin.google.com/ac/billing/subscriptions?hl=de) aktiviert ist. Melden Sie sich als Super Admin an.
    

### Nächste Schritte

[Gruppen erstellen und Mitglieder hinzufügen](#checklist-section-2)

### Nutzer und Gruppen

In dieser Aufgabe richten Sie Identitäten, Nutzer und Gruppen ein, um den Zugriff aufGoogle Cloud -Ressourcen zu verwalten.

Weitere Informationen zur Zugriffsverwaltung in Google Cloudfinden Sie hier:

*   [Übersicht: Identity and Access Management (IAM)](https://docs.cloud.google.com/iam/docs/overview?hl=de).
*   Best Practices finden Sie unter [IAM sicher verwenden](https://docs.cloud.google.com/iam/docs/using-iam-securely?hl=de).

### Wer führt diese Aufgabe aus?

Sie können diese Aufgabe ausführen, wenn Sie eines der folgenden Dinge haben:

*   Den Google Workspace- oder Cloud Identity-Super Admin, den Sie in der Aufgabe [Organisation](#checklist-section-1) erstellt haben.
*   Eine der folgenden IAM-Rollen:
    *   Organization Administrator (`roles/resourcemanager.organizationAdmin`).
    *   „Workforce Identity-Pooladministrator“ (`roles/iam.workforcePoolAdmin`).

### Was Sie in dieser Aufgabe tun

*   Stellen Sie eine Verbindung zu Cloud Identity oder Ihrem externen Identitätsanbieter (Identity Provider, IdP) her.
    
*   Erstellen Sie Administratorgruppen und ‑nutzer, die die restlichenGoogle Cloud -Einrichtungsschritte ausführen. Sie gewähren Zugriff auf diese Gruppen in einer späteren Aufgabe.
    

### Warum wir diese Aufgabe empfehlen

Bei dieser Aufgabe können Sie die folgenden Best Practices für die Sicherheit implementieren:

*   [Prinzip der geringsten Berechtigung](https://en.wikipedia.org/wiki/Principle_of_least_privilege): Erteilen Sie Nutzern die Berechtigungen, die mindestens für ihre Rolle erforderlich sind, und entfernen Sie den Zugriff, sobald er nicht mehr benötigt wird.
    
*   [Rollenbasierte Zugriffssteuerung (RBAC)](https://en.wikipedia.org/wiki/Role-based_access_control): Weisen Sie Nutzergruppen Berechtigungen ihrem Aufgabenbereich entsprechend zu. Fügen Sie individuellen Nutzerkonten keine Berechtigungen hinzu.
    

Mithilfe von Gruppen können Sie IAM-Rollen effizient auf eine Gruppe von Nutzern anwenden. So lässt sich die Zugriffsverwaltung vereinfachen.

### Identitätsanbieter auswählen

Sie haben folgende Möglichkeiten, Nutzer und Gruppen zu verwalten und mit Google Cloudzu verbinden:

*   _Google Workspace oder Cloud Identity_: Sie erstellen und verwalten Nutzer und Gruppen in Google Workspace oder Cloud Identity. Sie können die Synchronisierung mit Ihrem externen Identitätsanbieter auch später vornehmen.
*   _Ihr externer Identitätsanbieter, z. B. Microsoft Entra ID oder Okta_: Sie erstellen und verwalten Nutzer und Gruppen in Ihrem externen Identitätsanbieter. Anschließend verbinden Sie Ihren Anbieter mit Google Cloud , um die Einmalanmeldung zu aktivieren.

So wählen Sie Ihren Identitätsanbieter aus:

1.  Melden Sie sich in der Google Cloud -Konsole als einer der Nutzer an, die Sie unter [Wer führt diese Aufgabe aus?](#who-performs-users) angegeben haben.
    
2.  Gehen Sie zu **Google Cloud Einrichtung: Nutzer und Gruppen**.
    
    [Gehen Sie zu „Nutzer und Gruppen“](https://console.cloud.google.com/cloud-setup/users-groups?hl=de).
    
3.  Sehen Sie sich die Aufgabendetails an und klicken Sie auf **Identitätseinrichtung fortsetzen**.
    
4.  Wählen Sie auf der Seite **Identitätsanbieter auswählen** eine der folgenden Optionen aus, um eine geführte Einrichtung zu starten:
    
    *   **Verwenden Sie Google zur zentralen Verwaltung Google Cloud Benutzer**: Verwenden Sie Google Workspace oder Cloud Identity, um Benutzer und Gruppen als Superadministrator Ihrer verifizierten Domain bereitzustellen und zu verwalten. Sie können die Synchronisierung mit Ihrem externen Identitätsanbieter später vornehmen.
    *   **Microsoft Entra ID (Azure AD)**: Verwenden Sie OpenID Connect, um eine Verbindung zu Microsoft Entra ID zu konfigurieren.
    *   **Okta**: Verwenden Sie OpenID Connect, um eine Verbindung zu Okta zu konfigurieren.
    *   **OpenID Connect**: Verwenden Sie das OpenID-Protokoll, um eine Verbindung zu einem kompatiblen Identitätsanbieter herzustellen.
    *   **SAML**: Verwenden Sie das SAML-Protokoll, um eine Verbindung zu einem kompatiblen Identitätsanbieter herzustellen.
    *   **Einrichtung eines externen IdP überspringen**: Wenn Sie einen externen Identitätsanbieter haben und ihn noch nicht mit Google Cloudverbinden möchten, können Sie Nutzer und Gruppen in Google Workspace oder Cloud Identity erstellen.
5.  Klicken Sie auf **Weiter**.
    
6.  Weitere Informationen finden Sie in den folgenden Artikeln:
    
    *   [Nutzer und Gruppen in Cloud Identity erstellen](#create-in-cloud-identity)
    *   [Externen Identitätsanbieter mit Google Cloud](#connect-external-provider) verbinden

### Nutzer und Gruppen in Cloud Identity erstellen

Wenn Sie noch keinen Identitätsanbieter haben oder Ihren Identitätsanbieter noch nicht mit Google Cloudverbinden möchten, können Sie Nutzer und Gruppen in Cloud Identity oder Google Workspace erstellen und verwalten. Um Benutzer und Gruppen zu erstellen, gehen Sie wie folgt vor:

*   Erstellen Sie eine Gruppe für jede empfohlene Verwaltungsfunktion, einschließlich Organisation, Abrechnung und Netzwerkverwaltung.
*   Erstellen Sie [verwaltete Nutzerkonten](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=de#managed_user_account) für Administratoren.
*   Weisen Sie die Nutzer Verwaltungsgruppen zu, die ihren Aufgaben entsprechen.

#### Hinweise

*   Nutzer mit bestehenden Google-Konten finden und migrieren Weitere Informationen finden Sie im Hilfeartikel [Nutzer mit nicht verwalteten Konten hinzufügen](https://support.google.com/a/topic/7042002?hl=de).
    
*   Sie müssen Super Admin sein.
    

#### Administratorgruppen erstellen

Eine Gruppe ist eine benannte Sammlung von Google-Konten und Dienstkonten. Jede Gruppe hat eine eindeutige E‑Mail-Adresse, z. B. _gcp-billing-admins@example.com_. Sie erstellen Gruppen, um Nutzer zu verwalten und IAM-Rollen im großen Maßstab anzuwenden.

Die folgenden Gruppen werden empfohlen, um die Kernfunktionen Ihrer Organisation zu verwalten und die Google Cloud Einrichtung abzuschließen.

<table><tbody><tr><td><strong>Gruppe</strong></td><td><strong>Beschreibung</strong></td></tr><tr><td><code dir="ltr" translate="no">gcp-organization-admins</code></td><td>Alle Ressourcen der Organisation verwalten. Weisen Sie diese Rolle nur Ihren vertrauenswürdigsten Nutzern zu.</td></tr><tr><td><code dir="ltr" translate="no">gcp-billing-admins</code></td><td>Richten Sie Abrechnungskonten ein und überwachen Sie die Nutzung.</td></tr><tr><td><code dir="ltr" translate="no">gcp-network-admins</code></td><td>Virtual Private Cloud-Netzwerke, Subnetze und Firewallregeln erstellen.</td></tr><tr><td><code dir="ltr" translate="no">gcp-hybrid-connectivity-admins</code></td><td>Erstellen Sie Netzwerkgeräte wie Cloud-VPN-Instanzen und Cloud Router.</td></tr><tr><td><code dir="ltr" translate="no">gcp-logging-monitoring-admins</code></td><td>Alle Cloud Logging- und Cloud Monitoring-Funktionen verwenden.</td></tr><tr><td><code dir="ltr" translate="no">gcp-logging-monitoring-viewers</code></td><td>Lesezugriff auf eine Teilmenge von Logs und Monitoringdaten.</td></tr><tr><td><code dir="ltr" translate="no">gcp-security-admins</code><br></td><td>Sicherheitsrichtlinien für die gesamte Organisation, einschließlich Zugriffsverwaltung und <a href="https://docs.cloud.google.com/resource-manager/docs/organization-policy/org-policy-constraints?hl=de">Organisationseinschränkungsrichtlinien</a>, festlegen und verwalten. Weitere Informationen zur Planung Ihrer Google Cloud Sicherheitsinfrastruktur finden Sie im <a href="https://docs.cloud.google.com/architecture/security-foundations?hl=de">Google Cloud Blueprint für Unternehmensgrundlagen</a>.</td></tr><tr><td><code dir="ltr" translate="no">gcp-developers</code></td><td>Anwendungen entwerfen, codieren und testen.</td></tr><tr><td><code dir="ltr" translate="no">gcp-devops</code></td><td>End-to-End-Pipelines erstellen oder verwalten, die Continuous Integration und Continuous Delivery, Monitoring und Systembereitstellung unterstützen.</td></tr></tbody></table>

So erstellen Sie Gruppen mit Administratorzugriff:

1.  [Wählen Sie Google als Anbieter aus.](#select-provider)
    
2.  Auf der Seite **Gruppen erstellen** sollten Sie die Liste der empfohlenen administrativen Gruppen überprüfen und dann einen der folgenden Schritte ausführen:
    
    *   Um alle empfohlenen Gruppen zu erstellen, klicken Sie auf **Alle Gruppen erstellen**.
    *   Wenn Sie eine Teilmenge der empfohlenen Gruppen erstellen möchten, klicken Sie in den ausgewählten Zeilen auf **Erstellen**.
3.  Klicken Sie auf **Weiter**.
    

#### Administratoren erstellen

Wir empfehlen, anfangs Nutzer hinzuzufügen, die organisatorische, netzwerkbezogene, abrechnungsbezogene und andere Einrichtungsverfahren durchführen. Sie können nach Abschluss der Google Cloud Einrichtung weitere Nutzer hinzufügen.

So fügen Sie Nutzer mit Administratorzugriff hinzu, die Google Cloud Einrichtungsaufgaben ausführen:

1.  [Privatnutzerkonten](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=de#consumer_account) in [verwaltete Nutzerkonten](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=de#managed_user_account) migrieren, die von Cloud Identity verwaltet werden Eine detaillierte Anleitung finden Sie hier:
    
    *   [Privatnutzerkonten migrieren](https://docs.cloud.google.com/architecture/identity/migrating-consumer-accounts?hl=de)
    *   [Nutzer mit nicht verwalteten Konten hinzufügen](https://support.google.com/a/topic/7042002?hl=de)
2.  Melden Sie sich mit einem Super-Admin-Konto in der [Admin-Konsole](https://admin.google.com/?hl=de) an.
    
3.  Verwenden Sie eine der folgenden Optionen, um Nutzer hinzuzufügen:
    
    *   Informationen zum Hinzufügen von Nutzern in großen Mengen finden Sie unter [Mehrere Nutzer über eine CSV-Datei hinzufügen oder aktualisieren](https://support.google.com/cloudidentity/answer/40057?hl=de).
    *   Wenn Sie Nutzer einzeln hinzufügen möchten, lesen Sie den Hilfeartikel [Konto für neue Nutzer hinzufügen](https://support.google.com/cloudidentity/answer/33310?hl=de).
4.  Wenn Sie alle Nutzer hinzugefügt haben, kehren Sie zu **Google Cloud Einrichtung: Nutzer und Gruppen (Nutzer erstellen)** zurück.
    
5.  Klicken Sie auf **Weiter**.
    

#### Administratoren zu Gruppen hinzufügen

Fügen Sie die von Ihnen erstellten Nutzer den Gruppen mit Administratorzugriff hinzu, die ihrem Aufgabenbereich entsprechen.

1.  Sie müssen [Administratoren erstellt](#create-admin-users) haben.
2.  Sehen Sie sich in **Google Cloud Einrichtung: Nutzer und Gruppen (Nutzer zu Gruppen hinzufügen)** die Schrittdetails an.
    
3.  Gehen Sie in jeder **Gruppenzeile** so vor:
    
    1.  Klicken Sie auf **Mitglieder hinzufügen**.
    2.  Geben Sie die E‑Mail-Adresse des Nutzers ein.
    3.  Wählen Sie aus der Drop-down-Liste **Gruppenrolle** die Berechtigungseinstellungen der Gruppe für den Nutzer aus. Weitere Informationen finden Sie unter [Festlegen, wer Beiträge ansehen, posten und moderieren darf](https://support.google.com/groups/answer/2464975?hl=de).
        
        Jedes Mitglied übernimmt alle IAM-Rollen, die Sie einer Gruppe zuweisen, unabhängig von der ausgewählten Gruppenrolle.
        
    4.  Um einen weiteren Benutzer zu dieser Gruppe hinzuzufügen, klicken Sie auf **Weiteres Mitglied hinzufügen** und wiederholen Sie diese Schritte. Wir empfehlen, jeder Gruppe mehr als ein Mitglied hinzuzufügen.
        
    5.  Wenn Sie keine weiteren Nutzer mehr hinzufügen möchten, klicken Sie auf **Speichern**.
        
4.  Wenn Sie alle Gruppen durchgegangen sind, klicken Sie auf **Nutzer und Gruppen bestätigen**.
    
5.  Wenn Sie Ihren Identitätsanbieter mit Google Cloudverknüpfen möchten, lesen Sie den Hilfeartikel zum Thema:
    
    *   [Referenzarchitekturen: Externen IdP verwenden](https://docs.cloud.google.com/architecture/identity/reference-architectures?hl=de#using_an_external_idp)
    *   Informationen zum automatischen Bereitstellen von Nutzern und zum Aktivieren der Einmalanmeldung finden Sie unter:
        *   [Cloud Identity mit Active Directory verbinden](https://docs.cloud.google.com/architecture/identity/federating-gcp-with-active-directory-introduction?hl=de)
        *   [Cloud Identity mit Microsoft Entra ID verbinden](https://docs.cloud.google.com/architecture/identity/federating-gcp-with-azure-active-directory?hl=de)
    *   Wenn Sie Active Directory-Nutzer und ‑Gruppen mit Google Cloudsynchronisieren möchten, verwenden Sie [Directory Sync](https://support.google.com/a/topic/10343447?hl=de) oder [Google Cloud Directory Sync](https://support.google.com/a/answer/106368?hl=de).
        *   Einen Vergleich finden Sie unter [Directory Sync mit GCDS vergleichen](https://support.google.com/a/answer/10342938?hl=de).

### Externen Identitätsanbieter mit Google Cloudverbinden

Sie können Ihren vorhandenen Identitätsanbieter verwenden, um Gruppen und Nutzer zu erstellen und zu verwalten. Sie konfigurieren die Einmalanmeldung (SSO) für Google Cloud , indem Sie die Mitarbeiteridentitätsföderation mit Ihrem externen Identitätsanbieter einrichten. Die wichtigsten Konzepte dieses Prozesses finden Sie unter [Mitarbeiteridentitätsföderation](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=de).

Wenn Sie Ihren externen Identitätsanbieter verbinden möchten, müssen Sie eine geführte Einrichtung durchlaufen, die die folgenden Schritte umfasst:

1.  _Personalpool erstellen_: Mit einem Mitarbeiteridentitätspool können Sie Identitäten und deren Zugriff auf Ressourcen verwalten. Sie geben die folgenden Informationen in einem für Menschen lesbaren Format ein.
    *   **Personalpool-ID:** Eine global eindeutige Kennung, die in IAM verwendet wird.
    *   **Anbieter-ID**: Ein Name für Ihren Anbieter, den Nutzer angeben, wenn sie sich in Google Cloudanmelden.
2.  _Google Cloud in Ihrem Anbieter konfigurieren_: Die geführte Einrichtung umfasst spezifische Schritte für Ihren Anbieter.
3.  _Details zum Personalpool Ihres Anbieters eingeben_: Wenn Sie Ihren Anbieter als vertrauenswürdige Autorität zum Bestätigen von Identitäten hinzufügen möchten, rufen Sie die Details von Ihrem Anbieter ab und fügen Sie sie Google Cloudhinzu:
4.  _Anfänglichen Satz von administrativen Gruppen konfigurieren_: Die geführte Einrichtung enthält spezifische Schritte für Ihren Anbieter. Sie weisen Gruppen in Ihrem Anbieter zu und stellen eine Verbindung zu Google Cloudher. Eine detaillierte Beschreibung der einzelnen Gruppen finden Sie unter [Administrative Gruppen erstellen](#create-admin-groups).
5.  _Nutzer jeder Gruppe zuweisen_: Wir empfehlen, jeder Gruppe mehr als einen Nutzer zuzuweisen.

Hintergrundinformationen zum Verbindungsprozess für die einzelnen Anbieter finden Sie hier:

*   [Mitarbeiteridentitätsföderation mit Azure AD konfigurieren und Nutzer anmelden](https://docs.cloud.google.com/iam/docs/workforce-sign-in-azure-ad?hl=de)
*   [Mitarbeiteridentitätsföderation mit Okta konfigurieren und Nutzer anmelden](https://docs.cloud.google.com/iam/docs/workforce-sign-in-okta?hl=de)
*   Informationen zu anderen Anbietern, die OIDC oder SAML unterstützen, finden Sie unter [Mitarbeiteridentitätsföderation konfigurieren](https://docs.cloud.google.com/iam/docs/configuring-workforce-identity-federation?hl=de).

### Nächste Schritte

[Administratorgruppen Berechtigungen zuweisen](#checklist-section-3)

### Administratorzugriff

In dieser Aufgabe weisen Sie mit Identity and Access Management (IAM) Gruppen von Administratoren auf Organisationsebene Berechtigungssammlungen zu. Dieser Prozess bietet Administratoren eine zentrale Übersicht und Kontrolle über alle Cloud-Ressourcen, die zu Ihrer Organisation gehören.

Eine Übersicht über Identity and Access Management in Google Cloudfinden Sie in der [IAM-Übersicht](https://docs.cloud.google.com/iam/docs/overview?hl=de).

### Wer führt diese Aufgabe aus?

Dafür müssen Sie einer der folgenden Nutzer sein:

*   Ein Super Admin-Nutzer.
*   Ein Nutzer mit der Rolle „Organization Administrator“ (`roles/resourcemanager.organizationAdmin`).

### Was Sie in dieser Aufgabe tun

*   Sehen Sie sich eine Liste der Standardrollen an, die jeder Administratorgruppe zugewiesen sind, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.
    
*   Wenn Sie eine Gruppe anpassen möchten, haben Sie folgende Möglichkeiten:
    
    *   Rollen hinzufügen oder entfernen
    *   Wenn Sie eine Gruppe nicht verwenden möchten, können Sie sie löschen.

### Warum wir diese Aufgabe empfehlen

Sie müssen alle Administratorrollen für Ihre Organisation explizit zuweisen. Bei dieser Aufgabe können Sie die folgenden Best Practices für die Sicherheit implementieren:

*   [Prinzip der geringsten Berechtigung](https://en.wikipedia.org/wiki/Principle_of_least_privilege): Erteilen Sie Nutzern die Berechtigungen, die mindestens für ihre Tätigkeit erforderlich sind, und entfernen Sie den Zugriff, sobald er nicht mehr benötigt wird.
    
*   [Rollenbasierte Zugriffssteuerung (RBAC)](https://en.wikipedia.org/wiki/Role-based_access_control): Weisen Sie Nutzergruppen Berechtigungen ihrer Tätigkeit entsprechend zu. Weisen Sie einzelnen Nutzerkonten keine Rollen zu.
    

### Hinweise

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.

### Administratorgruppen Zugriff gewähren

Prüfen Sie die Standardrollen, die den einzelnen Gruppen zugewiesen sind, um jeder Administratorgruppe, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben, den entsprechenden Zugriff zu gewähren. Sie können Rollen hinzufügen oder entfernen, um den Zugriff für jede Gruppe anzupassen.

1.  Achten Sie darauf, dass Sie in der Google Cloud -Konsole als Super Admin angemeldet sind.
    
    Alternativ können Sie sich als Nutzer mit der Rolle „Organization Administrator“ anmelden (`roles/resourcemanager.organizationAdmin`).
    
2.  Gehen Sie zu **Google Cloud Einrichtung: Administratorzugriff**.
    
    ["Administratorzugriff" aufrufen](https://console.cloud.google.com/cloud-setup/administrator?hl=de)
    
3.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    
4.  Sehen Sie sich die Aufgabenübersicht an und klicken Sie auf **Ablauf für Administratorzugriff fortsetzen**.
    
5.  Sehen Sie sich die Gruppen in der Spalte **Gruppe (Hauptkonto)** an, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.
    
6.  Prüfen Sie für jede Gruppe die standardmäßigen **IAM-Rollen**. Sie können den einzelnen Gruppen Rollen hinzufügen oder entfernen, um sie an die spezifischen Anforderungen Ihrer Organisation anzupassen.
    
    Jede Rolle enthält mehrere Berechtigungen, die es Nutzern ermöglichen, relevante Aufgaben auszuführen. Weitere Informationen zu den Berechtigungen der einzelnen Rollen finden Sie in der [Referenz zu einfachen und vordefinierten IAM-Rollen](https://docs.cloud.google.com/iam/docs/roles-permissions?hl=de).
    
7.  Wenn Sie den einzelnen Gruppen Rollen zuweisen möchten, klicken Sie auf **Zugriff speichern und gewähren**.
    

Nächste Schritte
----------------

[Abrechnung](#checklist-section-4) einrichten

### Abrechnung

In dieser Aufgabe richten Sie ein Rechnungskonto für die Bezahlung von Google Cloud-Ressourcen ein. Dazu ordnen Sie Ihrer Organisation eines der folgenden Dinge zu.

*   Ein vorhandenes Cloud-Rechnungskonto. Wenn Sie keinen Zugriff auf das Konto haben, können Sie den Administrator Ihres Rechnungskontos um Zugriff bitten.
    
*   Ein neues Cloud-Rechnungskonto.
    

Weitere Informationen zur Abrechnung finden Sie in der [Dokumentation zu Cloud Billing](https://docs.cloud.google.com/billing/docs?hl=de).

### Wer führt diese Aufgabe aus?

Eine Person in der Gruppe `gcp-billing-admins@YOUR_DOMAIN`, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.

### Was Sie in dieser Aufgabe tun

*   Erstellen Sie ein Selfservice-Cloud-Rechnungskonto oder verwenden Sie ein vorhandenes.
*   Entscheiden Sie, ob Sie von einem Selfservice-Konto zu einem Rechnungskonto mit monatlicher Abrechnung wechseln möchten.
*   Richten Sie ein Cloud-Rechnungskonto und eine Zahlungsmethode ein.

### Warum wir diese Aufgabe empfehlen

Cloud Billing-Konten sind mit einem oder mehreren Google Cloud Projekten verknüpft und werden für die Abrechnung der von Ihnen genutzten Ressourcen wie virtuellen Maschinen, Netzwerken und Speicher verwendet.

### Art des Rechnungskontos ermitteln

Das Rechnungskonto, das Sie mit Ihrer Organisation verknüpfen, muss einem der folgenden Typen entsprechen.

*   _Selfservice (oder online)_: Sie registrieren sich online per Kredit- oder Debitkarte. Wir empfehlen diese Option für kleine Unternehmen oder Einzelpersonen. Wenn Sie sich online für ein Rechnungskonto anmelden, wird Ihr Konto automatisch als Selfservice-Kontotyp eingerichtet.
    
*   _Mit Rechnungsstellung (Offline)_: Wenn Sie bereits ein Self-Service-Abrechnungskonto haben, können Sie die Rechnungsstellung beantragen, wenn Ihr Unternehmen [bestimmte Voraussetzungen](https://docs.cloud.google.com/billing/docs/how-to/invoiced-billing?hl=de) erfüllt.
    

Sie können kein Rechnungskonto online erstellen, aber Sie können beantragen, ein Selfservice-Konto in ein Konto mit Rechnungsstellung umzuwandeln.

Weitere Informationen finden Sie unter [Cloud-Rechnungskontotypen](https://docs.cloud.google.com/billing/docs/concepts?hl=de#billing_account_types).

### Hinweise

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.

### Rechnungskonto einrichten

Nachdem Sie [einen Rechnungskontotyp ausgewählt](#account-type) haben, verknüpfen Sie das Rechnungskonto mit Ihrer Organisation. Nach Abschluss dieses Vorgangs können Sie mit Ihrem Rechnungskonto für Google Cloud -Ressourcen bezahlen.

1.  Melden Sie sich in der Google Cloud -Konsole als Nutzer der Gruppe `gcp-billing-admins@YOUR_DOMAIN` an.
    
2.  Klicken Sie auf **Google Cloud Einrichtung: Abrechnung**.
    
    [Zur Abrechnung](https://console.cloud.google.com/cloud-setup/billing?hl=de)
    
3.  Sehen Sie sich die Aufgabenübersicht an und klicken Sie auf **Abrechnung fortsetzen**.
    
4.  Wählen Sie eine der folgenden Optionen für das Rechnungskonto aus:
    
    ### Ein neues Konto erstellen
    
    Wenn Ihre Organisation noch kein Konto hat, erstellen Sie ein neues Konto.
    
    1.  Wählen Sie **Ich möchte ein neues Rechnungskonto erstellen** aus.
    2.  Klicken Sie auf **Weiter**.
    3.  Wählen Sie den Rechnungskontotyp aus, den Sie erstellen möchten. Eine detaillierte Anleitung finden Sie hier:
        
        *   Informationen zum Erstellen eines neuen Selfservice-Kontos finden Sie unter [Neues Selfservice-Cloud-Rechnungskonto erstellen](https://docs.cloud.google.com/billing/docs/how-to/create-billing-account?hl=de).
        *   Informationen zum Wechsel von einem bestehenden Selfservice-Konto zur Rechnungsstellung finden Sie unter [Monatliche Rechnungsstellung beantragen](https://docs.cloud.google.com/billing/docs/how-to/invoiced-billing?hl=de).
    4.  Prüfen Sie, ob Ihr Abrechnungskonto erstellt wurde:
        
        1.  Wenn Sie ein Konto mit monatlicher Abrechnung erstellt haben, warten Sie bis zu fünf Arbeitstage auf die E-Mail-Bestätigung.
            
        2.  Rufen Sie die [Seite Abrechnung](https://console.cloud.google.com/billing?hl=de) auf.
            
        3.  Wählen Sie oben auf der Seite in der Liste **Auswählen aus** Ihre Organisation aus. Wenn das Konto erfolgreich erstellt wurde, wird es in der Rechnungskontoliste angezeigt.
            
    
    ### Vorhandenes Konto verwenden
    
    Wenn Sie ein vorhandenes Rechnungskonto haben, können Sie es mit Ihrer Organisation verknüpfen.
    
    1.  Wählen Sie **Ich habe in dieser Liste ein Rechnungskonto gefunden, mit dem ich die Einrichtungsschritte abschließen möchte** aus.
    2.  Wählen Sie in der Drop-down-Liste **Abrechnung** das Konto aus, das Sie mit Ihrer Organisation verknüpfen möchten.
    3.  Klicken Sie auf **Weiter**.
    4.  Prüfen Sie die Details und klicken Sie auf **Rechnungskonto bestätigen**.
    
    ### Konto eines anderen Nutzers verwenden
    
    Wenn ein anderer Nutzer Zugriff auf ein vorhandenes Rechnungskonto hat, können Sie ihn bitten, das Rechnungskonto mit Ihrer Organisation zu verknüpfen. Alternativ kann er Ihnen Zugriff gewähren, damit Sie die Verknüpfung vornehmen können.
    
    1.  Wählen Sie **Ich möchte ein Rechnungskonto verwenden, das von einem anderen Google-Nutzerkonto verwaltet wird** aus.
    2.  Klicken Sie auf **Weiter**.
    3.  Geben Sie die E-Mail-Adresse des Administrators des Rechnungskontos ein.
    4.  Klicken Sie auf **Administrator kontaktieren**.
    5.  Warten Sie, bis sich der Abrechnungskontoadministrator mit weiteren Anweisungen bei Ihnen meldet.
    

### Nächste Schritte

[Ressourcenhierarchie erstellen und Zugriff zuweisen](#checklist-section-5)

Erste Architektur erstellen
---------------------------

### Hierarchie und Zugriff

In dieser Aufgabe richten Sie Ihre Ressourcenhierarchie ein, indem Sie die folgenden Ressourcen erstellen und den Zugriff darauf zuweisen:

[**Ordner**](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=de#folders)

Ordner bieten einen Gruppierungsmechanismus und grenzen Projekte voneinander ab. Beispielsweise können Ordner für Abteilungen in Ihrer Organisation wie Finanzen oder Einzelhandel stehen.

Die Umgebungsordner, z. B. `Production`, in Ihrer Ressourcenhierarchie sind [für die Anwendungsverwaltung konfiguriert](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=de). In diesen Ordnern können Sie Anwendungen definieren und verwalten.

[**Projekte**](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=de#projects)

Sie enthalten Ihre Google Cloud Ressourcen wie virtuelle Maschinen, Datenbanken und Storage-Buckets. Jeder Umgebungsordner enthält auch ein [Verwaltungsprojekt](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=de#management-project), mit dem Sie den Zugriff, die Abrechnung, die Beobachtbarkeit und andere administrative Funktionen für Ihre Anwendungen verwalten können.

Designaspekte und Best Practices zum Organisieren Ihrer Ressourcen in Projekten finden Sie unter [Ressourcenhierarchie für Ihre Google Cloud Landing-Zone festlegen](https://docs.cloud.google.com/architecture/landing-zones/decide-resource-hierarchy?hl=de).

### Wer führt diese Aufgabe aus?

Eine Person in der Gruppe `gcp-organization-admins@YOUR_DOMAIN`, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben, kann diese Aufgabe ausführen.

### Was Sie in dieser Aufgabe tun

*   Erstellen Sie eine erste Hierarchiestruktur mit Ordnern und Projekten.
*   IAM-Richtlinien festlegen, um den Zugriff auf Ihre Ordner und Projekte zu steuern.

### Warum wir diese Aufgabe empfehlen

Wenn Sie eine Struktur für Ordner und Projekte erstellen, können SieGoogle Cloud -Ressourcen und -Anwendungen besser verwalten. Sie können die Struktur verwenden, um den Zugriff basierend auf der Funktionsweise Ihrer Organisation zuzuweisen. Sie können beispielsweise Ressourcen anhand der geografischen Regionen, Tochterunternehmen oder Verantwortlichkeiten in Ihrer Organisation organisieren und Zugriff auf sie gewähren.

### Ressourcenhierarchie planen

Mit der Ressourcenhierarchie können Sie Grenzen festlegen und Ressourcen für allgemeine Aufgaben in Ihrer Organisation freigeben. Sie erstellen die Hierarchie mit einer der folgenden Erstkonfigurationen, die auf Ihrer Organisationsstruktur basiert:

*   _Einfach und umgebungsorientiert_:
    
    *   Isolieren Sie Umgebungen wie `Non-Production` und `Production`.
    *   Implementieren Sie in jedem Umgebungsordner unterschiedliche Richtlinien, regulatorische Anforderungen und Zugriffssteuerungen.
    *   Gut für kleine Unternehmen mit zentralisierten Umgebungen.
*   _Einfach und teamorientiert_:
    
    *   Isolieren Sie Teams wie `Development` und `QA`.
    *   Isolieren Sie den Zugriff auf Ressourcen mithilfe von Ordnern der untergeordneten Umgebungen unter den einzelnen Teamordnern.
    *   Gut für kleine Unternehmen mit autonomen Teams.
*   _Umgebungsorientiert_:
    
    *   Priorisieren Sie die Isolation von Umgebungen wie `Non-Production` und `Production`.
    *   Isolieren Sie Geschäftsbereiche unter jedem Umgebungsordner.
    *   Isolieren Sie Teams für jede Geschäftseinheit.
    *   Gut für große Unternehmen mit zentralisierten Umgebungen.
*   _An Geschäftseinheiten orientiert_:
    
    *   Priorisieren Sie die Isolation von Geschäftseinheiten wie `Human Resources` und `Engineering`, damit Nutzer nur auf die Ressourcen und Daten zugreifen können, die sie benötigen.
    *   Isolieren Sie Teams für jede Geschäftseinheit.
    *   Isolieren Sie Umgebungen für jedes Team.
    *   Gut für große Unternehmen mit autonomen Teams.

Jede Konfiguration hat einen `Common`\-Ordner für Projekte, die freigegebene Ressourcen enthalten. Dies kann u. a. Logging- und Monitoring-Projekte umfassen.

### Hinweise

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.
*   Erstellen oder verknüpfen Sie ein Rechnungskonto in der Aufgabe [Abrechnung](#checklist-section-4).

### Erste Ordner und Projekte konfigurieren

Wählen Sie die Ressourcenhierarchie aus, die Ihrer Organisationsstruktur entspricht.

So konfigurieren Sie die ersten Ordner und Projekte:

1.  Melden Sie sich in der Google Cloud Console als Nutzer der Gruppe `gcp-organization-admins@YOUR_DOMAIN` an, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.
    
2.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Hierarchie und Zugriff**.
    
    [Zur Seite „Hierarchie und Zugriff“](https://console.cloud.google.com/cloud-setup/hierarchy-and-access?hl=de)
    
4.  Sehen Sie sich die Aufgabenübersicht an und klicken Sie dann neben **Ressourcenhierarchie** auf **Starten**.
    
5.  Wählen Sie eine Startkonfiguration aus.
    
6.  Klicken Sie auf **Fortfahren und konfigurieren**.
    
7.  Passen Sie die Ressourcenhierarchie an Ihre Organisationsstruktur an. Sie können beispielsweise Folgendes anpassen:
    
    *   Ordnernamen.
    *   Dienstprojekte für jedes Team Um Zugriff auf Dienstprojekte zu gewähren, können Sie Folgendes erstellen:
        
        *   Eine Gruppe für jedes Dienstprojekt
        *   Nutzer in jeder Gruppe
        
        Eine Übersicht über Dienstprojekte finden Sie unter [Gemeinsam genutzte VPC](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=de).
        
    *   Projekte, die für Monitoring, Logging und Netzwerk erforderlich sind.
        
    *   Benutzerdefinierte Projekte.
        
8.  Klicken Sie auf **Weiter**.
    
9.  [Zugriff auf Ihre Ordner und Projekte gewähren](#configure-access-control)
    

### Zugriff auf Ordner und Projekte gewähren

In der Aufgabe [Administratorzugriff](#checklist-section-3) haben Sie Gruppen auf Organisationsebene Administratorzugriff gewährt. In dieser Aufgabe konfigurieren Sie den Zugriff auf Gruppen, die mit den neu konfigurierten Ordnern und Projekten interagieren.

Projekte, Ordner und Organisationen haben jeweils eigene IAM-Richtlinien, die über die Ressourcenhierarchie übernommen werden:

*   _Organisation_: Richtlinien gelten für alle Ordner und Projekte in der Organisation.
*   _Ordner_: Richtlinien gelten für Projekte und andere Ordner im Ordner.
*   _Projekt_: Richtlinien gelten nur für dieses Projekt und seine Ressourcen.

Aktualisieren Sie die [IAM-Richtlinien](https://docs.cloud.google.com/iam/docs/overview?hl=de#iam_policy) für Ihre Ordner und Projekte:

1.  Gewähren Sie im Abschnitt **Zugriffssteuerung konfigurieren** von [**Hierarchie und Zugriff**](https://console.cloud.google.com/cloud-setup/hierarchy-and-access?hl=de) Ihren Gruppen Zugriff auf Ihre Ordner und Projekte:
    
    1.  Sehen Sie sich in der Tabelle die Liste der empfohlenen IAM-Rollen an, die jeder Gruppe für jede Ressource zugewiesen sind.
        
    2.  Wenn Sie die den einzelnen Gruppen zugewiesenen Rollen ändern möchten, klicken Sie in der gewünschten Zeile auf **Bearbeiten**.
        
        Weitere Informationen zu den einzelnen Rollen finden Sie unter [Einfache und vordefinierte IAM-Rollen](https://docs.cloud.google.com/iam/docs/understanding-roles?hl=de).
        
2.  Klicken Sie auf **Weiter**.
    
3.  Überprüfen Sie Ihre Änderungen und klicken Sie auf **Konfigurationsentwurf bestätigen**.
    

### Abrechnung für Verwaltungsprojekte konfigurieren

Nachdem Sie Ihre Konfiguration bereitgestellt haben, müssen Sie die Abrechnung für jedes Verwaltungsprojekt konfigurieren. Das Rechnungskonto ist erforderlich, um für APIs mit zugehörigen Kosten zu bezahlen. Weitere Informationen finden Sie unter [Rechnungskonto für das Verwaltungsprojekt verknüpfen](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=de#billing).

### Nächste Schritte

[Sicherheitseinstellungen konfigurieren](#checklist-section-6)

### Sicherheit

In dieser Aufgabe konfigurieren Sie Sicherheitseinstellungen und -produkte, um Ihre Organisation zu schützen.

### Wer führt diese Aufgabe aus?

Für diese Aufgabe müssen Sie eine der folgenden Kriterien erfüllen:

*   Die Rolle „Organization Administrator“ (`roles/resourcemanager.organizationAdmin`).
*   Mitgliedschaft in einer der folgenden Gruppen, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben:
    *   `gcp-organization-admins@<your-domain>.com`
    *   `gcp-security-admins@<your-domain>.com`

### Was Sie in dieser Aufgabe tun

Wenden Sie empfohlene Organisationsrichtlinien basierend auf den folgenden Kategorien an:

*   Zugriffsmanagement.
*   Verhalten von Dienstkonten.
*   VPC-Netzwerkkonfiguration.
*   Cloud KMS mit Autokey – nur für die [Advanced Production Foundation-Option](https://docs.cloud.google.com/docs/enterprise/cloud-setup?hl=de#select-foundation-option) verfügbar.

Sie aktivieren auch Security Command Center, um Berichte zu Sicherheitslücken und Bedrohungen zu zentralisieren.

Außerdem empfehlen wir, [wichtige Kontakte](https://docs.cloud.google.com/resource-manager/docs/managing-notification-contacts?hl=de) zu konfigurieren, damit Sicherheits- und Betriebsteams wichtige Benachrichtigungen erhalten.

### Warum wir diese Aufgabe empfehlen

Wenn Sie empfohlene Organisationsrichtlinien anwenden, können Sie Nutzeraktionen einschränken, die nicht mit Ihren Sicherheitsstandards übereinstimmen.

Wenn Sie das Security Command Center aktivieren, haben Sie einen zentralen Ort, an dem Sie Sicherheitslücken und Bedrohungen analysieren können.

Durch die Erzwingung und Automatisierung von [Cloud KMS mit Autokey](https://docs.cloud.google.com/kms/docs/kms-autokey?hl=de) können Sie vom Kunden verwaltete Verschlüsselungsschlüssel (Customer-Managed Encryption Keys, CMEKs) konsistent zum Schutz Ihrer Ressourcen verwenden.

### Hinweis

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.

### Sicherheitsaufgabe starten

1.  Melden Sie sich in der Google Cloud -Konsole mit einem Nutzer an, den Sie unter [Wer führt diese Aufgabe aus?](#who-performs-security) angegeben haben.
    
2.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Sicherheit**.
    
    Gehen Sie zu [Sicherheit](https://console.cloud.google.com/cloud-setup/security?hl=de).
    
4.  Sehen Sie sich die Aufgabenübersicht an und klicken Sie dann auf **Sicherheit starten**.
    

### Berichte zu Sicherheitslücken und Bedrohungen zentralisieren

Aktivieren Sie Security Command Center, um Dienste für die Meldung von Sicherheitslücken und Bedrohungen zu zentralisieren. So können Sie Ihren Sicherheitsstatus verbessern und Risiken minimieren. Weitere Informationen finden Sie in der [Übersicht über das Security Command Center](https://docs.cloud.google.com/security-command-center/docs/security-command-center-overview?hl=de).

1.  Achten Sie auf der Seite **Google Cloud Einrichtung: Sicherheit** darauf, dass das Kästchen **Security Command Center aktivieren: Standard** aktiviert ist.
    
    Mit dieser Aufgabe wird die kostenlose Standardstufe aktiviert. Sie können später auf die Premium-Version upgraden. Weitere Informationen finden Sie unter [Security Command Center-Dienststufen](https://docs.cloud.google.com/security-command-center/docs/service-tiers?hl=de).
    
2.  Klicken Sie auf **SCC-Einstellungen anwenden**.
    

### Empfohlene Organisationsrichtlinien anwenden

Organisationsrichtlinien gelten auf Organisationsebene und werden von Ordnern und Projekten übernommen. In dieser Aufgabe prüfen Sie die Liste der empfohlenen Richtlinien und wenden sie an. Sie können Organisationsrichtlinien jederzeit ändern. Weitere Informationen finden Sie unter [Einführung in den Organisationsrichtliniendienst](https://docs.cloud.google.com/resource-manager/docs/organization-policy/overview?hl=de).

1.  Prüfen Sie die Liste der empfohlenen Organisationsrichtlinien. Wenn Sie eine empfohlene Richtlinie nicht anwenden möchten, entfernen Sie das Häkchen aus dem entsprechenden Kästchen.
    
    Eine detaillierte Beschreibung der einzelnen Organisationsrichtlinien finden Sie unter [Einschränkungen für Organisationsrichtlinien](https://docs.cloud.google.com/resource-manager/docs/organization-policy/org-policy-constraints?hl=de).
    
2.  Klicken Sie auf **Konfigurationen der Organisationsrichtlinien bestätigen**.
    

Die von Ihnen ausgewählten Organisationsrichtlinien werden angewendet, wenn Sie die Konfiguration in einer späteren Aufgabe bereitstellen.

### Kundenverschlüsselungsschlüssel erzwingen und automatisieren

Mit Cloud KMS mit Autokey können Entwickler in Ihrer Organisation bei Bedarf symmetrische Verschlüsselungsschlüssel zum Schutz Ihrer Google Cloud-Ressourcen erstellen. Sie können Cloud KMS mit Autokey konfigurieren, wenn Sie die [Option für die erweiterte Produktionsgrundlage](https://docs.cloud.google.com/docs/enterprise/cloud-setup?hl=de#select-foundation-option) ausgewählt haben.

1.  Lesen Sie die Beschreibung von Cloud KMS mit Autokey und klicken Sie dann für **Cloud KMS mit Autokey verwenden und Organisationsrichtlinien anwenden** auf **Ja (empfohlen)**.
2.  Klicken Sie auf **Schlüsselverwaltungskonfiguration bestätigen**.

Die folgenden Konfigurationen werden angewendet, wenn Sie Ihre Konfiguration in einer späteren Aufgabe bereitstellen:

*   Richten Sie in jedem Umgebungsordner Ihrer Hierarchie ein Autokey-Projekt ein.
*   Aktivieren Sie Cloud KMS mit Autokey für die Umgebungsordner.
*   Die Verwendung von vom Kunden verwalteten Verschlüsselungsschlüsseln (Customer-Managed Encryption Keys, CMEKs) für Ressourcen, die in den einzelnen Umgebungsordnern erstellt werden, ist erforderlich.
*   Schränken Sie jeden Ordner so ein, dass nur Cloud KMS-Schlüssel im Autokey-Projekt für diesen Ordner verwendet werden.

### Benachrichtigungskontakte für die Organisation konfigurieren

Sie können Organisationsbenachrichtigungskontakte konfigurieren, damit Sicherheits- und Betriebsbenachrichtigungen das richtige Team erreichen.

Damit die zuständigen Teams diese wichtigen Benachrichtigungen rechtzeitig erhalten, konfigurieren Sie benutzerdefinierte Kontakte mit [Wichtige Kontakte](https://docs.cloud.google.com/resource-manager/docs/managing-notification-contacts?hl=de).

### Nächste Schritte

[Zentrales Logging und Monitoring](#checklist-section-7):

### Zentrales Logging und Monitoring

In dieser Aufgabe konfigurieren Sie Folgendes:

*   Zentrales Logging, mit dem Sie Logs für alle Projekte in Ihrer Organisation analysieren und Erkenntnisse daraus gewinnen können.
*   Zentrales Monitoring, mit dem Sie Messwerte für alle in dieser Einrichtung erstellten Projekte visualisieren können.

### Wer führt diese Aufgabe aus?

Zum Einrichten von Logging und Monitoring benötigen Sie eine der folgenden Optionen:

*   Die Rollen „Logging-Administrator“ (`roles/logging.admin`) und „Monitoring-Administrator“ (`roles/monitoring.admin`).
*   Mitgliedschaft in einer der folgenden Gruppen, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben:
    *   `gcp-organization-admins@YOUR_DOMAIN`
    *   `gcp-security-admins@YOUR_DOMAIN`
    *   `gcp-logging-monitoring-admins@YOUR_DOMAIN`

### Was Sie in dieser Aufgabe tun

In dieser Aufgabe tun Sie Folgendes:

*   Sie organisieren Logs, die in Projekten in Ihrer Organisation erstellt werden, zentral, um Sicherheits-, Audit- und Complianceanforderungen zu erfüllen.
*   Konfigurieren Sie ein zentrales Monitoringprojekt, um Zugriff auf Monitoringmesswerte für alle Projekte zu haben, die Sie in dieser Einrichtung erstellt haben.

### Warum wir diese Aufgabe empfehlen

Die Logspeicherung und -aufbewahrung vereinfacht die Analyse und bewahrt den Audit-Trail. Das zentrale Monitoring bietet Ihnen einen Überblick über Messwerte an einem Ort.

### Hinweis

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.
*   Erstellen oder verknüpfen Sie ein Rechnungskonto in der Aufgabe [Abrechnung](#checklist-section-4).
*   Richten Sie die Hierarchie ein und weisen Sie Zugriffsrechte in der Aufgabe [Hierarchie und Zugriff](#checklist-section-5) zu.

### Logging zentral organisieren

Mit Cloud Logging können Sie Logdaten und Ereignisse von Google Cloudspeichern, durchsuchen, analysieren, überwachen und Benachrichtigungen senden. Sie können auch Logs von Ihren Anwendungen, lokalen Ressourcen und anderen Clouds erfassen und verarbeiten. Wir empfehlen, Logs mit Cloud Logging in einem einzigen Log-Bucket zusammenzuführen.

Hier finden Sie weitere Informationen:

*   Eine Übersicht finden Sie unter [Routing und Speicher – Übersicht](https://docs.cloud.google.com/logging/docs/routing/overview?hl=de).
*   Informationen zum Logging lokaler Ressourcen finden Sie unter [Logging lokaler Ressourcen mit BindPlane](https://docs.cloud.google.com/architecture/logging-on-premises-resources-with-bindplane?hl=de).
*   Eine Anleitung zum Ändern des Logfilters nach der Bereitstellung der Konfiguration finden Sie unter [Einschlussfilter](https://docs.cloud.google.com/logging/docs/routing/overview?hl=de#inclusion-filters).

So speichern Sie Ihre Logdaten in einem zentralen Log-Bucket:

1.  Melden Sie sich in der Google Cloud -Konsole als Nutzer an, den Sie unter [Wer führt diese Aufgabe aus?](#who-performs-logging) angegeben haben.
    
2.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    
3.  Rufen Sie **Google Cloud Einrichtung: Zentrales Logging und Monitoring** auf.
    
    [Zu „Zentrales Logging und Monitoring“](https://console.cloud.google.com/cloud-setup/logging?hl=de)
    
4.  Sehen Sie sich die Aufgabenübersicht an und klicken Sie auf **Zentrales Logging und Monitoring starten**.
    
5.  Sehen Sie sich die Aufgabendetails an.
    
6.  Wenn Sie Logs an ein zentrales Log-Bucket weiterleiten möchten, muss **Audit-Logs auf Organisationsebene in einem Log-Bucket speichern** ausgewählt sein.
    
7.  Maximieren Sie **Logs an einen Logging-Log-Bucket weiterleiten** und gehen Sie so vor:
    
    1.  Geben Sie im Feld **Name des Log-Buckets** einen Namen für den zentralen Log-Bucket ein.
        
    2.  Wählen Sie in der Liste **Log-Bucket-Region** die Region aus, in der Ihre Logdaten gespeichert werden.
        
        Weitere Informationen finden Sie unter [Log-Bucket-Standorte](https://docs.cloud.google.com/logging/docs/region-support?hl=de#buckets-locations).
        
    3.  Standardmäßig werden Logs 30 Tage lang gespeichert. Wir empfehlen großen Unternehmen, Logs 365 Tage lang zu speichern. Wenn Sie die Aufbewahrungsdauer anpassen möchten, geben Sie die Anzahl der Tage in das Feld **Aufbewahrungsdauer** ein.
        
        Für Logs, die länger als 30 Tage gespeichert sind, fallen Kosten für die Aufbewahrung an. Weitere Informationen finden Sie unter [Cloud Logging – Preise](https://cloud.google.com/stackdriver/pricing?hl=de).
        

### Logs außerhalb von Google Cloudexportieren

Wenn Sie Logs an ein Ziel außerhalb von Google Cloudexportieren möchten, können Sie Pub/Sub verwenden. Wenn Sie beispielsweise mehrere Cloud-Anbieter verwenden, können Sie Logdaten von jedem Cloud-Anbieter in ein Drittanbietertool exportieren.

Sie können die exportierten Logs nach Ihren individuellen Anforderungen filtern. Sie können beispielsweise die Arten von Logs einschränken, die Sie exportieren, um die Kosten zu kontrollieren oder die Daten zu bereinigen.

Weitere Informationen zum Exportieren von Logs finden Sie unter:

*   Eine Übersicht finden Sie unter [Was ist Pub/Sub?](https://docs.cloud.google.com/pubsub/docs/overview?hl=de)
*   Preisinformationen finden Sie hier:
    *   [Pub/Sub – Preise](https://cloud.google.com/pubsub/pricing?hl=de)
    *   [Best Practices für Cloud-Audit-Logs: Preise](https://docs.cloud.google.com/logging/docs/audit/best-practices?hl=de#pricing)
*   Informationen zum Streamen zu Splunk finden Sie unter [Logs von Google Cloud zu Splunk streamen](https://docs.cloud.google.com/architecture/stream-logs-from-google-cloud-to-splunk?hl=de).

So exportieren Sie Logs:

1.  Klicken Sie auf **Logs an andere Anwendungen, andere Repositories oder Dritte streamen**.
    
2.  Geben Sie im Feld **Pub/Sub-Themen-ID** eine Kennzeichnung für das Thema ein, das Ihre exportierten Logs enthält. Informationen zum Abonnieren eines Themas finden Sie unter [Pull-Abos](https://docs.cloud.google.com/pubsub/docs/pull?hl=de).
    
3.  So wählen Sie Logs zum Exportieren aus:
    
    1.  Informationen zu den einzelnen Logtypen finden Sie unter [Cloud-Audit-Logs](https://docs.cloud.google.com/logging/docs/audit/best-practices?hl=de#understand-audit-logs).
        
    2.  Wenn Sie verhindern möchten, dass eines der folgenden empfohlenen Logs exportiert wird, klicken Sie auf die Liste **Aufnahmefilter** und deaktivieren Sie das Kästchen für das Log:
        
        *   **Cloud-Audit-Logs: Administratoraktivität**: API-Aufrufe oder Aktionen, die die Konfiguration oder Metadaten von Ressourcen ändern.
        *   **Cloud-Audit-Logs: Systemereignis**: Google Cloud Aktionen, die die Ressourcenkonfiguration ändern.
        *   **Access Transparency**: Aktionen, die Google-Mitarbeiter beim Zugriff auf Kundeninhalte ausführen.
    3.  Wählen Sie die folgenden zusätzlichen Logs aus, um sie zu exportieren:
        
        *   **Cloud-Audit-Logs: Datenzugriff**: API-Aufrufe, die die Konfiguration oder Metadaten von Ressourcen lesen, sowie nutzergesteuerte API-Aufrufe, die von Nutzern bereitgestellte Ressourcendaten erstellen, ändern oder lesen.
        *   **Cloud-Audit-Logs: Richtlinie abgelehnt**: Google Cloud Verweigerung des Zugriffs auf Dienste für Nutzer- oder Dienstkonten aufgrund von Verstößen gegen Sicherheitsrichtlinien.
    4.  Die in diesem Schritt ausgewählten Logs werden nur exportiert, wenn sie in Ihren Projekten oder Ressourcen aktiviert sind. Eine Anleitung zum Ändern des Logfilters für Ihre Projekte und Ressourcen nach der Bereitstellung der Konfiguration finden Sie unter [Einschlussfilter](https://docs.cloud.google.com/logging/docs/routing/overview?hl=de#inclusion-filters).
        
    5.  Klicken Sie auf **OK**.
        
4.  Klicken Sie auf **Weiter zu Monitoring**.
    

### Zentrales Monitoring einrichten

Das zentrale Monitoring hilft Ihnen, den Zustand, die Leistung und die Sicherheit des Systems für mehrere Projekte zu analysieren. In dieser Aufgabe fügen Sie die Projekte, die Sie in der Aufgabe [Hierarchie und Zugriff](#checklist-section-5) erstellt haben, einem Projekt mit eingeschränktem Umfang hinzu. Anschließend können Sie diese Projekte über das den Bereich festlegende Projekt überwachen. Nachdem Sie die Cloud-Einrichtung abgeschlossen haben, können Sie andere Projekte so konfigurieren, dass sie vom Bereichsprojekt überwacht werden.

Weitere Informationen finden Sie unter [Übersicht über Messwertbereiche](https://docs.cloud.google.com/monitoring/settings?hl=de).

So richten Sie das zentrale Monitoring ein:

1.  Wenn Sie Projekte, die während der Google Cloud -Einrichtung erstellt wurden, für das zentrale Monitoring konfigurieren möchten, muss **Zentrales Monitoring verwenden** ausgewählt sein.
    
    Projekte, die Sie während der Google Cloud Einrichtung erstellt haben, werden dem [Messwertbereich](https://docs.cloud.google.com/monitoring/settings?hl=de) des aufgeführten **Bereichsprojekts** hinzugefügt.
    
2.  Cloud Monitoring umfasst ein kostenloses monatliches Kontingent. Weitere Informationen finden Sie unter [Cloud Monitoring-Preise](https://cloud.google.com/stackdriver/pricing?hl=de#monitoring-pricing-summary).
    
3.  Eine Anleitung zum Konfigurieren von Projekten, die Sie außerhalb von Google Cloud Setup erstellen, finden Sie unter:
    
    *   [Messwertbereich konfigurieren](https://docs.cloud.google.com/monitoring/settings/multiple-projects?hl=de)
    *   [Überwachte Projektlimits](https://docs.cloud.google.com/monitoring/quotas?hl=de#workspace_limits)

### Konfiguration abschließen

So führen Sie die Logging- und Monitoring-Aufgabe aus:

1.  Klicken Sie auf **Konfiguration bestätigen**.
    
2.  Überprüfen Sie die Details Ihrer Logging- und Monitoring-Konfiguration. Ihre Konfiguration wird erst bereitgestellt, wenn Sie Ihre Einstellungen in einer späteren Aufgabe bereitstellen.
    

### Nächste Schritte

[Richten Sie Ihre anfängliche Netzwerkkonfiguration ein.](#checklist-section-8)

### VPC-Netzwerke

In dieser Aufgabe erstellen Sie Ihre anfängliche Netzwerkkonfiguration, die Sie bei Bedarf skalieren können.

### Virtual-Private-Cloud-Architektur

Ein [VPC-Netzwerk (Virtual Private Cloud)](https://docs.cloud.google.com/vpc/docs/overview?hl=de) ist eine virtuelle Version eines physischen Netzwerks, das im Google-Produktionsnetzwerk implementiert wird. Ein VPC-Netzwerk ist eine globale Ressource, die aus regionalen [Subnetzwerken (Subnetzen)](https://docs.cloud.google.com/vpc/docs/subnets?hl=de) besteht.

VPC-Netzwerke bieten Netzwerkfunktionen für Ihre Google Cloud Ressourcen wie Compute Engine-VM-Instanzen, GKE-Container und App Engine-Flexible-Environment-Instanzen.

[Shared VPC](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=de) verbindet Ressourcen aus mehreren Projekten mit einem gemeinsamen VPC-Netzwerk, sodass diese über die internen IP-Adressen des Netzwerks miteinander kommunizieren können. Das folgende Diagramm zeigt die grundlegende Architektur eines freigegebene VPC-Netzwerks mit angehängten Dienstprojekten.

Bei der Verwendung von freigegebene VPC legen Sie ein Host-Projekt fest und ordnen ihm ein oder mehrere Service-Projekte zu. Virtuelle private Cloud-Netzwerke im Host-Projekt werden als freigegebene VPC-Netzwerke bezeichnet.

Das Beispieldiagramm enthält Produktions- und Nichtproduktions-Hostprojekte, die jeweils ein freigegebene VPC-Netzwerk beinhalten. Sie können ein Host-Projekt verwenden, um Folgendes zentral zu verwalten:

*   Routen
*   Firewalls
*   VPN-Verbindungen
*   Subnetze

Ein Dienstprojekt ist ein beliebiges Projekt, das an ein Hostprojekt angehängt ist. Sie können Subnetze, einschließlich sekundärer Bereiche, zwischen Host- und Dienstprojekten freigeben.

In dieser Architektur enthält jedes freigegebene VPC-Netzwerk öffentliche und private Subnetze:

*   Das öffentliche Subnetz kann von Instanzen mit Internetverbindung für externe Verbindungen genutzt werden.
*   Das private Subnetz kann von internen Instanzen verwendet werden, denen keine öffentlichen IP-Adressen zugewiesen sind.

In dieser Aufgabe erstellen Sie eine erste Netzwerkkonfiguration anhand des Beispieldiagramms.

### Wer führt diese Aufgabe aus?

Für diese Aufgabe müssen Sie eine der folgenden Kriterien erfüllen:

*   Die Rolle `roles/compute.networkAdmin`
*   Zugehörigkeit zur Gruppe `gcp-network-admins@YOUR_DOMAIN`, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.

### Was Sie in dieser Aufgabe tun

Erstellen Sie eine erste Netzwerkkonfiguration, die Folgendes umfasst:

*   Erstellen Sie mehrere Hostprojekte, die Ihren Entwicklungsumgebungen entsprechen.
*   Erstellen Sie in jedem Hostprojekt ein freigegebene VPC-Netzwerk, damit verschiedene Ressourcen dasselbe Netzwerk nutzen können.
*   Erstellen Sie in jedem freigegebene VPC-Netzwerk separate Subnetze, um Dienstprojekten Netzwerkzugriff zu gewähren.

### Warum wir diese Aufgabe empfehlen

Verschiedene Teams können eine freigegebene VPC verwenden, um eine Verbindung zu einem gemeinsamen, zentral verwalteten VPC-Netzwerk herzustellen.

Hinweise
--------

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.
*   Erstellen oder verknüpfen Sie ein Rechnungskonto in der Aufgabe [Abrechnung](#checklist-section-4).
*   Richten Sie die Hierarchie ein und weisen Sie Zugriffsrechte in der Aufgabe [Hierarchie und Zugriff](#checklist-section-5) zu.

### Netzwerkarchitektur konfigurieren

Erstellen Sie Ihre erste Netzwerkkonfiguration mit zwei Hostprojekten, um Nichtproduktions- und Produktionsarbeitslasten zu segmentieren. Jedes Hostprojekt enthält ein freigegebene VPC-Netzwerk, das von mehreren Dienstprojekten verwendet werden kann. Sie konfigurieren die Netzwerkdetails und stellen dann in einer späteren Aufgabe eine Konfigurationsdatei bereit.

So konfigurieren Sie Ihr anfängliches Netzwerk:

1.  Melden Sie sich in der Google Cloud Console als Nutzer der Gruppe `gcp-organization-admins@YOUR_DOMAIN` an, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.
    
2.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Organisation auswählen** Ihre Organisation aus.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Netzwerk**.
    
    Gehen Sie zu [Networking](https://console.cloud.google.com/cloud-setup/networking?hl=de).
    
4.  Sehen Sie sich die Standardnetzwerkarchitektur an.
    
5.  So bearbeiten Sie den Netzwerknamen:
    
    1.  Klicken Sie auf **Aktionen**.
    2.  Wählen Sie **Netzwerknamen bearbeiten** aus.
    3.  Geben Sie im Feld **Netzwerkname** Kleinbuchstaben, Zahlen oder Bindestriche ein. Der Netzwerkname darf maximal 25 Zeichen enthalten.
    4.  Klicken Sie auf **Speichern**.

#### Firewalldetails ändern

Die Standard-Firewallregeln im Hostprojekt basieren auf empfohlenen Best Practices. Sie können eine oder mehrere der Standard-Firewallregeln deaktivieren. Allgemeine Informationen zu Firewallregeln finden Sie unter [VPC-Firewallregeln](https://docs.cloud.google.com/firewall/docs/firewalls?hl=de).

So ändern Sie die Firewalleinstellungen:

1.  Klicken Sie auf **Aktionen**.
    
2.  Wählen Sie **Firewallregeln bearbeiten** aus.
    
3.  Ausführliche Informationen zu den einzelnen Standard-Firewallregeln finden Sie unter [Vorab ausgefüllte Regeln im Standardnetzwerk](https://docs.cloud.google.com/firewall/docs/firewalls?hl=de#more_rules_default_vpc).
    
4.  Wenn Sie eine Firewallregel deaktivieren möchten, entfernen Sie das Häkchen aus dem entsprechenden Kästchen.
    
5.  Wenn Sie das **Firewallregel-Logging** deaktivieren möchten, klicken Sie auf **Aus**.
    
    Standardmäßig wird der Traffic von und zu Compute Engine-Instanzen zu Prüfzwecken protokolliert. Für diesen Vorgang fallen Kosten an. Weitere Informationen finden Sie unter [Logging von VPC-Firewallregeln – Übersicht](https://docs.cloud.google.com/firewall/docs/vpc-firewall-rules-logging-overview?hl=de).
    
6.  Klicken Sie auf **Speichern**.
    

#### Subnetzdetails ändern

Jedes VPC-Netzwerk enthält mindestens ein Subnetz, das eine regionale Ressource mit einem zugehörigen IP-Adressbereich ist. Für diese multiregionale Konfiguration sind mindestens zwei Subnetze mit nicht überlappenden IP-Bereichen erforderlich.

Weitere Informationen finden Sie unter [Subnetze](https://docs.cloud.google.com/vpc/docs/subnets?hl=de).

Jedes Subnetz wird gemäß den empfohlenen Best Practices konfiguriert. Wenn Sie jedes Subnetz anpassen möchten, gehen Sie so vor:

1.  Klicken Sie auf **Aktionen**.
2.  Wählen Sie **Subnetze bearbeiten** aus.
3.  Geben Sie im Feld **Name** Kleinbuchstaben, Zahlen oder Bindestriche ein. Der Subnetzname darf 25 Zeichen nicht überschreiten.
4.  Wählen Sie im Drop-down-Menü **Region** eine Region in der Nähe Ihres Point of Service aus.
    
    Wir empfehlen, für jedes Subnetz eine andere Region zu verwenden. Sie können die Region nicht mehr ändern, nachdem Sie die Konfiguration bereitgestellt haben. Informationen zur Auswahl einer Region finden Sie unter [Regionale Ressourcen](https://docs.cloud.google.com/compute/docs/regions-zones/global-regional-zonal-resources?hl=de#regionalresources).
    
5.  Geben Sie im Feld **IP-Adressbereich** einen Bereich in CIDR-Notation ein, z. B. 10.0.0.0/24.
    
    Der eingegebene Bereich darf sich nicht mit anderen Subnetzen in diesem Netzwerk überschneiden. Informationen zu gültigen Bereichen finden Sie unter [IPv4-Subnetzbereiche](https://docs.cloud.google.com/vpc/docs/vpc?hl=de#manually_created_subnet_ip_ranges).
    
6.  Wiederholen Sie diese Schritte für Subnetz 2.
    
7.  Wenn Sie weitere Subnetze in diesem Netzwerk konfigurieren möchten, klicken Sie auf **Subnetz hinzufügen** und wiederholen Sie diese Schritte.
    
8.  Klicken Sie auf **Speichern**.
    

Ihre Subnetze werden automatisch den Best Practices gemäß konfiguriert. Wenn Sie die Konfiguration ändern möchten, gehen Sie auf der Seite **Google Cloud Einrichtung: VPC-Netzwerke** so vor:

1.  Wenn Sie VPC-Flusslogs deaktivieren möchten, wählen Sie in der Spalte **Flusslogs** die Option **Aus**.
    
    Wenn Flusslogs aktiviert sind, werden in jedem Subnetz Netzwerkflüsse aufgezeichnet, die Sie aus Sicherheitsgründen, zur Kostenoptimierung und zu anderen Zwecken analysieren können. Weitere Informationen finden Sie unter [VPC-Flusslogs verwenden](https://docs.cloud.google.com/vpc/docs/using-flow-logs?hl=de).
    
    Für VPC-Flusslogs fallen Kosten an. Weitere Informationen finden Sie unter [Virtual Private Cloud-Preise](https://cloud.google.com/vpc/pricing?hl=de).
    
2.  Wenn Sie den privaten Google-Zugriff deaktivieren möchten, wählen Sie in der Spalte **Privater Zugriff** die Option **Aus**.
    
    Wenn der private Google-Zugriff aktiviert ist, können VM-Instanzen ohne externe IP-Adressen auf Google APIs und ‑Dienste zugreifen. Weitere Informationen finden Sie unter [Privater Google-Zugriff](https://docs.cloud.google.com/vpc/docs/private-google-access?hl=de).
    
3.  Wenn Sie Cloud NAT aktivieren möchten, wählen Sie in der Spalte **Cloud NAT** die Option **An**.
    
    Wenn Cloud NAT aktiviert ist, können bestimmte Ressourcen ausgehende Verbindungen zum Internet herstellen. Weitere Informationen finden Sie unter [Cloud NAT – Übersicht](https://docs.cloud.google.com/nat/docs/overview?hl=de).
    
    Für Cloud NAT fallen Kosten an. Weitere Informationen finden Sie unter [Virtual Private Cloud – Preise](https://cloud.google.com/vpc/pricing?hl=de).
    
4.  Klicken Sie auf **Weiter zu „Dienstprojekte verknüpfen“**.
    

### Dienstprojekte mit Ihren Hostprojekten verknüpfen

Ein Dienstprojekt ist ein beliebiges Projekt, das an ein Hostprojekt angehängt wurde. Dieser Anhang ermöglicht die Teilnahme des Dienstprojekts an der freigegebene VPC. Jedes Dienstprojekt kann von verschiedenen Abteilungen oder Teams betrieben und verwaltet werden, um eine Trennung der Verantwortlichkeiten zu schaffen.

Weitere Informationen zum Verbinden mehrerer Projekte mit einem gemeinsamen VPC-Netzwerk finden Sie unter [Gemeinsam genutzte VPC – Übersicht](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=de).

So verknüpfen Sie Dienstprojekte mit Ihren Hostprojekten und schließen die Konfiguration ab:

1.  Wählen Sie für jedes Subnetz in der Tabelle **Gemeinsam genutzte VPC-Netzwerke** ein Dienstprojekt aus, mit dem eine Verbindung hergestellt werden soll. Wählen Sie dazu im Drop-down-Menü **Projekt auswählen** in der Spalte **Dienstprojekt** die Option „Projekt“ aus.
    
    Sie können ein Dienstprojekt mit mehreren Subnetzen verbinden.
    
2.  Klicken Sie auf **Weiter zur Überprüfung**.
    
3.  Überprüfen Sie Ihre Konfiguration und nehmen Sie Änderungen vor.
    
    Sie können Änderungen vornehmen, bis Sie die Konfigurationsdatei bereitstellen.
    
4.  Klicken Sie auf **Konfigurationsentwurf bestätigen**. Ihre Netzwerkkonfiguration wird Ihrer Konfigurationsdatei hinzugefügt.
    
    Ihr Netzwerk wird erst bereitgestellt, wenn Sie Ihre Konfigurationsdatei in einer späteren Aufgabe bereitstellen.
    

### Nächste Schritte

[Hybridkonnektivität einrichten](#checklist-section-9), um lokale Server oder andere Cloud-Anbieter mit Google Cloudzu verbinden.

### Hybridkonnektivität

In dieser Aufgabe stellen Sie Verbindungen zwischen den Netzwerken Ihres Peers (lokal oder in einer anderen Cloud) und Ihren Google Cloud Netzwerken her, wie im folgenden Diagramm dargestellt.

Dieser Vorgang erstellt ein HA VPN, eine Lösung mit hoher Verfügbarkeit (HA), die Sie schnell erstellen können, um Daten über das öffentliche Internet zu übertragen.

Nachdem Sie Ihre Google Cloud -Konfiguration bereitgestellt haben, empfehlen wir, eine robustere Verbindung mit [Cloud Interconnect](https://docs.cloud.google.com/network-connectivity/docs/interconnect/concepts/overview?hl=de) zu erstellen.

Weitere Informationen zu Verbindungen zwischen Peer-Netzwerken und Google Cloudfinden Sie unter:

*   [Cloud VPN – Übersicht](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=de)
*   [Produkt für die Netzwerkverbindung auswählen](https://docs.cloud.google.com/network-connectivity/docs/how-to/choose-product?hl=de)

### Wer führt diese Aufgabe aus?

Sie benötigen die Rolle "Organization Administrator" (`roles/resourcemanager.organizationAdmin`).

### Was Sie in dieser Aufgabe tun

Verbindungen mit niedriger Latenz und Hochverfügbarkeit zwischen Ihren VPC-Netzwerken und Ihren lokalen oder anderen Cloud-Netzwerken erstellen. Sie konfigurieren die folgenden Komponenten:

*   _Google Cloud HA VPN-Gateway_: Eine regionale Ressource mit zwei Schnittstellen, die jeweils eine eigene IP-Adresse haben. Sie geben den IP-Stacktyp an, der bestimmt, ob IPv6-Traffic in Ihrer Verbindung unterstützt wird. Hintergrundinformationen finden Sie unter [HA VPN](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=de#ha-vpn).
*   _Peer-VPN-Gateway_: Das Gateway in Ihrem Peer-Netzwerk, mit dem sich das HA VPN-Gateway Google Cloudverbindet. Sie geben externe IP-Adressen ein, die Ihr Peer-Gateway für die Verbindung zu Google Cloudverwendet. Weitere Informationen finden Sie unter [Peer-VPN-Gateway konfigurieren](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=de).
*   _Cloud Router_: Verwendet das Border Gateway Protocol (BGP), um Routen zwischen Ihrer VPC und Peer-Netzwerken dynamisch auszutauschen. Sie weisen Ihrem Cloud Router eine Nummer des autonomen Systems (Autonomous System Number, ASN) als Kennung zu und geben die ASN an, die Ihr Peer-Router verwendet. Weitere Informationen finden Sie unter [Cloud Router erstellen, um ein VPC-Netzwerk mit einem Peer-Netzwerk zu verbinden](https://docs.cloud.google.com/network-connectivity/docs/router/how-to/create-router-vpc-network?hl=de).
*   _VPN-Tunnel_: Verbinden das Google Cloud -Gateway mit dem Peer-Gateway. Sie geben das Internet Key Exchange-Protokoll (IKE) an, das zum Einrichten des Tunnels verwendet werden soll. Sie können einen zuvor generierten IKE-Schlüssel eingeben oder einen neuen Schlüssel generieren und kopieren. Weitere Informationen finden Sie unter [IKE konfigurieren](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=de#configure_ike).

### Warum wir diese Aufgabe empfehlen

HA VPN bietet eine sichere und hochverfügbare Verbindung zwischen Ihrer vorhandenen Infrastruktur und Google Cloud.

Hinweis
-------

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.
*   Erstellen oder verknüpfen Sie ein Rechnungskonto in der Aufgabe [Abrechnung](#checklist-section-4).
*   Richten Sie die Hierarchie ein und weisen Sie Zugriffsrechte in der Aufgabe [Hierarchie und Zugriff](#checklist-section-5) zu.
*   Konfigurieren Sie Ihr Netzwerk in der Aufgabe [VPC-Netzwerke](#checklist-section-8).

Holen Sie die folgenden Informationen vom Administrator des Peer-Netzwerks ein:

*   _Name Ihres Peer-VPN-Gateways_: Das Gateway, zu dem Ihr Cloud VPN eine Verbindung herstellt.
*   _IP-Adresse 0 der Peer-Schnittstelle_: Eine externe IP-Adresse auf dem Gateway Ihres Peer-Netzwerks.
*   _IP-Adresse 1 der Peer-Schnittstelle_: Eine zweite externe Adresse. Sie können auch die IP-Adresse 0 wiederverwenden, wenn Ihr Peer-Netzwerk nur eine einzige externe IP-Adresse hat.
*   _Peer-ASN (Autonomous System Number)_: Eine eindeutige Kennung, die dem Router Ihres Peer-Netzwerks zugewiesen ist.
*   _Cloud Router-ASN_: Eine eindeutige Kennung, die Sie Ihrem Cloud Router zuweisen.
*   _Internet Key Exchange-Schlüssel (IKE)_: Schlüssel, die Sie zum Einrichten von zwei VPN-Tunneln mit Ihrem Peer-VPN-Gateway verwenden. Wenn Sie noch keine Schlüssel haben, können Sie sie während dieser Einrichtung generieren und dann auf Ihr Peer-Gateway anwenden.

### Verbindungen konfigurieren

Gehen Sie so vor, um Ihre VPC-Netzwerke mit Ihren Peer-Netzwerken zu verbinden:

1.  Melden Sie sich als Nutzer mit der Rolle „Organization Administrator“ an.
    
2.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Hybridkonnektivität**.
    
    [Zu "Hybridkonnektivität"](https://console.cloud.google.com/cloud-setup/hybrid-connectivity?hl=de)
    
4.  So rufen Sie die Aufgabendetails auf:
    
    1.  Sehen Sie sich die Aufgabenübersicht an und klicken Sie auf **Hybridkonnektivität starten**.
        
    2.  Klicken Sie auf die einzelnen Tabs, um mehr über die Hybridkonnektivität zu erfahren, und klicken Sie dann auf **Weiter**.
        
    3.  Sehen Sie sich an, was in den einzelnen Schritten der Aufgabe zu erwarten ist, und klicken Sie auf **Weiter**.
        
    4.  Prüfen Sie die Konfigurationsinformationen für das Peer-Gateway, die Sie erfassen müssen, und klicken Sie auf **Weiter**.
        
5.  Suchen Sie im Bereich **Hybridverbindungen** die VPC-Netzwerke heraus, die Sie entsprechend Ihren Geschäftsanforderungen verbinden möchten.
    
6.  Klicken Sie in der Zeile des ersten ausgewählten Netzwerks auf **Konfigurieren**.
    
7.  Lesen Sie im Bereich **Konfigurationsübersicht** die Beschreibung und klicken Sie auf **Weiter**.
    
8.  Führen Sie im Bereich **Google Cloud HA VPN-Gateway** folgende Schritte aus:
    
    1.  Geben Sie im Feld **Name des Cloud-VPN-Gateways** bis zu 60 Zeichen aus Kleinbuchstaben, Zahlen und Bindestrichen ein.
        
    2.  Wählen Sie im Bereich **Innerer IP-Stacktyp des VPN-Tunnels** einen der folgenden Stacktypen aus:
        
        *   **IPv4 und IPv6 (empfohlen)**: Kann sowohl IPv4- als auch IPv6-Traffic unterstützen. Wir empfehlen diese Einstellung, wenn Sie IPv6-Traffic in Ihrem Tunnel zulassen möchten.
        *   **IPv4**: Kann nur IPv4-Traffic unterstützen.
        
        Der Stacktyp bestimmt den Typ des Traffics, der im Tunnel zwischen Ihrem VPC-Netzwerk und Ihrem Peer-Netzwerk zulässig ist. Sie können den Stacktyp nach dem Erstellen des Gateways nicht mehr ändern. Hintergrundinformationen finden Sie hier:
        
        *   [IPv6-Unterstützung](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=de#ipv6-support)
        *   [Stacktypen und BGP-Sitzungen](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=de#ha-vpn-stack-types)
    3.  Klicken Sie auf **Weiter**.
        
9.  Gehen Sie im Bereich **Peer-VPN-Gateway** so vor:
    
    1.  Geben Sie im Feld **Name des Peer-VPN-Gateways** den Namen ein, der von Ihrem Peer-Netzwerkadministrator bereitgestellt wurde. Sie können bis zu 60 Zeichen aus Kleinbuchstaben, Zahlen und Bindestrichen eingeben.
        
    2.  Geben Sie im Feld **IP-Adresse 0 der Peer-Schnittstelle** die externe IP-Adresse der Peer-Gateway-Schnittstelle ein, die vom Administrator des Peer-Netzwerks bereitgestellt wurde.
        
    3.  Führen Sie im Feld **IP-Adresse 1 der Peer-Schnittstelle** einen der folgenden Schritte aus:
        
        *   Wenn Ihr Peer-Gateway eine zweite Schnittstelle hat, geben Sie die IP-Adresse ein.
        *   Wenn Ihr Peer-Gateway nur eine Schnittstelle hat, geben Sie dieselbe Adresse ein, die Sie in **IP-Adresse 0 der Peer-Schnittstelle** eingegeben haben.
        
        Weitere Informationen finden Sie unter [Peer-VPN-Gateway konfigurieren](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=de).
        
    4.  Klicken Sie auf **Weiter**.
        
10.  Gehen Sie im Bereich **Cloud Router** so vor:
     
     1.  Geben Sie im Feld **Cloud Router ASN** die Nummer des autonomen Systems ein, die Sie Ihrem Cloud Router zuweisen möchten. Diese erhalten Sie vom Administrator Ihres Peer-Netzwerks. Weitere Informationen finden Sie unter [Cloud Router erstellen](https://docs.cloud.google.com/network-connectivity/docs/router/how-to/create-router-vpc-network?hl=de#create_a).
         
     2.  Geben Sie im Feld **Peer-Router-ASN** die Nummer des autonomen Systems des Peer-Netzwerk-Routers ein, die Ihnen vom Administrator Ihres Peer-Netzwerks mitgeteilt wurde.
         
11.  Gehen Sie im Bereich **VPN-Tunnel 0** so vor:
     
     1.  Geben Sie im Feld **Tunnel 0 Name** bis zu 60 Zeichen aus Kleinbuchstaben, Zahlen und Bindestrichen ein.
         
     2.  Wählen Sie im Bereich **IKE-Version** eine der folgenden Optionen aus:
         
         *   **IKEv2 – empfohlen**: Unterstützt IPv6-Traffic.
         *   **IKEv1**: Verwenden Sie diese Einstellung, wenn Sie keinen IPv6-Traffic im Tunnel zulassen möchten.
         
         Weitere Informationen finden Sie unter [VPN-Tunnel konfigurieren](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=de#configure_vpn_tunnels).
         
     3.  Geben Sie im Feld **Vorinstallierter IKE-Schlüssel** den Schlüssel ein, den Sie in der Peer-Gateway-Konfiguration verwenden, wie vom Administrator Ihres Peer-Netzwerks angegeben. Wenn Sie keinen vorhandenen Schlüssel haben, können Sie auf **Generieren und kopieren** klicken und den Schlüssel dann an den Administrator Ihres Peer-Netzwerks weitergeben.
         
12.  Wiederholen Sie im Bereich **VPN-Tunnel 1** den vorherigen Schritt, um die Einstellungen für den zweiten Tunnel anzuwenden. Sie konfigurieren diesen Tunnel für Redundanz und zusätzlichen Durchsatz.
     
13.  Klicken Sie auf **Speichern**.
     
14.  Wiederholen Sie diese Schritte für alle anderen VPC-Netzwerke, die Sie mit Ihrem Peer-Netzwerk verbinden möchten.
     

Nach der Bereitstellung
-----------------------

Nachdem Sie Ihre [Google Cloud Einrichtungskonfiguration](#checklist-section-) bereitgestellt haben, führen Sie die folgenden Schritte aus, um sicherzustellen, dass Ihre Netzwerkverbindung vollständig ist:

1.  Wenden Sie sich an den Administrator Ihres Peer-Netzwerks, um Ihr Peer-Netzwerk an Ihre Einstellungen für die Hybridkonnektivität anzupassen. Nach der Bereitstellung erhalten Sie spezifische Anweisungen für Ihr Peer-Netzwerk, darunter:
    
    *   Tunnel-Einstellungen
    *   Firewall-Einstellungen
    *   IKE-Einstellungen
2.  Prüfen Sie die von Ihnen erstellten Netzwerkverbindungen. Mit Network Intelligence Center können Sie beispielsweise die Verbindung zwischen Netzwerken prüfen. Weitere Informationen finden Sie unter [Konnektivitätstests – Übersicht](https://docs.cloud.google.com/network-intelligence-center/docs/connectivity-tests/concepts/overview?hl=de).
    
3.  Wenn Sie eine stabilere Verbindung benötigen, verwenden Sie Cloud Interconnect. Weitere Informationen finden Sie unter [Network Connectivity-Produkt auswählen](https://docs.cloud.google.com/network-connectivity/docs/how-to/choose-product?hl=de).
    

Nächste Schritte
----------------

[Konfiguration bereitstellen](#checklist-section-): Dazu gehören Einstellungen für Hierarchie und Zugriff, Logging, Netzwerk und Hybridkonnektivität.

Einstellungen bereitstellen
---------------------------

### Bereitstellen oder herunterladen

Während Sie die Google Cloud Einrichtung abschließen, werden Ihre Einstellungen aus den folgenden Aufgaben in Terraform-Konfigurationsdateien kompiliert:

*   [Hierarchie und Zugriff](#checklist-section-5)
*   [Sicherheit](#checklist-section-6)
*   [Zentrales Logging und Monitoring](#checklist-section-7)
*   [VPC-Netzwerke](#checklist-section-8)
*   [Hybridkonnektivität](#checklist-section-9)

Um Ihre Einstellungen anzuwenden, prüfen Sie Ihre Auswahl und wählen Sie eine Bereitstellungsmethode aus.

### Wer führt diese Aufgabe aus?

Eine Person in der Gruppe `gcp-organization-admins@YOUR_DOMAIN`, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.

### Was Sie in dieser Aufgabe tun

Stellen Sie Konfigurationsdateien bereit, um die Einrichtungseinstellungen anzuwenden.

### Warum wir diese Aufgabe empfehlen

Sie müssen Konfigurationsdateien bereitstellen, um die ausgewählten Einstellungen anzuwenden.

### Hinweise

Sie müssen die folgenden Aufgaben abschließen:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.
*   Erstellen oder verknüpfen Sie ein Rechnungskonto in der Aufgabe [Abrechnung](#checklist-section-4).
*   Richten Sie die Hierarchie ein und weisen Sie Zugriffsrechte in der Aufgabe [Hierarchie und Zugriff](#checklist-section-5) zu.

Wir empfehlen außerdem folgende Aufgaben:

*   Sie können die Sicherheit verbessern, indem Sie unter [Sicherheit](#checklist-section-6) kostenlose Dienste einrichten.
*   Sie können Logdaten an einem zentralen Ort zusammenführen und alle Projekte über ein einzelnes Projekt in der Aufgabe [Zentrales Logging und Monitoring](#checklist-section-7) überwachen.
*   Konfigurieren Sie Ihr erstes Netzwerk in der Aufgabe [VPC-Netzwerke](#checklist-section-8).
*   Verbinden Sie Peer-Netzwerke mit Google Cloud in der Aufgabe [Hybridkonnektivität](#checklist-section-9).

### Konfigurationsdetails prüfen

So prüfen Sie, ob Ihre Konfigurationseinstellungen vollständig sind:

1.  Melden Sie sich in der Google Cloud Console als Nutzer der Gruppe `gcp-organization-admins@YOUR_DOMAIN` an, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.
    
2.  Wählen Sie oben auf der Seite in der Drop-down-Liste **Auswählen aus** Ihre Organisation aus.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Bereitstellen oder herunterladen**.
    
    [Bereitstellen oder herunterladen](https://console.cloud.google.com/cloud-setup/download?hl=de)
    
4.  Prüfen Sie die ausgewählten Konfigurationseinstellungen. Klicken Sie auf die folgenden Tabs und prüfen Sie Ihre Einstellungen:
    
    *   **Ressourcenhierarchie und Zugriff**
    *   **Sicherheit**
    *   **Logging und Monitoring**
    *   **VPC-Netzwerke**
    *   **Hybridkonnektivität**

### Konfiguration bereitstellen

Nachdem Sie die Konfigurationsdetails geprüft haben, können Sie eine der folgenden Optionen auswählen:

*   **Direkt über die Console bereitstellen:** Verwenden Sie diese Option, wenn Sie keinen vorhandenen Terraform-Bereitstellungsworkflow haben und eine einfache Bereitstellungsmethode wünschen. Sie können diese Methode nur einmal verwenden.
    
*   **Terraform-Datei herunterladen und bereitstellen:** Verwenden Sie diese Option, wenn Sie die Ressourcenverwaltung mit einem Terraform-Bereitstellungsworkflow automatisieren möchten. Sie können diese Methode mehrmals zum Herunterladen und Bereitstellen verwenden.
    

Sie haben für die Bereitstellung folgende Möglichkeiten:

### Direkt bereitstellen

Wenn Sie keinen vorhandenen Terraform-Workflow haben und eine einfache einmalige Bereitstellung wünschen, können Sie die Bereitstellung direkt über die Console vornehmen.

1.  Klicken Sie auf **Direkt bereitstellen**.
    
2.  Warten Sie einige Minuten, bis die Bereitstellung abgeschlossen ist.
    
3.  Wenn die Bereitstellung fehlschlägt, gehen Sie so vor:
    
    1.  Klicken Sie auf **Vorgang wiederholen**, um die Bereitstellung noch einmal zu versuchen.
    2.  Wenn die Bereitstellung nach mehreren Versuchen fehlschlägt, können Sie sich an einen Administrator wenden. Klicken Sie dazu auf **Administrator der Organisation kontaktieren**.

### Herunterladen und bereitstellen

Wenn Sie Ihre Bereitstellung mit Ihrem Terraform-Bereitstellungsworkflow iterieren möchten, laden Sie Konfigurationsdateien herunter und stellen Sie sie bereit.

1.  Klicken Sie auf **Als Terraform herunterladen**, um die Konfigurationsdatei herunterzuladen.
    
2.  Das heruntergeladene Paket enthält Terraform-Konfigurationsdateien, die auf den Einstellungen basieren, die Sie in den folgenden Aufgaben ausgewählt haben:
    
    *   **Hierarchie und Zugriff**
    *   **Sicherheit**
    *   **Zentrales Logging und Monitoring**
    *   **VPC-Netzwerke**
    *   **Hybridkonnektivität**
    
    Wenn Sie nur Konfigurationsdateien bereitstellen möchten, die für Ihre Aufgaben relevant sind, können Sie das Herunterladen irrelevanter Dateien vermeiden. Dazu müssen Sie die Kontrollkästchen für die Konfigurationsdateien deaktivieren, die Sie nicht benötigen.
    
3.  Klicken Sie auf **Herunterladen**. Ein `terraform.tar.gz`\-Paket mit den ausgewählten Dateien wird auf Ihr lokales Dateisystem heruntergeladen.
    
4.  Eine detaillierte Anleitung zur Bereitstellung finden Sie unter [Grundlage mit Terraform bereitstellen, das über die Console heruntergeladen wurde](https://docs.cloud.google.com/docs/enterprise/deploy-foundation-using-terraform-from-console?hl=de).
    

### Nächste Schritte

[Wählen Sie einen Supportplan aus.](#checklist-section-10)

### Support

In dieser Aufgabe wählen Sie einen Supportplan aus, der Ihren geschäftlichen Anforderungen entspricht.

### Wer führt diese Aufgabe aus?

Eine Person in der Gruppe `gcp-organization-admins@YOUR_DOMAIN`, die in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt wurde.

### Was Sie in dieser Aufgabe tun

Wählen Sie eine Supportstufe aus, die auf die Anforderungen Ihres Unternehmens zugeschnitten ist.

### Warum wir diese Aufgabe empfehlen

Ein Premium-Supportplan bietet geschäftskritischen Support, um Probleme mit der Hilfe von Experten von Google Cloudschnell zu beheben.

### Support-Option wählen

Sie erhalten automatisch den kostenlosen Basissupport, der Zugriff auf die folgenden Ressourcen umfasst:

*   [Dokumentation](https://docs.cloud.google.com/docs?hl=de)
*   [Community-Support und ‑Diskussionen](https://docs.cloud.google.com/support/docs/community?hl=de)
*   [Support bei Abrechnungsproblemen](https://docs.cloud.google.com/support/billing?hl=de)

Geschäftskunden wird empfohlen, sich für [Premium-Support](https://docs.cloud.google.com/support/docs/premium?hl=de) zu registrieren, der persönlichen technischen Support durch Entwickler des Google-Supportteams bietet. [Google Cloud Hier](https://docs.cloud.google.com/support?hl=de) können Sie Supportpläne vergleichen.

### Hinweis

Führen Sie die folgenden Schritte aus:

*   Erstellen Sie einen Super Admin-Nutzer und Ihre Organisation in der Aufgabe [Organisation](#checklist-section-1).
*   In der Aufgabe [Nutzer und Gruppen](#checklist-section-2) fügen Sie Nutzer hinzu und erstellen Gruppen.
*   Weisen Sie Gruppen in der Aufgabe [Administratorzugriff](#checklist-section-3) IAM-Rollen zu.

### Unterstützung aktivieren

Wählen Sie eine Supportoption aus.

1.  Sehen Sie sich die Supportstufen an und wählen Sie eine aus. Weitere Informationen finden Sie unter [Google Cloud Customer Care](https://docs.cloud.google.com/support?hl=de#support-plans).
    
2.  Melden Sie sich in der Google Cloud -Konsole mit einem Nutzerkonto der Gruppe `gcp-organization-admins@<your-domain>.com` an, die Sie in der Aufgabe [Nutzer und Gruppen](#checklist-section-2) erstellt haben.
    
3.  Gehen Sie zu **Google Cloud Einrichtung: Support**.
    
    [Zum Support](https://console.cloud.google.com/cloud-setup/support?hl=de)
    
4.  Prüfen Sie die Aufgabendetails und klicken Sie auf **Supportangebote ansehen**, um eine Supportoption auszuwählen.
    
5.  Nachdem Sie die Supportoption eingerichtet haben, kehren Sie zur Seite **Google Cloud Einrichtung: Support** zurück und klicken Sie auf **Aufgabe als erledigt markieren**.
    

### Nächste Schritte

Nachdem Sie die Google Cloud Einrichtung abgeschlossen haben, können Sie Ihre ursprüngliche Einrichtung erweitern, vorgefertigte Lösungen bereitstellen und Ihre vorhandenen Workflows migrieren. Weitere Informationen finden Sie unter [Ersteinrichtung erweitern und mit dem Erstellen beginnen](https://docs.cloud.google.com/docs/enterprise/manage-foundation?hl=de).

Sofern nicht anders angegeben, sind die Inhalte dieser Seite unter der [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/) und Codebeispiele unter der [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0) lizenziert. Weitere Informationen finden Sie in den [Websiterichtlinien von Google Developers](https://developers.google.com/site-policies?hl=de). Java ist eine eingetragene Marke von Oracle und/oder seinen Partnern.

Zuletzt aktualisiert: 2026-11-11 (UTC).
