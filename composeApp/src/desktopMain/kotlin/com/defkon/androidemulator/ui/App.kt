package com.defkon.androidemulator.ui

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
import com.defkon.androidemulator.sdkmanager.SdkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

val sdkManager = SdkManager()

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var installing by remember { mutableStateOf(false) }
        var status by remember { mutableStateOf("Create Android Emulator") }

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
            Text(text = "Create Android Emulator")
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
                    sdkManager.setup().collect {
                        status = it.stepName
                    }
                }
            }
        }
    }
}

@Composable
fun onInstallClick() {
    LaunchedEffect(Unit) {
        sdkManager.setup().collect {
            println(it)
        }
    }
}