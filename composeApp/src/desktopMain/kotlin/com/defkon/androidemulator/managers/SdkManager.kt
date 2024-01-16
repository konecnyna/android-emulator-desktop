package com.defkon.androidemulator.managers

import com.defkon.androidemulator.ui.avdManager
import com.defkon.androidemulator.ui.shellManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SdkManager {

    fun isInstalled(): Flow<AvdStateEvent> = avdManager.getAvds()

    fun setup(console: (String) -> Unit): Flow<SetupStateEvent> = flow {
        emit(SetupStateEvent.Initializing)

        emit(SetupStateEvent.DownloadDependencies)
        shellManager.runSdkUtilShellCommand(
            SetupStateEvent.DownloadDependencies
        ) {
            console(it)
        }

        emit(SetupStateEvent.InstallTools)
        shellManager.runSdkUtilShellCommand(
            SetupStateEvent.InstallTools
        ) {
            val regex = """(\d+%) (\w+)""".toRegex()
            val matchResult = regex.find(it)
            if (matchResult != null) {
                val (percentage, word) = matchResult.destructured
                console("$word - $percentage")
            } else {
                console(it)
            }
        }

        emit(SetupStateEvent.CreateAvd)
        shellManager.runSdkUtilShellCommand(
            SetupStateEvent.CreateAvd, DEFAULT_AVD_NAME
        ) {
            console(it)
        }

        emit(SetupStateEvent.Finished)
    }

    companion object {
        const val DEFAULT_AVD_NAME = "android"
    }
}

sealed class SetupStateEvent(val stepName: String, val shellCmd: String) {
    data object Initializing : SetupStateEvent(stepName = "Initializing", shellCmd = "")
    data object DownloadDependencies :
        SetupStateEvent(stepName = "Download Dependencies", shellCmd = "download_sdk_tools")

    data object InstallTools : SetupStateEvent(stepName = "Install Tools", shellCmd = "install_system_image")
    data object DeleteAvd : SetupStateEvent(stepName = "Delete Avd", shellCmd = "delete_avd")
    data object CreateAvd : SetupStateEvent(stepName = "Create Avd", shellCmd = "create_avd")
    data object LaunchAvd : SetupStateEvent(stepName = "Launch Avd", shellCmd = "launch_avd")
    data object ListAvds : SetupStateEvent(stepName = "List Avd", shellCmd = "list_avds")
    data object Finished : SetupStateEvent(stepName = "Finish", shellCmd = "")
    data class Error(val message: String) : SetupStateEvent(stepName = "Error", shellCmd = "")
    data class Console(val message: String) : SetupStateEvent(stepName = "Console", shellCmd = "")
}