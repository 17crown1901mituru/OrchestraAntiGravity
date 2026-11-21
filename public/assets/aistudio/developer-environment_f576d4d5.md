# Menyiapkan lingkungan pengembangan lokal &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/developer-environment

Pelajari cara mengonfigurasi lingkungan pengembangan lokal untuk Google Cloud. Dokumen ini membahas alat inti yang diperlukan untuk membangun, menguji, dan menjalankan aplikasi dengan layanan Google Cloud :

*   **gcloud CLI**: Mengelola resource cloud, mengonfigurasi project, dan menyiapkan Kredensial Default Aplikasi (ADC) lokal.
*   **Library Klien Cloud**: Akses Google Cloud API secara terprogram menggunakan library idiomatis dalam bahasa pemrograman pilihan Anda.

Dokumen ini juga menunjukkan cara menginstal plugin `google-cloud-developer` untuk mendapatkan bantuan terkait tugas Google Cloud dan coding di sejumlah agen yang berbeda.

Untuk ringkasan alat dan antarmuka lainnya di Google Cloud, lihat [Cara berinteraksi dengan Google Cloud](https://docs.cloud.google.com/docs/get-started/interact-with-resources?hl=id).

Sebelum memulai
---------------

Minta administrator organisasi Anda untuk menyelesaikan prasyarat berikut:

*   **Sediakan identitas pengguna Anda**: Tetapkan akun Cloud Identity atau Google Workspace.
*   **Memberikan akses project dan peran IAM**: Tetapkan peran Identity and Access Management (IAM) yang diperlukan untuk project Anda.
*   **Mengaktifkan penagihan**: Pastikan Penagihan Cloud diaktifkan di project Anda.

Untuk penyiapan di seluruh organisasi, lihat [Google Cloud Alur panduan penyiapan](https://docs.cloud.google.com/docs/enterprise/cloud-setup?hl=id).

Menyiapkan gcloud CLI dan autentikasi
-------------------------------------

Konfigurasikan gcloud CLI (`gcloud`) untuk mengelola resource dan mengautentikasi lingkungan lokal Anda:

1.  [Instal gcloud CLI](https://docs.cloud.google.com/sdk/docs/install-sdk?hl=id) di workstation lokal Anda.
2.  Jalankan `gcloud init` di terminal Anda untuk login dan memilih project default Anda.
3.  Jalankan `gcloud auth application-default login` untuk mengonfigurasi Kredensial Default Aplikasi (ADC). ADC memungkinkan library klien lokal melakukan autentikasi secara otomatis terhadap project Anda.

Menginstal Library Klien Cloud
------------------------------

Untuk mengakses layanan Google Cloud secara terprogram dari kode aplikasi Anda:

1.  [Aktifkan Cloud API yang diperlukan](https://docs.cloud.google.com/apis/docs/getting-started?hl=id) untuk project Anda.
2.  Instal [Library Klien Cloud](https://docs.cloud.google.com/apis/docs/cloud-client-libraries?hl=id) untuk bahasa pemrograman Anda (seperti Java, Python, atau Go). Kode Anda melakukan autentikasi secara otomatis menggunakan ADC.

Menginstal plugin `google-cloud-developer`
------------------------------------------

Jika Anda menggunakan agen seperti Claude Code CLI, Antigravity CLI, atau Codex CLI untuk membantu Anda dalam pekerjaan pengembangan, Anda dapat menginstal plugin `google-cloud-developer` untuk mendapatkan bantuan terkait tugas Google Cloud dan coding. Plugin ini mencakup:

*   Keterampilan untuk membantu pembuatan akun, penyiapan penagihan, pengelolaan project, dan autentikasi ke layanan dan API Google Cloud .
*   Skill `finding-google-skills`, yang menemukan dan menjalankan skill produk Google yang tepat sesuai permintaan, sehingga tidak perlu memuat skill Google terlebih dahulu.
*   Server MCP jarak jauh [Developer Knowledge](https://developers.google.com/knowledge/mcp?hl=id), yang memberikan kemampuan untuk menelusuri dokumentasi developer resmi Google dan mengambil informasi untuk produk Google seperti Google Cloud, Firebase, Android, Maps, dan lainnya.

Instal plugin `google-cloud-developer` menggunakan petunjuk berikut berdasarkan agen Anda.

### Antigravity

Instal plugin dari Antigravity Marketplace:

1.  Di Antigravity, buka Customizations > Marketplace dan telusuri "Google Cloud Developer".
2.  Klik tombol **Add**.

Atau, instal plugin menggunakan Antigravity CLI:

agy plugin install google-cloud-developer

### Claude Code CLI

1.  Tambahkan marketplace plugin Google, lalu instal plugin:
    
    claude plugin marketplace add google/skills
        claude plugin install google-cloud-developer@google-plugins
    
2.  Aktifkan Developer Knowledge API di project Google Cloud Anda menggunakan gcloud CLI:
    
    gcloud services enable developerknowledge.googleapis.com --project=YOUR_PROJECT_ID
    
3.  Buat kunci API untuk Developer Knowledge API dengan mengikuti petunjuk [Membuat dan mengamankan kunci API](https://developers.google.com/knowledge/quickstart?hl=id#create-secure-key). Simpan kunci yang Anda buat di lokasi yang aman.
    
4.  Ekspor kunci API ke lingkungan Anda sebelum memulai Claude Code CLI:
    
    export DEVELOPERKNOWLEDGE_API_KEY=YOUR_API_KEY
    

### Codex CLI

1.  Tambahkan marketplace plugin Google, lalu instal plugin:
    
    codex plugin marketplace add google/skills
        codex plugin add google-cloud-developer@google-plugins
    
2.  Aktifkan Developer Knowledge API di project Google Cloud Anda menggunakan gcloud CLI:
    
    gcloud services enable developerknowledge.googleapis.com --project=YOUR_PROJECT_ID
    
3.  Buat kunci API untuk Developer Knowledge API dengan mengikuti petunjuk [Membuat dan mengamankan kunci API](https://developers.google.com/knowledge/quickstart?hl=id#create-secure-key). Simpan kunci yang Anda buat di lokasi yang aman.
    
4.  Aktifkan akses yang diautentikasi ke server MCP Pengetahuan Developer dengan memperbarui `~/.codex/config.toml` (atau `.codex/config.toml` project Anda) untuk menyertakan baris berikut:
    
    [mcp_servers.developer-knowledge]
          url = "https://developerknowledge.googleapis.com/mcp"
          env_http_headers = { "X-Goog-Api-Key" = "DEVELOPERKNOWLEDGE_API_KEY" }
    
5.  Ekspor kunci API ke lingkungan Anda sebelum memulai Codex:
    
    export DEVELOPERKNOWLEDGE_API_KEY=YOUR_API_KEY

Alat dan lingkungan alternatif
------------------------------

Jika alur kerja Anda memerlukan lingkungan pengembangan atau alat infrastruktur tertentu, pertimbangkan hal berikut:

*   **IDE dan ekstensi**: Kembangkan di [VS Code](https://docs.cloud.google.com/code/docs/vscode?hl=id) atau [IDE JetBrains yang didukung](https://docs.cloud.google.com/code/docs/intellij?hl=id) dengan dukungan Cloud Code.
*   **Cloud Workstations**: Gunakan lingkungan cloud terkelola dalam container dengan [Cloud Workstations](https://docs.cloud.google.com/workstations/docs/overview?hl=id).

Langkah berikutnya
------------------

*   Pelajari [Autentikasi di Google Cloud](https://docs.cloud.google.com/docs/get-started/authentication?hl=id).
