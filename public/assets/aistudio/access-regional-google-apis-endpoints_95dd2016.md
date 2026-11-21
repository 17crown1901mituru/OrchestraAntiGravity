# 비공개 엔드포인트를 통해 리전 및 멀티 리전 Google API에 액세스 &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/access-regional-google-apis-endpoints

이 문서에서는 Private Service Connect 엔드포인트를 사용하여 [지원되는 Google API의 리전 엔드포인트 및 멀티 리전 엔드포인트](https://docs.cloud.google.com/vpc/docs/about-accessing-regional-google-apis-endpoints?hl=ko)에 연결하는 방법을 설명합니다.

다른 Private Service Connect 구성에 대한 자세한 내용은 [Private Service Connect](https://docs.cloud.google.com/vpc/docs/private-service-connect?hl=ko)를 참조하세요.

필요한 역할
------

엔드포인트를 연결할 서브넷과 동일한 프로젝트에 엔드포인트를 만드는 경우 [프로젝트에 필요한 역할](#roles-project)을 참고하세요.

[공유 VPC](https://docs.cloud.google.com/vpc/docs/shared-vpc?hl=ko)를 사용하고 서비스 프로젝트에서 엔드포인트를 만들어 호스트 프로젝트의 서브넷에 연결하는 경우 다음 역할이 필요합니다.

*   서비스 프로젝트에 부여된 [프로젝트에 필요한 역할](#roles-project)
*   호스트 프로젝트에 부여된 [공유 VPC 구성을 위한 추가 필수 역할](#roles-shared-vpc)

### 프로젝트에 필요한 역할

리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하는 데 필요한 권한을 얻으려면 관리자에게 다음 IAM 역할을 부여해 달라고 요청하세요.

*   엔드포인트 만들기, 보기, 삭제: 프로젝트의 [Compute 네트워크 관리자](https://docs.cloud.google.com/iam/docs/roles-permissions/compute?hl=ko#compute.networkAdmin) (`roles/compute.networkAdmin`)
*   엔드포인트의 DNS 항목 구성: 프로젝트의 [DNS 관리자](https://docs.cloud.google.com/iam/docs/roles-permissions/dns?hl=ko#dns.admin) (`roles/dns.admin`)

역할 부여에 대한 자세한 내용은 [프로젝트, 폴더, 조직에 대한 액세스 관리](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ko)를 참조하세요.

[커스텀 역할](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=ko)이나 다른 [사전 정의된 역할](https://docs.cloud.google.com/iam/docs/roles-overview?hl=ko#predefined)을 통해 필요한 권한을 얻을 수도 있습니다.

### 공유 VPC 구성에 필요한 추가 역할

호스트 프로젝트 서브넷의 IP 주소를 사용하는 서비스 프로젝트에서 엔드포인트를 만드는 경우 [프로젝트 역할](#roles-project) (서비스 프로젝트) 외에 다음 권한이 필요합니다.

[사용자](#shared-vpc-roles-you)와 [네트워크 연결 서비스 계정](#shared-vpc-roles-service-account) 모두 호스트 프로젝트에 대한 권한이 필요합니다.

#### 사용자 계정에 부여할 역할

Google API의 리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하는 데 필요한 권한을 얻으려면 관리자에게 다음 IAM 역할을 부여해 달라고 요청하세요.

*   호스트 프로젝트 IP 주소를 사용하는 엔드포인트를 만들고, 보고, 삭제합니다.
    *   서비스 프로젝트에 대한 [Compute 네트워크 관리자](https://docs.cloud.google.com/iam/docs/roles-permissions/compute?hl=ko#compute.networkAdmin) (`roles/compute.networkAdmin`)
    *   호스트 프로젝트의 [Compute 네트워크 사용자](https://docs.cloud.google.com/iam/docs/roles-permissions/compute?hl=ko#compute.networkUser) (`roles/compute.networkUser`)

역할 부여에 대한 자세한 내용은 [프로젝트, 폴더, 조직에 대한 액세스 관리](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ko)를 참조하세요.

이러한 사전 정의된 역할에는 Google API의 리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하는 데 필요한 권한이 포함되어 있습니다. 필요한 정확한 권한을 보려면 **필수 권한** 섹션을 펼치세요.

#### 필수 권한

Google API의 리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하려면 다음 권한이 필요합니다.

*   호스트 프로젝트 IP 주소를 사용하는 엔드포인트를 만들고, 보고, 삭제하려면 다음 권한이 필요합니다.
    *   호스트 프로젝트에 대한 `compute.networks.use` 권한
    *   호스트 프로젝트에 대한 `compute.subnetworks.use` 권한

[커스텀 역할](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=ko)이나 다른 [사전 정의된 역할](https://docs.cloud.google.com/iam/docs/roles-overview?hl=ko#predefined)을 사용하여 이 권한을 부여받을 수도 있습니다.

#### 네트워크 연결 서비스 계정에 부여할 역할

`service-PROJECT_NUMBER@gcp-sa-networkconnectivity.iam.gserviceaccount.com`에 Google API의 리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하는 데 필요한 권한이 있는지 확인하려면 관리자에게 `service-PROJECT_NUMBER@gcp-sa-networkconnectivity.iam.gserviceaccount.com`에 다음 IAM 역할을 부여해 달라고 요청하세요.

*   호스트 프로젝트 IP 주소를 사용하는 엔드포인트 만들기, 보기, 삭제: 호스트 프로젝트의 [Compute 네트워크 사용자](https://docs.cloud.google.com/iam/docs/roles-permissions/compute?hl=ko#compute.networkUser) (`roles/compute.networkUser`)

역할 부여에 대한 자세한 내용은 [프로젝트, 폴더, 조직에 대한 액세스 관리](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ko)를 참조하세요.

이러한 사전 정의된 역할에는 Google API의 리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하는 데 필요한 권한이 포함되어 있습니다. 필요한 정확한 권한을 보려면 **필수 권한** 섹션을 펼치세요.

#### 필수 권한

Google API의 리전 엔드포인트에 액세스하는 Private Service Connect 엔드포인트를 관리하려면 다음 권한이 필요합니다.

*   호스트 프로젝트 IP 주소를 사용하는 엔드포인트를 만들고, 보고, 삭제하려면 다음 권한이 필요합니다.
    *   호스트 프로젝트에 대한 `compute.networks.get` 권한
    *   호스트 프로젝트에 대한 `compute.networks.use` 권한
    *   호스트 프로젝트에 대한 `compute.subnetworks.get` 권한
    *   호스트 프로젝트에 대한 `compute.subnetworks.use` 권한

관리자는 [커스텀 역할](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=ko)이나 다른 [사전 정의된 역할](https://docs.cloud.google.com/iam/docs/roles-overview?hl=ko#predefined)을 사용하여 `service-PROJECT_NUMBER@gcp-sa-networkconnectivity.iam.gserviceaccount.com`에게 이러한 권한을 부여할 수도 있습니다.

시작하기 전에
-------

1.  Google Cloud CLI를 [설치합니다](https://docs.cloud.google.com/sdk/docs/install?hl=ko). 설치 후 다음 명령어를 실행하여 Google Cloud CLI를 [초기화](https://docs.cloud.google.com/sdk/docs/initializing?hl=ko)합니다.
    
    $ gcloud init
    
    외부 ID 공급업체(IdP)를 사용하는 경우 먼저 [제휴 ID로 gcloud CLI에 로그인](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud?hl=ko)해야 합니다.
    
2.  아직 사용 설정되지 않은 Compute Engine, Network Connectivity Center, Cloud DNS API가 있으면 사용 설정합니다.
    
    **API 사용 설정에 필요한 역할**
    
    API를 사용 설정하려면 `serviceusage.services.enable` 권한이 필요합니다. 프로젝트를 만든 경우 소유자 역할 (`roles/owner`)을 통해 이 권한이 이미 있을 수 있습니다. 그렇지 않은 경우 서비스 사용량 관리자 역할 (`roles/serviceusage.serviceUsageAdmin`)을 통해 이 권한을 얻을 수 있습니다. [역할을 부여하는 방법 알아보기](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=ko)
    
    [API 사용 설정](https://console.cloud.google.com/apis/enableflow?apiid=compute.googleapis.com%2Cnetworkconnectivity.googleapis.com%2Cdns.googleapis.com&hl=ko)
    
3.  또한 Private Service Connect 엔드포인트를 통해 액세스하려는 대상 서비스의 [API를 사용 설정](https://docs.cloud.google.com/apis/docs/getting-started?hl=ko#enabling_apis)합니다. 예를 들어 `spanner.me-central2.rep.googleapis.com`에 액세스하려면 Cloud Spanner API를 사용 설정합니다. Private Service Connect는 API를 자동으로 사용 설정하지 않습니다.
    
4.  이그레스 방화벽 규칙이 엔드포인트로 트래픽을 허용하는지 확인합니다. VPC 네트워크의 기본 방화벽 구성은 묵시적 이그레스 허용 규칙이 포함되어 있으므로 이 트래픽을 허용합니다. 트래픽을 차단하는 우선순위가 더 높은 이그레스 규칙을 만들지 않았는지 확인합니다.
    

리전 Private Service Connect 엔드포인트 만들기
------------------------------------

리전 Private Service Connect 엔드포인트를 만들어 요청을 대상 Google API의 리전 엔드포인트 또는 멀티 리전 엔드포인트로 전송할 수 있습니다.

지원되는 리전 엔드포인트 및 멀티 리전 엔드포인트 목록은 [리전 서비스 엔드포인트](https://docs.cloud.google.com/vpc/docs/regional-service-endpoints?hl=ko)를 참고하세요.

### 콘솔

1.  Google Cloud 콘솔에서 **Private Service Connect** 페이지로 이동합니다.
    
    [Private Service Connect로 이동](https://console.cloud.google.com/net-services/psc?hl=ko)
    
2.  **엔드포인트 연결**을 클릭합니다.
    
3.  **대상**에서 **Google API**를 선택합니다.
    
4.  **범위** 목록에서 **리전**을 선택합니다.
    
5.  **리전** 목록에서 사용하려는 리전을 선택합니다.
    
6.  **대상 서비스** 목록에서 액세스하려는 서비스를 선택합니다.
    
7.  **엔드포인트 이름**에 엔드포인트 이름을 입력합니다.
    
8.  **네트워크**에서 네트워크를 선택합니다.
    
9.  **서브네트워크**에서 서브네트워크를 선택합니다.
    
10.  **IP 주소**에서 다음 중 하나를 수행하여 IP 주소를 구성합니다.
     
     *   **자동 할당**을 선택하여 새 고정 IP 주소를 자동으로 할당하고 예약합니다.
     *   목록에서 고정 IP 주소를 선택합니다.
     *   **IP 주소 만들기**를 클릭하여 새 고정 IP 주소를 예약합니다.
11.  (선택사항) VPC 네트워크의 모든 리전에서 엔드포인트를 사용할 수 있게 하려면 **전역 액세스 사용 설정**을 선택합니다.
     
12.  **엔드포인트 추가**를 클릭합니다.
     

### gcloud

[`gcloud network-connectivity regional-endpoints create` 명령어](https://docs.cloud.google.com/sdk/gcloud/reference/network-connectivity/regional-endpoints/create?hl=ko)를 사용하여 엔드포인트를 만듭니다.

gcloud network-connectivity regional-endpoints create ENDPOINT\_NAME \\
    --region=REGION \\
    --address=ADDRESS \\
    --network=projects/PROJECT\_ID/global/networks/NETWORK\_NAME \\
    --subnetwork=projects/PROJECT\_ID/regions/REGION/subnetworks/SUBNET\_NAME \\
    --target-google-api=REP\_DNS\_NAME

다른 리전의 리소스에서 엔드포인트를 사용할 수 있게 하려면 `--enable-global-access` 플래그를 추가합니다.

다음을 바꿉니다.

*   `ENDPOINT_NAME`: 엔드포인트의 이름
    
*   `REGION`: 엔드포인트를 만들려는 리전
    
*   `ADDRESS`: 엔드포인트에 사용할 IPv4 또는 `/96` IPv6 주소입니다. 생략하면 서브네트워크의 IPv4 주소가 할당됩니다. 다음 형식 중 하나를 사용하세요.
    
    *   IPv4 주소(예: `10.0.0.2`)
    *   IPv4 또는 IPv6 주소 리소스 URI(예: `projects/PROJECT_ID/regions/REGION/addresses/ADDRESS_NAME`)
*   `PROJECT_ID`: 엔드포인트를 만들려는 프로젝트
    
*   `NETWORK_NAME`: 엔드포인트의 VPC 네트워크 이름
    
*   `SUBNET_NAME`: 엔드포인트를 연결할 서브넷
    
*   `REP_DNS_NAME`: 연결하려는 리전 서비스 엔드포인트 또는 멀티 리전 엔드포인트의 호스트 이름. 예를 들어 `spanner.me-central2.rep.googleapis.com` (리전) 또는 `storage.us.rep.googleapis.com` (멀티 리전)입니다.
    

### API

[`projects.locations.regionalEndpoints.create` 메서드](https://docs.cloud.google.com/network-connectivity/docs/reference/networkconnectivity/rest/v1/projects.locations.regionalEndpoints/create?hl=ko)를 사용하여 엔드포인트를 만듭니다.

HTTP 메서드 및 URL:

  POST https://networkconnectivity.googleapis.com/v1/projects/PROJECT\_ID/locations/REGION/regionalEndpoints?regionalEndpointId=ENDPOINT\_NAME{
    "accessType": "REGIONAL",
    "address": "ADDRESS",
    "network": "projects/PROJECT\_ID/global/networks/NETWORK\_NAME",
    "subnetwork": "projects/PROJECT\_ID/regions/REGION/subnetworks/SUBNET\_NAME",
    "targetGoogleApi": "REP\_DNS\_NAME"
  }
  

다음을 바꿉니다.

*   `PROJECT_ID`: 엔드포인트의 프로젝트 ID
*   `REGION`: 엔드포인트를 만들려는 리전
*   `ENDPOINT_NAME`: 엔드포인트의 이름
*   `ADDRESS`: 엔드포인트에 사용할 IPv4 또는 `/96` IPv6 주소입니다. 생략하면 서브네트워크의 IPv4 주소가 할당됩니다. 다음 형식 중 하나를 사용하세요.
    *   IPv4 주소(예: `10.0.0.2`)
    *   IPv4 또는 IPv6 주소 리소스 URI(예: `projects/PROJECT_ID/regions/REGION/addresses/ADDRESS_NAME`)
*   `NETWORK_NAME`: 엔드포인트의 VPC 네트워크 이름
*   `SUBNET_NAME`: 엔드포인트를 연결할 서브넷의 이름입니다.
*   `REP_DNS_NAME`: 연결할 리전 서비스 엔드포인트 또는 멀티 리전 엔드포인트의 호스트 이름입니다. 예를 들어 `spanner.me-central2.rep.googleapis.com` (리전) 또는 `storage.us.rep.googleapis.com` (멀티 리전)입니다.

공유 VPC를 사용하는 경우 호스트 프로젝트의 서브넷에서 IP 주소를 사용하는 서비스 프로젝트에서 엔드포인트를 만들 수 있습니다. Google Cloud 콘솔을 사용하여 이 구성을 만들 수는 없습니다. gcloud CLI 또는 API를 사용해야 합니다.

### gcloud

1.  [`gcloud compute addresses create` 명령어](https://docs.cloud.google.com/sdk/gcloud/reference/compute/addresses/create?hl=ko)를 사용하여 리전 엔드포인트에 사용할 고정 IP 주소를 예약합니다. 고정 IP 주소 사용은 IPv4의 경우 선택사항이고 IPv6의 경우 필수입니다.
    
    서비스 프로젝트에서 주소 리소스를 만들고 호스트 프로젝트의 서브넷을 지정합니다.
    
    gcloud compute addresses create ADDRESS\_NAME \\
        --region=REGION \\
        --subnet=projects/HOST\_PROJECT/regions/REGION/subnetworks/HOST\_SUBNET \\
        --ip-version=IP\_VERSION \\
        --project=SERVICE\_PROJECT
    
    다음을 바꿉니다.
    
    *   `ADDRESS_NAME`: 예약된 IP 주소에 할당할 이름입니다.
    *   `REGION`: 엔드포인트 IP 주소의 리전. 이 리전은 서비스 프로듀서의 서비스 연결을 포함하는 동일한 리전이어야 합니다.
    *   `HOST_PROJECT`: 호스트 프로젝트의 프로젝트 ID입니다.
    *   `HOST_SUBNET`: 엔드포인트 IP 주소의 서브넷 이름
    *   `IP_VERSION`: IP 주소의 IP 버전으로, `IPV4` 또는 `IPV6`일 수 있습니다. 기본값은 `IPV4`입니다. `IPV6`를 지정하려면 IP 주소가 [내부 IPv6 주소 범위가 있는 서브넷](https://docs.cloud.google.com/vpc/docs/subnets?hl=ko#subnet-types)에 연결되어 있어야 합니다.
    *   `SERVICE_PROJECT`: 서비스 프로젝트의 프로젝트 ID
2.  [`gcloud network-connectivity regional-endpoints create` 명령어](https://docs.cloud.google.com/sdk/gcloud/reference/network-connectivity/regional-endpoints/create?hl=ko)를 사용하여 서비스 프로젝트에 리전 엔드포인트를 만듭니다.
    
    gcloud network-connectivity regional-endpoints create ENDPOINT\_NAME \\
        --region=REGION \\
        --address=ADDRESS \\
        --network=projects/HOST\_PROJECT/global/networks/HOST\_NETWORK \\
        --subnetwork=projects/HOST\_PROJECT/regions/REGION/subnetworks/HOST\_SUBNET \\
        --target-google-api=REP\_DNS\_NAME \\
        --project=SERVICE\_PROJECT
        
    
    다른 리전의 리소스에서 엔드포인트를 사용할 수 있게 하려면 `--enable-global-access` 플래그를 추가합니다.
    
    다음을 바꿉니다.
    
    *   `ENDPOINT_NAME`: 엔드포인트의 이름
        
    *   `REGION`: 엔드포인트를 만들려는 리전
        
    *   `ADDRESS`: 엔드포인트에 사용할 IPv4 또는 `/96` IPv6 주소입니다. 생략하면 서브네트워크의 IPv4 주소가 할당됩니다. 다음 형식 중 하나를 사용하세요.
        
        *   IPv4 주소(예: `10.0.0.2`)
        *   IPv4 또는 IPv6 주소 리소스 URI입니다. 주소 리소스는 호스트 프로젝트가 아닌 서비스 프로젝트에서 만들어야 합니다. 예를 들면 `projects/SERVICE_PROJECT/regions/REGION/addresses/ADDRESS_NAME`입니다.
        *   `HOST_PROJECT`: 호스트 프로젝트의 ID입니다.
    *   `HOST_NETWORK`: 호스트 프로젝트의 VPC 네트워크 이름입니다.
        
    *   `HOST_SUBNET`: 엔드포인트를 연결할 호스트 프로젝트의 서브넷
        
    *   `REP_DNS_NAME`: 연결할 리전 서비스 엔드포인트 또는 멀티 리전 엔드포인트의 호스트 이름입니다. 예를 들어 `spanner.me-central2.rep.googleapis.com` (리전) 또는 `storage.us.rep.googleapis.com` (멀티 리전)입니다.
        
    *   `SERVICE_PROJECT`: 서비스 프로젝트의 프로젝트 ID
        

### API

1.  리전 엔드포인트에 사용할 고정 IP 주소를 예약합니다. 고정 IP 주소 사용은 IPv4의 경우 선택사항이고 IPv6의 경우 필수입니다.
    
    서비스 프로젝트에서 주소 리소스를 만들고 호스트 프로젝트의 서브넷을 지정합니다.
    
    POST https://compute.googleapis.com/compute/v1/projects/SERVICE\_PROJECT/regions/REGION/addresses
    {
        "name": "ADDRESS\_NAME",
        "addressType": "INTERNAL",
        "subnetwork": "projects/HOST\_PROJECT/regions/REGION/subnetworks/HOST\_SUBNET",
        "ipVersion": "IP\_VERSION"
    }
    
    다음을 바꿉니다.
    
    *   `SERVICE_PROJECT`: 서비스 프로젝트의 ID
        
    *   `REGION`: 엔드포인트의 리전. 연결하려는 리전 엔드포인트와 동일한 리전이어야 합니다.
        
    *   `ADDRESS_NAME`: 고정 IP 주소에 할당할 이름
        
    *   `HOST_PROJECT`: 호스트 프로젝트의 ID입니다.
        
    *   `HOST_SUBNET`: IP 주소를 예약할 공유 VPC 네트워크의 서브넷입니다.
        
    *   `IP_VERSION`: IP 주소의 IP 버전으로, `IPV4` 또는 `IPV6`일 수 있습니다. 기본값은 `IPV4`입니다. `IPV6`를 지정하려면 IP 주소가 [내부 IPv6 주소 범위가 있는 서브넷](https://docs.cloud.google.com/vpc/docs/subnets?hl=ko#subnet-types)에 연결되어 있어야 합니다.
        
2.  [`projects.locations.regionalEndpoints.create` 메서드](https://docs.cloud.google.com/network-connectivity/docs/reference/networkconnectivity/rest/v1/projects.locations.regionalEndpoints/create?hl=ko)를 사용하여 Private Service Connect 엔드포인트를 만듭니다.
    
    HTTP 메서드 및 URL:
    
    POST https://networkconnectivity.googleapis.com/v1/projects/SERVICE\_PROJECT/locations/REGION/regionalEndpoints?regionalEndpointId=ENDPOINT\_NAME
    {
      "accessType": "REGIONAL",
      "address": "ADDRESS",
      "network": "projects/HOST\_PROJECT/global/networks/HOST\_NETWORK",
      "subnetwork": "projects/HOST\_PROJECT/regions/REGION/subnetworks/HOST\_SUBNET",
      "targetGoogleApi": "REP\_DNS\_NAME"
    }
    
    다음을 바꿉니다.
    
    *   `SERVICE_PROJECT`: 서비스 프로젝트의 ID
    *   `REGION`: 엔드포인트를 만들려는 리전
    *   `ENDPOINT_NAME`: 엔드포인트의 이름
    *   `ADDRESS`: 엔드포인트에 사용할 IPv4 또는 `/96` IPv6 주소입니다. 생략되면 서브네트워크의 IPv4 주소가 할당됩니다. 다음 형식 중 하나를 사용하세요.
        *   IPv4 주소(예: `10.0.0.2`)
        *   IPv4 또는 IPv6 주소 리소스 URI(예: `projects/SERVICE_PROJECT/regions/REGION/addresses/ADDRESS_NAME`)
    *   `HOST_PROJECT`: 호스트 프로젝트의 ID입니다.
    *   `HOST_NETWORK`: 호스트 프로젝트의 네트워크 이름입니다.
    *   `HOST_SUBNET`: 엔드포인트를 연결할 호스트 프로젝트의 서브넷 이름
    *   `REP_DNS_NAME`: 연결할 리전 서비스 엔드포인트 또는 멀티 리전 엔드포인트의 호스트 이름입니다. 예를 들어 `spanner.me-central2.rep.googleapis.com` (리전) 또는 `storage.us.rep.googleapis.com` (멀티 리전)입니다.

엔드포인트 나열
--------

구성된 모든 엔드포인트를 나열할 수 있습니다.

엔드포인트가 작동하는지 확인
---------------

엔드포인트가 구성된 VPC 네트워크 및 리전에서 가상 머신(VM) 인스턴스를 만듭니다. VM에서 다음 명령어를 실행하여 Private Service Connect 엔드포인트가 작동하는지 확인합니다. 엔드포인트는 핑(ICMP) 요청에 응답하지 않습니다.

*   IPv4 엔드포인트를 확인하려면 다음 단계를 따르세요.
    
    curl --connect-to REP\_DNS\_NAME:443:ADDRESS:443 \\
    'https://REP\_DNS\_NAME/PATH'
    
*   IPv6 엔드포인트를 확인하려면 다음 단계를 따르세요.
    
    curl -6 --connect-to REP\_DNS\_NAME:443:\[ADDRESS\]:443 \\
    'https://REP\_DNS\_NAME/PATH'
    

다음을 바꿉니다.

*   `REP_DNS_NAME`: 대상 지역 엔드포인트 또는 멀티 리전 엔드포인트의 공개 DNS 이름(예: `spanner.me-central2.rep.googleapis.com` 또는 `storage.us.rep.googleapis.com`)
*   `ADDRESS`: 엔드포인트의 IP 주소
*   `PATH`: 이 서비스에서 제공하는 리소스의 경로. 예를 들어 많은 서비스에서 `$discovery/rest?version=v1` 경로가 있는 탐색 문서를 제공합니다.

다음 요청 예시에서는 IP 주소가 `192.168.1.100`인 엔드포인트가 `me-central2`의 리전 엔드포인트에서 [Cloud Spanner API](https://docs.cloud.google.com/spanner/docs/reference/rest?hl=ko) 탐색 문서를 요청할 수 있는지 테스트합니다.

curl --connect-to spanner.me-central2.rep.googleapis.com:443:192.168.1.100:443 \\
'https://spanner.me-central2.rep.googleapis.com/$discovery/rest?version=v1'

엔드포인트의 비공개 DNS 항목 만들기
---------------------

클라이언트가 Private Service Connect 엔드포인트로 요청을 전달할 수 있도록 비공개 DNS 항목을 만들어야 합니다.

타겟 리전 또는 멀티 리전 엔드포인트와 동일한 호스트 이름을 사용하는 비공개 영역을 만들고 해당 영역에 루트 레코드를 만드는 것이 좋습니다.

*   Private Service Connect 엔드포인트의 타겟이 `spanner.me-central2.rep.googleapis.com`인 경우 `spanner.me-central2.rep.googleapis.com`의 정점 레코드가 포함된 `spanner.me-central2.rep.googleapis.com`의 비공개 영역을 만듭니다.
*   Private Service Connect 엔드포인트의 타겟이 `storage.us.rep.googleapis.com`인 경우 `storage.us.rep.googleapis.com`의 정점 레코드가 포함된 `storage.us.rep.googleapis.com`의 비공개 영역을 만듭니다.

도메인 루트에 레코드를 만들면 `logging.me-central2.rep.googleapis.com`와 같은 다른 엔드포인트의 공개 호스트 이름에 액세스할 수 있습니다.

다음 섹션에서는 Cloud DNS를 사용하여 비공개 영역과 apex DNS 레코드를 만드는 방법을 설명합니다.

### 비공개 영역 만들기

### 콘솔

1.  Google Cloud 콘솔에서 **DNS 영역 만들기** 페이지로 이동합니다.
    
    [DNS 영역 만들기로 이동](https://console.cloud.google.com/net-services/dns/zones/new/create?hl=ko)
    
2.  **영역 유형**으로 **비공개**를 선택합니다.
    
3.  **영역 이름**에 이름을 입력합니다. 예를 들어 Private Service Connect 엔드포인트가 `spanner.me-central2.rep.googleapis.com`을 가리키는 경우 `spanner-me-central2-rep-googleapis-com`을 입력합니다.
    
4.  **DNS 이름**에 대상 리전 엔드포인트의 호스트 이름을 지정하고 그 뒤에 점을 붙입니다. 예를 들면 `spanner.me-central2.rep.googleapis.com.`입니다.
    
5.  (선택사항) 설명을 추가합니다.
    
6.  **옵션**에서 **기본값(비공개)**을 선택합니다.
    
7.  비공개 영역이 표시될 VPC 네트워크를 선택합니다. 선택한 VPC 네트워크만 영역의 레코드를 쿼리할 수 있습니다.
    
8.  **만들기**를 클릭합니다.
    

### gcloud

[`dns managed-zones create`](https://docs.cloud.google.com/sdk/gcloud/reference/dns/managed-zones/create?hl=ko) 명령어를 실행합니다.

gcloud dns managed-zones create ZONE\_NAME \\
    --dns-name=REP\_DNS\_NAME. \\
    --networks=VPC\_NETWORK\_LIST \\
    --visibility=private \\
    --description="Private zone for REP\_DNS\_NAME"

다음을 바꿉니다.

*   `ZONE_NAME`: 영역의 이름(예: `spanner-me-central2-rep-googleapis.com`)
*   `DESCRIPTION`: 영역에 대한 설명
*   `REP_DNS_NAME`: 영역의 DNS 서픽스. 대상 지역 엔드포인트 호스트 이름을 사용합니다(예: `spanner.me-central2.rep.googleapis.com`).
*   `VPC_NETWORK_LIST`: 영역을 쿼리할 수 있는 VPC 네트워크의 쉼표로 구분된 목록.

### API

[`managedZones.create`](https://docs.cloud.google.com/dns/docs/reference/v1/managedZones/create?hl=ko) 메서드를 사용하여 `POST` 요청을 전송합니다.

HTTP 메서드 및 URL:

POST https://dns.googleapis.com/dns/v1/projects/PROJECT\_ID/managedZones

JSON 요청 본문:

{
  "name": "ZONE\_NAME",
  "dnsName": "REP\_DNS\_NAME.",
  "description": "DESCRIPTION",
  "visibility": "private",
  "privateVisibilityConfig": {
    "kind": "dns#managedZonePrivateVisibilityConfig",
    "networks": \[
      {
        "kind": "dns#managedZonePrivateVisibilityConfigNetwork",
        "networkUrl": "NETWORK\_1\_URL"
      },
      {
        "kind": "dns#managedZonePrivateVisibilityConfigNetwork",
        "networkUrl": "NETWORK\_2\_URL"
      }
    \]
  }
}

다음을 바꿉니다.

*   `PROJECT_ID`: DNS 영역의 프로젝트 ID.
*   `ZONE_NAME`: 이 지역 엔드포인트용으로 만든 비공개 영역의 이름
*   `REP_DNS_NAME`: 만들려는 레코드의 FQDN. 대상 지역 엔드포인트 호스트 이름을 사용합니다(예: `spanner.me-central2.rep.googleapis.com`).
*   `DESCRIPTION`: DNS 영역에 대한 설명
*   `NETWORK_1_URL` 및 `NETWORK_2_URL`: 영역을 쿼리할 권한이 있는 VPC 네트워크의 전체 URL(예: `https://www.googleapis.com/compute/v1/projects/PROJECT_ID/global/networks/NETWORK_NAME`)

### 영역에 DNS 레코드 추가

### 콘솔

1.  Google Cloud 콘솔에서 **Cloud DNS 영역** 페이지로 이동합니다.
    
    [Cloud DNS 영역으로 이동](https://console.cloud.google.com/net-services/dns/?hl=ko)
    
2.  레코드를 추가할 관리형 영역의 이름을 클릭합니다.
    
3.  **영역 세부정보** 페이지에서 **표준 추가**를 클릭합니다.
    
4.  **레코드 모음 만들기** 페이지에서 **DNS 이름** 필드를 비워 둡니다. 필드를 비워 두면 도메인 루트에 리소스 레코드가 생성됩니다.
    
5.  **리소스 레코드 유형**에서 `A`를 선택합니다.
    
6.  **IPv4 주소** 필드에 Private Service Connect 엔드포인트의 IP 주소를 입력하거나 선택합니다.
    
7.  **만들기**를 클릭합니다.
    

### gcloud

리소스 레코드 세트를 만들려면 [`gcloud dns record-sets create`](https://docs.cloud.google.com/sdk/gcloud/reference/dns/record-sets/create?hl=ko) 명령어를 사용합니다.

gcloud dns record-sets create REP\_DNS\_NAME \\
    --rrdatas=ADDRESS \\
    --type=RECORD\_TYPE \\
    --ttl=TTL \\
    --zone=ZONE\_NAME

다음을 바꿉니다.

*   `REP_DNS_NAME`: 만들려는 레코드의 FQDN. 대상 지역 엔드포인트 호스트 이름을 사용합니다(예: `spanner.me-central2.rep.googleapis.com`).
*   `ADDRESS`: Private Service Connect 엔드포인트의 IP 주소
*   `RECORD_TYPE`: DNS 레코드 유형(예: IPv4 레코드의 경우 `A`, IPv6 레코드의 경우 `AAAA`)
*   `TTL`: 리졸버가 이 리소스 레코드 세트를 캐시하는 TTL(초)(예: `300`)
*   `ZONE_NAME`: 이 지역 엔드포인트용으로 만든 비공개 영역의 이름

### API

리소스 레코드 세트를 추가하려면 [`resourceRecordSets.create` 메서드](https://docs.cloud.google.com/dns/docs/reference/v1/resourceRecordSets/create?hl=ko)를 사용합니다.

HTTP 메서드 및 URL:

POST https://www.googleapis.com/dns/v1/projects/PROJECT\_ID/managedZones/ZONE\_NAME/rrsets

JSON 요청 본문:

{
  "name": "REP\_DNS\_NAME.",
  "type": "RECORD\_TYPE",
  "ttl": TTL,
  "rrdatas": \[ "ADDRESS" \]
}

다음을 바꿉니다.

*   `PROJECT_ID`: DNS 영역의 프로젝트 ID.
*   `ZONE_NAME`: 이 지역 엔드포인트용으로 만든 비공개 영역의 이름
*   `REP_DNS_NAME`: 만들려는 레코드의 FQDN. 대상 지역 엔드포인트 호스트 이름을 사용합니다(예: `spanner.me-central2.rep.googleapis.com`).
*   `RECORD_TYPE`: DNS 레코드 유형(예: IPv4 레코드의 경우 `A`, IPv6 레코드의 경우 `AAAA`)
*   `TTL`: 리졸버가 이 리소스 레코드 세트를 캐시하는 TTL(초)(예: `300`)
*   `ADDRESS`: Private Service Connect 엔드포인트의 IP 주소

비공개 엔드포인트 이름을 사용하도록 클라이언트 구성
----------------------------

공개 DNS 이름 대신 비공개 DNS 이름을 사용하도록 클라이언트를 구성해야 합니다. 커스텀 엔드포인트를 사용하도록 구성하는 방법은 클라이언트 또는 클라이언트 라이브러리 문서를 참조하세요. 다음 페이지에는 몇 가지 일반적인 클라이언트의 구성 단계가 나와 있습니다.

*   **Python**: [클라이언트 옵션](https://googleapis.dev/python/google-api-core/latest/client_options.html)에서 `api_endpoint`를 구성할 수 있습니다.
    
*   **Go**: [ClientOptions](https://godoc.org/google.golang.org/api/option)에서 `WithEndpoint`를 구성할 수 있습니다.
    
*   **.NET**: 클라이언트의 빌더 클래스에서 [`Endpoint`](https://docs.cloud.google.com/dotnet/docs/reference/help/client-configuration?hl=ko#endpoint)를 구성할 수 있습니다.
    
*   **Java**: 클라이언트의 설정 클래스에서 [`setEndpoint`](https://docs.cloud.google.com/java/docs/endpoints?hl=ko)를 구성할 수 있습니다.
    
*   **gcloud**: [gcloud CLI](https://docs.cloud.google.com/sdk/gcloud/reference/topic/endpoint-override?hl=ko)에서 `api_endpoint_overrides`를 구성할 수 있습니다.
    

엔드포인트에 대한 자세한 정보 확인하기
---------------------

엔드포인트의 구성 세부정보를 볼 수 있습니다.

전역 액세스 구성 업데이트
--------------

리전 엔드포인트 대상이 있는 Private Service Connect 엔드포인트는 업데이트할 수 없습니다. 엔드포인트의 전역 액세스 설정을 변경해야 하는 경우 [엔드포인트를 삭제](#delete-endpoint)하고 필요한 전역 액세스 설정이 있는 [새 엔드포인트를 만듭니다](#create-regional-endpoint).

엔드포인트 삭제
--------

엔드포인트를 삭제할 수 있습니다.

하이브리드 네트워크에서 엔드포인트에 액세스
-----------------------

Cloud Interconnect 또는 Cloud VPN 터널을 위한 VLAN 연결로 Google Cloud 에 연결된 네트워크의 클라이언트는 Private Service Connect 엔드포인트에 도달할 수 있습니다.

*   VLAN 연결이나 Cloud VPN 터널은 엔드포인트와 같은 VPC 네트워크(또는 공유 VPC 네트워크)에서 종료되어야 합니다. 피어링된 VPC 네트워크의 클라이언트는 엔드포인트에 연결될 수 없습니다.
    
*   [전역 액세스](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=ko#create-regional-endpoint)가 구성된 경우 VLAN 연결의 클라이언트 트래픽 또는 Cloud VPN 터널이 다른 리전의 엔드포인트에 도달할 수 있습니다.
    
*   VLAN 연결에는 Dataplane v1 및 Dataplane v2가 모두 지원됩니다. Dataplane 버전에 대한 자세한 내용은 [Dataplane v2](https://docs.cloud.google.com/network-connectivity/docs/interconnect/concepts/terminology?hl=ko#dataplaneVersion)를 참조하세요.
    

비공개 DNS 영역에 쿼리를 수행할 수 있도록 다른 네트워크에 시스템을 구성해야 합니다.

Cloud DNS를 사용하여 비공개 DNS 영역을 구현한 경우 다음 단계를 완료합니다.

*   다른 네트워크가 연결되는 VPC 네트워크에 [인바운드 서버 정책](https://docs.cloud.google.com/dns/docs/policies?hl=ko#create-in)을 만듭니다.
    
*   다른 네트워크가 연결되는 VPC 네트워크에서 VLAN 연결 또는 Cloud VPN 터널이 있는 리전의 [인바운드 전달자 진입점](https://docs.cloud.google.com/dns/docs/policies?hl=ko#list-in-entrypoints)을 식별합니다.
    
*   다른 네트워크에 있는 시스템 및 DNS 네임서버를 구성하여 [엔드포인트의 DNS 이름](https://docs.cloud.google.com/vpc/docs/access-regional-google-apis-endpoints?hl=ko#configure-dns)을 VPC 네트워크에 연결되는 VLAN 연결 또는 Cloud VPN 터널과 동일한 리전의 인바운드 전달자 진입점으로 전달하세요.
