# Cuándo usar la IA generativa o la IA tradicional &nbsp;|&nbsp; AI and ML &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/ai-ml/generative-ai/generative-ai-or-traditional-ai?hl=es

[Ir al contenido principal](#main-content)

*   [Documentation](https://docs.cloud.google.com/docs)
    *   [Presentación](https://docs.cloud.google.com/docs/ai-ml)
    *   [Guías](https://docs.cloud.google.com/docs/ai-ml/generative-ai/generative-ai-or-traditional-ai)
*   [Consola](https://console.cloud.google.com/)

*   AI and ML products
    
*   [Gemini Enterprise Agent Platform](https://docs.cloud.google.com/gemini-enterprise-agent-platform)
*   [Gemini Enterprise](https://docs.cloud.google.com/gemini/enterprise/docs)
*   [Model Garden](https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/model-garden/explore-models)
*   Agents
    
    *   [Agent Development Kit (ADK)](https://docs.cloud.google.com/gemini-enterprise-agent-platform/build/adk)
    *   [Agent Gateway](https://docs.cloud.google.com/gemini-enterprise-agent-platform/govern/gateways/agent-gateway-overview)
    *   [Agent Registry](https://docs.cloud.google.com/gemini-enterprise-agent-platform/govern/agent-registry)
    *   [Agent Runtime](https://docs.cloud.google.com/gemini-enterprise-agent-platform/build/runtime)
    *   [Agent Studio](https://docs.cloud.google.com/gemini-enterprise-agent-platform/agent-studio)
    

*   Model development
    
    *   [Agent Platform Managed Training](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/training/overview)
    *   [Cloud TPU](https://docs.cloud.google.com/tpu/docs)
    *   [Deep Learning Containers](https://docs.cloud.google.com/deep-learning-containers/docs)
    *   [Deep Learning VM Images](https://docs.cloud.google.com/deep-learning-vm/docs)
    *   [Experiments on Gemini Enterprise Agent Platform](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/experiments/intro-vertex-ai-experiments)
    *   [Neural Architecture Search](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/training/neural-architecture-search/overview)
    *   [Ray on Gemini Enterprise Agent Platform](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/open-source/ray)
    
*   Deploy and serve
    
    *   [AI/ML orchestration on Cloud Run](https://docs.cloud.google.com/run/docs/ai)
    *   [AI/ML orchestration on GKE](https://docs.cloud.google.com/kubernetes-engine/docs/integrations/ai-infra)
    *   [Agent Platform Pipelines](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/pipelines/introduction)
    *   [Agent Platform Inference](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/predictions/get-predictions)
    *   [Feature Store](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/featurestore/latest/overview)
    *   [Model Monitoring](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/model-monitoring/overview)
    *   [Model Registry](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/model-registry/introduction)
    *   [Vertex AI TensorBoard](https://docs.cloud.google.com/gemini-enterprise-agent-platform/machine-learning/experiments/tensorboard-introduction)
    

*   [See additional products on overview page](https://docs.cloud.google.com/docs/ai-ml#related-products,-guides,-and-sites)

Este documento te ayudará a identificar cuándo la IA generativa, la IA tradicional o una combinación de ambas pueden adaptarse a tu caso práctico empresarial.

En este documento, la _IA tradicional_ hace referencia a las funciones y los casos prácticos de la IA que no requieren el uso de la IA generativa, como algunos casos prácticos de [clasificación](https://developers.google.com/machine-learning/crash-course/classification/video-lecture?hl=es) y [predicción](https://cloud.google.com/learn/what-is-predictive-analytics?hl=es). Los modelos de IA tradicionales destacan por su capacidad para aprender de los datos disponibles y clasificar información o predecir resultados futuros en función de patrones históricos. Los modelos de IA generativa amplían estas funciones para crear resúmenes, descubrir correlaciones ocultas complejas o generar contenido nuevo (como texto, imágenes o vídeos) que refleje el estilo y los patrones de los datos de entrenamiento.

Cuándo usar la IA generativa
----------------------------

En general, las soluciones de IA generativa destacan en tareas como las siguientes:

*   Crear y recomendar contenido.
*   Potencia la búsqueda conversacional y los chatbots.
*   Escalar y automatizar el flujo de trabajo para tareas repetitivas.
*   Usar el razonamiento asociativo para encontrar información valiosa y relaciones en documentos y datos.
*   Generar código y ayudar a los desarrolladores a escribir, explicar y documentar código.

En las siguientes secciones se muestran ejemplos de estos casos prácticos generales y habituales de IA generativa que se pueden personalizar para diferentes sectores.

### Creación y recomendación de contenido

*   Generar contenido relacionado con el marketing, como imágenes de productos, publicaciones en redes sociales y correos con imágenes relevantes.
*   Traducir contenido, como documentos, contenido de sitios web y conversaciones de chatbots multilingües.
*   Resumir contenido de texto, como documentos, artículos, comentarios de clientes e informes, para tomar decisiones más fundamentadas basadas en datos.
*   Crear resúmenes de información de varias fuentes que pueden incluir texto, imágenes y componentes de vídeo o audio.
*   Subtitulado automático de vídeos.
*   Crear contenido multimedia creativo, como imágenes a partir de descripciones de peticiones de texto, modificar o corregir imágenes mediante peticiones de texto (por ejemplo, quitar un objeto o cambiar la combinación de colores) y generar vídeos cortos o animaciones a partir de peticiones de texto o guiones.
*   Generar voces sintéticas realistas para audio, como pistas de voz en off y música.
*   Analizar y comprender el comportamiento, las preferencias, las reseñas y las interacciones anteriores de los usuarios para ofrecer recomendaciones de contenido personalizadas. El análisis se puede combinar con factores en tiempo real, como la ubicación, para adaptar las recomendaciones de contenido (productos, artículos y vídeos, entre otros).

### Búsqueda conversacional y chatbots

*   Crear asistentes virtuales para interacciones con los usuarios, como la asistencia al cliente y las ventas online.
*   Habilitar la búsqueda conversacional en grandes bases de conocimientos con consultas en lenguaje natural.
*   Encontrar respuestas a preguntas complejas que combinan consultas de texto con imágenes relacionadas.

### Comprensión de documentos y datos

*   Extraer datos y analizar contenido de texto, como informes, facturas, recibos, transacciones financieras o contratos, para destacar posibles errores o problemas de cumplimiento, identificar riesgos potenciales o descubrir anomalías que indiquen un fraude.
*   Analizar el sentimiento del contenido generado por usuarios, como las publicaciones en redes sociales y las reseñas de productos.
*   Analizar las conversaciones transcritas del centro de llamadas para extraer información valiosa, como los motivos más frecuentes por los que los clientes dan una valoración baja a las interacciones con el centro de llamadas.
*   Analizar datos de ciberseguridad, como informes de amenazas, artículos y repositorios, para extraer indicadores de amenazas clave. Este análisis permite una defensa de ciberseguridad proactiva para resumir y priorizar las estrategias de mitigación con recomendaciones para una respuesta más rápida.
    
    Analysis puede traducir gráficos de ataque complejos a explicaciones de texto sin formato sobre la exposición. También puede simular posibles rutas de ataque para destacar los recursos afectados y recomendar mitigaciones antes de que se puedan explotar los recursos.
    

### Generación de código y asistencia para desarrolladores

La IA generativa puede ayudarte con los siguientes tipos de tareas en todas las fases del ciclo de vida de desarrollo de software (SDLC):

*   Generar especificaciones y documentación de APIs mediante peticiones de lenguaje natural.
*   Crear recursos, como código, funciones, comandos de línea de comandos y secuencias de comandos de Terraform, a partir de peticiones de lenguaje natural.
*   Generar pruebas y explicaciones de código, incluidos comentarios y documentación para explicar el código.

Para obtener más información sobre cómo la IA generativa puede transformar las operaciones empresariales, como el servicio de atención al cliente, la productividad de los empleados y la automatización de procesos, consulta los [casos prácticos](https://docs.cloud.google.com/ai/generative-ai?hl=es#business-use-cases) de "IA generativa en Google Cloud".

Cuándo usar la IA tradicional
-----------------------------

Los casos prácticos tradicionales de la IA suelen centrarse en predecir resultados futuros o clasificar una categoría en función de un modelo de IA entrenado con fuentes de datos históricos, como [datos tabulares](https://docs.cloud.google.com/vertex-ai/docs/tabular-data/tabular101?hl=es) e imágenes. Las soluciones de IA tradicionales suelen ser suficientes para abordar varios casos prácticos de clasificación e IA predictiva, como los siguientes:

*   **Casos prácticos de clasificación:**
    *   Filtrar el spam de correo clasificando los correos como _spam_ o _no spam_, en función de un modelo de IA de clasificación tradicional entrenado con datos históricos.
    *   Entrenar un modelo de clasificación de imágenes tradicional con imágenes específicas de productos buenos y defectuosos para ayudar de forma eficaz con la inspección en tiempo real y la detección de defectos en la fabricación.
*   **Casos prácticos de regresión:**
    *   Predecir valores numéricos continuos, como el precio de una casa, en función de sus características y su ubicación.
    *   Predecir los ingresos que generará un cliente de una plataforma de comercio electrónico durante su relación con la empresa en función del historial de datos de compra.
*   **Casos prácticos de previsión de series temporales:** previsión de ventas y demanda.
*   **Casos prácticos de agrupamiento en clústeres:** segmentación de clientes.

Para obtener más información sobre el uso de la IA tradicional, consulta [Usos y ejemplos del análisis predictivo](https://cloud.google.com/learn/what-is-predictive-analytics?hl=es#section-5) en "¿Qué es el análisis predictivo?".

Decidir entre la IA tradicional y la IA generativa
--------------------------------------------------

El siguiente árbol de decisiones simplificado ofrece una referencia general para algunas rutas de decisión basadas en casos prácticos. En algunos casos, puede ser mejor usar tanto la IA tradicional como la IA generativa, tal como se describe en la siguiente sección, "Cuándo combinar la IA generativa con la IA tradicional".

El árbol de decisiones incluye las siguientes preguntas y respuestas basadas en casos prácticos:

*   Si tu caso práctico está relacionado con la clasificación o la detección, comprueba si un [modelo de IA tradicional preentrenado](https://docs.cloud.google.com/vertex-ai/docs?hl=es#vertex-ai-and-cloud-ml-products) puede cumplir los requisitos de tu caso práctico. Los modelos tradicionales preentrenados incluyen APIs de IA como Document AI, Vision AI, la API Natural Language y la API Video Intelligence.
    
    *   Si un modelo preentrenado cumple tus requisitos, úsalo.
    *   Si un modelo preentrenado no cumple tus requisitos, comprueba si hay suficientes datos de entrenamiento disponibles para entrenar un modelo personalizado.
        *   Si se dispone de suficientes datos de entrenamiento, ¿qué se debe priorizar: tener más control sobre el entrenamiento del modelo o conseguir una comercialización más rápida?
            *   Si necesitas un control exhaustivo del entrenamiento del modelo con personalizaciones como usar el algoritmo del modelo que prefieras, desarrollar tus propias funciones de pérdida, usar funciones específicas de [explicabilidad](https://docs.cloud.google.com/explainable-ai?hl=es) del modelo, el número de capas del modelo, la tasa de aprendizaje y otros [hiperparámetros](https://developers.google.com/machine-learning/guides/text-classification/step-5?hl=es) del modelo, usa un entrenamiento personalizado de un modelo de IA tradicional. Para obtener información sobre las diferencias entre el entrenamiento personalizado y el entrenamiento de un modelo en Vertex AI mediante AutoML, consulta [Elegir un método de entrenamiento](https://docs.cloud.google.com/vertex-ai/docs/start/training-methods?hl=es).
            *   Si tu prioridad empresarial es acelerar el tiempo de lanzamiento al mercado, usa la IA generativa. Si tu caso de uso es especializado, puedes mejorar el rendimiento de un modelo usando el ajuste de modelos, como el [ajuste supervisado](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/models/tune-models?hl=es#how-tuning-works) para la clasificación, el análisis de sentimientos o la extracción de entidades.
        *   Si no hay ningún conjunto de datos de entrenamiento disponible o si los conjuntos de datos disponibles no son lo suficientemente grandes para entrenar un modelo personalizado, usa modelos de IA generativa con [ingeniería de prompts](https://docs.cloud.google.com/discover/what-is-prompt-engineering?hl=es). Estos modelos se pueden ajustar aún más para realizar tareas especializadas mediante [ejemplos de datos](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/models/gemini-supervised-tuning-prepare?hl=es).
*   Si tu caso práctico está relacionado con la IA predictiva, usa la IA tradicional. La IA predictiva tradicional es especialmente eficaz con datos estructurados.
    
*   Si tu caso práctico está relacionado con casos prácticos de IA generativa, como la creación de resúmenes, la generación de contenido o la transcripción avanzada, usa la IA generativa. El uso de la IA generativa incluye casos prácticos que requieren procesar e introducir información de varias modalidades, como texto, imágenes, vídeos o audio.
    

Aunque los científicos de datos y los ingenieros de aprendizaje automático suelen dirigir el proceso de selección de modelos, también es importante tener en cuenta las aportaciones de las partes interesadas clave, como los líderes empresariales, los propietarios de productos, los expertos en el dominio y los usuarios finales. Por ejemplo, estos participantes pueden interactuar de las siguientes formas:

*   **Directivos y responsables de la toma de decisiones:** aprueban la selección cuando se ajusta a las prioridades de la empresa.
*   **Propietarios del producto:** pueden necesitar influir en el comportamiento del modelo o tener más control sobre él para que se ajuste a las prioridades del producto.
*   **Expertos en el dominio:** aplican sus conocimientos del dominio para mejorar la eficacia del modelo.
*   **Usuarios finales:** pueden necesitar entender los resultados del modelo y cómo incorporarlos para tomar decisiones más fundamentadas.

Cuándo combinar la IA generativa con la IA tradicional
------------------------------------------------------

La IA tradicional y la IA generativa no son mutuamente excluyentes. En algunos casos prácticos, se pueden usar de forma complementaria para alcanzar el objetivo empresarial final. Por ejemplo, puedes usar el resultado de un modelo de IA tradicional como parte de la petición de un modelo de IA generativa. A continuación se muestran algunos ejemplos de casos prácticos para combinar las funciones de la IA tradicional y la generativa:

*   La IA predictiva tradicional puede analizar el historial de datos para predecir la probabilidad de abandono de los clientes. Este análisis se puede integrar con un LLM o un chatbot basado en IA generativa, lo que permite a tu equipo de ventas explorar las predicciones mediante conversaciones en lenguaje natural. También puedes generar paneles de control de inteligencia empresarial (BI) mediante una conversación sencilla con el chatbot.
*   La IA predictiva tradicional puede pronosticar los riesgos de un caso práctico específico, mientras que la IA generativa puede simular diferentes situaciones para ayudar a formular posibles estrategias de mitigación.
*   La IA predictiva tradicional puede identificar segmentos de clientes para ayudar a crear marketing personalizado y campañas. Después, puedes usar la IA generativa para crear contenido de marketing personalizado que se adapte a cada segmento identificado.
*   La visión artificial tradicional con IA puede detectar y clasificar el lenguaje de signos para traducir la entrada de vídeo a texto. La IA generativa puede añadir contexto y matices a la lengua de signos, lo que permite optimizar la traducción a texto escrito, incluidos varios idiomas. La IA generativa también puede generar una salida de voz a partir de la traducción de texto, lo que permite una comunicación bidireccional fluida entre personas que usan la lengua de signos y personas que no la usan.
*   La IA tradicional puede realizar analíticas de vídeo y usar las funciones de inteligencia de vídeo para extraer información valiosa y características de los recursos de vídeo. Por ejemplo, puede detectar objetos, personas y texto, así como extraerlos de recursos de vídeo. La IA generativa puede usar esas estadísticas para crear experiencias novedosas, como chatbots, fichas, informes o artículos.

Para maximizar las ventajas empresariales de tus inversiones en IA generativa y tradicional, prioriza los resultados empresariales necesarios y las necesidades de los usuarios (soluciones de IA centradas en la empresa y en los usuarios). Este enfoque asegura que las soluciones sigan siendo relevantes, impulsen la adopción, mejoren la eficiencia y fomenten la innovación. Priorizar la experiencia de usuario en las soluciones basadas en IA ayuda a alinear las expectativas y a ofrecer resultados significativos.

Siguientes pasos
----------------

*   Consulta cómo [evaluar y definir tu caso práctico de IA generativa](https://docs.cloud.google.com/docs/ai-ml/generative-ai/evaluate-define-generative-ai-use-case?hl=es).
*   Consulta más información sobre las fases del desarrollo de una aplicación de IA generativa y elige los mejores productos y herramientas para tu caso práctico en [Desarrollar una aplicación de IA generativa en Google Cloud](https://docs.cloud.google.com/docs/ai-ml/generative-ai?hl=es).
*   Evalúa tus funciones de IA y crea una hoja de ruta para aprovechar todo su potencial con el [taller de preparación para la IA](https://docs.cloud.google.com/consulting/ai-readiness-workshop?hl=es).

A menos que se indique lo contrario, el contenido de esta página está sujeto a la [licencia Reconocimiento 4.0 de Creative Commons](https://creativecommons.org/licenses/by/4.0/) y las muestras de código están sujetas a la [licencia Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0). Para obtener más información, consulta las [políticas del sitio web de Google Developers](https://developers.google.com/site-policies?hl=es). Java es una marca registrada de Oracle o sus afiliados.

Última actualización: 2026-01-14 (UTC).
