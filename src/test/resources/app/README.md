# App binary folder

Drop the **Kasir Pintar Pro** APK here, then install it once.

## The simple way — one `.apk`
Put a single **universal APK** in this folder:
```
src/test/resources/app/kasir-pintar-pro.apk
```
Then:
```bash
scripts/install-app.sh            # first connected device
scripts/install-app.sh <serial>   # a specific device/emulator
```

> The single `.apk` must be a **universal / standalone** APK (contains all ABIs
> and resources). A split **base-only** APK installed alone fails with
> `INSTALL_FAILED_MISSING_SPLIT`. To make a universal APK from an App Bundle:
> ```bash
> bundletool build-apks --bundle=app.aab --output=app.apks --mode=universal
> unzip -p app.apks universal.apk > kasir-pintar-pro.apk
> ```

## Fallback — split APKs
If you only have the split APKs, put them under `_splits/` and the install
script handles them automatically:
```
src/test/resources/app/_splits/
  ├── base.apk
  ├── split_config.arm64_v8a.apk
  └── split_config.xxhdpi.apk
```

Notes:
- The framework does **not** reinstall the app during the run; it attaches to
  the installed app via `app.package`. So `app.path` in config is empty.
- Everything here is git-ignored — never commit the binaries.
