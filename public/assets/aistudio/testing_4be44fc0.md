# 테스트 권장사항 &nbsp;|&nbsp; Terraform &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/terraform/best-practices/testing

이 문서에서는 Google Cloud 용 Terraform 모듈과 구성을 테스트하기 위한 가이드라인과 권장사항을 제공합니다.

Terraform 모듈 및 구성을 테스트하기 위해서는 경우에 따라 애플리케이션 코드를 테스트할 때와 다른 패턴 및 규칙을 따라야 합니다. 애플리케이션 코드를 테스트할 때는 주로 애플리케이션 자체의 비즈니스 로직을 테스트하지만 인프라 코드를 완전히 테스트하기 위해서는 프로덕션 오류 위험을 최소화하기 위해 실제 클라우드 리소스를 배포해야 합니다. Terraform 테스트를 실행할 때는 몇 가지 사항에 유의해야 합니다.

*   Terraform 테스트를 실행하면 실제 인프라가 생성, 수정, 삭제되므로, 테스트 수행에 시간과 비용이 발생할 수 있습니다.
*   _엔드 투 엔드 아키텍처에 대해 순전히 단위 테스트를 실행할 수 없습니다_. 가장 좋은 방법은 아키텍처를 여러 모듈로 나누고 이를 개별적으로 테스트하는 것입니다. 이 방법은 빠른 테스트 런타임으로 인한 반복적인 개발 시간이 단축되고, 각 테스트에 대한 비용이 감소되고, 제어할 수 없는 요소들로 인한 테스트 실패 가능성이 줄어든다는 이점이 있습니다.
*   _가능한 경우 상태 재사용을 방지합니다_. 다른 구성과 함께 데이터를 공유하지만 이상적으로는 각 테스트를 독립적으로 수행하고 테스트 간 상태를 재사용하지 않아야 할 경우가 있습니다.

비용이 낮은 테스트 방법 먼저 사용
-------------------

Terraform 테스트를 위해 사용할 수 있는 방법은 여러 가지입니다. 비용, 실행 시간, 깊이 순으로 다음 방법들이 포함됩니다.

