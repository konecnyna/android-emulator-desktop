package com.defkon.androidemulator.shellmanager

import java.io.BufferedWriter
import java.io.OutputStreamWriter

class ShellManager {
    //private val runtime: Runtime = Runtime.getRuntime()

    fun runCommand(shellCommand: ShellCommand): ShellResult {
        return try {
            println("Running cmd: ${shellCommand.cmd}")

            val processBuilder = ProcessBuilder(shellCommand.cmd)
            val process = processBuilder
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

            val writer = BufferedWriter(OutputStreamWriter(process.outputStream))
            val bufferedReader = process.inputStream.bufferedReader()

            val output = bufferedReader.use {
                val text = it.readText()
                shellCommand.stream(writer, text.trim())
                text
            }

            val error = process.errorStream.bufferedReader().use {
                val errorText = it.readText().trim()
                println(errorText)
                errorText
            }

            val exitValue = process.waitFor()
            if (error.isNotEmpty()) {
                ShellResult.Failure(
                    error = error.trim(),
                    exitValue = exitValue
                )
            } else {
                ShellResult.Success(
                    output = output.trim(),
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