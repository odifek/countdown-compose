package com.example.countdownchallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.*

data class Time(val minutes: Int, val seconds: Int) {
    operator fun dec(): Time {
        if (minutes == 0 && seconds == 0) return this
        return Calendar.getInstance().apply {
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, seconds)
            add(Calendar.SECOND, -1)
        }.let { Time(it.get(Calendar.MINUTE), it.get(Calendar.SECOND)) }
    }

    fun zero(): Boolean = minutes == 0 && seconds == 0
}

@Composable
fun CountDownScreen() {

    val initialTime = Time(1, 45)
    var time by remember { mutableStateOf(initialTime)}
    
    LaunchedEffect(key1 = time.zero()) {
        while (true) {
            delay(1000)
            time = --time
        }
    }
    CountDown(time = time)
}
@Composable
fun CountDown(time: Time) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberWheel(range = (0..5), selected = time.minutes / 10)
        NumberWheel(range = (0..9), selected = time.minutes % 10)
        NumberWheel(range = (0..5), selected = time.seconds / 10)
        NumberWheel(range = (0..9), selected = time.seconds % 10)
    }
}

@Composable
private fun NumberWheel(range: IntRange, selected: Int) {
    val mid = (range.last - range.first) / 2f
    val offset = 32.dp * (mid - selected)
    Column(
        modifier = Modifier
            .offset(y = offset)
            .clip(RoundedCornerShape(25))
    ) {
        range.forEach { n ->
            val background =
                if (n == selected) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(background),
                contentAlignment = Alignment.Center
            ) {
                Text(text = n.toString(), fontSize = 20.sp, color = MaterialTheme.colors.onPrimary)
            }
        }
    }
}

@Preview(name = "Count Down preview", device = Devices.PIXEL_4)
@Composable
fun CountDownPreview() {
    CountDownScreen()
}