*   **정적 분석:** 컴파일러, 린터, 테스트 실행과 같은 도구를 사용해서 리소스를 배포하지 않고 구성의 문법 및 구조를 테스트합니다. 이렇게 하려면 [`terraform validate`](https://www.terraform.io/cli/commands/validate)를 사용합니다.
*   **모듈 통합 테스트**: 모듈이 올바르게 작동하는지 확인하기 위해 격리된 상태로 개별 모듈을 테스트합니다. 모듈의 통합 테스트에는 테스트 환경에 모듈을 배포하고 예상 리소스가 생성되는지 확인하는 과정이 포함됩니다. 다음과 같이 테스트를 쉽게 만들 수 있게 해주는 몇 가지 테스트 프레임워크가 있습니다.
    *   [Google의 청사진 테스트 프레임워크](https://pkg.go.dev/github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test)
    *   [Terratest](https://terratest.gruntwork.io/)
    *   [Kitchen-Terraform](https://newcontext-oss.github.io/kitchen-terraform/)
    *   [InSpec](https://github.com/inspec/inspec-gcp)
    *   [tftest](https://pypi.org/project/tftest/)
*   **엔드 투 엔드 테스트:** 통합 테스트 방식을 전체 환경으로 확장하여 여러 모듈이 함께 작동하는지 확인할 수 있습니다. 이 방식에서는 아키텍처를 구성하는 모든 모듈을 새로운 테스트 환경에 배포합니다. 이상적으로 테스트 환경이 프로덕션 환경과 가능한 한 비슷해야 합니다. 비용이 많이 들지만 변경사항으로 인해 프로덕션 환경이 중단되지 않는다는 가장 높은 신뢰도를 얻을 수 있습니다.

소규모로 시작
-------

테스트가 서로 반복적으로 기초로 사용되는지 확인합니다. _빠른 실패_ 방식에 따라 먼저 소규모로 테스트를 실행하고 점차 더 복잡한 테스트를 만듭니다.

프로젝트 ID 및 리소스 이름 무작위 구성
-----------------------

이름 충돌을 방지하기 위해 해당 구성에서 프로젝트 ID가 전역적으로 고유하고 각 프로젝트 내에서 리소스 이름이 겹치지 않는지 확인합니다. 이렇게 하려면 리소스에 네임스페이스를 사용합니다. Terraform에는 이를 위한 [무작위 제공자](https://registry.terraform.io/providers/hashicorp/random/latest/docs)가 기본 제공됩니다.

테스트에 개별 환경 사용
-------------

테스트 중에는 많은 리소스가 생성 및 삭제됩니다. 리소스 삭제 중 삭제 사고가 발생하지 않도록 테스트 환경이 개발 또는 프로덕션 프로젝트와 격리되어 있는지 확인해야 합니다. 가장 좋은 방법은 테스트마다 프로젝트 또는 폴더를 새로 만드는 것입니다. 잘못된 구성을 방지하기 위해 각 테스트를 실행할 때마다 서비스 계정을 만드는 것이 좋습니다.

모든 리소스 삭제
---------

인프라 코드를 테스트하기 위해서는 실제 리소스를 배포해야 합니다. 비용이 청구되지 않도록 방지하려면 삭제 단계를 구현해야 합니다.

특정 구성에서 관리되는 모든 원격 객체를 삭제하려면 `terraform destroy` 명령어를 사용합니다. 일부 테스트 프레임워크에는 삭제 단계가 기본 제공됩니다. 예를 들어 Terratest를 사용하는 경우 테스트에 `defer terraform.Destroy(t, terraformOptions)`를 추가합니다. Kitchen-Terraform을 사용할 경우에는 `terraform kitchen delete WORKSPACE_NAME`을 사용해서 작업공간을 삭제합니다.

`terraform destroy` 명령어를 실행한 후 추가 삭제 절차도 실행해서 Terraform으로 삭제하지 못한 모든 리소스를 삭제합니다. 이렇게 하려면 테스트 실행에 사용된 모든 프로젝트를 삭제하거나 [`project_cleanup`](https://github.com/terraform-google-modules/terraform-google-scheduled-function/tree/master/modules/project_cleanup)와 같은 도구를 사용합니다.

테스트 런타임 최적화
-----------

테스트 실행 시간을 최적화하려면 다음 방법을 따릅니다.

*   **테스트를 병렬로 실행합니다.** 일부 테스트 프레임워크에서는 여러 Terraform 테스트의 동시 실행이 지원됩니다.
    *   예를 들어 Terratest의 경우 테스트 기능 정의 후 `t.Parallel()`을 추가할 수 있습니다.
*   **단계별로 테스트합니다.** 개별적으로 테스트할 수 있는 독립적인 구성으로 테스트를 구분합니다. 이렇게 하면 테스트를 실행할 때 모든 단계를 진행할 필요가 없고 반복적인 개발 주기를 빠르게 진행할 수 있습니다.
    *   예를 들어 Kitchen-Terraform에서는 테스트를 개별 모음으로 분할합니다. 반복 시에는 각 모음을 독립적으로 실행합니다.
    *   마찬가지로 Terratest를 사용할 때는 `stage(t, STAGE_NAME, CORRESPONDING_TEST_FUNCTION__)_`을 사용해서 테스트의 각 단계를 래핑합니다_. 실행할 테스트를 나타내는 환경 변수를 설정합니다. 예를 들면 `SKIP`_`STAGE_NAME="true"`입니다.
    *   [청사진 테스트 프레임워크](https://pkg.go.dev/github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test)에서는 단계별 실행이 지원됩니다.

다음 단계
-----

*   [Google Cloud기반 Terraform에 대한 일반적인 스타일 및 구조 권장사항](https://docs.cloud.google.com/docs/terraform/best-practices/general-style-structure?hl=ko) 알아보기
*   [Terraform 루트 모듈 사용 시 권장사항](https://docs.cloud.google.com/docs/terraform/best-practices/root-modules?hl=ko) 알아보기
