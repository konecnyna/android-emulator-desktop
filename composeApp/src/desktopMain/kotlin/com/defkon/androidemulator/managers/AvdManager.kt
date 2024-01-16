package com.defkon.androidemulator.managers

import com.defkon.androidemulator.models.AvdEntity
import com.defkon.androidemulator.shellmanager.ShellManager
import com.defkon.androidemulator.shellmanager.ShellResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class AvdManager {
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

    fun launchAvd(avd: AvdEntity){
        shellManager.runSdkUtilShellCommand(SetupStateEvent.LaunchAvd, avd.name)
    }

    fun getAvds(): Flow<AvdStateEvent> = flow {
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
            emit(AvdStateEvent.Success(avds))
            return@flow
        } else if (result is ShellResult.Failure) {
            emit(AvdStateEvent.Error(result.error))
        }
    }

    private fun getAvdList(): ShellResult {
        return shellManager.runSdkUtilShellCommand(SetupStateEvent.ListAvds)
    }
}


sealed interface AvdStateEvent {
    data object Loading : AvdStateEvent
    data class Success(val avds: List<AvdEntity>) : AvdStateEvent
    data class Error(val message: String) : AvdStateEvent

}