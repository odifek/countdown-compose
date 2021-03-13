package com.example.countdownchallenge

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

private const val TAG = "CountDown"

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
    var time by remember { mutableStateOf(initialTime) }

//    LaunchedEffect(key1 = time.zero()) {
//        while (true) {
//            delay(1000)
//            time = --time
//        }
//    }
    CountDown(time = time)
}

@Composable
fun CountDown(time: Time) {
    var changeTime by remember { mutableStateOf(time) }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberWheel(range = (0..5), selected = changeTime.minutes / 10) {
            changeTime = changeTime.copy(minutes = it * 10 + changeTime.minutes % 10)
        }
        NumberWheel(range = (0..9), selected = changeTime.minutes % 10) {
            changeTime = changeTime.copy(minutes = changeTime.minutes / 10 * 10 + it)
        }
        NumberWheel(range = (0..5), selected = changeTime.seconds / 10) {
            changeTime = changeTime.copy(seconds = it * 10 + changeTime.seconds % 10)
        }
        NumberWheel(range = (0..9), selected = changeTime.seconds % 10) {
            changeTime =
                changeTime.copy(seconds = changeTime.seconds / 10 * 10 + it) // Replace the unit
        }
    }
}

@Composable
private fun NumberWheel(range: IntRange, selected: Int, onSelectionUpdate: (Int) -> Unit) {

    val boxSizeDp = 40.dp
    val boxSize = with(LocalDensity.current) { boxSizeDp.toPx() }
    val mid = (range.last - range.first) / 2f
    val offset = boxSize * (mid - selected)
    Log.d(TAG, "NumberWheel: offset: $offset")

    var offsetY by remember { mutableStateOf(0f) }
    val dragState = rememberDraggableState(onDelta = { delta ->
        val newOffset = offsetY + delta
        if (abs(newOffset) >= boxSize) {
            offsetY = 0f // Reset the offset before recomposing.
            val maxOffset = abs(boxSize * mid)
            val totalOffset = (newOffset + offset).coerceIn(-maxOffset, maxOffset)
            val selection = (mid - totalOffset / boxSize).toInt()
            onSelectionUpdate(selection)
        } else {
            offsetY = newOffset
        }
    })

    Column(
        modifier = Modifier
            .offset { IntOffset(x = 0, y = offset.roundToInt()) }
            .clip(RoundedCornerShape(25))
            .draggable(dragState, orientation = Orientation.Vertical)
    ) {
        range.forEach { n ->
            val background =
                if (n == selected) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
            Box(
                modifier = Modifier
                    .size(boxSizeDp)
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