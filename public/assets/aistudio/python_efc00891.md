# 建構 Python 應用程式 &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/python

Buildpack 支援透過環境變數進行語言慣用設定。

指定 Python 版本
------------

根據預設，Python 執行階段建構包會使用最新穩定版的 Python 解譯器。如果應用程式需要特定版本，您可以在應用程式的根目錄中加入 `.python-version` 檔案，指定所需版本。

3.14

### 使用`GOOGLE_PYTHON_VERSION`

您也可以使用 `GOOGLE_PYTHON_VERSION` 環境變數指定 Python 版本。如果同時設定這兩項，系統會優先採用 `GOOGLE_PYTHON_VERSION` 值，而非 `.python-version` 檔案。根據預設，如果未指定 `.python-version` 檔案和 `GOOGLE_PYTHON_VERSION` 環境變數，系統會使用最新 LTS 版本的 Python。

如要設定建構包，以便在部署應用程式時使用[支援的 Python 版本](https://docs.cloud.google.com/docs/buildpacks/runtime-support?hl=zh-tw#python)，請按照下列步驟操作：

pack build sample-python --builder=gcr.io/buildpacks/builder \
      --env GOOGLE_PYTHON_VERSION="3.14.x"

您也可以使用 `project.toml` 專案描述元，在專案檔案旁編碼環境變數。請參閱[使用環境變數建構應用程式](https://docs.cloud.google.com/docs/buildpacks/set-environment-variables?hl=zh-tw#build_the_application_with_environment_variables\))的說明。

指定依附元件
------

使用下列任一方法，為[支援的 Python 版本](https://docs.cloud.google.com/docs/buildpacks/runtime-support?hl=zh-tw#python)指定應用程式依附元件：

*   使用根目錄中的 `requirements.txt` 檔案。這個檔案必須與包含原始碼的 `main.py` 檔案位於同一目錄。`requirements.txt` 檔案中一行代表一個套件，每行都包含套件名稱以及要求的版本。為避免建構過程受到依附元件版本變更的影響，建議將依附元件套件固定於特定版本。
    
    以下是 `requirements.txt` 檔案範例：
    
    functions-framework
        requests==2.20.0
        numpy
    
*   使用 `pyproject.toml` 檔案指定依附元件。如果您在 `pyproject.toml` 檔案中管理應用程式依附元件，而非 `requirements.txt` 檔案，Python 建構套件會根據您在 `pyproject.toml` 檔案中指定的設定，判斷套件管理員。詳情請參閱[使用 `pyproject.toml` 檔案部署 Python 應用程式](https://docs.cloud.google.com/docs/buildpacks/python?hl=zh-tw#deploy-with-toml)。
    
    如果應用程式同時使用 `pyproject.toml` 檔案和 `requirements.txt` 檔案，系統會優先採用 `requirements.txt` 檔案。
    
    *   以下是 `pyproject.toml` 檔案範例：
        
        [project]
            name = "demo-app"
            version = "0.1.0"
            description = ""
            requires-python = ">=3.10"
            dependencies = [
                "flask>=3.1.1",
                "gunicorn>=23.0.0",
            ]
            
            [build-system]
            requires = ["setuptools>=61.0"]
            build-backend = "setuptools.build_meta"
        

套件管理工具
------

如果您使用 `requirements.txt file` 管理依附元件，預設套件管理工具會因您設定的 Python 版本而異。

如果您使用 `pyproject.toml` 檔案管理依附元件，而非 `requirements.txt` 檔案，Python 建構套件會根據 `pyproject.toml` 檔案中的設定，判斷要使用哪個套件管理工具。建構包支援 pip、uv 和 Poetry 套件管理工具。詳情請參閱「[使用 `pyproject.toml` 檔案部署 Python 應用程式](https://docs.cloud.google.com/docs/buildpacks/python?hl=zh-tw#deploy-with-toml)」。

### Python 3.14 以上版本

從 Python 3.14 以上版本開始，Python 建構套件會使用 [uv](https://docs.astral.sh/uv/) 套件管理員，做為您在 `requirements.txt` 檔案中指定依附元件的預設安裝程式。

如要使用 [`pip`](http://pip.readthedocs.org/) 做為套件管理員，請設定環境變數 `GOOGLE_PYTHON_PACKAGE_MANAGER="pip"`。

### Python 3.13 以下版本

如果是 Python 3.13 版和更早版本，Python 建構套件會使用 [pip](http://pip.readthedocs.org/) 套件管理工具，安裝您在 [`requirements.txt`](http://pip.readthedocs.org/en/stable/user_guide/#requirements-files) 檔案中定義的依附元件。

如要使用 [uv](https://docs.astral.sh/uv/) 做為套件管理員，請設定環境變數 `GOOGLE_PYTHON_PACKAGE_MANAGER="uv"`。

### 設定 pip

您可以[使用環境變數](https://pip.pypa.io/en/stable/topics/configuration/)設定 pip 的行為：

pack build sample-python --builder=gcr.io/buildpacks/builder \
      --env PIP_DEFAULT_TIMEOUT='60'

### Artifact Registry 中的私人依附元件

[Artifact Registry Python 存放區](https://docs.cloud.google.com/artifact-registry/docs/python/store-python?hl=zh-tw)可代管 Python 函式的私人依附元件。在 Cloud Build 上建構應用程式時，Python 建構套件會自動為 [Cloud Build 服務帳戶](https://docs.cloud.google.com/build/docs/cloud-build-service-account?hl=zh-tw)產生 Artifact Registry 憑證。您只需要在 `requirements.txt` 中加入 Artifact Registry 網址，不必產生額外憑證。例如：

--extra-index-url REPOSITORY_URL
    sampleapp
    Flask==0.10.1
    google-cloud-storage

應用程式進入點
-------

下節說明 Python 建構包的預設進入點。

### Cloud Run 來源部署作業的進入點

只有在您使用 Python 執行階段[將原始碼部署至 Cloud Run](https://docs.cloud.google.com/run/docs/deploying-source-code?hl=zh-tw) 時，才能使用這項功能。如果您直接使用 `pack build` 建構容器映像檔，而非透過 Cloud Run 來源部署程序，則不適用這項功能。

[Python 建構套件](https://docs.cloud.google.com/docs/buildpacks/python?hl=zh-tw)支援現代網路架構，例如 [FastAPI](https://fastapi.tiangolo.com/)、[Gradio](https://www.gradio.app/)、[Streamlit](https://streamlit.io/) 和 [Agent Development Kit (ADK)](https://google.github.io/adk-docs/)。

#### Python 3.12 以下版本

如果您使用 Python 3.12 以下版本，Python 建構套件預設會使用 [Gunicorn](https://gunicorn.org/) 做為工作負載的 WSGI HTTP 伺服器。Python 建構套件會將預設進入點設為 `gunicorn -b :8080 main:app`。

#### Python 3.13 以上版本

如果是 Python 3.13 以上版本，Python 建構套件會根據 `requirements.txt` 檔案中的網路伺服器或架構設定，為 [Cloud Run 來源部署作業](https://docs.cloud.google.com/run/docs/deploying-source-code?hl=zh-tw)設定預設進入點。這項預設設定僅適用於 Cloud Run 服務來源部署作業，不適用於 Cloud Run 函式。

使用 Python 執行階段從來源部署 Cloud Run 服務時，建構包會透過下列方式判斷 Python 版本和預設進入點：

*   如果您未在來源檔案中指定 Python 版本，Python 建構包會將預設版本設為[最新支援的 Python 版本](https://docs.cloud.google.com/docs/buildpacks/runtime-support?hl=zh-tw#python)。建構包會根據您在 `requirements.txt` 檔案中設定的網路伺服器或架構[](#default-entrypoint)，決定預設進入點。
    
*   如果您未在 `requirements.txt` 檔案中指定網路伺服器或框架，Python 建構包預設會使用 Gunicorn 做為工作負載的 WSGI HTTP 伺服器。Python 建構套件會將預設進入點設為 `gunicorn -b :8080 main:app`。
    
*   Python 建構套件會根據 `requirements.txt` 檔案中定義的下列優先順序，設定預設進入點：
    
    1.  `gunicorn`
    2.  `uvicorn`
    3.  `fastapi[standard]`
    4.  `gradio`
    5.  `streamlit`
    6.  `google-adk`

#### 設定網路伺服器或框架

下表列出從來源部署至 Cloud Run 時，`requirements.txt` 檔案中各項常見 Python 設定的預設進入點：

| 主要設定 | 預設進入點 | 環境變數 |
| --- | --- | --- |
| `gunicorn` | `gunicorn -b :8080 main:app` |  |
| `numpy` | `gunicorn -b :8080 main:app` |  |
| `fastapi`  
`uvicorn` | `uvicorn main:app --host 0.0.0.0 --port 8080` |  |
| `fastapi[standard]` | `uvicorn main:app --host 0.0.0.0 --port 8080` |  |
| `uvicorn`  
`gunicorn` | `gunicorn -b :8080 main:app` |  |
| `gradio` | `python main.py` | `GRADIO_SERVER_NAME=0.0.0.0`  
`GRADIO_SERVER_PORT=8080` |
| `streamlit` | `streamlit run main.py --server.address 0.0.0.0 --server.port 8080` |  |
| `google-adk` | `adk api_server --host 0.0.0.0 --port 8080` |  |

為避免部署失敗，請在來源檔案中使用[支援的 Python 版本](https://docs.cloud.google.com/docs/buildpacks/runtime-support?hl=zh-tw#python)，並在 `requirements.txt` 檔案中指定網路伺服器。

或者，您也可以執行下列來源部署指令來指定進入點：

  gcloud run deploy SERVICE --source .  --set-build-env-vars GOOGLE_ENTRYPOINT="ENTRYPOINT"

更改下列內容：

*   SERVICE：要部署的服務名稱。
*   ENTRYPOINT：要用於原始碼的預設進入點。

如果無法將原始碼部署至 Cloud Run，或在記錄中發現錯誤，請參閱 [Cloud Run 疑難排解指南](https://docs.cloud.google.com/run/docs/troubleshooting?hl=zh-tw#source-deploy-errors)。

### 所有其他部署作業的進入點

Python 建構套件會使用 [Gunicorn](https://gunicorn.org/) 做為工作負載的預設 WSGI HTTP 伺服器。使用 Python 建構套件建構的應用程式會以預設設定啟動 `gunicorn` 程序，類似於執行：

gunicorn --bind :8080 main:app

### 自訂應用程式進入點

您可以使用 `Procfile` 或環境變數，自訂應用程式啟動指令。您可能需要這麼做，才能自訂[預設進入點設定](https://docs.cloud.google.com/docs/buildpacks/python?hl=zh-tw#default-entrypoint)。

您可以在根目錄中建立 `Procfile`，並使用自訂設定。 範例：

web: gunicorn --bind :$PORT --workers 1 --threads 8 --timeout 0 main:app

或者，您也可以使用 `GOOGLE_ENTRYPOINT` 環境變數搭配 `pack` 指令。範例：

pack build sample-python \
      --builder gcr.io/buildpacks/builder
      --env "GOOGLE_ENTRYPOINT='gunicorn --bind :$PORT main:app'"

環境變數
----

Python buildpack 支援下列環境變數，可自訂容器

### `PIP_<key>`

請參閱 pip [說明文件](https://pip.pypa.io/en/stable/user_guide/#environment-variables)。

**範例：** `PIP_DEFAULT_TIMEOUT=60` 會為 `pip` 指令設定 `--default-timeout=60`。

使用 `pyproject.toml` 檔案部署 Python 應用程式
------------------------------------

Python 建構包支援您使用 `pyproject.toml` 檔案設定的專案。這項功能可讓您直接將透過 Poetry、uv 或 pip 管理的應用程式部署至 Cloud Run 和 Cloud Run functions。App Engine 不支援這項功能。

只有當根目錄中沒有 `requirements.txt` 檔案時，Python 建構套件才會使用 `pyproject.toml` 檔案。如果應用程式同時使用 `pyproject.toml` 檔案和 `requirements.txt` 檔案，則 `requirements.txt` 檔案的優先順序較高。

### 支援的建構套件設定

Python 建構套件支援下列設定：

*   **pip buildpack**：如果偵測到下列所有條件，會直接從 `pyproject.toml` 安裝依附元件：
    
    *   根目錄中存在 `pyproject.toml` 檔案，且您未設定高優先順序工具，例如 `poetry.lock` 檔案、`[tool.poetry]` 區段或 `uv.lock` 檔案。
        
    *   將 `GOOGLE_PYTHON_PACKAGE_MANAGER` 環境變數設為 `pip`。
        
*   **uv 建構包**：支援以 [uv](https://docs.astral.sh/uv) 管理的 Python 專案。如果偵測到下列任一情況，這個建構包就會啟動：
    
    *   專案根目錄中存在 `uv.lock` 檔案和 `pyproject.toml` 檔案。
    *   專案根目錄中存在 `pyproject.toml` 檔案，且您已將 `GOOGLE_PYTHON_PACKAGE_MANAGER` 環境變數設為 `uv`。
    *   存在 `pyproject.toml` 檔案，且您未加入其他高優先順序的鎖定檔案 (例如 `poetry.lock`、`uv.lock`) 或設定 (例如 `[tool.poetry]`)，且您未設定 `GOOGLE_PYTHON_PACKAGE_MANAGER` 環境變數。
*   **Poetry 建構包**：支援您使用 [Poetry](https://python-poetry.org/) 管理的 Python 專案。如果偵測到下列任一情況，這個建構套件就會啟動：
    
    *   專案根目錄中存在 `poetry.lock` 檔案和 `pyproject.toml` 檔案。
    *   專案根目錄中存在 `pyproject.toml` 檔案，且 `pyproject.toml` 檔案中存在 `[tool.poetry]` 區段。

### 套件管理工具優先順序

Python 建構套件會根據下列優先順序的設定，決定預設套件管理員：

1.  系統會優先處理 `requirements.txt` 檔案。只有在有這個檔案的情況下，Python 建構包才會使用[預設套件管理員](#package-manager)，在建構步驟中安裝依附元件。如果沒有 `requirements.txt` 檔案，偵測程序會繼續下一個步驟。
    
2.  建構套件接著會檢查 `pyproject.toml` 檔案是否有 `poetry.lock` 檔案或 `[tool.poetry]` 區段。如果找到，建構程序會繼續使用 Poetry 安裝依附元件。
    
3.  如果系統未偵測到 Poetry 設定，建構套件會檢查 `uv.lock` 檔案。如果找到，建構程序會繼續使用 uv 安裝依附元件。
    
4.  如果沒有鎖定檔案，建構包會檢查 `GOOGLE_PYTHON_PACKAGE_MANAGER` 環境變數，瞭解 `pip` 或 `uv` 設定。
    
5.  預設值，如果您未設定環境變數，且只使用 `pyproject.toml` 檔案 (不含 uv 或 Poetry)，建構包預設會對所有[支援的 Python 版本](https://docs.cloud.google.com/docs/buildpacks/runtime-support?hl=zh-tw#python)使用 uv。
    

### 含有 `pyproject.toml` 檔案的進入點

如果您使用 `pyproject.toml` 檔案部署應用程式，而非使用 `requirements.txt` 檔案，Python 建構包會採用不同的方法判斷進入點。如要瞭解如何使用 `requirements.txt` 檔案設定應用程式進入點，請參閱「[應用程式進入點](#entrypoint)」。

建構包會按照以下優先順序搜尋進入點：

1.  如果根目錄中存在 `Procfile`，或您設定了 `GOOGLE_ENTRYPOINT` 環境變數，這些設定一律會覆寫 `pyproject.toml` 指令碼判斷的任何進入點。
    
2.  Python 建構包會使用您在 `[tool.poetry.scripts]` 和 `[project.scripts]` 區段中設定的自訂指令碼。如果您設定的[指令碼](https://packaging.python.org/en/latest/guides/writing-pyproject-toml/#creating-executable-scripts)包含 `start`，這就是您的進入點。 例如 `poetry run start` 或 `uv run start`。
    
3.  如果您未設定 `start` 指令碼，但定義了其他指令碼，則您定義的指令碼就是預設進入點。例如：`poetry run mycmd` 或 `uv run mycmd`。
    

與以 `requirements.txt` 為基礎的建構作業不同，Python 建構套件不會自動為 `pyproject.toml` 專案安裝 `gunicorn`。如要使用 `gunicorn` 或任何其他伺服器，您必須在 `pyproject.toml` 檔案中明確新增至依附元件。

如果您未在 `pyproject.toml` 檔案中設定[自訂指令碼](https://github.com/GoogleCloudPlatform/buildpacks/tree/main/builders/testdata/python/generic/pyproject_script)，建構包會嘗試從 `pyproject.toml` 依附元件偵測常見的架構，例如 `gunicorn`、`uvicorn` 或 `fastapi`，並[判斷預設進入點](#entrypoint)。
