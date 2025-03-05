package com.example.bettingline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RaceScreen()
        }
    }
}

@Composable
fun RaceScreen() {
    var raceInProgress by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var eventLog by remember { mutableStateOf(listOf<String>()) }

    val horses = remember {
        mutableStateListOf(
            Horse("Thunder", 1.2f, 0.8f, 0.7f, 0.9f),
            Horse("Blaze", 1.1f, 0.9f, 0.5f, 0.8f),
            Horse("Storm", 1.3f, 0.7f, 0.6f, 1.0f),
            Horse("Shadow", 1.0f, 1.0f, 0.9f, 0.6f)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Race Track
        Box(modifier = Modifier.weight(1f)) {
            HorseRaceScreen(
                horses = horses,
                raceInProgress = raceInProgress,
                onRaceEnd = { winner = it },
                onEvent = { event -> eventLog = eventLog + event }
            )
        }

        // Winner Text
        winner?.let {
            Text(text = "🏆 Winner: $it!", style = MaterialTheme.typography.headlineMedium)
        }

        // Event Log
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            eventLog.takeLast(5).forEach { event ->
                Text(text = event, modifier = Modifier.padding(4.dp))
            }
        }

        // Start/Restart Button
        Button(
            onClick = {
                raceInProgress = !raceInProgress
                winner = null
                eventLog = emptyList()
                horses.forEach { it.progress.value = 0f }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (raceInProgress) "Restart Race" else "Start Race")
        }
    }
}

@Composable
fun HorseRaceScreen(
    horses: List<Horse>,
    raceInProgress: Boolean,
    onRaceEnd: (String) -> Unit,
    onEvent: (String) -> Unit
) {
    val raceState = remember { horses.map { it.copy(progress = mutableFloatStateOf(0f)) } }
    val raceFinished = remember { mutableStateOf(false) }
    val animatables = remember { raceState.map { Animatable(0f) } }
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val trackRadius = size.minDimension / 3
        drawCircle(color = Color.Gray, center = Offset(centerX, centerY), radius = trackRadius)
        raceState.forEachIndexed { index, horse ->
            val animatedProgress = animatables[index].value
            val angle = animatedProgress * 360f
            val radian = Math.toRadians(angle.toDouble())
            val x = centerX + trackRadius * cos(radian).toFloat()
            val y = centerY + trackRadius * sin(radian).toFloat()

            drawCircle(color = horse.color, radius = 20f, center = Offset(x, y))
            //showIcon(horse, x, y)
        }
    }

    LaunchedEffect(raceInProgress) {
        if (!raceInProgress) return@LaunchedEffect

        while (raceState.any { it.progress.value < 1.0f } && !raceFinished.value) {
            raceState.forEachIndexed { index, horse ->
                if (horse.progress.value >= 1.0f) {
                    if (!raceFinished.value) {
                        raceFinished.value = true
                        onRaceEnd(horse.name)
                    }
                    return@forEachIndexed
                }

                val speedFactor = horse.speed * 0.001f
                val newProgress = (horse.progress.value + speedFactor).coerceAtMost(1.0f)

                // Smooth animation to new position
                launch {
                    animatables[index].animateTo(
                        targetValue = newProgress,
                        animationSpec = tween(durationMillis = 100, easing = { it })
                    )
                }

                horse.progress.value = newProgress

                // Random Events that will impede the horses by their luck
                if (Random.nextFloat() < 0.02f) {
                    val event = listOf("wind", "track condition", "collision").random()
                    val affected = Random.nextFloat() > horse.luck

                    if (affected) {
                        when (event) {
                            "wind" -> {
                                horse.progress.value -= 0.02f
                                onEvent("💨 Wind slowed ${horse.name}!")
                            }
                            "track condition" -> {
                                horse.progress.value -= 0.015f
                                onEvent("🌧️ Track condition affected ${horse.name}!")
                            }
                            "collision" -> {
                                horse.progress.value -= 0.03f
                                onEvent("🐎 Collision! ${horse.name} lost speed!")
                            }
                        }
                    }
                }
            }
            delay(50L)
        }
    }
}
// horse will have stats to determine the winner
data class Horse(
    val name: String,
    val speed: Float,
    val stamina: Float,
    val luck: Float,  // 0.0 (unlucky) to 1.0 (very lucky)
    val experience: Float,
    val progress: MutableState<Float> = mutableFloatStateOf(0f), // Mutable state
    val color: Color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
)
