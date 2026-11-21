# Crear una aplicación de Node.js &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/nodejs

Especificar versiones de Node.js
--------------------------------

El proyecto buildpacks ofrece asistencia para las versiones LTS actuales y activas de Node.js. Hay versiones anteriores de Node.js disponibles, pero es posible que el proyecto no las mantenga activamente.

### Estás usando `package.json`

Puedes especificar la versión de Node.js de tu aplicación durante la implementación configurando el campo `engines.node` en `package.json`. Para configurar el paquete de compilación de forma que use la versión más reciente de Node.js v16 al implementar tu aplicación, puedes usar los siguientes valores en tu `package.json`:

"engines": {
      "node": "16.x.x"
    }

### Estás usando `GOOGLE_NODEJS_VERSION`

También se puede especificar la versión de Node.js mediante la variable de entorno `GOOGLE_NODEJS_VERSION`. Si se definen ambas configuraciones, el valor de `GOOGLE_NODEJS_VERSION` tiene prioridad sobre la propiedad `engines.node`. Si no se proporciona ningún valor, se usará la versión LTS más reciente de Node.js.

Para configurar el paquete de compilación para que use Node.js 16 al desplegar tu aplicación, sigue estos pasos:

pack build --builder=gcr.io/buildpacks/builder \
       sample-functions-framework-node \
       --env GOOGLE_NODEJS_VERSION=16.x.x

