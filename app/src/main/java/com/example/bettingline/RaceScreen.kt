package com.example.bettingline

import android.app.Application
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RaceScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val horses = remember { mutableStateListOf<HorseWithState>() }
    var raceInProgress by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var eventLog by remember { mutableStateOf(listOf<String>()) }

    val dao = remember {
        HorseDatabase.getDatabase(context).horseDao()
    }

    // Collect horses from the DB
    LaunchedEffect(Unit) {
        dao.getAllHorses().collect { dbHorses ->
            horses.clear()
            horses.addAll(
                dbHorses.map {
                    HorseWithState(
                        id = it.id,
                        name = it.name,
                        speed = it.speed,
                        stamina = it.stamina,
                        luck = it.luck,
                        experience = it.experience,
                        colorHex = it.colorHex,
                    )
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Race Track
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .aspectRatio(1.5f) // Optional: keeps canvas oval-shape
        ) {
            HorseRaceScreen(
                horses = horses,
                raceInProgress = raceInProgress,
                onRaceEnd = { winner = it },
                onEvent = { event -> eventLog = eventLog + event }
            )
        }

        winner?.let {
            Text(
                text = "🏆 Winner: $it!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }

        // Event Log
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

        // Horse list display with delete button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            horses.forEach { horse ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.horse),
                        contentDescription = horse.name,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White),
                        colorFilter = ColorFilter.tint(horse.color)
                    )
                    Text(
                        text = horse.name,
                        color = horse.color,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (!raceInProgress) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    dao.deleteById(horse.id) // Use the ID from HorseWithState
                                    horses.remove(horse) // Remove from UI list
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = "Delete Horse",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    scope.launch {
                        // Always stop race first
                        raceInProgress = false
                        delay(100) // Let the UI reset

                        // Reset game state
                        winner = null
                        eventLog = emptyList()
                        horses.forEach { it.progress.floatValue = 0f }

                        // Then start race again
                        raceInProgress = true
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                if (raceInProgress ) {
                    Text("Restart Race", color = Color.White)
                } else {
                    Text("Start Race", color = Color.White)

                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!raceInProgress) {
                Button(
                    onClick = {
                        scope.launch {
                            val newHorse = generateRandomHorse()
                            dao.insertHorse(newHorse)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32CD32))
                ) {
                    Text("Add Horse", color = Color.White)
                }
            }
        }
    }
}


fun generateRandomHorse(): Horse {
    val colors = listOf("#FF0000", "#00FF00", "#0000FF", "#FFA500", "#800080")
    val names = listOf("Thunder", "Blaze", "Storm", "Shadow", "Lightning", "Dusty", "Rocket")

    return Horse(
        name = names.random(),
        speed = Random.nextFloat() * 0.5f + 1.0f,
        stamina = Random.nextFloat(),
        luck = Random.nextFloat(),
        experience = Random.nextFloat(),
        colorHex = colors.random()
    )
}

@Composable
fun HorseRaceScreen(
    horses: List<HorseWithState>,
    raceInProgress: Boolean,
    onRaceEnd: (String) -> Unit,
    onEvent: (String) -> Unit
) {
    val raceFinished = remember { mutableStateOf(false) }
    val animatables = remember(horses.size) { horses.map { Animatable(0f) }.toMutableList() }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx: Float
    val screenHeightPx: Float

    with(density) {
        screenWidthPx = configuration.screenWidthDp.dp.toPx()
        screenHeightPx = configuration.screenHeightDp.dp.toPx()
    }

    val centerX = screenWidthPx / 2f
    val centerY = screenHeightPx / 2.5f
    val xRadius = screenWidthPx / 3f
    val yRadius = screenHeightPx / 4f

    Box(modifier = Modifier.fillMaxSize()) {

        // Draw the oval track
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawOval(
                color = Color.DarkGray,
                topLeft = Offset(centerX - xRadius, centerY - yRadius),
                size = Size(xRadius * 2, yRadius * 2)
            )
            val trackPadding = 20.dp.toPx() // Adjust for thickness
            drawOval(
                color = Color.Green,
                topLeft = Offset(centerX - xRadius + trackPadding, centerY - yRadius + trackPadding),
                size = Size((xRadius - trackPadding) * 2, (yRadius - trackPadding) * 2)
            )
        }

        // Draw each horse using Image and calculated position
        horses.forEachIndexed { index, horse ->
            val progress = animatables[index].value
            val angle = Math.toRadians((progress * 360f).toDouble())

            val x = centerX + xRadius * cos(angle).toFloat()
            val y = centerY + yRadius * sin(angle).toFloat()

            val horseSizePx = with(density) { 40.dp.toPx() }

            Image(
                painter = painterResource(id = R.drawable.horse),
                contentDescription = horse.name,
                modifier = Modifier
                    .size(with(density) { horseSizePx.toDp() })
                    .offset {
                        IntOffset(
                            (x - horseSizePx / 2).toInt(),
                            (y - horseSizePx / 2).toInt()
                        )
                    }
                    .graphicsLayer {
                        rotationZ = (progress * 360f + 90f) % 360
                        scaleX = -1f
                    },
                colorFilter = ColorFilter.tint(horse.color),
            )
        }
    }


    // Race logic stays the same
    LaunchedEffect(raceInProgress) {
        if (!raceInProgress) return@LaunchedEffect
        raceFinished.value = false

        while (horses.any { it.progress.floatValue < 1f } && !raceFinished.value) {
            horses.forEachIndexed { index, horse ->
                if (horse.progress.floatValue >= 1.0f) {
                    if (!raceFinished.value) {
                        raceFinished.value = true
                        onRaceEnd(horse.name)
                    }
                    return@forEachIndexed
                }

                val speedFactor = horse.speed * 0.001f
                val newProgress = (horse.progress.floatValue + speedFactor).coerceAtMost(1.0f)
                horse.progress.floatValue = newProgress

                launch {
                    animatables[index].animateTo(
                        newProgress,
                        animationSpec = tween(durationMillis = 100)
                    )
                }

                if (Random.nextFloat() < 0.02f) {
                    val event = listOf("wind", "track condition", "collision").random()
                    val affected = Random.nextFloat() > horse.luck
                    if (affected) {
                        val message = when (event) {
                            "wind" -> "💨 Wind slowed ${horse.name}!"
                            "track condition" -> "🌧️ Track affected ${horse.name}!"
                            else -> "🐎 Collision! ${horse.name} lost speed!"
                        }
                        horse.progress.floatValue -= 0.02f
                        onEvent(message)
                    }
                }
            }
            delay(50L)
        }
    }
}
