# Configurar alertas y monitorización de cuotas &nbsp;|&nbsp; Cloud Quotas &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/quotas/monitor?hl=es

Puedes configurar alertas de uso y monitorización para tus cuotas. El panel de control Cuotas de Cloud se integra con [Cloud Monitoring](https://docs.cloud.google.com/monitoring/docs/monitoring-overview?hl=es) para mayor comodidad. En este documento se describe cómo configurar alertas, crear gráficos y obtener más información sobre el uso de Cloud Monitoring para Cloud Quotas.

Configurar alertas de uso de cuota básicas
------------------------------------------

Puede configurar alertas de cuota en la página **Gestión de identidades y accesos y administración \> Cuotas y límites del sistema** para recibir notificaciones de eventos de cuota. Por ejemplo, puedes configurar una alerta para que te avise cuando el uso de tu cuota alcance un porcentaje del valor máximo. Esta función solo se admite en las cuotas a nivel de proyecto.

Para configurar una alerta para una cuota o un límite del sistema específicos, haz lo siguiente:

1.  Asegúrate de que tienes [permisos para crear alertas](https://docs.cloud.google.com/docs/quotas/permissions?hl=es#permissions_create_alert).
    
2.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
3.  En la columna situada más a la derecha de la tabla, haga clic en **Más acciones** y, a continuación, seleccione **Crear alerta de uso**. Se abrirá el panel **Plantillas de políticas de alertas**.
    
4.  En **Configurar notificaciones**, selecciona el canal de notificación. El canal de notificación es la forma en que recibes la alerta, por ejemplo, por correo electrónico, SMS o Pub/Sub.
    
5.  Haz clic en **Crear**.
    

Crear gráficos
--------------

El explorador de métricas de Cloud Monitoring te permite crear gráficos para ver métricas. Puede usarlo para ver métricas relacionadas con Cloud Quotas.

Para ver las métricas de un recurso monitorizado con el explorador de métricas, haz lo siguiente:

1.  En la Google Cloud consola, ve a la página  **Explorador de métricas**:
    
    [Ve al **explorador de métricas**](https://console.cloud.google.com/monitoring/metrics-explorer?hl=es).
    
    Si usas la barra de búsqueda para encontrar esta página, selecciona el resultado cuya sección sea **Monitorización**.
    
2.  En la barra de herramientas de la consola Google Cloud , selecciona tu proyecto Google Cloud . En las configuraciones de [App Hub](https://docs.cloud.google.com/app-hub/docs/overview?hl=es), selecciona el proyecto host de App Hub o el proyecto de gestión de la carpeta habilitada para la aplicación.
3.  En el elemento **Métrica**, despliega el menú **Seleccionar una métrica**, introduce `quota usage` en la barra de filtros y, a continuación, usa los submenús para seleccionar un tipo de recurso y una métrica específicos:
    1.  En el menú **Recursos activos**, selecciona **Cuota de consumidor**.
    2.  En el menú **Categorías de métricas activas**, selecciona **Cuota**.
    3.  En el menú **Métricas activas**, seleccione una métrica de la lista. Para mostrar las métricas activas e inactivas, haga clic en **Activo** para borrar el filtro del menú **Seleccionar una métrica**.
    4.  Haz clic en **Aplicar**.
4.  Para añadir filtros que quiten series temporales de los resultados de la consulta, usa el [elemento **Filter**](https://docs.cloud.google.com/monitoring/charts/metrics-selector?hl=es#filter-option).
    
5.  Para combinar series temporales, usa los menús del [**elemento "Agregación"**](https://docs.cloud.google.com/monitoring/charts/metrics-selector?hl=es#select_display). Por ejemplo, para mostrar la utilización de la CPU de tus VMs en función de su zona, selecciona **Media** en el primer menú y **zona** en el segundo.
    
    Todas las series temporales se muestran cuando el primer menú del elemento **Agregación** se define como **Sin agregar**. Los ajustes predeterminados del elemento **Agregación** se determinan en función del tipo de métrica que hayas seleccionado.
    
6.  Para las cuotas y otras métricas que registran una muestra al día, haga lo siguiente:
    1.  En el panel **Visualización**, seleccione **Gráfico de barras apiladas** en **Tipo de widget**.
    2.  Establece un periodo de al menos una semana.

Una vez que hayas encontrado la información de uso de cuotas que buscabas, otra utilidad de Cloud Monitoring es crear paneles de control y alertas personalizados. Para obtener más información, consulta el artículo [Aprovechar al máximo Cloud Monitoring](#do-more).

Comprobar la compatibilidad con métricas de cuota
-------------------------------------------------

No todos los servicios admiten métricas de cuotas en Cloud Monitoring. Para ver las métricas de cuotas correspondientes a los servicios que las admiten, selecciona **Cuota de consumidor** como tipo de recurso al crear un gráfico o al crear una política de alertas. No se muestran los servicios que no admiten métricas de cuotas.

*   Estos son algunos de los servicios más comunes que admiten métricas: Compute Engine, Dataflow, Spanner, Pub/Sub, Cloud Vision, Speech-to-Text, Cloud Monitoring y Cloud Logging.
    
*   Algunos de los servicios habituales que no admiten las métricas de cuotas son App Engine y Cloud SQL.
    

Obtener nombres de métricas
---------------------------

Las cuotas y los límites del sistema tienen dos tipos de nombres: nombres visibles y nombres de métrica. Los nombres visibles tienen espacios y mayúsculas que facilitan la lectura a los usuarios. Es más probable que los nombres de las métricas estén en minúsculas y delimitados por guiones bajos en lugar de espacios. El formato exacto depende del servicio.

En las siguientes instrucciones se muestra cómo obtener los nombres de las métricas de cuota y de límite del sistema mediante la consola de Google Cloud o la CLI de gcloud.

### Consola

1.  En la Google Cloud consola, ve a la página **IAM y administración \> Cuotas y límites del sistema**:
    
    [Ir a Cuotas y límites del sistema](https://console.cloud.google.com/iam-admin/quotas?hl=es)
    
    En la tabla de esta página se muestran las cuotas y los límites del sistema que tienen un uso o valores ajustados, así como una entrada de referencia para otras cuotas. La entrada de referencia tiene la palabra "default" entre paréntesis al final de la lista en la columna **Nombre**. Por ejemplo, `SetIAMPolicy requests per minute per region (default)` es la entrada de referencia de la cuota `SetIamPolicyRequestsPerMinutePerProject`.
    
2.  Si no ve la columna **Métrica**, siga estos pasos.
    
    1.  Haz clic en **Opciones de visualización de columnas**.
    2.  Selecciona **Métrica**.
    3.  Haz clic en **Aceptar**. La columna **Métrica** aparece en la tabla.

En la columna **Métrica** se muestran los nombres de las métricas. Para filtrar los resultados, introduce un nombre o un valor de propiedad en el campo situado junto a **Filtrar**.

### gcloud

Para obtener los nombres de las métricas de un Google Cloud servicio mediante la CLI de gcloud, ejecuta el comando `quotas info list`. Para omitir las líneas que no incluyan nombres de métricas, envía la salida a un comando como `grep` con `metric:` como término de búsqueda o usa la marca [`--format`](https://docs.cloud.google.com/sdk/gcloud/reference?hl=es#--format) de gcloud CLI:

gcloud beta quotas info list --project=PROJECT_ID_OR_NUMBER \
        --service=SERVICE_NAME --format="value(metric)"

Haz los cambios siguientes:

*   `PROJECT_ID_OR_NUMBER`: el ID o el número del proyecto.
*   `SERVICE_NAME`: el nombre del servicio cuyas métricas de cuota quieres ver. Por ejemplo, el nombre del servicio de Compute Engine es `compute.googleapis.com`. Incluye la parte `googleapis.com` del nombre del servicio.

Sacar más partido a Cloud Monitoring
------------------------------------

Las herramientas de [Cloud Monitoring](https://docs.cloud.google.com/monitoring/docs/monitoring-overview?hl=es) te permiten monitorizar el uso, los valores y los errores de las cuotas en profundidad. Estas métricas sirven para crear paneles de control y alertas personalizados. Por ejemplo, puedes ver el uso de la cuota a lo largo del tiempo o recibir una alerta cuando te acerques al valor de la cuota.

Cloud Monitoring admite una gama amplia de métricas, que podrás combinar con filtros y agregaciones para obtener perspectivas nuevas y útiles del uso que haces de las cuotas. Por ejemplo, puedes combinar una métrica para asignar un uso de cuota de asignación con un filtro `quota_metric` en nombres de TPU de Cloud.

Los precios de Cloud Monitoring se describen en el documento [Precios de Observabilidad de Google Cloud](https://cloud.google.com/stackdriver/pricing?hl=es).

La [documentación de Cloud Monitoring](https://docs.cloud.google.com/monitoring/docs/monitoring-overview?hl=es) es muy extensa, así que aquí tienes algunos documentos para empezar:

*   [Crear gráficos:](https://docs.cloud.google.com/monitoring/charts?hl=es) guía completa para crear gráficos y tablas, y añadirlos a un panel de control personalizado.
*   [Introducción a las alertas:](https://docs.cloud.google.com/monitoring/alerts?hl=es) una descripción general de cómo funcionan las alertas y de las opciones que tienes para crear una política de alertas.
*   [Gestionar políticas de alertas:](https://docs.cloud.google.com/monitoring/alerts/manage-alerts?hl=es) guía sobre varias tareas de gestión de las políticas de alertas que ya tienes. Por ejemplo, ver, editar o eliminar una política, o añadirla a un panel de control.
*   [Usar métricas de cuotas:](https://docs.cloud.google.com/monitoring/alerts/using-quota-metrics?hl=es) un documento detallado dedicado a los casos prácticos de las cuotas, con ejemplos que abarcan temas como la creación de alertas para errores `quota/exceeded`.
*   [Google Cloud Guía de métricas:](https://docs.cloud.google.com/monitoring/api/metrics_gcp_p_z?hl=es#gcp-serviceruntime) documento de referencia sobre las métricas. En la sección `serviceruntime` se indican las métricas de cuotas que se usan para la monitorización.
