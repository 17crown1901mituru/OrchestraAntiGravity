# Terraform, Cloud Build, GitOps를 사용하여 인프라를 코드로 관리 &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/resource-management/managing-infrastructure-as-code

이 튜토리얼에서는 널리 사용되는 GitOps 방법론을 사용하여 [Terraform](https://cloud.google.com/docs/terraform?hl=ko) 및 [Cloud Build](https://docs.cloud.google.com/build?hl=ko)로 코드형 인프라를 관리하는 방법을 설명합니다. _GitOps_는 [Weaveworks에 의해 처음 창안](https://web.archive.org/web/20240202145840/https://www.weave.works/blog/gitops-operations-by-pull-request)된 용어로, Git 저장소를 사용하여 원하는 환경 상태를 저장하는 것이 핵심 개념입니다. Terraform은 코드를 사용하여 클라우드 인프라를 예측 가능하게 만들고, 변경하고, 개선할 수 있는 [HashiCorp](https://www.hashicorp.com/) 도구입니다. 이 튜토리얼에서는 [Cloud Build](https://docs.cloud.google.com/build?hl=ko)( Google Cloud 지속적 통합 서비스)를 사용하여 환경에 Terraform 매니페스트를 자동으로 적용합니다.

이 튜토리얼은 인프라를 예측 가능하게 변경할 수 있는 효과적인 전략을 찾고 있는 개발자와 운영자를 대상으로 합니다. 이 도움말에서는 개발자가 Google Cloud, Linux, GitHub에 익숙하다고 가정합니다.

[State of DevOps](https://cloud.google.com/devops/?hl=ko) 보고서에는 소프트웨어 배포 성능을 향상시키는 기능이 나와 있습니다. 이 튜토리얼에서는 다음 기능을 설명합니다.

*   [버전 제어](https://dora.dev/devops-capabilities/technical/version-control/)
*   [지속적 통합](https://dora.dev/devops-capabilities/technical/continuous-integration/)
*   [지속적 배포](https://dora.dev/devops-capabilities/technical/continuous-delivery/)
*   [지속적 테스트](https://dora.dev/devops-capabilities/technical/test-automation/)

아키텍처
----

이 튜토리얼에서 Terraform 실행 관리에 GitOps 사례를 어떻게 적용하는지 알아보려면 다음 아키텍처 다이어그램을 살펴보세요. 이 다이어그램에서는 GitHub 분기(`dev` 및 `prod`)를 사용하여 실제 환경을 나타냅니다. 이러한 환경은 가상 프라이빗 클라우드(VPC) 네트워크(각각 `dev` 및 `prod`)에 의해 Google Cloud 프로젝트에 정의됩니다.

Terraform 코드를 `dev` 브랜치 또는 `prod` 브랜치로 푸시하면 프로세스가 시작됩니다. 이 시나리오에서 Cloud Build는 각 환경에서 Terraform 매니페스트를 트리거한 후 적용하여 원하는 상태를 달성합니다. 반면 Terraform 코드를 다른 브랜치(예: 기능 브랜치)로 푸시하면 Cloud Build가 실행되어 `terraform plan`을 실행하지만 환경에는 아무것도 적용되지 않습니다.

개발자나 운영자는[보호되지 않는 브랜치](https://help.github.com/en/articles/about-protected-branches)에 인프라 제안을 한 후 [pull 요청](https://help.github.com/en/articles/about-pull-requests)을 통해 이를 제출하는 것이 좋습니다. 이 튜토리얼의 뒷부분에서 설명하는 [Cloud Build GitHub 앱](https://github.com/marketplace/google-cloud-build)은 빌드 작업을 자동으로 트리거하고 `terraform plan` 보고서를 이러한 pull 요청에 연결합니다. 이렇게 하면 공동작업자와 잠재적인 변경사항을 논의 및 검토하고 변경사항이 기본 브랜치에 병합되기 전에 후속 커밋을 추가할 수 있습니다.

문제가 발생하지 않으면 먼저 변경사항을 `dev` 브랜치에 병합해야 합니다. 이 병합으로 인해 `dev` 환경에 대한 인프라 배포가 트리거되므로 이 환경을 테스트할 수 있습니다. 배포한 내용을 테스트하고 확인한 후에는 `dev` 브랜치를 `prod` 브랜치로 병합하여 프로덕션 환경에 대한 인프라 설치를 트리거해야 합니다.

목표
--

*   GitHub 저장소를 설정합니다.
*   Cloud Storage 버킷에 상태를 저장하도록 Terraform을 구성합니다.
*   Cloud Build 서비스 계정에 권한을 부여합니다.
*   Cloud Build를 GitHub 저장소에 연결합니다.
*   기능 브랜치에서 환경 구성을 변경합니다.
*   변경사항을 개발 환경으로 승격합니다.
*   변경사항을 프로덕션 환경으로 승격합니다.

비용
--

이 문서에서는 비용이 청구될 수 있는 Google Cloud구성요소( )를 사용합니다.

*   [Cloud Build](https://docs.cloud.google.com/build/pricing?hl=ko)
*   [Cloud Storage](https://docs.cloud.google.com/storage/pricing?hl=ko)
*   [Compute Engine](https://docs.cloud.google.com/compute/pricing?hl=ko)

프로젝트 사용량을 기준으로 예상 비용을 산출하려면 [가격 계산기](https://docs.cloud.google.com/products/calculator?hl=ko)를 사용합니다.

Google Cloud 신규 사용자는 [무료 체험판](https://docs.cloud.google.com/free?hl=ko)을 사용할 수 있습니다.

이 문서에 설명된 태스크를 완료했으면 만든 리소스를 삭제하여 청구가 계속되는 것을 방지할 수 있습니다. 자세한 내용은 [삭제](#clean-up)를 참조하세요.

기본 요건
-----

*   Sign in to your Google Cloud account. If you're new to Google Cloud, [create an account](https://console.cloud.google.com/freetrial?hl=ko) to evaluate how our products perform in real-world scenarios. New customers also get $300 in free credits to run, test, and deploy workloads.
*   In the Google Cloud console, on the project selector page, select or create a Google Cloud project.
    
    **Roles required to select or create a project**
    
    *   **Select a project**: Selecting a project doesn't require a specific IAM role—you can select any project that you've been granted a role on.
    *   **Create a project**: To create a project, you need the Project Creator role (`roles/resourcemanager.projectCreator`), which contains the `resourcemanager.projects.create` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ko).
    
    [Go to project selector](https://console.cloud.google.com/projectselector2/home/dashboard?hl=ko)
    
*   [Verify that billing is enabled for your Google Cloud project](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled?hl=ko#confirm_billing_is_enabled_on_a_project).
    

*   In the Google Cloud console, on the project selector page, select or create a Google Cloud project.
    
    **Roles required to select or create a project**
    
    *   **Select a project**: Selecting a project doesn't require a specific IAM role—you can select any project that you've been granted a role on.
    *   **Create a project**: To create a project, you need the Project Creator role (`roles/resourcemanager.projectCreator`), which contains the `resourcemanager.projects.create` permission. [Learn how to grant roles](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ko).
    
    [Go to project selector](https://console.cloud.google.com/projectselector2/home/dashboard?hl=ko)
    
*   [Verify that billing is enabled for your Google Cloud project](https://docs.cloud.google.com/billing/docs/how-to/verify-billing-enabled?hl=ko#confirm_billing_is_enabled_on_a_project).
    

2.  In the Google Cloud console, activate Cloud Shell.
    
    [Activate Cloud Shell](https://console.cloud.google.com/?cloudshell=true&hl=ko)
    
    At the bottom of the Google Cloud console, a [Cloud Shell](https://docs.cloud.google.com/shell/docs/how-cloud-shell-works?hl=ko) session starts and displays a command-line prompt. Cloud Shell is a shell environment with the Google Cloud CLI already installed and with values already set for your current project. It can take a few seconds for the session to initialize.
    
3.  Cloud Shell에서 방금 선택한 프로젝트의 ID를 가져옵니다.
    
    gcloud config get-value project
    
    이 명령어가 프로젝트 ID를 반환하지 않으면 프로젝트를 사용하도록 Cloud Shell을 구성합니다. `PROJECT_ID`를 프로젝트 ID로 바꿉니다.
    
    gcloud config set project PROJECT\_ID
    
4.  필요한 API를 사용 설정합니다.
    
    gcloud services enable cloudbuild.googleapis.com compute.googleapis.com
    
    이 단계를 완료하는 데 몇 분 정도 걸릴 수 있습니다.
5.  Cloud Shell에서 Git을 사용한 적이 없으면 내 이름과 이메일 주소를 사용하여 Git을 구성합니다.git config --global user.email "YOUR\_EMAIL\_ADDRESS"
    git config --global user.name "YOUR\_NAME"
    Git은 이 정보를 사용하여 Cloud Shell에서 만드는 커밋의 작성자를 식별합니다.

GitHub 저장소 설정
-------------

이 튜토리얼에서는 단일 Git 저장소를 사용하여 클라우드 인프라를 정의합니다. 서로 다른 환경에 해당하는 각각의 브랜치를 사용하여 이 인프라를 조정합니다.

*   `dev` 브랜치에는 개발 환경에 적용되는 최신 변경사항이 포함됩니다.
*   `prod` 브랜치에는 프로덕션 환경에 적용되는 최신 변경사항이 포함됩니다.

이 인프라를 사용하면 언제든지 저장소를 참조하여 각 환경에 필요한 구성을 파악하고 먼저 이를 `dev` 환경에 병합해 새로운 변경사항을 제안할 수 있습니다. 그런 다음 `dev` 브랜치를 후속 `prod` 브랜치에 병합해 변경사항을 승격합니다.

시작하려면 [solutions-terraform-cloudbuild-gitops](https://github.com/GoogleCloudPlatform/solutions-terraform-cloudbuild-gitops.git) 저장소를 포크합니다.

1.  GitHub에서 [https://github.com/GoogleCloudPlatform/solutions-terraform-cloudbuild-gitops.git](https://github.com/GoogleCloudPlatform/solutions-terraform-cloudbuild-gitops.git)으로 이동합니다.
2.  페이지 오른쪽 상단에서 **포크**를 클릭합니다.
    
    이제 소스 파일이 있는 `solutions-terraform-cloudbuild-gitops` 저장소의 복사본이 준비되었습니다.
    

1.  Cloud Shell에서 `YOUR_GITHUB_USERNAME`을 GitHub 사용자 이름을 바꿔 이 포크된 저장소를 클론합니다.
    
    cd ~
    git clone https://github.com/YOUR\_GITHUB\_USERNAME/solutions-terraform-cloudbuild-gitops.git
    cd ~/solutions-terraform-cloudbuild-gitops
    

이 저장소의 코드는 다음과 같이 구성됩니다.

*   `environments/` 폴더에는 `dev` 및 `prod` 같은 환경을 나타내는 하위 폴더가 포함됩니다. 따라서 다양한 성숙도, 개발, 프로덕션 단계에서 워크로드 간에 논리적인 구분이 가능합니다. 이러한 환경을 최대한 유사하게 유지하는 것이 좋지만 각 하위 폴더에는 필요에 따라 고유한 설정을 지정할 수 있는 자체 Terraform 구성이 있습니다.
    
*   `modules/` 폴더에는 인라인 Terraform 모듈이 있습니다. 이러한 모듈은 관련 리소스의 논리적 그룹을 나타내며 서로 다른 환경에서 코드를 공유하는 데 사용됩니다.
    
*   `cloudbuild.yaml` 파일은 일련의 단계를 바탕으로 태스크를 수행하는 방법 등 Cloud Build에 내릴 명령이 포함된 빌드 구성 파일입니다. 이 파일은 Cloud Build가 코드를 가져오는 브랜치에 따라 조건부 실행을 지정합니다. 예를 들면 다음과 같습니다.
    
    *   `dev` 및 `prod` 분기의 경우 다음 단계가 실행됩니다.
        
        1.  `terraform init`
        2.  `terraform plan`
        3.  `terraform apply`
    *   다른 브랜치의 경우 다음 단계가 실행됩니다.
        
        1.  모든 `environments` 하위 폴더에 `terraform init`
        2.  모든 `environments` 하위 폴더에 `terraform plan`

제안된 변경사항이 모든 환경에 적합한지 확인하려면 `terraform init` 및 `terraform plan`을 모든 `environments` 하위 폴더에 대해 실행합니다. pull 요청을 병합하기 전에 요금제를 검토하여 예를 들어 승인되지 않은 항목에 액세스 권한이 부여되지 않도록 할 수 있습니다.

Cloud Storage 버킷에 상태를 저장하도록 Terraform 구성
----------------------------------------

기본적으로 Terraform은 [상태](https://www.terraform.io/docs/state/)를 `terraform.tfstate`라는 파일에 로컬로 저장합니다. 이러한 기본 구성으로 인해 여러 사용자가 동시에 Terraform을 실행하고 각 머신이 현재 인프라를 자체적으로 이해할 경우에는 팀의 Terraform 사용이 어려워질 수 있습니다.

이러한 문제를 방지하기 위해 이 섹션에서는 Cloud Storage 버킷을 가리키는 [원격 상태](https://www.terraform.io/docs/state/remote.html)를 구성합니다. 원격 상태는 [backends](https://www.terraform.io/docs/backends)의 기능으로, 이 튜토리얼에서는 `backend.tf` 파일에 구성됩니다. 예를 들면 다음과 같습니다.

다음 단계에서는 Cloud Storage 버킷을 만들고 새 버킷과 Google Cloud 프로젝트를 가리키도록 일부 파일을 변경합니다.

1.  Cloud Shell에서 Cloud Storage 버킷을 만듭니다.
    
    PROJECT_ID=$(gcloud config get-value project)
        gcloud storage buckets create gs://${PROJECT_ID}-tfstate
    1\. [객체 버전 관리](https://docs.cloud.google.com/storage/docs/object-versioning?hl=ko)를 사용 설정하여 배포 기록을 유지합니다.```sh
        gcloud storage buckets update gs://${PROJECT_ID}-tfstate --versioning
        ```
        
        Enabling Object Versioning increases
        [storage costs](https://cloud.google.com/storage/pricing){: track-type="tutorial" track-name="internalLink" track-metadata-position="body" },
        which you can mitigate by configuring
        [Object Lifecycle Management](/storage/docs/lifecycle){: track-type="tutorial" track-name="internalLink" track-metadata-position="body" } 
        to delete old state versions.
    
    1.  `terraform.tfvars` 파일과 `backend.tf` 파일 모두에서 `PROJECT_ID` 자리표시자를 프로젝트 ID로 바꿉니다.
        
        cd ~/solutions-terraform-cloudbuild-gitops
        sed -i s/PROJECT\_ID/$PROJECT\_ID/g environments/\*/terraform.tfvars
        sed -i s/PROJECT\_ID/$PROJECT\_ID/g environments/\*/backend.tf
        
        OS X/Mac OS에서는 다음과 같이 `sed -i` 뒤에 따옴표(`""`)를 두 개 추가해야 할 수 있습니다.
        
        cd ~/solutions-terraform-cloudbuild-gitops
        sed -i "" s/PROJECT\_ID/$PROJECT\_ID/g environments/\*/terraform.tfvars
        sed -i "" s/PROJECT\_ID/$PROJECT\_ID/g environments/\*/backend.tf
        
    2.  모든 파일이 업데이트되었는지 확인합니다.
        
        git status
        
        출력 형식은 다음과 같습니다.
        
        On branch dev
        Your branch is up-to-date with 'origin/dev'.
        Changes not staged for commit:
         (use "git add <file>..." to update what will be committed)
         (use "git checkout -- <file>..." to discard changes in working directory)
               modified:   environments/dev/backend.tf
               modified:   environments/dev/terraform.tfvars
               modified:   environments/prod/backend.tf
               modified:   environments/prod/terraform.tfvars
        no changes added to commit (use "git add" and/or "git commit -a")
        
    3.  변경사항을 커밋하고 푸시합니다.
        
        git add --all
            git commit -m "Update project IDs and buckets"
            git push origin dev
        
        GitHub 구성에 따라 인증하여 이전 변경사항을 푸시해야 합니다.
        
    
    Cloud Build 서비스 계정에 권한 부여
    -------------------------
    
    [Cloud Build 서비스 계정](https://docs.cloud.google.com/build/docs/securing-builds/set-service-account-permissions?hl=ko)에서 Google Cloud 리소스를 관리하기 위해 Terraform 스크립트를 실행하려면 프로젝트에 적절한 액세스 권한을 부여해야 합니다. 편의를 위해 이 튜토리얼에서는 [프로젝트 편집자](https://docs.cloud.google.com/iam/docs/understanding-roles?hl=ko#basic) 액세스 권한을 부여합니다. 하지만 프로젝트 편집자 역할이 폭넓은 권한을 가진 경우 프로덕션 환경에서는 회사의 IT 보안 권장사항을 따라야 합니다. 일반적으로 최소한의 액세스 권한을 제공하는 것이 좋습니다. 보안 권장사항은 [모든 액세스 시도를 명시적으로 확인](https://docs.cloud.google.com/architecture/framework/security/implement-zero-trust?hl=ko#verify_every_access_attempt_explicitly)을 참조하세요.
    
    1.  Cloud Shell에서 프로젝트의 Cloud Build 서비스 계정의 이메일을 가져옵니다.
        
        CLOUDBUILD_SA="$(gcloud projects describe $PROJECT_ID \
                --format 'value(projectNumber)')@cloudbuild.gserviceaccount.com"
        
    2.  Cloud Build 서비스 계정에 필요한 액세스 권한을 부여합니다.
        
        gcloud projects add-iam-policy-binding $PROJECT_ID \
                --member serviceAccount:$CLOUDBUILD_SA --role roles/editor
        
    
    Cloud Build를 GitHub 저장소에 직접 연결
    ------------------------------
    
    이 섹션에서는 [Cloud Build GitHub 앱](https://github.com/marketplace/google-cloud-build)을 설치하는 방법을 설명합니다. 이 앱을 설치하면 GitHub 저장소와Google Cloud 프로젝트를 연결할 수 있어 새 브랜치를 만들거나 코드를 GitHub에 푸시할 때마다 Cloud Build가 Terraform 매니페스트를 자동으로 적용할 수 있습니다.
    
    다음 단계에서는 `solutions-terraform-cloudbuild-gitops` 저장소에 앱을 설치하는 방법만 안내합니다. 하지만 개발자는 전체 또는 일부 저장소에 앱을 설치할 수 있습니다.
    
    1.  Cloud Build 앱의 GitHub Marketplace 페이지로 이동합니다.
        
        [Cloud Build 앱 페이지 열기](https://github.com/marketplace/google-cloud-build)
        
        *   GitHub에서 앱을 처음 구성할 때는 페이지 하단에서 **Google Cloud Build로 설정**을 클릭한 후 **이 앱에 GitHub 계정 액세스 권한 부여**를 클릭합니다.
        *   GitHub에서 앱을 처음으로 구성하는 것이 아니라면 **액세스 구성**을 클릭합니다. 개인 계정의 **애플리케이션** 페이지가 열립니다.
    2.  Cloud Build 행에서 **구성**을 클릭합니다.
        
    3.  **저장소만 선택**을 선택한 후 **`solutions-terraform-cloudbuild-gitops`**를 선택하여 저장소에 연결합니다.
        
    4.  **저장** 또는 **설치**를 클릭합니다. 버튼 라벨은 워크플로에 따라 다릅니다. 설치를 계속할 수 있도록 Google Cloud 로 리디렉션됩니다.
        
    5.  Google Cloud 계정으로 로그인합니다. 요청이 있을 경우 GitHub와 Cloud Build 통합을 승인합니다.
        
    6.  **Cloud Build** 페이지에서 프로젝트를 선택합니다. 마법사가 표시됩니다.
        
    7.  **저장소 선택** 섹션에서 GitHub 계정과 **`solutions-terraform-cloudbuild-gitops`** 저장소를 선택합니다.
        
    8.  이용약관에 동의하면 체크박스를 선택하고 **연결**을 클릭합니다.
        
    9.  **트리거 만들기** 섹션에서 **트리거 만들기**를 클릭합니다.
        
        1.  트리거 이름을 추가합니다(예: `push-to-branch`). 나중에 필요하므로 이 트리거 이름을 기록해 두세요.
        2.  **이벤트** 섹션에서 **브랜치로 푸시**를 선택합니다.
        3.  **소스** 섹션의 **브랜치** 필드에서 `.*`를 선택합니다.
        4.  **만들기**를 클릭합니다.
    
    이제 Cloud Build GitHub 앱이 구성되었으며 GitHub 저장소가 Google Cloud 프로젝트에 연결되었습니다. 이제부터 GitHub 저장소를 변경하면 Cloud Build 실행이 트리거되고 [GitHub 검사](https://developer.github.com/v3/checks/)를 사용하여 GitHub에 결과가 다시 보고됩니다.
    
    새 기능 브랜치에서 환경 구성 변경
    -------------------
    
    지금까지 대부분의 환경을 구성했습니다. 이제 개발 환경에서 몇 가지 코드를 변경해야 합니다.
    
    1.  GitHub에서 포크된 저장소의 기본 페이지로 이동합니다.
        
        https://github.com/YOUR\_GITHUB\_USERNAME/solutions-terraform-cloudbuild-gitops
        
    2.  `dev` 브랜치에 있는지 확인합니다.
        
    3.  수정할 파일을 열려면 `modules/firewall/main.tf` 파일로 이동하고 연필 아이콘을 클릭합니다.
        
    4.  30행의 `target_tags` 필드에서 `"http-server2"` 오타를 수정합니다.
        
        값은 `"http-server"`여야 합니다.
        
    5.  페이지 하단에 'Fixing http firewall target'과 같은 커밋 메시지를 추가하고 **이 커밋에 사용할 새 브랜치를 만들고 pull 요청 시작**을 선택합니다.
        
    6.  **변경사항 제안**을 클릭합니다.
        
    7.  다음 페이지에서 **pull 요청 만들기**를 클릭하여 변경사항과 함께 새 pull 요청을 엽니다.
        
        pull 요청이 열리면 Cloud Build 작업이 자동으로 시작됩니다.
        
    8.  **모든 검사 표시**를 클릭하고 체크 표시가 녹색이 될 때까지 기다립니다.
        
    9.  **Google Cloud Build에 대한 자세한 내용보기** 링크에서 `terraform plan`의 출력을 비롯해 자세한 내용을 보려면 **세부정보**를 클릭합니다.
        
    
    pull 요청을 아직 병합하지 마세요.
    
    이 Cloud Build 작업은 `cloudbuild.yaml` 파일에서 정의된 파이프라인을 실행했습니다. 앞의 설명처럼 이 파이프라인은 가져올 브랜치에 따라 서로 다르게 동작합니다. 빌드는 `$BRANCH_NAME` 변수가 모든 환경 폴더와 일치하는지 검사합니다. 일치하면 Cloud Build는 해당 환경에 대해 `terraform plan`을 실행합니다. 일치하지 않으면 Cloud Build에서 모든 환경에 `terraform plan`을 실행하여 제안된 변경사항이 모든 환경에 적합한지 확인합니다. 이러한 요금제 중 하나라도 실행에 실패하면 빌드가 실패합니다.
    
    마찬가지로 `terraform apply` 명령어는 환경 분기에 대해 실행되지만 다른 경우에는 완전히 무시됩니다. 이 섹션에서는 새 브랜치에 대한 코드 변경사항을 제출했으므로 인프라 배포가 Google Cloud 프로젝트에 적용되지 않았습니다.
    
    분기 병합 전에 Cloud Build 실행 성공 적용
    -----------------------------
    
    각각의 Cloud Build 실행이 성공할 때만 병합을 적용하려면 다음 단계를 진행합니다.
    
    1.  GitHub에서 포크된 저장소의 기본 페이지로 이동합니다.
        
        https://github.com/YOUR\_GITHUB\_USERNAME/solutions-terraform-cloudbuild-gitops
        
    2.  저장소 이름 아래에서 **설정**을 클릭합니다.
        
    3.  왼쪽 메뉴에서 **브랜치**를 클릭합니다.
        
    4.  **브랜치 보호 규칙**에서 **규칙 추가**를 클릭합니다.
        
    5.  **브랜치 이름 패턴**에 `dev`를 입력합니다.
        
    6.  **일치하는 브랜치 보호** 섹션에서 **병합 전에 상태 검사를 통과해야 함**을 선택합니다.
        
    7.  이전에 만든 Cloud Build 트리거 이름을 검색합니다.
        
    8.  **만들기**를 클릭합니다.
        
    9.  3~7단계를 반복하여 **브랜치 이름 패턴**을 `prod`로 설정합니다.
        
    
    이 구성은 `dev` 브랜치와 `prod` 브랜치를 모두 [보호](https://help.github.com/en/articles/about-protected-branches)하는 데 중요합니다. 즉, 커밋이 먼저 다른 브랜치로 푸시되어야 하며 그런 다음에만 보호되는 브랜치에 병합할 수 있습니다. 이 튜토리얼의 보호에서는 Cloud Build 실행이 성공해야만 병합이 허용됩니다.
    
    병합 대기 중인 pull 요청이 있습니다. 이제 `dev` 환경에 원하는 상태를 적용할 차례입니다.
    
    1.  GitHub에서 포크된 저장소의 기본 페이지로 이동합니다.
        
        https://github.com/YOUR\_GITHUB\_USERNAME/solutions-terraform-cloudbuild-gitops
        
    2.  저장소 이름 아래에서 **pull 요청**을 클릭합니다.
        
    3.  방금 만든 pull 요청을 클릭합니다.
        
    4.  **pull 요청 병합**을 클릭한 후 **병합 확인**을 클릭합니다.
        
    5.  새 Cloud Build가 트리거되었는지 확인합니다.
        
        [Cloud Build 페이지로 이동](https://console.cloud.google.com/cloud-build/builds?hl=ko)
        
    6.  빌드를 열고 로그를 확인합니다.
        
        빌드가 완료되면 다음과 같은 내용이 표시됩니다.
        
        Step #3 - "tf apply": external\_ip = EXTERNAL\_IP\_VALUE
        Step #3 - "tf apply": firewall\_rule = dev-allow-http
        Step #3 - "tf apply": instance\_name = dev-apache2-instance
        Step #3 - "tf apply": network = dev
        Step #3 - "tf apply": subnet = dev-subnet-01
        
    7.  `EXTERNAL_IP_VALUE`를 복사하고 웹브라우저에서 주소를 엽니다.
        
        http://EXTERNAL\_IP\_VALUE
        
        이 프로비저닝에서는 VM을 부팅하고 방화벽 규칙을 전파하는 데 몇 초 정도 걸릴 수 있습니다. 최종적으로 웹브라우저에 **Environment: dev**가 표시됩니다.
        
    8.  Cloud Storage 버킷의 Terraform 상태 파일로 이동합니다.
        
        https://storage.cloud.google.com/PROJECT\_ID\-tfstate/env/dev/default.tfstate
        
    
    이제 개발 환경을 완벽하게 테스트했으므로 인프라 코드를 프로덕션으로 승격할 수 있습니다.
    
    1.  GitHub에서 포크된 저장소의 기본 페이지로 이동합니다.
        
        https://github.com/YOUR\_GITHUB\_USERNAME/solutions-terraform-cloudbuild-gitops
        
    2.  저장소 이름 아래에서 **pull 요청**을 클릭합니다.
        
    3.  **새 pull 요청**을 클릭합니다.
        
    4.  **기본 저장소**에 방금 포크한 저장소를 선택합니다.
        
    5.  **기본**의 경우 자체 기본 저장소에서 `prod`를 선택합니다. **비교**에는 `dev`를 선택합니다.
        
    6.  **pull 요청 만들기**를 클릭합니다.
        
    7.  **제목**에 `Promoting networking changes`와 같은 제목을 입력한 후 **pull 요청 만들기**를 클릭합니다.
        
    8.  Cloud Build의 `terraform plan` 세부정보를 비롯해 제안된 변경사항을 검토하고 **pull 요청 병합**을 클릭합니다.
        
    9.  **병합 확인**을 클릭합니다.
        
    10.  Google Cloud 콘솔에서 **빌드 기록** 페이지를 열어 프로덕션 환경에 적용되는 변경사항을 확인합니다.
         
         [Cloud Build 페이지로 이동](https://console.cloud.google.com/cloud-build/builds?hl=ko)
         
    11.  빌드가 완료될 때까지 기다린 다음 로그를 확인합니다.
         
         로그 끝에 다음과 같은 내용이 표시됩니다.
         
         Step #3 - "tf apply": external\_ip = EXTERNAL\_IP\_VALUE
         Step #3 - "tf apply": firewall\_rule = prod-allow-http
         Step #3 - "tf apply": instance\_name = prod-apache2-instance
         Step #3 - "tf apply": network = prod
         Step #3 - "tf apply": subnet = prod-subnet-01
         
    12.  `EXTERNAL_IP_VALUE`를 복사하고 웹브라우저에서 주소를 엽니다.
         
         http://EXTERNAL\_IP\_VALUE
         
         이 프로비저닝에서는 VM을 부팅하고 방화벽 규칙을 전파하는 데 몇 초 정도 걸릴 수 있습니다. 최종적으로 웹브라우저에 **Environment: prod**가 표시됩니다.
         
    13.  Cloud Storage 버킷의 Terraform 상태 파일로 이동합니다.
         
         https://storage.cloud.google.com/PROJECT\_ID\-tfstate/env/prod/default.tfstate
         
    
    Cloud Build에서 서버리스 코드형 인프라 파이프라인을 성공적으로 구성했습니다. 앞으로 다음을 해볼 수 있습니다.
    
    *   별도의 사용 사례에 배포를 추가합니다.
    *   필요에 맞게 추가 환경을 만듭니다.
    *   환경별로 VPC를 사용하는 대신 환경별로 프로젝트를 사용합니다.
    
    삭제
    --
    
    이 튜토리얼을 완료한 후에는 향후에 요금이 청구되지 않도록Google Cloud 에서 만든 리소스를 삭제합니다.
    
    ### 프로젝트 삭제
    
    1.  In the Google Cloud console, go to the **Manage resources** page.
        
        [Go to Manage resources](https://console.cloud.google.com/iam-admin/projects?hl=ko)
        
    2.  In the project list, select the project that you want to delete, and then click **Delete**.
    3.  In the dialog, type the project ID, and then click **Shut down** to delete the project.
    
    ### GitHub 저장소 삭제
    
    GitHub 저장소에서 새 pull 요청을 차단하지 않으려면 브랜치 보호 규칙을 삭제하면 됩니다.
    
    1.  GitHub에서 포크된 저장소의 기본 페이지로 이동합니다.
    2.  저장소 이름 아래에서 **설정**을 클릭합니다.
    3.  왼쪽 메뉴에서 **브랜치**를 클릭합니다.
    4.  **브랜치 보호 규칙** 섹션에서 `dev` 및 `prod` 행의 **삭제** 버튼을 클릭합니다.
    
    원하는 경우 GitHub에서 Cloud Build 앱을 완전히 제거할 수 있습니다.
    
    1.  GitHub **애플리케이션** 설정으로 이동합니다.
        
        [GitHub 애플리케이션 페이지로 이동](https://github.com/settings/installations)
        
    2.  **설치된 GitHub 앱** 탭의 **Cloud Build** 행에서 **구성**을 클릭합니다. 그런 다음 **위험 영역** 섹션에서 **Google Cloud 빌더 제거** 행의 **제거** 버튼을 클릭합니다.
        
        페이지 상단에 다음과 같은 메시지가 표시됩니다. '이제 다 되었습니다. Google Cloud Build를 제거하기 위한 작업이 대기 열에 추가되었습니다.'
        
    3.  **승인된 GitHub 앱** 탭에서 **Google Cloud Build** 행의 **취소** 버튼을 클릭한 다음 팝업에서 **액세스 취소를 확인합니다.**를 클릭합니다.
        
    
    GitHub 저장소를 유지하지 않으려면 다음 안내를 따르세요.
    
    1.  GitHub에서 포크된 저장소의 기본 페이지로 이동합니다.
    2.  저장소 이름 아래에서 **설정**을 클릭합니다.
    3.  **위험 영역**까지 아래로 스크롤합니다.
    4.  **이 저장소 삭제**를 클릭하고 확인 단계를 따릅니다.
    
    다음 단계
    -----
    
    *   [Cloud Foundation Toolkit 템플릿](https://docs.cloud.google.com/foundation-toolkit?hl=ko)을 사용하여Google Cloud에서 반복 가능한 기업용 기본 환경을 빠르게 구축하기
    *   이 튜토리얼에 설명된 GitOps 워크플로에 대해 알아보려면 Next' 19의 [Repeatable Google Cloud Environments at Scale With Cloud Build Infra-As-Code Pipelines](https://www.youtube.com/watch?v=3vfXQxWJazM&hl=ko)를 확인하세요.
    *   [Cloud Build를 사용한 GitOps 스타일의 지속적 배포](https://docs.cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build?hl=ko) 튜토리얼 확인하기
    *   [빌드 단계 순서 구성](https://docs.cloud.google.com/build/docs/configuring-builds/configure-build-step-order?hl=ko), [아티팩트 빌드, 테스트, 배포](https://docs.cloud.google.com/build/docs/configuring-builds/build-test-deploy-artifacts?hl=ko), [커스텀 빌드 단계 만들기](https://docs.cloud.google.com/build/docs/create-custom-build-steps?hl=ko) 등 Cloud Build의 고급 기능 살펴보기
    *   [Cloud Build로 Terraform 배포의 확장 및 규정 준수 보장](https://cloud.google.com/blog/products/devops-sre/terraform-gitops-with-google-cloud-build-and-storage?hl=ko)에 대한 블로그를 확인하세요.
    *   [DevOps](https://cloud.google.com/devops/?hl=ko)에 대한 리소스를 읽어보세요.
    *   이 튜토리얼과 관련된 DevOps 기능 자세히 알아보기
        *   [버전 제어](https://docs.cloud.google.com/solutions/devops/devops-tech-version-control?hl=ko)
        *   [지속적 통합](https://docs.cloud.google.com/solutions/devops/devops-tech-continuous-integration?hl=ko)
        *   [지속적 배포](https://docs.cloud.google.com/solutions/devops/devops-tech-continuous-delivery?hl=ko)
        *   [지속적 테스트](https://docs.cloud.google.com/solutions/devops/devops-tech-test-automation?hl=ko)
    *   [DevOps 빠른 점검](https://dora.dev/quickcheck/)을 사용하여 업계와 비교되는 자신의 현재 상태를 파악하기
