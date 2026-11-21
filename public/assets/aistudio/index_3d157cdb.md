# Encriptado en tránsito de Google Cloud &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/encryption-in-transit?hl=es

_Este contenido se actualizó por última vez en abril del 2025 y representa la situación en el momento en que se redactó. Las políticas y los sistemas de seguridad de Google pueden cambiar en el futuro, ya que mejoramos constantemente la protección que proporcionamos a nuestros clientes._

En Google, nuestros controles de seguridad ayudan a proteger tus datos, tanto si se transfieren por Internet como si circulan por la infraestructura de Google o están almacenados en nuestros servidores. Los cimientos de nuestra estrategia de seguridad son la autenticación, la integridad y el cifrado de los datos, independientemente de si están en reposo o en tránsito. En este documento se describe cómo hemos diseñadoGoogle Cloud para cifrar los datos en tránsito desde Internet y los datos en tránsito en las redes de Google. Este documento no se aplica a los datos en tránsito a través de interconexiones entre las redes de centros de datos de los clientes y las redes de centros de datos de Google.

El cifrado en tránsito usa varias tecnologías para proteger los datos, como Seguridad en la capa de transporte (TLS), BoringSSL, Seguridad en la capa de transporte de la aplicación (ALTS) y el [protocolo de seguridad PSP](https://cloud.google.com/blog/products/identity-security/announcing-psp-security-protocol-is-now-open-source?hl=es). Además de la protección predeterminada que ofrece Google, puedes proteger aún más los datos añadiendo opciones de cifrado como IPsec, certificados TLS gestionados y Cloud Service Mesh.

Este documento está dirigido a los directores de seguridad de la información (CISO) y a los equipos de operaciones de seguridad que usen o se planteen utilizar Google Cloud. Se presupone que el lector tiene conocimientos básicos sobre [encriptado](https://en.wikipedia.org/wiki/Encryption) y [primitivas criptográficas](https://en.wikipedia.org/wiki/Cryptographic_primitive).

Autenticación, integridad y cifrado
-----------------------------------

Google cuenta con las siguientes medidas de seguridad para preservar la autenticidad, la integridad y la privacidad de los datos en tránsito:

*   La **autenticación** verifica la identidad de un peer (una persona o un proceso) en una conexión.
*   La **integridad** evita que los datos que envías se modifiquen durante el tránsito entre el origen y el destino.
*   El **cifrado** usa criptografía para que tus datos sean ilegibles durante la transferencia y para mantener su confidencialidad.

El cifrado en tránsito ayuda a proteger tus datos si se intercepta algún tipo de comunicación mientras se transfieren datos entre el usuario final y Google Cloud o entre dos servicios. El cifrado en tránsito autentica los endpoints y cifra los datos antes de la transmisión. Al llegar, el receptor descifra los datos y verifica que no se hayan modificado durante el tránsito.

El encriptado debe entenderse como un elemento más de los que componen cualquier estrategia de seguridad más amplia. El cifrado en tránsito protege tus datos frente a posibles atacantes y elimina la necesidad de que Google, los clientes de Google Cloud o los usuarios finales confíen en las capas inferiores de la red.

### ¿Cómo se enruta el tráfico?

En esta sección, descubrirás cómo se transfieren las solicitudes de los usuarios finales alGoogle Cloud servicio o la aplicación de cliente correspondiente y cómo se enruta el tráfico entre servicios.

Un _Google Cloud servicio_ es un servicio modular en la nube que ofrecemos a nuestros clientes. Estos servicios incluyen computación, almacenamiento de datos, analíticas de datos y aprendizaje automático. Por ejemplo, Cloud Storage es un Google Cloud servicio.

Una _aplicación de cliente_ es una aplicación alojada en Google Cloud que puedes crear y desplegar como cliente de Google mediante los Google Cloud servicios. Las aplicaciones de clientes o soluciones de partners que estén alojadas en Google Cloud no se considerarán Google Cloud servicios. Por ejemplo, una aplicación que crees con Cloud Run, Google Kubernetes Engine o una VM en Compute Engine es una aplicación de cliente.

En el siguiente diagrama se muestran las rutas del tráfico desde el usuario final hasta Google, las rutas dentro de la red de Google y la seguridad de cada conexión. Se muestran las siguientes rutas de tráfico:

*   Entre un usuario final en Internet y un servicio (etiquetado como A en el diagrama) Google Cloud
*   Entre el usuario final en Internet y una aplicación de cliente alojada enGoogle Cloud (etiquetada como B en el diagrama)
*   De máquina virtual a máquina virtual (etiquetada como C en el diagrama)
*   Entre una máquina virtual y Google Front End (GFE) (etiquetado como D en el diagrama)
*   GFE a APIs y servicios de Google (etiquetado como E en el diagrama)
*   Google Cloud servicio a Google Cloud servicio (etiquetado como F en el diagrama)

Cifrado en tránsito entre el usuario final y Google
---------------------------------------------------

En las siguientes secciones se ofrece más información sobre las solicitudes de enrutamiento de los usuarios finales que se muestran en el diagrama anterior.

### Entre el usuario final y un Google Cloud servicio

LosGoogle Cloud servicios, como Cloud Storage o Compute Engine, son servicios en la nube que ofrecemos a los clientes y que se ejecutan en nuestro [entorno de producción](https://docs.cloud.google.com/docs/security/production-services-protection?hl=es). Google Cloud Los servicios aceptan solicitudes de todo el mundo mediante un sistema distribuido globalmente llamado [Google Front End (GFE)](https://docs.cloud.google.com/docs/security/infrastructure/design?hl=es#google-frontend-service). GFE cancela el tráfico de las conexiones HTTP(S), TCP y TLS entrantes, proporciona medidas de respuesta a los ataques DDoS y enruta y equilibra la carga de tráfico que reciben los propios servicios deGoogle Cloud . Hay puntos de presencia de GFE en todo el mundo, y las rutas se anuncian mediante unicast o [Anycast](https://tools.ietf.org/html/rfc1546).

GFE enruta el tráfico de un usuario final a través de la red de Google hasta unGoogle Cloud servicio Google Cloud y de un usuario final a una aplicación de cliente alojada en Google Cloud y que usa [Cloud Load Balancing](https://docs.cloud.google.com/load-balancing/docs/load-balancing-overview?hl=es).

Las solicitudes que los clientes envían a un Google Cloud servicio a través de HTTPS, HTTP/2 o HTTP/3 están protegidas con TLS. TLS se implementa en GFE mediante [BoringSSL](https://boringssl.googlesource.com/boringssl), una implementación de software libre del protocolo TLS mantenida por Google. BoringCrypto, el núcleo de BoringSSL, se ha validado según el estándar [FIPS 140-3 de nivel 1](https://csrc.nist.gov/projects/cryptographic-module-validation-program/certificate/4735).

El GFE negocia parámetros criptográficos estándar del sector con el cliente, incluida la negociación de claves con seguridad directa. En el caso de los clientes más antiguos y con menos funciones, GFE elige las primitivas criptográficas antiguas más seguras que ofrece el cliente.

### Entre el usuario final y una aplicación de cliente alojada en Google Cloud

Puedes enrutar el tráfico de los usuarios finales desde Internet a tus aplicaciones de cliente alojadas en Google Cloud mediante una conexión directa o a través de un balanceador de carga. Cuando el tráfico se enruta directamente, se dirige a la dirección IP externa del servidor de la VM que aloja la aplicación.

Puedes usar TLS con el balanceador de carga de aplicación externo o el balanceador de carga de red de proxy externo para conectarte a tu aplicación que se ejecuta en Google Cloud. Cuando usas un balanceador de carga de capa 7, la conexión entre el usuario final y el balanceador de carga se cifra mediante TLS de forma predeterminada (siempre que la aplicación cliente del usuario final admita TLS). GFE cancela las conexiones TLS de los usuarios finales mediante certificados TLS autogestionados o gestionados por Google. Para obtener más información sobre cómo personalizar el certificado, consulta el [artículo sobre certificados SSL](https://docs.cloud.google.com/compute/docs/load-balancing/http/ssl-certificates?hl=es). Para habilitar el cifrado entre el balanceador de carga y la VM que aloja la aplicación del cliente, consulta [Cifrado del balanceador de carga a los backends](https://docs.cloud.google.com/load-balancing/docs/ssl-certificates/encryption-to-the-backends?hl=es).

Para configurar TLS mutuo (mTLS), consulta la [descripción general de TLS mutuo](https://docs.cloud.google.com/load-balancing/docs/mtls?hl=es). En el caso de las cargas de trabajo de GKE y Compute Engine, considera la posibilidad de usar [Cloud Service Mesh](https://docs.cloud.google.com/service-mesh/docs/security/security-overview?hl=es) para usar mTLS en la autenticación mutua (cliente y servidor) y cifrar las comunicaciones en tránsito con los certificados que gestionas.

En el caso de los [dominios personalizados de Firebase Hosting](https://firebase.google.com/docs/hosting/custom-domain?hl=es) y [Cloud Run](https://docs.cloud.google.com/run/docs/mapping-custom-domains?hl=es), puedes usar nuestros certificados TLS gratuitos y automatizados. Con los dominios personalizados de Cloud Run, también puedes [proporcionar tus propios certificados SSL](https://docs.cloud.google.com/load-balancing/docs/https/setup-global-ext-https-serverless?hl=es) y utilizar un encabezado de protocolo de seguridad de transporte estricta mediante HTTP (HSTS).

Google Cloud cifra los datos de los clientes en tránsito dentro de las redes de Google, a menos que se indique lo contrario en esta sección.

Las interconexiones especializadas de rendimiento ultraalto que conectan las TPUs o las GPUs de los superordenadores de IA de Google son independientes de las redes descritas en este documento. En Google Cloud, estas interconexiones de rendimiento ultraalto son ICI para las TPUs y NVLink para las GPUs. Para obtener más información, consulta la [arquitectura de las TPU](https://docs.cloud.google.com/tpu/docs/system-architecture-tpu-vm?hl=es) y los [tipos de máquinas con GPU](https://docs.cloud.google.com/compute/docs/gpus?hl=es).

El tráfico a través de conexiones a máquinas virtuales que usan direcciones IP externas sale de las redes de Google. Eres responsable de configurar tu propio cifrado para esas conexiones.

### Google Cloud Autenticación y cifrado de la red virtual

La red virtual deGoogle Cloudcifra, protege la integridad y autentica el tráfico entre máquinas virtuales.

El encriptado del tráfico de IP privada en la misma VPC o en redes de VPC emparejadas dentro de la red virtual de Google Cloudse lleva a cabo en la capa de red.

Cada par de hosts de comunicación establece una clave de sesión mediante un canal de control protegido por [ALTS](#service-to-service-authentication) para las comunicaciones autenticadas, encriptadas y protegidas contra alteraciones. La clave de sesión se utiliza para cifrar la comunicación de máquina virtual a máquina virtual entre esos hosts, y las claves de sesión se rotan de forma periódica.

Las conexiones entre máquinas virtuales dentro de las redes de VPC y las redes de VPC emparejadas dentro de la red de producción de Google están protegidas con integridad y encriptadas. Estas conexiones incluyen las conexiones entre las máquinas virtuales de los clientes y entre las máquinas virtuales de los clientes y las gestionadas por Google, como Cloud SQL. En el diagrama de [Cómo se enruta el tráfico](#how-traffic) se muestra esta interacción (conexión C). Ten en cuenta que, como Google activa el cifrado automático en función del uso de direcciones IP internas, las conexiones entre máquinas virtuales que usan direcciones IP externas no se cifran automáticamente.

La red virtual deGoogle Cloudautentica todo el tráfico entre máquinas virtuales. Esta autenticación, que se consigue mediante tokens de seguridad, evita que un host comprometido falsifique paquetes en la red.

Los tokens de seguridad se encapsulan en un encabezado de túnel que incluye información de autenticación sobre el emisor y el receptor. El plano de control del host de envío configura el token, mientras que el host de recepción lo valida. Los tokens de seguridad, compuestos por un token con la información del emisor y por el secreto del host, se generan previamente para cada flujo.

### Conectividad con servicios y APIs de Google

La gestión del tráfico difiere según dónde se aloje el Google Cloud servicio.

La mayoría de los servicios y de las APIs de Google se alojan en GFEs. El tráfico de una máquina virtual a GFE usa direcciones IP externas para llegar a los servicios, pero puedes configurar el [acceso privado](https://docs.cloud.google.com/vpc/docs/private-access-options?hl=es) para no usar direcciones IP externas. Google Cloud La conexión del GFE al servicio se autentica y se cifra.

Algunos servicios, como [Cloud SQL](https://docs.cloud.google.com/sql/docs/mysql/introduction?hl=es), se alojan en instancias de VM gestionadas por Google. Si las VMs de los clientes acceden a los servicios alojados en instancias de VM gestionadas por Google mediante direcciones IP externas, el tráfico permanece en la red de producción de Google, pero no se cifra automáticamente debido al uso de las direcciones IP externas. Para obtener más información, consulta el artículo sobre la [Google Cloud autenticación y el cifrado de redes virtuales](#gcp-virtual).

Cuando un usuario envía una solicitud a un Google Cloud servicio, protegemos los datos en tránsito (proporcionando autenticación, integridad y cifrado) mediante HTTPS con un certificado de una autoridad de certificación web pública. Todos los datos que el usuario envía al GFE se cifran en tránsito con TLS o QUIC. El GFE negocia un protocolo de cifrado concreto con el cliente según su compatibilidad. aunque, en la medida de lo posible, intenta utilizar protocolos de encriptado más modernos.

### Autenticación, integridad y encriptado entre servicios

La infraestructura de Google usa ALTS para la autenticación, la integridad y el encriptado de las conexiones desde el GFE a un Google Cloud servicio y de un Google Cloud servicio a otro Google Cloud servicio.

ALTS usa [identidades basadas en servicios](https://docs.cloud.google.com/docs/security/binary-authorization-for-borg?hl=es#step_4_service-based_identity) para la autenticación. Los servicios que se ejecutan en el entorno de producción de Google reciben credenciales que afirman sus identidades basadas en servicios. Al hacer o recibir una RPC de otros servicios, el servicio utiliza sus credenciales para autenticarse. ALTS verifica que estas credenciales las haya emitido la CA interna de Google. La CA interna de Google no está relacionada con el [Servicio de Autoridades de Certificación](https://cloud.google.com/security/products/certificate-authority-service?hl=es) y es independiente de él.

ALTS usa el encriptado y la protección de integridad criptográfica para el tráfico que transporta datos desde el GFE a un servicio y entre servicios que se ejecutan en el entorno de producción de Google. Google Cloud

ALTS también se usa para encapsular otros protocolos de capa 7, como HTTP, en mecanismos de RPC para el tráfico entre los GFE y los servicios de Google Cloud. Esta protección ayuda a aislar la capa de la aplicación y elimina cualquier dependencia en la seguridad de la ruta de red.

Métodos de cifrado en tránsito
------------------------------

En las siguientes secciones se describen algunas de las tecnologías que usa Google para cifrar los datos en tránsito.

### Cifrado de red mediante PSP

PSP es un protocolo independiente del transporte que permite la seguridad por conexión y admite la descarga del cifrado en hardware de tarjeta de interfaz de red inteligente (SmartNIC). Cuando las SmartNICs están disponibles, ALTS usa PSP para cifrar los datos en tránsito en nuestra red.

PSP se ha diseñado para cumplir los requisitos del tráfico de centros de datos a gran escala. La infraestructura de Google usa PSP para cifrar el tráfico dentro de nuestros centros de datos y entre ellos. PSP también admite protocolos que no son TCP, como UDP, y usa una clave de cifrado única para cada conexión.

### Seguridad de transporte en la capa de la aplicación

ALTS es un sistema de autenticación mutua y encriptado desarrollado por Google. ALTS proporciona autenticación, confidencialidad e integridad a los datos en tránsito entre los servicios que se ejecutan en el entorno de producción de Google. ALTS consta de los siguientes protocolos:

*   **Protocolo de handshake:** el cliente inicia un intercambio de claves combinado de curva elíptica y seguro frente a ataques cuánticos. Al final del handshake, los servicios implicados autentican las identidades de los demás intercambiando y verificando certificados firmados, y calculan una clave de tráfico compartida. Entre los algoritmos admitidos para derivar la clave de tráfico compartida se encuentra el algoritmo poscuántico [ML-KEM (FIPS 203)](https://csrc.nist.gov/pubs/fips/203/final) del NIST. Para obtener más información, consulta [Criptografía poscuántica](https://cloud.google.com/security/resources/post-quantum-cryptography?hl=es).
    
*   **Protocolo de registro:** los datos de servicio a servicio se cifran en tránsito mediante la clave de tráfico compartida. De forma predeterminada, ALTS usa el cifrado AES-128-GCM para todo el tráfico. Los datos en tránsito en el sistema de almacenamiento de nivel más bajo de Google usan el cifrado AES-256, y ALTS proporciona autenticación de mensajes AES. El encriptado en ALTS se proporciona mediante BoringSSL o PSP. Este cifrado se valida en el nivel 1 de FIPS 140-2 o en el nivel 1 de FIPS 140-3.
    

Las claves de firma raíz de los certificados ALTS se almacenan en la CA interna de Google.

Siguientes pasos
----------------

*   Para obtener más información sobre la seguridad de nuestros centros de datos, consulta el artículo [Seguridad de los centros de datos](https://www.google.com/about/datacenters/data-security/?hl=es).
*   Para obtener información sobre las opciones de configuración de seguridad de las interconexiones entreGoogle Cloud y las redes de centros de datos de clientes, consulta [Elegir un producto de conectividad de red (IPSec)](https://docs.cloud.google.com/network-connectivity/docs/how-to/choose-product?hl=es) y [Información general sobre MACsec para Cloud Interconnect](https://docs.cloud.google.com/network-connectivity/docs/interconnect/concepts/macsec-overview?hl=es).
*   Para obtener información sobre el Google Cloud cumplimiento y las certificaciones de cumplimiento, consulta el [centro de recursos para el cumplimiento](https://cloud.google.com/compliance?hl=es), donde se incluye nuestro [informe de auditoría SOC 3](https://www.google.com/work/soc3.html?hl=es).
*   Para consultar las prácticas recomendadas sobre cómo proteger tus datos en tránsito, consulta el [blueprint de fundamentos para empresas](https://docs.cloud.google.com/architecture/security-foundations/networking?hl=es), [Google Cloud Architecture Framework: Security, privacy, and compliance](https://docs.cloud.google.com/architecture/framework/security?hl=es) y [Decide how to meet regulatory requirements for encryption in transit](https://docs.cloud.google.com/architecture/landing-zones/decide-security?hl=es#encrypt-transit).
