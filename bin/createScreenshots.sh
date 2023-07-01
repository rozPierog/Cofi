#!/bin/bash
set -e
set -x

while read -r line; do
    osascript -e 'tell application "Terminal" to do script "$ANDROID_HOME/emulator/emulator -avd '$line'"'
done < bin/emulatorNames

function waitForEmulator {
    boot_animation=""
    fail_counter=0
    timeout_in_sec=360

    until [[ "$boot_animation" == "1" ]]; do
      boot_animation=$(adb -s "$1" -e shell getprop sys.boot_completed 2>&1 &)
#      if [[ "$boot_animation" != "1" ]]; then
#        echo "Waiting for emulator to start"
#        if [[ $fail_counter -gt timeout_in_sec ]]; then
#          echo "Timeout ($timeout_in_sec seconds) reached; failed to start emulator"
#          exit 1
#        fi
#      fi
      sleep 1
    done

    echo "Emulator is ready"
}

devicesOutput=$(adb devices)

SAVEIFS=$IFS                   # Save current IFS
IFS=$'\n'                      # Change IFS to new line
devicesOutput=($devicesOutput) # split to array
IFS=$SAVEIFS                   # Restore IFS

for element in "${devicesOutput[@]}"; do
    case "$element" in
        *device)
            deviceNames=($element)
            deviceName="${deviceNames[0]}"
            waitForEmulator "$deviceName"
    esac
done

./gradlew -Pandroid.testInstrumentationRunnerArguments.class=com.omelan.cofi.ScreenshotCreator connectedFullDebugAndroidTest

./bin/pullScreenshots.sh
