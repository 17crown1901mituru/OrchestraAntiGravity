# IA generativa &nbsp;|&nbsp; AI and ML &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/ai-ml/generative-ai?hl=es

Elige modelos y la infraestructura para tu aplicación de IA generativa    
--------------------------------------------------------------------------

Descubre qué productos, frameworks y herramientas son la mejor opción para compilar tu aplicación de IA generativa. En la siguiente imagen, se muestran los componentes comunes de una aplicación de IA generativa alojada en Cloud.

1.  **Alojamiento de aplicaciones:** Procesamiento para alojar tu aplicación. Tu aplicación puede usar [las bibliotecas cliente y los SDK de Google Cloud](https://docs.cloud.google.com/apis/docs/cloud-client-libraries?hl=es) para comunicarse con diferentes productos de Cloud.
    
2.  **Alojamiento de modelos:** Es un alojamiento escalable y seguro para un modelo generativo.
    
3.  **Modelo:** Modelo generativo para texto, chat, imágenes, código, incorporaciones y [multimodal](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/learn/models?hl=es).
    
4.  **[Solución de anclaje](https://docs.cloud.google.com/vertex-ai/docs/generative-ai/grounding/overview?hl=es):** Anclar el resultado del modelo a fuentes de información verificables y actualizadas.
    
5.  **Base de datos**: Almacena los datos de tu aplicación. Puedes reutilizar tu base de datos existente como tu solución de fundamentación, a través del aumento de las instrucciones con una consulta en SQL o el almacenamiento de tus datos como embeddings de vectores con una extensión como [pgvector](https://cloud.google.com/blog/products/databases/announcing-vector-support-in-postgresql-services-to-power-ai-enabled-applications?hl=es).
    
6.  **Almacenamiento:** Almacena archivos, como imágenes, videos o [frontends web estáticos](https://docs.cloud.google.com/storage/docs/hosting-static-website?hl=es). También puedes usar Storage para los datos de puesta a tierra sin procesar (p. ej., PDF) que luego conviertes en embeddings y almacenas en una base de datos vectorial.
    

En las siguientes secciones, se explica cada uno de esos componentes para ayudarte a elegir qué productos de Google Cloud probar.

### Infraestructura de hosting de aplicaciones

Elige un producto para alojar y entregar la carga de trabajo de tu aplicación, que realiza llamadas al modelo generativo.

¿Quieres una infraestructura sin servidores administrada?

[Cloud Run](https://docs.cloud.google.com/run/docs/overview/what-is-cloud-run?hl=es)

### Infraestructura de hosting de modelos

Google Cloud proporciona múltiples formas de alojar un modelo generativo, desde la plataforma insignia de Vertex AI, hasta hosting personalizable y portátil en Google Kubernetes Engine.

¿Quieres una infraestructura completamente administrada con herramientas y APIs de IA generativa de primer nivel?

[Vertex AI](https://docs.cloud.google.com/vertex-ai/docs/generative-ai/learn/overview?hl=es)

¿Tu modelo requiere un kernel especializado, un SO heredado o tiene condiciones de licencia especiales?

[Compute Engine](https://docs.cloud.google.com/compute/docs/gpus/about-gpus?hl=es)

### Fundamentación y RAG

Para garantizar respuestas del modelo fundamentadas y precisas, **[fundamenta](https://docs.cloud.google.com/vertex-ai/docs/generative-ai/grounding/overview?hl=es)** tu aplicación de IA generativa con [datos en tiempo real](https://cloud.google.com/blog/products/ai-machine-learning/how-to-use-grounding-for-your-llms-with-text-embeddings?hl=es). Esto se denomina [generación mejorada por recuperación (RAG)](https://cloud.google.com/use-cases/retrieval-augmented-generation?hl=es).

Si deseas generar contenido basado en información actualizada de Internet, los modelos de Gemini pueden evaluar si el conocimiento del modelo es suficiente o si se requiere la fundamentación con la Búsqueda de Google.

Puedes implementar la conexión a tierra con un índice de tus datos con un motor de búsqueda. Muchos motores de búsqueda ahora almacenan incorporaciones en una [base de datos de vectores](https://docs.cloud.google.com/discover/what-is-a-vector-database?hl=es), que es un formato óptimo para [operaciones como la búsqueda de similitud](https://cloud.google.com/blog/products/ai-machine-learning/how-to-use-grounding-for-your-llms-with-text-embeddings?hl=es). Google Cloud ofrece varias soluciones de bases de datos de vectores para diferentes casos de uso.

Nota: Puedes fundamentar con bases de datos no vectoriales. Para ello, consulta una base de datos existente, como Cloud SQL o Firestore, y usa el resultado de la consulta en la instrucción del modelo.

¿Quieres una solución optimizada y completamente administrada que admita la mayoría de las fuentes de datos y evite el acceso directo a las incorporaciones subyacentes?

[Vertex AI Search](https://docs.cloud.google.com/generative-ai-app-builder/docs/try-enterprise-search?hl=es)

cerrar

Estás creando un motor de búsqueda para RAG

¿Quieres compilar un motor de búsqueda para RAG con un orquestador administrado con una interfaz similar a LlamaIndex?

[Motor de RAG de Vertex AI](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/rag-overview?hl=es)

cerrar

Puedes usar una [arquitectura de referencia](https://docs.cloud.google.com/architecture/ai-ml?hl=es) para crear un motor de búsqueda personalizado y una base de datos vectorial para casos de uso de RAG.

¿Necesitas una búsqueda de vectores de baja latencia, una publicación a gran escala o una base de datos de vectores especializada y optimizada?

[Vector Search de Vertex AI](https://docs.cloud.google.com/vertex-ai/docs/vector-search/overview?hl=es)

¿Se accede a tus datos de forma programática (OLTP)? ¿Ya usas una base de datos SQL?

¿Quieres usar modelos de Google AI directamente desde tu base de datos? ¿Necesitas una latencia baja?

¿Tienes un gran conjunto de datos analíticos (OLAP)? ¿Requiere procesamiento por lotes y acceso frecuente a tablas de SQL por parte de humanos o secuencias de comandos (ciencia de datos)?

[BigQuery](https://cloud.google.com/blog/products/ai-machine-learning/open-source-framework-for-connecting-llms-to-your-data?hl=es)

Fundamentos con APIs
--------------------

En lugar de (o además) usar tus propios datos para los fundamentos, muchos servicios en línea ofrecen APIs que puedes usar para recuperar datos de los fundamentos para mejorar la instrucción de tu modelo.

Comienza con la compilación
---------------------------
