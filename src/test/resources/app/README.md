# App binary folder

Place the Kasir Pintar Pro APK here as:

    kasir-pintar-pro.apk

The framework will auto-install it if `app.path` in
`src/test/resources/config/config.properties` points to a file that exists.

If the app is already installed on the device/emulator, leave `app.path`
pointing at a non-existent file and set `app.package` / `app.activity` instead.

The `.apk` is intentionally git-ignored — do not commit the binary.
