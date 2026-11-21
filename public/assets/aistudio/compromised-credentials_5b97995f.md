# Gestionar credenciales de Google Cloud vulneradas &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compromised-credentials?hl=es

LasGoogle Cloud credenciales controlan el acceso a tus recursos alojados en Google Cloud. Para proteger tus datos y evitar que los atacantes accedan a ellos, debes gestionar tus credenciales con sumo cuidado.

Te recomendamos que protejas todas tus Google Cloud credenciales para evitar que se acceda a ellas por error. Entre estas credenciales se incluyen las siguientes:

*   Credenciales de servicio:
    
    *   [Claves privadas de cuentas de servicio](https://docs.cloud.google.com/iam/docs/keys-create-delete?hl=es) (archivos JSON y p12)
    *   [Claves de API](https://docs.cloud.google.com/docs/authentication/api-keys?hl=es)
    *   [Secretos del ID de cliente de OAuth2](https://developers.google.com/identity/protocols/oauth2?hl=es)
*   Credenciales de usuario creadas y gestionadas en estaciones de trabajo de desarrolladores u otros ordenadores:
    
    *   [Credenciales de Google Cloud CLI](https://docs.cloud.google.com/sdk/docs/authorizing?hl=es)
        
    *   [Credenciales predeterminadas de la aplicación](https://docs.cloud.google.com/docs/authentication?hl=es#adc)
        
    *   Cookies del navegador
        

Las credenciales de la CLI de Google Cloud se almacenan en el directorio principal del usuario. Puedes consultarlas en la CLI de Google Cloud con el comando `gcloud auth list`. Las credenciales predeterminadas de la aplicación se almacenan en la estación de trabajo del desarrollador. Las cookies de navegador son específicas de cada navegador, pero suelen almacenarse en la estación de trabajo del desarrollador.

Si sospechas que se han vulnerado tus credenciales, debes tomar medidas de inmediato para limitar el impacto de la vulneración en tuGoogle Cloud cuenta.

Monitorizar si se han vulnerado credenciales
--------------------------------------------

Para monitorizar posibles vulneraciones, ten en cuenta lo siguiente:

*   Monitoriza la actividad sospechosa de la cuenta, como la elevación de privilegios y la creación de varias cuentas. Monitoriza estas actividades con [Registros de auditoría de Cloud](https://docs.cloud.google.com/logging/docs/audit?hl=es), [Policy Intelligence](https://docs.cloud.google.com/policy-intelligence/docs/overview?hl=es) y [Security Command Center](https://docs.cloud.google.com/security-command-center/docs/security-command-center-overview?hl=es). Utiliza los siguientes servicios y funciones de Security Command Center:
    
    *   [Detección de amenazas de eventos](https://docs.cloud.google.com/security-command-center/docs/concepts-event-threat-detection-overview?hl=es) para identificar amenazas basadas en actividades de administradores, cambios en grupos y cambios en permisos de Gestión de Identidades y Accesos (IAM). Para cada categoría de amenaza, se proporcionan [pasos de investigación recomendados](https://docs.cloud.google.com/security-command-center/docs/how-to-investigate-threats?hl=es) para ayudarte a responder.
    *   [Servicio Acciones Sensibles](https://docs.cloud.google.com/security-command-center/docs/concepts-sensitive-actions-overview?hl=es) para monitorizar las acciones de tu organización, carpetas y proyectos que podrían perjudicar a tu empresa si las lleva a cabo un agente malintencionado.
    *   [Gestión de derechos de infraestructura en la nube (CIEM)](https://docs.cloud.google.com/security-command-center/docs/ciem-overview?hl=es) (vista previa) para gestionar el acceso de las identidades y generar resultados sobre las configuraciones incorrectas.
*   Monitorizar los inicios de sesión de los usuarios en [Google Workspace](https://support.google.com/a/answer/6000239?hl=es) y [Cloud Identity](https://support.google.com/cloudidentity/answer/7420688?hl=es). Para hacer un mejor seguimiento de los problemas, puedes exportar los registros a [Cloud Logging](https://docs.cloud.google.com/logging/docs/audit/configure-gsuite-audit-logs?hl=es).
    
*   Monitoriza los secretos en tus repositorios de código con herramientas como [Detección de anomalías](https://docs.cloud.google.com/security-command-center/docs/concepts-security-sources?hl=es#anomaly_detection) o [análisis de secretos](https://docs.github.com/en/code-security/secret-scanning/about-secret-scanning).
    
*   Monitoriza las anomalías en el uso de las claves de cuentas de servicio con [Cloud Monitoring](https://docs.cloud.google.com/iam/docs/service-account-monitoring?hl=es) o CIEM.
    

Asegúrate de que tu centro de operaciones de seguridad (SOC) reciba una notificación de inmediato y tenga los manuales de procedimientos, las herramientas y el acceso necesarios para responder rápidamente a una posible vulneración de credenciales. Usa el [nivel Enterprise de Security Command Center](https://docs.cloud.google.com/security-command-center/docs/service-tiers?hl=es) para habilitar funciones de SIEM y SOAR, como guías, flujos de trabajo de respuesta y acciones automatizadas. También puedes integrar [Security Command Center con tu SIEM](https://docs.cloud.google.com/security-command-center/docs/how-to-configure-scc-splunk?hl=es) o [importar registros a Google Security Operations](https://docs.cloud.google.com/chronicle/docs/ingestion/cloud/ingest-gcp-logs?hl=es) para analizarlos en mayor profundidad.

Protege tus recursos de Google Cloud frente a credenciales vulneradas
---------------------------------------------------------------------

Sigue los pasos que se indican en las siguientes secciones lo antes posible para proteger tus recursos si sospechas que se ha vulnerado una credencial.

### Revocar y volver a emitir credenciales

Si sospechas que una credencial se ha visto comprometida, revócala y vuelve a emitirla. Ten cuidado para evitar que se interrumpa el servicio al revocar las credenciales.

Por lo general, para volver a emitir credenciales, debes generar una nueva, enviarla a todos los servicios y usuarios que la necesiten y, a continuación, revocar la antigua.

En las siguientes secciones se proporcionan instrucciones específicas para cada tipo de credencial.

#### Sustituir una clave de cuenta de servicio

1.  En la Google Cloud consola, ve a la página **Cuentas de servicio**.
    
    [Ir a Cuentas de servicio](https://console.cloud.google.com/iam-admin/serviceaccounts?hl=es)
    
2.  Busca la cuenta de servicio afectada.
    
3.  Crea una clave para la cuenta de servicio.
    
4.  Envía la nueva clave a todas las ubicaciones en las que se usaba la antigua.
    
5.  Elimina la llave antigua.
    

Para obtener más información, consulta el artículo [Crear cuentas de servicio](https://docs.cloud.google.com/iam/docs/service-accounts-create?hl=es).

#### Volver a generar claves de API

1.  En la Google Cloud consola, ve a la página **Credenciales**.
    
    [Ir a Credenciales](https://console.cloud.google.com/apis/credentials?hl=es)
    
2.  Crea una clave de API con el botón **Crear credenciales**. Configura la nueva clave para que sea igual que la clave de API vulnerada. Las restricciones de la clave de API deben coincidir. De lo contrario, es posible que se produzca una interrupción.
    
3.  Envía la clave de API a todas las ubicaciones en las que se usaba la clave antigua.
    
4.  Elimina la llave antigua.
    

Para obtener más información, consulta [Autenticarse mediante claves de API](https://docs.cloud.google.com/docs/authentication/api-keys?hl=es).

#### Restablecer el secreto de un ID de cliente de OAuth2

Si cambias el secreto de un ID de cliente, se producirá una interrupción temporal mientras se rota el secreto.

1.  En la Google Cloud consola, ve a la página **Credenciales**.
    
    [Ir a Credenciales](https://console.cloud.google.com/apis/credentials?hl=es)
    
2.  Selecciona el ID de cliente de OAuth2 vulnerado y edítalo.
    
3.  Haz clic en **Reset Secret** (Restablecer secreto).
    
4.  Envía el nuevo secreto a tu aplicación.
    

Para obtener más información, consulta [Configurar OAuth 2.0](https://support.google.com/cloud/answer/6158849?hl=es) y [Usar OAuth 2.0 para acceder a las APIs de Google](https://developers.google.com/identity/protocols/oauth2?hl=es).

#### Eliminar credenciales de Google Cloud CLI como administrador

Como administrador de Google Workspace, quita el acceso a la CLI de Google Cloud de la lista de aplicaciones conectadas del usuario. Para obtener más información, consulta [Ver y quitar el acceso a aplicaciones de terceros](https://support.google.com/a/answer/2537800?hl=es#zippy=,view-and-remove-access-to-third-party-applications).

Cuando el usuario vuelva a acceder a la CLI de Google Cloud, se le pedirá automáticamente que vuelva a autorizar la aplicación.

#### Eliminar las credenciales de Google Cloud CLI como usuario

1.  Abre la lista de [aplicaciones con acceso a tu cuenta de Google](https://myaccount.google.com/permissions?hl=es).
    
2.  Quita Google Cloud CLI de la lista de aplicaciones conectadas.
    

Cuando vuelvas a acceder a la CLI de Google Cloud, se te pedirá automáticamente que vuelvas a autorizar la aplicación.

#### Revocar las credenciales predeterminadas de la aplicación como administrador

Si sospechas que una credencial predeterminada de la aplicación se ha visto comprometida, puedes revocarla. Este procedimiento puede provocar una interrupción temporal hasta que se vuelva a crear el archivo de credenciales.

Como administrador de Google Workspace, retira el acceso a la biblioteca de autenticación de Google de la lista de aplicaciones conectadas del usuario. Para obtener más información, consulta el artículo [Ver y quitar el acceso a aplicaciones de terceros](https://support.google.com/a/answer/2537800?hl=es#zippy=,view-and-remove-access-to-third-party-applications).

#### Revocar las credenciales de aplicación predeterminadas como usuario

Si sospechas que una credencial predeterminada de aplicación que has creado se ha visto comprometida, puedes revocarla. Este procedimiento puede provocar una interrupción temporal hasta que se vuelva a crear el archivo de credenciales. Solo el propietario de la credencial vulnerada puede completar este procedimiento.

1.  [Instala e inicializa Google Cloud CLI](https://docs.cloud.google.com/sdk/docs/install?hl=es) si aún no lo has hecho.
    
2.  Autoriza gcloud CLI con tu identidad de usuario, no con una cuenta de servicio:
    
     gcloud auth login
    
    Para obtener más información, consulta [Autorizar la CLI de gcloud](https://docs.cloud.google.com/sdk/docs/authorizing?hl=es).
    
3.  Revoca las credenciales:
    
      gcloud auth application-default revoke
    
4.  También puedes eliminar el archivo `application_default_credentials.json`. La ubicación depende de tu sistema operativo:
    
    *   Linux y macOS: `$HOME/.config/gcloud/`
    *   Windows: `%APPDATA%\gcloud\`
5.  Vuelve a crear el archivo de credenciales:
    
     gcloud auth application-default login
    

#### Invalidar cookies del navegador como administrador

Si sospechas que las cookies del navegador se han visto comprometidas, los administradores de Google Workspace pueden [cerrar la sesión de un usuario](https://support.google.com/a/answer/178854?hl=es) en su cuenta.

Además, [fuerza un cambio de contraseña](https://support.google.com/a/answer/2537800?hl=es#zippy=,reset-a-users-password) inmediatamente.

Estas acciones invalidan todas las cookies existentes y se pide al usuario que vuelva a iniciar sesión.

#### Invalidar las cookies del navegador como usuario

Si sospechas que las cookies del navegador se han visto comprometidas, cierra la sesión de tu [cuenta de Google](https://accounts.google.com/?hl=es) y cambia la contraseña inmediatamente.

Estas acciones invalidan todas las cookies que ya tengas. La próxima vez que accedas a Google Cloud, tendrás que volver a iniciar sesión.

Después de revocar las credenciales vulneradas y restaurar el servicio, revisa todo el acceso a tus recursos de Google Cloud . Puedes usar Logging o Security Command Center.

En Logging, haz lo siguiente:

1.  Consulta los [registros de auditoría](https://docs.cloud.google.com/logging/docs/audit?hl=es) en la consolaGoogle Cloud .
    
    [Ir a Explorador de registros](https://console.cloud.google.com/logs/query?hl=es)
    
2.  Busca todos los recursos que puedan verse afectados y asegúrate de que toda la actividad de la cuenta (especialmente la relacionada con las credenciales vulneradas) sea la esperada.
    

En Security Command Center, haz lo siguiente:

1.  En la Google Cloud consola, ve a la página **Resultados** de Security Command Center.
    
    [Ir a Resultados](https://console.cloud.google.com/security/command-center/findings?hl=es)
    
2.  Si es necesario, selecciona tu Google Cloud proyecto u organización.
    
3.  En la sección **Filtros rápidos**, haga clic en el filtro adecuado para que se muestre el resultado que necesita en la tabla **Resultados de la consulta de detecciones**. Por ejemplo, si selecciona **Event Threat Detection** o **Container Threat Detection** en la subsección **Nombre visible de la fuente**, solo aparecerán en los resultados los hallazgos del servicio seleccionado.
    
    La tabla se rellena con los resultados de la fuente que has seleccionado.
    
4.  Para ver los detalles de un resultado específico, haga clic en su nombre en **`Category`**. El panel de detalles del resultado se amplía para mostrar un resumen de los detalles del resultado.
    
5.  Para mostrar todos los resultados que se han producido por las acciones del mismo usuario, sigue estos pasos:
    
    1.  En el panel de detalles, copia la dirección de correo que aparece junto a **Correo principal**.
    2.  Cierra el panel.
    3.  En el **editor de consultas**, introduce la siguiente consulta:
        
        access.principal_email="USER_EMAIL"
        
        Sustituye USER\_EMAIL por la dirección de correo que has copiado anteriormente.
        
        Security Command Center muestra todos los resultados asociados a las acciones realizadas por el usuario que has especificado.
        

### Eliminar todos los recursos no autorizados

Asegúrate de que no haya recursos inesperados, como máquinas virtuales, aplicaciones de App Engine, cuentas de servicio o cubos de Cloud Storage, a los que pueda acceder la credencial vulnerada.

Cuando te asegures de que has identificado todos los recursos no autorizados, puedes eliminarlos inmediatamente. Esto es especialmente importante en el caso de los recursos de Compute Engine, ya que los atacantes pueden usar cuentas vulneradas para extraer datos o poner en peligro tus sistemas de producción.

También puedes intentar aislar los recursos no autorizados para que tus equipos forenses puedan realizar análisis adicionales.

### Contactar con Cloud Customer Care

Si necesitas ayuda para encontrar los registros y las herramientas que necesitas para llevar a cabo los pasos de investigación y mitigación, ponte en contacto con el [servicio de atención al cliente](https://docs.cloud.google.com/support?hl=es) y abre un [caso de asistencia](https://docs.cloud.google.com/support/docs/customer-care-procedures?hl=es#create_a_support_case). Google Cloud

Prácticas recomendadas para evitar que se pongan en peligro las credenciales
----------------------------------------------------------------------------

En esta sección se describen las prácticas recomendadas que puedes implementar para evitar que se vean comprometidas tus credenciales.

### Separar las credenciales del código

Gestiona y almacena tus credenciales por separado del código fuente. Es muy habitual enviar por error tanto las credenciales como el código fuente a un sitio de gestión de fuentes como GitHub, lo que hace que tus credenciales sean vulnerables a los ataques.

Si usas GitHub u otro repositorio público, puedes implementar herramientas como [Detección de anomalías](https://docs.cloud.google.com/security-command-center/docs/concepts-security-sources?hl=es#anomaly_detection) o [Análisis de secretos](https://docs.github.com/en/code-security/secret-scanning/about-secret-scanning), que te avisan de los secretos expuestos en tus repositorios de GitHub. Para evitar que las claves se confirmen en tus repositorios de GitHub, puedes usar herramientas como [git-secrets](https://github.com/awslabs/git-secrets).

Usa soluciones de gestión de secretos, como [Secret Manager](https://docs.cloud.google.com/secret-manager/docs/overview?hl=es) y [HashiCorp Vault](https://www.vaultproject.io/), para almacenar tus secretos, rotarlos periódicamente y aplicar el principio de privilegio mínimo.

### Implementar las prácticas recomendadas de las cuentas de servicio

Para proteger las cuentas de servicio, consulta las [prácticas recomendadas para trabajar con cuentas de servicio](https://docs.cloud.google.com/iam/docs/best-practices-service-accounts?hl=es).

### Limitar la duración de las sesiones

Para forzar la reautenticación periódica, limita el tiempo que las sesiones permanecen activas en las cuentas de Google y Google Cloud . Para obtener más información, consulta lo siguiente:

*   [Configurar la duración de las sesiones de Google Workspace](https://support.google.com/a/answer/7576830?hl=es)
    
*   [Configurar la duración de las sesiones de los Google Cloud servicios](https://support.google.com/a/answer/9368756?hl=es)
    
*   [Configurar la duración de las sesiones de Cloud Identity](https://support.google.com/cloudidentity/answer/7576830?hl=es)
    

### Usar Controles de Servicio de VPC para limitar el acceso

Para limitar el impacto de las credenciales vulneradas, crea [perímetros de servicio con Controles de Servicio de VPC](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=es). Cuando configuras Controles de Servicio de VPC, los recursos que se encuentran dentro del perímetro solo pueden comunicarse con otros recursos que también estén dentro del perímetro.
