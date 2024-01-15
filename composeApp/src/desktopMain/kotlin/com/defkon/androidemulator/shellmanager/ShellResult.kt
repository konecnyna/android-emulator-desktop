package com.defkon.androidemulator.shellmanager

sealed class ShellResult(open val exitValue: Int) {
    data class Success(
        val output: String,
        override val exitValue: Int,
    ): ShellResult(exitValue)

    data class Failure(
        val error: String,
        override val exitValue: Int,
    ): ShellResult(exitValue)
}