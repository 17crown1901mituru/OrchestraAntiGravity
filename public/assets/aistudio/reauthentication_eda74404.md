# Riautenticazione &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/reauthentication

Questa pagina descrive alcuni scenari in cui potrebbe essere necessario eseguire di nuovo l'autenticazione, anche se in precedenza è andata a buon fine.

Configurazione della sessione di Google Workspace
-------------------------------------------------

Se accedi utilizzando un account utente Google Workspace Google Cloud , l'amministratore di Google Workspace può configurare la durata massima della sessione e se è richiesta la riautenticazione alla scadenza della sessione. Anche le credenziali fornite dai file delle credenziali predefinite dell'applicazione (ADC) locali scadono alla scadenza della sessione. Devi aggiornarle eseguendo di nuovo il [`gcloud auth application-default login` comando](https://docs.cloud.google.com/sdk/gcloud/reference/auth/application-default/login?hl=it).

Se hai domande sulla configurazione della sessione di Google Workspace, contatta l'amministratore di Google Workspace. Per informazioni sull' impostazione della durata della sessione di Google Workspace, vedi [Impostare la durata della sessione per i Google Cloud servizi](https://support.google.com/a/answer/9368756?hl=it).

Riabilitazione di Identity-Aware Proxy
--------------------------------------

IAP può essere configurato in modo da richiedere la riautenticazione ai servizi e alle applicazioni protetti dopo un periodo di tempo specifico. Per saperne di più, vedi [Riabilitazione di IAP](https://docs.cloud.google.com/iap/docs/configuring-reauth?hl=it).

Scadenza del token di aggiornamento
-----------------------------------

I token di aggiornamento possono scadere a causa della durata della sessione o per altri motivi. Quando scadono, devi eseguire di nuovo l'autenticazione. Per saperne di più, vedi [Scadenza del token di aggiornamento](https://developers.google.com/identity/protocols/oauth2?hl=it#expiration) nella documentazione di Google Identity.

Azioni sensibili
----------------

Le seguenti Google Cloud azioni sono considerate azioni sensibili:

*   Modifiche all'assegnazione della fatturazione
    
*   Modifiche ai criteri di autorizzazione IAM a livello di organizzazione, cartella o progetto
    

Gli utenti che possono eseguire azioni sensibili sono noti come _utenti con privilegi_. Per proteggere gli utenti con privilegi dall'impersonificazione da parte di malintenzionati tramite il furto di cookie, Google Cloud è richiesta la riautenticazione prima che possano essere eseguite azioni sensibili.

La riautenticazione per le azioni sensibili è in fase di implementazione negli Google Cloud account. Il completamento dell'implementazione è previsto per il 2026.

### Quando è richiesta la riautenticazione

Quando avvii un'azione sensibile, devi inserire di nuovo la password o completare l'autenticazione a più fattori (MFA) se sono soddisfatte tutte le seguenti condizioni:

*   L'azione viene avviata nella Google Cloud console.
*   Non hai eseguito la riautenticazione negli ultimi 15 minuti.
*   Il tuo account utente è gestito da Google.

Gli account utente gestiti da un provider di identità (IdP) esterno e federati utilizzando la federazione delle identità per la forza lavoro non sono tenuti a eseguire la riautenticazione.

Disabilitare la riautenticazione
--------------------------------

La riautenticazione per le azioni sensibili è abilitata per impostazione predefinita. Per richiedere un' eccezione, [contatta l'assistenza](https://console.cloud.google.com/support?hl=it) e indica il motivo dell' eccezione.
