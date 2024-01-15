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
    if [ ! -d "$cmdline_tools_dir" ]; then
        echo "cmdline-tools directory not found. Downloading..."

        # Download the cmdline-tools
        curl -o "$zip_file" "$download_url" && echo "Download complete."

        # Unzip the downloaded file
        echo "Unzipping cmdline-tools..."
        # Tools dir is needed emulator as it needs VERY precise structure - https://stackoverflow.com/questions/60992720/cannot-run-android-emulator-using-command-line-tools-on-linux-panic-broken-avd
        mkdir -p "$cmdline_tools_dir/tools"
        unzip -q "$zip_file" -d "$cmdline_tools_dir" && mv "$cmdline_tools_dir"/cmdline-tools/* "$cmdline_tools_dir/tools" && rm -r -r "$cmdline_tools_dir"/cmdline-tools && echo "Unzipping complete."


        # Clean up the downloaded zip file
        rm -r "$zip_file" && echo "Clean up complete."

        echo "cmdline-tools are ready to use."
    fi
}

delete_avd() {
  local avd_name=$1
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
  mkdir platform-tools
  sdkmanager --update
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

reset_workspace() {
  rm -r .android/; rm -r cmdline-tools/; rm -r platforms/; rm -r .knowPackages; rm -r cmdline-tools/; rm -r system-images/; rm -r licenses/; rm -r emulator/; rm -r .temp/; rm .knownPackages
}

install() {
  download_sdk_tools
  install_system_image
  delete_avd $1
  create_avd $1
  launch_avd $1
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
  echo "reset_workspace"
  echo "list_avds"
}

arg1=$2
case "$1" in
    create_avd)
        create_avd $arg1
        ;;
    delete_avd)
        delete_avd $arg1
        ;;
    download_sdk_tools)
        download_sdk_tools
        ;;
    help)
        help
        ;;
    install)
        install $arg1
        ;;
    install_system_image)
        install_system_image $arg1
        ;;
    launch_avd)
        launch_avd $arg1
        ;;
    list_avds)
        list_avds
        ;;
    reset_workspace)
        reset_workspace
        ;;
    *)
        help "error"
        ;;
esac
