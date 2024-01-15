package com.defkon.androidemulator.sdkmanager

import com.defkon.androidemulator.shellmanager.ShellCommand
import com.defkon.androidemulator.shellmanager.ShellManager
import com.defkon.androidemulator.shellmanager.ShellResult
import java.io.File

// https://gist.github.com/iliiliiliili/61917ce2ed127104773712fcf54731c1
// https://developer.android.com/studio/index.html#command-line-tools-only
// https://dl.google.com/android/repository/platform-tools-latest-darwin.zip
// dl: https://dl.google.com/android/repository/commandlinetools-mac-11076708_latest.zip
class SdkManager {
    private val workingDir = File(System.getProperty("user.dir")).parent
    private val sdkRoot = "$workingDir/bin/sdk"
    private val shellManager = ShellManager()
    val cmdLineToolsFolder = "$sdkRoot/cmdline-tools/latest/bin"
    val sdkManagerBin = "$cmdLineToolsFolder/sdkmanager"
    val avdmanagerBin = "$cmdLineToolsFolder/avdmanager"

    fun setup() {
        shellManager.runCommand(ShellCommand(
            cmd = listOf(
                "/bin/sh",
                "-c",
                "$workingDir/bin/sdk-util.sh"
            ),
            stream = { writer, output ->

                println(output)
                writer.write("no\n")
                writer.flush()
                writer.close()
            }
        ))
//        val cpu = getCpuType()
//        if (cpu == CpuType.unknown) {
//            println("Error: Unknown cpu type")
//            return
//        }
//
//        setupSdkManager(cpu)
//        setupEmulator()
    }

    private fun setupEmulator() {
        val test = shellManager.runCommand(ShellCommand(
            cmd = listOf(
                "/bin/sh",
                "-c",
                "$avdmanagerBin create avd --name \"foo\" --package \"system-images;android-33;google_apis_playstore;x86_64\""
//                "$avdmanagerBin create avd --name \"foo\"",
//                "create avd --name \"foo\"",
//                "avd",
//                "--name",
//                "bb"
//                "--package system-images;android-33;google_apis_playstore;x86_64'",
            ),
            stream = { writer, output ->

                println(output)
                writer.write("no\n")
                writer.flush()
                writer.close()
            }
        ))
        println("!!!!!!!")
        println(test)
    }

    private fun getCpuType(): CpuType {
        val type = shellManager.runCommand(ShellCommand(
            cmd = listOf("uname", "-m"),
//            stream = { println(it) }
        ))

        return when (type) {
            is ShellResult.Failure -> CpuType.unknown
            is ShellResult.Success -> enumValueOf<CpuType>(type.output)
        }
    }

    private fun setupSdkManager(cpu: CpuType) {
        val result = shellManager.runCommand(ShellCommand(
            cmd = listOf(sdkManagerBin, "platform-tools"),
//            stream = { println(it) }
        ))
        println(result)

        val imageCpuType = when (cpu) {
            CpuType.x86_64 -> "x86_64"
            CpuType.arm64 -> "arm64-v8a"
            CpuType.unknown -> {} //fix with sealed class
        }

        shellManager.runCommand(ShellCommand(
            cmd = listOf(
                sdkManagerBin,
                "--verbose",
                "--sdk_root=/Users/defkon/github/android-emulator/bin/sdk",
                "system-images;android-33;google_apis_playstore;$imageCpuType"
            ),
//            stream = { println(it) }
        ))
    }
}

sealed interface SetupStateEvent {
    data object Initializing : SetupStateEvent
    data object UpdateSdk : SetupStateEvent
}


enum class CpuType {
    x86_64,
    arm64,
    unknown
}