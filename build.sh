#!/bin/sh
#
# This is a script to ease pushing plugins to your phone.
# It will:
#   - Build the plugin
#   - Push it to your Aliucord plugins folder
#   - Force close Aliucord
#   - Launch Aliucord
#
# Usage:
#   - Build a specific plugin:
#       ./build.sh PLUGIN_FOLDER
#   - Build all plugins:
#       ./build.sh "*"

set -e

(
  die() {
    echo "$@"
    exit 1
  } >&2

  [ "$1" != "*" ] && [ ! -d "$1" ] && die "Usage: $0 [PLUGIN_FOLDER]"
  command -v d8 >/dev/null || die "Please add the Android SDK build tools to your path (Android/Sdk/build-tools/SOME_VERSION)"

  cd ../buildtool
  echo "Building plugin..."
  ./buildtool -p "$1"

  cd ../buildsPlugins
  [ "$(adb devices | wc -l)" = "2" ] && die "No android device found. Connect to your phone via adb first"

  echo "Pushing plugin zip to device..."
  if [ "$1" = "*" ]; then
    adb push -- *.zip /storage/emulated/0/Aliucord/plugins
  else
    adb push "$1.zip" /storage/emulated/0/Aliucord/plugins
  fi

  echo "Force stopping Aliucord..."
  adb shell am force-stop com.aliucord

  echo "Launching Aliucord..."
  adb shell monkey -p com.aliucord -c android.intent.category.LAUNCHER 1
)
