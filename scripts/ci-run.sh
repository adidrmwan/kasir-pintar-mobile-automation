#!/usr/bin/env bash
# ============================================================
#  CI entry point run INSIDE the Android emulator step.
#  Installs the app (split or universal), prepares the device,
#  starts Appium, then runs the test suite.
#  Env expected: APP_PACKAGE, TAGS, LOGIN_EMAIL, LOGIN_PASSWORD
# ============================================================
set -euo pipefail

APP_PACKAGE="${APP_PACKAGE:-org.owline.kasirpintarpro}"
TAGS="${TAGS:-@barang}"

echo "▶ Installing app..."
if [ -d src/test/resources/app/_splits ]; then
  adb install-multiple -r -g src/test/resources/app/_splits/*.apk
else
  adb install -r -g src/test/resources/app/universal.apk
fi

echo "▶ Preparing device..."
bash scripts/prepare-device.sh "$APP_PACKAGE" || true

echo "▶ Starting Appium..."
appium --log-timestamp --log appium.log &
npx wait-on -t 60000 http://127.0.0.1:4723/status

echo "▶ Running tests (tags: $TAGS)..."
mvn test -Denv=dev \
  -Dcucumber.filter.tags="$TAGS" \
  -Dlogin.email="${LOGIN_EMAIL:-}" \
  -Dlogin.password="${LOGIN_PASSWORD:-}"
