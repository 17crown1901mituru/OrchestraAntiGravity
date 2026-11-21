# Ver y gestionar cuotas &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/view-manage?hl=es

En este documento se describe cómo puedes ver los valores de las cuotas en la consola deGoogle Cloud . También puedes gestionar las cuotas desde laGoogle Cloud consola, la API Cloud Quotas y la CLI de Google Cloud (gcloud CLI).

Limitaciones
------------

Cloud Quotas tiene las siguientes limitaciones:

*   En la mayoría de los casos, los ajustes de _aumento_ de cuota deben hacerse a [nivel de proyecto](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=es). [Solo un número limitado de productos admite ajustes de aumento de cuota a nivel de organización.](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=es#organizations) Para saber si un producto admite ajustes de aumento de cuota a nivel de organización, consulta la documentación de ese producto. Google Cloud
    
*   Puedes solicitar ajustes para _reducir_ las cuotas a nivel de proyecto, de organización y de [carpeta](https://docs.cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy?hl=es#folders).
    

Antes de empezar
----------------

Para llevar a cabo las tareas que se describen en este documento, se necesitan roles de Gestión de Identidades y Accesos (IAM).

### Roles obligatorios

Para obtener los permisos que necesitas para solicitar ajustes de cuota, pide a tu administrador que te conceda los siguientes roles de gestión de identidades y accesos en el proyecto, la carpeta o la organización:

*   Para ver las cuotas, sigue estos pasos: [Lector de cuota](https://docs.cloud.google.com/iam/docs/roles-permissions/servicemanagement?hl=es#servicemanagement.quotaViewer) (`roles/servicemanagement.quotaViewer`)
*   Para solicitar ajustes de cuota, sigue estos pasos: [Administrador de cuota](https://docs.cloud.google.com/iam/docs/roles-permissions/servicemanagement?hl=es#servicemanagement.quotaAdmin) (`roles/servicemanagement.quotaAdmin`)

Para obtener más información sobre cómo conceder roles, consulta el artículo [Gestionar acceso a proyectos, carpetas y organizaciones](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=es).

Estos roles predefinidos contienen los permisos necesarios para solicitar ajustes de cuota. Para ver los permisos exactos que se necesitan, despliega la sección **Permisos necesarios**:

#### Permisos obligatorios

Para solicitar ajustes de cuota, se necesitan los siguientes permisos:

*   Para solicitar ajustes de cuota, sigue estos pasos: `` `serviceusage.quotas.update` ``

También puedes obtener estos permisos con [roles personalizados](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=es) u otros [roles predefinidos](https://docs.cloud.google.com/iam/docs/roles-overview?hl=es#predefined).

Ver cuotas en la Google Cloud consola
-------------------------------------

Puedes ver los valores de tu cuota actual en dos zonas diferentes de la [Google Cloud consola](https://console.cloud.google.com/?hl=es):

*   La página [**Cuotas y límites del sistema**](https://console.cloud.google.com/quotas?project=_&hl=es), que incluye todo el uso y los valores de las cuotas de tu proyecto.
    
*   El [**Google Cloud panel de control de la API de la consola**](https://console.cloud.google.com/apis/dashboard?hl=es), que incluye la información de la cuota de una API concreta, incluido el uso de recursos a lo largo del tiempo.
    

### Ver las cuotas de un proyecto

En la página **IAM y administración \> Cuotas y límites del sistema**, se muestra una tabla con columnas configurables. Las columnas **Servicio** y **Cuota** proporcionan información general sobre la cuota que se describe. Por ejemplo, el **servicio** podría ser la **API Cloud Logging** y la **cuota**, las **solicitudes de ingesta de registros por minuto**. El campo **Quota** también describe cómo se evalúa la cuota. Las cuotas de frecuencia se evalúan por minuto, por 100 segundos o por día. Las cuotas que no incluyen ninguna de estas instrucciones son cuotas de asignación.

Para ver el uso y los valores de las cuotas de todos los recursos de tu proyecto, sigue estos pasos:

### Consola

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
2.  Para centrarte en la información que necesitas, filtra y ordena la lista de la siguiente manera:
    
    *   Para filtrar la lista, introduzca las propiedades y los valores en el campo. Por ejemplo, para ver las cuotas de la API de BigQuery, selecciona **Servicio** y, a continuación, **API de BigQuery**.
        
    *   Para ordenar la lista, haz clic en el encabezado de la columna por la que quieras ordenar. Por ejemplo, para ordenar alfabéticamente por el nombre de la cuota, haz clic en el encabezado de la columna **Quota**.
        
    
    De forma predeterminada, la lista se ordena de manera que muestra en primera posición las cuotas que más se han utilizado para que te sea más fácil ver qué cuotas están cerca de superarse.
    
3.  Opcional: Para inspeccionar el uso de cuota agregado a nivel de carpeta y de organización, selecciona la organización o la carpeta en el selector de recursos.
    

Puedes consultar más información sobre la cuota en las columnas **Métrica**, **Nombre del límite** y **Recurso monitorizado**. Estas columnas proporcionan la información detallada necesaria para trazar una cuota.

Para obtener información sobre el uso de tu cuota, consulta las columnas **Porcentaje de uso actual** y **Uso actual**. El uso actual se calcula según los siguientes criterios:

*   En el caso de las cuotas de tarifas por minuto:
    
    *   El _promedio_ de uso por minuto de los últimos 10 minutos.
*   En el caso de las cuotas de frecuencia diaria:
    
    *   El uso _total_ realizado hasta el momento en el día actual, según la hora estándar del Pacífico.
*   En el caso de las cuotas de asignación:
    
    *   El valor más reciente. Por ejemplo, este número puede mostrar el número de balanceadores de carga que utiliza tu proyecto.
*   En el caso de las cuotas simultáneas:
    
    *   El valor más reciente. Por ejemplo, este número puede mostrar el número de insert\_operations en curso de un servicio determinado.

Para obtener más información sobre las cuotas de un servicio concreto, consulta la documentación de ese servicio. Por ejemplo, la cuota de Compute Engine se describe en [Cuotas de recursos](https://docs.cloud.google.com/compute/quotas?hl=es).

### Ver y exportar el uso de la cuota a lo largo del tiempo

Puedes ver el uso de la cuota a lo largo del tiempo con gráficos. La página Cuotas y límites del sistema de la consola incluye gráficos de cada cuota o límite del sistema que se muestra en la tabla Cuotas y límites del sistema. Después de ver un gráfico, puedes exportarlo. Para ver y exportar un gráfico, sigue estos pasos:

### Consola

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
2.  Busca la cuota o el límite del sistema que te interese. En la entrada de esta cuota o límite del sistema, haz clic en **Mostrar gráfico de uso**. Se abrirá la vista de gráfico.
    
3.  En la vista de gráfico, define el periodo. El valor predeterminado es 7 días. Para cambiarlo, haz clic en el menú del periodo y selecciona el que quieras.
    
4.  Para exportar el gráfico, haz clic en **Más opciones del gráfico** y, a continuación, selecciona **Descargar** \> **Descargar PNG**. El gráfico se descarga como un archivo PNG.
    

Para sacar más partido a los gráficos, consulta [Configurar alertas y monitorización de cuotas](https://docs.cloud.google.com/docs/quotas/set-up-quota-alerts?hl=es).

### Ver cuotas específicas de una API

Para ver información detallada sobre las cuotas de una API concreta, incluido el uso a lo largo del tiempo, visita la página de cuotas de la API en la Google Cloud consola. En función de la API, estos límites pueden incluir _solicitudes por día_, _solicitudes por minuto_ y _solicitudes por minuto por usuario_. Algunas API definen límites muy bajos hasta que [la facturación se habilite en tu proyecto](https://docs.cloud.google.com/billing/docs/how-to/modify-project?hl=es#enable_billing_for_a_project).

Si no hay ningún valor para una cuota concreta, se muestra `Unlimited`.

Puedes consultar los límites actuales de una API en concreto en dos áreas diferentes de la Google Cloud consola: en la página **Cuotas y límites del sistema** Google Cloud de la consola o en la página **APIs y servicios**. Usa la vista de consola que prefieras:

### Consola

*   Para ver las cuotas específicas de una API en la página **Cuotas y límites del sistema** mediante un filtro, sigue estos pasos:
    
    1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
        
        [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
        
    2.  Haga clic en **Filtrar** y seleccione **Servicio** en el menú que aparece.
        
    3.  Selecciona o introduce el nombre del servicio que quieras ver.
        
        En la página se muestra información sobre las cuotas y los límites del sistema de la API especificada. Los números que aparecen en la columna `Value` son los valores de cuota predeterminados, a menos que hayas ajustado la cuota.
        
*   Para ver las cuotas específicas de una API en la página **APIs y servicios**, sigue estos pasos:
    
    1.  Ve a la página **APIs & Services** (APIs y servicios):
        
        [Ir a APIs y servicios](https://console.cloud.google.com/apis/dashboard?project=_&hl=es)
        
    2.  Selecciona el proyecto.
        
    3.  Haz clic en el nombre de la API del servicio que quieras ver.
        
    4.  Haz clic en la pestaña **Cuotas y límites del sistema**.
        
        En la página se muestra información sobre las cuotas y los límites del sistema de la API especificada. Los números que aparecen en la columna `Value` son los valores de cuota predeterminados, a menos que hayas ajustado la cuota.
        

Para ver el uso a lo largo del tiempo, haz clic en **Mostrar gráfico de uso**.

### Filtrar cuotas

Para filtrar la lista de cuotas por propiedades específicas, sigue estos pasos:

### Consola

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
2.  Haz clic en **Filtrar** para consultar tus cuotas por un nombre o valor de propiedad específico.
    

Gestionar las cuotas con la consola
-----------------------------------

En función del uso que hagas de los recursos, puedes ajustar al alza o a la baja los valores de cuota de tu proyecto. En esta sección se describe cómo cambiar los valores de cuota. Para ver los valores, en la columna **Detalles**, haga clic en **Todas las cuotas**.

### Solicitar un ajuste de cuota

Las solicitudes de ajuste de cuotas de Cloud están sujetas a revisión. Si tu solicitud de ajuste de cuota requiere una revisión, recibirás un correo en el que se te confirmará que la hemos recibido. Si necesitas más ayuda, responde al correo. Después de revisar tu solicitud, recibirás una notificación por correo electrónico en la que se indicará si se ha aprobado.

### Consola

Para ajustar un valor de cuota, sigue estos pasos:

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
    Si se te pide que selecciones un proyecto, elige el que quieras ajustar.
2.  Busca el valor de la cuota que quieras actualizar en la columna **Quota** y selecciona la casilla situada junto a la cuota que quieras actualizar.
    
    Si es necesario, usa el cuadro de búsqueda **Filtrar** para buscar tu cuota.
    
3.  Haga clic en **Editar** y aparecerá el cuadro de diálogo **Cambios en la cuota**.
    
4.  En el campo **Valor nuevo**, introduce el valor de cuota que quieras. Algunos valores de cuota tienen una unidad de medida. Si es el caso, selecciona la unidad que quieras en la lista adyacente. Haz clic en **Listo**.
    
    Opcional: Si ves una casilla con el texto `I understand that this request will remove any overrides`, significa que, si ajustas el valor de la cuota a un número igual o superior al predeterminado, se eliminará la [anulación de cuota](https://docs.cloud.google.com/docs/quotas/view-manage?hl=es#create_override) anterior. Si es lo que quieres, selecciona la casilla y continúa.
    
5.  Para aumentar el valor de la cuota por encima del número indicado en el cuadro de diálogo, seleccione **Solicitar una cuota superior**.
    
    1.  En el formulario **Cambios en la cuota**, introduce el valor de cuota actualizado que quieras en el campo **Valor nuevo**. Si aparece el campo **Descripción de la solicitud**, introduce una descripción. Por último, haz clic en **Listo**.
        
    2.  **Si aparece el botón **Siguiente**, haz clic en él y rellena tus datos de contacto en la pantalla siguiente.**
        
6.  Haz clic en **Enviar solicitud**.
    

Si no puedes solicitar un ajuste desde la consola, solicita el aumento a [Customer Care de Cloud](https://docs.cloud.google.com/support?hl=es).

Para obtener más información sobre cómo funciona el proceso de aumento de cuota, consulta [Acerca de los ajustes de cuota](https://docs.cloud.google.com/docs/quotas/overview?hl=es#about_increase_requests).

#### Organizar solicitudes por lotes para obtener valores de cuota más altos

Puede agrupar solicitudes para aumentar la cuota marcando la casilla situada junto a cada cuota que quiera incluir. Sin embargo, agrupar solicitudes puede aumentar el tiempo que tarda Google Cloud en revisar tu solicitud.

Para reducir el tiempo de revisión, agrupa las solicitudes de ajuste de cuota por producto y área. Por ejemplo, si quieres solicitar ajustes en las cuotas de redes y de máquinas virtuales de Compute Engine, crea una solicitud para las cuotas de redes y otra para las cuotas de máquinas virtuales de Compute Engine.

### Ver solicitudes de aumento de cuota

Puedes ver las solicitudes de aumento de cuota pendientes y anteriores en laGoogle Cloud consola.

Para ver las solicitudes de aumento de cuota pendientes, sigue estos pasos:

### Consola

1.  Asegúrate de que tienes [permiso para ver las solicitudes de aumento de cuota](https://docs.cloud.google.com/docs/quotas/permissions?hl=es#permissions_increase).
    
2.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
3.  Aparece un icono **Pendiente** junto a los valores de cuota que están pendientes de una decisión sobre las solicitudes de aumento enviadas anteriormente. Haz clic en **Pendiente** para ver los detalles de las solicitudes pendientes.
    

Las solicitudes pendientes también se muestran en el formulario **Cambios de cuota** cuando se va a enviar un ajuste de cuota.

Para ver todas las solicitudes de aumento de cuota, incluidas las pendientes y las anteriores, sigue estos pasos:

### Consola

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
2.  Haz clic en la pestaña **Aumentar solicitudes**.
    
3.  Haz clic en **Filtrar** para consultar tus solicitudes de aumento de cuota por una propiedad específica.
    

Cuando se selecciona una organización o una carpeta, la página muestra todas las solicitudes de aumento de cuota de todos los proyectos de la organización o la carpeta.

### Crear una anulación de cuota

Para restringir el uso de un recurso concreto, crea una _anulación de cuota_ cambiando el valor de la cuota a un valor inferior al valor predeterminado. A veces, la creación de una anulación de cuota se denomina _limitación del uso_.

Para crear una anulación de cuota, sigue los pasos para [actualizar un valor de cuota](#requesting_higher_quota).

No todos los servicios pueden anular las cuotas. Para obtener información específica de un servicio, consulta la documentación sobre cuotas y límites del sistema de tu servicio.

### Restablecer un valor de cuota

Para restablecer el valor de la cuota después de aplicar una anulación, sigue estos pasos:

### Consola

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
2.  Introduce texto en el cuadro de búsqueda **Filtrar** para buscar tu cuota. Selecciona **Tiene anulaciones: True** para mostrar solo las cuotas que tienen una anulación.
    
3.  En la página Cuotas, **Anular** aparece junto a los límites de cuota que se han anulado. Haz clic en **Anular** para ver los detalles de esta cuota y una opción para restablecer el valor.
    
4.  Para confirmar que el restablecimiento elimina las anulaciones, marca la casilla situada antes de **Restablecer valor** y, a continuación, haz clic en **Restablecer valor** para restablecer la cuota.
    

Para restablecer una cuota manualmente, sigue los mismos pasos que para [solicitar un aumento de cuota](#requesting_higher_quota). Asegúrate de que el **Valor nuevo** sea igual al **Valor predeterminado** que se muestra debajo del nombre de la cuota en el formulario **Cambios en la cuota**.

Gestionar cuotas con la API Cloud Quotas
----------------------------------------

Puedes usar la API Cloud Quotas para obtener información sobre las cuotas actuales y definir preferencias de cuota para Google Cloud APIs y servicios. Para obtener más información, consulta lo siguiente:

*   Para obtener una descripción general, consulta el artículo [Descripción general de la API Cloud Quotas](https://docs.cloud.google.com/docs/quotas/api-overview?hl=es).
*   Para obtener instrucciones sobre cómo configurar tu entorno de desarrollo, consulta el artículo [Configurar la API Cloud Quotas](https://docs.cloud.google.com/docs/quotas/development-environment?hl=es).
*   Para ver ejemplos de cómo usar la API Cloud Quotas para ajustar cuotas y automatizar ajustes de cuotas en tus Google Cloud proyectos, carpetas u organizaciones, consulta [Implementar casos prácticos habituales](https://docs.cloud.google.com/docs/quotas/implement-common-use-cases?hl=es).

Gestionar cuotas con gcloud CLI
-------------------------------

Para usar la CLI de gcloud, asegúrate de haber [instalado](https://docs.cloud.google.com/sdk/docs/install?hl=es) y [inicializado](https://docs.cloud.google.com/sdk/docs/initializing?hl=es) la versión más reciente de la CLI de gcloud, incluido el componente de [comandos beta](https://docs.cloud.google.com/sdk/docs/components?hl=es#alpha_and_beta_components). Si usas Cloud Shell para interactuar con Google Cloud, la CLI de gcloud ya está instalada.

Consulta las siguientes secciones para obtener más información:

*   Por ejemplo, para los comandos `gcloud beta quotas info` y `gcloud beta quotas preferences`, consulta [Usar gcloud CLI para ver y gestionar cuotas](https://docs.cloud.google.com/docs/quotas/gcloud-cli-examples?hl=es).
    
*   Para ver una lista completa de los comandos y las marcas de `gcloud beta quotas`, consulta la sección [gcloud beta quotas](https://docs.cloud.google.com/sdk/gcloud/reference/beta/quotas?hl=es) de la referencia de Google Cloud CLI.
    
    *   [gcloud beta quotas info](https://docs.cloud.google.com/sdk/gcloud/reference/beta/quotas/info?hl=es)
    *   [Preferencias de cuotas de gcloud beta](https://docs.cloud.google.com/sdk/gcloud/reference/beta/quotas/preferences?hl=es)

Solicitar cuota de proyecto
---------------------------

Para obtener más información sobre cómo solicitar _cuotas de proyectos_ adicionales, consulta el artículo de asistencia [Solicitudes de cuotas de proyectos](https://support.google.com/cloud/answer/6330231?hl=es).

Siguientes pasos
----------------

*   Configura el [ajustador de cuotas](https://docs.cloud.google.com/docs/quotas/quota-adjuster?hl=es) para monitorizar y enviar solicitudes de ajuste de cuotas por ti.
*   Usa la [API Cloud Quotas](https://docs.cloud.google.com/docs/quotas/api-overview?hl=es) para gestionar tus cuotas a nivel de proyecto.
*   Controla el acceso mediante [Gestión de identidades y accesos](https://docs.cloud.google.com/docs/quotas/permissions?hl=es) o [Controles de Servicio de VPC](https://docs.cloud.google.com/docs/quotas/configure-vpc-service-controls?hl=es).
*   Configure su [proyecto de cuota](https://docs.cloud.google.com/docs/quotas/quota-project?hl=es).
*   [Monitoriza](https://docs.cloud.google.com/docs/quotas/monitor?hl=es) y [soluciona problemas](https://docs.cloud.google.com/docs/quotas/troubleshoot?hl=es) de cuotas.
