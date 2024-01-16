package com.defkon.androidemulator.ui.screens.devicemgmt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.defkon.androidemulator.models.AvdEntity
import com.defkon.androidemulator.ui.avdManager
import com.defkon.androidemulator.ui.screens.devicemgmt.components.DeviceManagementItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DeviceManagementScreen(emulators: List<AvdEntity>) {
    // fix.
    var selectedAvd: AvdEntity? by remember { mutableStateOf(null) }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
    ) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                emulators.map {
                    DeviceManagementItem(it) {
                        selectedAvd = it
                    }
                }
            }
        }

        selectedAvd?.let {
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    avdManager.launchAvd(it)
                }
            }
        }
    }
}