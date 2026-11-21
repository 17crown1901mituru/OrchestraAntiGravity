# Autorización y control de acceso &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/authorization-access-control

Gestión de Identidades y Accesos (IAM) es una herramienta que te permite controlar quién puede hacer qué en tu Google Cloud entorno.

El acceso se controla con permisos de gestión de identidades y accesos, que son necesarios para trabajar con cualquier recurso en un Google Cloud entorno. Cuando se te conceden permisos para trabajar con un recurso, tienes _autorización_ para acceder a él. Si no tienes la autorización adecuada, no podrás acceder a los recursos de Google Cloud.

Permisos y roles
----------------

Para trabajar con un recurso, tu cuenta de usuario debe tener los permisos pertinentes para acceder a él.

Normalmente, el administrador de gestión de identidades y accesos es el responsable de controlar el acceso a los recursos. Tu administrador puede darte permisos para acceder a un solo recurso o a todos los recursos de un proyecto, una carpeta o una organización. Los administradores conceden los permisos pertinentes a tu cuenta de usuario en paquetes llamados _roles_. Siempre que tu cuenta de usuario tenga un rol con los permisos adecuados, podrás usar ese rol para acceder a los recursos de Google Cloud .

Por lo general, el flujo de trabajo para realizar una acción en cualquier recurso de tu entorno deGoogle Cloud es el siguiente:

1.  Quieres realizar una acción en un recurso (por ejemplo, subir un objeto a un segmento de Cloud Storage), pero no tienes los permisos adecuados. Sin los permisos, no podrás realizar la acción.
2.  Puedes solicitar los permisos que necesites a tu administrador de IAM a través del sistema de gestión de solicitudes que prefieras o directamente desde el mensaje de error de permisos de la consola Google Cloud .
3.  Tu administrador de IAM asigna un rol que contiene los permisos adecuados a tu cuenta de usuario. Ahora puedes realizar la acción.

Usar la gestión de identidades y accesos como administrador
-----------------------------------------------------------

Normalmente, los administradores son los responsables de asignar roles a los usuarios para que puedan acceder a los recursos. Google Cloud Los usuarios se representan mediante identidades autenticadas conocidas como _principales_.

Para asignar roles a una entidad principal en un recurso, se debe editar la _política de permiso_ asociada al recurso. Las políticas de permisos indican qué entidades principales tienen acceso al recurso y qué acciones pueden realizar en él. Gestión de identidades y accesos usa las políticas de permiso para determinar si una entidad principal tiene los permisos necesarios para acceder al recurso. Por lo tanto, para conceder acceso a un principal a un recurso concreto, debe actualizar la política de permisos del recurso con el principal y los roles que quiera conceder.

Los administradores pueden conceder roles a los principales en los siguientes tipos de recursos:

*   **Proyectos, carpetas y organizaciones:** estos recursos son los contenedores que se usan para estructurar la [jerarquía de recursos](https://docs.cloud.google.com/iam/docs/resource-hierarchy-access-control?hl=es). Los roles que asignes a estos recursos de contenedor se aplicarán a todos los recursos específicos del servicio que contengan.
*   **Recursos específicos de un servicio:** son las funciones o los componentes que ofrece un servicio. Por ejemplo, Compute Engine tiene recursos como instancias, discos y subredes. Conceder roles en un recurso específico de un servicio proporciona un control de acceso más granular que conceder roles en un recurso de contenedor, ya que limita el acceso de un usuario solo a ese recurso.

Control de acceso avanzado con gestión de identidades y accesos
---------------------------------------------------------------

Las políticas de permiso son el método más habitual para controlar el acceso a un Google Cloud entorno con gestión de identidades y accesos. Sin embargo, IAM también ofrece otras opciones más avanzadas para controlar el acceso, como las siguientes:

*   Tipos de políticas adicionales, como las de denegación y las de límites de acceso de principales
*   Controles de acceso condicionales basados en atributos
*   Controles de acceso temporal, como Privileged Access Manager (PAM)

Otras formas de control de acceso
---------------------------------

Aunque la gestión de identidades y accesos es el método principal de control de acceso paraGoogle Cloud, hay otros servicios Google Cloud que pueden afectar al acceso de un usuario a los recursos.

A continuación, se indican algunos ejemplos de otros servicios que pueden afectar al acceso de un usuario:

*   [Administrador de contextos de acceso:](https://docs.cloud.google.com/access-context-manager/docs/overview?hl=es) Administrador de contextos de acceso te permite definir un control de acceso pormenorizado y basado en atributos para proyectos y recursos en Google Cloud.
*   [Identity-Aware Proxy (IAP):](https://docs.cloud.google.com/iap/docs/concepts-overview?hl=es) IAP usa un modelo de control de acceso a nivel de aplicación en el que se establece una capa de autorización central para las aplicaciones a las que se accede mediante HTTPS.
*   [Servicio de política de organización:](https://docs.cloud.google.com/resource-manager/docs/organization-policy/overview?hl=es) La política de organización te permite configurar restricciones en toda la jerarquía de recursos para que puedas controlar los recursos en la nube de tu organización de forma centralizada y programática.
*   [Controles de Servicio de VPC:](https://docs.cloud.google.com/vpc-service-controls/docs/overview?hl=es) Controles de Servicio de VPC te permite definir perímetros que ayudan a proteger los Google Cloud recursos y los datos Google Cloud de los servicios que especifiques explícitamente.

Siguientes pasos
----------------

*   Para obtener una descripción más detallada del sistema de gestión de identidades y accesos y de cómo funciona, consulta la [página de descripción general de IAM](https://docs.cloud.google.com/iam/docs/overview?hl=es) en la documentación de IAM.
*   Para obtener información sobre los mensajes de error de IAM, consulta el artículo [Solucionar problemas con mensajes de error de permisos](https://docs.cloud.google.com/iam/docs/permission-error-messages?hl=es) de la documentación de IAM.
