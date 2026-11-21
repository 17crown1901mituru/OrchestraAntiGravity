# Obtener recomendaciones de descuentos por compromiso de uso (CUD) &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/cuds-recommender

Las recomendaciones de [descuentos por uso continuado (CUD)](https://docs.cloud.google.com/docs/cuds?hl=es) te ayudan a optimizar los costes de los recursos de los proyectos de tu cuenta de facturación de Cloud. Las recomendaciones de CUD se generan automáticamente mediante una fórmula que analiza las métricas de uso históricas y recientes recogidas por la facturación de Cloud, e incluye el uso cubierto por los compromisos actuales. Puedes aplicar estas recomendaciones para comprar compromisos adicionales y optimizar aún más tus Google Cloud costes.

Las recomendaciones están disponibles para un [subconjunto de compromisos basados en recursos](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#supported-resource-types-recommendation) y un [subconjunto de compromisos basados en el gasto](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#supported-spend-types-recommendation) para [productos aptos](https://docs.cloud.google.com/docs/cuds?hl=es#spend_based_commitments) , incluidos los [compromisos flexibles de Compute](https://docs.cloud.google.com/compute/docs/instances/committed-use-discounts-overview?hl=es#spend_based).

Consulta las guías de esta página para obtener información sobre las siguientes tareas:

*   [Acerca de las recomendaciones de compromiso](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#understanding-recommendations)
*   [Definir permisos para acceder a las recomendaciones](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#permissions)
*   [Ver recomendaciones](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#viewing-recommendations)
*   [Interpretar el resumen de recomendaciones](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#understanding-summary-cards)
*   [Simular escenarios de ahorro con los descuentos por compromiso de uso basados en la inversión](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#simulate-scenarios)
*   [Aplicar recomendaciones para comprar compromisos adicionales](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#purchasing-recommendations)
*   [Rechazar recomendaciones](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#dismissing-recommendations)
*   [Configurar los ajustes de recomendaciones](https://docs.cloud.google.com/docs/cuds-recommender?hl=es#configure-recommendation)

Para obtener más información sobre el servicio Recommender, consulta la [descripción general de Recommender](https://docs.cloud.google.com/recommender/docs/overview?hl=es).

Información sobre las recomendaciones de compromisos
----------------------------------------------------

Las recomendaciones de descuentos por uso confirmado te permiten identificar patrones de gasto y uso en tus Google Cloud proyectos. Tus patrones de gasto generan recomendaciones de [compromisos basados en el gasto](https://docs.cloud.google.com/docs/cuds-spend-based?hl=es), incluidos los [compromisos flexibles de Compute](https://docs.cloud.google.com/compute/docs/instances/committed-use-discounts-overview?hl=es#spend_based), y tus patrones de uso en Compute Engine generan recomendaciones de [compromisos basados en recursos](https://docs.cloud.google.com/docs/cuds?hl=es#resource_based_commitments). Las recomendaciones de compromisos basados en recursos también tienen en cuenta el uso de [tipos de máquinas personalizadas en Compute Engine](https://docs.cloud.google.com/compute/docs/instances/creating-instance-with-custom-machine-type?hl=es). Comprar los compromisos recomendados te ayuda a optimizar losGoogle Cloud costes.

Permisos necesarios para ver y modificar recomendaciones
--------------------------------------------------------

En función de tus necesidades, pide a tu administrador que te asigne los siguientes roles predefinidos de gestión de identidades y accesos:

*   Si tu cuenta de Facturación de Cloud tiene habilitada la [compartición de descuentos](https://docs.cloud.google.com/compute/docs/committed-use-discounts/share-resource-cuds-across-projects?hl=es#turning_on_committed_use_discount_sharing) para los compromisos basados en recursos, debes tener uno de estos roles en la cuenta de Facturación de Cloud:
    
    *   Para ver solo las recomendaciones, asigna el rol **Lector de cuentas de facturación** (`roles/billing.viewer`).
    *   Para ver y modificar recomendaciones, asigna el rol **Administrador de cuenta de facturación** (`roles/billing.admin`).
    
    [Consulta cómo asignar estos roles para gestionar el acceso a una cuenta de facturación de Cloud](https://docs.cloud.google.com/billing/docs/how-to/grant-access-to-billing?hl=es).
    
*   Si tu cuenta de facturación de Cloud _no_ tiene [uso compartido de descuentos](https://docs.cloud.google.com/compute/docs/committed-use-discounts/share-resource-cuds-across-projects?hl=es#turning_on_committed_use_discount_sharing) para compromisos basados en recursos, necesitas uno de estos roles en cada proyecto asociado a tu cuenta de facturación de Cloud que haya comprado descuentos por compromiso de uso:
    
    *   Para ver solo las recomendaciones, asigna el rol **Lector** (`roles/viewer`) a los proyectos.
    *   Para ver y modificar recomendaciones, asigna el rol **Propietario** (`roles/owner`) o **Editor** (`roles/editor`) en los proyectos.
    
    [Consulta cómo asignar estos roles para gestionar el acceso a los proyectos](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=es).
    

Si usas roles personalizados, actualízalos para que incluyan los siguientes permisos individuales:

#### (Haz clic para desplegar) Permisos necesarios para los roles personalizados

### Permisos para simular escenarios de ahorro por CUD

*   Para simular situaciones basadas en precios según catálogo, necesitas lo siguiente: `billing.cudrecommendations.generateDefaultPriceSavingRecommendation`
*   Si tienes un contrato de precios personalizado, debes `billing.cudrecommendations.generateCustomPriceSavingRecommendation`simular escenarios en función de tus precios personalizados.

### Permisos para ver recomendaciones

Para ver las recomendaciones de DUC _basados en la inversión_, siga estos pasos:

*   `recommender.spendBasedCommitmentRecommendations.get`
*   `recommender.spendBasedCommitmentRecommendations.list`
*   `recommender.spendBasedCommitmentInsights.get`
*   `recommender.spendBasedCommitmentInsights.list`
*   `recommender.spendBasedCommitmentRecommenderConfig.get`

Para ver las recomendaciones de _descuentos por compromiso de uso basados en recursos_, siga estos pasos:

*   `recommender.usageCommitmentRecommendations.get`
*   `recommender.commitmentUtilizationInsights.get`
*   `recommender.usageCommitmentRecommendations.list`
*   `recommender.commitmentUtilizationInsights.list`

### Permisos para modificar recomendaciones

Para modificar las recomendaciones de CUDs _basados en el gasto_, sigue estos pasos:

*   `recommender.spendBasedCommitmentRecommendations.update`
*   `recommender.spendBasedCommitmentInsights.update`
*   `recommender.spendBasedCommitmentRecommenderConfig.update`

Para modificar las recomendaciones de _descuentos por compromiso de uso basados en recursos_, sigue estos pasos:

*   `recommender.usageCommitmentRecommendations.update`
*   `recommender.commitmentUtilizationInsights.update`

Hay varias formas de ver las recomendaciones de descuentos por uso comprometido.

Para ver las recomendaciones de compromisos que se aplican a todo el uso de tu cuenta de facturación de Cloud, usa el [centro de control de FinOps](https://docs.cloud.google.com/billing/docs/how-to/finops-hub?hl=es).

Para ver las recomendaciones de compromiso de un proyecto concreto de tu propiedad, usa la página **Recomendaciones de costes** del [centro de recomendaciones](https://docs.cloud.google.com/recommender/docs/recommendation-hub/identify-configuration-problems?hl=es) de la Google Cloud consola.

Para ver las recomendaciones de descuento por uso confirmado de tu cuenta de Facturación de Cloud, haz una de las siguientes acciones:

| Ver todas las recomendaciones de una cuenta de facturación de Cloud | Ver recomendaciones de un proyecto específico |
| --- | --- |
| 
Si tienes permisos de cuenta de facturación de Cloud, puedes obtener recomendaciones de DUC para todo el uso facturado a la cuenta de facturación de Cloud mediante el **centro de FinOps**.

2.  En la Google Cloud consola, abre el centro de FinOps.
    
    [Ir al centro de FinOps](https://console.cloud.google.com/billing/optimize?hl=es)
3.  En el mensaje que aparecerá, elige la cuenta de facturación de Cloud de la que quieras ver las recomendaciones de DUA.
    
    Si hay recomendaciones de CUD disponibles para tu cuenta de facturación de Cloud, las encontrarás en la sección **Principales recomendaciones**.
    
4.  Para ver información detallada sobre una recomendación, haz clic en ella.

 | 

Si tienes permisos para un proyecto concreto, consulta las recomendaciones de creación, actualización y eliminación de recursos del proyecto en la página **Recomendaciones de costes** de Active Assist.

2.  En la Google Cloud consola, abre la página **Recomendaciones de costes** de Active Assist.
    
    [Ir a Recomendaciones de costes](https://console.cloud.google.com/active-assist/list/cost/recommendations?hl=es)
3.  Selecciona el proyecto del que quieras ver las recomendaciones de CUD.
4.  Para ver información detallada sobre una recomendación, haz clic en ella.

 |

Interpretar el resumen de la recomendación
------------------------------------------

A continuación, se muestra un ejemplo de un resumen de una recomendación de CUD basada en el gasto, junto con el gráfico que muestra cómo se calcula la recomendación. A grandes rasgos, la recomendación se basa en la utilización de tus recursos, la cantidad de uso que quieras cubrir con las CUDs y las CUDs que ya tengas. En el gráfico se muestra el nivel de utilización en el que ahorrarás costes si te suscribes a un compromiso.

### Estimar el uso óptimo

#### Estadísticas de uso

Para ver una breve explicación de cómo se ha calculado tu recomendación basada en el gasto, consulta la sección **Estadísticas de uso** de la recomendación. Las estadísticas de uso explican el punto de equilibrio de tu compra de compromiso. Por ejemplo, tu información valiosa sobre el uso será similar a esta, en función de los detalles de tu recomendación de CUD:

"Con la cobertura de DUC recomendada de 90 USD/hora, los ahorros seguirán siendo positivos aunque el uso apto se reduzca en un 54 %".

#### Ejemplo de cómo calcular el uso óptimo

Para estimar la utilización de recursos con la que alcanzarás el punto de equilibrio de tus costes, resta el porcentaje de descuento de CUD a 100. Por ejemplo, si recibes una recomendación para un descuento por compromiso de uso de Cloud SQL de 1 año con un descuento del 25%, el uso de recursos con el que alcanzarás el punto de equilibrio es `100% - 25% = 75%`.

Para entender la estimación, supongamos que el uso de Cloud SQL es de 100 USD al precio de lista. Si tus instancias de Cloud SQL tienen un tiempo de actividad del 100% y te suscribes a un descuento por uso confirmado de 1 año basado en el gasto con un descuento del 25 %, pagarás `$100 - (25% of $100) = $75` por el uso.

Si el tiempo de actividad de tus instancias de Cloud SQL se reduce al 80%, el precio de catálogo es de 80 USD, pero con un compromiso, pagarías 75 USD por el uso, lo que te daría un descuento de `($80 - $75) / $80 = 0.0625` o `6.25%` sobre el precio de catálogo.

Del mismo modo, si el tiempo de actividad es del 75 %, los costes serán los mismos que si pagaras el precio de lista. Si es inferior al 75 %, ya no ahorrarás dinero al suscribirte a un compromiso.

Para ver una breve explicación de cómo se ha calculado tu recomendación basada en el gasto, consulta la sección **Estadísticas de uso** de la recomendación.

Simular situaciones para obtener ahorros con los DUCs
-----------------------------------------------------

En el centro de FinOps, puedes usar una recomendación de CUD _basada en el gasto_ o _en los recursos_ como punto de partida para simular varios escenarios de uso y personalizar la recomendación para comprar un compromiso que maximice tus ahorros.

[Ir al centro de FinOps](https://console.cloud.google.com/billing/optimize?hl=es)

### Crear un modelo de situación de CUD

1.  Para empezar a personalizar una recomendación en el centro de FinOps, haz clic en la **recomendación** que quieras modelizar.
2.  En la página de detalles de la recomendación, haz clic en **Crear un escenario**.
3.  En la herramienta de modelización de escenarios, usa las opciones que mejor reflejen tu uso. Estas son algunas de las formas en las que puedes personalizar la recomendación:
    
    *   Sección **Recomendación basada en el historial de uso:** de forma predeterminada, la recomendación se basa en los últimos 30 días de uso. Para analizar tu uso durante un periodo más largo, cambia el número de días del historial que quieres tener en cuenta.
        
        Para excluir las fechas en las que haya tenido un uso atípico, como un periodo en el que haya tenido una demanda inusualmente alta, habilite **Ignorar el historial de uso de días concretos** y especifique un intervalo de tiempo.
        
        La recomendación se vuelve a calcular en función del número de días que selecciones y de las fechas que excluyas.
        
    *   Sección **Uso apto cubierto**: puede modelizar la cantidad de uso cubierta por un CUD, en función del tipo de CUD:
        
        *   En el caso de las CUD basadas en el gasto, puedes definir un porcentaje de gasto por hora.
        *   En el caso de los CUDs basados en recursos, puedes definir un número de unidades de recursos usadas.
        
        Para ayudarte a modelar tu uso en el mundo real de forma más precisa, esta sección incluye un mensaje que muestra tu uso estable real del modelo en el periodo indicado.
        
    *   Sección **Plazo del CUD**: puedes seleccionar un plazo de 1 o 3 años para el modelo de escenario del CUD. La recomendación se vuelve a calcular si cambias el plazo seleccionado.
        

Por ejemplo, en la siguiente captura de pantalla se muestra una recomendación de modelo de escenario de CUD para la compra de un compromiso flexible de Compute de 3 años, basada en los últimos 30 días de uso.

#### Estadísticas de uso en el modelo de escenario

En el _modelo de caso práctico de CUD_, la sección **Estadísticas de uso** ofrece una breve explicación de cómo se ha calculado la recomendación basada en el gasto. Las estadísticas de uso del escenario incluyen tus patrones de gasto históricos del importe recomendado y explican el punto de equilibrio de tu compra de compromiso. Por ejemplo, tus estadísticas de uso serán similares a las siguientes, en función de los detalles del modelo de tu caso práctico de CUD:

*   "Con la cobertura del modelo de escenario de DUC de 0,07 USD/hora, los ahorros seguirán siendo positivos aunque el uso apto se reduzca en un 47 %".

Para compartir la configuración de la situación con otros usuarios, haz clic en **Copiar en el portapapeles** para copiar un enlace que puedas compartir. Cuando los destinatarios abran el enlace, verán el escenario con los parámetros que hayas elegido y con información actualizada sobre el uso adicional que se produzca antes de que abran el enlace.

Aplicar recomendaciones para comprar compromisos adicionales
------------------------------------------------------------

Una vez que hayas revisado la recomendación y seleccionado las opciones que se ajustan a tus necesidades, puedes iniciar el proceso de compra.

### Revisar y comprar recomendaciones basadas en recursos

Mientras [consultas tus recomendaciones de compromiso](#viewing-recommendations), abre la página de detalles de la recomendación y sigue estos pasos:

1.  En la parte inferior de la página, haz clic en **Revisar y comprar**.
    
    *   Para obtener recomendaciones sobre los **compromisos basados en el gasto**, revisa los campos rellenados automáticamente del formulario **Comprar un descuento por compromiso de uso** para comprobar que son correctos.
        
        Si tu cuenta de facturación de Cloud se factura en una moneda que no es el dólar estadounidense, los costes y los ahorros estimados se mostrarán tanto en dólares como en tu moneda local.
        
        [Más información sobre los compromisos basados en el gasto, incluidos los compromisos flexibles de Compute](https://docs.cloud.google.com/docs/cuds-spend-based?hl=es)
        
    *   En el caso de las recomendaciones de **compromiso basado en recursos**, se te redirigirá a la sección de Compute Engine de la Google Cloud consola, donde podrás completar la compra mediante el formulario **Comprar un descuento por uso confirmado**.
        
        Es posible que se te pida que selecciones un proyecto. Este es el proyecto en el que se compran los compromisos. Asegúrate de que la API Compute Engine esté habilitada en el proyecto seleccionado y de que tengas permisos suficientes en el proyecto para comprar compromisos basados en recursos.
        
        Los campos del formulario de compra se rellenan automáticamente en función de la recomendación. Revisa los campos para comprobar que la información sea correcta y actualiza los valores según sea necesario.
        
        [Más información sobre los compromisos basados en recursos de Compute Engine](https://docs.cloud.google.com/compute/docs/instances/signing-up-committed-use-discounts?hl=es)
        
2.  Para completar el proceso de compra, después de revisar el formulario, haz clic en **Comprar**.
    

Cerrar recomendaciones
----------------------

Si no quieres ver una recomendación concreta, puedes **cerrarla**. De esta forma, ningún usuario podrá ver la recomendación en el centro de FinOps de facturación de Cloud ni en las páginas de Active Assist.

Para rechazar recomendaciones de tu cuenta de facturación de Cloud, sigue estos pasos:

1.  En la Google Cloud consola, abre el **centro de control de FinOps** de tu cuenta de facturación de Cloud.
    
    [Ir al centro de FinOps](https://console.cloud.google.com/billing/optimize?hl=es)
    
2.  En la parte inferior de la sección **Ahorro potencial al mes**, haz clic en **Ver todas las recomendaciones**.
    
3.  En la lista de recomendaciones, haga clic en **Acciones** y, a continuación, seleccione **Rechazar**.
    

Configurar los ajustes predeterminados de las recomendaciones de CUD
--------------------------------------------------------------------

Para personalizar las recomendaciones de CUD que recibes, configura los ajustes de recomendaciones siguiendo estos pasos. Los ajustes de configuración se aplicarán en el plazo de un día hábil.

1.  Ve a la página [**Descuentos por compromiso de uso (CUDs)**](https://console.cloud.google.com/billing/reports/commitments?hl=es) de la sección Facturación de la Google Cloud consola.
2.  Selecciona **Configurar recomendaciones**.
3.  Introduce el umbral de cobertura que prefieras como porcentaje.
4.  Elige la duración de los plazos de compromiso que prefieras.
5.  Para ver las recomendaciones, vaya a la [**página Recomendaciones**](https://console.cloud.google.com/home/recommendations?hl=es).

Descuentos por compromiso de uso basados en recursos admitidos por las recomendaciones
--------------------------------------------------------------------------------------

Las recomendaciones de CUDs basados en recursos solo están disponibles para las vCPUs y la memoria de los siguientes tipos y familias de máquinas:

*   [Serie A2 optimizada para aceleradores](https://docs.cloud.google.com/compute/docs/accelerator-optimized-machines?hl=es#a2_vms)
*   [Serie C2 optimizada para la computación](https://docs.cloud.google.com/compute/docs/compute-optimized-machines?hl=es)
*   [Serie C2D optimizada para la computación](https://docs.cloud.google.com/compute/docs/compute-optimized-machines?hl=es#c2d_series)
*   [Serie C3 de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#c3_series)
*   [Serie C3D de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#c3d_series)
*   [Serie C4 de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#c4_series)
*   [Serie E2 de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#e2_machine_types)
*   [Serie M1 con memoria optimizada](https://docs.cloud.google.com/compute/docs/memory-optimized-machines?hl=es#m1_series)
*   [Serie M2 con memoria optimizada](https://docs.cloud.google.com/compute/docs/memory-optimized-machines?hl=es#m2_series)
*   [Serie N1 de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#n1_machines)
*   [Serie N2 de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#n2_series)
*   [Serie N4 de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#n4_series)
*   [Serie N2D de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#n2d_machines)
*   [Serie Tau T2D de uso general](https://docs.cloud.google.com/compute/docs/general-purpose-machines?hl=es#t2d_machines)

Para obtener más información, consulta [Compromisos basados en recursos](https://docs.cloud.google.com/docs/cuds?hl=es#resource_based_commitments).

Descuentos por compromiso de uso basados en la inversión admitidos por las recomendaciones
------------------------------------------------------------------------------------------

Las recomendaciones de CUDs basadas en el gasto solo están disponibles para los siguientes productos:

*   AlloyDB for PostgreSQL
*   Servicio de copia de seguridad y DR
*   Copia de seguridad de GKE
*   Suscripción a CUD de streaming de Dataflow
*   Memorystore
*   Spanner
*   Cloud SQL
*   Descuentos por compromiso de uso flexibles
*   VMware Engine de Google Cloud
*   Google Cloud NetApp Volumes

Para obtener más información, consulta [Compromisos basados en el gasto](https://docs.cloud.google.com/docs/cuds?hl=es#spend_based_commitments).

*   [Más información sobre los descuentos por compromiso de uso](https://docs.cloud.google.com/docs/cuds?hl=es)
*   [Analizar la eficacia de los descuentos por uso confirmado](https://docs.cloud.google.com/billing/docs/how-to/cud-analysis?hl=es)
*   [Ver los créditos que recibe en los informes](https://docs.cloud.google.com/billing/docs/how-to/reports?hl=es#credits)
*   [Comprender lo que se ahorra con informes de desglose de costes](https://docs.cloud.google.com/billing/docs/how-to/cost-breakdown?hl=es)
