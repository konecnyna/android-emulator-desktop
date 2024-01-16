package com.defkon.androidemulator.sdkmanager

import com.defkon.androidemulator.models.AvdEntity
import com.defkon.androidemulator.shellmanager.ShellCommand
import com.defkon.androidemulator.shellmanager.ShellManager
import com.defkon.androidemulator.shellmanager.ShellResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File


class SdkManager {
    private val workingDir = File(System.getProperty("user.dir")).parent
    private val shellManager = ShellManager()
    val avdRegex = Regex(
            "Name:\\s+(.*?)\\s+" +
                    "Device:\\s+(.*?)\\s+\\(.*?\\)\\s+" +
                    "Path:\\s+(.*?)\\s+" +
                    "Target:\\s+(.*?)\\s+" +
                    "Based\\s+on:.*?\\s+" +
                    "Sdcard:\\s+(.*?)\\s+",
            RegexOption.DOT_MATCHES_ALL
    )

    fun isInstalled(): Flow<AvdStateEvent> = flow  {
        emit(AvdStateEvent.Loading)
        val result = getAvdList()
        if (result is ShellResult.Success) {

            val avds = avdRegex.findAll(result.output).map { matchResult ->
                AvdEntity(
                        name = matchResult.groupValues[1],
                        device = matchResult.groupValues[2],
                        path = File(matchResult.groupValues[3]),
                        target = matchResult.groupValues[4],
                        sdCard = matchResult.groupValues[5]
                )
            }.toList()
            println(result.output)
            emit(AvdStateEvent.Success(avds))
            return@flow
        } else if (result is ShellResult.Failure) {
            emit(AvdStateEvent.Error(result.error))
        }
    }


    fun setup(console: (String) -> Unit): Flow<SetupStateEvent> = flow {
        emit(SetupStateEvent.Initializing)

        emit(SetupStateEvent.DownloadDependencies)
        runStep(SetupStateEvent.DownloadDependencies) {
            console(it)
        }

        emit(SetupStateEvent.InstallTools)
        runStep(SetupStateEvent.InstallTools)  {
            console(it)
        }

        emit(SetupStateEvent.CreateAvd)
        runStep(SetupStateEvent.CreateAvd, DEFAULT_AVD_NAME)  {
            console(it)
        }

        emit(SetupStateEvent.LaunchAvd)
        runStep(SetupStateEvent.LaunchAvd, DEFAULT_AVD_NAME)  {
            console(it)
        }
    }

    private fun getAvdList(): ShellResult {
        return runStep(SetupStateEvent.ListAvds)
    }

    private fun runStep(step: SetupStateEvent, arg1: String = "", stream: (String) -> Unit = { println(it) }): ShellResult {
        return shellManager.runCommand(ShellCommand(
            cmd = listOf(
                    "/bin/sh",
                    "-c",
                    "$workingDir/shell/sdk-util.sh ${step.shellCmd} $arg1"
            ),
            stream = { _, output -> stream(output) }
        ))
    }

    companion object {
        const val DEFAULT_AVD_NAME = "android"
    }
}

sealed class SetupStateEvent(val stepName: String, val shellCmd: String) {
    data object Initializing : SetupStateEvent(stepName = "Initializing", shellCmd = "")
    data object DownloadDependencies : SetupStateEvent(stepName = "Download Dependencies", shellCmd = "download_sdk_tools")
    data object InstallTools : SetupStateEvent(stepName = "Install Tools", shellCmd = "install_system_image")
    data object DeleteAvd : SetupStateEvent(stepName = "Delete Avd", shellCmd = "delete_avd")
    data object CreateAvd : SetupStateEvent(stepName = "Create Avd", shellCmd = "create_avd")
    data object LaunchAvd : SetupStateEvent(stepName = "Launch Avd", shellCmd = "launch_avd")
    data object ListAvds : SetupStateEvent(stepName = "List Avd", shellCmd = "list_avds")
    data class Error(val message: String) : SetupStateEvent(stepName = "Error", shellCmd = "")
    data class Console(val message: String) : SetupStateEvent(stepName = "Console", shellCmd = "")
}

sealed interface AvdStateEvent {
    data object Loading: AvdStateEvent
    data class Success(val avds: List<AvdEntity>): AvdStateEvent
    data class Error(val message: String): AvdStateEvent

}