package com.defkon.androidemulator.ui.screens.devicemgmt.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.defkon.androidemulator.models.AvdEntity

@Composable
fun DeviceManagementItem(device: AvdEntity, onLaunchClicked: (AvdEntity) -> Unit) {
    Row {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = "Device name: ${device.name}",
                fontWeight = FontWeight.Bold
            )
            Text(text = "Device type: ${device.device}")
        }

        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { onLaunchClicked(device) }) {
                Text("Launch")
            }
        }
    }
}