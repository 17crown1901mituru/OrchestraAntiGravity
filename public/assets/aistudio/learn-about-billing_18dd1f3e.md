# Informationen zu Cloud Billing &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/learn-about-billing

Cloud Billing ist eine Sammlung von Diensten und Tools, mit denen Sie Ihre Kosten für Google Cloud und die Google Maps Platform im Blick behalten, analysieren, bezahlen und optimieren können.

*   **Für alle Dienste erforderlich**: Sie benötigen ein aktives Cloud-Rechnungskonto, das mit Ihren Google Cloud -Projekten verknüpft ist, um einen Google Cloud- oder Google Maps Platform-Dienst zu verwenden, einschließlich Diensten innerhalb der [kostenlosen Stufe](https://docs.cloud.google.com/free/docs/free-cloud-features?hl=de#free-tier).
    
*   **Kostentransparenz**: Sie erhalten Einblick darin, wie sich Ihre Nutzung auf die Kosten auswirkt.
    
*   **Kostenkontrolle**: Implementieren Sie Kontrollen, um unerwartete Ausgaben zu vermeiden.
    
*   **Kostenoptimierung**: Möglichkeiten zur Reduzierung Ihrer Cloud-Ausgaben finden.
    

Wichtige Komponenten und Beziehungen
------------------------------------

*   Cloud-Rechnungskonto: Hier wird festgelegt, wer für einen bestimmten Satz vonGoogle Cloud -Ressourcen bezahlt. Es ist mit einem Google-Zahlungsprofil verknüpft. Je nach Ihren Anforderungen haben Sie möglicherweise ein oder mehrere Rechnungskonten.
*   Google-Zahlungsprofil: Dieses wird im Google-Zahlungscenter verwaltet und enthält Ihre Zahlungsmethoden (z. B. Kreditkarten oder Bankkonten), Ihre Adresse und Ihre Steuerinformationen.
*   Google Cloud -Projekte: Ihre Ressourcen (z. B. virtuelle Maschinen, Speicher, Datenbanken) sind in Projekten organisiert. Jedes Projekt muss mit einem Cloud-Rechnungskonto verknüpft sein, um zu funktionieren. Ein einzelnes Rechnungskonto kann für viele Projekte verwendet werden.

[Weitere Informationen zur Ressourcenorganisation und Zugriffsverwaltung](https://docs.cloud.google.com/billing/docs/onboarding-checklist?hl=de)

Arten von Cloud-Rechnungskonten
-------------------------------

Es gibt zwei Haupttypen von Rechnungskonten:

*   **_Selfservice_** (online): Die Kosten werden automatisch über die verknüpfte Zahlungsmethode abgerechnet. Das kann passieren, wenn Ihre Kosten einen bestimmten Schwellenwert erreichen oder in einem regelmäßigen monatlichen Zyklus. Die meisten neuen Nutzer beginnen mit diesem Typ.
*   **_Monatliche Rechnungsstellung_** (offline): Sie erhalten eine monatliche Rechnung für die angefallenen Kosten und bezahlen per Scheck oder Banküberweisung. Für diese Option müssen in der Regel bestimmte Teilnahmevoraussetzungen erfüllt sein.

[Rechnungskontotyp und Abrechnungszeitraum für Cloud Billing ermitteln](https://docs.cloud.google.com/billing/docs/how-to/billing-cycle?hl=de)

Konto verwalten und Kosten ermitteln
------------------------------------

Sobald Sie ein Cloud-Rechnungskonto haben, müssen Sie es verwalten und wissen, wie Sie Ihre Ausgaben im Blick behalten.

### Grundlegende Kontoverwaltung

Zu den häufigen Aufgaben gehören:

*   [Neues Selfservice-Cloud-Rechnungskonto erstellen](https://docs.cloud.google.com/billing/docs/how-to/create-billing-account?hl=de)
*   [Mit Ihrem Rechnungskonto verknüpfte Projekte aufrufen](https://docs.cloud.google.com/billing/docs/how-to/view-linked?hl=de)
*   [Abrechnung für ein Projekt aktivieren, deaktivieren oder ändern](https://docs.cloud.google.com/billing/docs/how-to/modify-project?hl=de)
*   [Zahlungsmethoden hinzufügen, entfernen oder aktualisieren](https://docs.cloud.google.com/billing/docs/how-to/payment-methods?hl=de)
*   [Rechnungen, Kontoauszüge und Zahlungsbelege aufrufen](https://docs.cloud.google.com/billing/docs/how-to/get-invoice?hl=de)
*   [Cloud-Rechnungskonto schließen oder wieder öffnen](https://docs.cloud.google.com/billing/docs/how-to/close-or-reopen-billing-account?hl=de)

### Festlegen, wer auf die Abrechnung zugreifen kann

Google Cloud verwendet Identity and Access Management (IAM), um den Zugriff auf Cloud Billing zu steuern. Dem Ersteller des Rechnungskontos wird automatisch die Rolle „Billing Account Administrator“ zugewiesen. Ein Rechnungskontoadministrator kann Nutzern oder Gruppen verschiedene Rollen zuweisen, z. B.:

*   **Billing Account Administrator:** Vollständige Kontrolle über das Rechnungskonto.
*   **Billing Account Viewer:** Kann Kosten und Transaktionen aufrufen.
*   **Billing Account User:** Kann Projekte mit dem Rechnungskonto verknüpfen.

Es ist wichtig, diese Berechtigungen sorgfältig festzulegen, damit die richtigen Personen die richtige Zugriffsebene haben.

[Informationen zur Zugriffssteuerung und zu Berechtigungen für Cloud Billing](https://cloud.google.com/billing/docs/how-to/billing-access?hl=de) Sie können auch [benutzerdefinierte Rollen für Cloud Billing erstellen](https://cloud.google.com/billing/docs/how-to/custom-roles?hl=de).

### Kosten ansehen

Die Google Cloud Console bietet [mehrere Tools](https://docs.cloud.google.com/billing/docs/reports?hl=de), mit denen Sie Ihre Ausgaben nachvollziehen können:

*   **Abrechnungsberichte**: Hier können Sie Ihre Kostentrends einsehen, nach Projekt, Dienst, SKU und Labels filtern und die Auswirkungen von Gutschriften sehen. Dies ist das wichtigste Tool, um die Kostentreiber zu verstehen. [Informationen zur Verwendung von Abrechnungsberichten](https://docs.cloud.google.com/billing/docs/how-to/reports?hl=de)
*   **Kostentabelle**: Hier erhalten Sie eine detaillierte tabellarische Ansicht Ihrer Kosten für eine Rechnung oder einen Kontoauszug. Das ist für den Abgleich nützlich. [Kostendetails Ihrer Rechnung ansehen](https://docs.cloud.google.com/billing/docs/how-to/cost-table?hl=de)
*   **Kostenaufschlüsselung**: Hier sehen Sie auf einen Blick Ihre Kosten nach Abzug der Einsparungen. So können Sie die Nettoauswirkungen von Gutschriften und Rabatten nachvollziehen. [Informationen zu Einsparungen mit Kostenaufschlüsselungsberichten](https://docs.cloud.google.com/billing/docs/how-to/cost-breakdown?hl=de)

Cloud-Ausgaben überwachen und optimieren
----------------------------------------

Cloud Billing bietet nicht nur Kostentransparenz, sondern hilft Ihnen auch, Ihre Ausgaben aktiv zu kontrollieren und zu senken.

### Budgets und Ausgabenbenachrichtigungen festlegen

Sie können Budgets erstellen, um Überraschungen auf Ihrer Rechnung zu vermeiden.

*   Geplante und tatsächliche Ausgaben im Blick behalten: Sie können Ihre Gebühren mit einem von Ihnen festgelegten Zielbetrag vergleichen.
*   Umfang: Budgets können für das gesamte Rechnungskonto, bestimmte Projekte, Dienste oder Labels gelten.
*   Benachrichtigungen: Legen Sie Schwellenwerte fest (z. B. bei 50 %, 90 % und 100 % Ihres Budgetbetrags), um E-Mail-Benachrichtigungen an wichtige Stakeholder auszulösen, wenn die Kosten diese Werte überschreiten.

Budgets begrenzen Ihre Ausgaben nicht automatisch, aber sie liefern wichtige Warnungen.

[Informationen zum Erstellen, Bearbeiten oder Löschen von Budgets und Budgetbenachrichtigungen](https://cloud.google.com/billing/docs/how-to/budgets?hl=de)

### Kostenanomalien ansehen und verwalten

Mit der Anomalieerkennung können Sie unerwartete Kosten in den Projekten Ihres Rechnungskontos verwalten. Anomalien sind Spitzen oder Abweichungen bei den Nutzungskosten, die von Ihren erwarteten Ausgaben abweichen.

Im Anomalien-Dashboard können Sie Folgendes tun:

*   Untersuchen Sie die Ursachen von Anomalien.
*   Sie können Ihre Ansicht anpassen, indem Sie Grenzwerte für die Auswirkungen auf die Kosten und den Abweichungsprozentsatz festlegen.
*   Automatisierte Warnungen und Benachrichtigungen für erkannte Anomalien verwalten.

[Weitere Informationen zur Anomalieerkennung für Kosten](https://docs.cloud.google.com/billing/docs/how-to/manage-anomalies?hl=de)

### Kosten optimieren (FinOps)

Cloud Billing-Tools unterstützen Sie bei Ihren Financial Operations (FinOps) und helfen Ihnen, den geschäftlichen Nutzen der Cloud zu maximieren. Wichtige Hebel für die **Optimierung**:

*   **FinOps-Hub**: Hier finden Sie personalisierte Empfehlungen zur Kostenoptimierung und können den FinOps-Zustand im Blick behalten. [FinOps-Hub entdecken](https://docs.cloud.google.com/billing/docs/how-to/finops-hub?hl=de)
*   **Rabatte für zugesicherte Nutzung (CUDs)**: Sie erhalten stark ermäßigte Preise, wenn Sie sich verpflichten, über einen Zeitraum von einem oder drei Jahren eine bestimmte Menge an Ressourcen zu nutzen oder einen bestimmten Betrag auszugeben. Ideal für stabile Arbeitslasten. [Effektivität Ihrer Rabatte für zugesicherte Nutzung analysieren](https://docs.cloud.google.com/billing/docs/how-to/cud-analysis?hl=de)

Nächste Schritte
----------------

*   Weitere Informationen zu den grundlegenden Konzepten finden Sie in der [Cloud Billing-Übersicht](https://docs.cloud.google.com/billing/docs/concepts?hl=de).
*   Probieren Sie die [interaktiven Anleitungen](https://docs.cloud.google.com/billing/docs/interactive-tutorials?hl=de) in der Google Cloud Console aus.
*   [Erstellen Sie ein Rechnungskonto.](https://docs.cloud.google.com/billing/docs/how-to/create-billing-account?hl=de)
*   [Kosten mit Abrechnungsberichten im Blick behalten](https://docs.cloud.google.com/billing/docs/reports?hl=de)
*   [Kosten mit FinOps-Hub optimieren](https://docs.cloud.google.com/billing/docs/how-to/finops-hub?hl=de)
