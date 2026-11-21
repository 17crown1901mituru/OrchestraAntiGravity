# Configurare le Credenziali predefinite dell'applicazione &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/provide-credentials-adc

La modalità di configurazione Credenziali predefinite dell'applicazione#39;applicazione (ADC) per l'utilizzo da parte delle librerie client Cloud, delle librerie client delle API di Google e delle API REST e RPC dipende dall'ambiente in cui viene eseguito il codice.

Per informazioni su dove e in quale ordine ADC cerca le credenziali, consulta [Come funzionano le credenziali predefinite dell'applicazione](https://docs.cloud.google.com/docs/authentication/application-default-credentials?hl=it).

Se utilizzi le chiavi API, non devi configurare le credenziali predefinite dell'applicazione. Per ulteriori informazioni, vedi [Utilizzare le chiavi API per accedere alle API](https://docs.cloud.google.com/docs/authentication/api-keys-use?hl=it).

Fornisci le credenziali ad ADC
------------------------------

Seleziona l'ambiente in cui viene eseguito il codice:

*   [Ambiente di sviluppo locale](https://docs.cloud.google.com/docs/authentication/set-up-adc-local-dev-environment?hl=it)
    
*   [Risorsa con un service account collegato](https://docs.cloud.google.com/docs/authentication/set-up-adc-attached-service-account?hl=it)
    
*   [Ambiente containerizzato](https://docs.cloud.google.com/docs/authentication/set-up-adc-containerized-environment?hl=it)
    
*   [On-premise o un altro cloud provider](https://docs.cloud.google.com/docs/authentication/set-up-adc-on-premises?hl=it)
    
*   [Ambiente di sviluppo basato sul cloud](https://docs.cloud.google.com/docs/authentication/set-up-adc-cloud-dev-environment?hl=it)
    

Puoi fornire le credenziali ad ADC utilizzando il comando [`gcloud auth application-default login`](https://docs.cloud.google.com/sdk/gcloud/reference/auth/application-default/login?hl=it). In questo modo, le credenziali sono disponibili per le librerie client Cloud e le librerie client delle API di Google.

gcloud CLI stessa non utilizza ADC per accedere alle risorse Google Cloud. Per scoprire come fornire le credenziali a gcloud CLI, consulta [Autenticazione per gcloud CLI](https://docs.cloud.google.com/sdk/docs/authenticate?hl=it).

Passaggi successivi
-------------------

*   Scopri di più su [come ADC trova le credenziali](https://docs.cloud.google.com/docs/authentication/application-default-credentials?hl=it).
*   [Autenticati per utilizzare le librerie client di Cloud](https://docs.cloud.google.com/docs/authentication/client-libraries?hl=it).
*   [Autenticati per usare REST](https://docs.cloud.google.com/docs/authentication/rest?hl=it).
*   Esplora i [metodi di autenticazione](https://docs.cloud.google.com/docs/authentication?hl=it).
