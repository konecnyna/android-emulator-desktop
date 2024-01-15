#!/bin/bash
export ANDROID_HOME=`pwd`

export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/tools/bin/:$PATH


export ANDROID_SDK_ROOT=`pwd`
export ANDROID_AVD_HOME="$ANDROID_HOME/.android/avd"
export ANDROID_EMULATOR_HOME="$ANDROID_HOME/.android"

download_sdk_tools() {
    local cmdline_tools_dir="cmdline-tools"
    local download_url="https://dl.google.com/android/repository/commandlinetools-mac-11076708_latest.zip"
    local zip_file="commandlinetools.zip"

    echo "Checking for cmdline-tools directory..."

    # Check if cmdline-tools directory exists
    if [ -d "$cmdline_tools_dir" ]; then
        echo "cmdline-tools directory already exists."
    else
        echo "cmdline-tools directory not found. Downloading..."

        # Download the cmdline-tools
        curl -o "$zip_file" "$download_url" && echo "Download complete."

        # Unzip the downloaded file
        echo "Unzipping cmdline-tools..."
        unzip -q "$zip_file" -d "$cmdline_tools_dir" && echo "Unzipping complete."

        # Clean up the downloaded zip file
        rm "$zip_file" && echo "Clean up complete."

        echo "cmdline-tools are ready to use."
    fi
}

delete_avd() {
  avd_name=$1
  if [ -z "$avd_name" ]; then
    echo "Please provide avd_name argument"
  else
    # List existing AVDs
    existing_avds=$(avdmanager list avd | grep -Eo "Name: [^[:space:]]+")

    # Check if the specified AVD exists
    if [[ "$existing_avds" == *"$avd_name"* ]]; then
      # Delete the AVD
      avdmanager delete avd -n "$avd_name"
      echo "Deleted AVD: $avd_name"
    else
      echo "AVD $avd_name does not exist"
    fi
  fi
}

install_system_image() {
  mkdir -p "$ANDROID_HOME/.android/avd"
  # Emulator launch fails without empty dir
  mkdir platforms
  yes|sdkmanager --verbose --sdk_root="$ANDROID_HOME" "system-images;android-30;google_apis_playstore;x86_64"
}

create_avd() {
  echo "no"|avdmanager --verbose create avd --name "$1" --package 'system-images;android-30;google_apis_playstore;x86_64' --device pixel_7_pro
}

launch_avd() {
  emulator -avd $1
}

list_avds() {
#  avdmanager list device
  avdmanager list avd
}

help() {
  if [[ "$1" == "error" ]]; then
  echo "Error: No matching keyword."
  echo ""
  fi
  echo "Commands:"
  echo "install_system_image"
  echo "delete_avd"
  echo "create_avd"
  echo "launch_avd"
  echo "download_sdk_tools"
  echo "list_avds"
}

arg1=$2
case "$1" in
    download_sdk_tools)
        download_sdk_tools
        ;;
    install_system_image)
        install_system_image $arg1
        ;;
    delete_avd)
        delete_avd $arg1
        ;;
    create_avd)
        create_avd $arg1
        ;;
    launch_avd)
        launch_avd $arg1
        ;;
    list_avds)
        list_avds
        ;;
    help)
        help
        ;;
    *)
      help "error"
        ;;
esac
