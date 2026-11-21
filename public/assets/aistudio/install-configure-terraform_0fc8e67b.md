# Instala Terraform &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/install-configure-terraform

Instala Terraform
-----------------

En esta página, se describen los pasos para instalar Terraform para Google Cloud en [Cloud Shell](https://docs.cloud.google.com/shell/docs?hl=es-419) y en una shell local. Cloud Shell es un entorno de shell interactivo para Google Cloud que te permite aprender y experimentar con Google Cloud y administrar tus proyectos y recursos desde tu navegador web.

Para obtener una guía introductoria sobre el uso de Terraform con Google Cloud, consulta la [Guía de inicio rápido de Terraform para Google Cloud](https://docs.cloud.google.com/docs/terraform/create-vm-instance?hl=es-419) .

### Cloud Shell

1.  Para usar una terminal en línea con gcloud CLI ya configurada, activa Cloud Shell.
    
    En la parte inferior de esta página, se inicia una sesión de Cloud Shell y se muestra una instrucción de línea de comandos. La sesión puede tardar unos segundos en inicializarse.
    
2.  Para instalar la CLI de Terraform, sigue las [instrucciones de HashiCorp](https://developer.hashicorp.com/terraform/tutorials/gcp-get-started/install-cli).
    
    Puedes [personalizar tu entorno](https://docs.cloud.google.com/shell/docs/configuring-cloud-shell?hl=es-419#environment_customization) para instalar la CLI de Terraform en el inicio del entorno o instalar el objeto binario en tu directorio principal para conservar la instalación entre sesiones.
    
3.  Ejecuta el siguiente comando para verificar que Terraform esté disponible:
    
    terraform
    
    El resultado debería ser similar al siguiente ejemplo:
    
    Usage: terraform [global options] <subcommand> [args]
        
        The available commands for execution are listed below.
        The primary workflow commands are given first, followed by
        less common or more advanced commands.
        
        Main commands:
          init          Prepare your working directory for other commands
          validate      Check whether the configuration is valid
          plan          Show changes required by the current configuration
          apply         Create or update infrastructure
          destroy       Destroy previously-created infrastructure
    
4.  Para usar Terraform con Google Cloud, debes asegurarte de que se completen las siguientes tareas en Google Cloud:
    
    *   [Crea o ten un Google Cloud proyecto](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=es-419).
    *   [Habilita la facturación](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled?hl=es-419#confirm_billing_is_enabled_on_a_project) para el Google Cloud proyecto.
    *   [Habilita las APIs](https://docs.cloud.google.com/apis/docs/getting-started?hl=es-419#enabling_apis) para los Google Cloud servicios con los que deseas trabajar.
    *   [Configura la autenticación](https://docs.cloud.google.com/docs/terraform/authentication?hl=es-419) para Terraform.

### Shell local

1.  Usa las [instrucciones de instalación](https://developer.hashicorp.com/terraform/tutorials/gcp-get-started/install-cli) que proporciona Terraform.
    
2.  Ejecuta el siguiente comando para verificar que Terraform esté disponible:
    
    terraform
    
    El resultado debería ser similar al siguiente ejemplo:
    
     Usage: terraform [global options] <subcommand> [args]
        
        The available commands for execution are listed below.
        The primary workflow commands are given first, followed by
        less common or more advanced commands.
        
        Main commands:
          init          Prepare your working directory for other commands
          validate      Check whether the configuration is valid
          plan          Show changes required by the current configuration
          apply         Create or update infrastructure
          destroy       Destroy previously-created infrastructure
    
3.  Para usar Terraform con Google Cloud, debes asegurarte de que se completen las siguientes tareas en Google Cloud:
    
    *   [Crea o ten un Google Cloud proyecto](https://docs.cloud.google.com/resource-manager/docs/creating-managing-projects?hl=es-419).
    *   [Habilita la facturación](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled?hl=es-419#confirm_billing_is_enabled_on_a_project) para el Google Cloud proyecto.
    *   [Habilita las APIs](https://docs.cloud.google.com/apis/docs/getting-started?hl=es-419#enabling_apis) para los Google Cloud servicios con los que deseas trabajar.
    *   [Configura la autenticación](https://docs.cloud.google.com/docs/terraform/authentication?hl=es-419) para Terraform.

¿Qué sigue?
-----------

*   Revisa la [Guía de inicio rápido de Terraform para Google Cloud](https://docs.cloud.google.com/docs/terraform/create-vm-instance?hl=es-419)
*   Obtén información sobre los [comandos básicos de Terraform](https://docs.cloud.google.com/docs/terraform/basic-commands?hl=es-419).

Salvo que se indique lo contrario, el contenido de esta página está sujeto a la [licencia Atribución 4.0 de Creative Commons](https://creativecommons.org/licenses/by/4.0/), y los ejemplos de código están sujetos a la [licencia Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0). Para obtener más información, consulta las [políticas del sitio de Google Developers](https://developers.google.com/site-policies?hl=es-419). Java es una marca registrada de Oracle o sus afiliados.

Última actualización: 2026-11-11 (UTC)
