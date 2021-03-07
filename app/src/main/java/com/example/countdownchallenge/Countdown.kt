package com.example.countdownchallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.unit.*
import java.util.*
import kotlin.math.abs

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

    val boxSize = 40.dp
    var selection by remember { mutableStateOf(selected)}
    val mid = (range.last - range.first) / 2f
    val offset by remember { mutableStateOf(boxSize * (mid - selection))}

    var isMax = false
    var isMin = false
    var offsetY by remember { mutableStateOf(0f)}
    val dragState = rememberDraggableState(onDelta = { delta ->
        // We don't want to record the drag if we've reached the end of the bar
        val newOffset = offsetY + delta
        if (isMax) {
            if (newOffset <= offsetY) offsetY = newOffset
        } else if (isMin) {
            if (newOffset >= offsetY) offsetY = newOffset
        } else {
            offsetY = newOffset
        }
    })

    val effectiveDragOffset = with(LocalDensity.current) {
        boxSize * (offsetY.toDp() / boxSize).toInt() // Round off to a number a multiple of boxSize
    }

    val maxOffset = abs(boxSize.value * mid)
    val totalOffset = (effectiveDragOffset + offset).coerceIn(-maxOffset.dp, maxOffset.dp)
    isMin = totalOffset.value <= -maxOffset // Have we reached the minimum point
    isMax = totalOffset.value >= maxOffset

    selection = (mid - totalOffset / boxSize).toInt() // Calculate the current selection based on the total offset
    // TODO: onSelectionUpdate(selection)
    Column(
        modifier = Modifier
            .offset(y = totalOffset)
            .clip(RoundedCornerShape(25))
            .draggable(dragState, orientation = Orientation.Vertical)
    ) {
        range.forEach { n ->
            val background =
                if (n == selection) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
            Box(
                modifier = Modifier
                    .size(boxSize)
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