# Descripción general de los extremos de API regionales y multirregionales &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/about-regional-endpoints

Google Cloud recomienda usar extremos regionales o multirregionales en los siguientes casos:

*   Los datos en tránsito deben permanecer dentro de una región o jurisdicción específica.
*   Necesitas evidencia del manejo de datos regionales para fines de cumplimiento o auditoría.
*   Necesitas dominios de falla aislados regionalmente para acceder a los Google Cloud servicios.

Los extremos regionales y multirregionales ofrecen la mayor cantidad de beneficios a los clientes que requieren el uso de servicios específicos de la ubicación. Estos extremos también garantizan que los datos en tránsito permanezcan en una ubicación en particular. Esta localidad de datos se mantiene, ya sea que se acceda a los datos a través de conectividad privada o a través de Internet pública.

Los extremos regionales y multirregionales son diferentes del Acceso privado a Google. El Acceso privado a Google permite que las VMs en subredes privadas lleguen a las APIs de Google sin atravesar Internet. Los extremos regionales proporcionan un aislamiento regional y una residencia de datos más estrictos, ya que toda la ruta de acceso de la solicitud y el frontend del servicio están regionalizados.

Los extremos de API regionales proporcionan acceso a Google Cloud los servicios a través de un API extremo que se limita a una sola Google Cloud región. El tráfico que se envía a un extremo regional se procesa y se finaliza con TLS dentro de la región especificada.

Para la mayoría de los Google Cloud servicios, puedes usar extremos regionales para trabajar con recursos regionales dentro de la región especificada. Por lo general, las operaciones en recursos globales, recursos multirregionales y recursos regionales fuera de la región no son compatibles con el extremo regional.

Extremos multirregionales
-------------------------

Los extremos de API multirregionales proporcionan acceso a Google Cloud los servicios a través de un extremo de API que se limita a un conjunto de Google Cloud regiones dentro del mismo país, como Estados Unidos, India o Canadá, o jurisdicción, como la Unión Europea. El tráfico que se envía a un extremo multirregional se procesa y se finaliza con TLS por completo dentro de la jurisdicción especificada.

Para la mayoría de los Google Cloud servicios, puedes usar extremos multirregionales para trabajar con recursos multirregionales dentro de la multirregión especificada. Por lo general, no se admiten las operaciones en recursos globales, recursos regionales y recursos multirregionales de otras jurisdicciones.

Beneficios de los extremos regionales y multirregionales
--------------------------------------------------------

El uso de extremos regionales y multirregionales tiene los siguientes beneficios:

*   **Residencia de datos**: Ayuda a cumplir con los requisitos estrictos de residencia de datos, ya que mantiene los datos en tránsito dentro de la región elegida. La sesión de TLS siempre finaliza dentro de la región especificada.
*   **Aislamiento regional**: Reduce las dependencias entre regiones, lo que reduce los problemas debido a fallas en otras regiones.
*   **Cumplimiento**: Te ayuda a cumplir con los requisitos reglamentarios o los requisitos de la política que exigen que los datos se procesen y almacenen dentro de límites geográficos específicos.

Limitaciones de los extremos regionales y multirregionales
----------------------------------------------------------

*   No se puede acceder a los extremos regionales y multirregionales a través del Acceso privado a Google. Para conectarte a extremos regionales a través del acceso privado, usa Private Service Connect para crear extremos privados. Para obtener más información, consulta [Accede a las APIs regionales de Google a través de extremos privados](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=es-419).
    
*   Muchos servicios no admiten operaciones entre regiones desde extremos regionales. Para ejecutar operaciones entre regiones, usa extremos globales. Si las operaciones entre regiones permanecen dentro de una jurisdicción, usa extremos multirregionales.
    
