package com.example.countdownchallenge

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = time.zero(), key2 = started) {
        while (started && !time.zero()) {
            delay(1000)
            time = --time
            started = !time.zero()
        }
    }

    val playPause = if (started) Icons.Default.PauseCircle else Icons.Default.PlayCircle
    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        CountDown(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.8f), time = time
        ) { time = it }
        Column {
            IconButton(onClick = { if (!started && !time.zero()) started = true }) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "start count down",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            IconButton(onClick = { if (started) started = false }) {
                Icon(
                    imageVector = Icons.Default.PauseCircle,
                    contentDescription = "pause count down",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CountDown(modifier: Modifier = Modifier, time: Time, updateTime: (Time) -> Unit) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberWheel(range = (0..5), selected = time.minutes / 10) {
            updateTime(time.copy(minutes = it * 10 + time.minutes % 10))
        }
        NumberWheel(range = (0..9), selected = time.minutes % 10) {
            updateTime(time.copy(minutes = time.minutes / 10 * 10 + it))
        }
        NumberWheel(range = (0..5), selected = time.seconds / 10) {
            updateTime(time.copy(seconds = it * 10 + time.seconds % 10))
        }
        NumberWheel(range = (0..9), selected = time.seconds % 10) {
            updateTime(time.copy(seconds = time.seconds / 10 * 10 + it)) // Replace the unit
        }
    }
}

@Composable
private fun NumberWheel(range: IntRange, selected: Int, onSelectionUpdate: (Int) -> Unit) {

    val boxSizeDp = 40.dp
    val boxSize = with(LocalDensity.current) { boxSizeDp.toPx() }
    val mid = (range.last - range.first) / 2f
    val reset = selected == range.last
    val offset by animateFloatAsState(
        targetValue = boxSize * (mid - selected),
        animationSpec = if (reset) {
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
        } else {
            tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        }
    )
    //Log.d(TAG, "NumberWheel: offset: $offset")

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
            Digit(n, active = selected == n, boxSizeDp)
        }
    }
}

@Composable
private fun Digit(value: Int, active: Boolean, boxSizeDp: Dp) {
    val background by animateColorAsState(
        if (active) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
    )
    Box(
        modifier = Modifier
            .size(boxSizeDp)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(text = value.toString(), fontSize = 20.sp, color = MaterialTheme.colors.onPrimary)
    }
}

@Preview(name = "Count Down preview", device = Devices.PIXEL_4)
@Composable
fun CountDownPreview() {
    CountDownScreen()
}