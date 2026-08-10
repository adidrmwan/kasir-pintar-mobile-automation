#!/usr/bin/env bash
# ============================================================
#  Pre-grant runtime permissions & silence OS pop-ups for the
#  app under test, so tests don't waste time on permission dialogs.
#
#  Usage:
#    scripts/prepare-device.sh [appPackage] [deviceSerial]
#  Defaults:
#    appPackage  = org.owline.kasirpintarpro
#    deviceSerial= (first connected device)
# ============================================================
set -u

PKG="${1:-org.owline.kasirpintarpro}"
SERIAL="${2:-}"
ADB="adb"
[ -n "$SERIAL" ] && ADB="adb -s $SERIAL"

echo "▶ Preparing device for $PKG"

# Dangerous (runtime) permissions this app may request.
PERMS=(
  android.permission.CAMERA
  android.permission.READ_EXTERNAL_STORAGE
  android.permission.WRITE_EXTERNAL_STORAGE
  android.permission.READ_CONTACTS
  android.permission.ACCESS_FINE_LOCATION
  android.permission.ACCESS_COARSE_LOCATION
  android.permission.READ_PHONE_STATE
  android.permission.POST_NOTIFICATIONS
  android.permission.BLUETOOTH_SCAN
  android.permission.BLUETOOTH_CONNECT
  android.permission.BLUETOOTH_ADVERTISE
)

for p in "${PERMS[@]}"; do
  if $ADB shell pm grant "$PKG" "$p" >/dev/null 2>&1; then
    echo "  ✓ granted $p"
  else
    echo "  – skip $p (not grantable on this OS/app)"
  fi
done

# Disable the OS autofill service so Google's "Save password?" prompt never
# appears during login.
if $ADB shell settings put secure autofill_service null >/dev/null 2>&1; then
  echo "  ✓ autofill service disabled (no 'Save password' prompt)"
fi

# Keep the screen awake during the run (avoids the device dozing/locking mid-test).
$ADB shell svc power stayon true >/dev/null 2>&1 && echo "  ✓ stay-awake while plugged in"
$ADB shell settings put system screen_off_timeout 1800000 >/dev/null 2>&1 && echo "  ✓ screen timeout 30m"
$ADB shell input keyevent KEYCODE_WAKEUP >/dev/null 2>&1
$ADB shell wm dismiss-keyguard >/dev/null 2>&1 && echo "  ✓ keyguard dismissed (non-secure locks only)"

echo "✔ Device ready."
echo "  NOTE: if the device has a PIN/pattern/secure screen lock, disable it on"
echo "        the device (Settings > Security > Screen lock > None). Appium cannot"
echo "        reliably pass a secure MIUI lockscreen."
