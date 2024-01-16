package com.defkon.androidemulator.ui.screens.devicemgmt

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Blackbird Android",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
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