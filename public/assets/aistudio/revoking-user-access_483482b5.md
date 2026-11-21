# Revocar el acceso a un proyecto de Google Cloud &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/data-loss-prevention/revoking-user-access?hl=es

En este documento se describen las prácticas recomendadas, los escenarios y los procedimientos para revocar el acceso de un usuario a un proyecto de Google Cloud . Como cada empresa tiene políticas y cargas de trabajo diferentes, le recomendamos que utilice este documento para elaborar sus propias políticas y procedimientos, que le permitan revocar el acceso de forma coherente y oportuna.

Cuando un empleado deja tu empresa, finaliza tu relación con un contratista o un colaborador pasa a otros proyectos, hay algunas cosas que debes hacer para revocar el acceso innecesario a tus recursos de la nube.

Algunos de estos procesos son opcionales. Debes determinar cuáles de estos pasos debes seguir en función de tus necesidades de seguridad, los productos que utilices y la confianza que tengas en la persona a la que se le va a revocar el acceso.

Prácticas recomendadas para configurar un proyecto
--------------------------------------------------

Si tomas decisiones meditadas durante la configuración, puedes mejorar la capacidad de tu proyecto para revocar el acceso de los usuarios de forma eficiente.

### Federar cuentas de usuario con tu proveedor de identidades

Cuando federes cuentas de usuario con tu proveedor de identidades, asegúrate de propagar los eventos de suspensión y eliminación de usuarios. Con la propagación, cuando suspendes o eliminas una cuenta de usuario de tu proveedor de identidades, el usuario también pierde el acceso a los recursos de Google Cloud .

