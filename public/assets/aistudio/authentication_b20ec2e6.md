# Autenticação do Terraform &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/authentication

Este documento descreve como se autenticar no Google Cloud ao usar o Terraform.

O Application Default Credentials (ADC) é a maneira recomendada de autenticação no Google Cloud ao usar o Terraform. O ADC é uma estratégia usada pelas bibliotecas de autenticação do Google para encontrar credenciais automaticamente com base no ambiente do aplicativo. Quando você usa o ADC, o Terraform pode ser executado em um ambiente de desenvolvimento ou ambiente de produção sem alterar a forma como ele se autentica nos serviços e nas APIs doGoogle Cloud . Para informações sobre onde o ADC procura credenciais e em que ordem, consulte [Como o Application Default Credentials funciona](https://docs.cloud.google.com/docs/authentication/application-default-credentials?hl=pt-br).

Autenticar ao usar o Terraform em um ambiente de desenvolvimento local
----------------------------------------------------------------------

Quando você usa o Terraform em um ambiente de desenvolvimento local, como uma estação de trabalho de desenvolvimento, é possível autenticar usando as credenciais associadas com a sua [conta de usuário](https://docs.cloud.google.com/docs/authentication?hl=pt-br#user-accounts) ou [conta de serviço](https://docs.cloud.google.com/iam/docs/service-account-overview?hl=pt-br).

### Autenticar usando uma conta de usuário

Para configurar o ADC com uma conta de usuário, use a Google Cloud CLI:

1.  [Instale](https://docs.cloud.google.com/sdk/docs/install?hl=pt-br) a CLI do Google Cloud. Após a instalação, [inicialize](https://docs.cloud.google.com/sdk/docs/initializing?hl=pt-br) a Google Cloud CLI executando o seguinte comando:
    
    gcloud init
    
    Ao usar um provedor de identidade (IdP) externo, primeiro [faça login na CLI gcloud com sua identidade federada](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud?hl=pt-br).
    
2.  Se você estiver usando um shell local, crie credenciais de autenticação local para sua conta de usuário:
    
    gcloud auth application-default login
    
    Não é necessário fazer isso se você estiver usando o Cloud Shell.
    
    Se um erro de autenticação for retornado e você estiver usando um provedor de identidade (IdP) externo, confirme se você [fez login na CLI gcloud com sua identidade federada](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud?hl=pt-br).
    
    Uma tela de login é exibida. Após o login, suas credenciais são armazenadas no [arquivo de credenciais local usado pelo ADC](https://docs.cloud.google.com/docs/authentication/application-default-credentials?hl=pt-br#personal).
    

### Autenticar usando a identidade temporária de conta de serviço

Você pode usar a identidade temporária de conta de serviço para configurar um arquivo ADC local. O Terraform usa essas credenciais automaticamente.

1.  Verifique se você tem o papel do IAM de Criador de token da conta de serviço (`roles/iam.serviceAccountTokenCreator`) na conta de serviço que está representando. Para mais informações, consulte [Papéis necessários](https://docs.cloud.google.com/docs/authentication/use-service-account-impersonation?hl=pt-br#required-roles).
    
2.  Use a identidade temporária de conta de serviço para criar um arquivo ADC local executando o seguinte comando:
    
    gcloud auth application-default login --impersonate-service-account SERVICE_ACCT_EMAIL
    

Se você quiser permitir que os usuários usem uma fonte de autenticação primária compartilhada e uma conta de serviço variável por ambiente, defina o campo [`impersonate_service_account`](https://registry.terraform.io/providers/hashicorp/google/latest/docs/guides/provider_reference#impersonate_service_account) no arquivo de configuração do Terraform:

provider "google" {
      impersonate_service_account = "SERVICE_ACCT_EMAIL"
    }

Autenticar ao executar o Terraform no Google Cloud
--------------------------------------------------

Ao executar o Terraform em um ambiente de desenvolvimento baseado na nuvem do Google Cloud , como o Cloud Shell, a ferramenta usa as credenciais fornecidas ao fazer login para autenticação.

Ao usar o Terraform com serviços do Google Cloud , como o Compute Engine, o App Engine e as funções do Cloud Run, é possível anexar uma [conta serviço gerenciado pelo usuário](https://docs.cloud.google.com/iam/docs/service-account-types?hl=pt-br#user-created) aos recursos. Geralmente, a anexação de uma conta de serviço acontece quando os recursos desse serviço podem ser executados ou incluem o código do aplicativo. Quando você vincula uma conta de serviço a um recurso, o código em execução no recurso pode usar essa conta de serviço como identidade.

Anexar uma conta de serviço gerenciada pelo usuário é a maneira preferencial de fornecer credenciais ao ADC para códigos de produção em execução no Google Cloud.

Se precisar de ajuda para determinar os papéis que você precisa fornecer à conta de serviço, consulte [Escolher papéis predefinidos](https://docs.cloud.google.com/iam/docs/choose-predefined-roles?hl=pt-br).

Para informações sobre quais recursos podem ser anexados a uma conta de serviço e ajuda para anexar a conta de serviço ao recurso, consulte a [Documentação do IAM sobre como anexar uma conta de serviço](https://docs.cloud.google.com/iam/docs/attach-service-accounts?hl=pt-br#attaching-new-resource).

Configure a autenticação:

1.  Verifique se você tem o papel do IAM de criação de contas de serviço (`roles/iam.serviceAccountCreator`) e o papel de administrador do IAM do projeto (`roles/resourcemanager.projectIamAdmin`). [Saiba como conceder papéis](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=pt-br).
2.  Crie a conta de serviço:
    
    gcloud iam service-accounts create SERVICE\_ACCOUNT\_NAME
    
    Substitua `SERVICE_ACCOUNT_NAME` por um nome para a conta de serviço.
    
3.  Para conceder acesso ao projeto e aos recursos, conceda um papel à conta de serviço:
    
    gcloud projects add-iam-policy-binding PROJECT\_ID \--member\="serviceAccount:SERVICE\_ACCOUNT\_NAME@PROJECT\_ID.iam.gserviceaccount.com" \--role\=ROLE
    
    Substitua:
    
    *   `SERVICE_ACCOUNT_NAME`: o nome da conta de serviço.
    *   `PROJECT_ID`: o ID do projeto em que você criou a conta de serviço
    *   `ROLE`: o papel a ser concedido
4.  Para conceder outro papel à conta de serviço, execute o comando que você fez na etapa anterior.
5.  Conceda ao principal o papel necessário para anexar a conta de serviço a outros recursos.
    
    gcloud iam service-accounts add-iam-policy-binding SERVICE\_ACCOUNT\_NAME@PROJECT\_ID.iam.gserviceaccount.com \--member\="user:USER\_EMAIL" \--role\=roles/iam.serviceAccountUser
    
    Substitua:
    
    *   `SERVICE_ACCOUNT_NAME`: o nome da conta de serviço.
    *   `PROJECT_ID`: o ID do projeto em que você criou a conta de serviço
    *   `USER_EMAIL`: o endereço de e-mail de uma Conta do Google

Autenticar ao executar o Terraform no local ou em outro provedor de nuvem
-------------------------------------------------------------------------

Se o aplicativo estiver sendo executado fora do Google Cloud, será necessário fornecer credenciais reconhecidas pelo Google Cloud para usar os serviços doGoogle Cloud .

### Autenticar usando a federação de identidade da carga de trabalho

A maneira mais comum de autenticar com o Google Cloud usando credenciais de um IdP externo é usar a [federação de identidade da carga de trabalho](https://docs.cloud.google.com/iam/docs/workload-identity-federation?hl=pt-br). É possível criar um arquivo de configuração de credenciais e definir a variável de ambiente `GOOGLE_APPLICATION_CREDENTIALS` para apontar para ele. Essa abordagem é mais segura do que criar uma chave de conta de serviço. Para instruções sobre como configurar a federação de identidade da carga de trabalho para o ADC, consulte [Federação de identidade da carga de trabalho com outras nuvens](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-other-clouds?hl=pt-br).

Autenticar usando chaves de conta de serviço
--------------------------------------------

Ao executar o Terraform em um ambiente de desenvolvimento local, no local ou em um provedor de nuvem diferente, é possível criar uma conta de serviço, conceder a ela os papéis do IAM necessários para o aplicativo e criar uma chave para a conta de serviço.

Para criar uma chave de conta de serviço e disponibilizá-la ao ADC, faça o seguinte:

1.  Crie uma conta de serviço com os papéis de que seu aplicativo precisa e uma chave para essa conta de serviço. Para fazer isso, siga as instruções em [Como criar uma chave de conta de serviço](https://docs.cloud.google.com/iam/docs/keys-create-delete?hl=pt-br#creating).
    

Autenticar nos back-ends do Cloud Storage
-----------------------------------------

O Terraform permite configurar o Cloud Storage como um back-end para armazenar arquivos de estado. Para fazer a autenticação em um [back-end do Cloud Storage](https://docs.cloud.google.com/docs/terraform/resource-management/store-state?hl=pt-br), use qualquer um dos métodos descritos nesta página. Para informações sobre variáveis de configuração relacionadas à autenticação para back-ends do Cloud Storage, consulte a [Página de back-ends do Terraform para o Cloud Storage](https://developer.hashicorp.com/terraform/language/settings/backends/gcs#configuration-variables).

A seguir
--------

*   Trabalhe no [Guia de início rápido do Terraform para Google Cloud](https://docs.cloud.google.com/docs/terraform/create-vm-instance?hl=pt-br)
*   Saiba mais sobre os [comandos básicos do Terraform](https://docs.cloud.google.com/docs/terraform/basic-commands?hl=pt-br).
