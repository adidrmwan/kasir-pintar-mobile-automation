#!/usr/bin/env bash
# ============================================================
#  CI entry point run INSIDE the Android emulator step.
#  Starts Appium and runs the suite. The app is fresh-installed and the device
#  prepared automatically by the @BeforeAll hook (scripts/install-app.sh +
#  scripts/prepare-device.sh), so this script only starts Appium + mvn.
#  Env expected: TAGS, LOGIN_EMAIL, LOGIN_PASSWORD
# ============================================================
set -euo pipefail

TAGS="${TAGS:-@barang}"

echo "▶ Starting Appium..."
appium --log-timestamp --log appium.log &
npx wait-on -t 60000 http://127.0.0.1:4723/status

echo "▶ Running tests (tags: $TAGS)..."
mvn test -Denv=dev \
  -Dcucumber.filter.tags="$TAGS" \
  -Dlogin.email="${LOGIN_EMAIL:-}" \
  -Dlogin.password="${LOGIN_PASSWORD:-}"
