# Device Farm (Android AVD) di SumoPod

Menjalankan farm emulator Android di VPS SumoPod, di belakang Selenium Grid,
supaya framework automation bisa run paralel dari mana saja.

> ⚠️ **SumoPod itu VPS/PaaS Linux (Tencent Cloud Jakarta), bukan penyedia
> perangkat Android.** Emulator hanya jalan wajar jika VPS mengekspos `/dev/kvm`.
> Ini bukan default di kebanyakan VPS cloud — **cek dulu.**

## Langkah 0 — Cek KVM (WAJIB, penentu)

SSH ke VPS SumoPod, jalankan:

```bash
ls -l /dev/kvm && egrep -c '(vmx|svm)' /proc/cpuinfo
```

- Ada `/dev/kvm` + angka > 0 → lanjut.
- `No such file` / permission denied → **hosting emulator tidak layak di sini.**
  Lihat bagian "Kalau tanpa KVM" di bawah. Tanya support SumoPod apakah ada
  paket dengan *nested virtualization*.

## Langkah 1 — Prasyarat host

```bash
# Docker + compose plugin
curl -fsSL https://get.docker.com | sh
```

## Langkah 2 — Jalankan farm

```bash
cd deploy/device-farm
docker compose up -d
docker compose ps
```

- Grid console: `http://<IP-SUMOPOD>:4444/ui`
- Lihat layar emulator (debug): `http://<IP-SUMOPOD>:6080`

## Langkah 3 — Arahkan framework ke farm

Tidak perlu ubah kode. Override saat `mvn test`:

```bash
mvn test -Dappium.server.url=http://<IP-SUMOPOD>:4444
```

atau ubah `appium.server.url` di
`src/test/resources/config/config.properties`.

## Menambah kapasitas paralel

Tiap emulator ≈ butuh **2 vCPU + 2–4 GB RAM**. Untuk N emulator, sediakan
resource ~N kali lipat. Duplikasi blok `android-node-*` di `docker-compose.yml`
(sudah ada contoh yang di-comment), ganti nama/host/port.

## Keamanan (penting, farm terbuka ke internet)

- Port 4444/6080 **jangan** dibuka bebas. Batasi via firewall SumoPod ke IP kamu,
  atau akses lewat SSH tunnel:
  ```bash
  ssh -L 4444:localhost:4444 user@<IP-SUMOPOD>
  ```
- noVNC (6080) tanpa auth = siapa pun bisa lihat emulator. Tutup kalau tak dipakai.

## Kalau tanpa KVM (fallback)

Kalau SumoPod ternyata tak mendukung KVM, pilihan realistis:

1. **redroid** (Android dalam container) — tak butuh KVM penuh, **tapi** butuh
   modul kernel `binder`/`ashmem` di-load di host + container privileged.
   Biasanya baru bisa di VPS bare-metal / yang kernelnya kamu kontrol. Cek:
   ```bash
   ls /dev/binder* ; lsmod | grep -E 'binder|ashmem'
   ```
2. **Emulator di mesin lokal** (laptop/PC ber-KVM/HAXM), SumoPod hanya jadi
   **Selenium Grid Hub + reporting**; node emulator daftar dari lokal ke hub.
3. **Device cloud SaaS** (BrowserStack/LambdaTest/Sauce Labs) untuk device asli —
   bukan self-host, tapi paling minim ribet.
