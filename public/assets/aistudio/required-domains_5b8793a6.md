# Zugriff auf Console-Domains von Google Cloud erlauben &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/required-domains

Wenn Sie oder Ihr Unternehmen eine lokale Netzwerkkonfiguration verwenden, die den Zugriff auf bestimmte URLs verweigert, z. B. eine Firewall oder einen Proxyserver, können beim Zugriff auf die Google Cloud Console oder bei deren Verwendung Fehler auftreten. In diesem Dokument sind die Domains und Domainmuster aufgeführt, die zugelassen werden müssen, damit die Google Cloud Konsole ordnungsgemäß funktioniert.

Erforderliche Domains
---------------------

Die folgenden Domains und Domainmuster sind für die Funktionalität derGoogle Cloud Console erforderlich. Wenn eine dieser Domains blockiert ist, funktioniert die Console nicht wie erwartet.

Achten Sie darauf, dass Ihre Netzwerkkonfiguration den Zugriff auf die folgenden Domains zulässt:

| Domain oder Domainmuster | Zweck |
| --- | --- |
| console.cloud.google.com | Die Google Cloud Console |
| www.gstatic.com | Statische Inhalte wie Skripts, Stylesheets und Bilder |
| ssl.gstatic.com | Bilder |
| fonts.gstatic.com | Schriftarten |
| \*.clients6.google.com | Google APIs |
| \*.googleapis.com | Google APIs |
| apis.google.com | Google API-Clientbibliotheken |
| accounts.google.com | Google-Kontoauthentifizierung und -anmeldung |
| reauth.cloud.google.com | Konformität mit der Multi-Faktor-Authentifizierung (MFA) |
| csp.withgoogle.com | Berichte zu Verstößen gegen die Content Security Policy (CSP) |

Domains überwachen
------------------

Die folgenden Domains und URLs werden für das Zustandsmonitoring der Google Cloud Console verwendet. Wenn eine dieser Optionen blockiert ist, funktioniert die Console möglicherweise weiterhin, aber Google wird nicht über Fehler oder Verhaltensprobleme informiert, die bei der Verwendung der Console auftreten.

| Domain oder URL |
| --- |
| cloud.google.com/log |
| www.google-analytics.com |
| www.googletagmanager.com |
