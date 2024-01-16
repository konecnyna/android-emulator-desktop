package com.defkon.androidemulator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.defkon.androidemulator.sdkmanager.AvdStateEvent
import com.defkon.androidemulator.sdkmanager.SdkManager
import com.defkon.androidemulator.sdkmanager.SetupStateEvent
import com.defkon.androidemulator.shellmanager.ShellResult
import com.defkon.androidemulator.ui.screens.InstallScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

val sdkManager = SdkManager()

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    var emulators  by remember { mutableStateOf("None") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            sdkManager.isInstalled().collect {
                if (it is AvdStateEvent.Success) {
                    emulators = it.avds.map { avd -> avd.name }.joinToString { name -> "$name" }
                }
            }
        }
    }

    MaterialTheme {
        if (emulators.isNotEmpty()) {
            Text(text = emulators)
        } else {
            InstallScreen()
        }
    }
}