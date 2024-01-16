package com.defkon.androidemulator.shellmanager

import java.io.BufferedWriter

data class ShellCommand(
    val cmd: List<String>,
    val stream: (BufferedWriter, String) -> Unit = { _, _ -> },
    val environmentVariables: List<String> = listOf()
)