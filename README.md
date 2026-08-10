# Kasir Pintar Pro — Mobile Automation (Appium + Cucumber BDD)

Automated UI testing for the **CRUD flow of Barang (item) tipe Default** on the
**Kasir Pintar** Android app (`org.owline.kasirpintar`), built with **Appium 2**
and **Cucumber BDD** (Java 17 + Maven).

> ✅ **Terverifikasi hijau & stabil** — full suite `@barang` (7 skenario CRUD:
> Create, 3× data-driven, Read, Update, Delete) lolos **7/7, 0 flaky, dua kali
> berturut-turut** di Android Emulator (API 35, arm64). Target: **Kasir Pintar
> PRO** `org.owline.kasirpintarpro`.
>
> Jalankan cepat:
> ```bash
> export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
> export ANDROID_HOME="$HOME/Library/Android/sdk"
> export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
> ./scripts/prepare-device.sh org.owline.kasirpintarpro   # grant izin + stay-awake
> appium &                                                # terminal terpisah (ANDROID_HOME wajib)
> mvn test -Denv=dev -Dcucumber.filter.tags="@barang"
> ```
>
> **Catatan device:** disarankan **emulator / device non-MIUI**. Device MIUI
> (Xiaomi) sangat flaky (cloud-sync + state-restore) dan memblokir adb install.
> App PRO adalah split APK; pasang dengan
> `adb install-multiple base.apk split_config.arm64_v8a.apk split_config.xxhdpi.apk`
> (split ada di `src/test/resources/app/_splits/`).

### Setup toolchain (macOS)

```bash
brew install openjdk@17 maven
# JDK 17 keg-only, arahkan JAVA_HOME saat menjalankan:
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

---

## 1. Prasyarat (Prerequisites)

| Tool | Versi disarankan | Catatan |
|------|------------------|---------|
| JDK | 17+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node.js | 18+ | untuk menjalankan Appium |
| Appium Server | 2.x | `npm i -g appium` |
| Appium UiAutomator2 Driver | latest | `appium driver install uiautomator2` |
| Android SDK / Platform Tools | latest | `adb` harus ada di PATH |
| Android Emulator atau device fisik | Android 8+ | USB debugging aktif untuk device fisik |
| Allure CLI (opsional) | 2.x | untuk membuka report HTML |

Verifikasi environment:

```bash
adb devices          # device/emulator harus terlihat
appium driver list   # uiautomator2 harus "installed"
```

---

## 2. Konfigurasi

Semua konfigurasi ada di
[`src/test/resources/config/config.properties`](src/test/resources/config/config.properties).

Yang **wajib** disesuaikan sebelum run:

1. **Aplikasi** — salah satu:
   - Taruh APK di `src/test/resources/app/kasir-pintar-pro.apk` (auto-install), **atau**
   - Kosongkan/hapus `app.path` dan isi `app.package` + `app.activity` untuk app yang sudah terpasang.
2. **Akun login** — taruh di file **`src/test/resources/config/config.local.properties`**
   (git-ignored, tidak ikut ter-commit):
   ```properties
   login.email=akun@contoh.com
   login.password=RahasiaKamu
   ```
3. **Device** — sesuaikan `device.name` / `platform.version` bila perlu.

> App target sudah terverifikasi: **Kasir Pintar** `org.owline.kasirpintar`
> (activity `.SplashScreen`), versi **5.1.2**. Package/activity sudah terisi
> di `config.properties`.

Setiap key bisa dioverride dari CLI, contoh:
```bash
mvn test -Dlogin.email=qa@demo.com -Dlogin.password=secret
```

> **Locator sudah asli** — semua `resource-id` di page object diambil langsung
> dari app di device (bukan placeholder). Jika Kasir Pintar merilis versi baru
> yang mengubah id, perbarui via Appium Inspector.
>
> **Catatan alur app:** menu barang ada di **"Product or Service"** (halaman
> *Management* setelah login), field **Code wajib diisi**, dan "Item Type" sudah
> default **"Default"**. `no.reset=false` membuat tiap skenario login ulang dari
> awal (data bersih & independen).
>
> **Quirk aplikasi yang sudah ditangani otomatis oleh framework:**
> - Setelah login muncul dialog **"access your Device type?"** dengan *delay* dan
>   bisa muncul berulang; framework mem-*poll* & men-dismiss-nya via touch gesture
>   (`ManagementPage`).
> - Nama produk disimpan dengan **spasi di belakang** (mis. `"Teh Manis "`), jadi
>   pencocokan baris memakai `txtNamaBarang` + `textContains`.

---

## 3. Instalasi dependency

```bash
mvn clean install -DskipTests
```

---

## 4. Menjalankan Appium server

> **PENTING:** Appium server **wajib** punya `ANDROID_HOME`/`ANDROID_SDK_ROOT`
> yang menunjuk ke Android SDK, atau session gagal dibuat
> (*"Neither ANDROID_HOME nor ANDROID_SDK_ROOT ... exported"*).

Di terminal terpisah:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
appium
```

Server default: `http://127.0.0.1:4723` (sesuai `appium.server.url`).

Pastikan device terbaca: `adb devices` harus menampilkan device kamu
(mis. `R9RW106JEVW  device`). Dengan satu device terhubung, Appium memilihnya
otomatis.

