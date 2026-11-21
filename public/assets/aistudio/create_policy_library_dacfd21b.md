# Creazione di una libreria di criteri &nbsp;|&nbsp; Terraform on Google Cloud &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/policy_validation/create_policy_library#duplicate_the_sample_library

In qualità di amministratore dell'organizzazione, devi definire le policy che gli sviluppatori devono rispettare quando applicano l'infrastruttura come codice. L'insieme di policy della tua organizzazione è rappresentato come una raccolta di policy. Questa pagina ti aiuta a creare un repository di policy centralizzato e ad aggiungere vincoli.

Prima di iniziare
-----------------

*   Ti serve un repository Git vuoto per archiviare la raccolta di policy della tua organizzazione.
*   Devi configurare Git per connetterti in modo sicuro. Ad esempio, se il repository Git si trova su GitHub, puoi seguire la procedura descritta in [Connessione a GitHub con SSH](https://docs.github.com/en/authentication/connecting-to-github-with-ssh).

Duplicare la raccolta di esempio
--------------------------------

Google fornisce un repository di esempio che include un insieme di modelli di vincoli predefiniti che puoi modificare per uso personale.

1.  Clona il repository di esempio della raccolta di policy e duplicalo in POLICY\_LIBRARY\_REPO:
    
    git clone https://github.com/GoogleCloudPlatform/policy-library.git
    cd policy-library
    git remote set-url origin POLICY\_LIBRARY\_REPO
    git push origin main
    
2.  Esamina i modelli di vincoli disponibili in `policies/templates`: `ls policies/templates`
    
3.  Seleziona i modelli di vincoli che vuoi utilizzare. Per questo esempio, scegli `gcp_storage_location_v1.yaml`, che applica la località per i bucket Cloud Storage.
    
4.  Crea i file YAML dei vincoli corrispondenti a questi modelli in `policies/constraints`.
    
5.  Dalla copia locale del repository `policy-library`, utilizza i seguenti comandi per eseguire il commit e il push delle modifiche:
    
    git add --all .
        git commit -m "Initial commit of policy library constraints"
        git push -u origin main
    

### Struttura della raccolta

Un repository della raccolta di policy contiene le seguenti directory:

*   `policies/` : questa directory contiene due sottodirectory:
    
    *   `constraints/` : questa directory è inizialmente vuota. Inserisci qui i file dei vincoli.
    *   `templates/` : questa directory contiene modelli di vincoli predefiniti.
*   `validator/` : questa directory contiene i file `.rego` e i relativi unit test. Non è necessario toccare questa directory, a meno che tu non intenda modificare i modelli di vincoli esistenti o crearne di nuovi. L'esecuzione di `make build` inserisce i contenuti Rego nei file dei modelli di vincoli corrispondenti.
    

Aggiornamenti periodici
-----------------------

Periodicamente, devi eseguire il pull di eventuali modifiche dal repository pubblico, che potrebbe contenere nuovi modelli e file Rego.

git remote add public https://github.com/GoogleCloudPlatform/policy-library.git
    git pull public main
    git push origin main

Passaggi successivi
-------------------

La raccolta di policy contiene una serie di modelli di vincoli e una cartella `samples` con vincoli di esempio. Per informazioni dettagliate su come scrivere e utilizzare i modelli di vincoli e i vincoli, consulta [Creare vincoli Terraform](https://docs.cloud.google.com/docs/terraform/policy-validation/create-terraform-constraints?hl=it) o [Creare vincoli CAI](https://docs.cloud.google.com/docs/terraform/policy-validation/create-cai-constraints?hl=it).
