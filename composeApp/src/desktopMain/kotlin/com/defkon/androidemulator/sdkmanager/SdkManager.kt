package com.defkon.androidemulator.sdkmanager

import com.defkon.androidemulator.shellmanager.ShellCommand
import com.defkon.androidemulator.shellmanager.ShellManager
import com.defkon.androidemulator.shellmanager.ShellResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File


class SdkManager {
    private val workingDir = File(System.getProperty("user.dir")).parent
    private val shellManager = ShellManager()


    fun setup(console: (String) -> Unit): Flow<SetupStateEvent> = flow {
        emit(SetupStateEvent.Initializing)
        val cpu = getCpuType()
        if (cpu != CpuType.x86_64) {
            emit(SetupStateEvent.Error(message = "Error: Unsupported cpu type ($cpu)"))
            return@flow
        }

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


    private fun getCpuType(): CpuType {
        val type = shellManager.runCommand(ShellCommand(
            cmd = listOf("uname", "-m"),
        ))

        return when (type) {
            is ShellResult.Failure -> CpuType.unknown
            is ShellResult.Success -> enumValueOf<CpuType>(type.output)
        }
    }

    private suspend fun runStep(step: SetupStateEvent, arg1: String = "", stream: (String) -> Unit = {}) {
        shellManager.runCommand(ShellCommand(
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
    data class Error(val message: String) : SetupStateEvent(stepName = "Error", shellCmd = "")
    data class Console(val message: String) : SetupStateEvent(stepName = "Console", shellCmd = "")
}


enum class CpuType {
    x86_64,
    arm64,
    unknown
}