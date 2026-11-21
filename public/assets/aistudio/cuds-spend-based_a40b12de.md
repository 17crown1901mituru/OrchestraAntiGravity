# 지출 기반 약정 사용 할인 &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/cuds-spend-based

지출 기반 약정 사용 할인(CUD)은 제품이나 서비스에 대해 최소 금액을 지출하겠다는 약속하에 할인된 가격이 제공됩니다. 할인은 서비스의 [운영 가능 리소스](https://docs.cloud.google.com/docs/cuds?hl=ko#spend_based_commitments) 집합에 적용됩니다.

[지출 기반 약정이 적용되는 제품 알아보기](https://docs.cloud.google.com/docs/cuds?hl=ko#spend_based_commitments)

CUD를 사용할 수 있는 Google Cloud서비스마다 별도의 지출 기반 약정을 구매해야 합니다. CUD는 Cloud Billing 계정에서 결제하는 모든 프로젝트의 대상 사용량에 적용됩니다.

지출 기반 약정 구매
-----------

Google Cloud 콘솔에서 지출 기반 CUD를 구매할 수 있습니다. 적용 가능한 리전의 적격한 사용량에 할인이 적용됩니다.

약정을 구매하면 약정은 구매한 시간대에 따라 적용됩니다. 시간이 끝나기 10분 전에 구매한 약정은 다음 시간의 시작 시점부터 적용됩니다. 그렇지 않으면 다다음 시간의 시작 부분에서 시작됩니다. 이러한 시간은 현지 시간대를 기준으로 합니다. 예를 들면 다음과 같습니다.

시간이 끝나기 10분 전(49분 이전)에 약정을 구매하면 할인은 다음 시간의 시작 시점부터 적용됩니다. 시간이 끝나기 전 10분 이내(50분 이후)에 약정을 구매하면 할인은 1시간을 건너뛰고 다다음 시간의 시작 부분부터 적용됩니다.

이 예시를 사용하면 오후 4시 51분에 구매한 약정은 오후 6시에 시작되고 오후 4시 49분에 구매한 약정은 오후 5시에 시작됩니다.

### 권한 필요

Cloud Billing 계정의 지출 기반 CUD를 구매하거나 관리하려면 [결제 계정 관리자](https://docs.cloud.google.com/billing/docs/how-to/billing-access?hl=ko)여야 합니다.

### 구매 단계

지출 기반 약정 사용 할인을 구매하려면 다음 단계를 완료하세요.

1.  Google Cloud 콘솔에 로그인합니다.
    
    [Google Cloud 콘솔에 로그인](https://console.cloud.google.com/?hl=ko)
    
2.  Google Cloud 콘솔 **탐색 메뉴** 를 열고 **결제**를 선택합니다.
    
    Cloud Billing 계정이 두 개 이상 있으면 다음 중 하나를 수행하세요.
    
    *   현재 프로젝트의 Cloud Billing을 관리하려면 **연결된 결제 계정으로 이동**을 선택합니다.
    *   다른 Cloud Billing 계정을 찾으려면 **결제 계정 관리**를 선택한 후 관리하려는 계정을 선택합니다.
3.  약정 사용 할인 [대시보드](https://docs.cloud.google.com/billing/docs/how-to/cud-analysis-spend-based?hl=ko#understanding_dashboard) 또는 [분석 보고서](https://docs.cloud.google.com/billing/docs/how-to/cud-analysis-spend-based?hl=ko#understanding_analysis)에서 약정을 구매할 수 있습니다.
    
    1.  대시보드를 보려면 **Cloud Billing** 메뉴에서 **약정 사용 할인(CUD)**을 선택합니다.
        
    2.  약정을 구매하려면 **구매**를 선택합니다.
        
4.  **제품**을 선택합니다.
    
5.  약정의 **이름**을 입력합니다.
    
6.  약정 요금이 청구되는 **약정 기간**을 1년 또는 3년 중에서 선택합니다.
    
7.  시간당 **약정 금액**을 상응하는 할인 지출을 기준으로 입력합니다.
    
    *   이 금액은 약정 사용 할인으로 발생한 할인 비용과 같습니다.
    *   **약정 요약**에서 반복 청구인 **약정 수수료**를 확인할 수 있습니다.
    *   약정이 완전히 활용되면 **약정 요약**에서 예상 절감액을 확인할 수 있습니다.
        
8.  [약정 단위 관련 서비스별 약관](https://cloud.google.com/terms/service-terms?hl=ko)을 읽어보세요.
    
9.  입력한 모든 내용을 검토하여 올바른 금액을 사용하고 있는지 확인합니다. 제품에 특정 리전 내 약정이 필요한 경우 올바른 리전도 선택했는지 확인합니다.
    
10.  구매를 미리 보려면 **구매** 버튼을 클릭합니다. 이는 실제 구매를 처리하는 것이 아니라 완료하기 전에 검토할 수 있도록 요약을 준비하는 것입니다.
     
11.  구매를 확정하려면 약정 요약을 검토하고 **구매**를 다시 클릭합니다.
     

약정을 구매할 서비스마다 다음 단계를 반복합니다.

약정 보기
-----

[CUD 효과 분석](https://docs.cloud.google.com/billing/docs/how-to/analyze-cuds?hl=ko)에 설명된Google Cloud 콘솔 약정 대시보드나 분석 보고서에서 지출 기반 약정 사용 할인 세부정보를 볼 수 있습니다.

약정 대시보드와 분석 보고서에 대한 설명은 [CUD 분석 보고서 이해](https://docs.cloud.google.com/billing/docs/how-to/analyze-cuds?hl=ko#understanding_analysis) 및 [분석 보고서 이해](https://docs.cloud.google.com/billing/docs/how-to/cud-analysis-spend-based?hl=ko#understanding_analysis)를 참조하세요.

약정 취소
-----

구매한 약정을 취소할 수 없습니다. 약정 기간 동안 동의한 월납부액을 지불해야 합니다. 약정 및 약정이 적용되는 리소스는 해당 리소스의 향후 표준 가격 변경에 영향을 받지 않습니다.

실수로 약정을 구매했거나 약정을 잘못 구성한 경우 [Cloud Billing 지원팀](https://docs.cloud.google.com/support/billing?hl=ko)에 문의하여 도움을 요청하세요.

약정 사용 할인 작동 방식
--------------

지출 기반 약정 사용 할인은 1시간마다 적용 가능한 사용량에 적용됩니다.

약정한 시간보다 더 많이 사용하면 초과분 요금이 일반 주문형 요율로 청구됩니다. 약정한 시간보다 적게 사용하면 약정을 완전히 활용하지 못하고 전체 할인을 받을 수 없습니다.

약정 요금 및 크레딧이 Cloud Billing 계정과 프로젝트에 적용되는 방식을 이해하려면 [약정 사용 할인 요금 및 크레딧의 기여 분석](https://docs.cloud.google.com/docs/cuds-attribution?hl=ko)을 참조하세요.

청구서, 인보이스, 내보낸 데이터 이해하기
-----------------------

청구서, 인보이스, 내보낸 데이터의 지출 기반 CUD를 이해하려면 [CUD 효과 분석](https://docs.cloud.google.com/billing/docs/how-to/analyze-cuds?hl=ko)을 참조하세요.

기존 사용량 기반 CUD
-------------

기존 사용량 기반 CUD 프로그램에서는 약정 금액이 주문형 가격입니다. 기존 및 신규 사용량 기반 CUD 프로그램의 차이점에 대한 자세한 내용은 [사용량 기반 CUD 프로그램 개선 사항](https://docs.cloud.google.com/docs/cuds-multiprice?hl=ko)을 참조하세요.

API 및 참조
--------

Cloud Commerce Consumer Procurement API를 사용하여 VMware Engine 및 Compute Engine 가변형 약정에 대한 약정을 구매할 수 있습니다. 자세한 내용은 [문서](https://docs.cloud.google.com/marketplace/docs/commitment-api-purchasing?hl=ko)를 참조하세요.

지원 받기
-----

청구서의 CUD와 관련된 궁금한 점이 있으면 [Cloud Billing 지원팀](https://docs.cloud.google.com/support/billing?hl=ko)에 문의하여 도움을 요청하세요.

*   [약정 사용 할인 자세히 알아보기](https://docs.cloud.google.com/docs/cuds?hl=ko)
*   [CUD 효과 분석 방법 알아보기](https://docs.cloud.google.com/billing/docs/how-to/analyze-cuds?hl=ko)
*   [Cloud Billing 보고서 및 비용 추세 보기](https://docs.cloud.google.com/billing/docs/how-to/reports?hl=ko)
*   [보고서에 표시되는 크레딧 보기](https://docs.cloud.google.com/billing/docs/how-to/reports?hl=ko#credits)
*   [비용 분석 보고서로 절감되는 비용 알아보기](https://docs.cloud.google.com/billing/docs/how-to/cost-breakdown?hl=ko)
*   [Cloud Billing 데이터를 BigQuery로 내보내는 방법 알아보기](https://docs.cloud.google.com/billing/docs/how-to/export-data-bigquery?hl=ko)
*   [비용 및 결제 내역 보기](https://docs.cloud.google.com/billing/docs/how-to/view-history?hl=ko)
