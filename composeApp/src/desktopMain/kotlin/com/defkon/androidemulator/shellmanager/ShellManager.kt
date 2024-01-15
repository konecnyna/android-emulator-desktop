package com.defkon.androidemulator.shellmanager

import java.io.BufferedWriter
import java.io.OutputStreamWriter

class ShellManager {
    //private val runtime: Runtime = Runtime.getRuntime()

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