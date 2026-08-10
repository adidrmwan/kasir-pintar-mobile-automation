# App binary folder

Place the **Kasir Pintar Pro** app here so it can be installed onto the
device/emulator for local runs. It is a **split APK**, so use one of:

**A) Split APKs (recommended — what we have):**
```
src/test/resources/app/_splits/
  ├── base.apk
  ├── split_config.arm64_v8a.apk
  └── split_config.xxhdpi.apk
```

**B) A single universal APK** (built from the .aab with
`bundletool build-apks --mode=universal`):
```
src/test/resources/app/app.apk
```

Then install it once:
```bash
scripts/install-app.sh            # first connected device
scripts/install-app.sh <serial>   # a specific device/emulator
```

Notes:
- A **base-only** APK installed alone fails with `INSTALL_FAILED_MISSING_SPLIT` —
  you need the config splits too.
- The framework does **not** reinstall the app (Appium can't install split APKs
  without the .aab). It attaches to the already-installed app via `app.package`.
- Everything here is git-ignored — never commit the binaries.
