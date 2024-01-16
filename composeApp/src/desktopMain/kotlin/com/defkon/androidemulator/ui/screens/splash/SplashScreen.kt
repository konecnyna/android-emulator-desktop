package com.defkon.androidemulator.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Blackbird Android",
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource("icon.png"),
            contentDescription = null,
            modifier = Modifier.size(256.dp)
        )

        CircularProgressIndicator(
            modifier = Modifier.width(32.dp),
        )
    }
}