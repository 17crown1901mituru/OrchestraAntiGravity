# Cloud Quotas API – Übersicht &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/api-overview

Mit der Cloud Quotas API können Sie [Kontingente](https://docs.cloud.google.com/docs/quotas/overview?hl=de) auf Projektebene programmatisch anpassen und [Anfragen zur Kontingentanpassung](https://docs.cloud.google.com/docs/quotas/overview?hl=de#about_increase_requests) auf Projektebene automatisieren. Sie können die Cloud Quotas API beispielsweise für Folgendes verwenden:

*   **Kontingentanpassungen automatisieren**: Mit der Cloud Quotas API können Sie Kontingentanpassungen basierend auf Ihren eigenen Kriterien anfordern. Um beispielsweise Fehler aufgrund von Kontingentüberschreitungen zu vermeiden, können Sie die API verwenden, um eine Kontingentanpassung programmatisch anzufordern, wenn Compute Engine-Ressourcen 80 % des verfügbaren Kontingents erreichen.
    
*   **Kontingentkonfigurationen für mehrere Projekte wiederverwenden**: Mit der Cloud Quotas API können Sie Ihre Kontingentkonfigurationen von Projekt zu Projekt klonen. Wenn es einen bekannten Satz von Kontingenten gibt, die für jedes neue Google Cloud Projekt erhöht werden müssen, können Sie die Cloud Quotas API verwenden, um dies in der Erstellungslogik Ihres Projekts zu automatisieren. Anfragen zur Kontingentanpassung unterliegen der Genehmigung Google Cloud .
    
*   **Anfragen zu Kundenkontingenten bearbeiten**: Wenn Sie ein in Google Cloudeingebundener SaaS-Anbieter sind, erhalten Sie möglicherweise Anfragen zur Kontingenterhöhung über ein anderes kundenseitiges Portal als die Google Cloud Console. Diese Anfragen müssen zur Verarbeitung an Google Cloud weitergeleitet werden. Die Cloud Quotas API kann Kundenanfragen automatisch weiterleiten.
    
*   **Versionsverwaltung für die Clientkonfiguration aktivieren**: Die Cloud Quotas API ist deklarativ. Sie können Kontingentkonfigurationen als Code behandeln und Konfigurationen in Ihrem eigenen versionierten System für Verlauf und Rollback speichern.
    

Beschränkungen
--------------

Für Cloud-Kontingente gelten die folgenden Einschränkungen:

*   In den meisten Fällen müssen Anpassungen zur _Erhöhung_ des Kontingents auf [Projektebene](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=de) vorgenommen werden. Eine begrenzte Anzahl von Produkten unterstützt [Anpassungen zur Kontingenterhöhung](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=de#organizations) auf Organisationsebene. Ob ein Google Cloud Produkt Anpassungen zur Kontingenterhöhung auf Organisationsebene unterstützt, erfahren Sie in der Dokumentation für dieses Produkt.
    
*   Sie können Anpassungen zur _Verringerung_ des Kontingents für Kontingente auf Projekt-, Organisations- und [Ordnerebene](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=de#folders) anfordern.
    

Dienstendpunkt
--------------

Ein Dienstendpunkt ist eine Basis-URL, die die Netzwerkadresse eines API-Dienstes angibt. Ein Dienst kann mehrere Endpunkte haben. Der Cloud Quotas API-Dienst hat den folgenden Endpunkt und alle URIs beziehen sich auf ihn:

`https://cloudquotas.googleapis.com`

Erforderliche Rollen
--------------------

Um die Berechtigungen zu erhalten, die Sie für den Zugriff auf die Ressourcen `cloudquotas_quotaPreferences`, `cloudquotas_quotaInfos`, und `cloudquotas_quotaAdjusterSettings` benötigen, bitten Sie Ihren Administrator, Ihnen die IAM-Rolle Cloud-Kontingente-Administrator (`cloudquotas.admin`) für das Projekt zuzuweisen. Weitere Informationen zum Zuweisen von Rollen finden Sie unter [Zugriff auf Projekte, Ordner und Organisationen verwalten](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=de).

Diese vordefinierte Rolle enthält die Berechtigungen, die für den Zugriff auf die Ressourcen `cloudquotas_quotaPreferences`, `cloudquotas_quotaInfos` und `cloudquotas_quotaAdjusterSettings` erforderlich sind. Maximieren Sie den Abschnitt **Erforderliche Berechtigungen**, um die genau erforderlichen Berechtigungen anzuzeigen:

#### Erforderliche Berechtigungen

Die folgenden Berechtigungen sind für den Zugriff auf die Ressourcen `cloudquotas_quotaPreferences`, `cloudquotas_quotaInfos` und `cloudquotas_quotaAdjusterSettings` erforderlich:

*   `cloudquotas.quotas.update`
*   `cloudquotas.quotas.get`
*   `monitoring.timeSeries.list`
*   `resourcemanager.projects.get`
*   `resourcemanager.projects.list`

Sie können diese Berechtigungen auch mit [benutzerdefinierten Rollen](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=de) oder anderen [vordefinierten Rollen](https://docs.cloud.google.com/iam/docs/roles-overview?hl=de#predefined) erhalten.

API-Ressourcenmodell
--------------------

Das Cloud Quotas API-Ressourcenmodell besteht aus zwei Ressourcen: `QuotaPreference` und `QuotaInfo`.

### Kontingentpräferenz

Die Ressource `QuotaPreference` steht für Ihre Kontingenteinstellung für eine bestimmte [Dimensionskombination](https://docs.cloud.google.com/docs/quotas/configure-dimensions?hl=de#dimension_precedence). Verwenden Sie diese Ressource, um die Kontingente in Ihren Projekten, Ordnern oder Organisationen anzupassen.

#### Bevorzugten Wert für eine Region festlegen

Das folgende Beispiel zeigt eine `QuotaPreference`\-Ressource in einer `CreateQuotaPreference`\-Methode.

{
    "service": "compute.googleapis.com",
    "quotaId": "GPUS-PER-GPU-FAMILY-per-project-region",
    "quotaConfig": {
        "preferredValue": 100
    },
    "dimensions": {
        "region": "us-central1"
    }
}

Der `preferredValue` von 100 gibt an, dass der Anforderer möchte, dass das Kontingent `GPUS-PER-GPU-FAMILY-per-project-region` auf diesen Wert gesetzt wird. Das Feld „Dimensionen“ gibt an, dass die Einstellung nur für die Region `us-central1` gilt.

#### Zugewiesenen Wert prüfen

Sehen Sie sich Ihre Kontingenteinstellung an und prüfen Sie den zugewiesenen Wert im Feld `grantedValue`.

Führen Sie dazu im Terminal folgenden Befehl aus:

gcloud alpha quotas preferences describe QUOTA\_PREFERENCE\_ID \--project\=PROJECT

Ersetzen Sie Folgendes:

*   `QUOTA_PREFERENCE_ID`: die ID Ihrer Kontingenteinstellung. Dies ist der Wert, der beim Erstellen der Kontingenteinstellung angegeben wurde.
*   `PROJECT`: die ID oder Nummer Ihres Google Cloud Projekts.

Wenn Sie eine Anfrage zur Kontingentanpassung gestellt haben und die Anfrage teilweise genehmigt oder abgelehnt wurde, wird nach dem Feld `grantedValue` das Feld `stateDetail` angezeigt. Im Feld `grantedValue` wird die vorgenommene Anpassung angezeigt und das Feld `stateDetail` enthält weitere Informationen zum endgültigen Status der Anfrage. So wird beispielsweise ein teilweise genehmigter Status erklärt oder der Grund für eine Ablehnung angegeben.

Ob der zugewiesene Wert der endgültig genehmigte Wert ist, sehen Sie im Feld `reconciling`. Wenn Ihre Anfrage noch geprüft wird, ist das Feld `reconciling` auf `true` gesetzt. Wenn das Feld `reconciling` auf `false` gesetzt oder weggelassen wird, ist der zugewiesene Wert der endgültig genehmigte Wert.

Die folgenden Code-Snippets zeigen Beispiele für das Kontingenteinstellungsobjekt. Sie verwenden eine Demokontingenteinstellung mit der ID `compute_googleapis_com-gpus-us-central1`.

### gcloud

Wenn Sie Ihre Kontingenteinstellung mit der gcloud CLI aufrufen, sieht die Ausgabe ungefähr so aus:

createTime: '2023-01-15T01:30:15.01Z'
dimensions:
    region: us-central1
name: projects/PROJECT\_NUMBER/locations/global/quotaPreferences/compute\_googleapis\_com-gpus-us-central1
quotaConfig:
    grantedValue: '100'
    preferredValue: '100'
    traceId: 123acd-345df23
    requestOrigin: ORIGIN\_UNSPECIFIED
service: compute.googleapis.com
quotaId: GPUS-PER-GPU-FAMILY-per-project-region
updateTime: '2023-01-16T02:35:16.01Z'

### REST

Wenn Sie Ihre Kontingenteinstellung mit der Cloud Quotas API aufrufen, sieht die Ausgabe ungefähr so aus:

{
    "name": "projects/PROJECT\_NUMBER/locations/global/quotaPreferences/compute\_googleapis\_com-gpus-us-central1",
    "service": "compute.googleapis.com",
    "quotaId": "GPUS-PER-GPU-FAMILY-per-project-region",
    "quotaConfig": {
        "preferredValue": 100,
        "grantedValue": 100,
        "traceId": "123acd-345df23",
        "requestOrigin": "ORIGIN\_UNSPECIFIED"
    },
    "dimensions": {
        "region": "us-central1"
    },
    "createTime": "2023-01-15T01:30:15.01Z",
    "updateTime": "2023-01-16T02:35:16.01Z"
}

Diese Ausgabe enthält die folgenden Werte:

*   `PROJECT_NUMBER`: eine automatisch generierte eindeutige Kennzeichnung für das Projekt.

Die Antwort zeigt ein `grantedValue` von 100, was bedeutet, dass der `preferredValue` aus dem vorherigen Beispiel genehmigt und erfüllt wurde. Einstellungen für verschiedene Dimensionen sind unterschiedliche `QuotaPreference`\-Ressourcen. Beispielsweise sind `QuotaPreference` für CPU in den Regionen `us-central1` und `us-east1` zwei unterschiedliche Ressourcen.

#### Kontingentpräferenz ist erforderlich

`QuotaPreference`\-Ressourcen werden verwendet, um Ihren bevorzugten Wert für ein bestimmtes Kontingent anzugeben. Der aktuelle Wert für ein bestimmtes Kontingent basiert auf:

*   `QuotaPreference`\-Anfragen von Ihnen.
    
*   Von genehmigte Anfragen zur Kontingenterhöhung Google Cloud.
    
*   Änderungen an Kontingenten, die von initiiert werden Google Cloud.
    

Das Löschen eines `QuotaPreference` wird nicht unterstützt. Sie können jedoch einen bevorzugten Kontingentwert festlegen, der niedriger als der Google Cloud genehmigte Wert ist, um weitere Leitlinien hinzuzufügen.

Weitere Informationen zur Ressource `QuotaPreference` finden Sie in der [Referenz zur Cloud Quotas API](https://docs.cloud.google.com/docs/quotas/reference/rest?hl=de).

Weitere Informationen zu den `QuotaPreference`\-Abfragen finden Sie unter [Häufige Anwendungsfälle implementieren](https://docs.cloud.google.com/docs/quotas/implement-common-use-cases?hl=de).

### Kontingentinformationen

`QuotaInfo` ist eine schreibgeschützte Ressource, die Informationen zu einem bestimmten Kontingent für ein bestimmtes Projekt, einen Ordner oder eine Organisation bereitstellt. Es zeigt Informationen aus den von Google Cloud Diensten definierten Kontingenten und alle erfüllten Kontingent anpassungen an, die von Kunden initiiert wurden. Die Ressource `QuotaInfo` enthält Informationen wie Metadaten, Containertyp und Dimensionen.

#### Unterschiedliche Kontingentwerte nach Region festlegen

Das folgende Beispiel für eine `QuotaInfo`\-Ressource zeigt, dass das CPU-Kontingent für das Projekt 200 für die Region `us-central1` und 100 für alle anderen Regionen beträgt.

{
    "name": "projects/PROJECT\_NUMBER/locations/global/services/compute.googleapis.com/quotaInfos/CPUS-per-project-region",
    "quotaId": "CPUS-per-project-region",
    "metric": "compute.googleapis.com/cpus",
    "containerType": "PROJECT",
    "dimensions": \[
        "region"
    \],
    "isPrecise": true,
    "quotaDisplayName": "CPUs per project per region",
    "metricDisplayName": "CPUs",
    "dimensionsInfo": \[
        {
            "dimensions": {
                "region": "us-central1"
            },
            "details": {
                "quotaValue": 200,
                "resetValue": 200
            },
            "applicableLocations": \[
                "us-central1",
            \]
        },
        {
            "details": {
                "quotaValue": 100,
                "resetValue": 100
            },
            "applicableLocations": \[
                "us-central2",
                "us-west1",
                "us-east1"
            \]
        }
    \]
}

Diese Ausgabe enthält die folgenden Werte:

*   `PROJECT_NUMBER`: eine automatisch generierte eindeutige Kennzeichnung für das Projekt.

#### Globales Kontingent festlegen

Das folgende Beispiel für eine `QuotaInfo`\-Ressource zeigt ein Ratenkontingent mit einem Aktualisierungsintervall pro Minute. Die Dimensionen sind leer, d. h., es handelt sich um ein globales Kontingent. Alle Kontingente ohne Regions- oder Zonendimension sind global.

{
    "name": "projects/PROJECT\_NUMBER/locations/global/services/compute.googleapis.com/quotaInfos/ReadRequestsPerMinutePerProject",
    "quotaId": "ReadRequestsPerMinutePerProject",
    "metric": "compute.googleapis.com/read\_requests",
    "refreshInterval": "minute",
    "containerType": "PROJECT",
    "dimensions": \[\],
    "isPrecise": false,
    "quotaDisplayName": "Read Requests per Minute",
    "metricDisplayName": "Read Requests",
    "dimensionsInfo": \[
        {
            "details": {
                "quotaValue": 100,
                "resetValue": 200
            },
            "applicableLocations": \[
                "global"
            \]
        }
    \]
}

Diese Ausgabe enthält die folgenden Werte:

*   `PROJECT_NUMBER`: eine automatisch generierte eindeutige Kennzeichnung für das Projekt.

Weitere Informationen zur Ressource `QuotaInfo` finden Sie in der [Referenz zur Cloud Quotas API](https://docs.cloud.google.com/docs/quotas/reference/rest?hl=de).

Weitere Informationen zu den `QuotaPreference`\-Abfragen finden Sie unter [Häufige Anwendungsfälle implementieren](https://docs.cloud.google.com/docs/quotas/implement-common-use-cases?hl=de).

### Einstellungen für Kontingentanpassung

Die Ressource `QuotaAdjusterSettings` steht für Ihre Einstellungen für die Kontingentanpassung für ein bestimmtes Projekt. Wenn die Kontingentanpassung aktiviert ist, überwacht sie Ihre Nutzung der angegebenen Ressourcen und sendet Anfragen zur Kontingentanpassung, wenn die Ressourcennutzung den Kontingentwert erreicht.

*   Verwenden Sie einen [GET](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaAdjusterSettings/getQuotaAdjusterSettings?hl=de) Vorgang, um die `QuotaAdjusterSettings` Ressource abzurufen und die aktuellen Einstellungen für die Kontingentanpassung für ein Projekt aufzurufen.
    
*   Verwenden Sie einen [PATCH](https://docs.cloud.google.com/docs/quotas/reference/rest/v1/projects.locations.quotaAdjusterSettings/updateQuotaAdjusterSettings?hl=de) -Vorgang, um die Kontingentanpassung für ein Projekt zu aktivieren und die folgenden Optionen für die Ressource `QuotaAdjusterSettings` festzulegen:
    
      "quota_adjuster_settings" :{
             "name": "projects/PROJECT_NUMBER/locations/global/quotaAdjusterSettings",
             "enablement": ENABLED,
        }
    
    Ersetzen Sie `PROJECT_NUMBER` durch die eindeutige Kennung für Ihr Projekt.
    

Weitere Informationen finden Sie unter [Kontingentanpassung aktivieren](https://docs.cloud.google.com/docs/quotas/quota-adjuster?hl=de#enable) und [Kontingentanpassung deaktivieren](https://docs.cloud.google.com/docs/quotas/quota-adjuster?hl=de#disable).

Ressourcennamen
---------------

Ressourcen sind benannte Entitäten und werden durch Ressourcennamen identifiziert. Ressourcennamen werden in allen Anfragen und Antworten verwendet und jede Ressource muss einen eigenen eindeutigen Ressourcennamen haben. Jeder Ressourcenname wird durch eine Reihe von Feldern codiert.

### Ressource für Kontingentpräferenz

Die Namenskonvention für eine `QuotaPreference`\-Ressource verwendet das folgende Muster:

projects/PROJECT\_NUMBER/locations/global/quotaPreferences/QUOTA\_PREFERENCE\_ID

Sie können `quotaPreferenceId` beim Erstellen einer Kontingenteinstellung festlegen. Andernfalls wird eine ID generiert. Es wird empfohlen, den Dienstnamen, die Kontingent-ID, den Standort und andere Dimensionen über ein `quotaPreferenceId`\-Benennungsschema zu codieren. Die `quotaPreferenceId` muss für das Projekt, den Ordner oder die Organisation eindeutig sein.

Beispiel: `quotaPreference` Ein Muster zum Codieren Ihrer Kontingentpräferenz-ID sieht so aus:

SERVICE\_LOCATION\_DIMENSION1-VALUES-IN-ORDER

Das folgende Beispiel veranschaulicht dieses Muster:

compute\_us-central1\_nvidia-200

Bei einem Ressourcennamen sollten Sie die [`GET`](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences/get?hl=de) Methode verwenden, um eine `QuotaPreference`abzurufen. Sie können auch die [`PATCH`](https://docs.cloud.google.com/docs/quotas/reference/rest/v1beta/organizations.locations.quotaPreferences/patch?hl=de) Methode mit aktivierter Option `allow_missing` aufrufen, um eine `QuotaPreference` zu erstellen oder zu aktualisieren.

### Ressource für Kontingentinformationen

Die Namenskonvention für eine `QuotaInfo`\-Ressource verwendet das folgende Muster:

projects/PROJECT\_NUMBER/locations/global/services/SERVICE\_NAME/quotaInfos/QUOTA\_ID

### Ressource für Einstellungen für Kontingentanpassung

Die Namenskonvention für eine `QuotaAdjusterSettings`\-Ressource verwendet das folgende Muster:

projects/PROJECT\_NUMBER/locations/global/quotaAdjusterSettings

Nächste Schritte
----------------

*   [Gängige Anwendungsfälle implementieren](https://docs.cloud.google.com/docs/quotas/implement-common-use-cases?hl=de)
    
*   Cloud Quotas API [Referenz](https://docs.cloud.google.com/docs/quotas/reference/rest?hl=de)
    
*   Lernen Sie [Kontingente](https://docs.cloud.google.com/docs/quotas/overview?hl=de) kennen
