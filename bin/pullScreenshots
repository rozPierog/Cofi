#!/bin/bash

screenshotsNames=("1_en-US" "2_en-US" "3_en-US" "4_en-US" "5_en-US" "6_en-US")
screenshotFormat=".png"
pathToPhoneMetadata="fastlane/metadata/android/en-US/images/phoneScreenshots/"
pathToTabletMetadata="fastlane/metadata/android/en-US/images/tenInchScreenshots/"
pathToWearMetadata="fastlane/metadata/android/en-US/images/wearScreenshots/"
pathToScreenshots="sdcard/Pictures/"

devicesOutput=$(adb devices)

SAVE_IFS=$IFS                  # Save current IFS
IFS=$'\n'                      # Change IFS to new line
devicesOutput=($devicesOutput) # split to array
IFS=$SAVE_IFS                  # Restore IFS

mkdir -p "${pathToPhoneMetadata}"
mkdir -p "${pathToTabletMetadata}"
mkdir -p "${pathToWearMetadata}"

[[ "$PWD" =~ bin ]] && echo "❌ Run from project root ❌" && exit 2

for i in "${screenshotsNames[@]}"; do
    for element in "${devicesOutput[@]}"; do
        case "$element" in
        *device)
            deviceNames=($element)
            deviceName="${deviceNames[0]}"
            newestScreenshot=$(adb -s "${deviceName}" shell ls -t $pathToScreenshots | grep "$i" | head -1)
            humanEmuName=$(adb -s "${deviceName}" emu avd name)
            if [[ ${humanEmuName} =~ Tab ]]; then
                adb -s "${deviceName}" pull "$pathToScreenshots$newestScreenshot" "$pathToTabletMetadata$i$screenshotFormat" && echo "pulled tablet screenshots from ${humanEmuName} ✅"
            elif [[ ${humanEmuName} =~ Wear ]]; then
                adb -s "${deviceName}" pull "$pathToScreenshots$newestScreenshot" "$pathToWearMetadata$i$screenshotFormat" && echo "pulled wear screenshots from ${humanEmuName} ✅"
            else
                adb -s "${deviceName}" pull "$pathToScreenshots$newestScreenshot" "$pathToPhoneMetadata$i$screenshotFormat" && echo "pulled  screenshots from ${humanEmuName} ✅"
            fi
            ;;
        esac
    done
done
