package com.defkon.androidemulator.shellmanager

import com.defkon.androidemulator.managers.SetupStateEvent
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter

class ShellManager {
    private val workingDir = File(System.getProperty("user.dir")).parent

    fun runSdkUtilShellCommand(
        step: SetupStateEvent,
        arg1: String = "",
        stream: (String) -> Unit = { }
    ): ShellResult {

        return runCommand(ShellCommand(
            cmd = listOf(
                "/bin/sh",
                "-c",
                "$workingDir/shell/sdk-util.sh ${step.shellCmd} $arg1"
            ),
            stream = { _, output ->
                println(output)
                stream(output)
            }
        ))
    }

    fun runCommand(shellCommand: ShellCommand): ShellResult {
        return try {
            println("Running cmd: ${shellCommand.cmd}")

            val processBuilder = ProcessBuilder(shellCommand.cmd)
            val process = processBuilder.start()

            val writer = BufferedWriter(OutputStreamWriter(process.outputStream))
            val bufferedReader = process.inputStream.bufferedReader()

            val outputBuilder = StringBuilder()
            val errorBuilder = StringBuilder()

            val readThread = Thread {
                bufferedReader.useLines { lines ->
                    lines.forEach {
                        outputBuilder.append(it).append("\n")
                        shellCommand.stream(writer, it)
                    }
                }
            }

            val errorThread = Thread {
                process.errorStream.bufferedReader().useLines { lines ->
                    lines.forEach {
                        errorBuilder.append(it).append("\n")
                        println(it)
                    }
                }
            }

            readThread.start()
            errorThread.start()
            readThread.join()
            errorThread.join()

            val exitValue = process.waitFor()
            val output = outputBuilder.toString().trim()
            val error = errorBuilder.toString().trim()

            if (error.isNotEmpty()) {
                ShellResult.Failure(
                    error = error,
                    exitValue = exitValue
                )
            } else {
                ShellResult.Success(
                    output = output,
                    exitValue = exitValue
                )
            }
        } catch (exception: Exception) {
            ShellResult.Failure(
                error = exception.message ?: "No message provided",
                exitValue = -1
            )
        }
    }
}