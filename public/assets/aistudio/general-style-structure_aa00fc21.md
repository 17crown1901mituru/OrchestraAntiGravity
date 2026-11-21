# Prácticas recomendadas para el estilo y la estructura generales &nbsp;|&nbsp; Terraform &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/best-practices/general-style-structure

En este documento se ofrecen recomendaciones básicas sobre el estilo y la estructura de las configuraciones de Terraform. Estas recomendaciones se aplican a los módulos de Terraform reutilizables y a las configuraciones raíz.

Esta guía no es una introducción a Terraform. Para obtener una introducción al uso de Terraform con Google Cloud, consulta el artículo [Empezar a usar Terraform](https://docs.cloud.google.com/docs/terraform/get-started-with-terraform?hl=es).

Sigue una estructura de módulo estándar
---------------------------------------

*   Los módulos de Terraform deben seguir la [estructura de módulo estándar](https://www.terraform.io/docs/modules/create.html).
*   Empieza cada módulo con un archivo `main.tf`, donde se encuentran los recursos de forma predeterminada.
*   En cada módulo, incluye un archivo `README.md` en formato Markdown. En el archivo `README.md`, incluye documentación básica sobre el módulo.
*   Coloca los ejemplos en una carpeta `examples/`, con un subdirectorio independiente para cada ejemplo. En cada ejemplo, incluya un archivo `README.md` detallado.
*   Crea agrupaciones lógicas de recursos con sus propios archivos y nombres descriptivos, como `network.tf`, `instances.tf` o `loadbalancer.tf`.
    *   No asignes un archivo a cada recurso. Agrupa los recursos por su objetivo compartido. Por ejemplo, combina `google_dns_managed_zone` y `google_dns_record_set` en `dns.tf`.
*   En el directorio raíz del módulo, incluya solo archivos de metadatos de Terraform (`*.tf`) y del repositorio (como `README.md` y `CHANGELOG.md`).
*   Coloca cualquier documentación adicional en un subdirectorio `docs/`.

Adoptar una convención de nomenclatura
--------------------------------------

*   Asigna nombres a todos los objetos de configuración con guiones bajos para delimitar varias palabras. De esta forma, se asegura la coherencia con la convención de nomenclatura de los tipos de recursos, los tipos de fuentes de datos y otros valores predefinidos. Esta convención no se aplica a los [argumentos](https://www.terraform.io/docs/glossary#argument) de nombre.
    
    Recomendado:
    
    resource "google\_compute\_instance" **"web\_server"** {
      name = "web-server"
    }
    
    No recomendado:
    
    resource "google\_compute\_instance" **"web-server"** {
      name = "web-server"
    }
    
*   Para simplificar las referencias a un recurso que es el único de su tipo (por ejemplo, un único balanceador de carga para todo un módulo), asigna el nombre `main` al recurso.
    
    *   Requiere un esfuerzo mental adicional recordar `some_google_resource.my_special_resource.id` en lugar de `some_google_resource.main.id`.
*   Para diferenciar los recursos del mismo tipo entre sí (por ejemplo, `primary` y `secondary`), proporciona nombres de recursos descriptivos.
    
*   Usa nombres de recursos en singular.
    
*   En el nombre del recurso, no repitas el tipo de recurso. Por ejemplo:
    
    Recomendado:
    
    resource "google\_compute\_global\_address" "main" { ... }
    
    No recomendado:
    
    resource "google\_compute\_global\_address" "main\_global\_address" { … }
    

Usar variables con cuidado
--------------------------

*   Declara todas las variables en `variables.tf`.
*   Asigna a las variables nombres descriptivos que sean relevantes para su uso o finalidad:
    *   Los datos de entrada, las variables locales y los datos de salida que representan valores numéricos (como el tamaño del disco o el tamaño de la RAM) _deben_ nombrarse con unidades (como `ram_size_gb`). Google Cloud Las APIs no tienen unidades estándar, por lo que, al nombrar las variables con unidades, se aclara la unidad de entrada esperada para los responsables del mantenimiento de la configuración.
    *   Para las unidades de almacenamiento, usa prefijos de unidades binarias (potencias de 1024): `kibi`, `mebi` y `gibi`. Para todas las demás unidades de medida, utilice prefijos de unidades decimales (potencias de 1000): `kilo`, `mega` y `giga`. Este uso coincide con el de Google Cloud.
    *   Para simplificar la lógica condicional, asigna nombres positivos a las variables booleanas; por ejemplo, `enable_external_access`.
*   Las variables deben tener descripciones. Las descripciones se incluyen automáticamente en la documentación generada automáticamente de un módulo publicado. Las descripciones añaden contexto adicional para los nuevos desarrolladores que los nombres descriptivos no pueden proporcionar.
*   Asigna tipos a las variables definidas.
*   Cuando proceda, proporcione valores predeterminados:
    *   En el caso de las variables que tienen valores independientes del entorno (como disk size), proporcione valores predeterminados.
    *   En el caso de las variables que tienen valores específicos del entorno (como `project_id`), no proporcione valores predeterminados. De esta forma, el módulo de llamada debe proporcionar valores significativos.
*   Usa valores predeterminados vacíos para las variables (como cadenas o listas vacías) solo cuando dejar la variable vacía sea una preferencia válida que las APIs subyacentes no rechacen.
*   Usa las variables con prudencia. Solo se deben parametrizar los valores que deban variar en cada instancia o entorno. Cuando decidas si quieres exponer una variable, asegúrate de que tienes un caso práctico concreto para cambiarla. Si solo hay una pequeña probabilidad de que se necesite una variable, no la expongas.
    *   Añadir una variable con un valor predeterminado es compatible con versiones anteriores.
    *   Eliminar una variable es incompatible con versiones anteriores.
    *   En los casos en los que se reutiliza un literal en varios lugares, puedes usar un [valor local](https://www.terraform.io/docs/configuration/locals.html) sin exponerlo como una variable.

Exponer resultados
------------------

*   Organiza todos los resultados en un archivo `outputs.tf`.
*   Proporciona descripciones significativas de todos los resultados.
*   Descripciones de los resultados de los documentos en el archivo `README.md`. Genera descripciones automáticamente en las confirmaciones con herramientas como [terraform-docs](https://github.com/terraform-docs/terraform-docs).
*   Genera todos los valores útiles a los que los módulos raíz puedan hacer referencia o compartir. Sobre todo en el caso de los módulos de código abierto o que se usen mucho, expón todas las salidas que se puedan consumir.
*   No transfieras las salidas directamente a través de variables de entrada, ya que, de lo contrario, no se añadirán correctamente al gráfico de dependencias. Para asegurarse de que se crean [dependencias implícitas](https://learn.hashicorp.com/terraform/getting-started/dependencies.html), compruebe que las salidas hacen referencia a atributos de recursos. En lugar de hacer referencia directamente a una variable de entrada de una instancia, pasa el atributo como se muestra a continuación:
    
    Recomendado:
    
    output "name" {
      description = "Name of instance"
      value       = google\_compute\_instance.main.name
    }
    
    No recomendado:
    
    output "name" {
      description = "Name of instance"
      value       = var.name
    }
    

Usar fuentes de datos
---------------------

*   Coloca las [fuentes de datos](https://www.terraform.io/docs/configuration/data-sources.html) junto a los recursos que las referencian. Por ejemplo, si vas a obtener una imagen para usarla al iniciar una instancia, colócala junto a la instancia en lugar de recoger recursos de datos en su propio archivo.
*   Si el número de fuentes de datos es elevado, te recomendamos que las transfieras a un archivo `data.tf` específico.
*   Para obtener datos relativos al entorno actual, usa la [interpolación](https://www.terraform.io/language/expressions/strings#interpolation) de variables o recursos.

Limitar el uso de secuencias de comandos personalizadas
-------------------------------------------------------

*   Usa secuencias de comandos solo cuando sea necesario. Terraform no tiene en cuenta ni gestiona el estado de los recursos creados mediante secuencias de comandos.
    *   Evita las secuencias de comandos personalizadas, si es posible. Úsalos solo cuando los recursos de Terraform no admitan el comportamiento deseado.
    *   Cualquier secuencia de comandos personalizada que se utilice debe tener una razón claramente documentada para existir y, lo ideal, un plan de retirada.
*   Terraform puede llamar a secuencias de comandos personalizadas a través de provisionadores, incluido el provisionador local-exec.
*   Coloca las secuencias de comandos personalizadas a las que llama Terraform en un directorio `scripts/`.

Incluir secuencias de comandos auxiliares en un directorio independiente
------------------------------------------------------------------------

*   Organiza las secuencias de comandos auxiliares que no llama Terraform en un directorio `helpers/`.
*   Secuencias de comandos de ayuda de documentos en el archivo `README.md` con explicaciones e invocaciones de ejemplo.
*   Si las secuencias de comandos auxiliares aceptan argumentos, proporciona comprobación de argumentos y `--help`salida.

Colocar archivos estáticos en un directorio independiente
---------------------------------------------------------

*   Los archivos estáticos a los que hace referencia Terraform, pero que no ejecuta (como los scripts de inicio cargados en instancias de Compute Engine), deben organizarse en un directorio `files/`.
*   Coloca los HereDocs largos en archivos externos, separados de su HCL. Haz referencia a ellos con la función [`file()`](https://www.terraform.io/language/functions/file).
*   En el caso de los archivos que se leen mediante la función de Terraform [`templatefile`](https://www.terraform.io/docs/configuration/functions/templatefile.html), usa la extensión de archivo `.tftpl`.
    *   Las plantillas deben colocarse en un directorio `templates/`.

Proteger recursos con reconocimiento del estado
-----------------------------------------------

En el caso de los recursos con estado, como las bases de datos, asegúrate de que la [protección contra la eliminación](https://www.terraform.io/language/meta-arguments/lifecycle) esté habilitada. Por ejemplo:

resource "google_sql_database_instance" "main" {
      name = "primary-instance"
      settings {
        tier = "D0"
      }
    
      lifecycle {
        prevent_destroy = true
      }
    }

Usar el formato integrado
-------------------------

Todos los archivos de Terraform deben cumplir los estándares de `terraform fmt`.

### Limitar la complejidad de las expresiones

*   Limita la complejidad de las expresiones interpoladas. Si necesitas muchas funciones en una sola expresión, plantéate dividirla en varias expresiones usando [valores locales](https://www.terraform.io/docs/configuration/locals.html).
*   No incluyas más de una operación ternaria en una sola línea. En su lugar, usa varios valores locales para crear la lógica.

Usar `count` para valores condicionales
---------------------------------------

Para crear una instancia de un recurso de forma condicional, usa el metaargumento [`count`](https://www.terraform.io/language/meta-arguments/count). Por ejemplo:

variable "readers" {
      description = "..."
      type        = list
      default     = []
    }
    
    resource "resource_type" "reference_name" {
      // Do not create this resource if the list of readers is empty.
      count = length(var.readers) == 0 ? 0 : 1
      ...
    }

Usa con moderación las variables especificadas por el usuario para definir la variable `count` de los recursos. Si se proporciona un atributo de recurso para una variable de este tipo (como `project_id`) y ese recurso aún no existe, Terraform no puede generar un plan. En su lugar, Terraform informa del error [`value of count cannot be computed`](https://github.com/hashicorp/terraform/issues/17421). En estos casos, usa una variable `enable_x` independiente para calcular la lógica condicional.

Usa `for_each` para los recursos iterados
-----------------------------------------

Si quieres crear varias copias de un recurso a partir de un recurso de entrada, usa el metaargumento [`for_each`](https://www.terraform.io/language/meta-arguments/for_each).

Publicar módulos en un registro
-------------------------------

*   **Módulos reutilizables:** publica módulos reutilizables en un [registro de módulos](https://www.terraform.io/internals/module-registry-protocol).
    
*   **Módulos de código abierto:** publica módulos de código abierto en el [registro de Terraform](https://registry.terraform.io/).
    
*   **Módulos privados:** publica módulos privados en un [registro privado](https://www.terraform.io/cloud-docs/registry).
    

Siguientes pasos
----------------

*   Consulta las [prácticas recomendadas para usar módulos reutilizables](https://docs.cloud.google.com/docs/terraform/best-practices/reusable-modules?hl=es).
*   Consulta las [prácticas recomendadas para usar módulos raíz de Terraform](https://docs.cloud.google.com/docs/terraform/best-practices/root-modules?hl=es).