También puedes usar un descriptor de proyecto `project.toml` para codificar la variable de entorno junto con los archivos de tu proyecto. Consulta las instrucciones para [compilar la aplicación con variables de entorno](https://docs.cloud.google.com/docs/buildpacks/set-environment-variables?hl=es#build_the_application_with_environment_variables\)).

### Consejos

*   El campo `engines.node` puede aceptar una restricción de semver. La biblioteca específica que usamos para los buildpacks de Node.js es [Masterminds/semver](https://github.com/Masterminds/semver).
*   No uses especificadores de mayor que (>) en `engines.node`
*   Al desplegar la aplicación en el entorno estándar de App Engine, la propiedad `engines.node` debe ser compatible con el tiempo de ejecución especificado en `app.yaml`.
*   Puedes consultar documentación adicional sobre la opción de configuración `engines.node` en `package.json` en la documentación oficial de NPM, en el [tema de los motores](https://docs.npmjs.com/cli/v8/configuring-npm/package-json).
*   Al desplegar una función en Cloud Run Functions, la propiedad `engines.node` debe ser compatible con el tiempo de ejecución usado para desplegar la función.

Instalar dependencias
---------------------

### Usar NPM

*   NPM es el gestor de paquetes predeterminado.
*   Siempre que sea posible, utilice `package-lock.json` para mejorar el rendimiento de la caché.
*   De forma predeterminada, solo se instalan las dependencias de producción.
*   Puedes especificar la sección de la versión de npm mediante el campo `engines.npm` en el archivo `package.json`.

### Usar Yarn

*   Yarn se usa en su lugar cuando incluye el archivo `yarn.lock` en su proyecto.
*   Puedes especificar la versión de Yarn que quieres usar en el campo `engines.yarn` de tu archivo `package.json`.
*   Admitimos el modo PnP de Yarn 2 si tu proyecto incluye un `.yarn/cache`.

### Usar Pnpm

*   Pnpm se usa en su lugar cuando incluyes el archivo `pnpm-lock.yaml` en tu proyecto.
*   Puedes especificar una versión de pnpm en el campo `engines.pnpm` de tu archivo `package.json`.
*   Para ver un ejemplo práctico, consulta la aplicación [sample-node-pnpm](https://github.com/GoogleCloudPlatform/buildpack-samples/tree/master/sample-node-pnpm).

Usar módulos privados
---------------------

Puedes usar un [módulo npm privado](https://docs.npmjs.com/private-modules/intro) proporcionando la configuración para autenticarte con el registro en un archivo `.npmrc` del directorio de la función. Si usas Yarn 2 o una versión posterior como gestor de paquetes, este archivo se llama `.yarnrc.yml`.

### Módulos privados de Artifact Registry

Un [repositorio de paquetes de Node.js de Artifact Registry](https://docs.cloud.google.com/artifact-registry/docs/nodejs/quickstart?hl=es) puede alojar módulos privados para tu función. Cuando despliega una función de Buildpacks, el proceso de compilación genera automáticamente credenciales de Artifact Registry para la [cuenta de servicio de Cloud Build](https://docs.cloud.google.com/build/docs/cloud-build-service-account?hl=es). Solo tienes que incluir el repositorio de Artifact Registry en tu archivo `.npmrc` cuando uses la versión 1 de npm o Yarn. Por ejemplo, si usas NPM o Yarn versión 1:

@SCOPE:registry=https://REGION_ID-npm.pkg.dev/PROJECT_ID/REPOSITORY_NAME
    //REGION_ID-npm.pkg.dev/PROJECT_ID/REPOSITORY_NAME:always-auth=true

Si usas Yarn versión 2 o posterior, solo tienes que incluir el repositorio de Artifact Registry en tu archivo `.yarnrc.yml` sin credenciales adicionales. Por ejemplo:

npmScopes:
      SCOPE:
        npmRegistryServer: https://REGION_ID-npm.pkg.dev/PROJECT_ID/REPOSITORY_NAME
        npmAlwaysAuth: true

### Módulos privados de otros repositorios

En la [documentación de npm](https://docs.npmjs.com/getting-started/working_with_tokens) se explica cómo crear tokens de acceso de solo lectura personalizados. No recomendamos usar el archivo `.npmrc` creado en el directorio principal porque contiene un token de lectura y escritura. Los permisos de escritura no son necesarios durante la implementación y podrían suponer un riesgo de seguridad.

No incluyas el archivo `.npmrc` si no usas repositorios privados, ya que puede aumentar el tiempo de implementación de tus funciones.

#### Formato de archivo

Si usas un archivo `.npmrc` para definir un token de autenticación personalizado, debe incluir la línea que se muestra a continuación.

//REGISTRY_DOMAIN/:_authToken=AUTH_TOKEN

Sustituye:

*   REGISTRY\_DOMAIN: el nombre de dominio de tu registro privado de npm. Por ejemplo, si el host de tu repositorio es `npmjs.org`, asigna el valor `registry.npmjs.org` a este campo.
*   AUTH\_TOKEN: el token de autorización de tu registro de npm. Puede ser el valor de texto literal del token o la cadena de texto `${NPM_TOKEN}`, que el comando `npm` sustituye por el valor del token real del entorno.
    
    Puedes definir la variable de entorno `$NPM_TOKEN` con el argumento [`--set-build-env-vars`](https://docs.cloud.google.com/functions/docs/configuring/env-var?hl=es#using_build_environment_variables) del comando [`gcloud functions deploy`](https://docs.cloud.google.com/sdk/gcloud/reference/functions/deploy?hl=es). Para obtener más información sobre el token de autenticación de NPM, consulta el [tutorial de NPM sobre módulos privados](https://docs.npmjs.com/using-private-packages-in-a-ci-cd-workflow#create-and-check-in-a-project-specific-npmrc-file).
    

Ejecutar pasos de compilación personalizados durante la implementación
----------------------------------------------------------------------

De forma predeterminada, `npm run build` se ejecuta si se especifica una secuencia de comandos en el archivo `package.json`. Sin embargo, puedes especificar pasos de compilación personalizados para anular el comportamiento predeterminado y ejecutar solo las secuencias de comandos que quieras durante la compilación. Puedes controlar los pasos de compilación mediante la variable de entorno `GOOGLE_NODE_RUN_SCRIPTS` o `gcp-build` en el archivo `package.json`.

Solo puedes usar un método. Ten en cuenta que la variable de entorno `GOOGLE_NODE_RUN_SCRIPTS` tiene prioridad y anula cualquier valor que se haya especificado para `gcp-build` en tu `package.json`.

De forma predeterminada, cuando configuras pasos de compilación personalizados, se instalan primero `dependencies` y `devDependencies` en el archivo `package.json` antes de que se ejecuten secuencias de comandos o comandos. Para anular el comportamiento predeterminado, puedes usar la variable de entorno [`NODE_ENV`](#node_env).

### Estás usando `GOOGLE_NODE_RUN_SCRIPTS`

Puedes transferir la variable de entorno `GOOGLE_NODE_RUN_SCRIPTS` a la compilación para controlar qué secuencias de comandos se ejecutan. Puedes especificar uno o varios scripts o, en su lugar, pasar una variable de entorno vacía para evitar que se ejecute el comportamiento predeterminado, como `GOOGLE_NODE_RUN_SCRIPTS=`. Para obtener más información, consulta el artículo sobre [variables de entorno](#google_node_run_scripts).

### Estás usando `package.json`

Si añades `gcp-build` en tu archivo `package.json`, solo se ejecutará `npm run gcp-build`, lo que significa que se anulará el comportamiento predeterminado. Puede especificar uno o varios comandos, o bien una cadena vacía para evitar que se ejecute ningún comando, como `"gcp-build":""`.

"scripts": {
      ...
      "gcp-build": "npm run lint && npm run build"
      ...
    }

Punto de entrada de la aplicación
---------------------------------

El buildpack de Node.js ejecutará el comando especificado en el campo `scripts.start` de tu `package.json`. Si no se define `scripts.start`, el paquete de compilación ejecutará `npm start`.

Te recomendamos que uses un Procfile porque elimina `npm` o `yarn` de la ruta.

Variables de entorno
--------------------

Puedes [definir variables de entorno para configurar las compilaciones](https://docs.cloud.google.com/docs/buildpacks/set-environment-variables?hl=es) de tu imagen de contenedor.

El paquete de compilación de Node.js admite las siguientes variables de entorno para personalizar tu contenedor.

### `NPM_CONFIG_<key>`

Consulta la [documentación](https://docs.npmjs.com/misc/config#environment-variables).

**Ejemplo:** `NPM_CONFIG_FLAG=value` pasa `-flag=value` a los comandos de `npm`.

### `NODE_ENV`

Especifica el entorno de desarrollo durante la compilación. Se ha definido como `npm install`.

**Ejemplo:** `NODE_ENV=development` instala tanto `dependencies` como `devDependencies`, que se especifican en `package.json`.

### `GOOGLE_NODE_RUN_SCRIPTS`

Especifica una lista ordenada de secuencias de comandos de npm de `package.json` que se ejecutarán después de instalar las dependencias. La lista debe estar separada por comas y se ejecuta en el orden en el que se enumeran las secuencias de comandos.

Si especifica `GOOGLE_NODE_RUN_SCRIPTS`, solo se ejecutarán las secuencias de comandos que incluya en la lista. Por ejemplo, si quieres evitar que se ejecute el valor predeterminado `npm run build`, especifica la variable de entorno sin ningún valor.

**Ejemplos:**

*   `GOOGLE_NODE_RUN_SCRIPTS=lint,build` se ejecuta `npm run lint` y, a continuación, `npm run build`.
*   `GOOGLE_NODE_RUN_SCRIPTS=` no ejecuta ninguna secuencia de comandos.
