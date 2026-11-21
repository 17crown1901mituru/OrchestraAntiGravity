# Resumen de Google Cloud &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/overview?hl=es

Hemos preparado este resumen para darte una visión general deGoogle Cloud. En él, te ofrecemos una breve descripción de las funciones más utilizadas, así como indicaciones sobre la documentación que puedes consultar para obtener información más detallada. Saber qué está disponible y cómo funcionan las partes en conjunto puede ayudarte a tomar decisiones sobre cómo proceder. También recibirás indicaciones para acceder a algunos tutoriales que puedes usar para probar Google Cloud en diferentes situaciones.

Para obtener información general sobre el cloud computing, consulta [Ventajas y desventajas del cloud computing](https://cloud.google.com/learn/advantages-of-cloud-computing?hl=es).

Google Cloud recursos
---------------------

Google Cloud consta de un conjunto de recursos físicos, como ordenadores y unidades de disco duro, y recursos virtuales, como máquinas virtuales, que se incluyen en [centros de datos](https://cloud.google.com/about/locations?hl=es) de todo el mundo. Cada ubicación de un centro de datos se encuentra en una _región_. Las regiones están disponibles en África, Asia, Australia, Europa, Norteamérica, Sudamérica y Oriente Medio. Cada región está formada por un conjunto de _zonas_, que están aisladas entre sí en la misma región. Cada zona se identifica con un nombre que combina una letra con el nombre de la región. Por ejemplo, la zona `a` de la región de Asia Oriental se llama `asia-east1-a`.

Esta distribución de recursos ofrece varias ventajas, como la redundancia en caso de errores y una latencia reducida al ubicar los recursos más cerca de los clientes. Esta distribución también introduce algunas reglas sobre cómo se pueden usar los recursos juntos.

Acceder a recursos a través de servicios
----------------------------------------

En cloud computing, los productos que tradicionalmente se llamaban "software" y "hardware" ahora se han convertido en _servicios_. Estos servicios ofrecen acceso a los recursos subyacentes. La [lista de servicios Google Cloud disponibles](https://cloud.google.com/products/?hl=es) es larga y sigue ampliándose. Al desarrollar sitios web o aplicaciones enGoogle Cloud, puedes combinar estos servicios hasta dar con la infraestructura que necesitas y, a continuación, añadir tu código para habilitar los casos de uso que quieras crear.

Recursos globales, regionales y de zona
---------------------------------------

Se puede acceder a algunos recursos desde cualquier otro recurso, en todas las regiones y zonas. Estos _recursos globales_ incluyen imágenes de disco, resúmenes de disco y redes preconfigurados. Solo se puede acceder a algunos recursos desde recursos que se encuentren en la misma región. Estos _recursos regionales_ incluyen direcciones IP externas estáticas. Solo se puede acceder a otros recursos desde recursos que se encuentren en la misma zona. Estos _recursos zonales_ incluyen instancias de VM, sus tipos y discos.

En el siguiente diagrama se muestra la relación entre el ámbito global, las regiones y las zonas, así como algunos de sus recursos:

El alcance de una operación varía en función del tipo de recursos con los que trabajes. Por ejemplo, crear una red es una operación global porque una red es un recurso global, mientras que reservar una dirección IP es una operación regional porque la dirección es un recurso regional.

Cuando empieces a optimizar tus Google Cloud aplicaciones, es importante que sepas cómo interactúan estas regiones y zonas. Por ejemplo, aunque pudieras hacerlo, no te convendría asociar un disco de una región a un ordenador de otra región, ya que la latencia que introducirías provocaría un rendimiento deficiente. Por suerte, Google Cloud no te permitirá hacerlo, ya que los discos solo se pueden conectar a ordenadores de la misma zona.

En función del nivel de autogestión que requiera el [servicio de computación y alojamiento](https://docs.cloud.google.com/docs/overview/cloud-platform-services?hl=es#computing-hosting) que elijas, puede que tengas que pensar en cómo y dónde se asignan los recursos.

Para obtener más información sobre la distribución geográfica de Google Cloud, consulta [Geografía y regiones](https://docs.cloud.google.com/docs/geography-and-regions?hl=es).

Proyectos
---------

Todos los recursos de GCP que asignes y utilices deben pertenecer a un proyecto. Google Cloud Los proyectos pueden entenderse como la entidad organizativa de lo que construyes. Un proyecto se compone de los ajustes, los permisos y otros metadatos que describen tus aplicaciones. Los recursos de un mismo proyecto pueden trabajar juntos fácilmente, por ejemplo, comunicándose a través de una red interna, de acuerdo con las reglas de regiones y zonas. Un proyecto no puede acceder a los recursos de otro proyecto a menos que utilices la [VPC compartida](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=es) o el [emparejamiento entre redes de VPC](https://docs.cloud.google.com/vpc/docs/vpc-peering?hl=es).

Cada proyecto Google Cloud tiene lo siguiente:

*   Un nombre de proyecto, proporcionado por ti
*   Un ID de proyecto, que puedes proporcionar tú o que Google Cloud puede proporcionarte.
*   Un número de proyecto, que Google Cloud proporciona.

Cuando trabajes con Google Cloud, usarás estos identificadores en determinados comandos y llamadas a la API. En la siguiente captura de pantalla se muestra el nombre, el ID y el número de un proyecto:

En este ejemplo:

*   **Proyecto de ejemplo** es el nombre del proyecto.
*   **example-id** es el ID del proyecto.
*   **123456789012** es el número de proyecto.

Cada ID de proyecto es único en Google Cloud. Una vez que hayas creado un proyecto, podrás eliminarlo, pero su ID no se podrá volver a usar.

Puedes crear varios proyectos y usarlos para separar tu trabajo de la forma que consideres más adecuada. Por ejemplo, puedes tener un proyecto al que puedan acceder todos los miembros del equipo y otro al que solo puedan acceder algunos.

Cuando la facturación está habilitada, cada proyecto se asocia a una cuenta de facturación. Varios proyectos pueden facturar el uso de los recursos a la misma cuenta.

Un proyecto actúa como espacio de nombres. Esto significa que cada recurso de cada proyecto debe tener un nombre único, pero normalmente puedes reutilizar los nombres de los recursos si están en proyectos independientes. Algunos nombres de recursos deben ser únicos a nivel global. Consulta los detalles en la documentación del recurso.

Para obtener más información, consulta el artículo sobre cómo [crear y gestionar proyectos](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=es).

Formas de interactuar con los servicios
---------------------------------------

Google Cloud te ofrece tres formas básicas de interactuar con los servicios y los recursos.

### Google Cloud console

La [Google Cloud console](https://console.cloud.google.com/?hl=es) proporciona una interfaz gráfica de usuario basada en la Web que puedes usar para gestionar tus proyectos y recursos de Google Cloud . Cuando usas la consolaGoogle Cloud , creas un proyecto o eliges uno que ya tengas y, a continuación, usas los recursos que creas en el contexto de ese proyecto.

### Interfaz de línea de comandos

Si prefieres trabajar en la línea de comandos, puedes realizar la mayoría de las tareas con [CLI de Google Cloud](https://docs.cloud.google.com/sdk/gcloud?hl=es).Google Cloud La CLI de gcloud te permite gestionar el flujo de trabajo de desarrollo y losGoogle Cloud recursos en una ventana de terminal.

Por ejemplo, puedes crear una instancia de máquina virtual (VM) de Compute Engine ejecutando el comando [`gcloud compute instances create`](https://docs.cloud.google.com/sdk/gcloud/reference/compute/instances/create?hl=es) en el entorno de shell.

Puedes ejecutar comandos de `gcloud` de las siguientes formas:

*   Puedes instalar la [CLI de Google Cloud](https://docs.cloud.google.com/sdk/docs?hl=es). La CLI de gcloud te permite abrir una ventana de terminal en tu ordenador y ejecutar comandos para gestionar recursos de Google Cloud .
    
*   Puedes usar [Cloud Shell](https://docs.cloud.google.com/shell/docs/features?hl=es), un shell basado en navegador. Como se ejecuta en una ventana del navegador, no tienes que instalar nada en tu ordenador. Puedes abrir Cloud Shell desde la consola [Google Cloud](https://console.cloud.google.com/?cloudshell=true&hl=es) .
    

Cloud Shell ofrece lo siguiente:

*   Una instancia de máquina virtual de Compute Engine temporal.
*   Un [editor de código integrado](https://docs.cloud.google.com/shell/docs/editor-overview?hl=es).
*   Almacenamiento en discos persistentes de 5 GB.
*   gcloud CLI y otras herramientas preinstaladas.
*   Compatibilidad con los lenguajes Java, Go, Python, Node.js, PHP, Ruby y .NET.
*   Función de vista previa web.
*   Autorización integrada para acceder a proyectos y recursos de la consola Google Cloud .

Para ver una lista de comandos `gcloud`, consulta la [referencia de `gcloud`](https://docs.cloud.google.com/sdk/gcloud/reference?hl=es).

Para obtener más información sobre Cloud Shell, consulta [Cómo funciona Cloud Shell](https://docs.cloud.google.com/shell/docs/how-cloud-shell-works?hl=es).

### Bibliotecas de cliente

Google Cloud proporciona [bibliotecas de cliente](https://docs.cloud.google.com/sdk/cloud-client-libraries?hl=es) que te permiten crear y gestionar recursos fácilmente. Google Cloud Las bibliotecas de cliente exponen APIs con dos fines principales:

*   Las _APIs de aplicaciones_ proporcionan acceso a los servicios. Las APIs de aplicaciones están optimizadas para los lenguajes admitidos, como Node.js y Python. Las bibliotecas se han diseñado en torno a metáforas de servicios, por lo que puedes trabajar con los servicios de forma más natural y escribir menos código repetitivo. Las bibliotecas también proporcionan funciones auxiliares para la [autenticación y la autorización](https://docs.cloud.google.com/docs/authentication?hl=es).
    
*   Las _APIs de administrador_ ofrecen funciones para gestionar recursos. Por ejemplo, puedes usar las APIs de administrador si quieres crear tus propias herramientas automatizadas.
    

También puedes usar las [bibliotecas de cliente de la API de Google](https://developers.google.com/api-client-library/?hl=es) para acceder a las APIs de productos como Google Maps, Google Drive y YouTube.

Precios
-------

Para saber cómo explorar y evaluar Google Cloud sin coste económico, consulta las [funciones gratuitas y la oferta de prueba Google Cloud](https://docs.cloud.google.com/free/docs/free-cloud-features?hl=es) .

Para consultar los detalles de los precios de los servicios individuales, consulta la [lista de precios](https://cloud.google.com/pricing/list?hl=es).

Para estimar los costes totales de ejecutar una carga de trabajo específica enGoogle Cloud, consulta la [calculadora de precios](https://cloud.google.com/products/calculator?hl=es).

Prueba Google Cloud
-------------------

Si es la primera vez que utilizas Google Cloud, crea una cuenta para evaluar el rendimiento de nuestros productos en situaciones reales. Los nuevos clientes también reciben 300 USD en crédito gratuito para ejecutar, probar y desplegar cargas de trabajo.

[Empezar gratis](https://console.cloud.google.com/freetrial?hl=es)
