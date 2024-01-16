package com.defkon.androidemulator.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.defkon.androidemulator.managers.AvdManager
import com.defkon.androidemulator.models.AvdEntity
import com.defkon.androidemulator.managers.AvdStateEvent
import com.defkon.androidemulator.managers.SdkManager
import com.defkon.androidemulator.shellmanager.ShellManager
import com.defkon.androidemulator.ui.screens.devicemgmt.DeviceManagementScreen
import com.defkon.androidemulator.ui.screens.InstallScreen
import com.defkon.androidemulator.ui.screens.splash.SplashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

val sdkManager = SdkManager()
val shellManager = ShellManager()
val avdManager = AvdManager()


@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    var loading by remember { mutableStateOf(true) }
    var emulators: List<AvdEntity> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            sdkManager.isInstalled().collect {
                if (it is AvdStateEvent.Success) {
                    emulators = it.avds
                }
            }
            loading = false
        }
    }

    MaterialTheme {
        when {
            emulators.isNotEmpty() && !loading -> DeviceManagementScreen(emulators)
            loading -> SplashScreen()
            else -> InstallScreen()
        }
    }
}