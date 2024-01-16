package com.defkon.androidemulator.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.defkon.androidemulator.managers.AvdManager
import com.defkon.androidemulator.managers.AvdStateEvent
import com.defkon.androidemulator.managers.SdkManager
import com.defkon.androidemulator.models.AvdEntity
import com.defkon.androidemulator.shellmanager.ShellManager
import com.defkon.androidemulator.ui.screens.InstallScreen
import com.defkon.androidemulator.ui.screens.devicemgmt.DeviceManagementScreen
import com.defkon.androidemulator.ui.screens.splash.SplashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val sdkManager = SdkManager()
val shellManager = ShellManager()
val avdManager = AvdManager()


@Composable
fun App() {
    var loading by remember { mutableStateOf(true) }
    var refreshDevices by remember { mutableStateOf(true) }
    var emulators: List<AvdEntity> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(refreshDevices) {
        withContext(Dispatchers.IO) {
            sdkManager.isInstalled().collect {
                if (it is AvdStateEvent.Success) {
                    emulators = it.avds
                }
            }
            loading = false
            refreshDevices = false
        }
    }

    MaterialTheme {
        when {
            emulators.isNotEmpty() && !loading -> DeviceManagementScreen(emulators)
            loading -> SplashScreen()
            else -> InstallScreen { refreshDevices = true }
        }
    }
}