*   Algunos Google Cloud servicios no admiten extremos regionales ni multirregionales. Para obtener una lista completa de los servicios admitidos, consulta [Extremos de servicios regionales](https://docs.cloud.google.com/docs/security/compliance/regional-service-endpoints?hl=es-419).
    
*   Los extremos regionales y multirregionales pueden tener una latencia más alta que los extremos globales. El rendimiento varía según tu ubicación y la región que usas.
    

Implementa extremos regionales y multirregionales
-------------------------------------------------

Puedes acceder a extremos regionales o multirregionales de forma privada desde tu VPC a través de balanceadores de cargas regionales. Por ejemplo, puedes definir un [perímetro de Controles del servicio de VPC](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=es-419) para aislar los recursos de la nube de un proyecto determinado de la Internet y de redes no autorizadas. Para un perímetro determinado, decides para qué servicios aplicar los Controles del servicio de VPC y, luego, colocas los proyectos dentro del perímetro. Para obtener más información, consulta la [sección Usa los Controles del servicio de VPC](#vpc-service-controls) en este documento.

### Acceso público

Puedes acceder a extremos regionales y multirregionales de forma pública a través de Internet. Cuando accedes a extremos regionales desde Internet, el tráfico se enruta a través de las Google Cloud [herramientas de redes del nivel Estándar](https://docs.cloud.google.com/network-tiers/docs/overview?hl=es-419). La conexión, incluida la finalización de TLS, se controla dentro de la región de destino.

Para obtener más información, consulta [Accede a las APIs regionales de Google a través de extremos públicos](https://docs.cloud.google.com/docs/security/compliance/access-public-regional-endpoints?hl=es-419).

### Acceso privado

Para obtener acceso privado a extremos regionales y multirregionales, usa [Private Service Connect](https://docs.cloud.google.com/vpc/docs/private-service-connect?hl=es-419). Private Service Connect crea una dirección IP privada en tu [nube privada virtual (VPC)](https://docs.cloud.google.com/vpc/docs/vpc?hl=es-419). La dirección IP privada se enruta al servicio regional. Aunque las APIs globales suelen usar el Acceso privado a Google estándar con rangos especiales, como `private.googleapis.com`, los extremos regionales y multirregionales requieren una configuración de extremo privado explícita.

Para obtener más información, consulta [Accede a las APIs regionales de Google a través de extremos privados](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=es-419).

### Configura el enrutamiento y los firewalls

Sigue estas instrucciones para configurar correctamente el enrutamiento y los firewalls.

#### Enrutamiento local

Si te conectas a extremos regionales desde entornos locales a través de Cloud Interconnect o Cloud VPN, debes anunciar los rangos de IP privadas del extremo regional. Esta configuración garantiza el enrutamiento adecuado desde tu red local a los Google Cloud servicios.

*   **Cloud Router**: usa [anuncios de ruta personalizados](https://docs.cloud.google.com/network-connectivity/docs/router/how-to/configure-custom-learned-routes?hl=es-419) para anunciar todos los rangos de IP privadas a través de la conexión híbrida.
*   **Firewalls locales**: Configura tus firewalls locales para permitir el tráfico saliente a los rangos de direcciones IP virtuales públicas o privadas de los extremos regionales. Para obtener más información, consulta [Reglas de firewall de VPC](https://docs.cloud.google.com/firewall/docs/using-firewalls?hl=es-419).

#### Reglas de firewall de VPC

Configura tus políticas de firewall de salida para permitir el tráfico a las direcciones IP privadas de los extremos regionales. Si quitas las rutas predeterminadas, verifica que haya rutas adecuadas para los rangos de direcciones IP virtuales (VIP) del extremo regional:

*   Para el acceso VIP público, asegúrate de que existan rutas para los rangos VIP de los extremos regionales con el siguiente salto establecido en la puerta de enlace de Internet predeterminada.
    
*   Para Private Service Connect, usa direcciones IP o enrutamiento interno estándar.
    

Para obtener más información, consulta [Configura una política de firewall jerárquica para permitir el tráfico de salida desde una red de VPC específica](https://docs.cloud.google.com/firewall/docs/quickstarts/configure-hfwpolicy-allow-egress-vpc?hl=es-419).

Usa los Controles del servicio de VPC
-------------------------------------

Los extremos regionales son compatibles con los perímetros de Controles del servicio de VPC. Para obtener más información sobre los Controles del servicio de VPC, consulta [Configuración y detalles del perímetro de servicio](https://docs.cloud.google.com/vpc-service-controls/docs/service-perimeters?hl=es-419).

Los Controles del servicio de VPC ayudan a evitar el robo de datos de Google Cloud los servicios mediante la creación de perímetros de seguridad. Como no puedes acceder a extremos regionales a través del Acceso privado a Google, para acceder a los extremos dentro de un perímetro de VPC, debes usar Private Service Connect para configurar extremos privados.

*   **Aplicación del perímetro**: Cuando agregas un servicio, como `storage.googleapis.com`, a la lista de **Servicios restringidos** en tu perímetro de Controles del servicio de VPC, las reglas de los Controles del servicio de VPC se aplican a todos los métodos de acceso para ese servicio, incluido el acceso a través de extremos regionales.
    
*   **Verificaciones de control de servicio:** Las solicitudes de API a extremos regionales, ya sean públicas o privadas, están sujetas a verificaciones de políticas por parte de la API de Service Control. La API de Service Control evalúa la solicitud en relación con la configuración del perímetro de Controles del servicio de VPC y revisa la red de origen, el proyecto de origen y el proyecto de recursos de destino.
    
*   **Dentro del perímetro:** Los recursos dentro de un perímetro de Controles del servicio de VPC, como las VMs, pueden acceder a los servicios a través de extremos regionales si el servicio forma parte de los servicios accesibles del perímetro. El extremo de red debe estar dentro de tu perímetro de VPC, lo que requiere que crees extremos regionales privados con Private Service Connect. Para obtener más información, consulta [Accede a las APIs regionales de Google a través de extremos privados](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=es-419).
    
*   **Fuera del perímetro:** Los Controles del servicio de VPC bloquean los intentos de acceder a los recursos protegidos dentro del perímetro desde fuera de este con cualquier tipo de extremo, a menos que los niveles de acceso o las reglas de entrada y salida permitan el acceso de forma explícita.
    

Por ejemplo, defines un perímetro de Controles del servicio de VPC para aplicar una política en los recursos de Cloud Storage y, luego, colocas `secure_project` dentro del perímetro. Solo las VMs de `secure_project` pueden acceder a los recursos de Cloud Storage de `secure_project`. Además, se impide que las VMs accedan a los recursos de Cloud Storage fuera del perímetro.

### Beneficios de los Controles del servicio de VPC

El uso de los Controles del servicio de VPC proporciona los siguientes beneficios:

*   Obtienes los beneficios del aislamiento regional y la residencia de datos junto con la protección contra el robo de datos de los Controles del servicio de VPC.
    
*   Con los Controles del servicio de VPC, el acceso que se limita a una sola región también se rige por los límites de seguridad de tus perímetros de Controles del servicio de VPC.
    

### Configura los Controles del servicio de VPC

Dentro de un perímetro de Controles del servicio de VPC, debes acceder a [extremos regionales privados](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=es-419) con Private Service Connect desde dentro de un perímetro de VPC. Para obtener más información, consulta [Accede a las APIs regionales de Google a través de extremos privados](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=es-419).

*   Es posible que se bloquee el acceso estándar a extremos regionales públicos, ya que parece originarse desde fuera del perímetro.
    
*   Asegúrate de que el servicio al que accedes, como `compute.googleapis.com` o `bigquery.googleapis.com`, esté incluido en la lista de servicios restringidos por el perímetro.
    

Usa políticas de la organización para una aplicación adicional
--------------------------------------------------------------

Las políticas de la organización proporcionan controles adicionales para los recursos de la nube de tu organización. Las siguientes dos políticas funcionan con extremos regionales para aplicar la residencia de datos y mejorar el aislamiento regional:

*   [Restringir el uso de extremos (`constraints/gcp.restrictEndpointUsage`)](#enforce-regional-endpoints)
*   [Restricciones de ubicación de recursos (`constraints/gcp.resourceLocations`)](#enforce-location-constraint)

### Aplica el uso de extremos regionales

Para aplicar el uso de extremos regionales, usa la restricción de políticas de la organización **Restringir el uso de extremos** para denegar el acceso a los extremos globales. Para obtener más información, consulta [Cómo restringir el uso de extremos](https://docs.cloud.google.com/docs/security/compliance/restrict-endpoint-usage?hl=es-419).

Los extremos regionales garantizan que los datos en tránsito permanezcan dentro de una región, y la restricción Restringir el uso de extremos garantiza que los recursos que se usan para almacenar datos en reposo solo se creen en las regiones que permites. El uso de ambas políticas proporciona protecciones de residencia de datos más completas para los datos en reposo y para los datos en tránsito.

La restricción Restringir el uso de extremos se establece con una lista de denegación, lo que permite solicitudes a los extremos de API de cualquier servicio admitido que no se denieguen de forma explícita. Después de configurar los extremos regionales, los extremos globales siguen siendo accesibles, a menos que uses la restricción de políticas de la organización Restringir el uso de extremos.

La restricción Restringir el uso de extremos controla las ubicaciones físicas en las que los usuarios pueden crear y almacenar recursos nuevos Google Cloud , lo que aplica la residencia de datos en reposo. Esta política solo se aplica a la creación de recursos nuevos para los servicios admitidos. Los recursos existentes no se ven afectados.

### Aplica restricciones de ubicación

La política de restricción de ubicaciones de recursos (`constraints/gcp.resourceLocations`) controla las ubicaciones físicas en las que se pueden crear y almacenar recursos nuevos, lo que aborda la residencia de datos en reposo. Google Cloud Para obtener más información, consulta [Establece la política de la organización](https://docs.cloud.google.com/resource-manager/docs/organization-policy/defining-locations?hl=es-419) en la documentación de Resource Manager.

Cuando usas la política de restricción de ubicaciones de recursos, defines una lista de ubicaciones permitidas o denegadas Google Cloud . Para las ubicaciones, puedes usar regiones, zonas o multirregiones. La política solo se aplica a la creación de recursos nuevos para los servicios admitidos. Los recursos existentes no se ven afectados.

Usar esta política con extremos regionales es beneficioso, ya que los extremos regionales garantizan que los datos en tránsito permanezcan dentro de una región, y la política de restricción de ubicaciones garantiza que los recursos que almacenan las regiones que permites.

### Aplicación combinada

Si usas la política Restringir el uso de extremos para denegar extremos globales y la política de restricción de ubicaciones de recursos para restringir las ubicaciones en las que se pueden crear recursos, aplicas los datos en reposo y los datos en tránsito.

*   **Datos en reposo**: Los recursos solo se crean y almacenan en regiones permitidas.
*   **Datos en tránsito**: Las interacciones de la API con esos recursos se controlan por completo dentro de la región especificada cuando se usa el extremo regional correspondiente.

Precios
-------

Para los extremos regionales privados, los precios se basan en [el uso de Private Service Connect](https://docs.cloud.google.com/vpc/pricing?hl=es-419). Los cargos de facturación incluyen el extremo y el procesamiento de datos.

Para los extremos regionales públicos, no se factura el tráfico de entrada. [El tráfico de salida se factura según los precios de red del nivel Estándar.](https://docs.cloud.google.com/network-tiers/pricing?hl=es-419)

Documentación de referencia
---------------------------

*   Usa la [Google Cloud CLI para administrar extremos regionales](https://docs.cloud.google.com/sdk/gcloud/reference/network-connectivity/regional-endpoints?hl=es-419).
*   Consulta la documentación de REST [`regionalEndpoints`](https://docs.cloud.google.com/network-connectivity/docs/reference/networkconnectivity/rest/v1/projects.locations.regionalEndpoints?hl=es-419).

¿Qué sigue?
-----------

*   [Accede a extremos regionales privados](https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints?hl=es-419).
*   [Accede a extremos regionales públicos](https://docs.cloud.google.com/docs/security/compliance/access-public-regional-endpoints?hl=es-419).
*   [Accede a las APIs regionales de Google a través de backends](https://docs.cloud.google.com/vpc/docs/configure-private-service-connect-controls?hl=es-419).
*   Consulta los Google Cloud [extremos de servicio regionales admitidos](https://docs.cloud.google.com/docs/security/compliance/regional-service-endpoints?hl=es-419).
