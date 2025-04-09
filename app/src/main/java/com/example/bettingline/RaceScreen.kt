package com.example.bettingline

import DatabaseHelper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
@Composable
fun RaceScreen() {
    var raceInProgress by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var eventLog by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

    // Fetch horses from the database
    val horses = remember { mutableStateListOf<Horse>() }

    // Fetch data and populate the database with default horses if it's empty
    LaunchedEffect(Unit) {
        val fetchedHorses = databaseHelper.horseDao.getAllHorses()

        // If the database is empty, add default horses
        if (fetchedHorses.isEmpty()) {
            val defaultHorses = listOf(
                HorseEntity(name = "Thunder", speed = 1.2f, stamina = 0.8f, luck = 0.7f, experience = 0.9f, colorHex = "#FF0000"),
                HorseEntity(name = "Blaze", speed = 1.1f, stamina = 0.9f, luck = 0.5f, experience = 0.8f, colorHex = "#00FF00"),
                HorseEntity(name = "Storm", speed = 1.3f, stamina = 0.7f, luck = 0.6f, experience = 1.0f, colorHex = "#0000FF"),
                HorseEntity(name = "Shadow", speed = 1.0f, stamina = 1.0f, luck = 0.9f, experience = 0.6f, colorHex = "#FFFF00")
            )

            // Insert default horses into the database
            defaultHorses.forEach { databaseHelper.horseDao.insert(it) }
            // Add them to the in-memory list as Horses with progress = 0
            horses.addAll(defaultHorses.map { Horse(it.id, it.name, it.speed, it.stamina, it.luck, it.experience, progress = 0f, colorHex = it.colorHex) })
        } else {
            // Convert the fetched HorseEntity into Horse (with progress)
            horses.addAll(fetchedHorses.map { Horse(it.id, it.name, it.speed, it.stamina, it.luck, it.experience, progress = 0f, colorHex = it.colorHex) })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Race Track
        Box(modifier = Modifier.weight(1f)) {
            key(raceInProgress) {
                HorseRaceScreen(
                    horses = horses,
                    raceInProgress = raceInProgress,
                    onRaceEnd = { winner = it },
                    onEvent = { event -> eventLog = eventLog + event }
                )
            }
        }

        // Winner Text in white
        winner?.let {
            Text(
                text = "🏆 Winner: $it!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }

        // Event Log with white text
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            eventLog.takeLast(5).forEach { event ->
                Text(
                    text = event,
                    modifier = Modifier.padding(4.dp),
                    color = Color.White
                )
            }
        }

        // show list of horses and their corresponding color and name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            horses.forEach { horse ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.horse), // Replace with your horse.png
                        contentDescription = horse.name,
                        modifier = Modifier
                            .size(50.dp)
                            .background(horse.color),
                    )
                    Text(
                        text = horse.name,
                        color = horse.color, // Text color matches the horse's color
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Start/Restart Button
        Button(
            onClick = {
                raceInProgress = !raceInProgress
                winner = null
                eventLog = emptyList()
                horses.forEach { it.progress = 0f }  // Reset progress when restarting the race
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text(
                text = if (raceInProgress) "Restart Race" else "Start Race",
                color = Color.White
            )
        }
    }
}

@Composable
fun HorseRaceScreen(
    horses: List<Horse>,  // Use the list of Horse objects that includes progress
    raceInProgress: Boolean,
    onRaceEnd: (String) -> Unit,
    onEvent: (String) -> Unit
) {
    val raceState = remember { horses.map { it.copy(progress = 0f) } }  // Reset the race state each time
    val raceFinished = remember { mutableStateOf(false) }
    val animatables = remember { raceState.map { Animatable(0f) } }  // Animation for smooth movement

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val xRadius = size.width / 3
        val yRadius = size.height / 4

        // Draw the race track as an oval
        drawOval(color = Color.Gray, topLeft = Offset(centerX - xRadius, centerY - yRadius), size = androidx.compose.ui.geometry.Size(xRadius * 2, yRadius * 2))

        // Draw each horse in the race
        raceState.forEachIndexed { index, horse ->
            val animatedProgress = animatables[index].value
            val angle = animatedProgress * 360f
            val radian = Math.toRadians(angle.toDouble())
            val x = centerX + xRadius * cos(radian).toFloat()
            val y = centerY + yRadius * sin(radian).toFloat()

            drawCircle(color = horse.color, radius = 20f, center = Offset(x, y))
        }
    }

    LaunchedEffect(raceInProgress) {
        if (!raceInProgress) return@LaunchedEffect

        while (raceState.any { it.progress < 1.0f } && !raceFinished.value) {
            raceState.forEachIndexed { index, horse ->
                if (horse.progress >= 1.0f) {
                    if (!raceFinished.value) {
                        raceFinished.value = true
                        onRaceEnd(horse.name)
                    }
                    return@forEachIndexed
                }

                // Calculate speed factor
                val speedFactor = horse.speed * 0.001f
                val newProgress = (horse.progress + speedFactor).coerceAtMost(1.0f)

                // Animate the horse's progress on the track
                launch {
                    animatables[index].animateTo(
                        targetValue = newProgress,
                        animationSpec = tween(durationMillis = 100, easing = { it })
                    )
                }

                horse.progress = newProgress  // Update the horse's progress

                // Random events affecting the horses based on their luck
                if (Random.nextFloat() < 0.02f) {
                    val event = listOf("wind", "track condition", "collision").random()
                    val affected = Random.nextFloat() > horse.luck

                    if (affected) {
                        when (event) {
                            "wind" -> {
                                horse.progress -= 0.02f
                                onEvent("💨 Wind slowed ${horse.name}!")
                            }
                            "track condition" -> {
                                horse.progress -= 0.015f
                                onEvent("🌧️ Track condition affected ${horse.name}!")
                            }
                            "collision" -> {
                                horse.progress -= 0.03f
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
