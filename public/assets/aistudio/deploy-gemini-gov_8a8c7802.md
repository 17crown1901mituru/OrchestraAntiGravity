# Indicazioni per il deployment di Gemini for Government &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/deploy-gemini-gov

Last reviewed 2026-09-24 UTC

Questo documento fornisce indicazioni tecniche per le agenzie federali e i dipartimenti del DoD degli Stati Uniti sul deployment e l'utilizzo di Gemini per la pubblica amministrazione in conformità ai requisiti FedRAMP High e DoD Impact Level 4 (IL4). Questo documento descrive quali servizi e funzionalità sono inclusi nei confini di autorizzazione e i passaggi per aiutarti a soddisfare i tuoi obblighi di conformità.

Gemini per la pubblica amministrazione utilizza [Assured Workloads](https://docs.cloud.google.com/assured-workloads/docs/overview?hl=it) per soddisfare i requisiti di conformità. Devi eseguire il deployment di tutte le risorse di Gemini in Government all'interno di una cartella Assured Workloads configurata per il tuo regime di conformità specifico (FedRAMP High o IL4).

Dipendenze dei prodotti principali
----------------------------------

Gemini per la pubblica amministrazione si basa su diversi serviziGoogle Cloud . La tabella seguente elenca lo stato di conformità di ogni servizio.

| ServizioGoogle Cloud | Stato FedRAMP High | Stato IL4 |
| --- | --- | --- |
| 
Gemini Enterprise

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

AI generativa su Gemini Enterprise Agent Platform (in precedenza AI generativa su Vertex AI)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

BigQuery

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

Cloud Storage

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

Looker (Google Cloud core)

 | 

Organizzazione Google Cloud

 | 

Inviata

 |

La tabella seguente elenca i servizi e le funzionalità che puoi utilizzare all'interno di Gemini per la pubblica amministrazione per i deployment FedRAMP High e IL4.

| Funzionalità | FedRAMP High | IL4 |
| --- | --- | --- |
| 
[Completamento automatico](https://docs.cloud.google.com/gemini/enterprise/docs/configure-autocomplete?hl=it)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

[Controlli di pubblicazione](https://docs.cloud.google.com/gemini/enterprise/docs/configure-serving-controls?hl=it)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

Datastore autorizzati come [Cloud Storage](https://docs.cloud.google.com/gemini/enterprise/docs/connect-cloud-storage?hl=it) e [BigQuery](https://docs.cloud.google.com/gemini/enterprise/docs/connect-bigquery?hl=it)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

[Grounding web per aziende](https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/grounding/web-grounding-enterprise?hl=it)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

[Caricamento di documenti](https://docs.cloud.google.com/gemini/enterprise/docs/assistant-chat?hl=it#upload_and_chat_about_files) da computer locali

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

Possibilità per gli utenti finali di [selezionare i modelli](https://docs.cloud.google.com/gemini/enterprise/docs/answer-generation-models?hl=it)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

[Memorizzazione dei dati nella cache in-memory](https://docs.cloud.google.com/gemini-enterprise-agent-platform/resources/zero-data-retention?hl=it#in-memory-caching)

 | 

Organizzazione Google Cloud

 | 

Organizzazione Google Cloud

 |
| 

[Agente Deep Research](https://docs.cloud.google.com/gemini/enterprise/docs/research-assistant?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

Creazione di agenti no-code utilizzando [Agent Designer](https://docs.cloud.google.com/gemini/enterprise/docs/agent-designer?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Galleria degli agenti](https://docs.cloud.google.com/gemini/enterprise/docs/agent-gallery?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Generazione di immagini con Nano Banana](https://docs.cloud.google.com/gemini/enterprise/docs/assistant-analyze?hl=it#generate_an_image)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Gemini Notebook Enterprise](https://docs.cloud.google.com/gemini/enterprise/notebooklm-enterprise/docs/overview?hl=it) (esclude overview audio, infografica, presentazione e overview video)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Model Armor](https://docs.cloud.google.com/gemini/enterprise/docs/enable-model-armor?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

Connettori federati di Google Workspace (include [Gmail](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/gmail?hl=it), [Google Drive](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/gdrive?hl=it) e [Google Calendar](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/gcal?hl=it))

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

Connettori federati di terze parti (come Microsoft Office 365 Government Community Cloud (GCC), Office GCC High e DoD e [Salesforce](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/salesforce?hl=it))

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Canvas](https://docs.cloud.google.com/gemini/enterprise/docs/assistant-canvas?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Progetti](https://docs.cloud.google.com/gemini/enterprise/docs/projects?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |
| 

[Skill](https://docs.cloud.google.com/gemini/enterprise/docs/skills?hl=it)

 | 

Organizzazione Google Cloud

 | 

Revisione DISA

 |

Supporto del modello
--------------------

Questa sezione descrive il supporto per i modelli integrati e di terze parti.

### Supporto dei modelli integrati

Puoi utilizzare alcuni modelli integrati che supportano gli [endpoint multiregionali degli Stati Uniti](https://docs.cloud.google.com/gemini-enterprise-agent-platform/resources/data-residency?hl=it#ml-processing-google-models), sono in disponibilità generale e supportano gli impegni di residenza dei dati (DRZ).

Se hai bisogno di impegni DRZ, è tua responsabilità limitare l'accesso ai modelli che supportano solo endpoint globali. Per saperne di più su come limitare l'accesso a questi modelli, consulta [Limita l'utilizzo degli endpoint](https://docs.cloud.google.com/docs/security/compliance/restrict-endpoint-usage?hl=it).

La tabella seguente elenca i modelli integrati che supportano FedRAMP High, IL4, IL5, ITAR e CJIS.

  

| Modello | FedRAMP High | IL4 e IL5 | ITAR | CJIS |
| --- | --- | --- | --- | --- |
| Gemini 4 Argon(`gemini-4-argon`) |  |  |  |  |
| Gemini 3.8 Flash(`gemini-3.8-flash`) |  |  |  |  |
| Gemini 3.7 Flash(`gemini-3.7-flash`) |  |  |  |  |
| Gemini 3.6 Flash(`gemini-3.6-flash`) |  |  |  |  |
| Gemini 3.5 Flash-Lite(`gemini-3.5-flash-lite`) |  |  |  |  |
| Gemini 3.5 Flash(`gemini-3.5-flash`) |  |  |  |  |
| Gemini 3.1 Flash Image(`gemini-3.1-flash-image`) |  |  |  |  |
| Gemini 3.1 Flash-Lite(`gemini-3.1-flash-lite`) |  |  |  |  |
| Claude Haiku 5.5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Opus 5.5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Sonnet 5.5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Sonnet 5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Opus 5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Fable 5.1 di Anthropic su Google Cloud |  |  |  |  |
| Claude Fable 5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Haiku 4.5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Opus 4.5 di Anthropic su Google Cloud |  |  |  |  |
| Claude Opus 4.8 di Anthropic su Google Cloud |  |  |  |  |
| Claude Opus 4.7 di Anthropic su Google Cloud |  |  |  |  |
| Claude Opus 4.6 di Anthropic su Google Cloud |  |  |  |  |
| Claude Sonnet 4.6 di Anthropic su Google Cloud |  |  |  |  |

### Supporto di modelli di terze parti

Google Cloud è responsabile della gestione delle autorizzazioni per la sua infrastruttura di gestione e i container personalizzati, in modo che tu possa eseguire il deployment dei tuoi modelli di terze parti per FedRAMP High e DoD IL4 e IL5.

Sebbene Google non convalidi lo stato di conformità dei deployment di modelli di terze parti o l'utilizzo di questi modelli di terze parti, puoi utilizzare i servizi autorizzati (come l'AI generativa su Gemini Enterprise Agent Platform e Gemini Enterprise Agent Platform Inference: Batch and Online) per interagire con più di 200 modelli linguistici di grandi dimensioni (LLM) proprietari, di terze parti e open source disponibili in Model Garden. È tua responsabilità assicurarti che i modelli di terze parti vengano sottoposti a deployment, configurati e protetti in conformità ai requisiti di conformità FedRAMP e DoD IL4 e IL5. Per saperne di più, consulta [Panoramica dei modelli di cui è stato eseguito il deployment autonomo](https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/model-garden/self-deployed-models?hl=it) e [Controlla l'accesso ai modelli di Model Garden](https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/control-model-access?hl=it).

Supporto dei connettori
-----------------------

I connettori recuperano i dati da Google o da origini dati di terze parti in Gemini Enterprise. I due tipi di connettori disponibili sono i connettori federati e i connettori di importazione.

I connettori federati recuperano i dati da origini dati Google o di terze parti supportate. Quando un utente esegue una query, Gemini Enterprise unisce la richiesta all'origine dati connessa, recupera i risultati corrispondenti in tempo reale e li restituisce nella risposta alla query. I dati dell'origine dati connessa non vengono conservati nell'indice di Gemini Enterprise.

I connettori di importazione copiano e indicizzano i dati dal sistema di origine inGoogle Cloud. Quando un utente esegue una query, Gemini Enterprise recupera i risultati da questo indice persistente per fornire la risposta. I dati importati rimangono nell'indice finché non vengono [rimossi o aggiornati](https://docs.cloud.google.com/gemini-enterprise-agent-platform/build/vector-search/create-manage-index?hl=it) esplicitamente.

Puoi attivare e configurare i connettori dalla console di Gemini Enterprise. Per limitare le origini dati e le destinazioni in uscita consentite, puoi utilizzare i vincoli delle policy dell'organizzazione.

### Assistenza per i connettori di terze parti

I [connettori federati di terze parti](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/connect-third-party-data-source?hl=it) (disponibili in GA o Anteprima pubblica) rientrano nell'ambito dell'autorizzazione FedRAMP High di Google. L'autorizzazione IL5 per i connettori federati che utilizzano Gemini Enterprise è in attesa.

### Supporto dei connettori Google

Il connettore federato di Google per Google Workspace (che include Gmail, Google Drive e Google Calendar) rientra nell'ambito dell'autorizzazione FedRAMP High di Google. L'autorizzazione IL5 è in attesa.

Inoltre, puoi utilizzare i seguenti connettori di importazione di Google per rispettare i requisiti FedRAMP High e IL5:

*   [BigQuery](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/connect-bigquery?hl=it)
*   [Cloud Storage](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/connect-cloud-storage?hl=it)

Per garantire la conformità, crea una connessione che utilizzi endpoint regionali e che si trovi all'interno della stessa organizzazione e dello stesso confine dei Controlli di servizio VPC.

Per saperne di più sui connettori, consulta [Introduzione ai connettori e ai datastore](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/introduction-to-connectors-and-data-stores?hl=it).

Funzionalità non autorizzate che devi disattivare manualmente
-------------------------------------------------------------

I seguenti servizi e funzionalità non sono autorizzati per FedRAMP High o IL4. Tuttavia, non sono bloccati dai pacchetti di controllo di Assured Workloads e sono disponibili nel tuo progetto. Nell'ambito dell'analisi del rischio, potresti dover valutare l'utilizzo del servizio in relazione ai dati sensibili e ai controlli di mitigazione a tua disposizione. In base alla tua valutazione, potresti dover disattivare manualmente le funzionalità di questo elenco nella configurazione dell'app Gemini Enterprise.

<table><tbody><tr><th>Agenti e gallerie</th><td><ul><li><a href="https://docs.cloud.google.com/vertex-ai/generative-ai/docs/prompt-gallery?hl=it">Galleria dei prompt</a></li></ul></td></tr><tr><th>Grounding</th><td><ul><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/grounding/grounding-with-google-maps?hl=it">Grounding con Google Maps Platform</a></li><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/grounding/grounding-with-google-search?hl=it">Grounding con la Ricerca Google</a></li><li>Grounding utilizzando i caricamenti su Google Drive (per i casi d'uso IL4)</li><li>Grounding utilizzando i caricamenti di Microsoft OneDrive (per i casi d'uso IL4)</li></ul></td></tr><tr><th>Funzionalità generative</th><td><ul><li><a href="https://ai.google.dev/gemini-api/docs/imagen?hl=it">Genera immagini utilizzando Imagen</a></li><li><a href="https://ai.google.dev/gemini-api/docs/video?hl=it">Genera video con Veo</a></li></ul></td></tr><tr><th>Funzionalità relative a utenti, sessioni e UI</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/user-events?hl=it">Eventi utente per le app Gemini Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/configure-personalization?hl=it">Personalizzazione e memoria in Gemini Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/notebooklm-enterprise/docs/overview?hl=it">Gemini Notebook Enterprise (per casi d'uso IL4 e IL5)</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/notebooklm-enterprise/docs/overview?hl=it#notebooklm-availability">Gemini Notebook Enterprise autonomo</a></li></ul></td></tr><tr><th>Altre caratteristiche</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/use-knowledge-graph-search?hl=it">Knowledge Graph privato in Gemini Enterprise</a></li><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/enable-model-armor?hl=it">Model Armor (per i casi d'uso IL4 e IL5)</a></li><li><a href="https://docs.cloud.google.com/gemini-enterprise-agent-platform/resources/zero-data-retention?hl=it#durable-data-caching">Memorizzazione nella cache durevole</a></li></ul></td></tr></tbody></table>

Per saperne di più sulla memorizzazione nella cache durevole, consulta [Gemini Enterprise Agent Platform e conservazione dei dati pari a zero](https://docs.cloud.google.com/gemini-enterprise-agent-platform/resources/zero-data-retention?hl=it).

Funzionalità non autorizzate che non puoi disattivare
-----------------------------------------------------

I seguenti servizi e funzionalità sono disponibili nel pacchetto di controlli Assured Workloads. Non puoi disattivarli. Se utilizzi queste funzionalità, ti consigliamo di eseguire un'analisi del rischio adeguata prima di concedere l'autorizzazione per assicurarti che l'utilizzo del servizio sia appropriato per il tuo deployment FedRAMP High o IL4. Ad esempio, puoi valutare l'utilizzo dei servizi in relazione alla sensibilità dei dati. Puoi anche valutare se sono disponibili controlli di mitigazione basati sulla crittografia dei dati per stabilire il controllo esclusivo sull'accesso ai dati.

<table><tbody><tr><th>Analytics e funzionalità dipendenti</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/view-analytics?hl=it">Visualizza i dati di analisi</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/conversational-analytics-api/data-agent-authored-context-looker?hl=it">Contesto dell'agente per i dati per le origini dati di Looker</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/conversational-analytics-api/data-agent-authored-context-bq?hl=it">Contesto dell'agente per i dati per le origini dati BigQuery</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/conversational-analytics-api/overview?hl=it#build-and-interact-with-a-data-agent">Agenti per i dati che crei</a> utilizzando l'API Conversational Analytics</li><li><a href="https://docs.cloud.google.com/bigquery/docs/gemini-overview?hl=it">Gemini in BigQuery</a></li><li><a href="https://docs.cloud.google.com/gemini/docs/looker/overview?hl=it">Gemini in Looker</a></li></ul></td></tr><tr><th>Connettori dati e datastore</th><td><ul><li>DatastoreGoogle Cloud come Cloud SQL e Firestore</li></ul></td></tr><tr><th>Funzionalità relative a utenti, sessioni e UI</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/enterprise/docs/configure-prompt-chips?hl=it">Chip di prompt</a></li></ul></td></tr><tr><th>Altre caratteristiche</th><td><ul><li><a href="https://docs.cloud.google.com/gemini/docs/codeassist/overview?hl=it">Gemini Code Assist</a></li></ul></td></tr></tbody></table>

Esegui il deployment dell'ambiente
----------------------------------

Segui questi passaggi per ricevere assistenza per il deployment di un ambiente che soddisfi i tuoi obblighi di conformità:

1.  Esegui il deployment di Assured Workloads:
    1.  [Crea una cartella Assured Workloads](https://docs.cloud.google.com/assured-workloads/docs/create-folder?hl=it) che utilizzi Data Boundary per FedRAMP High o Data Boundary per IL4.
    2.  [Crea il tuo progetto Google Cloud](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=it) all'interno di questa cartella.
    3.  [Verifica che tutti gli utenti e i service account](https://docs.cloud.google.com/resource-manager/docs/access-control-proj?hl=it) dispongano delle autorizzazioni di Identity and Access Management (IAM) richieste.
2.  Configura la tua rete FedRAMP High o IL4. Per saperne di più, consulta [Configura la rete per FedRAMP e DoD suGoogle Cloud](https://docs.cloud.google.com/architecture/configure-networks-fedramp-dod-google-cloud?hl=it).
    1.  Crea un'[app Gemini Enterprise](https://docs.cloud.google.com/gemini/enterprise/docs/create-app?hl=it). Seleziona **Multi-regione USA** come località. La policy di residenza dei dati di Assured Workloads applica questa opzione.
3.  Connettiti a un'[origine dati di Google](https://docs.cloud.google.com/gemini/enterprise/docs/create-data-store?hl=it) che si trova nella cartella Assured Workloads. I datastore autorizzati per FedRAMP High e IL4 sono i bucket Cloud Storage e i set di dati BigQuery.
    
4.  Configura le funzionalità di conformità autorizzate.
    
5.  [Disattiva le funzionalità non autorizzate](#turn-off-unauthorized-features) descritte in [Funzionalità non autorizzate che devi disattivare manualmente](#unauthorized-features).
    
6.  Forma il tuo personale affinché non utilizzi [funzionalità non autorizzate che non puoi disattivare](#unauthorized-features-cant).
    

Disattiva le funzionalità non autorizzate
-----------------------------------------

Completa le attività descritte in questa sezione per disattivare le [funzionalità non autorizzate](#unauthorized-features) che puoi disattivare manualmente.

### Disattiva la galleria dei prompt

Completa questa attività per ogni app di cui esegui il deployment in Gemini Enterprise.

Assicurati di disporre di uno dei seguenti ruoli:

*   **Discovery Engine Admin** (`roles/discoveryengine.admin`)
*   **Gemini Enterprise Admin** (`roles/discoveryengine.agentspaceAdmin`)
*   Un ruolo personalizzato con l'autorizzazione `discoveryengine.engines.update`
    

### Console

1.  Nella console Google Cloud , vai alla pagina **App Gemini Enterprise**.

[Vai ad App](https://console.cloud.google.com/gemini-enterprise/apps?hl=it)

1.  Seleziona l'app.
2.  Nel menu di navigazione, fai clic su **Configurazioni** e poi seleziona la scheda **Gestione delle funzionalità**.
3.  Disattiva **Abilita la galleria dei prompt**.
4.  Fai clic su **Salva**.

### REST

Per aggiornare la mappa di controllo delle funzionalità della tua app, utilizza il metodo `engines.patch` sull'endpoint API `discoveryengine.googleapis.com`.

Imposta la chiave `prompt-gallery` su `FEATURE_STATE_OFF` nella [configurazione di `features`](https://docs.cloud.google.com/generative-ai-app-builder/docs/reference/rest/v1/projects.locations.collections.engines?hl=it) del tuo motore.

Prima di utilizzare i dati della richiesta, apporta le sostituzioni seguenti:

*   `PROJECT_ID`: l'ID del progetto
*   `APP_ID`: l'ID della tua app

Metodo HTTP e URL:

PATCH https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features

Corpo JSON della richiesta:

{
  "features": {
    "prompt-gallery": "FEATURE\_STATE\_OFF"
  }
}

Per inviare la richiesta, espandi una di queste opzioni:

#### curl (Linux, macOS o Cloud Shell)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

curl -X PATCH \\  
     -H "Authorization: Bearer $(gcloud auth print-access-token)" \\  
     -H "x-goog-user-project: PROJECT\_ID" \\  
     -H "Content-Type: application/json; charset=utf-8" \\  
     -d @request.json \\  
     "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features"

#### PowerShell (Windows)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

$cred = gcloud auth print-access-token  
$headers = @{ "Authorization" = "Bearer $cred"; "x-goog-user-project" = "PROJECT\_ID" }

Invoke-WebRequest \`  
    -Method PATCH \`  
    -Headers $headers \`  
    -ContentType: "application/json; charset=utf-8" \`  
    -InFile request.json \`  
    -Uri "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features" | Select-Object -Expand Content

Dovresti ricevere una risposta JSON simile alla seguente:

{
  "name": "projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID",
  "features": {
    "prompt-gallery": "FEATURE\_STATE\_OFF"
  }
}

### Disattiva il grounding con Google Maps Platform

Per disattivare il grounding con Google Maps Platform per tutte le richieste, puoi disabilitare l'**API Maps Grounding** (`mapsgrounding.googleapis.com`) utilizzando uno dei seguenti metodi:

*   Nella pagina **API e servizi** della console, individua l'**API Map Grounding** (`mapsgrounding.googleapis.com`) e seleziona **Disabilita API**. Per saperne di più, consulta [Abilita e disabilita i servizi](https://docs.cloud.google.com/service-usage/docs/enable-disable?hl=it#before).
    
*   In Google Cloud CLI, esegui:
    
    gcloud services disable mapsgrounding.googleapis.com
    

### Disattiva il grounding con la Ricerca Google

Per disattivare il grounding con la Ricerca Google per tutte le richieste, imposta il vincolo booleano della policy dell'organizzazione `constraints/vertexai.disableGenAIGoogleSearchGrounding` su `true` a livello della cartella Assured Workloads. Per saperne di più, consulta [Aggiornamento delle policy con regole booleane](https://docs.cloud.google.com/organization-policy/apply-policies?hl=it#boolean_constraints).

### Disattiva il grounding utilizzando i caricamenti su Google Drive (solo IL4)

Questa sezione si applica solo ai clienti DoD IL4.

Il grounding con Google Drive è attivo solo quando colleghi un datastore Google Drive alla tua app. Per disattivare il grounding con i caricamenti su Google Drive, completa i seguenti passaggi:

*   Assicurati che non siano configurati datastore di Google Drive nelle tue app Gemini Enterprise. Se un'app è già collegata a un datastore di Google Drive, rimuovila dalle impostazioni dell'app nella console o elimina il datastore utilizzando il [metodo `dataStores.delete`](https://docs.cloud.google.com/generative-ai-app-builder/docs/reference/rest/v1beta/projects.locations.collections.dataStores/delete?hl=it) nell'API Discovery Engine.
    
*   Disattiva le funzionalità di condivisione dei dati di Workspace nella Console di amministrazione Google o disattiva le [**funzionalità intelligenti e la personalizzazione**](https://knowledge.workspace.google.com/admin/security/manage-google-workspace-smart-features-for-your-users?hl=it) in Google Workspace per impedire al connettore di accedere ai documenti degli utenti su Google Drive.
    

### Disattiva il grounding utilizzando i caricamenti di Microsoft OneDrive (solo IL4)

Questa sezione si applica solo ai clienti DoD IL4.

Il grounding con Microsoft OneDrive è abilitato solo quando colleghi un datastore Microsoft OneDrive alla tua app. Per disabilitare il grounding con i caricamenti di Microsoft OneDrive, completa i seguenti passaggi:

*   Assicurati che non siano configurati datastore di Microsoft OneDrive nelle tue app Gemini Enterprise. Se un'app è già collegata a un datastore Microsoft OneDrive, rimuovila dalle [impostazioni dell'app nella console](https://docs.cloud.google.com/gemini/enterprise/docs/connectors/connect-existing-data-store?hl=it) o elimina il datastore utilizzando il [metodo `dataStores.delete`](https://docs.cloud.google.com/generative-ai-app-builder/docs/reference/rest/v1/projects.locations.collections.dataStores/delete?hl=it) nell'API Discovery Engine.
*   Per bloccare l'accesso del connettore, revoca le credenziali o elimina l'ID client e le credenziali dell'applicazione OAuth 2.0 registrata in Microsoft Entra ID (Azure AD) utilizzato per la connettività di Microsoft OneDrive.

### Disattiva Imagen e Veo

Per disattivare la generazione di immagini con Imagen e la generazione di video con Veo, completa i seguenti passaggi:

*   Imposta il vincolo dell'elenco delle policy dell'organizzazione `constraints/vertexai.allowedModels` o `constraints/vertexai.allowedGenAIModels` a livello di cartella o organizzazione per rifiutare i modelli `imagen` e `veo` (ad esempio `publishers/google/models/imagen-4.0-ultra-generate-001`) o per consentire solo i modelli approvati. Per saperne di più, consulta [Controlla l'accesso ai modelli di Model Garden](https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/control-model-access?hl=it).
*   Per limitare l'accesso alle previsioni sui modelli Imagen o Veo, configura una [policy di negazione IAM](https://docs.cloud.google.com/iam/docs/deny-overview?hl=it) che nega l'autorizzazione IAM `aiplatform.endpoints.predict` sul percorso della risorsa per il modello. Il percorso della risorsa del modello è `projects/PROJECT_ID/locations/LOCATION/publishers/google/models/MODEL_NAME`.
    
    Sostituisci quanto segue:
    
    *   PROJECT\_ID: l'ID del progetto
    *   LOCATION: la località dell'app
    *   MODEL\_NAME: il nome del modello da consentire o negare

### Disattiva gli eventi utente per le app Gemini Enterprise

Gli eventi utente vengono inviati dai tracker di eventi lato client o importati collettivamente. Per disattivare la raccolta di eventi utente, completa una delle seguenti operazioni:

*   Quando configuri il widget Ricerca per la tua applicazione, disattiva il logging degli eventi impostando la proprietà `disableUserEventsCollection` su `true` all'interno dello spazio dei nomi di configurazione [`uiSettings`](https://docs.cloud.google.com/gemini/enterprise/docs/reference/rest/v1/projects.locations.collections.dataStores.widgetConfigs?hl=it#uisettings). Ad esempio:
    
    "uiSettings": {
        "disableUserEventsCollection": true
        }
    
*   Non chiamare gli endpoint di inserimento degli eventi (nello specifico il [metodo `userEvents.collect`](https://docs.cloud.google.com/gemini/enterprise/docs/reference/rest/v1/projects.locations.userEvents/collect?hl=it) e il [metodo `userEvents.write`](https://docs.cloud.google.com/gemini/enterprise/docs/reference/rest/v1/projects.locations.userEvents/write?hl=it)) e assicurati che gli script dei pixel di ricerca (come `v1beta_event.js`) non siano attivi nelle pagine client.
    
*   Aggiorna l'impostazione a livello di applicazione come segue:
    
    1.  Nella console Google Cloud , vai alla pagina **App Gemini Enterprise**.
    
    [Vai ad App](https://console.cloud.google.com/gemini-enterprise/apps?hl=it)
    
    1.  Seleziona l'app.
    2.  Nel menu di navigazione, fai clic su **Configurazioni**.
    3.  Disattiva **Abilita raccolta di eventi utente**.
    4.  Fai clic su **Salva**.

### Disattiva la personalizzazione e la memoria in Gemini Enterprise

Completa questa attività per ogni app di cui esegui il deployment in Gemini Enterprise.

Assicurati di disporre di uno dei seguenti ruoli:

*   **Discovery Engine Admin** (`roles/discoveryengine.admin`)
*   **Gemini Enterprise Admin** (`roles/discoveryengine.agentspaceAdmin`)
*   Un ruolo personalizzato con l'autorizzazione `discoveryengine.engines.update`
    

### Console

Per istruzioni, vedi [Disattiva la personalizzazione e la memoria](https://docs.cloud.google.com/gemini/enterprise/docs/configure-personalization?hl=it#disable-personalization-and-memory).

### REST

Per aggiornare la mappa di controllo delle funzionalità della tua app, utilizza il metodo `engines.patch` sull'endpoint API `discoveryengine.googleapis.com`.

Imposta le chiavi `personalization-memory` e `personalization-suggested-highlights` su `FEATURE_STATE_OFF` nella [configurazione `features`](https://docs.cloud.google.com/generative-ai-app-builder/docs/reference/rpc/google.cloud.discoveryengine.v1?hl=it#engine) del motore.

Prima di utilizzare i dati della richiesta, apporta le sostituzioni seguenti:

*   `PROJECT_ID`: l'ID del progetto
*   `APP_ID`: l'ID della tua app

Metodo HTTP e URL:

PATCH https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features

Corpo JSON della richiesta:

{
  "features": {
    "personalization-memory": "FEATURE\_STATE\_OFF",
    "personalization-suggested-highlights": "FEATURE\_STATE\_OFF"
  }
}

Per inviare la richiesta, espandi una di queste opzioni:

#### curl (Linux, macOS o Cloud Shell)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

curl -X PATCH \\  
     -H "Authorization: Bearer $(gcloud auth print-access-token)" \\  
     -H "x-goog-user-project: PROJECT\_ID" \\  
     -H "Content-Type: application/json; charset=utf-8" \\  
     -d @request.json \\  
     "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features"

#### PowerShell (Windows)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

$cred = gcloud auth print-access-token  
$headers = @{ "Authorization" = "Bearer $cred"; "x-goog-user-project" = "PROJECT\_ID" }

Invoke-WebRequest \`  
    -Method PATCH \`  
    -Headers $headers \`  
    -ContentType: "application/json; charset=utf-8" \`  
    -InFile request.json \`  
    -Uri "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features" | Select-Object -Expand Content

Dovresti ricevere una risposta JSON simile alla seguente:

{
  "name": "projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID",
  "features": {
    "prompt-gallery": "FEATURE\_STATE\_OFF",
    "personalization-memory": "FEATURE\_STATE\_OFF",
    "personalization-suggested-highlights": "FEATURE\_STATE\_OFF"
  }
}

### Disattiva il Knowledge Graph privato

Per disattivare il Knowledge Graph privato, puoi utilizzare la console o l'API.

Assicurati di disporre di uno dei seguenti ruoli:

*   **Discovery Engine Admin** (`roles/discoveryengine.admin`)
*   **Gemini Enterprise Admin** (`roles/discoveryengine.agentspaceAdmin`)
*   Un ruolo personalizzato con l'autorizzazione `discoveryengine.engines.update`
    

### Console

Per le istruzioni, consulta [Gestisci la configurazione di Knowledge Graph](https://docs.cloud.google.com/gemini/enterprise/docs/use-knowledge-graph-search?hl=it#manage-kg-config).

### REST

Per aggiornare la configurazione dell'app, utilizza il metodo `engines.patch` sull'endpoint API `discoveryengine.googleapis.com`.

Imposta `enablePrivateKnowledgeGraph` e `enableCloudKnowledgeGraph` su `false` all'interno del [parametro `knowledgeGraphConfig`](https://docs.cloud.google.com/generative-ai-app-builder/docs/reference/rest/v1/projects.locations.collections.engines?hl=it#knowledgegraphconfig).

Prima di utilizzare i dati della richiesta, apporta le sostituzioni seguenti:

*   `PROJECT_ID`: l'ID del progetto
*   `APP_ID`: l'ID della tua app

Metodo HTTP e URL:

PATCH https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=knowledgeGraphConfig

Corpo JSON della richiesta:

{
  "knowledgeGraphConfig": {
    "enableCloudKnowledgeGraph": false,
    "enablePrivateKnowledgeGraph": false
  }
}

Per inviare la richiesta, espandi una di queste opzioni:

#### curl (Linux, macOS o Cloud Shell)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

curl -X PATCH \\  
     -H "Authorization: Bearer $(gcloud auth print-access-token)" \\  
     -H "x-goog-user-project: PROJECT\_ID" \\  
     -H "Content-Type: application/json; charset=utf-8" \\  
     -d @request.json \\  
     "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=knowledgeGraphConfig"

#### PowerShell (Windows)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

$cred = gcloud auth print-access-token  
$headers = @{ "Authorization" = "Bearer $cred"; "x-goog-user-project" = "PROJECT\_ID" }

Invoke-WebRequest \`  
    -Method PATCH \`  
    -Headers $headers \`  
    -ContentType: "application/json; charset=utf-8" \`  
    -InFile request.json \`  
    -Uri "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=knowledgeGraphConfig" | Select-Object -Expand Content

Dovresti ricevere una risposta JSON simile alla seguente:

{
  "name": "projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID",
  "knowledgeGraphConfig": {
    "enableCloudKnowledgeGraph": false,
    "enablePrivateKnowledgeGraph": false
  }
}

### Disattiva Model Armor (solo IL4)

Questa sezione si applica solo ai clienti DoD IL4.

Per disattivare Model Armor per gli ambienti DOD IL4, rimuovi i relativi modelli dalle tue app e poi disattiva l'API.

Per rimuovere i template, consulta [Rimuovi i template di Model Armor da un'app Gemini Enterprise](https://docs.cloud.google.com/gemini/enterprise/docs/enable-model-armor?hl=it#remove-model-armor-templates).

Per disabilitare l'**API Model Armor** (`modelarmor.googleapis.com`), utilizza uno dei seguenti metodi:

*   Nella pagina **API e servizi** della console, individua l'**API Model Armor** (`modelarmor.googleapis.com`) e seleziona **Disabilita API**. Per saperne di più, consulta [Abilita e disabilita i servizi](https://docs.cloud.google.com/service-usage/docs/enable-disable?hl=it#before).
    
*   In Google Cloud CLI, esegui:
    
    gcloud services disable modelarmor.googleapis.com
    

### Disattiva la memorizzazione nella cache durevole

Per disattivare la memorizzazione nella cache durevole, consulta [Disattiva la memorizzazione nella cache durevole](https://docs.cloud.google.com/gemini-enterprise-agent-platform/resources/zero-data-retention?hl=it#expandable-4).

### Disattiva Gemini Notebook Enterprise (solo IL4 e IL5)

Questa sezione si applica solo ai clienti DoD IL4 e IL5.

Completa questa attività per ogni app di cui esegui il deployment in Gemini Enterprise.

Assicurati di disporre di uno dei seguenti ruoli:

*   **Discovery Engine Admin** (`roles/discoveryengine.admin`)
*   **Gemini Enterprise Admin** (`roles/discoveryengine.agentspaceAdmin`)
*   Un ruolo personalizzato con l'autorizzazione `discoveryengine.engines.update`
    

### Console

1.  Nella console Google Cloud , vai alla pagina **App Gemini Enterprise**.

[Vai ad App](https://console.cloud.google.com/gemini-enterprise/apps?hl=it)

1.  Seleziona l'app.
2.  Nel menu di navigazione, fai clic su **Configurazioni** e poi seleziona la scheda **Gestione delle funzionalità**.
3.  Disattiva **Notebook**.
4.  Fai clic su **Salva**.

### REST

Per aggiornare la mappa di controllo delle funzionalità della tua app, utilizza il metodo `engines.patch` sull'endpoint API `discoveryengine.googleapis.com`. Imposta la chiave `notebook-lm` su `FEATURE_STATE_OFF` nella [configurazione di `features`](https://docs.cloud.google.com/generative-ai-app-builder/docs/reference/rest/v1/projects.locations.collections.engines?hl=it) del tuo motore.

Prima di utilizzare i dati della richiesta, apporta le sostituzioni seguenti:

*   `PROJECT_ID`: l'ID del progetto
*   `APP_ID`: l'ID della tua app

Metodo HTTP e URL:

PATCH https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features

Corpo JSON della richiesta:

{
  "features": {
    "notebook-lm": "FEATURE\_STATE\_OFF"
  }
}

Per inviare la richiesta, espandi una di queste opzioni:

#### curl (Linux, macOS o Cloud Shell)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

curl -X PATCH \\  
     -H "Authorization: Bearer $(gcloud auth print-access-token)" \\  
     -H "x-goog-user-project: PROJECT\_ID" \\  
     -H "Content-Type: application/json; charset=utf-8" \\  
     -d @request.json \\  
     "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features"

#### PowerShell (Windows)

Salva il corpo della richiesta in un file denominato `request.json`, quindi esegui il comando seguente:

$cred = gcloud auth print-access-token  
$headers = @{ "Authorization" = "Bearer $cred"; "x-goog-user-project" = "PROJECT\_ID" }

Invoke-WebRequest \`  
    -Method PATCH \`  
    -Headers $headers \`  
    -ContentType: "application/json; charset=utf-8" \`  
    -InFile request.json \`  
    -Uri "https://discoveryengine.googleapis.com/v1/projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID?updateMask=features" | Select-Object -Expand Content

Dovresti ricevere una risposta JSON simile alla seguente:

{
  "name": "projects/PROJECT\_ID/locations/global/collections/default\_collection/engines/APP\_ID",
  "features": {
    "prompt-gallery": "FEATURE\_STATE\_OFF",
    "notebook-lm": "FEATURE\_STATE\_OFF"
  }
}

### Rimuovi l'accesso a Gemini Notebook Enterprise autonomo

Per rimuovere l'accesso a Gemini Notebook Enterprise autonomo (`https://notebooklm.cloud.google.com/`), revoca i seguenti ruoli IAM a tutti gli utenti e i gruppi:

*   **Cloud NotebookLM User** (`roles/discoveryengine.notebookLmUser`)
*   **Cloud NotebookLM Admin** (`roles/discoveryengine.notebookLmOwner`)

Per istruzioni sulla revoca dei ruoli, consulta [Gestisci l'accesso a progetti, cartelle e organizzazioni](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=it).

Facoltativamente, se la tua organizzazione ha acquistato un abbonamento autonomo separato a Gemini Notebook Enterprise (anziché un abbonamento a Gemini Enterprise), disattiva il rinnovo automatico:

1.  Nella console Google Cloud , vai alla pagina **Gemini Enterprise > Gestisci abbonamenti**.
    
    [Vai a Gemini Enterprise](https://console.cloud.google.com/gemini-enterprise/start?hl=it)
    
2.  Fai clic sul nome del tuo abbonamento autonomo.
    
3.  Fai clic su **Modifica**.
    
4.  Deseleziona la casella di controllo **Rinnovo automatico abbonamento**.
    
5.  Fai clic su **Invia**.
