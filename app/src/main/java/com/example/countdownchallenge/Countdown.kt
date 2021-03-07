package com.example.countdownchallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "Count Down preview", device = Devices.PIXEL_4)
@Composable
fun CountDown() {
    Column(modifier = Modifier.fillMaxSize()) {

        (0..9).forEach { n ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colors.primary)
                    .border(1.dp, MaterialTheme.colors.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = n.toString(), color = MaterialTheme.colors.onPrimary)
            }
        }
    }
}