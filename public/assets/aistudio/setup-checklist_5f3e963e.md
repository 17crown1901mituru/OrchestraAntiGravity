# Flujo guiado de configuración de Google Cloud &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/enterprise/setup-checklist?hl=es

[Ir al contenido principal](#main-content)

*   [Áreas tecnológicas](https://docs.cloud.google.com/docs)
    *   [Introducción](https://docs.cloud.google.com/docs/get-started)
    *   [Guías](https://docs.cloud.google.com/docs/overview)
*   [Herramientas para varios productos](https://docs.cloud.google.com/docs/cross-product-overviews)
*   [Consola](https://console.cloud.google.com/)

*   Descubrir
    
*   [Descripción general de Google Cloud](https://docs.cloud.google.com/docs/overview)
*   [El enfoque de Google Cloud sobre los cambios](https://docs.cloud.google.com/docs/cloud-approach-to-change)

*   [Well-Architected Framework](https://docs.cloud.google.com/docs/get-started/well-architected-framework)
*   Empezar
    

*   [Autorización y control de acceso](https://docs.cloud.google.com/docs/get-started/authorization-access-control)
*   [Comparar los servicios de AWS y Azure con Google Cloud](https://docs.cloud.google.com/docs/get-started/aws-azure-gcp-service-comparison)
*   Planificar la creación de recursos
    
*   [Información general sobre la planificación de recursos](https://docs.cloud.google.com/docs/get-started/resource-planning-overview)
*   [Tener en cuenta la distribución geográfica](https://docs.cloud.google.com/docs/get-started/consider-geographic-distribution)
*   [Crear una jerarquía de propiedad](https://docs.cloud.google.com/docs/get-started/create-ownership-hierarchy)
*   [Considera el mantenimiento](https://docs.cloud.google.com/docs/get-started/consider-resource-maintenance)
*   Interactuar con Google Cloud
    
*   [Resumen de la interacción](https://docs.cloud.google.com/docs/get-started/interact-with-resources)
*   [Dominios obligatorios](https://docs.cloud.google.com/docs/get-started/required-domains)
*   [Accesibilidad](https://docs.cloud.google.com/docs/accessibility)
*   [Accesos directos de teclado](https://docs.cloud.google.com/docs/shortcuts)
*   [Obtener asistencia](https://docs.cloud.google.com/docs/get-started/getting-support)
*   Ver opciones de consumo
    
*   [Información sobre la facturación de Cloud](https://docs.cloud.google.com/billing/docs/concepts)
*   Descuentos por uso confirmado
    
    *   [Introducción](https://docs.cloud.google.com/docs/cuds)
    *   Programa CUD y migración mejorados
        
        *   [Mejoras en el programa de CUDs basados en la inversión](https://docs.cloud.google.com/docs/cuds-multiprice)
        *   [Cambios en el modelo de datos de los CUDs basados en la inversión](https://docs.cloud.google.com/docs/cuds-multiprice-datamodel)
        *   [Calendario de la migración al nuevo modelo de CUD](https://docs.cloud.google.com/docs/cuds-new-model-timeline)
        *   [Verificar los descuentos después de la migración](https://docs.cloud.google.com/docs/cuds-verify-discounts)
        *   [Elegir la cantidad correcta de CUD que se va a comprar](https://docs.cloud.google.com/docs/cuds-choose-correctly)
        *   [Consultas de ejemplo del nuevo modelo de datos de CUDs](https://docs.cloud.google.com/docs/cuds-example-queries)
        *   [SKUs, ofertas e IDs de modelo de consumo de CUDs migrados](https://docs.cloud.google.com/docs/cuds-migration)
        *   [Mejoras en la interfaz de usuario de facturación](https://docs.cloud.google.com/docs/cuds-billing-ui-improvements)
        
    *   [Atribución de confirmaciones](https://docs.cloud.google.com/docs/cuds-attribution)
    *   [Recomendaciones](https://docs.cloud.google.com/docs/cuds-recommender)
    *   [Descuentos por compromiso de uso basados en la inversión](https://docs.cloud.google.com/docs/cuds-spend-based)
    
*   [Programa gratuito de Google Cloud](https://docs.cloud.google.com/free/docs/free-cloud-features)

Flujo guiado de configuración de Google Cloud
---------------------------------------------

Antes de ejecutar cargas de trabajo en Google Cloud, recomendamos que los administradores configuren una base con Google Cloud Setup. Una base incluye ajustes fundamentales que te ayudan a organizar, gestionar y mantenerGoogle Cloud recursos.

Con la guía interactiva de Google Cloud Configuración, puedes desplegar rápidamente una configuración predeterminada o hacer ajustes para adaptarla a las necesidades de tu empresa:

[Ve a Google Cloud Configuración](https://console.cloud.google.com/cloud-setup/overview?hl=es).

En este documento se describen los pasos y la información general que te ayudarán a completar el proceso de configuración, que incluye las siguientes fases:

*   [Selecciona una opción de base:](#select-foundation-option) En función de la carga de trabajo que quieras admitir, selecciona una base de prueba de concepto, de producción o de seguridad mejorada.
    
*   [Crea tu organización, administradores y facturación:](#establish-organization-administrators-billing) configura el nodo de nivel superior de tu jerarquía, crea los usuarios administradores iniciales y asigna acceso, y conecta tu método de pago.
    
*   [Crea una arquitectura inicial:](#create-initial-architecture) selecciona una estructura inicial de carpetas y proyectos, aplica ajustes de seguridad, configura el registro y la monitorización, y configura tu red.
    
*   [Despliega tu configuración:](#deploy-your-settings) las opciones de arquitectura iniciales se compilan en archivos de configuración de Terraform. Puedes implementar rápidamente a través de la Google Cloud consola o descargar los archivos para personalizar y repetir el proceso con tu propio flujo de trabajo. Después de la implementación, selecciona un plan de asistencia.
    

Selecciona una opción de Google Cloud Configuración básica
----------------------------------------------------------

Para empezar a usar la configuración de Google Cloud , selecciona una de las siguientes opciones básicas en función de las necesidades de tu organización:

*   **Prueba de concepto:** admite cargas de trabajo de prueba de concepto teniendo en cuenta la seguridad básica. Esta opción te guía por las tareas de organización y facturación. Por ejemplo, puedes seleccionar esta opción para experimentar con Google Cloud antes de comprometerte a un plazo más largo.
    
*   **Producción:** admite cargas de trabajo listas para producción con la seguridad y la escalabilidad como prioridades. Esta opción incluye todas las Google Cloud tareas de configuración de este documento. Por ejemplo, puedes seleccionar esta opción para configurar una base segura y escalable para tu organización.
    
*   **Seguridad mejorada:** incluye todas las tareas de la base de producción, así como Cloud KMS con la configuración de Autokey en la tarea [Seguridad](#checklist-section-6). Por ejemplo, puedes seleccionar esta opción si tu organización está sujeta a requisitos de seguridad estrictos.
    

Para seleccionar una opción de base, haz lo siguiente:

1.  Ve a **Google Cloud Configuración: fundamentos**.
    
    [Ir a Aspectos básicos](https://console.cloud.google.com/cloud-setup/foundations?hl=es)
    
2.  Haz clic en **Iniciar** en una de las siguientes opciones:
    
    *   **Prueba de concepto**.
    *   **Producción**.
    *   **Seguridad mejorada.**
3.  Elige una de estas opciones:
    
    *   Si has seleccionado la opción **Prueba de concepto**, consulta [Crear una base para la prueba de concepto](#create-proof-of-concept-foundation).
    *   Si has seleccionado las opciones **Producción** o **Seguridad mejorada**, consulta [Establecer tu organización, administradores y facturación](#establish-organization-administrators-billing).

### Crea una base para la prueba de concepto

Una base de prueba de concepto te ayuda a hacer lo siguiente:

*   Tareas de organización y facturación.
*   Crea una implementación ligera que incluya lo siguiente:
    *   Una [carpeta configurada para la gestión de aplicaciones](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=es) en la que puedes definir y gestionar aplicaciones.
    *   Un [proyecto de gestión](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=es#management-project) que te ayuda a gestionar el acceso, la facturación, la observabilidad y otras funciones administrativas de tus aplicaciones.
    *   Un [proyecto](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=es#projects) estándar en el que puedes desplegar recursos.
    *   Grupos de administradores de la organización y de facturación.
    *   Políticas de organización recomendadas.

Para crear una base de prueba de concepto, haz lo siguiente:

1.  Completa la tarea [Organización](#checklist-section-1).
    
    Configura un proveedor de identidades, verifica tu dominio y genera tu organización.
    
2.  Inicia sesión en la consola como el usuario superadministrador que has creado en la tarea [Organización](#checklist-section-1).
    
3.  Selecciona la **opción de base [Prueba de concepto](#select-foundation-option)**[.](#select-foundation-option)
    
[](#select-foundation-option)5.  [
    
    Asegúrate de que esté seleccionada la organización que has creado y haz clic en **Continuar con la facturación**.
    
    Se crean los grupos `gcp-organization-admins` y `gcp-billing-admins`, y se te añade como miembro de cada uno de ellos.
    
    ](#select-foundation-option)
6.  Selecciona o crea una cuenta de facturación. Para obtener más información, consulta la tarea [Facturación](#checklist-section-4).
    
7.  Haz clic en **Continuar para revisar e implementar Foundation**.
    
8.  En la pantalla **Revisar y desplegar la configuración**, revisa las siguientes configuraciones de borrador:
    
    *   **Jerarquía de recursos:** revisa las carpetas y los proyectos.
        
    *   **Políticas de organización:** consulta la lista de políticas de organización recomendadas. Para obtener más información, consulta [Aplicar políticas de la organización recomendadas](#apply-recommended-organization-policies).
        
9.  Haz clic en **Desplegar**. Se ha implementado la base de tu prueba de concepto.
    
10.  Para habilitar la facturación en el proyecto de gestión, consulta el artículo [Vincular una cuenta de facturación a un proyecto de gestión](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=es#billing).
     

Para obtener información sobre cómo experimentar y crear, consulta [Crea tu Google Cloud arquitectura](https://docs.cloud.google.com/docs/enterprise/manage-foundation?hl=es#startbuilding).

Configurar tu organización, los administradores y la facturación
----------------------------------------------------------------

### Organización

Un recurso de organización en Google Cloud representa a tu empresa y es el nodo de nivel superior de tu jerarquía. Para crear tu organización, debes configurar un servicio de gestión de identidades de Google y asociarlo a tu dominio. Cuando completes este proceso, se creará automáticamente un recurso de organización.

Para obtener una descripción general del recurso de organización, consulta lo siguiente:

*   [Gestionar recursos de la organización](https://docs.cloud.google.com/resource-manager/docs/creating-managing-organization?hl=es).
*   [Prácticas recomendadas para planificar cuentas y organizaciones](https://docs.cloud.google.com/architecture/identity/best-practices-for-planning?hl=es)

### ¿Quién lleva a cabo esta tarea?

Los dos administradores siguientes realizan esta tarea:

*   Un administrador de identidades responsable de asignar acceso basado en roles. Asigna a esta persona como superadministrador de Cloud Identity. Para obtener más información sobre el usuario superadministrador, consulta el artículo [Roles de administrador predefinidos](https://support.google.com/cloudidentity/answer/2405986?hl=es).
    
*   Un administrador de dominio con acceso al host de dominios de la empresa. Esta persona edita la configuración de tu dominio, como las configuraciones de DNS, como parte del proceso de verificación del dominio.
    

### Qué debes hacer en esta tarea

*   Si aún no lo has hecho, configura Cloud Identity, donde crearás una [cuenta de usuario gestionada](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=es#managed_user_account) para tu usuario superadministrador.
*   Vincula Cloud Identity a tu dominio (por ejemplo, _example.com_).
*   Verifica tu dominio. De esta forma, se crea el nodo raíz de tu jerarquía de recursos, conocido como [recurso de organización](https://docs.cloud.google.com/resource-manager/docs/creating-managing-organization?hl=es).

### ¿Por qué recomendamos esta tarea?

Debes configurar lo siguiente como parte de tu Google Cloud base:

*   Un servicio de identidad de Google para gestionar las identidades de forma centralizada.
*   Un recurso de organización para establecer la raíz de tu jerarquía y el control de acceso.

### Opciones del servicio de identidad de Google

Utilizas uno o ambos de los siguientes servicios de gestión de identidades de Google para administrar las credenciales de los usuarios de Google Cloud :

*   Cloud Identity: gestiona usuarios y grupos de forma centralizada. Puedes federar identidades entre Google y otros proveedores de identidades. Para obtener más información, consulta el [resumen de Cloud Identity](https://docs.cloud.google.com/identity/docs/overview?hl=es).
*   Google Workspace: gestiona usuarios y grupos, y proporciona acceso a productos de productividad y colaboración, como Gmail y Google Drive. Para obtener más información, consulta [Google Workspace](http://workspace.google.com/?hl=es).

Para obtener información detallada sobre la planificación de la identidad, consulta el artículo [Planificar el proceso de incorporación de las identidades de tu empresa](https://docs.cloud.google.com/architecture/identity/overview-assess-and-plan?hl=es).

### Antes de empezar

Para saber cómo gestionar una cuenta de superadministrador, consulta las [prácticas recomendadas para cuentas de superadministrador](https://docs.cloud.google.com/resource-manager/docs/super-admin-best-practices?hl=es).

### Configurar un proveedor de identidades y verificar tu dominio

Los pasos que completes en esta tarea dependen de si eres un cliente nuevo o actual. Identifica la opción que se adapte a tus necesidades:

*   **Cliente nuevo:** configura Cloud Identity, verifica tu dominio y crea tu organización.
    
*   **Cliente de Google Workspace:** usa Google Workspace como proveedor de identidades para los usuarios que acceden a Google Workspace y Google Cloud. Si tienes previsto crear usuarios que solo accedan a Google Cloud, habilita Cloud Identity.
    
*   **Cliente de Cloud Identity:** verifica tu dominio, comprueba que se haya creado tu organización y confirma que Cloud Identity está habilitado.
    

### Cliente nuevo

### Cliente nuevo: configurar Cloud Identity y crear una organización

Para crear tu recurso de organización, primero debes configurar Cloud Identity, que te ayuda a gestionar los usuarios y grupos que acceden a los recursos de Google Cloud .

En esta tarea, configurarás la [edición gratuita](https://support.google.com/cloudidentity/answer/7295541?hl=es) de Cloud Identity.Puedes habilitar la edición premium de Cloud Identity después de completar la configuración inicial. Consulta más información en el artículo [Comparar las funciones de las ediciones de Cloud Identity](https://support.google.com/cloudidentity/answer/7431902?hl=es).

1.  Identifica a la persona que actúa como administrador de Cloud Identity (también conocido como superadministrador) en tu organización.
    
    Anota el nombre de usuario del administrador con el siguiente formato: admin-_nombre_@example.com. Por ejemplo, admin-maria@example.com. Especifica este nombre de usuario cuando crees tu primer usuario administrador.
    
2.  Para completar el proceso de configuración y crear la cuenta de superadministrador, ve a la [página de registro de Cloud Identity](https://workspace.google.com/signup/gcpidentity/welcome?hl=es).
    
    Si aparece un error al configurar la cuenta de administrador, consulta el artículo [Error "La cuenta de Google ya existe"](https://support.google.com/a/answer/1275816?hl=es).
    

#### Verificar tu dominio y crear tu recurso de organización

Cloud Identity requiere que verifiques que eres el propietario de tu dominio. Una vez que se haya completado la verificación, se creará automáticamente el Google Cloud [recurso de organización](https://docs.cloud.google.com/resource-manager/docs/creating-managing-organization?hl=es).

1.  Asegúrate de haber creado una cuenta de superadministrador al [configurar tu proveedor de identidades](#configure-identity).
    
2.  Verifica tu dominio en Cloud Identity. Cuando completes el proceso de verificación, ten en cuenta lo siguiente:
    
    *   Cuando se te pida, no hagas clic en Crear usuarios. Crearás usuarios en una tarea posterior.
    *   Si no puedes registrar tu dominio, consulta el artículo [No puedo registrarme en un servicio de Google con mi dominio](https://support.google.com/a/answer/80610?hl=es).
    *   El proceso de verificación puede tardar varias horas.
    
    Para ver los pasos que debes seguir para verificar tu dominio, consulta el artículo [Verificar un dominio](https://support.google.com/cloudidentity/answer/7331243?hl=es).
    
3.  Cuando hayas completado los pasos de verificación del dominio, haz clic en **Configurar Google Cloud consola ahora**.
    
4.  Inicia sesión en la consola Google Cloud como superadministrador con la dirección de correo que hayas especificado. Por ejemplo, admin-maria@example.com.
    
5.  Ve a **Google Cloud Configuración: organización**. Tu organización se crea automáticamente.
    
    [Ir a Organización](https://console.cloud.google.com/cloud-setup/organization?hl=es)
    
6.  En la lista desplegable **Seleccionar de**, situada en la parte superior de la página, selecciona tu organización.
    

#### Solicitar licencias de usuario de Cloud Identity adicionales

La edición gratuita de Cloud Identity incluye un número determinado de licencias de usuario. Para ver los pasos que debes seguir para consultar y solicitar licencias, consulta el artículo [Límite de usuarios en Cloud Identity Free Edition](https://support.google.com/cloudidentity/answer/7295541?hl=es).

### Cliente de Workspace

### Cliente de Google Workspace: verifica tu dominio y habilita Cloud Identity

Si ya eres cliente de Google Workspace, verifica tu dominio, comprueba que se haya creado automáticamente tu recurso de organización y habilita Cloud Identity (opcional).

1.  Para verificar tu dominio en Google Workspace, consulta el artículo [Verificar un dominio](https://support.google.com/a/answer/60216?hl=es). Mientras completas el proceso de verificación, ten en cuenta lo siguiente:
    
    *   Cuando se te pida, no hagas clic en Crear usuarios. Crearás usuarios en una tarea posterior.
    *   Si no puedes registrar tu dominio, consulta el artículo [No puedo registrarme en un servicio de Google con mi dominio](https://support.google.com/a/answer/80610?hl=es).
    *   El proceso de verificación puede tardar varias horas.
2.  Inicia sesión en la Google Cloud consola como superadministrador.
    
3.  Ve a **Google Cloud Configuración: organización**.
    
    [Ir a Organización](https://console.cloud.google.com/cloud-setup/organization?hl=es)
    
4.  Selecciona **Ya soy cliente de Google Workspace**.
    
5.  Comprueba que el nombre de tu organización se muestre en la lista **Organización**.
    
6.  Si quieres crear usuarios que tengan acceso a Google Cloud, pero que no reciban licencias de Google Workspace, sigue estos pasos.
    
    1.  En Google Workspace, [habilita Cloud Identity](https://support.google.com/cloudidentity/answer/7384506?hl=es).
        
    2.  Cuando configures Cloud Identity, [inhabilita la asignación automática de licencias de Google Workspace](https://support.google.com/cloudidentity/answer/7338389?hl=es).
        

### Cliente de Cloud Identity

### Cliente de Cloud Identity: verifica tu dominio

Si ya eres cliente de Cloud Identity, asegúrate de haber verificado tu dominio y de que se haya creado automáticamente tu recurso de organización.

1.  Para asegurarte de que has verificado tu dominio, consulta el artículo [Verificar un dominio](https://support.google.com/cloudidentity/answer/7331243?hl=es). Mientras completas el proceso de verificación, ten en cuenta lo siguiente:
    
    *   Cuando se te pida, no hagas clic en Crear usuarios. Crearás usuarios en una tarea posterior.
    *   Si no puedes registrar tu dominio, consulta el artículo [No puedo registrarme en un servicio de Google con mi dominio](https://support.google.com/a/answer/80610?hl=es).
    *   El proceso de verificación puede tardar varias horas.
2.  Inicia sesión en la Google Cloud consola como superadministrador.
    
3.  Ve a **Google Cloud Configuración: organización**.
    
    [Ir a Organización](https://console.cloud.google.com/cloud-setup/organization?hl=es)
    
4.  Selecciona **Ya soy cliente de Cloud Identity**.
    
5.  Comprueba que el nombre de tu organización se muestre en la lista **Organización**.
    
6.  Asegúrate de que Cloud Identity esté habilitado en [Consola de administración de Google > Suscripciones](https://admin.google.com/ac/billing/subscriptions?hl=es). Inicia sesión como superadministrador.
    

### Siguientes pasos

[Crea grupos y añade miembros](#checklist-section-2).

### Usuarios y grupos

En esta tarea, configurarás identidades, usuarios y grupos para gestionar el acceso a los recursos deGoogle Cloud .

Para obtener más información sobre la gestión de accesos en Google Cloud, consulta los siguientes artículos:

*   [Información general sobre la gestión de identidades y accesos (IAM)](https://docs.cloud.google.com/iam/docs/overview?hl=es)
*   Para consultar las prácticas recomendadas, consulta [Usar la gestión de identidades y accesos de forma segura](https://docs.cloud.google.com/iam/docs/using-iam-securely?hl=es).

### ¿Quién lleva a cabo esta tarea?

Puedes realizar esta tarea si tienes uno de los siguientes elementos:

*   El superadministrador de Google Workspace o Cloud Identity que has creado en la tarea [Organización](#checklist-section-1).
*   Uno de los siguientes roles de gestión de identidades y accesos:
    *   Administrador de la organización (`roles/resourcemanager.organizationAdmin`).
    *   Administrador de grupos de identidades de Workforce (`roles/iam.workforcePoolAdmin`).

### Qué debes hacer en esta tarea

*   Conéctate a Cloud Identity o a tu proveedor de identidades externo.
    
*   Crea grupos de administración y usuarios que completen el resto de losGoogle Cloud pasos de configuración. En una tarea posterior, concederás acceso a estos grupos.
    

### ¿Por qué recomendamos esta tarea?

Esta tarea te ayuda a implementar las siguientes prácticas recomendadas de seguridad:

*   [Principio de mínimos accesos:](https://en.wikipedia.org/wiki/Principle_of_least_privilege) concede a los usuarios los permisos mínimos necesarios para desempeñar su rol y retira el acceso en cuanto ya no lo necesiten.
    
*   [Control de acceso basado en roles (RBAC):](https://en.wikipedia.org/wiki/Role-based_access_control) asigna permisos a grupos de usuarios en función de su rol. No añadas permisos a cuentas de usuario individuales.
    

Puedes usar grupos para aplicar roles de gestión de identidades y accesos de forma eficiente a una colección de usuarios. Esta práctica te ayuda a simplificar la gestión de accesos.

### Seleccionar un proveedor de identidades

Puedes usar una de las siguientes opciones para gestionar usuarios y grupos, y conectarlos a Google Cloud:

*   _Google Workspace o Cloud Identity:_ crea y gestiona usuarios y grupos en Google Workspace o Cloud Identity. Puedes sincronizarte con tu proveedor de identidades externo más adelante.
*   _Tu proveedor de identidades externo, como Microsoft Entra ID u Okta:_ creas y gestionas usuarios y grupos en tu proveedor de identidades externo. A continuación, conecta tu proveedor a Google Cloud para habilitar el inicio de sesión único.

Para seleccionar tu proveedor de identidades, sigue estos pasos:

1.  Inicia sesión en la Google Cloud consola como uno de los usuarios que has identificado en la sección [Quién realiza esta tarea](#who-performs-users).
    
2.  Ve a **Google Cloud Configuración: usuarios y grupos**.
    
    [Ir a Usuarios y grupos](https://console.cloud.google.com/cloud-setup/users-groups?hl=es)
    
3.  Revisa los detalles de la tarea y haz clic en **Continuar con la configuración de la identidad**.
    
4.  En la página **Seleccione su proveedor de identidad**, elija una de las siguientes opciones para iniciar una configuración guiada:
    
    *   **Usar Google para gestionar usuarios de forma centralizada: Google Cloud** usa Google Workspace o Cloud Identity para aprovisionar y gestionar usuarios y grupos como superadministrador de tu dominio verificado. Puedes sincronizarlo más adelante con tu proveedor de identidades externo.
    *   **Microsoft Entra ID (Azure AD):** usa OpenID Connect para configurar una conexión con Microsoft Entra ID.
    *   **Okta:** usa OpenID Connect para configurar una conexión a Okta.
    *   **OpenID Connect:** usa el protocolo OpenID para conectarte a un proveedor de identidades compatible.
    *   **SAML** usa el protocolo SAML para conectarte a un proveedor de identidades compatible.
    *   **Saltar la configuración de un IdP externo por ahora:** si tienes un proveedor de identidades externo y aún no quieres conectarlo a Google Cloud, puedes crear usuarios y grupos en Google Workspace o Cloud Identity.
5.  Haz clic en **Continuar**.
    
6.  Consulta una de las siguientes secciones para ver los pasos que debes seguir:
    
    *   [Crear usuarios y grupos en Cloud Identity](#create-in-cloud-identity)
    *   [Conectar tu proveedor de identidades externo a Google Cloud](#connect-external-provider)

### Crear usuarios y grupos en Cloud Identity

Si no tienes ningún proveedor de identidades o no estás preparado para conectar tu proveedor de identidades a Google Cloud, puedes crear y gestionar usuarios y grupos en Cloud Identity o Google Workspace. Para crear usuarios y grupos, sigue estos pasos:

*   Crea un grupo para cada función administrativa recomendada, como la administración de la organización, la facturación y la red.
*   Crea cuentas de [usuario gestionado](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=es#managed_user_account) para los administradores.
*   Asigna usuarios a grupos administrativos que se correspondan con sus responsabilidades.

#### Antes de empezar

*   Busca y migra usuarios que ya tengan cuentas de Google. Para obtener información detallada, consulta el artículo [Añadir usuarios con cuentas no gestionadas](https://support.google.com/a/topic/7042002?hl=es).
    
*   Debes ser superadministrador.
    

#### Crear grupos administrativos

Un grupo es una colección de cuentas de Google y cuentas de servicio con un nombre. Cada grupo tiene una dirección de correo única, como _gcp-billing-admins@example.com_. Los grupos se crean para gestionar usuarios y aplicar roles de IAM a gran escala.

Te recomendamos los siguientes grupos para que puedas gestionar las funciones principales de tu organización y completar el proceso de configuración. Google Cloud

<table><tbody><tr><td><strong>Grupo</strong></td><td><strong>Descripción</strong></td></tr><tr><td><code dir="ltr" translate="no">gcp-organization-admins</code></td><td>Administrar todos los recursos de la organización. Asigna este rol solo a los usuarios en los que más confíes.</td></tr><tr><td><code dir="ltr" translate="no">gcp-billing-admins</code></td><td>Configurar cuentas de facturación y monitorizar el uso.</td></tr><tr><td><code dir="ltr" translate="no">gcp-network-admins</code></td><td>Crea redes, subredes y reglas de cortafuegos de la nube privada virtual.</td></tr><tr><td><code dir="ltr" translate="no">gcp-hybrid-connectivity-admins</code></td><td>Crea dispositivos de red, como instancias de Cloud VPN y Cloud Router.</td></tr><tr><td><code dir="ltr" translate="no">gcp-logging-monitoring-admins</code></td><td>Usar todas las funciones de Cloud Logging y Cloud Monitoring.</td></tr><tr><td><code dir="ltr" translate="no">gcp-logging-monitoring-viewers</code></td><td>Acceso de solo lectura a un subconjunto de registros y datos de monitorización.</td></tr><tr><td><code dir="ltr" translate="no">gcp-security-admins</code><br></td><td>Establecer y gestionar las políticas de seguridad de toda la organización, incluidas las políticas de gestión de acceso y las <a href="https://docs.cloud.google.com/resource-manager/docs/organization-policy/org-policy-constraints?hl=es">políticas de restricciones de organizaciones</a>. Consulta el <a href="https://docs.cloud.google.com/architecture/security-foundations?hl=es">Google Cloud plan de las bases de la empresa</a> para obtener más información sobre cómo planificar tu Google Cloud infraestructura de seguridad.</td></tr><tr><td><code dir="ltr" translate="no">gcp-developers</code></td><td>Diseñar, programar y probar aplicaciones.</td></tr><tr><td><code dir="ltr" translate="no">gcp-devops</code></td><td>Crea o gestiona flujos de procesamiento integrales que sean compatibles con la integración y la entrega continuas, la monitorización y el aprovisionamiento del sistema.</td></tr></tbody></table>

Para crear grupos administrativos, siga estos pasos:

1.  [Selecciona Google como proveedor.](#select-provider)
    
2.  En la página **Crear grupos**, revisa la lista de grupos administrativos recomendados y, a continuación, haz una de las siguientes acciones:
    
    *   Para crear todos los grupos recomendados, haz clic en **Crear todos los grupos**.
    *   Si quieres crear un subconjunto de los grupos recomendados, haz clic en **Crear** en las filas elegidas.
3.  Haz clic en **Continuar**.
    

#### Crear usuarios administradores

Te recomendamos que, al principio, añadas a los usuarios que completen los procedimientos de configuración de la organización, la red, la facturación y otros. Puedes añadir otros usuarios después de completar el Google Cloud proceso de configuración.

Para añadir usuarios administradores que realicen tareas de configuración, sigue estos pasos: Google Cloud

1.  Migrar [cuentas de consumidor](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=es#consumer_account) a [cuentas de usuario gestionadas](https://docs.cloud.google.com/architecture/identity/overview-google-authentication?hl=es#managed_user_account) controladas por Cloud Identity. Para obtener instrucciones detalladas, consulta lo siguiente:
    
    *   [Migrar cuentas de consumidor](https://docs.cloud.google.com/architecture/identity/migrating-consumer-accounts?hl=es).
    *   [Añadir usuarios con cuentas no gestionadas](https://support.google.com/a/topic/7042002?hl=es)
2.  Inicia sesión en la [consola de administración de Google](https://admin.google.com/?hl=es) con una cuenta de superadministrador.
    
3.  Usa una de las siguientes opciones para añadir usuarios:
    
    *   Para añadir usuarios en bloque, consulta el artículo [Añadir o actualizar varios usuarios utilizando un archivo CSV](https://support.google.com/cloudidentity/answer/40057?hl=es).
    *   Para añadir usuarios de forma individual, consulta el artículo [Añadir una cuenta para un usuario nuevo](https://support.google.com/cloudidentity/answer/33310?hl=es).
4.  Cuando hayas terminado de añadir usuarios, vuelve a **Google Cloud Configuración: usuarios y grupos (crear usuarios)**.
    
5.  Haz clic en **Continuar**.
    

#### Añadir usuarios administradores a grupos

Añade los usuarios que has creado a grupos administrativos que se correspondan con sus tareas.

1.  Asegúrate de haber [creado usuarios administradores](#create-admin-users).
2.  En **Google Cloud Configuración: Usuarios y grupos (añadir usuarios a grupos)**, consulta los detalles del paso.
    
3.  En cada fila de **Grupo**, haga lo siguiente:
    
    1.  Haz clic en **Añadir miembros**.
    2.  Introduce la dirección de correo electrónico del usuario.
    3.  En la lista desplegable **Rol del grupo**, selecciona los ajustes de permisos del grupo del usuario. Para obtener más información, consulta el artículo [Configurar quién puede ver, publicar y moderar contenido](https://support.google.com/groups/answer/2464975?hl=es).
        
        Cada miembro hereda todos los roles de gestión de identidades y accesos que asignes a un grupo, independientemente del rol de grupo que selecciones.
        
    4.  Para añadir otro usuario a este grupo, haz clic en **Añadir otro miembro** y repite estos pasos. Te recomendamos que añadas más de un miembro a cada grupo.
        
    5.  Cuando hayas terminado de añadir usuarios a este grupo, haz clic en **Guardar**.
        
4.  Cuando hayas terminado con todos los grupos, haz clic en **Confirmar usuarios y grupos**.
    
5.  Si quieres federar tu proveedor de identidades en Google Cloud, consulta lo siguiente:
    
    *   [Arquitecturas de referencia: uso de un proveedor de identidades externo](https://docs.cloud.google.com/architecture/identity/reference-architectures?hl=es#using_an_external_idp)
    *   Para aprovisionar usuarios automáticamente y habilitar el inicio de sesión único, consulta lo siguiente:
        *   [Federa Cloud Identity con Active Directory](https://docs.cloud.google.com/architecture/identity/federating-gcp-with-active-directory-introduction?hl=es).
        *   [Federar Cloud Identity con Microsoft Entra ID](https://docs.cloud.google.com/architecture/identity/federating-gcp-with-azure-active-directory?hl=es).
    *   Para sincronizar usuarios y grupos de Active Directory con Google Cloud, utiliza [Directory Sync](https://support.google.com/a/topic/10343447?hl=es) o [Google Cloud Directory Sync](https://support.google.com/a/answer/106368?hl=es).
        *   Para ver una comparación, consulta el artículo [Comparativa entre Directory Sync y GCDS](https://support.google.com/a/answer/10342938?hl=es).

### Conectar tu proveedor de identidades externo a Google Cloud

Puedes usar tu proveedor de identidades para crear y gestionar grupos y usuarios. Para configurar el inicio de sesión único en Google Cloud , configura la federación de identidades de trabajo con tu proveedor de identidades externo. Para conocer los conceptos clave de este proceso, consulta [Federación de Identidades de Workforce](https://docs.cloud.google.com/iam/docs/workforce-identity-federation?hl=es).

Para conectar tu proveedor de identidades externo, debes completar una configuración guiada que incluye los siguientes pasos:

1.  _Crea un grupo de empleados:_ un grupo de identidades de empleados te ayuda a gestionar las identidades y su acceso a los recursos. Debe introducir los siguientes datos en un formato legible por humanos.
    *   **ID de grupo de Workforce:** un identificador único global que se usa en IAM.
    *   **ID de proveedor:** nombre del proveedor que los usuarios especificarán al iniciar sesión en Google Cloud.
2.  _Configurar Google Cloud en tu proveedor:_ la configuración guiada incluye pasos específicos para tu proveedor.
3.  _Introduce los detalles del grupo de empleados de tu proveedor:_ para añadir tu proveedor como autoridad de confianza que afirme identidades, obtén los detalles de tu proveedor y añádelos a Google Cloud:
4.  _Configurar un conjunto inicial de grupos de administración:_ la configuración guiada incluye pasos específicos para tu proveedor. Asigna grupos en tu proveedor y establece una conexión con Google Cloud. Para obtener una descripción detallada de cada grupo, consulta [Crear grupos de administración](#create-admin-groups).
5.  _Asigna usuarios a cada grupo:_ te recomendamos que asignes más de un usuario a cada grupo.

Para obtener información general sobre el proceso de conexión de cada proveedor, consulte lo siguiente:

*   [Configura la federación de identidades de los empleados con Azure AD e inicia sesión con los usuarios](https://docs.cloud.google.com/iam/docs/workforce-sign-in-azure-ad?hl=es).
*   [Configurar la federación de identidades de los empleados con Okta e iniciar sesión de los usuarios](https://docs.cloud.google.com/iam/docs/workforce-sign-in-okta?hl=es)
*   Para otros proveedores que admitan OIDC o SAML, consulta [Configurar la federación de identidades de Workforce](https://docs.cloud.google.com/iam/docs/configuring-workforce-identity-federation?hl=es).

### Siguientes pasos

[Asigna permisos a tus grupos de administradores](#checklist-section-3).

### Acceso administrativo

En esta tarea, usarás Gestión de Identidades y Accesos (IAM) para asignar conjuntos de permisos a grupos de administradores a nivel de organización. Este proceso ofrece a los administradores una visibilidad y un control centralizados sobre todos los recursos en la nube que pertenezcan a tu organización.

Para obtener una descripción general de Gestión de Identidades y Accesos en Google Cloud, consulta la [información general sobre IAM](https://docs.cloud.google.com/iam/docs/overview?hl=es).

### ¿Quién lleva a cabo esta tarea?

Para realizar esta tarea, debes tener uno de los siguientes roles:

*   Un usuario superadministrador.
*   Un usuario con el rol Administrador de la organización (`roles/resourcemanager.organizationAdmin`).

### Qué debes hacer en esta tarea

*   Consulta una lista de los roles predeterminados asignados a cada grupo de administradores que hayas creado en la tarea [Usuarios y grupos](#checklist-section-2).
    
*   Si quieres personalizar un grupo, puedes hacer lo siguiente:
    
    *   Añade o quita roles.
    *   Si no tienes previsto usar un grupo, puedes eliminarlo.

### ¿Por qué recomendamos esta tarea?

Debes conceder explícitamente todos los roles de administrador de tu organización. Esta tarea te ayuda a implementar las siguientes prácticas recomendadas de seguridad:

*   [Principio de mínimos accesos:](https://en.wikipedia.org/wiki/Principle_of_least_privilege) concede a los usuarios los permisos mínimos necesarios para desempeñar su trabajo y retira el acceso en cuanto ya no lo necesiten.
    
*   [Control de acceso basado en roles (RBAC):](https://en.wikipedia.org/wiki/Role-based_access_control) asigna permisos a grupos de usuarios en función de su trabajo. No concedas roles a cuentas de usuario individuales.
    

### Antes de empezar

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).

### Conceder acceso a grupos de administradores

Para conceder el acceso adecuado a cada grupo de administradores que hayas creado en la tarea [Usuarios y grupos](#checklist-section-2), revisa los roles predeterminados que se han asignado a cada grupo. Puedes añadir o quitar roles para personalizar el acceso de cada grupo.

1.  Asegúrate de haber iniciado sesión en la Google Cloud consola como superadministrador.
    
    También puedes iniciar sesión como usuario con el rol de administrador de la organización (`roles/resourcemanager.organizationAdmin`).
    
2.  Ve a **Google Cloud Configuración: acceso de administrador**.
    
    [Ve a Acceso de administrador](https://console.cloud.google.com/cloud-setup/administrator?hl=es).
    
3.  Selecciona el nombre de tu organización en la lista desplegable **Seleccionar de**, situada en la parte superior de la página.
    
4.  Revisa el resumen de la tarea y haz clic en **Continuar con el acceso de administrador**.
    
5.  Revisa los grupos de la columna **Grupo (principal)** que has creado en la tarea [Usuarios y grupos](#checklist-section-2).
    
6.  Revisa los **roles de gestión de identidades y accesos** predeterminados de cada grupo. Puedes añadir o quitar roles asignados a cada grupo para adaptarlos a las necesidades específicas de tu organización.
    
    Cada rol contiene varios permisos que permiten a los usuarios realizar tareas relevantes. Para obtener más información sobre los permisos de cada rol, consulta la [referencia de roles básicos y predefinidos de gestión de identidades y accesos](https://docs.cloud.google.com/iam/docs/understanding-roles?hl=es#predefined).
    
7.  Cuando quieras asignar roles a cada grupo, haz clic en **Guardar y dar acceso**.
    

Siguientes pasos
----------------

Configura la [facturación](#checklist-section-4).

### Facturación

En esta tarea, configurarás una cuenta de facturación para pagar los recursos de Google Cloud. Para ello, asocia una de las siguientes opciones a tu organización.

*   Una cuenta de facturación de Cloud. Si no tienes acceso a la cuenta, puedes solicitarlo al administrador de tu cuenta de facturación.
    
*   Una cuenta de facturación de Cloud.
    

Para obtener más información sobre la facturación, consulta la [documentación de Facturación de Cloud](https://docs.cloud.google.com/billing/docs?hl=es).

### ¿Quién lleva a cabo esta tarea?

Una persona incluida en el grupo `gcp-billing-admins@YOUR_DOMAIN` que has creado en la tarea [Usuarios y grupos](#checklist-section-2).

### Qué debes hacer en esta tarea

*   Crea una cuenta de facturación de Cloud con servicio automático o usa una que ya tengas.
*   Decidir si quieres pasar de una cuenta de autoservicio a una cuenta facturada.
*   Configura una cuenta de facturación de Cloud y un método de pago.

### ¿Por qué recomendamos esta tarea?

Las cuentas de facturación de Cloud están vinculadas a uno o varios Google Cloud proyectos y se utilizan para pagar los recursos que utilizas, como las máquinas virtuales y los recursos de red y de almacenamiento.

### Determinar el tipo de cuenta de facturación

La cuenta de facturación que asocies a tu organización debe ser de uno de los siguientes tipos.

*   _Autogestión (u online):_ regístrate online con una tarjeta de crédito o débito. Te recomendamos esta opción si eres una pequeña empresa o un particular. Cuando te registras online para obtener una cuenta de facturación, esta se configura automáticamente como una cuenta de autoservicio.
    
*   _Facturado (u offline)_. Si ya tienes una cuenta de facturación de autoservicio, es posible que puedas solicitar la facturación mensual si tu empresa cumple los [requisitos](https://docs.cloud.google.com/billing/docs/how-to/invoiced-billing?hl=es).
    

No puedes crear una cuenta con facturación mensual online, pero sí puedes solicitar que se convierta una cuenta de autoservicio en una cuenta con facturación mensual.

Para obtener más información, consulta [Tipos de cuentas de facturación de Cloud](https://docs.cloud.google.com/billing/docs/concepts?hl=es#billing_account_types).

### Antes de empezar

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).

### Configurar la cuenta de facturación

Ahora que has [elegido un tipo de cuenta de facturación](#account-type), asocia la cuenta de facturación a tu organización. Cuando completes este proceso, podrás usar tu cuenta de facturación para pagar los recursos de Google Cloud .

1.  Inicia sesión en la Google Cloud consola como usuario del grupo `gcp-billing-admins@YOUR_DOMAIN`.
    
2.  Ve a **Google Cloud Configuración > Facturación**.
    
    [Ir a Facturación](https://console.cloud.google.com/cloud-setup/billing?hl=es)
    
3.  Revisa el resumen de la tarea y haz clic en **Continuar con la facturación**.
    
4.  Selecciona una de las siguientes opciones de cuenta de facturación:
    
    ### Crear una cuenta
    
    Si tu organización no tiene una cuenta, crea una.
    
    1.  Selecciona **Quiero crear una cuenta de facturación**.
    2.  Haz clic en **Continuar**.
    3.  Selecciona el tipo de cuenta de facturación que quieras crear. Para obtener instrucciones detalladas, consulta lo siguiente:
        
        *   Para crear una cuenta de autoservicio, consulta el artículo [Crear una cuenta de facturación de Cloud de autoservicio](https://docs.cloud.google.com/billing/docs/how-to/create-billing-account?hl=es).
        *   Para cambiar una cuenta de autoservicio a la facturación mensual, consulta el artículo [Solicitar la facturación mensual](https://docs.cloud.google.com/billing/docs/how-to/invoiced-billing?hl=es).
    4.  Verifica que se haya creado tu cuenta de facturación:
        
        1.  Si has creado una cuenta con facturación mensual, espera hasta 5 días hábiles para recibir el correo de confirmación.
            
        2.  Ve a la [página Facturación](https://console.cloud.google.com/billing?hl=es).
            
        3.  Selecciona tu organización en la lista **Seleccionar de**, situada en la parte superior de la página. Si la cuenta se ha creado correctamente, aparecerá en la lista de cuentas de facturación.
            
    
    ### Usar mi cuenta
    
    Si ya tienes una cuenta de facturación, puedes asociarla a tu organización.
    
    1.  Selecciona **He identificado una cuenta de facturación de esta lista que quiero usar para completar los pasos de configuración**.
    2.  En la lista desplegable **Facturación**, selecciona la cuenta que quieras asociar a tu organización.
    3.  Haz clic en **Continuar**.
    4.  Revisa los detalles y haz clic en **Confirmar cuenta de facturación**.
    
    ### Usar la cuenta de otro usuario
    
    Si otro usuario tiene acceso a una cuenta de facturación, puedes pedirle que la asocie a tu organización o que te dé acceso para completar la asociación.
    
    1.  Selecciona **Quiero usar una cuenta de facturación gestionada por otra cuenta de usuario de Google**.
    2.  Haz clic en **Continuar**.
    3.  Introduce la dirección de correo del administrador de la cuenta de facturación.
    4.  Haz clic en **Contactar con el administrador**.
    5.  Espera a que el administrador de la cuenta de facturación se ponga en contacto contigo para darte más instrucciones.
    

### Siguientes pasos

[Crea una jerarquía de recursos y asigna acceso](#checklist-section-5).

Crear una arquitectura inicial
------------------------------

### Jerarquía y acceso

En esta tarea, configurarás tu jerarquía de recursos creando y asignando acceso a los siguientes recursos:

[**Carpetas**](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=es#folders)

Ofrecen un mecanismo de agrupación y límites de aislamiento entre proyectos. Por ejemplo, las carpetas pueden representar departamentos de tu organización, como finanzas o comercio.

Las carpetas de entorno, como `Production`, de tu jerarquía de recursos están [configuradas para la gestión de aplicaciones](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=es). Puedes definir y gestionar aplicaciones en estas carpetas.

[**Proyectos**](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=es#projects)

Contienen tus Google Cloud recursos, como máquinas virtuales, bases de datos y segmentos de almacenamiento. Cada una de las carpetas de entorno también contiene un [proyecto de gestión](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=es#management-project), que te ayuda a gestionar el acceso, la facturación, la observabilidad y otras funciones administrativas de tus aplicaciones.

Para conocer las consideraciones de diseño y las prácticas recomendadas para organizar tus recursos en proyectos, consulta el artículo [Decidir una jerarquía de recursos para tu Google Cloud zona de aterrizaje](https://docs.cloud.google.com/architecture/landing-zones/decide-resource-hierarchy?hl=es).

### ¿Quién lleva a cabo esta tarea?

Una persona incluida en el grupo `gcp-organization-admins@YOUR_DOMAIN` que has creado en la tarea [Usuarios y grupos](#checklist-section-2) puede realizar esta tarea.

### Qué debes hacer en esta tarea

*   Crea una estructura jerárquica inicial que incluya carpetas y proyectos.
*   Define políticas de gestión de identidades y accesos para controlar el acceso a tus carpetas y proyectos.

### ¿Por qué recomendamos esta tarea?

Crear una estructura para carpetas y proyectos te ayuda a gestionar losGoogle Cloud recursos y las aplicaciones. Puedes usar la estructura para asignar acceso en función del funcionamiento de tu organización. Por ejemplo, puedes organizar y proporcionar acceso en función de la colección única de regiones geográficas, estructuras de filiales o marcos de rendición de cuentas de tu organización.

### Configurar la jerarquía de recursos

La jerarquía de recursos te ayuda a crear límites y a compartir recursos en toda tu organización para realizar tareas comunes. Puedes crear tu jerarquía con una de las siguientes configuraciones iniciales, en función de la estructura de tu organización:

*   _Orientado a entornos sencillos:_
    
    *   Aísla entornos como `Non-production` y `Production`.
    *   Implementa políticas, requisitos normativos y controles de acceso distintos en cada carpeta de entorno.
    *   Ideal para pequeñas empresas con entornos centralizados.
*   _Sencillo y orientado al equipo:_
    
    *   Aísla equipos como `Development` y `QA`.
    *   Aísla el acceso a los recursos mediante carpetas de entorno secundarias en cada carpeta de equipo.
    *   Ideal para pequeñas empresas con equipos autónomos.
*   _Orientado al entorno:_
    
    *   Prioriza el aislamiento de entornos como `Non-production` y `Production`.
    *   En cada carpeta de entorno, aísla las unidades de negocio.
    *   En cada unidad de negocio, aísla los equipos.
    *   Ideal para grandes empresas con entornos centralizados.
*   _Orientado a la unidad de negocio:_
    
    *   Prioriza el aislamiento de las unidades de negocio, como `Human Resources` y `Engineering`, para asegurarte de que los usuarios solo puedan acceder a los recursos y los datos que necesiten.
    *   En cada unidad de negocio, aísla los equipos.
    *   Aísla los entornos de cada equipo.
    *   Ideal para grandes empresas con equipos autónomos.

Cada configuración tiene una carpeta `Common` para los proyectos que contienen recursos compartidos. Esto puede incluir proyectos de registro y monitorización.

### Antes de empezar

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a grupos en la tarea [Acceso administrativo](#checklist-section-3).
*   Crea o vincula una cuenta de facturación en la tarea [Facturación](#checklist-section-4).

### Configurar carpetas y proyectos iniciales

Selecciona la jerarquía de recursos que represente la estructura de tu organización.

Para configurar las carpetas y los proyectos iniciales, haz lo siguiente:

1.  Inicia sesión en la Google Cloud consola como usuario del grupo `gcp-organization-admins@YOUR_DOMAIN` que has creado en la tarea [Usuarios y grupos](#checklist-section-2).
    
2.  En la lista desplegable **Seleccionar de**, situada en la parte superior de la página, selecciona tu organización.
    
3.  Ve a **Google Cloud Configuración: jerarquía y acceso**.
    
    [Ir a Jerarquía y acceso](https://console.cloud.google.com/cloud-setup/hierarchy-and-access?hl=es)
    
4.  Revisa el resumen de la tarea y, a continuación, haz clic en **Iniciar** junto a **Jerarquía de recursos**.
    
5.  Selecciona una configuración inicial.
    
6.  Haga clic en **Continuar y configurar**.
    
7.  Personaliza la jerarquía de recursos para que refleje la estructura de tu organización. Por ejemplo, puedes personalizar lo siguiente:
    
    *   Nombres de carpetas.
    *   Proyectos de servicio para cada equipo. Para conceder acceso a proyectos de servicio, puedes crear lo siguiente:
        
        *   Un grupo para cada proyecto de servicio.
        *   Usuarios de cada grupo.
        
        Para obtener una descripción general de los proyectos de servicio, consulta [VPC compartida](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=es).
        
    *   Proyectos necesarios para la monitorización, el registro y las redes.
        
    *   Proyectos personalizados.
        
8.  Haz clic en **Continuar**.
    
9.  [Da acceso a tus carpetas y proyectos.](#configure-access-control)
    

### Conceder acceso a tus carpetas y proyectos

En la tarea [Acceso de administrador](#checklist-section-3), has concedido acceso de administrador a grupos a nivel de organización. En esta tarea, configurarás el acceso a los grupos que interactúan con las carpetas y los proyectos que has configurado.

Los proyectos, las carpetas y las organizaciones tienen sus propias políticas de gestión de identidades y accesos, que se heredan a través de la jerarquía de recursos:

*   _Organización:_ las políticas se aplican a todas las carpetas y proyectos de la organización.
*   _Carpeta:_ las políticas se aplican a los proyectos y a otras carpetas de la carpeta.
*   _Proyecto:_ las políticas solo se aplican a ese proyecto y a sus recursos.

Actualiza las [políticas de gestión de identidades y accesos](https://docs.cloud.google.com/iam/docs/overview?hl=es#iam_policy) de tus carpetas y proyectos:

1.  En la sección **Configurar el control de acceso** de [**Jerarquía y acceso**](https://console.cloud.google.com/cloud-setup/hierarchy-and-access?hl=es), concede a tus grupos acceso a tus carpetas y proyectos:
    
    1.  En la tabla, revisa la lista de roles de gestión de identidades y accesos recomendados que se han concedido a cada grupo para cada recurso.
        
    2.  Si quieres modificar los roles asignados a cada grupo, haz clic en **Editar** en la fila correspondiente.
        
        Para obtener más información sobre cada rol, consulta el artículo [Roles básicos y predefinidos de gestión de identidades y accesos](https://docs.cloud.google.com/iam/docs/understanding-roles?hl=es).
        
2.  Haz clic en **Continuar**.
    
3.  Revisa los cambios y haz clic en **Confirmar configuración del borrador**.
    

### Configurar la facturación de proyectos de gestión

Una vez que hayas implementado la configuración, debes configurar la facturación de cada proyecto de gestión. La cuenta de facturación es necesaria para pagar las APIs que tienen costes asociados. Para obtener más información, consulta [Vincular una cuenta de facturación a un proyecto de gestión](https://docs.cloud.google.com/resource-manager/docs/manage-applications?hl=es#billing).

### Siguientes pasos

[Configura los ajustes de seguridad](#checklist-section-6).

### Seguridad

En esta tarea, configurarás los ajustes y productos de seguridad para ayudar a proteger tu organización.

### ¿Quién lleva a cabo esta tarea?

Para completar esta tarea, debes tener uno de los siguientes elementos:

*   El rol Administrador de la organización (`roles/resourcemanager.organizationAdmin`).
*   Por ser miembro de uno de los siguientes grupos que has creado en la tarea [Usuarios y grupos](#checklist-section-2):
    *   `gcp-organization-admins@<your-domain>.com`
    *   `gcp-security-admins@<your-domain>.com`

### Qué debes hacer en esta tarea

Aplica las políticas de organización recomendadas en función de las siguientes categorías:

*   Gestión de accesos.
*   Comportamiento de la cuenta de servicio.
*   Configuración de la red de VPC.
*   Cloud KMS con Autokey: solo está disponible en la opción de base [**Seguridad mejorada**](#select-foundation-option).

También puedes habilitar Security Command Center para centralizar los informes de vulnerabilidades y amenazas.

### ¿Por qué recomendamos esta tarea?

Si aplicas las políticas de organización recomendadas, podrás limitar las acciones de los usuarios que no se ajusten a tu postura de seguridad.

Habilitar Security Command Center te ayuda a crear una ubicación central para analizar vulnerabilidades y amenazas.

Si aplicas y automatizas [Cloud KMS con Autokey](https://docs.cloud.google.com/kms/docs/kms-autokey?hl=es), podrás usar las claves de cifrado gestionadas por el cliente (CMEKs) de forma coherente para proteger tus recursos.

### Antes de empezar

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).

### Iniciar la tarea de seguridad

1.  Inicia sesión en la Google Cloud consola con un usuario que hayas identificado en la sección [Quién realiza esta tarea](#who-performs-security).
    
2.  Selecciona tu organización en el menú desplegable **Seleccionar de**, situado en la parte superior de la página.
    
3.  Ve a **Google Cloud Configuración > Seguridad**.
    
    [Ir a Seguridad](https://console.cloud.google.com/cloud-setup/security?hl=es)
    
4.  Revisa la información general de la tarea y, a continuación, haz clic en **Iniciar seguridad**.
    

### Centralizar los informes de vulnerabilidades y amenazas

Para centralizar los servicios de informes de vulnerabilidades y amenazas, habilita Security Command Center. De esta forma, puedes reforzar tu posición de seguridad y mitigar los riesgos. Para obtener más información, consulta el [resumen de Security Command Center](https://docs.cloud.google.com/security-command-center/docs/security-command-center-overview?hl=es).

1.  En la página **Google Cloud Configuración: Seguridad**, asegúrate de que la casilla **Habilitar Security Command Center Standard** esté marcada.
    
    Esta tarea habilita el nivel Standard gratuito. Puedes cambiar a la versión Premium más adelante. Para obtener más información, consulta [Niveles de servicio de Security Command Center](https://docs.cloud.google.com/security-command-center/docs/service-tiers?hl=es).
    
2.  Haga clic en **Aplicar ajustes de SCC**.
    

### Aplicar las políticas de organización recomendadas

Las políticas de la organización se aplican a nivel de organización y las heredan las carpetas y los proyectos. En esta tarea, revisa y aplica la lista de políticas recomendadas. Puedes modificar las políticas de la organización en cualquier momento. Para obtener más información, consulta el artículo [Introducción al servicio de políticas de la organización](https://docs.cloud.google.com/resource-manager/docs/organization-policy/overview?hl=es).

1.  Revisa la lista de políticas de la organización recomendadas. Si no quieres aplicar una política recomendada, haz clic en su casilla para quitarla.
    
    Para obtener una explicación detallada de cada política de la organización, consulta [Restricciones de las políticas de la organización](https://docs.cloud.google.com/resource-manager/docs/organization-policy/org-policy-constraints?hl=es).
    
2.  Haz clic en **Confirmar configuraciones de políticas de organización**.
    

Las políticas de la organización que selecciones se aplicarán cuando implementes la configuración en una tarea posterior.

### Implementar y automatizar claves de cifrado de clientes

Cloud KMS con Autokey permite a los desarrolladores de tu organización crear claves de cifrado simétricas cuando sea necesario para proteger tus recursos de Google Cloud. Puedes configurar Cloud KMS con Autokey si has seleccionado la opción de base [**Seguridad mejorada**](#select-foundation-option).

1.  Lee la descripción de Cloud KMS con Autokey y, en **Usar Cloud KMS con Autokey y aplicar políticas de organización**, haz clic en **Sí (recomendado)**.
2.  Haz clic en **Confirmar configuración de gestión de claves**.

Las siguientes configuraciones se aplican cuando implementas la configuración en una tarea posterior:

*   Configura un proyecto de Autokey en cada carpeta de entorno de tu jerarquía.
*   Habilita Cloud KMS con Autokey en las carpetas de entorno.
*   Requerir el uso de claves de cifrado gestionadas por el cliente (CMEKs) para los recursos creados en cada carpeta de entorno.
*   Restringe cada carpeta para que solo use las claves de Cloud KMS del proyecto Autokey de esa carpeta.

### Siguientes pasos

[Registro y monitorización centralizados.](#checklist-section-7)

### Registro y monitorización centralizados

En esta tarea, configurará lo siguiente:

*   Registro centralizado para ayudarte a analizar y obtener información valiosa de los registros de todos los proyectos de tu organización.
*   Monitorización centralizada para ayudarte a visualizar las métricas de todos los proyectos creados en esta configuración.

### ¿Quién lleva a cabo esta tarea?

Para configurar el registro y la monitorización, debes tener una de las siguientes opciones:

*   Los roles Administrador de almacenamiento de registros (`roles/logging.admin`) y Administrador de monitorización (`roles/monitoring.admin`).
*   Por ser miembro de uno de los siguientes grupos que has creado en la tarea [Usuarios y grupos](#checklist-section-2):
    *   `gcp-organization-admins@YOUR_DOMAIN`
    *   `gcp-security-admins@YOUR_DOMAIN`
    *   `gcp-logging-monitoring-admins@YOUR_DOMAIN`

### Qué debes hacer en esta tarea

En esta tarea, debes hacer lo siguiente:

*   Organiza de forma centralizada los registros que se crean en los proyectos de tu organización para mejorar la seguridad, la auditoría y el cumplimiento.
*   Configura un proyecto de monitorización central para que tenga acceso a las métricas de monitorización de los proyectos que hayas creado en esta configuración.

### ¿Por qué recomendamos esta tarea?

El almacenamiento y la conservación de registros simplifican el análisis y conservan tu registro de auditoría. La monitorización centralizada te permite ver las métricas en un solo lugar.

### Antes de empezar

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).
*   Crea o vincula una cuenta de facturación en la tarea [Facturación](#checklist-section-4).
*   Configura tu jerarquía y asigna acceso en la tarea [Jerarquía y acceso](#checklist-section-5).

### Organizar el registro de forma centralizada

Cloud Logging te ayuda a almacenar, buscar, analizar, monitorizar y recibir alertas sobre datos de registro y eventos de Google Cloud. También puedes recoger y procesar registros de tus aplicaciones, recursos on-premise y otras nubes. Te recomendamos que uses Cloud Logging para consolidar los registros en un solo contenedor de registros.

Para obtener más información, consulta las siguientes secciones:

*   Para obtener información general, consulta el artículo [Información general sobre el enrutamiento y el almacenamiento](https://docs.cloud.google.com/logging/docs/routing/overview?hl=es).
*   Para obtener información sobre el registro de recursos locales, consulta el artículo [Registrar recursos locales con BindPlane](https://docs.cloud.google.com/architecture/logging-on-premises-resources-with-bindplane?hl=es).
*   Para ver los pasos que debes seguir para cambiar el filtro de registros después de implementar la configuración, consulta [Filtros de inclusión](https://docs.cloud.google.com/logging/docs/routing/overview?hl=es#inclusion-filters).

Para almacenar los datos de registro en un contenedor de registro central, haga lo siguiente:

1.  Inicia sesión en la Google Cloud consola como usuario que hayas identificado en la sección [Quién realiza esta tarea](#who-performs-logging).
    
2.  En la lista desplegable **Seleccionar de**, situada en la parte superior de la página, selecciona tu organización.
    
3.  Vaya a **Google Cloud Configuración: registro y monitorización centralizados**.
    
    [Ir a registro y monitorización centralizados](https://console.cloud.google.com/cloud-setup/logging?hl=es)
    
4.  Revisa la descripción general de la tarea y haz clic en **Iniciar registro y monitorización centralizados**.
    
5.  Revisa los detalles de la tarea.
    
6.  Para enrutar los registros a un segmento de registros central, asegúrate de que la opción **Almacenar registros de auditoría a nivel de organización en un segmento de registros** esté seleccionada.
    
7.  Despliega **Dirigir registros a un bucket de registro de Logging** y haz lo siguiente:
    
    1.  En el campo **Nombre del contenedor de registro**, introduce un nombre para el contenedor de registro central.
        
    2.  En la lista **Región del segmento de registro**, selecciona la región en la que se almacenan tus datos de registro.
        
        Para obtener más información, consulta [Ubicaciones de los segmentos de registro](https://docs.cloud.google.com/logging/docs/region-support?hl=es#buckets-locations).
        
    3.  De forma predeterminada, los registros se almacenan durante 30 días. Recomendamos que las grandes empresas almacenen los registros durante 365 días. Para personalizar el periodo de conservación, introduce el número de días en el campo **Periodo de conservación**.
        
        Los registros almacenados durante más de 30 días tienen un coste de retención. Para obtener más información, consulta el [resumen de precios de Cloud Logging](https://cloud.google.com/stackdriver/pricing?hl=es).
        

### Exportar registros fuera de Google Cloud

Si quieres exportar registros a un destino que no sea Google Cloud, puedes hacerlo mediante Pub/Sub. Por ejemplo, si utilizas varios proveedores de servicios en la nube, puedes decidir exportar los datos de registro de cada proveedor a una herramienta de terceros.

Puedes filtrar los registros que exportes para que se ajusten a tus necesidades y requisitos específicos. Por ejemplo, puedes limitar los tipos de registros que exportas para controlar los costes o reducir el ruido en tus datos.

Para obtener más información sobre la exportación de registros, consulta los siguientes artículos:

*   Para obtener una descripción general, consulta [¿Qué es Pub/Sub?](https://docs.cloud.google.com/pubsub/docs/overview?hl=es)
*   Para obtener información sobre los precios, consulta lo siguiente:
    *   [Precios de Pub/Sub](https://cloud.google.com/pubsub/pricing?hl=es).
    *   [Prácticas recomendadas de Registros de auditoría de Cloud: precios](https://docs.cloud.google.com/logging/docs/audit/best-practices?hl=es#pricing)
*   Para obtener información sobre cómo enviar registros a Splunk, consulta [Enviar registros de Google Cloud a Splunk](https://docs.cloud.google.com/architecture/stream-logs-from-google-cloud-to-splunk?hl=es).

Para exportar los registros, haz lo siguiente:

1.  Haz clic en **Enviar tus registros a otras aplicaciones, otros repositorios o terceros**.
    
2.  En el campo **ID de tema de Pub/Sub**, introduce un identificador para el tema que contiene los registros exportados. Para obtener información sobre cómo suscribirte a un tema, consulta [Suscripciones de extracción](https://docs.cloud.google.com/pubsub/docs/pull?hl=es).
    
3.  Para seleccionar los registros que quieras exportar, sigue estos pasos:
    
    1.  Para obtener información sobre cada tipo de registro, consulta el artículo [Información sobre Cloud Audit Logs](https://docs.cloud.google.com/logging/docs/audit/best-practices?hl=es#understand-audit-logs).
        
    2.  Para evitar que se exporte uno de los siguientes registros recomendados, haz clic en la lista **Filtro de inclusión** y desmarca la casilla del registro:
        
        *   **Registros de auditoría de Cloud: actividad de administrador:** llamadas a la API o acciones que modifican la configuración o los metadatos de los recursos.
        *   **Registros de auditoría de Cloud: evento del sistema:** Google Cloud acciones que modifican la configuración de los recursos.
        *   **Transparencia de acceso:** acciones que lleva a cabo el personal de Google al acceder al contenido de los clientes.
    3.  Seleccione los siguientes registros adicionales para exportarlos:
        
        *   **Registros de auditoría de Cloud: acceso a los datos:** llamadas a la API que leen la configuración o los metadatos de los recursos, y llamadas a la API controladas por el usuario que crean, modifican o leen los datos de los recursos proporcionados por el usuario.
        *   **Registros de auditoría de Cloud: acceso denegado por infracción de las políticas:** Google Cloud denegaciones de acceso al servicio a cuentas de usuario o de servicio, basadas en infracciones de las políticas de seguridad.
    4.  Los registros que seleccione en este paso solo se exportarán si están habilitados en sus proyectos o recursos. Para ver los pasos que debes seguir para cambiar el filtro de registro de tus proyectos y recursos después de implementar la configuración, consulta [Filtros de inclusión](https://docs.cloud.google.com/logging/docs/routing/overview?hl=es#inclusion-filters).
        
    5.  Haz clic en **Aceptar**.
        
4.  Haz clic en **Continuar con la monitorización**.
    

### Configurar la monitorización centralizada

La monitorización centralizada te ayuda a analizar el estado, el rendimiento y la seguridad del sistema de varios proyectos. En esta tarea, añades los proyectos que has creado durante la tarea [Jerarquía y acceso](#checklist-section-5) a un proyecto de ámbito. Después, podrá monitorizar esos proyectos desde el proyecto de ámbito. Una vez que haya completado la configuración de Cloud, podrá configurar otros proyectos para que los monitorice el proyecto de ámbito.

Para obtener más información, consulta el [resumen del ámbito de las métricas](https://docs.cloud.google.com/monitoring/settings?hl=es).

Para configurar la monitorización centralizada, haga lo siguiente:

1.  Para configurar los proyectos creados durante la Google Cloud configuración de la monitorización centralizada, asegúrate de que la opción **Usar monitorización centralizada** esté seleccionada.
    
    Los proyectos que hayas creado durante la configuración se añadirán al [ámbito de las métricas](https://docs.cloud.google.com/monitoring/settings?hl=es) del **proyecto de ámbito** que se indica. Google Cloud
    
2.  Cloud Monitoring incluye una asignación mensual gratuita. Para obtener más información, consulta el [resumen de precios de Cloud Monitoring](https://cloud.google.com/stackdriver/pricing?hl=es#monitoring-pricing-summary).
    
3.  Para saber cómo configurar los proyectos que cree fuera de la configuración de Google Cloud , consulte lo siguiente:
    
    *   [Configurar un ámbito de métricas](https://docs.cloud.google.com/monitoring/settings/multiple-projects?hl=es).
    *   [Límites de proyectos monitorizados](https://docs.cloud.google.com/monitoring/quotas?hl=es#workspace_limits)

### Completar la configuración

Para completar la tarea de registro y monitorización, haz lo siguiente:

1.  Haz clic en **Confirmar configuración**.
    
2.  Revisa los detalles de la configuración de registro y monitorización. La configuración no se implementará hasta que lo hagas en una tarea posterior.
    

### Siguientes pasos

[Determina la configuración inicial de tu red](#checklist-section-8).

### Redes de VPC

En esta tarea, puedes elegir la configuración inicial de tu red, que puedes ampliar a medida que cambien tus necesidades.

### Arquitectura de la nube privada virtual

Una red de [nube privada virtual (VPC)](https://docs.cloud.google.com/vpc/docs/overview?hl=es) es una versión virtual de una red física que se implementa en la red de producción de Google. Una red de VPC es un recurso global que consta de [subredes](https://docs.cloud.google.com/vpc/docs/subnets?hl=es) regionales.

Las redes VPC proporcionan funciones de red a tus Google Cloud recursos, como las instancias de máquina virtual de Compute Engine, los contenedores de GKE y las instancias del entorno flexible de App Engine.

La [VPC compartida](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=es) conecta recursos de varios proyectos a una red de VPC común para que puedan comunicarse entre sí mediante las direcciones IP internas de la red. En el siguiente diagrama se muestra la arquitectura básica de una red de VPC compartida con proyectos de servicio adjuntos.

Cuando usas la VPC compartida, designas un proyecto host y le asocias uno o varios proyectos de servicio. Las redes de nube privada virtual del proyecto host se denominan redes de VPC compartida.

El diagrama de ejemplo tiene proyectos host de producción y fuera de producción, y cada uno de ellos contiene una red de VPC compartida. Puedes usar un proyecto host para gestionar de forma centralizada lo siguiente:

*   Rutas
*   Cortafuegos
*   Conexiones VPN
*   Subredes

Un proyecto de servicio es cualquier proyecto que esté vinculado a un proyecto del host. Puedes compartir subredes, incluidos rangos secundarios, entre proyectos host y de servicio.

En este tipo de arquitectura, cada red VPC compartida contiene subredes públicas y privadas:

*   Las instancias orientadas a Internet pueden usar la subred pública para la conectividad externa.
*   La subred privada se puede usar para instancias internas que no tienen asignadas direcciones IP públicas.

En esta tarea, crearás una configuración de red inicial basada en el diagrama de ejemplo.

### ¿Quién lleva a cabo esta tarea?

Para realizar esta tarea, necesitas uno de los siguientes elementos:

*   El rol `roles/compute.networkAdmin`.
*   Se incluye en el grupo `gcp-network-admins@YOUR_DOMAIN` que has creado en la tarea [Usuarios y grupos](#checklist-section-2).

### Qué debes hacer en esta tarea

Crea una configuración de red inicial, que incluya lo siguiente:

*   Crea varios proyectos host para reflejar tus entornos de desarrollo.
*   Crea una red de VPC compartida en cada proyecto del host para que los distintos recursos puedan compartir la misma red.
*   Crea subredes distintas en cada red de VPC compartida para proporcionar acceso a la red a los proyectos de servicio.

### ¿Por qué recomendamos esta tarea?

Los distintos equipos pueden usar una VPC compartida para conectarse a una red de VPC común y gestionada de forma centralizada.

Antes de empezar
----------------

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).
*   Crea o vincula una cuenta de facturación en la tarea [Facturación](#checklist-section-4).
*   Configura tu jerarquía y asigna acceso en la tarea [Jerarquía y acceso](#checklist-section-5).

### Configurar la arquitectura de red

Crea tu configuración de red inicial con dos proyectos de host para segmentar las cargas de trabajo de producción y no de producción. Cada proyecto del host contiene una red de VPC compartida, que pueden usar varios proyectos de servicio. Configura los detalles de la red y, después, implementa un archivo de configuración en una tarea posterior.

Para configurar tu red inicial, sigue estos pasos.

1.  Inicia sesión en la Google Cloud consola como usuario del grupo `gcp-organization-admins@YOUR_DOMAIN` que has creado en la tarea [Usuarios y grupos](#checklist-section-2).
    
2.  Selecciona tu organización en la lista desplegable **Seleccionar una organización**, situada en la parte superior de la página.
    
3.  Ve a **Google Cloud Configuración: redes**.
    
    [Ir a Redes](https://console.cloud.google.com/cloud-setup/networking?hl=es)
    
4.  Revisa la arquitectura de red predeterminada.
    
5.  Para editar el nombre de la red, sigue estos pasos:
    
    1.  Haz clic en **Acciones**.
    2.  Selecciona **Editar nombre de la red**.
    3.  En el campo **Nombre de la red**, introduce letras minúsculas, números o guiones. El nombre de la red no puede tener más de 25 caracteres.
    4.  Haz clic en **Guardar**.

#### Modificar los detalles del cortafuegos

Las reglas de cortafuegos predeterminadas del proyecto host se basan en las prácticas recomendadas. Puedes inhabilitar una o varias de las reglas de cortafuegos predeterminadas. Para obtener información general sobre las reglas de cortafuegos, consulte [Reglas de cortafuegos de VPC](https://docs.cloud.google.com/firewall/docs/firewalls?hl=es).

Para modificar la configuración del cortafuegos, sigue estos pasos:

1.  Haz clic en **Acciones**.
    
2.  Selecciona **Editar reglas de cortafuegos**.
    
3.  Para obtener información detallada sobre cada regla de cortafuegos predeterminada, consulta [Reglas predefinidas en la red predeterminada](https://docs.cloud.google.com/firewall/docs/firewalls?hl=es#more_rules_default_vpc).
    
4.  Para inhabilitar una regla de cortafuegos, desmarque la casilla correspondiente.
    
5.  Para inhabilitar **Almacenamiento de registros de reglas de cortafuegos**, haz clic en **Desactivado**.
    
    De forma predeterminada, el tráfico hacia y desde las instancias de Compute Engine se registra con fines de auditoría. Este proceso conlleva costes. Para obtener más información, consulta [Almacenamiento de registros de reglas de cortafuegos](https://docs.cloud.google.com/firewall/docs/firewall-rules-logging?hl=es).
    
6.  Haz clic en **Guardar**.
    

#### Modificar los detalles de una subred

Cada red de VPC contiene al menos una subred, que es un recurso regional con un intervalo de direcciones IP asociado. En esta configuración multirregional, debes tener al menos dos subredes con intervalos de IPs que no se solapen.

Para obtener más información, consulta [Subredes](https://docs.cloud.google.com/vpc/docs/subnets?hl=es).

Cada subred se configura siguiendo las prácticas recomendadas. Si quieres personalizar cada subred, haz lo siguiente:

1.  Haz clic en **Acciones**.
2.  Selecciona **Editar subredes**.
3.  En el campo **Nombre**, introduce letras minúsculas, números o guiones. El nombre de la subred no puede tener más de 25 caracteres.
4.  En el menú desplegable **Región**, selecciona una región que esté cerca de tu punto de servicio.
    
    Te recomendamos que elijas una región diferente para cada subred. No puedes cambiar la región después de implementar la configuración. Para obtener información sobre cómo elegir una región, consulta [Recursos regionales](https://docs.cloud.google.com/compute/docs/regions-zones/global-regional-zonal-resources?hl=es#regionalresources).
    
5.  En el campo **Intervalo de direcciones IP**, introduce un intervalo en notación CIDR (por ejemplo, 10.0.0.0/24).
    
    El intervalo que introduzcas no debe solaparse con otras subredes de esta red. Para obtener información sobre los intervalos válidos, consulte [Intervalos de subred IPv4](https://docs.cloud.google.com/vpc/docs/vpc?hl=es#manually_created_subnet_ip_ranges).
    
6.  Repite estos pasos para la subred 2.
    
7.  Para configurar subredes adicionales en esta red, haz clic en **Añadir subred** y repite estos pasos.
    
8.  Haz clic en **Guardar**.
    

Las subredes se configuran automáticamente de acuerdo con las prácticas recomendadas. Si quieres modificar la configuración, en la página **Google Cloud Configuración: redes de VPC**, haz lo siguiente:

1.  Para desactivar los registros de flujo de VPC, en la columna **Registros de flujo**, selecciona **Desactivado**.
    
    Cuando los registros de flujo están activados, cada subred registra los flujos de red que puede analizar para mejorar la seguridad, optimizar los gastos y otros fines. Para obtener más información, consulta [Usar registros de flujo de VPC](https://docs.cloud.google.com/vpc/docs/using-flow-logs?hl=es).
    
    Los registros de flujo de VPC generan costes. Para obtener más información, consulta la página [Precios de Virtual Private Cloud](https://cloud.google.com/vpc/pricing?hl=es).
    
2.  Para desactivar el acceso privado de Google, en la columna **Acceso privado**, selecciona **Desactivado**.
    
    Cuando Acceso privado de Google está activado, las instancias de VM que no tienen direcciones IP externas pueden acceder a las APIs y los servicios de Google. Para obtener más información, consulta [Acceso privado a Google](https://docs.cloud.google.com/vpc/docs/private-google-access?hl=es).
    
3.  Para activar Cloud NAT, en la columna **Cloud NAT**, selecciona **Activado**.
    
    Cuando Cloud NAT está activado, determinados recursos pueden crear conexiones salientes a Internet. Para obtener más información, consulta la [descripción general de Cloud NAT](https://docs.cloud.google.com/nat/docs/overview?hl=es).
    
    Cloud NAT genera costes. Para obtener más información, consulta la página [Precios de nube privada virtual](https://cloud.google.com/vpc/pricing?hl=es).
    
4.  Haz clic en **Continuar para vincular proyectos de servicio**.
    

### Vincular proyectos de servicio a proyectos del host

Un proyecto de servicio es cualquier proyecto que se haya vinculado a un proyecto del host. Este adjunto permite que el proyecto de servicio participe en la VPC compartida. Cada proyecto de servicio puede ser gestionado y administrado por diferentes departamentos o equipos para separar las responsabilidades.

Para obtener más información sobre cómo conectar varios proyectos a una red de VPC común, consulta la [descripción general de la VPC compartida](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=es).

Para vincular proyectos de servicio a tus proyectos host y completar la configuración, haz lo siguiente:

1.  En cada subred de la tabla **Redes de VPC compartidas**, selecciona un proyecto de servicio al que conectarte. Para ello, selecciona un proyecto en el menú desplegable **Seleccionar un proyecto** de la columna **Proyecto de servicio**.
    
    Puedes conectar un proyecto de servicio a varias subredes.
    
2.  Haz clic en **Continuar con la revisión**.
    
3.  Revisa la configuración y haz los cambios que quieras.
    
    Puedes hacer cambios hasta que implementes el archivo de configuración.
    
4.  Haz clic en **Confirmar configuración del borrador**. La configuración de tu red se añade al archivo de configuración.
    
    Tu red no se desplegará hasta que despliegues el archivo de configuración en una tarea posterior.
    

### Siguientes pasos

[Configura la conectividad híbrida](#checklist-section-9), que te ayuda a conectar servidores on-premise u otros proveedores de servicios en la nube a Google Cloud.

### Conectividad híbrida

En esta tarea, establecerás conexiones entre tus redes de otro proveedor de la nube o locales y tus redes de Google Cloud , como se muestra en el siguiente diagrama.

Este proceso crea una VPN de alta disponibilidad (HA), que es una solución de alta disponibilidad que puedes crear rápidamente para transmitir datos a través de la red de Internet pública.

Después de implementar la configuración de Google Cloud , te recomendamos que crees una conexión más sólida con [Cloud Interconnect](https://docs.cloud.google.com/network-connectivity/docs/interconnect/concepts/overview?hl=es).

Para obtener más información sobre las conexiones entre redes de pares y Google Cloud, consulta lo siguiente:

*   [Descripción general de Cloud VPN](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=es)
*   [Elegir un producto de conectividad de red](https://docs.cloud.google.com/network-connectivity/docs/how-to/choose-product?hl=es)

### ¿Quién lleva a cabo esta tarea?

Debe tener el rol Administrador de la organización (`roles/resourcemanager.organizationAdmin`).

### Qué debes hacer en esta tarea

Crea conexiones de baja latencia y alta disponibilidad entre tus redes de VPC y tus redes on-premise u otras redes en la nube. Configura los siguientes componentes:

*   _Google Cloud Pasarela VPN de alta disponibilidad:_ recurso regional que tiene dos interfaces, cada una con su propia dirección IP. Especifica el tipo de pila IP, que determina si se admite el tráfico IPv6 en tu conexión. Para obtener información general, consulta [VPN de alta disponibilidad](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=es#ha-vpn).
*   _Pasarela VPN de par:_ la pasarela de tu red de par a la que se conecta la pasarela de VPN de alta disponibilidad. Google Cloud Introduces las direcciones IP externas que usa tu pasarela de peer para conectarse a Google Cloud. Para obtener información general, consulta [Configurar la pasarela de VPN de par](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=es).
*   _Cloud Router:_ usa el protocolo de pasarela fronteriza (BGP) para intercambiar rutas de forma dinámica entre tu VPC y las redes de los pares. Asigna un número de sistema autónomo (ASN) como identificador de tu Cloud Router y especifica el ASN que usa tu router de peer. Para obtener información general, consulta [Crear un Cloud Router para conectar una red de VPC a una red de emparejamiento](https://docs.cloud.google.com/network-connectivity/docs/router/how-to/create-router-vpc-network?hl=es).
*   _Túneles VPN:_ conectan la Google Cloud pasarela con la pasarela de par. Especifica el protocolo de intercambio de claves en Internet (IKE) que se va a usar para establecer el túnel. Puedes introducir tu propia clave IKE generada anteriormente o generar y copiar una nueva. Para obtener información general, consulta [Configurar IKE](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=es#configure_ike).

### ¿Por qué recomendamos esta tarea?

Una VPN de alta disponibilidad proporciona una conexión segura y de alta disponibilidad entre tu infraestructura y Google Cloud.

Antes de empezar
----------------

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).
*   Crea o vincula una cuenta de facturación en la tarea [Facturación](#checklist-section-4).
*   Configura tu jerarquía y asigna acceso en la tarea [Jerarquía y acceso](#checklist-section-5).
*   Configura tu red en la tarea [Redes de VPC](#checklist-section-8).

Recoge la siguiente información del administrador de tu red entre iguales:

*   _Nombre de la pasarela VPN de tu par:_ la pasarela a la que se conecta tu Cloud VPN.
*   _Dirección IP de la interfaz de peer 0:_ una dirección IP externa de tu gateway de red de peer.
*   _Dirección IP de la interfaz del peer 1:_ una segunda dirección externa. También puedes reutilizar la dirección IP 0 si tu red del peer solo tiene una dirección IP externa.
*   _Número de sistema autónomo (ASN) del peer:_ identificador único asignado al router de la red del peer.
*   _ASN de Cloud Router:_ identificador único que asignará a su Cloud Router.
*   _Claves de intercambio de claves en Internet (IKE):_ claves que usas para establecer dos túneles VPN con tu pasarela VPN de par. Si no tienes claves, puedes generarlas durante esta configuración y, a continuación, aplicarlas a tu gateway de peer.

### Configurar las conexiones

Para conectar tus redes de VPC a tus redes emparejadas, sigue estos pasos:

1.  Inicia sesión como usuario con el rol Administrador de la organización.
    
2.  En la lista desplegable **Seleccionar de**, situada en la parte superior de la página, selecciona tu organización.
    
3.  Ve a **Google Cloud Configuración: conectividad híbrida**.
    
    [Ir a Conectividad híbrida](https://console.cloud.google.com/cloud-setup/hybrid-connectivity?hl=es)
    
4.  Para ver los detalles de la tarea, haz lo siguiente:
    
    1.  Revisa la descripción general de la tarea y haz clic en **Iniciar conectividad híbrida**.
        
    2.  Haz clic en cada pestaña para obtener información sobre la conectividad híbrida y, a continuación, haz clic en **Continuar**.
        
    3.  Consulta qué puedes esperar en cada paso de la tarea y haz clic en **Continuar**.
        
    4.  Revisa la información de configuración de la pasarela entre iguales que debes recoger y haz clic en **Continuar**.
        
5.  En el área **Conexiones híbridas**, identifica las redes de VPC que quieras conectar en función de las necesidades de tu empresa.
    
6.  En la fila de la primera red que haya elegido, haga clic en **Configurar**.
    
7.  En el área **Resumen de la configuración**, lee la descripción y haz clic en **Siguiente**.
    
8.  En el área **Google Cloud Pasarela VPN de alta disponibilidad**, haga lo siguiente:
    
    1.  En el campo **Nombre de la puerta de enlace de Cloud VPN**, introduce hasta 60 caracteres con letras minúsculas, números y guiones.
        
    2.  En el área **Tipo de pila de IP interna del túnel VPN**, selecciona uno de los siguientes tipos de pila:
        
        *   **IPv4 e IPv6 (opción recomendada):** puede admitir tráfico IPv4 e IPv6. Te recomendamos que uses este ajuste si tienes previsto permitir el tráfico IPv6 en tu túnel.
        *   **IPv4** solo puede admitir tráfico IPv4.
        
        El tipo de pila determina el tipo de tráfico que se permite en el túnel entre tu red de VPC y tu red de peer. No puedes modificar el tipo de pila después de crear la pasarela. Para obtener información general, consulta lo siguiente:
        
        *   [Compatibilidad con IPv6](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=es#ipv6-support)
        *   [Tipos de pila y sesiones de BGP](https://docs.cloud.google.com/network-connectivity/docs/vpn/concepts/overview?hl=es#ha-vpn-stack-types)
    3.  Haz clic en **Siguiente**.
        
9.  En el área **Pasarela de VPN de par**, haga lo siguiente:
    
    1.  En el campo **Nombre de la pasarela VPN de la red remota**, introduce el nombre que te haya proporcionado el administrador de la red remota. Puedes introducir hasta 60 caracteres con letras minúsculas, números y guiones.
        
    2.  En el campo **Dirección IP de la interfaz de peer 0**, introduce la dirección IP externa de la interfaz de la pasarela de peer que te haya proporcionado el administrador de tu red de peer.
        
    3.  En el campo **Dirección IP de la interfaz de peer 1**, haga una de las siguientes acciones:
        
        *   Si tu pasarela de otro proveedor tiene una segunda interfaz, introduce su dirección IP.
        *   Si tu pasarela de otro proveedor solo tiene una interfaz, introduce la misma dirección que has introducido en **Dirección IP de la interfaz 0 de la pasarela de otro proveedor**.
        
        Para obtener información general, consulta [Configurar la pasarela de VPN de par](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=es).
        
    4.  Haz clic en **Siguiente**.
        
10.  En el área **Cloud Router**, haga lo siguiente:
     
     1.  En el campo **ASN de router de Cloud**, introduce el número de sistema autónomo que quieras asignar a tu router de Cloud, tal como te lo haya proporcionado el administrador de tu red de peer. Para obtener información general, consulta [Crear un Cloud Router](https://docs.cloud.google.com/network-connectivity/docs/router/how-to/create-router-vpc-network?hl=es#create_a).
         
     2.  En el campo **ASN del router de la red de pares**, introduce el número de sistema autónomo del router de tu red de pares, tal como te lo haya proporcionado el administrador de la red de pares.
         
11.  En el área **Túnel VPN 0**, haz lo siguiente:
     
     1.  En el campo **Nombre del túnel 0**, introduce hasta 60 caracteres con letras minúsculas, números y guiones.
         
     2.  En el área **Versión de IKE**, seleccione una de las siguientes opciones:
         
         *   **IKEv2 (recomendado):** admite tráfico IPv6.
         *   **IKEv1** usa este ajuste si no tienes previsto permitir el tráfico IPv6 en el túnel.
         
         Para obtener información general, consulta [Configurar túneles VPN](https://docs.cloud.google.com/network-connectivity/docs/vpn/how-to/configuring-peer-gateway?hl=es#configure_vpn_tunnels).
         
     3.  En el campo **Clave precompartida de IKE**, introduce la clave que usas en la configuración de tu gateway de peer, tal como te la ha proporcionado el administrador de tu red de peer. Si no tienes una clave, puedes hacer clic en **Generar y copiar** y, a continuación, dar la clave al administrador de tu red entre iguales.
         
12.  En el área **Túnel VPN 1**, repite el paso anterior para aplicar los ajustes del segundo túnel. Configura este túnel para que tenga redundancia y un mayor rendimiento.
     
13.  Haz clic en **Guardar**.
     
14.  Repite estos pasos con el resto de las redes de VPC que quieras conectar a tu red de peer.
     

Después de la implementación
----------------------------

Una vez que hayas [implementado la configuración Google Cloud Setup](#checklist-section-), sigue estos pasos para asegurarte de que la conexión de red se ha completado:

1.  Colabora con el administrador de tu red de pares para alinearla con tu configuración de conectividad híbrida. Después de la implementación, se proporcionan instrucciones específicas para tu red entre iguales, entre las que se incluyen las siguientes:
    
    *   Configuración del túnel.
    *   Configuración del cortafuegos.
    *   Ajustes de IKE.
2.  Valida las conexiones de red que has creado. Por ejemplo, puedes usar Network Intelligence Center para comprobar la conectividad entre redes. Para obtener más información, consulta la [descripción general de las pruebas de conectividad](https://docs.cloud.google.com/network-intelligence-center/docs/connectivity-tests/concepts/overview?hl=es).
    
3.  Si tu empresa necesita una conexión más sólida, usa Cloud Interconnect. Para obtener más información, consulta [Elegir un producto de conectividad de red](https://docs.cloud.google.com/network-connectivity/docs/how-to/choose-product?hl=es).
    

Siguientes pasos
----------------

[Implementa tu configuración](#checklist-section-), que incluye ajustes para tu jerarquía y acceso, registro, red y conectividad híbrida.

Implementar la configuración
----------------------------

### Desplegar o descargar

A medida que completes el proceso de configuración de Google Cloud , los ajustes de las siguientes tareas se compilarán en archivos de configuración de Terraform:

*   [Jerarquía y acceso](#checklist-section-5)
*   [Seguridad](#checklist-section-6)
*   [Registro y monitorización centralizados](#checklist-section-7)
*   [Redes de VPC](#checklist-section-8)
*   [Conectividad híbrida](#checklist-section-9)

Para aplicar los ajustes, revisa las selecciones y elige un método de implementación.

### ¿Quién lleva a cabo esta tarea?

Una persona incluida en el grupo `gcp-organization-admins@YOUR_DOMAIN` que has creado en la tarea [Usuarios y grupos](#checklist-section-2).

### Qué debes hacer en esta tarea

Implementa los archivos de configuración para aplicar los ajustes.

### ¿Por qué recomendamos esta tarea?

Para aplicar la configuración que has seleccionado, debes implementar los archivos de configuración.

### Antes de empezar

Debes completar las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).
*   Crea o vincula una cuenta de facturación en la tarea [Facturación](#checklist-section-4).
*   Configura tu jerarquía y asigna acceso en la tarea [Jerarquía y acceso](#checklist-section-5).

Te recomendamos que realices las siguientes tareas:

*   Refuerza tu posición de seguridad configurando servicios gratuitos en la tarea [Seguridad](#checklist-section-6).
*   Consolida los datos de registro en una sola ubicación y monitoriza todos los proyectos desde un único proyecto en la tarea [Registro y monitorización centralizados](#checklist-section-7).
*   Configura tu red inicial en la tarea [Redes de VPC](#checklist-section-8).
*   Conecta redes entre iguales en la tarea Google Cloud [Conectividad híbrida](#checklist-section-9).

### Revisar los detalles de la configuración

Para asegurarte de que has completado los ajustes de configuración, haz lo siguiente:

1.  Inicia sesión en la consola Google Cloud como usuario del grupo`gcp-organization-admins@YOUR_DOMAIN`que has creado en la tarea [Usuarios y grupos](#checklist-section-2).
    
2.  En la lista desplegable **Seleccionar de**, situada en la parte superior de la página, selecciona tu organización.
    
3.  Ve a **Google Cloud Configuración: desplegar o descargar**.
    
    [Ir a Desplegar o descargar](https://console.cloud.google.com/cloud-setup/download?hl=es)
    
4.  Revisa los ajustes de configuración que has seleccionado. Haga clic en cada una de las pestañas siguientes y revise su configuración:
    
    *   **Jerarquía de recursos y acceso**
    *   **Seguridad**
    *   **Almacenamiento de registros y monitorización**
    *   **Redes de VPC**
    *   **Conectividad híbrida**

### Desplegar la configuración

Ahora que has revisado los detalles de la configuración, utiliza una de las siguientes opciones:

*   **Despliega directamente desde la consola:** usa esta opción si no tienes un flujo de trabajo de despliegue de Terraform y quieres un método de despliegue sencillo. Solo puedes realizar una implementación con este método.
    
*   **Descargar y desplegar el archivo de Terraform:** usa esta opción si quieres automatizar la gestión de recursos mediante un flujo de trabajo de despliegue de Terraform. Puedes descargar e implementar varias veces con este método.
    

Implementa el servicio mediante una de las siguientes opciones:

### Implementar directamente

Si no tienes un flujo de trabajo de Terraform y quieres hacer una implementación sencilla y puntual, puedes hacerlo directamente desde la consola.

1.  Haz clic en **Desplegar directamente**.
    
2.  Espera varios minutos a que se complete el despliegue.
    
3.  Si la implementación falla, haz lo siguiente:
    
    1.  Para volver a intentar la implementación, haz clic en **Volver a intentar el proceso**.
    2.  Si la implementación falla tras varios intentos, puedes ponerte en contacto con un administrador para obtener ayuda. Para hacerlo, haz clic en **Contactar con el administrador de la organización**.

### Descargar e implementar

Si quieres iterar en tu implementación usando tu flujo de trabajo de implementación de Terraform, descarga e implementa archivos de configuración.

1.  Para descargar el archivo de configuración, haga clic en **Descargar como Terraform**.
    
2.  El paquete que descargues contendrá archivos de configuración de Terraform basados en los ajustes que hayas seleccionado en las siguientes tareas:
    
    *   **Jerarquía y acceso**
    *   **Seguridad**
    *   **Registro y monitorización centralizados**
    *   **Redes de VPC**
    *   **Conectividad híbrida**
    
    Si solo quieres implementar archivos de configuración que sean relevantes para tus responsabilidades, puedes evitar descargar archivos que no lo sean. Para ello, desmarca las casillas de los archivos de configuración que no necesites.
    
3.  Haz clic en **Descargar**. Se descargará en tu sistema de archivos local un paquete `terraform.tar.gz` que incluye los archivos seleccionados.
    
4.  Para ver los pasos detallados de la implementación, consulta [Implementar la base con Terraform descargado desde la consola](https://docs.cloud.google.com/docs/enterprise/deploy-foundation-using-terraform-from-console?hl=es).
    

### Siguientes pasos

Elige un [plan de asistencia](#checklist-section-10).

### Asistencia

En esta tarea, debes elegir un plan de asistencia que se ajuste a las necesidades de tu empresa.

### ¿Quién lleva a cabo esta tarea?

Una persona incluida en el grupo `gcp-organization-admins@YOUR_DOMAIN` que se creó en la tarea [Usuarios y grupos](#checklist-section-2).

### Qué debes hacer en esta tarea

Elige un plan de asistencia en función de las necesidades de tu empresa.

### ¿Por qué recomendamos esta tarea?

Con el plan Asistencia Premium, los expertos de Google Cloudte ofrecen ayuda para resolver rápidamente problemas críticos para la empresa.

### Elige una opción de asistencia.

Obtienes automáticamente el plan de asistencia básico gratuito, que incluye acceso a los siguientes recursos:

*   [Documentación](https://docs.cloud.google.com/docs?hl=es)
*   [Asistencia y debates de la comunidad](https://docs.cloud.google.com/support/docs/community?hl=es)
*   [Asistencia para problemas de facturación](https://docs.cloud.google.com/support/billing?hl=es)

En el caso de los clientes empresariales, lo más recomendable es suscribirse a nuestro plan de asistencia [Premium](https://docs.cloud.google.com/support/docs/premium?hl=es), ya que ofrece asistencia técnica personalizada de la mano de ingenieros de Google. Para comparar los planes de asistencia, consulta [Google Cloud Customer Care](https://docs.cloud.google.com/support?hl=es).

### Antes de empezar

Completa las siguientes tareas:

*   Crea un usuario superadministrador y tu organización en la tarea [Organización](#checklist-section-1).
*   Añade usuarios y crea grupos en la tarea [Usuarios y grupos](#checklist-section-2).
*   Asigna roles de gestión de identidades y accesos a los grupos en la tarea [Acceso administrativo](#checklist-section-3).

### Habilitar la asistencia

Identifica y selecciona una opción de asistencia.

1.  Revisa y selecciona un plan de asistencia. Para obtener más información, consulta [Google Cloud Atención al cliente](https://docs.cloud.google.com/support?hl=es#support-plans).
    
2.  Inicia sesión en la Google Cloud consola con un usuario del grupo `gcp-organization-admins@<your-domain>.com` que has creado en la tarea [Usuarios y grupos](#checklist-section-2).
    
3.  Ve a **Google Cloud Configuración: asistencia**.
    
    [Ir al servicio de asistencia](https://console.cloud.google.com/cloud-setup/support?hl=es)
    
4.  Revisa los detalles de la tarea y haz clic en **Ver ofertas de asistencia** para seleccionar una opción de asistencia.
    
5.  Después de configurar la opción de asistencia, vuelve a la página **Google Cloud Configuración: asistencia** y haz clic en **Marcar tarea como completada**.
    

### Siguientes pasos

Ahora que has completado la Google Cloud configuración, puedes ampliarla, implementar soluciones prediseñadas y migrar tus flujos de trabajo. Para obtener más información, consulta el artículo [Ampliar la configuración inicial y empezar a crear](https://docs.cloud.google.com/docs/enterprise/manage-foundation?hl=es).

A menos que se indique lo contrario, el contenido de esta página está sujeto a la [licencia Reconocimiento 4.0 de Creative Commons](https://creativecommons.org/licenses/by/4.0/) y las muestras de código están sujetas a la [licencia Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0). Para obtener más información, consulta las [políticas del sitio web de Google Developers](https://developers.google.com/site-policies?hl=es). Java es una marca registrada de Oracle o sus afiliados.

Última actualización: 2026-01-14 (UTC).
