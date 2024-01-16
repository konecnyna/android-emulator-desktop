package com.defkon.androidemulator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.defkon.androidemulator.sdkmanager.SetupStateEvent
import com.defkon.androidemulator.ui.sdkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun InstallScreen() {
    var installing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Create Android Emulator") }
    var console by remember { mutableStateOf("") }


    Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Image(
                painter = painterResource("icon.png"),
                contentDescription = null,
                modifier = Modifier.size(256.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = status)
        AnimatedVisibility(visible = console.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = console)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
                onClick = { installing = true },
                enabled = !installing
        ) {
            Text("Install")
        }
    }

    if (installing) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                sdkManager.setup {
                    console = it
                }.collect {
                    when (it) {
                        is SetupStateEvent.Error -> {
                            status = "Error!"
                            console = it.message
                        }
                        else -> {
                            status = it.stepName
                        }
                    }
                }
            }
        }
    }
}