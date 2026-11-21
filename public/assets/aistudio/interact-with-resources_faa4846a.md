# Formas de interactuar con Google Cloud &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/interact-with-resources

Google Cloud proporciona una amplia variedad de interfaces y herramientas para ayudarte a aumentar la productividad. Ya sea que prefieras una interfaz gráfica de apuntar y hacer clic, un entorno de desarrollo alojado en la nube o una línea de comandos asistida por IA, puedes elegir la ruta que mejor se adapte a tu flujo de trabajo. Combina estas interfaces y herramientas para crear canalizaciones de desarrollo, implementación y supervisión.

Interfaces gráficas
-------------------

Usa interfaces gráficas para la administración visual de recursos, las tareas administrativas y las descripciones generales de alto nivel de tu presencia en la nube.

### Google Cloud Consola de

Si prefieres administrar tus Google Cloud proyectos y recursos a través de una interfaz gráfica de usuario, usa la consola basada en la Web. [Google Cloud consola](https://console.cloud.google.com/?hl=es-419). Sirve como tu centro principal para lo siguiente:

*   **Creación y organización de recursos**: Implementa, escala y supervisa la infraestructura de la nube.
*   **Administración y gobernanza**: Administra las políticas de Identity and Access Management (IAM) , configura las cuentas de facturación y audita la actividad del sistema.
*   **Administración y visualización de datos**: Almacena, consulta y procesa datos en bases de datos y servicios de estadísticas.
*   **Asistencia integrada**: Usa funciones asistidas por IA para hacer preguntas sobre tu arquitectura o solucionar problemas a medida que aparecen.

Para garantizar la funcionalidad adecuada de la consola, consulta [Permite el acceso a los Google Cloud dominios de la consola](https://docs.cloud.google.com/docs/get-started/required-domains?hl=es-419).

### Google Cloud App para dispositivos móviles

La [Google Cloud app para dispositivos móviles](https://cloud.google.com/app?hl=es-419), disponible en [iOS](https://itunes.apple.com/us/app/google-cloud-console/id1005120814) y [Android](https://play.google.com/store/apps/details?id=com.google.android.apps.cloudconsole&hl=es-419), es un complemento móvil de la consola basada en la Web Google Cloud . Úsala para supervisar servicios, responder a incidentes y realizar la administración básica de recursos directamente desde un dispositivo móvil.

Entornos de desarrollo
----------------------

Los entornos de desarrollo ofrecen espacios preconfigurados, contextuales y, a menudo, alojados en la nube para compilar y administrar tus aplicaciones.

### Cloud Shell (y el editor de Cloud Shell)

[Cloud Shell](https://docs.cloud.google.com/shell/docs?hl=es-419) proporciona un entorno de línea de comandos basado en la Web que incluye Google Cloud CLI y otras herramientas de línea de comandos. Usa Cloud Shell como una terminal interactiva basada en la Web para interactuar con Google Cloud sin instalar nada de forma local.

Cloud Shell también incluye el [editor de Cloud Shell](https://docs.cloud.google.com/shell/docs/editor-overview?hl=es-419), un editor de código integrado que te permite explorar directorios de archivos, ver y editar archivos, y mantener el acceso continuo a Cloud Shell. El editor de Cloud Shell está disponible de forma predeterminada con cada instancia de Cloud Shell y se basa en [Code OSS](https://github.com/microsoft/vscode).

### Antigravity 2.0

Para la organización de agentes, [Antigravity 2.0](https://antigravity.google/docs/get-started?hl=es-419) proporciona un entorno de GUI para administrar subagentes autónomos paralelos, ejecutar tareas programadas y organizar flujos de trabajo en el editor, la terminal y el navegador.

### Cloud Workstations

[Cloud Workstations](https://docs.cloud.google.com/workstations/docs/overview?hl=es-419) proporciona entornos de desarrollo administrados y personalizables en Google Cloud. Los equipos de la plataforma pueden usar Cloud Workstations para proporcionar a los desarrolladores IDE estandarizados y alojados en contenedores (como IntelliJ IDEA o VS Code). Este enfoque ayuda a garantizar una seguridad coherente en toda la organización, ya que mantiene los entornos de desarrollo dentro de tu red de nube privada virtual (VPC).

### Cloud Code

[Cloud Code](https://docs.cloud.google.com/code/docs?hl=es-419) extiende los entornos de desarrollo integrados (IDE) compatibles, incluidos VS Code, IntelliJ IDEA y el editor de Cloud Shell, para ayudarte a compilar e implementar aplicaciones en Google Cloud. Usa Cloud Code para la administración de clústeres de Google Kubernetes Engine, la implementación directa en Cloud Run y la asistencia de código potenciada por Gemini dentro de tu IDE.

Las herramientas para desarrolladores incluyen interfaces de línea de comandos (CLI), bibliotecas cliente programáticas y utilidades que permiten prácticas modernas de ingeniería de software.

### SDK de Google Cloud

El [SDK de Google Cloud](https://docs.cloud.google.com/sdk/docs/overview?hl=es-419) proporciona acceso programático a Google Cloud través de herramientas de línea de comandos y bibliotecas cliente:

*   **Bibliotecas cliente de Cloud**: Usa bibliotecas idiomáticas (como Python, Go, y Java) para llamar a las Google Cloud APIs directamente dentro del código de tu aplicación. Para obtener instrucciones centralizadas de instalación y configuración de biblioteca cliente, consulta [Bibliotecas cliente de Cloud](https://docs.cloud.google.com/apis/docs/cloud-client-libraries?hl=es-419).
*   **Google Cloud CLI**: Usa la herramienta de línea de comandos principal para administrar y configurar Google Cloud recursos, crear secuencias de comandos y automatizar canalizaciones de CI/CD. Para obtener instrucciones centralizadas de instalación y configuración, consulta [Instala Google Cloud CLI](https://docs.cloud.google.com/sdk/docs/install-sdk?hl=es-419).

### Herramientas de línea de comandos potenciadas por IA

Las interfaces de línea de comandos potenciadas por IA te permiten administrar Google Cloud recursos y desarrollar aplicaciones con lenguaje natural. Por ejemplo, la [CLI de Antigravity](https://antigravity.google/docs/cli?hl=es-419) es una herramienta de línea de comandos de agente que admite codificación, generación de código, investigación, administración de tareas y organización de la nube.

### Servidores y agentes de MCP

El [Protocolo de contexto del modelo (MCP)](https://docs.cloud.google.com/discover/what-is-model-context-protocol?hl=es-419) es un estándar abierto que actúa como un puente entre los modelos de IA y tus fuentes de datos o herramientas. Conecta tus aplicaciones de IA (como el IDE de Antigravity, VS Code o Cursor) a servidores de MCP remotos para recopilar más contexto, fundamentar las respuestas en datos del producto o realizar tareas específicas:

*   **Servidor de MCP de Developer Knowledge**: Proporciona a tus herramientas de IA acceso directo a la documentación Google Cloud y las prácticas recomendadas más recientes. Para obtener más información, consulta [Conéctate al servidor de MCP de Developer Knowledge](https://developers.google.com/knowledge/mcp?hl=es-419).
*   **Google Cloud \*\*Servidores de MCP remotos\*\***: Permite que los modelos de lenguaje grandes (LLM) usen Google y Google Cloud servicios en tus aplicaciones de IA a través de servidores de MCP remotos y herramientas específicas del producto. Para obtener una descripción general de la arquitectura y las capacidades de MCP, consulta [Google Cloud Descripción general de los servidores de MCP](https://docs.cloud.google.com/mcp/overview?hl=es-419).
*   **Agent Registry**: Descubre, reutiliza y controla agentes de IA autónomos y herramientas de MCP en toda tu organización con [Agent Registry](https://docs.cloud.google.com/agent-registry/overview?hl=es-419) como un catálogo centralizado.

#### Descubre servidores de MCP remotos y habilidades de agente

Google Cloud ofrece servidores de MCP remotos para un subconjunto cada vez mayor de productos. Estos servidores remotos se ejecutan en la infraestructura de Google y proporcionan extremos HTTP para tus aplicaciones de IA. Para descubrir si existen servidores de MCP remotos y habilidades de agente para un producto determinado, usa los siguientes métodos:

*   **Revisa los productos compatibles**: Para verificar si un Google Cloud producto ofrece un servidor de MCP remoto, conjuntos de herramientas o documentación de referencia, consulta la tabla centralizada [de productos compatibles](https://docs.cloud.google.com/mcp/supported-products?hl=es-419). En esta página, se proporcionan los extremos directos del servidor de MCP, la documentación de referencia de MCP y las guías del usuario para cada servicio compatible.
*   **Descubre las capacidades de forma programática**: Una vez que se configura un servidor de MCP remoto para tu proyecto o se registra en [Agent Registry](https://docs.cloud.google.com/agent-registry/overview?hl=es-419), las aplicaciones de IA pueden descubrir de forma programática las capacidades del servidor, como las herramientas, las instrucciones y los recursos de datos disponibles, mediante los métodos de descubrimiento de MCP estándar (por ejemplo, `tools/list`, `prompts/list`, y `resources/list`) o mediante la consulta a la API de Agent Registry.
*   **Explora el [repositorio de habilidades de agente de](https://github.com/google/skills) Google en GitHub**: Este repositorio contiene habilidades de agente para Google productos y tecnologías, incluidos Google Cloud.

### Herramientas de infraestructura como código (IaC)

Para los equipos que practican DevOps, Google Cloud admite herramientas de [infraestructura como código](https://docs.cloud.google.com/docs/iac?hl=es-419) (IaC) estándar de la industria, como Terraform, Pulumi y Config Connector, para administrar la infraestructura a través de archivos de configuración declarativos.

El uso de IaC te permite almacenar las definiciones de tu infraestructura en el control de origen, lo que permite implementaciones repetibles, pruebas automatizadas y registro de auditoría como parte de tu proceso de administración de cambios.

Asistencia de IA integrada
--------------------------

Google Cloud también incluye la integración directa de IA en los flujos de trabajo de los desarrolladores para obtener conocimiento y asistencia contextuales.

### Gemini para Google Cloud (Web y dispositivos móviles)

Gemini para Google Cloud ofrece asistencia potenciada por IA generativa a los Google Cloud usuarios, incluidos desarrolladores y científicos de datos. Gemini para Google Cloud está incorporado en muchos Google Cloud productos y proporciona una experiencia de asistencia integrada con el contexto de tu proyecto específico:

*   **En la Google Cloud consola**: Usa la barra lateral integrada de Gemini Cloud Assist para hacer preguntas en lenguaje natural sobre tu entorno (por ejemplo, "¿Por qué mi clúster de GKE muestra una tasa de errores alta?") o para generar SQL complejo de BigQuery a partir de una instrucción.
*   **En la Google Cloud app para dispositivos móviles**: Usa interfaces de voz y chat para supervisar incidentes y obtener resúmenes de solución de problemas basados en IA mientras estás lejos de tu escritorio.
*   **En entornos de desarrollo**: Usa **Gemini Code Assist** en tu IDE para escribir, refactorizar y documentar el código de la aplicación.
*   **Para la administración del ciclo de vida de la infraestructura**: Usa **Gemini Cloud Assist** para diseñar, implementar y optimizar tus recursos de la nube.

Para explorar más de lo que ofrece Gemini para Google Cloud , consulta [Descripción general de Gemini para Google Cloud](https://docs.cloud.google.com/gemini/docs/overview?hl=es-419) .
