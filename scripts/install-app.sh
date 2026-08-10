#!/usr/bin/env bash
# ============================================================
#  Install the app under test onto a connected device/emulator.
#
#  Put the app in src/test/resources/app/ as EITHER:
#    - split APKs in _splits/  (base.apk + split_config.*.apk), OR
#    - a single universal .apk directly in the folder.
#
#  Usage: scripts/install-app.sh [deviceSerial]
# ============================================================
set -euo pipefail

SERIAL="${1:-}"
ADB="adb"
[ -n "$SERIAL" ] && ADB="adb -s $SERIAL"

APP_DIR="src/test/resources/app"

$ADB wait-for-device

if ls "$APP_DIR"/_splits/*.apk >/dev/null 2>&1; then
  echo "▶ Installing split APKs from $APP_DIR/_splits ..."
  $ADB install-multiple -r -g "$APP_DIR"/_splits/*.apk
elif ls "$APP_DIR"/*.apk >/dev/null 2>&1; then
  echo "▶ Installing APK from $APP_DIR ..."
  # Single APK (universal). A base-only split APK will fail (MISSING_SPLIT).
  $ADB install -r -g "$(ls "$APP_DIR"/*.apk | head -1)"
else
  echo "✖ No app found. Put split APKs in $APP_DIR/_splits/ or a .apk in $APP_DIR/" >&2
  exit 1
fi

echo "✔ App installed."
