# Imágenes base &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/base-images

Una imagen base es el punto de partida para la mayoría de los flujos de trabajo de desarrollo basados en contenedores. Los desarrolladores comienzan con una imagen base y, sobre ella, colocan las bibliotecas, los objetos binarios y los archivos de configuración necesarios para ejecutar su aplicación. Los paquetes de compilación de Google Cloud publican imágenes base con varias configuraciones de los paquetes de sistema y lenguajes disponibles.

Las imágenes base se alojan en todas las regiones en las que Artifact Registry está disponible. Puedes personalizar la ruta de acceso de la imagen base; para ello, reemplaza la primera parte del URI por la región que elijas:

`REGION-docker.pkg.dev/serverless-runtimes/STACK/runtimes/RUNTIME_ID`

Reemplaza lo siguiente:

*   REGION por la región preferida, por ejemplo, `us-central1`.
*   STACK por la pila preferida del sistema operativo, por ejemplo, `google-24`.
*   RUNTIME\_ID por el ID del entorno de ejecución que usa la función, por ejemplo, `python313`.

Por ejemplo, los paquetes de compilación hacen referencia a la imagen base de Node.js 24 que usa la pila `google-24`, alojada en `us-central1` con la siguiente URL: `us-central1-docker.pkg.dev/serverless-runtimes/google-24/runtimes/nodejs24`.

Para obtener más información sobre los entornos de ejecución compatibles, consulta el [Ciclo de vida del entorno de ejecución](https://docs.cloud.google.com/docs/buildpacks/runtime-support?hl=es-419#support_schedule).

Parches de seguridad
--------------------

Para proporcionar parches de seguridad, las imágenes base proporcionadas por Google dependen de proveedores ascendentes, como proyectos de código abierto y distribuciones de sistemas operativos. Los proveedores ascendentes identifican de forma rutinaria las vulnerabilidades de seguridad y publican actualizaciones de parches para sus componentes. Google recibe esas actualizaciones y las incluye en una nueva versión de imagen base con el siguiente proceso:

1.  Google crea una imagen nueva que contiene las dependencias parcheadas y la etiqueta con `public-image-next`.
2.  Google prueba la imagen con componentes y dependencias actualizados en entornos de etapa de pruebas.
3.  Después de que las pruebas se realizan correctamente, Google comienza a actualizar todas las imágenes base de las cargas de trabajo en ejecución para usar la nueva imagen base.
4.  Una vez que se completan las pruebas y las actualizaciones, Google promueve la nueva etiqueta de imagen a `latest` y `public-image-current`, y quita la etiqueta `latest` anterior.