---

## 5. Menjalankan test

**Seluruh skenario CRUD:**

```bash
mvn test
```

**Skenario / tag tertentu** (via `cucumber.filter.tags`):

```bash
mvn test -Dcucumber.filter.tags="@create"
mvn test -Dcucumber.filter.tags="@update or @delete"
mvn test -Dcucumber.filter.tags="@barang and @smoke"
```

Tag yang tersedia: `@barang` (semua), `@create`, `@read`, `@update`,
`@delete`, `@smoke`, `@data-driven`.

**Skenario data-driven:** file feature memuat `Skenario konsep` (Scenario
Outline) dengan tabel `Contoh` (Examples) untuk membuat beberapa barang
sekaligus dari data — satu step definition dipakai ulang untuk banyak baris data
(lihat [`barang_crud.feature`](src/test/resources/features/barang_crud.feature)).

---

## 6. Laporan (Reports)

Setelah run:

- **Cucumber HTML** → `target/cucumber-report/cucumber.html`
- **Cucumber JSON** → `target/cucumber-report/cucumber.json`
- **Allure** (hasil mentah di `allure-results/`):

```bash
allure serve allure-results
```

Screenshot otomatis dilampirkan ke report **saat skenario gagal**
(lihat [`Hooks.java`](src/test/java/com/kasirpintar/automation/steps/Hooks.java)).

---

## 7. Struktur proyek

```
kasir-pintar-mobile-automation/
├── pom.xml
├── README.md
└── src/test/
    ├── java/com/kasirpintar/automation/
    │   ├── config/    ConfigReader.java          # config.properties + config.local + override CLI
    │   ├── driver/    DriverManager.java          # lifecycle AndroidDriver (thread-safe)
    │   ├── model/     Barang.java                 # data uji (record)
    │   ├── pages/     BasePage, WelcomePage,       # Page Object Model
    │   │              LoginPage, ManagementPage,
    │   │              BarangListPage, BarangFormPage,
    │   │              BarangDetailPage
    │   ├── runner/    TestRunner.java              # JUnit 5 suite -> Cucumber
    │   └── steps/     BarangSteps.java, Hooks.java
    └── resources/
        ├── config/    config.properties            # + config.local.properties (git-ignored: kredensial)
        ├── features/  barang_crud.feature          # skenario BDD (English Gherkin)
        └── app/       (taruh .apk di sini, opsional)
```

**Prinsip desain**
- **Page Object Model** — locator & interaksi terpisah dari step definition.
- **Explicit wait** (`WebDriverWait`) di `BasePage`, tanpa `Thread.sleep` → reliabel.
- **Separation of concerns** — feature (skenario) ↔ step definition ↔ page object ↔ driver.
- **Config-driven** — device, app, akun, dan wait bisa diatur tanpa ubah kode.
- **Thread-local driver** — siap dikembangkan ke eksekusi paralel.

---

## 8. Continuous Integration (GitHub Actions)

Workflow: [`.github/workflows/android-automation.yml`](.github/workflows/android-automation.yml).

Berjalan otomatis pada push/PR ke `main`, terjadwal mingguan, dan bisa
dijalankan manual (**Actions → Run workflow**, isi tag yang diinginkan).

Yang dilakukan pipeline:
1. Setup JDK 17, Node.js, Appium 2 + driver UiAutomator2.
2. Menjalankan Android emulator (API 30) via `reactivecircus/android-emulator-runner`.
3. `mvn test` dengan tag dari input.
4. Upload artifact: Cucumber report, Allure results, log Appium.
5. Publish Allure HTML report ke **GitHub Pages** (branch `main`).

**Secrets yang perlu diset** (Settings → Secrets and variables → Actions):

| Secret | Isi |
|--------|-----|
| `APP_BASE64` | APK di-encode base64: `base64 -i kasir-pintar-pro.apk \| pbcopy` |
| `LOGIN_EMAIL` | email akun tes |
| `LOGIN_PASSWORD` | password akun tes |

> Emulator butuh akselerasi hardware (KVM). Job memakai runner `ubuntu-latest`
> dengan step *Enable KVM* — billing 1x (lebih murah dari macOS yang 10x) dan
> sekarang jadi setup yang direkomendasikan action-nya.

---

## 9. Troubleshooting singkat

| Gejala | Kemungkinan penyebab / solusi |
|--------|-------------------------------|
| `Neither ANDROID_HOME nor ANDROID_SDK_ROOT ... exported` | Export `ANDROID_HOME` **di terminal tempat Appium server dijalankan** (lihat §4) |
| `Could not start AndroidDriver ...` | Appium server belum jalan / URL salah |
| Test langsung gagal di login | `login.email` / `login.password` belum diisi di `config.local.properties` |
| `cannot access org.openqa.selenium.ContextAware` saat compile | Versi Selenium/appium-client bentrok → pakai `appium.java.client.version=10.1.1` (sudah di `pom.xml`) |
| Elemen tidak ketemu | App update mengubah `resource-id` → perbarui locator via Appium Inspector |
| App tidak terpasang | `app.path` salah, atau `app.package`/`app.activity` belum diisi |
| Device/emulator tak terdeteksi | jalankan `adb devices`, pastikan device online |