Para obtener más información, consulta las [prácticas recomendadas para federar Google Cloud con un proveedor de identidades externo](https://docs.cloud.google.com/architecture/identity/best-practices-for-federating?hl=es).

Para obtener más información sobre las prácticas recomendadas relacionadas con la identidad, consulta [Prácticas recomendadas para planificar cuentas y organizaciones](https://docs.cloud.google.com/architecture/identity/best-practices-for-planning?hl=es).

Para obtener información sobre la federación de identidades de Workforce, consulta [Federación de identidades de Workforce](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=es).

### Usar Grupos de Google para gestionar el acceso a los recursos del proyecto

Grupos de Google te permite organizar a los usuarios en función de su pertenencia a un equipo, sus requisitos de acceso u otros criterios. Después de crear grupos de Google, puedes asignar acceso a Google Cloud proyectos y recursos en función de la pertenencia a grupos. Cuando un usuario cambia de equipo o de puesto, puedes mover su cuenta a otro grupo, lo que elimina automáticamente el acceso que le concedían las políticas de permiso al grupo anterior.

No siempre es adecuado usar Grupos de Google. Por ejemplo, no deberías usar grupos basados únicamente en la estructura de tu empresa para gestionar el acceso. Para consultar las prácticas recomendadas sobre el uso de grupos, consulta el artículo [Prácticas recomendadas para utilizar Grupos de Google](https://docs.cloud.google.com/iam/docs/groups-best-practices?hl=es).

Para obtener más información, consulta los artículos [Gestionar grupos en la consola de Google Cloud](https://docs.cloud.google.com/iam/docs/groups-in-cloud-console?hl=es) y [Crear grupos en tu organización](https://support.google.com/cloudidentity/answer/9400082?hl=es).

### Usar OS Login

Usa OS Login en lugar de claves SSH basadas en metadatos para que las claves autorizadas del usuario estén vinculadas a su identidad de Google. Cuando eliminas una cuenta de usuario, las claves autorizadas y el acceso a las VMs se revocan automáticamente. Para obtener más información, consulta [Usar el inicio de sesión del SO para asegurar la evaluación continua del acceso en función de las políticas de gestión de identidades y accesos](https://docs.cloud.google.com/compute/docs/connect/ssh-best-practices/login-access?hl=es#use-oslogin).

Para obtener instrucciones, consulta [Configurar Inicio de sesión con SO](https://docs.cloud.google.com/compute/docs/oslogin/set-up-oslogin?hl=es).

### Restringir el acceso desde cuentas de usuario externas

No concedas acceso al proyecto a usuarios externos, ya que no puedes controlar el ciclo de vida de estas cuentas de usuario. Para restringir a los usuarios externos, usa la restricción de lista [`iam.allowedPolicyMemberDomains`](https://docs.cloud.google.com/resource-manager/docs/organization-policy/restricting-domains?hl=es).

Para obtener instrucciones, consulta el artículo [Restringir identidades por dominio](https://docs.cloud.google.com/resource-manager/docs/organization-policy/restricting-domains?hl=es).

### Usar proxies de autenticación con bases de datos

Los proxies de autenticación te permiten conectar el ciclo de vida de las credenciales de la base de datos con el ciclo de vida de una identidad de Google. Cuando suspendes o eliminas una cuenta de usuario en Cloud Identity o Google Workspace, se revoca automáticamente el acceso a las bases de datos.

Para obtener más información, consulta [Proxy de autenticación de Cloud SQL](https://docs.cloud.google.com/sql/docs/mysql/sql-proxy?hl=es) y [Proxy de autenticación de AlloyDB para PostgreSQL](https://docs.cloud.google.com/alloydb/docs/auth-proxy/overview?hl=es).

### Prepararse para la rotación de credenciales

Diseña tus proyectos y recursos para permitir la rotación sin interrupciones de las credenciales a nivel de proyecto. Se trata de secretos vinculados al propio proyecto, como claves de cuenta de servicio, secretos de cliente de OAuth y secretos específicos de la aplicación, como contraseñas raíz de bases de datos. Para obtener más información, consulta [Gestionar credencialesGoogle Cloud](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=es) vulneradas.

### Restringir claves de API

Cuando crees y gestiones claves de API, restringe el conjunto de sitios web, direcciones IP y aplicaciones que pueden usarlas. Una cuenta de usuario con roles como Lector o [Administrador de claves de API](https://docs.cloud.google.com/iam/docs/understanding-roles?hl=es#serviceusage.apiKeysAdmin) puede ver las claves de API de tu proyecto, por lo que las claves sin restricciones deben rotarse o eliminarse para revocar el acceso a la facturación. Para obtener más información, consulta [Proteger una clave de API](https://docs.cloud.google.com/docs/authentication/api-keys?hl=es).

### Monitorizar los permisos de acceso

Un seguimiento cuidadoso del acceso ayuda a mitigar el posible abuso del acceso. Puedes usar [Recomendador de roles de IAM](https://docs.cloud.google.com/policy-intelligence/docs/role-recommendations-overview?hl=es) para monitorizar el uso de los roles y aplicar el principio de mínimos accesos. Además, las funciones de [gestión de derechos de infraestructura en la nube (CIEM)](https://docs.cloud.google.com/security-command-center/docs/ciem-overview?hl=es) de Security Command Center te permiten gestionar qué identidades tienen acceso a qué recursos de tus implementaciones y mitigar las posibles vulnerabilidades derivadas de errores de configuración.

### Usar el acceso uniforme a nivel de segmento en Cloud Storage

El [acceso uniforme a nivel de segmento](https://docs.cloud.google.com/storage/docs/uniform-bucket-level-access?hl=es) te permite usar solo IAM para gestionar los permisos de tus segmentos de Cloud Storage. Usa el acceso uniforme a nivel de segmento junto con [otras opciones de control de acceso](https://docs.cloud.google.com/storage/docs/access-control?hl=es#additional_access_control_options) para definir quién puede acceder al contenido de tus segmentos.

### Prácticas recomendadas adicionales

Además de las prácticas recomendadas que se describen en este documento, consulta las siguientes:

*   [Prácticas recomendadas para trabajar con cuentas de servicio](https://docs.cloud.google.com/iam/docs/best-practices-service-accounts?hl=es)
*   [Decide la seguridad de tu Google Cloud zona de aterrizaje](https://docs.cloud.google.com/architecture/landing-zones/decide-security?hl=es)
*   [Verificar explícitamente cada intento de acceso](https://docs.cloud.google.com/architecture/framework/security/implement-zero-trust?hl=es#verify_every_access_attempt_explicitly)

Situaciones en las que se revoca el acceso a los proyectos Google Cloud
-----------------------------------------------------------------------

Si has implementado las prácticas recomendadas que se indican en [Prácticas recomendadas para configurar tu proyecto](#bps-setting-up), en la siguiente tabla se resume cómo puedes revocar el acceso.

| Situación | Opciones para revocar el acceso |
| --- | --- |
| Un empleado deja tu empresa. | Si configuras la federación entre Cloud Identity o Google Workspace con el aprovisionamiento automático de usuarios, la revocación del acceso puede producirse automáticamente.
Si no has seguido las prácticas recomendadas y has concedido acceso a tus recursos a identidades de usuario externas, debes quitar manualmente las identidades de tus proyectos y recursos.

 |
| Un empleado cambia de puesto. | Quitas al empleado del grupo del equipo. |
| Finaliza un contrato. | Si configuras la federación entre Cloud Identity o Google Workspace con el aprovisionamiento automático de usuarios, la revocación del acceso puede producirse automáticamente.

Si no has seguido las prácticas recomendadas y has concedido acceso a tus recursos a identidades de usuarios externos, debes quitar manualmente las identidades de tus proyectos y recursos.

 |
| Se ha vulnerado una cuenta. | Para obtener instrucciones, consulta el artículo sobre cómo [gestionar credencialesGoogle Cloud](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=es) vulneradas. |

Si has tomado buenas decisiones al configurar el proyecto, los siguientes procesos serán una forma eficiente de revocar el acceso de una persona.

Para determinar a qué recursos tiene acceso una persona, usa Analizador de políticas. Para obtener instrucciones, consulta [Analizar políticas de gestión de identidades y accesos](https://docs.cloud.google.com/policy-intelligence/docs/analyze-iam-policies?hl=es).

### Eliminar la cuenta de usuario de tu proveedor de identidades

Si el usuario abandona tu organización y has federado Cloud Identity o Google Workspace con tu proveedor de identidades, con el aprovisionamiento automático de usuarios, la revocación del acceso puede producirse automáticamente.

Para obtener información sobre cómo eliminar usuarios de la federación de identidades de Workforce, consulte el artículo [Eliminar usuarios de la federación de identidades de Workforce y sus datos](https://docs.cloud.google.com/iam/docs/workforce-delete-user-data?hl=es).

### Mover la cuenta a otro grupo

Si el usuario va a cambiar de rol, elimina su cuenta de usuario de los Grupos de Google en los que esté. Si has federado Cloud Identity o Google Workspace con tu proveedor de identidades para gestionar la pertenencia a grupos, la revocación del acceso puede producirse automáticamente.

Para obtener más información, consulta el artículo [Ver y editar los detalles de un grupo](https://docs.cloud.google.com/iam/docs/groups-in-cloud-console?hl=es#viewing-editing-details).

### Quitar la cuenta de usuario de las políticas de gestión de identidades y accesos de autorización

Para quitar una cuenta de usuario de las políticas de permiso a nivel de proyecto, siga estos pasos:

1.  En la consola de Google Cloud , ve a la página **Permisos de gestión de identidades y accesos**.
    
    [Permisos de gestión de identidades y accesos](https://console.cloud.google.com/iam-admin?hl=es)
    
2.  Selecciona el proyecto del que quieras quitar una cuenta de usuario.
    
3.  Marca la casilla situada junto a la fila que contiene la cuenta de usuario que quieras quitar de la lista de miembros y, a continuación, haz clic en **Quitar**.
    

Para verificar otras ubicaciones en las que se puede definir la política de permiso, como carpetas, organizaciones o recursos individuales, consulta [Verificar que se han quitado los permisos](#verify-perms).

### Rotar las credenciales de un proyecto

#### Rotar las claves de cuenta de servicio

Si usas claves de cuenta de servicio para autenticarte en una cuenta de servicio, debes rotar las claves. Además, piensa si la persona podría haber tenido acceso a las claves de la cuenta de servicio en algún lugar fuera de las herramientas de Google Cloud, como tu repositorio de código fuente o las configuraciones de la aplicación.

1.  En la Google Cloud consola, ve a la página **Credenciales de API**.
    
    [Credenciales de la API](https://console.cloud.google.com/apis/credentials?hl=es)
    
2.  Haga clic en el nombre de la cuenta de servicio que quiera modificar.
    
3.  En la pestaña **Clave**, haz clic en **Añadir clave**.
    
4.  Haz clic en **Crear clave**.
    
5.  Elige el **tipo de clave** que quieras crear. En la mayoría de los casos, se recomienda **JSON**, pero **P12** está disponible para ofrecer compatibilidad con versiones anteriores del código que depende de él.
    
6.  Haz clic en **Crear**. Se descargará automáticamente un archivo con la nueva clave a través de tu navegador. Implementa esta clave en las aplicaciones que la necesiten.
    
7.  Después de confirmar que la nueva clave funciona correctamente, vuelve a la página de credenciales y elimina la clave antigua asociada a esa cuenta de servicio.
    

#### Rotar los secretos de ID de cliente de OAuth

Los secretos de ID de cliente de OAuth no proporcionan acceso directo a tu proyecto. Sin embargo, si un atacante conoce el secreto del ID de cliente de OAuth, puede suplantar tu aplicación y solicitar acceso a las cuentas de Google de tus usuarios desde una aplicación maliciosa.

Es posible que tengas que cambiar los secretos del ID de cliente de OAuth si la persona cuyo acceso se va a revocar ha tenido acceso al secreto, ya sea en tu repositorio de código fuente, en las configuraciones de la aplicación o a través de roles de gestión de identidades y accesos.

1.  En la Google Cloud consola, ve a la página **Credenciales de API**.
    
    [Credenciales de la API](https://console.cloud.google.com/apis/credentials?hl=es)
    
2.  Haz clic en el nombre del ID de cliente de OAuth 2.0 que quieras modificar.
    
3.  En la página **ID de cliente**, haz clic en **Restablecer secreto**.
    
4.  Haz clic en **Restablecer** en el cuadro de diálogo de confirmación para revocar inmediatamente el antiguo secreto y definir uno nuevo. Ten en cuenta que los usuarios activos tendrán que volver a autenticarse en su próxima solicitud.
    
5.  Implementa el nuevo secreto en las aplicaciones que lo necesiten.
    

#### Rotar claves de API

Las claves de API no proporcionan acceso a tu proyecto ni a los datos de tus usuarios, pero controlan a quién factura Google las solicitudes de API. Una cuenta de usuario con roles como Lector o [Administrador de claves de API](https://docs.cloud.google.com/iam/docs/understanding-roles?hl=es#serviceusage.apiKeysAdmin) puede ver las claves de API de tu proyecto. Si tienes alguna clave sin restricciones, debes eliminarla o volver a generarla cuando revoques el acceso de alguien a tu proyecto.

1.  En la Google Cloud consola, ve a la página **Credenciales de API**.
    
    [Credenciales de la API](https://console.cloud.google.com/apis/credentials?hl=es)
    
2.  Haga clic en el nombre de la clave de API que quiera modificar.
    
3.  Haz clic en **Regenerate key** (Volver a generar clave).
    
4.  En un cuadro de diálogo se mostrará la clave recién creada. Implementa esta clave en cualquier aplicación que use la clave que quieras sustituir.
    
5.  Después de confirmar que tus aplicaciones funcionan correctamente con la nueva clave, vuelve a la página de credenciales y elimina la clave antigua sin restricciones.
    

### Revocar el acceso a las VMs

Si la persona a la que vas a revocar el acceso no tiene acceso para iniciar sesión en ninguna de las VMs de tu proyecto, puedes saltarte este paso.

1.  Quita todas las [claves SSH a nivel de proyecto](https://docs.cloud.google.com/compute/docs/connect/restrict-ssh-keys?hl=es#remove-project-key) a las que tenía acceso la persona.
    
2.  En cada VM en la que la persona tuviera acceso SSH, [elimina las claves a nivel de instancia](https://docs.cloud.google.com/compute/docs/connect/restrict-ssh-keys?hl=es#remove-instance-key).
    
3.  Quita la cuenta de la persona de las máquinas virtuales a las que tenía acceso.
    
4.  Comprueba si la persona ha instalado aplicaciones sospechosas para proporcionar acceso a la VM a través de una puerta trasera. Si no tienes claro si es seguro el código que se ejecuta en la VM, vuelve a crearla y vuelve a implementar las aplicaciones que necesites desde la fuente.
    
5.  Comprueba que los ajustes del cortafuegos de la máquina virtual no se hayan modificado con respecto a la configuración prevista.
    
6.  Si creas VMs a partir de imágenes base personalizadas, comprueba que las imágenes base no se hayan modificado de forma que pongan en peligro la seguridad de las nuevas VMs.
    

### Revocar el acceso a bases de datos

Si tu proyecto no usa recursos de Cloud SQL ni de AlloyDB para PostgreSQL, puedes saltarte este paso.

Para revocar el acceso a una base de datos de Cloud SQL, sigue estos pasos:

1.  En la Google Cloud consola, ve a la página **Instancias de SQL**.
    
    [Instancias de SQL](https://console.cloud.google.com/sql/instances?hl=es)
    
2.  Haz clic en el ID de instancia de la base de datos a la que quieras revocar el acceso.
    
3.  En el menú de la izquierda, haz clic en **Conexiones**.
    
4.  Confirma que la lista de direcciones IP de **Redes autorizadas** y la lista de aplicaciones de **Autorización de App Engine** coinciden con lo que esperas. Si la persona cuyo acceso intentas revocar tiene acceso a las redes o aplicaciones que se indican aquí, podrá acceder a esta base de datos.
    
5.  En el menú de la izquierda, haz clic en **Usuarios**.
    
6.  Elimina o cambia la contraseña de las cuentas de usuario a las que tenía acceso esa persona. Asegúrate de actualizar las aplicaciones que dependan de esas cuentas de usuario.
    

Para revocar el acceso a una base de datos de AlloyDB para PostgreSQL, consulta [Quitar un usuario o una cuenta de servicio de IAM de un clúster](https://docs.cloud.google.com/alloydb/docs/manage-iam-authn?hl=es#delete-user).

### Volver a desplegar App Engine

De forma predeterminada, las aplicaciones de App Engine tienen acceso a una cuenta de servicio que es editora del proyecto asociado. Los controladores de solicitudes de App Engine pueden crear máquinas virtuales y leer o modificar datos en Cloud Storage, entre otras acciones. Una persona con la capacidad de implementar código en App Engine podría usar esta cuenta de servicio para abrir una puerta trasera en tu proyecto. Si te preocupa la integridad del código de las aplicaciones que has desplegado, puedes volver a desplegarlas (incluidos los módulos) con una imagen correcta de tu sistema de control de versiones.

### Verificar que se han quitado los permisos

Puede verificar los permisos a nivel de organización, de proyecto o con el Analizador de políticas.

Para encontrar los recursos a los que puede acceder un usuario concreto a nivel de organización, utiliza el método [`search-all-iam-policies`](https://docs.cloud.google.com/sdk/gcloud/reference/asset/search-all-iam-policies?hl=es) en Google Cloud CLI. Por ejemplo, para determinar si un usuario tiene acceso a tus recursos, ejecuta lo siguiente:

gcloud asset search-all-iam-policies --scope='organizations/ORGANIZATION_ID --query='policy:IDENTITY'

Donde:

*   `ORGANIZATION_ID` es el número de tu organización.
*   `IDENTITY` es la identidad del usuario, como una dirección de correo electrónico.

Para verificar los permisos de un proyecto, consulta [Permisos que tiene una entidad de seguridad en un proyecto](https://docs.cloud.google.com/asset-inventory/docs/search-allow-policies?hl=es#permissions_a_principal_has_on_a_project).

Para verificar los permisos con Analizador de políticas, consulta [Determinar a qué recursos puede acceder una entidad de seguridad](https://docs.cloud.google.com/policy-intelligence/docs/analyze-iam-policies?hl=es#resource-query).

Siguientes pasos
----------------

*   Consulta [Gestionar credenciales Google Cloud comprometidas](https://docs.cloud.google.com/docs/security/compromised-credentials?hl=es).
    
*   [Prácticas recomendadas para mitigar los tokens de OAuth vulnerados en la CLI de Google Cloud](https://docs.cloud.google.com/architecture/bps-for-mitigating-gcloud-oauth-tokens?hl=es)
    
*   Crea una [política de denegación](https://docs.cloud.google.com/iam/docs/deny-overview?hl=es) para especificar a qué permisos no puede acceder el usuario.
