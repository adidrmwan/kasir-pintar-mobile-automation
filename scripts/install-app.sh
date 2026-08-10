#!/usr/bin/env bash
# ============================================================
#  Fresh-install the app under test onto a connected device/emulator.
#  Uninstalls any existing copy first, so every run starts from a clean app.
#
#  Teammates: just drop ONE .apk file into src/test/resources/app/
#  (must be a UNIVERSAL/standalone APK). Then run this script (or let the
#  @BeforeAll hook run it automatically at the start of the test run).
#
#  A split "base" APK alone fails with INSTALL_FAILED_MISSING_SPLIT — in that
#  case place the config splits in src/test/resources/app/_splits/ instead.
#
#  Usage: scripts/install-app.sh [deviceSerial]
# ============================================================
set -euo pipefail

SERIAL="${1:-}"
PKG="${APP_PACKAGE:-org.owline.kasirpintarpro}"
ADB="adb"
[ -n "$SERIAL" ] && ADB="adb -s $SERIAL"

APP_DIR="src/test/resources/app"

$ADB wait-for-device

echo "▶ Uninstalling existing $PKG (if any)..."
$ADB uninstall "$PKG" >/dev/null 2>&1 || true

# 1) Preferred: a single .apk placed directly in the folder (universal APK).
if ls "$APP_DIR"/*.apk >/dev/null 2>&1; then
  APK="$(ls "$APP_DIR"/*.apk | head -1)"
  echo "▶ Installing $APK ..."
  $ADB install -g "$APK"

# 2) Fallback: split APKs under _splits/.
elif ls "$APP_DIR"/_splits/*.apk >/dev/null 2>&1; then
  echo "▶ Installing split APKs from $APP_DIR/_splits ..."
  $ADB install-multiple -g "$APP_DIR"/_splits/*.apk

else
  echo "✖ No app found. Put a universal .apk in $APP_DIR/ (or split APKs in $APP_DIR/_splits/)." >&2
  exit 1
fi

echo "✔ App freshly installed."
