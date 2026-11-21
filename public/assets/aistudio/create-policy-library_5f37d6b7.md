# Criar uma biblioteca de políticas &nbsp;|&nbsp; Terraform &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/policy-validation/create-policy-library

Como administrador da organização, você precisa definir políticas que os desenvolvedores adotem ao aplicar a infraestrutura como código. O conjunto de políticas da organização é representado como uma biblioteca de políticas. Nesta página, você verá como criar um repositório de políticas centralizado e adicionar restrições.

Antes de começar
----------------

*   É preciso ter um repositório Git vazio para armazenar a biblioteca de políticas da organização.
*   É preciso configurar o Git para se conectar com segurança. Por exemplo, se o repositório git estiver no GitHub, siga o processo descrito em [Como se conectar ao GitHub com SSH](https://docs.github.com/en/authentication/connecting-to-github-with-ssh).

Copiar a biblioteca de amostra
------------------------------

O Google oferece um repositório de amostra que inclui um conjunto de modelos de restrição predefinidos que você pode modificar para seu uso pessoal.

1.  Clone o repositório de amostra da biblioteca de políticas e duplique-o no POLICY\_LIBRARY\_REPO:
    
    git clone https://github.com/GoogleCloudPlatform/policy-library.git
    cd policy-library
    git remote set-url origin POLICY\_LIBRARY\_REPO
    git push origin main
    
2.  Examine os modelos de restrição disponíveis em `policies/templates`: `ls policies/templates`
    
3.  Selecione os modelos de restrição que você quer usar. Neste exemplo, escolha `gcp_storage_location_v1.yaml`, que aplica a localização para buckets do Cloud Storage.
    
4.  Crie arquivos YAML de restrição correspondentes a esses modelos em `policies/constraints`.
    
5.  Na sua cópia local do repositório `policy-library`, use os seguintes comandos para confirmar e enviar suas alterações:
    
    git add --all .
        git commit -m "Initial commit of policy library constraints"
        git push -u origin main
    

### Estrutura da biblioteca

Um repositório de biblioteca de políticas contém os seguintes diretórios:

*   `policies/`: esse diretório inclui dois subdiretórios:
    
    *   `constraints/`: esse diretório inicialmente está vazio. Coloque os arquivos de restrição aqui.
    *   `templates/`: esse diretório contém modelos de restrição predefinidos.
*   `validator/`: esse diretório contém os arquivos `.rego` e os testes de unidade associados a eles. Você não precisa tocar nesse diretório, a menos que pretenda modificar modelos de restrição existentes ou criar novos. Executar `make build` in-line o conteúdo do Rego nos arquivos de modelo de restrição correspondentes.
    

Atualizações periódicas
-----------------------

Periodicamente, você deve extrair todas as alterações do repositório público, que podem conter novos modelos e arquivos Rego.

git remote add public https://github.com/GoogleCloudPlatform/policy-library.git
    git pull public main
    git push origin main

Próximas etapas
---------------

A biblioteca de políticas contém vários modelos de restrição e uma pasta `samples` com restrições de exemplo. Leia [Criar restrições do Terraform](https://docs.cloud.google.com/docs/terraform/policy-validation/create-terraform-constraints?hl=pt-br) ou [Criar restrições de CAI](https://docs.cloud.google.com/docs/terraform/policy-validation/create-cai-constraints?hl=pt-br) para ver detalhes sobre como escrever e usar restrições e modelos de restrição.
