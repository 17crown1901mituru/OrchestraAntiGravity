# Información general sobre el diseño de seguridad de la infraestructura de Google &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/infrastructure/design?hl=es

_Este contenido se actualizó por última vez en junio del 2024 y representa la situación en el momento en que se redactó. Las políticas y los sistemas de seguridad de Google pueden cambiar en el futuro, ya que mejoramos constantemente la protección que proporcionamos a nuestros clientes._

[Descargar versión en PDF](https://docs.cloud.google.com/static/docs/security/infrastructure/design/resources/google_infrastructure_whitepaper_fa.pdf?hl=es)

Introducción
------------

En este documento se describe de forma general cómo se diseña la seguridad de la infraestructura técnica de Google. Está dirigido a directivos de seguridad, arquitectos de seguridad y auditores.

En este documento se describe lo siguiente:

*   La infraestructura técnica global de Google, diseñada para ofrecer seguridad durante todo el ciclo de vida del tratamiento de la información en Google. Esta infraestructura ayuda a proporcionar lo siguiente:
    *   Despliegue seguro de servicios
    *   Almacenamiento seguro de datos con medidas de protección de la privacidad de los usuarios finales
    *   Comunicación segura entre servicios
    *   Comunicación segura y privada con los clientes a través de Internet
    *   Funcionamiento seguro por parte de los ingenieros de Google
*   Cómo usamos esta infraestructura para crear servicios de Internet, incluidos servicios de consumo como la Búsqueda de Google, Gmail y Google Fotos, y servicios empresariales como Google Workspace yGoogle Cloud.
*   Los productos y servicios de seguridad que son el resultado de las innovaciones que hemos implementado internamente para satisfacer nuestras necesidades de seguridad. Por ejemplo, [BeyondCorp](https://cloud.google.com/beyondcorp?hl=es) es el resultado directo de nuestra implementación interna del [modelo de seguridad de confianza cero](https://cloud.google.com/blog/products/identity-security/cloud-ciso-perspectives-january-2022?hl=es).
*   Cómo se ha diseñado la seguridad de la infraestructura en capas progresivas. Estas capas incluyen lo siguiente:
    
    *   [Infraestructura de bajo nivel](#secure-low-level)
        
    *   [Despliegue de servicios](#secure-service)
        
    *   [Almacenamiento de datos](#secure-data)
        
    *   [Comunicación a través de Internet](#secure-internet)
        
    *   [Operaciones](#operational-security)
        

En las secciones restantes de este documento se describen las capas de seguridad.

Infraestructura de bajo nivel segura
------------------------------------

En esta sección se describe cómo protegemos las instalaciones físicas de nuestros centros de datos, el hardware de nuestros centros de datos y la pila de software que se ejecuta en el hardware.

### Seguridad de las instalaciones físicas

Diseñamos y construimos nuestros propios centros de datos, que incorporan varias capas de seguridad física. El acceso a estos centros de datos está estrictamente controlado. Usamos varias capas de seguridad física para proteger las plantas de nuestros centros de datos. Utilizamos identificación biométrica, detectores de metales, cámaras, barreras para vehículos y sistemas de detección láser de intrusos. Para obtener más información, consulta [Seguridad de los centros de datos](https://www.google.com/about/datacenters/data-security/?hl=es).

Dentro del centro de datos, implementamos controles adicionales para asegurarnos de que el acceso físico a los servidores esté protegido y monitorizado. Para obtener más información, consulta [Cómo protege Google el espacio físico y lógico de un centro de datos](https://docs.cloud.google.com/docs/security/physical-to-logical-space?hl=es).

También alojamos algunos servidores en centros de datos de terceros. En estos centros de datos, cumplimos los mismos estándares normativos que en nuestros propios centros de datos. Nos aseguramos de que haya medidas de seguridad física y conectividad controladas por Google, además de las capas de seguridad que proporciona el operador del centro de datos. Por ejemplo, utilizamos sistemas de identificación biométrica, cámaras y detectores de metales que son independientes de las capas de seguridad que proporciona el operador del centro de datos.

A menos que se indique lo contrario, los controles de seguridad de este documento se aplican tanto a los centros de datos propiedad de Google como a los de terceros.

### Diseño y procedencia del hardware

Los centros de datos de Google constan de miles de servidores conectados a una red local. Diseñamos las placas de servidor y los equipos de redes. Revisamos a los proveedores de componentes con los que trabajamos y elegimos los componentes con cuidado. Trabajamos con proveedores para auditar y validar las propiedades de seguridad que proporcionan los componentes. También diseñamos chips personalizados, incluido un chip de seguridad de hardware (llamado [Titan](https://docs.cloud.google.com/docs/security/titan-hardware-chip?hl=es)), que implementamos en servidores, dispositivos y periféricos. Estos chips nos permiten identificar y autenticar dispositivos legítimos de Google a nivel de hardware, así como actuar como raíces de confianza de hardware.

### Pila de arranque seguro e identidad de la máquina

Los servidores de Google usan varias tecnologías para asegurarse de que arrancan la pila de software prevista. En cada paso del proceso de arranque, Google implementa controles líderes del sector para ayudar a aplicar el estado de arranque que esperamos y a proteger los datos de los clientes.

Nos esforzamos por mejorar continuamente nuestros servidores con cada generación de hardware y por llevar estas mejoras al resto del sector mediante la participación en procesos de estándares con Trusted Computing Group y DMTF.

Cada servidor del centro de datos tiene su propia identidad única. Esta identidad se puede vincular a las raíces de confianza del hardware y al software con el que se inicia el equipo. Esta identidad se usa para autenticar las llamadas a la API hacia y desde los servicios de gestión de bajo nivel de la máquina. Esta identidad también se usa para la autenticación mutua de servidores y el cifrado de transporte. Hemos desarrollado el sistema [Seguridad de transporte en la capa de la aplicación (ALTS)](https://docs.cloud.google.com/docs/security/encryption-in-transit/application-layer-transport-security?hl=es) para proteger las comunicaciones de llamadas a procedimientos remotos (RPC) que se realizan dentro de nuestra infraestructura. Estas identidades de máquina se pueden revocar de forma centralizada para responder a un incidente de seguridad. Además, sus certificados y claves se rotan periódicamente y los antiguos se revocan.

Hemos desarrollado sistemas automatizados para hacer lo siguiente:

*   Asegúrate de que los servidores ejecuten versiones actualizadas de sus pilas de software (incluidos los parches de seguridad).
*   Detectar y diagnosticar problemas de hardware y software.
*   Asegura la integridad de las máquinas y los periféricos con el inicio verificado y la certificación.
*   Asegúrate de que solo las máquinas que ejecuten el software y el firmware previstos puedan acceder a las credenciales que les permitan comunicarse en la red de producción.
*   Quita o repara las máquinas si no superan la comprobación de integridad o cuando ya no las necesites.

Para obtener más información sobre cómo protegemos nuestra pila de arranque y la integridad de las máquinas, consulta [Cómo aplica Google la integridad del arranque en máquinas de producción](https://docs.cloud.google.com/docs/security/boot-integrity?hl=es) y [Atestación remota de máquinas desagregadas](https://docs.cloud.google.com/docs/security/remote-attestation?hl=es).

Despliegue seguro de servicios
------------------------------

Los servicios de Google son los archivos binarios de las aplicaciones que nuestros desarrolladores escriben y ejecutan en nuestra infraestructura. Algunos ejemplos de servicios de Google son los servidores de Gmail, las bases de datos de Spanner, los servidores de Cloud Storage, los transcodificadores de vídeo de YouTube y las máquinas virtuales de Compute Engine que ejecutan aplicaciones de clientes. Para gestionar la escala necesaria de la carga de trabajo, puede que miles de máquinas ejecuten archivos binarios del mismo servicio. Un servicio de orquestación de clústeres llamado [_Borg_](https://research.google/pubs/pub43438/?hl=es) controla los servicios que se ejecutan directamente en la infraestructura.

La infraestructura no presupone el nivel de confianza entre los servicios que se ejecutan en ella. Este modelo de confianza se denomina _modelo de seguridad de confianza cero_. Un modelo de seguridad de confianza cero implica que ningún dispositivo ni usuario es de confianza de forma predeterminada, tanto si se encuentra dentro como fuera de la red.

Como la infraestructura está diseñada para admitir varios propietarios, los datos de nuestros clientes (consumidores, empresas e incluso nuestros propios datos) se distribuyen en una infraestructura compartida. Esta infraestructura se compone de decenas de miles de máquinas homogéneas. La infraestructura no segrega los datos de los clientes en una sola máquina o en un conjunto de máquinas, excepto en circunstancias específicas, como cuando usas Google Cloud para aprovisionar máquinas virtuales en [nodos de un solo inquilino de Compute Engine](https://docs.cloud.google.com/compute/docs/nodes/sole-tenant-nodes?hl=es).

Google Cloud y Google Workspace cumplen los requisitos normativos relativos a la residencia de datos. Para obtener más información sobre la residencia de los datos y Google Cloud, consulta el artículo sobre [restricciones de ubicaciones de recursos](https://docs.cloud.google.com/resource-manager/docs/organization-policy/defining-locations?hl=es). Para obtener más información sobre la residencia de los datos y Google Workspace, consulta el artículo [Regiones de datos: elegir la ubicación geográfica de los datos](https://support.google.com/a/answer/7630496?hl=es).

### Identidad, integridad y aislamiento de los servicios

Para habilitar la comunicación entre servicios, las aplicaciones usan la autenticación y la autorización criptográficas. La autenticación y la autorización proporcionan un control de acceso sólido a un nivel de abstracción y una granularidad que los administradores y los servicios pueden entender.

Los servicios no dependen de la segmentación de la red interna ni de los cortafuegos como mecanismo de seguridad principal. El filtrado de entrada y salida en varios puntos de nuestra red ayuda a evitar la suplantación de IP. Este enfoque también nos ayuda a maximizar el rendimiento y la disponibilidad de nuestra red. Para Google Cloud, puedes añadir mecanismos de seguridad adicionales, como [Controles de Servicio de VPC](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=es) y [Cloud Interconnect](https://docs.cloud.google.com/network-connectivity/docs/interconnect/concepts/overview?hl=es).

Cada servicio que se ejecuta en la infraestructura tiene una identidad de cuenta de servicio asociada. Se proporciona a un servicio credenciales criptográficas que puede usar para demostrar su identidad a otros servicios al hacer o recibir RPCs. Estas identidades se usan en las políticas de seguridad. Las políticas de seguridad aseguran que los clientes se comuniquen con el servidor previsto y que los servidores limiten los métodos y los datos a los que pueden acceder determinados clientes.

Utilizamos varias técnicas de aislamiento y sandboxing para proteger un servicio de otros servicios que se ejecutan en la misma máquina. Entre estas técnicas se incluyen la separación de usuarios de Linux, las zonas de pruebas basadas en lenguajes (como la [API Sandboxed](https://developers.google.com/code-sandboxing/sandboxed-api?hl=es)) y en el kernel, el kernel de aplicaciones para contenedores (como [gVisor](https://gvisor.dev/)) y la virtualización basada en hardware. En general, usamos más capas de aislamiento para las cargas de trabajo más arriesgadas. Entre las cargas de trabajo más arriesgadas se incluyen las que procesan entradas no saneadas de Internet. Por ejemplo, las cargas de trabajo más arriesgadas incluyen la ejecución de convertidores de archivos complejos en entradas no fiables o la ejecución de código arbitrario como servicio para productos como Compute Engine.

Para aumentar la seguridad, los servicios sensibles, como el servicio de orquestación de clústeres y algunos servicios de gestión de claves, se ejecutan exclusivamente en máquinas dedicadas.

En Google Cloud, para proporcionar un aislamiento criptográfico más sólido a tus cargas de trabajo y proteger los datos en uso, admitimos los servicios de [Confidential Computing](https://docs.cloud.google.com/confidential-computing?hl=es) para las instancias de máquinas virtuales (VM) de Compute Engine y los nodos de Google Kubernetes Engine (GKE).

### Gestión del acceso entre servicios

El propietario de un servicio puede gestionar el acceso creando una lista de otros servicios que puedan comunicarse con él. Esta función de gestión de acceso se proporciona a través de la infraestructura de Google. Por ejemplo, un servicio puede restringir las llamadas a procedimientos remotos entrantes únicamente a una lista permitida de otros servicios. El propietario también puede configurar el servicio con una lista de identidades de servicio permitidas, que la infraestructura aplica automáticamente. La aplicación incluye el registro de auditoría, las justificaciones y la restricción de acceso unilateral (por ejemplo, para las solicitudes de ingenieros).

Los ingenieros de Google que necesitan acceder a los servicios también tienen identidades individuales. Los servicios se pueden configurar para permitir o denegar el acceso en función de sus identidades. Todas estas identidades (máquina, servicio y empleado) se encuentran en un espacio de nombres global que mantiene la infraestructura.

Para gestionar estas identidades, la infraestructura proporciona un sistema de flujo de trabajo que incluye cadenas de aprobación, registros y notificaciones. Por ejemplo, la política de seguridad puede exigir la autorización de varias partes. Este sistema usa la regla de las dos personas para asegurarse de que un ingeniero no pueda realizar operaciones sensibles sin obtener primero la aprobación de otro ingeniero autorizado. Este sistema permite que los procesos de gestión de acceso seguro se escalen a miles de servicios que se ejecutan en la infraestructura.

La infraestructura también proporciona a los servicios el servicio canónico para la gestión de usuarios, grupos y membresías, de modo que puedan implementar un control de acceso personalizado y detallado cuando sea necesario.

Las identidades de los usuarios finales se gestionan por separado, tal como se describe en el artículo [Gestión del acceso a los datos de los usuarios finales en Google Workspace](#access-management-of-end-user-data-in-google-workspace).

### Cifrado de la comunicación entre cargas de trabajo

La infraestructura ofrece confidencialidad e integridad para los datos de RPC de la red. Todo el tráfico de Google Cloud red virtual está cifrado. La comunicación entre las cargas de trabajo de la infraestructura está cifrada, con excepciones que solo se conceden a las cargas de trabajo de alto rendimiento en las que el tráfico no cruza las múltiples capas de seguridad física en el perímetro de un centro de datos de Google. Google Cloud La comunicación entre los servicios de infraestructura de Google Cloud tiene protección de integridad criptográfica.

La infraestructura proporciona de forma automática y eficiente (con la ayuda de la descarga de hardware) cifrado de extremo a extremo para el tráfico RPC de la infraestructura que se transmite por la red entre centros de datos.

### Gestión del acceso a los datos de usuario final en Google Workspace

Un servicio típico de Google Workspace se escribe para hacer algo por un usuario final. Por ejemplo, un usuario final puede almacenar su correo en Gmail. La interacción del usuario final con una aplicación como Gmail puede abarcar otros servicios de la infraestructura. Por ejemplo, Gmail puede llamar a una API People para acceder a la libreta de direcciones del usuario final.

En la sección [Cifrado de la comunicación entre servicios](#encryption-inter-service) se describe cómo se ha diseñado un servicio (como Contactos de Google) para proteger las solicitudes de RPC de otro servicio (como Gmail). Sin embargo, este nivel de control de acceso sigue siendo un conjunto amplio de permisos, ya que Gmail puede solicitar los contactos de cualquier usuario en cualquier momento.

Cuando Gmail hace una solicitud RPC a Contactos de Google en nombre de un usuario final, la infraestructura permite que Gmail presente un ticket de permiso de usuario final en la solicitud RPC. Esta incidencia demuestra que Gmail está haciendo la solicitud RPC en nombre de ese usuario final concreto. El ticket permite que Contactos de Google implemente una medida de protección para que solo devuelva datos del usuario final cuyo nombre se indica en el ticket.

La infraestructura proporciona un servicio de identidad de usuario central que emite estas incidencias contextuales de usuarios finales. El servicio de identidad verifica el inicio de sesión del usuario final y, luego, envía una credencial de usuario, como una cookie o un token de OAuth, al dispositivo del usuario. Las solicitudes que envíe posteriormente el dispositivo a nuestra infraestructura deben incluir esa credencial de usuario final.

Cuando un servicio recibe una credencial de usuario final, la envía al servicio de identidad para que la verifique. Si se verifica la credencial del usuario final, el servicio de identidad devuelve un ticket de contexto del usuario final de corta duración que se puede usar para las RPCs relacionadas con la solicitud del usuario. En nuestro ejemplo, el servicio que obtiene el ticket de contexto de usuario final es Gmail, que envía el ticket a Contactos de Google. A partir de ese momento, en todas las llamadas en cascada, el servicio de llamadas puede enviar el ticket de contexto del usuario final al destinatario como parte de la RPC.

En el siguiente diagrama se muestra cómo se comunican el servicio A y el servicio B. La infraestructura proporciona identidad de servicio, autenticación mutua automática, comunicación entre servicios cifrada y aplicación de las políticas de acceso definidas por el propietario del servicio. Cada servicio tiene una configuración de servicio que crea el propietario del servicio. Para la comunicación cifrada entre servicios, la autenticación mutua automática usa las identidades de la persona que llama y de la persona a la que se llama. La comunicación solo es posible cuando la configuración de una regla de acceso lo permite.

Para obtener información sobre la gestión de accesos en Google Cloud, consulta la [descripción general de IAM](https://docs.cloud.google.com/iam/docs/overview?hl=es).

### Gestión del acceso a los datos de usuario final en Google Cloud

Al igual que la [gestión de acceso a los datos de los usuarios finales en Google Workspace](#access-management-of-end-user-data-in-google-workspace), la infraestructura proporciona un servicio de identidad de usuario central que autentica las cuentas de servicio y emite tickets de contexto de usuario final después de que se autentique una cuenta de servicio. La gestión de acceso entre servicios de Google Cloud se suele hacer con [agentes de servicio](https://docs.cloud.google.com/iam/docs/service-agents?hl=es) en lugar de con tickets de contexto de usuario final.

Google Cloud usa Gestión de Identidades y Accesos (IAM) y productos contextuales, como Identity-Aware Proxy, para permitirte gestionar el acceso a los recursos de tuGoogle Cloud organización. Las solicitudes a los servicios de Google Cloud pasan por la gestión de identidades y accesos para verificar los permisos.

El proceso de gestión de accesos es el siguiente:

1.  Las solicitudes se reciben a través del [servicio Google Front End](#google-frontend-service) o del servicio Cloud Front End para las VMs de los clientes.
2.  La solicitud se dirige al servicio de identidad de usuario central, que completa la comprobación de autenticación y emite las incidencias contextuales de usuarios finales.
3.  La solicitud también se envía para comprobar elementos como los siguientes:
    *   Permisos de acceso de gestión de identidades y accesos, incluidas las políticas y la pertenencia a grupos
    *   Transparencia de acceso con [Transparencia de acceso](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/overview?hl=es)
    *   [Registros de auditoría de Cloud](https://docs.cloud.google.com/logging/docs/audit?hl=es)
    *   [Cuotas](https://docs.cloud.google.com/docs/quotas/overview?hl=es)
    *   [Facturación](https://docs.cloud.google.com/billing/docs/concepts?hl=es)
    *   Calculadora de atributos
    *   Perímetros de seguridad de [Controles de Servicio de VPC](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=es)
4.  Una vez que se han superado todas estas comprobaciones, se llaman los servicios de backend. Google Cloud

Para obtener información sobre la gestión de accesos en Google Cloud, consulta la [descripción general de IAM](https://docs.cloud.google.com/iam/docs/overview?hl=es).

Almacenamiento seguro de datos
------------------------------

En esta sección se describe cómo implementamos la seguridad de los datos almacenados en la infraestructura.

### Encriptado en reposo

La infraestructura de Google proporciona varios servicios de almacenamiento y sistemas de archivos distribuidos (por ejemplo, Spanner y [Colossus](https://cloud.google.com/blog/products/storage-data-transfer/a-peek-behind-colossus-googles-file-system?hl=es)), así como un servicio de gestión de claves centralizado. Las aplicaciones de Google acceden al almacenamiento físico mediante la infraestructura de almacenamiento. Utilizamos varias capas de encriptado para proteger los datos en reposo. De forma predeterminada, la infraestructura de almacenamiento cifra los datos de usuario antes de que se escriban en el almacenamiento físico.

La infraestructura realiza el cifrado en la capa de aplicación o de infraestructura de almacenamiento. Google gestiona y es propietario de las claves de este cifrado. El cifrado permite que la infraestructura se aísle de posibles amenazas en los niveles inferiores del almacenamiento, como el firmware de disco malicioso. Cuando es posible, también habilitamos la compatibilidad con el cifrado de hardware en nuestros discos duros y SSDs, y hacemos un seguimiento meticuloso de cada unidad durante todo su ciclo de vida. Antes de que un dispositivo de almacenamiento cifrado y retirado pueda salir físicamente de nuestras instalaciones, se limpia mediante un proceso de varios pasos que incluye dos verificaciones independientes. Los dispositivos que no superan este proceso de limpieza se destruyen físicamente (es decir, se trituran) en las instalaciones.

Además del cifrado que realiza la infraestructura con claves de cifrado gestionadas y propiedad de Google, Google Cloud y Google Workspace proporciona servicios de gestión de claves que puedes poseer y gestionar. En el caso deGoogle Cloud, [Cloud KMS](https://docs.cloud.google.com/docs/security/key-management-deep-dive?hl=es) es un servicio en la nube que te permite crear tus propias claves criptográficas, incluidas las claves basadas en hardware con certificación FIPS 140-3 de nivel 3. Estas claves son específicas para ti, no para el servicio de Google Cloud , y puedes gestionarlas de acuerdo con tus políticas y procedimientos. En Google Workspace, puedes usar el cifrado del lado del cliente. Para obtener más información, consulta el artículo [Cifrado del lado del cliente y colaboración reforzada en Google Workspace](https://cloud.google.com/blog/products/workspace/new-google-workspace-security-features?hl=es).

### Eliminación de datos

La eliminación de material o datos criptográficos suele empezar marcando claves o datos específicos como programados para su eliminación. El proceso para marcar datos para su eliminación tiene en cuenta las políticas específicas del servicio y las políticas específicas del cliente.

Si programamos la eliminación de los datos o inhabilitamos las claves primero, podemos recuperarlos en caso de que se eliminen por error, ya sea por iniciativa del cliente, debido a un error o como resultado de un error en un proceso interno.

Cuando un usuario final elimina su cuenta, la infraestructura notifica a los servicios que gestionan los datos del usuario final que la cuenta se ha eliminado. Los servicios pueden programar la eliminación de los datos asociados a la cuenta de usuario final eliminada. Esta función permite que los usuarios finales controlen sus datos.

Para obtener más información, consulta [Eliminación de datos enGoogle Cloud](https://docs.cloud.google.com/docs/security/deletion?hl=es). Para obtener información sobre cómo usar Cloud Key Management Service para inhabilitar tus propias claves, consulta [Destruir y restaurar versiones de claves](https://docs.cloud.google.com/kms/docs/destroy-restore?hl=es).

Comunicación segura por Internet
--------------------------------

En esta sección se describe cómo protegemos la comunicación entre Internet y los servicios que se ejecutan en la infraestructura de Google.

Como se explica en la sección [Diseño y procedencia del hardware](#hardware-design), la infraestructura consta de muchas máquinas físicas interconectadas a través de la LAN y la WAN. La seguridad de la comunicación entre servicios no depende de la seguridad de la red. Sin embargo, aislamos nuestra infraestructura de Internet en un espacio de direcciones IP privadas. Solo exponemos un subconjunto de las máquinas directamente al tráfico de Internet externo para poder implementar protecciones adicionales, como defensas contra ataques de denegación de servicio (DoS).

### Servicio Google Front End

Cuando un servicio debe estar disponible en Internet, puede registrarse en un servicio de infraestructura llamado Google Front End (GFE). GFE se asegura de que todas las conexiones TLS se terminen con los certificados correctos y de que se sigan las prácticas recomendadas, como la compatibilidad con la confidencialidad directa perfecta. GFE también aplica protecciones contra ataques DoS. A continuación, GFE reenvía las solicitudes del servicio mediante el protocolo de seguridad de RPC descrito en el artículo [Gestión del acceso a los datos de usuario final en Google Workspace](#access-management-of-end-user-data-in-google-workspace).

De hecho, cualquier servicio interno que deba publicarse externamente utiliza GFE como frontend de proxy inverso inteligente. GFE proporciona el alojamiento de direcciones IP públicas de su nombre de DNS público, la protección de la denegación de servicio (DoS) y la terminación de TLS. Los GFE se ejecutan en la infraestructura como cualquier otro servicio y pueden escalar para adaptarse a los volúmenes de solicitudes entrantes.

Cuando las VMs de los clientes en redes de Google Cloud VPC acceden a APIs y servicios de Google alojados directamente en Borg, las VMs de los clientes se comunican con GFEs específicos denominados _front-ends de Cloud_. Para minimizar la latencia, los front-ends de Cloud se encuentran en la misma región de la nube que la VM del cliente. El enrutamiento de red entre las VMs del cliente y los Cloud Front Ends no requiere que las VMs del cliente tengan direcciones IP externas. Cuando [Acceso privado de Google](https://docs.cloud.google.com/vpc/docs/private-google-access?hl=es) está habilitado, las VMs de los clientes que solo tienen direcciones IP internas pueden comunicarse con las direcciones IP externas de las APIs y los servicios de Google mediante Cloud Front Ends. Todo el enrutamiento de red entre las VMs de los clientes, las APIs de Google y los servicios depende de los saltos siguientes dentro de la red de producción de Google, incluso en el caso de las VMs de los clientes que tienen direcciones IP externas.

### Protección contra ataques DoS

La escala de nuestra infraestructura le permite absorber muchos ataques de denegación de servicio. Para reducir aún más el riesgo de que los ataques DoS afecten a los servicios, hemos implementado protecciones DoS multicapa y multinivel.

Cuando nuestra red troncal de fibra óptica proporciona una conexión externa a uno de nuestros centros de datos, la conexión pasa por varias capas de balanceadores de carga de hardware y software. Estos balanceadores de carga envían información sobre el tráfico entrante a un servicio central de denegación de servicio que se ejecuta en la infraestructura. Cuando el servicio central de DoS detecta un ataque de DoS, puede configurar los balanceadores de carga para que rechacen o limiten el tráfico asociado al ataque.

Las instancias de GFE también envían información sobre las solicitudes que reciben al servicio central de denegación de servicio, incluida información de la capa de aplicación a la que no tienen acceso los balanceadores de carga. El servicio central de denegación de servicio puede configurar las instancias de GFE para que rechacen o limiten el tráfico de ataque.

### Autenticación de usuarios

Después de la protección contra ataques DoS, la siguiente capa de defensa para la comunicación segura procede del servicio de identidad central. Los usuarios finales interactúan con este servicio a través de la página de inicio de sesión de Google. El servicio pide un nombre de usuario y una contraseña, y también puede solicitar información adicional a los usuarios en función de los factores de riesgo. Entre los factores de riesgo se incluye si los usuarios han iniciado sesión desde el mismo dispositivo o desde una ubicación similar en el pasado. Después de autenticar al usuario, el servicio de identidad emite credenciales, como cookies y tokens de OAuth, que se pueden usar en llamadas posteriores.

Cuando los usuarios inician sesión, pueden usar segundos factores, como contraseñas de un solo uso o llaves de seguridad resistentes al phishing, como la [llave de seguridad Titan](https://cloud.google.com/titan-security-key?hl=es). La llave de seguridad Titan es un token físico compatible con el estándar [FIDO Universal 2nd Factor (U2F)](https://en.wikipedia.org/wiki/Universal_2nd_Factor). Hemos ayudado a desarrollar el estándar abierto U2F con FIDO Alliance. La mayoría de las plataformas y los navegadores web han adoptado este estándar de autenticación abierto.

Seguridad operativa
-------------------

En esta sección se describe cómo desarrollamos el software de infraestructura, protegemos los ordenadores y las credenciales de nuestros empleados, y nos defendemos de las amenazas a la infraestructura, tanto internas como externas.

### Desarrollo de software seguro

Además de las [protecciones de control de código fuente y el proceso de revisión por dos partes](#secure-service) que hemos descrito anteriormente, usamos bibliotecas que impiden que los desarrolladores introduzcan determinadas clases de errores de seguridad. Por ejemplo, tenemos bibliotecas y frameworks que ayudan a eliminar las vulnerabilidades de XSS en aplicaciones web. También usamos herramientas automatizadas, como fuzzers, herramientas de análisis estático y escáneres de seguridad web, para detectar automáticamente errores de seguridad.

Como comprobación final, realizamos revisiones de seguridad manuales que van desde triajes rápidos para funciones menos arriesgadas hasta revisiones de diseño e implementación en profundidad para las funciones más arriesgadas. El equipo que lleva a cabo estas revisiones incluye a expertos en seguridad web, criptografía y seguridad de sistemas operativos. Las revisiones pueden llevar al desarrollo de nuevas funciones de bibliotecas de seguridad y nuevos fuzzers que podemos usar en futuros productos.

Además, tenemos un [programa de recompensas por vulnerabilidades](https://www.google.com/about/appsecurity/reward-program/?hl=es) que ofrece recompensas a quienes descubran y nos informen de errores en nuestra infraestructura o aplicaciones. Para obtener más información sobre este programa, incluidas las recompensas que hemos dado, consulta [Estadísticas clave de los cazadores de errores](https://bughunters.google.com/about/key-stats?hl=es).

También invertimos en la búsqueda de vulnerabilidades de día cero y otros problemas de seguridad en el software de código abierto que usamos. Gestionamos [Project Zero](https://googleprojectzero.blogspot.com/), un equipo de investigadores de Google que se dedica a investigar vulnerabilidades de día cero, como [Spectre y Meltdown](https://googleprojectzero.blogspot.com/search?q=spectre). Además, somos la entidad que más CVEs y correcciones de errores de seguridad envía para el hipervisor KVM de Linux.

### Protecciones de código fuente

Nuestro código fuente se almacena en repositorios con integridad y gobernanza de origen integradas, donde se pueden auditar las versiones actuales y anteriores del servicio. La infraestructura requiere que los binarios de un servicio se compilen a partir de un código fuente específico después de que se haya revisado, verificado y probado. [Autorización binaria para Borg (BAB)](https://docs.cloud.google.com/docs/security/binary-authorization-for-borg?hl=es) es una comprobación interna del cumplimiento que se realiza cuando se despliega un servicio. BAB hace lo siguiente:

*   Asegura que el software y la configuración de producción que se despliegan en Google se revisen y autoricen, sobre todo si el código correspondiente puede acceder a datos de usuario.
*   Se asegura de que los despliegues de código y configuración cumplan una serie de estándares mínimos.
*   Limita la capacidad de un empleado interno o de un adversario para hacer modificaciones maliciosas en el código fuente y también proporciona un rastro forense de un servicio a su fuente.

### Proteger los dispositivos y las credenciales de los empleados

Implementamos medidas de protección para evitar que se vean comprometidos los dispositivos y las credenciales de nuestros empleados. Para proteger a nuestros empleados frente a intentos de phishing sofisticados, hemos sustituido la autenticación de segundo factor con contraseñas de un solo uso por el uso obligatorio de llaves de seguridad compatibles con U2F.

Monitorizamos los dispositivos cliente que usan nuestros empleados para operar nuestra infraestructura. Nos aseguramos de que las imágenes del sistema operativo de estos dispositivos estén actualizadas con los parches de seguridad y controlamos las aplicaciones que los empleados pueden instalar en sus dispositivos. También tenemos sistemas que analizan las aplicaciones, las descargas, las extensiones de navegador y el contenido del navegador web instalados por los usuarios para determinar si son adecuados para dispositivos corporativos.

Estar conectado a la LAN corporativa no es nuestro mecanismo principal para conceder privilegios de acceso. En su lugar, usamos la seguridad de confianza cero para proteger el acceso de los empleados a nuestros recursos. Los controles de gestión de acceso a nivel de aplicación solo permiten que los empleados accedan a las aplicaciones internas cuando utilizan un dispositivo gestionado y se conectan desde redes y ubicaciones geográficas esperadas. Un dispositivo cliente se considera de confianza en función de un certificado que se emite a la máquina individual y de las aserciones sobre su configuración (como el software actualizado). Para obtener más información, consulta [BeyondCorp](https://cloud.google.com/beyondcorp?hl=es).

### Reducción de riesgos internos

El _riesgo interno_ es la posibilidad de que un empleado, un contratista u otro colaborador actual o anterior que tenga o haya tenido acceso autorizado a nuestra red, sistema o datos haga un uso inadecuado de ese acceso para poner en riesgo la confidencialidad, la integridad o la disponibilidad de nuestra información o nuestros sistemas de información.

Para reducir los riesgos internos, limitamos y monitorizamos activamente las actividades de los empleados a los que se les ha concedido acceso de administrador a la infraestructura. Trabajamos continuamente para eliminar la necesidad de acceso privilegiado para determinadas tareas mediante la automatización, que puede llevar a cabo las mismas tareas de forma segura y controlada. Exponemos APIs limitadas que permiten depurar sin exponer datos sensibles y requerimos la aprobación de dos partes para determinadas acciones sensibles realizadas por operadores humanos.

El acceso de los empleados de Google a la información de los usuarios finales se puede registrar mediante hooks de infraestructura de bajo nivel. Nuestro equipo de seguridad monitoriza los patrones de acceso e investiga los eventos inusuales. Para obtener más información, consulta [Acceso privilegiado en Google Cloud](https://docs.cloud.google.com/docs/security/privileged-access-management?hl=es).

Usamos la [autorización binaria para Borg](https://docs.cloud.google.com/docs/security/binary-authorization-for-borg?hl=es) para proteger nuestra cadena de suministro frente a riesgos internos. Además, nuestra inversión en [BeyondProd](https://docs.cloud.google.com/docs/security/beyondprod?hl=es) nos ayuda a proteger los datos de los usuarios en la infraestructura de Google y a generar confianza en nuestros servicios.

En Google Cloud, puedes monitorizar el acceso a tus datos mediante [Transparencia de acceso](https://docs.cloud.google.com/assured-workloads/access-transparency/docs/overview?hl=es). Los registros de Transparencia de acceso te permiten verificar que el personal de Google accede a tu contenido únicamente por motivos comerciales válidos, como solucionar una interrupción o atender tus solicitudes de asistencia. [Aprobación de acceso](https://docs.cloud.google.com/assured-workloads/access-approval/docs/overview?hl=es) asegura que el equipo de Asistencia de Google Cloud y el de ingeniería necesiten tu aprobación explícita para acceder a tus datos. La aprobación se verifica mediante criptografía para asegurar la integridad de la aprobación de acceso.

Para obtener más información sobre las protecciones de los servicios de producción, consulta [Cómo protege Google sus servicios de producción](https://docs.cloud.google.com/docs/security/production-services-protection?hl=es).

### Monitorización de amenazas

El [grupo de análisis de amenazas](https://blog.google/threat-analysis-group/?hl=es) de Google monitoriza a los agentes de amenazas y la evolución de sus tácticas y técnicas. Los objetivos de este grupo son mejorar la seguridad de los productos de Google y compartir esta información para que la comunidad online se beneficie de ella.

Para ello, puedes usar [Google Cloud Threat Intelligence for Google Security Operations](https://chronicle.security/products/uppercase/) y [VirusTotal](https://support.virustotal.com/hc/en-us/categories/360000162878-Documentation) para monitorizar y responder a muchos tipos de malware. Google Cloud Threat Intelligence for Google Security Operations es un equipo de investigadores de amenazas que desarrolla inteligencia sobre amenazas para usarla con [Google Security Operations](https://docs.cloud.google.com/chronicle/docs/overview?hl=es). Google Cloud VirusTotal es una base de datos de malware y una solución de visualización que puedes usar para entender mejor cómo funciona el malware en tu empresa.

Para obtener más información sobre nuestras actividades de monitorización de amenazas, consulta el [informe Threat Horizons](https://cloud.google.com/blog/products/identity-security/coin-mining-ransomware-apts-target-cloud-gcat-report?hl=es).

### Detección de intrusos

Usamos sofisticados flujos de procesamiento de datos para integrar señales basadas en el host de dispositivos concretos, señales basadas en la red de varios puntos de monitorización de la infraestructura y señales de servicios de infraestructura. Las reglas y la inteligencia artificial basadas en estas canalizaciones envían a los ingenieros de seguridad operativa alertas sobre posibles incidentes. [Nuestros equipos de investigación y respuesta a incidentes](https://docs.cloud.google.com/docs/security/incident-response?hl=es) clasifican, investigan y responden a estos posibles incidentes las 24 horas del día, los 365 días del año. Realizamos ejercicios de [equipo rojo](https://en.wikipedia.org/wiki/Red_team) para medir y mejorar la eficacia de nuestros mecanismos de detección y respuesta.

Siguientes pasos
----------------

*   Para obtener más información sobre cómo protegemos nuestra infraestructura, consulta el libro [Building secure and reliable systems (O'Reilly)](https://www.oreilly.com/library/view/building-secure-and/9781492083115/).
*   Consulta más información sobre la [seguridad de los centros de datos](https://www.google.com/about/datacenters/data-security/?hl=es).
*   Consulta más información sobre cómo nos protegemos contra los [ataques DDoS](https://cloud.google.com/blog/products/identity-security/identifying-and-protecting-against-the-largest-ddos-attacks?hl=es).
    
*   Consulta información sobre nuestra solución de confianza cero, [BeyondCorp](https://cloud.google.com/beyondcorp?hl=es).
