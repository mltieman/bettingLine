package com.example.bettingline


import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material.icons.filled.Archive
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun RaceScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val horses = remember { mutableStateListOf<HorseWithState>() }
    var raceInProgress by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var eventLog by remember { mutableStateOf(listOf<String>()) }

    var showBetDialog by remember { mutableStateOf(false) }
    var showBetsPopup by remember { mutableStateOf(false) }
    var currentSelectedHorse by remember { mutableStateOf<HorseWithState?>(null) }
    val players = remember { mutableStateListOf<String>() }
    val bets = remember { mutableStateMapOf<Pair<String, String>, Int>() } // key: (player, horse), value: amount
    var totalBetsPerHorse = bets
        .entries
        .groupBy { it.key.second } // horseName
        .mapValues { entry -> entry.value.sumOf { it.value } } // sum of amounts
    val totalPool = bets.values.sum()
    var playerName by remember { mutableStateOf("") }
    var betAmount by remember { mutableStateOf("") }

    fun resetBettingState() {
        showBetDialog = false
        currentSelectedHorse = null
        totalBetsPerHorse = emptyMap()
        players.clear()
        bets.clear()
        playerName = ""
        betAmount = ""
        eventLog = emptyList()
    }
    //val winnings = remember { mutableStateMapOf<String, Int>()}

    fun calculateOddsBasedWinnings(winningHorse: String): Map<String, Int> {
        val winningHorseTotal = totalBetsPerHorse[winningHorse] ?: 0

        if (winningHorseTotal == 0) return emptyMap() // No one bet on winner

        val result = mutableMapOf<String, Int>()

        bets.forEach { (playerHorsePair, amount) ->
            val (playerNamed, horseName) = playerHorsePair

            if (horseName == winningHorse) {
                val playerShare = amount.toDouble() / winningHorseTotal
                val payout = (playerShare * totalPool).toInt()
                result[playerNamed] = payout
            }
        }

        return result
    }

    val dao = remember {
        HorseDatabase.getDatabase(context).horseDao()
    }

    // Collect horses from the DB
    LaunchedEffect(Unit) {
        dao.getAllHorses().collect { dbHorses ->
            // Ensure there are no more than 8 horses in the list
            val horsesList = dbHorses.map {
                HorseWithState(
                    id = it.id,
                    name = it.name,
                    speed = it.speed,
                    stamina = it.stamina,
                    luck = it.luck,
                    experience = it.experience,
                    colorHex = it.colorHex,
                )
            }.shuffled().take(6) // Shuffle and take up to 8 horses
            horses.clear()
            horses.addAll(horsesList)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { showBetsPopup = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
            ) {
                Text("Show Bets", color = Color.White)
            }
        }
        if (!raceInProgress && winner == null) {
            Text(
                "Bet a winner", fontSize = 50.sp, color = Color.White
            )
        }
        // Race Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f) // Adjust the weight for more vertical space
                .offset(x = (-20).dp) // Shift the track slightly to the left
                .offset(y = (40).dp)
                .align(Alignment.CenterHorizontally) // Center it horizontally
        ) {

            HorseRaceScreen(
                horses = horses,
                raceInProgress = raceInProgress,
                onRaceEnd = { s ->
                    winner = s
                    scope.launch {
                        delay(10000L) // Wait 3 seconds before resetting to start screen
                        raceInProgress = false
                        winner = null
                        eventLog = emptyList()
                        horses.forEach { it.progress.floatValue = 0f }
                        resetBettingState()
                    } },
                onEvent = { event -> eventLog = eventLog + event }
            )
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 2.dp)
//                    .verticalScroll(rememberScrollState())
//                    .offset(x = (20).dp)
//
//            ) {
//                eventLog.takeLast(5).forEach { event ->
//                    Text(
//                        text = event,
//                        modifier = Modifier.padding(4.dp),
//                        color = Color.White
//                    )
//                }
//            }
        }
        winner?.let {
            //eventLog = listOfNotNull()
//            Text(
//                text = "🏆 Winner: $it!",
//                style = MaterialTheme.typography.headlineMedium,
//                color = Color.White
//            )
            val winnings = calculateOddsBasedWinnings(winner!!)
            Text(
                text = "🏆 Race Results",
                color = Color.White,
                fontSize = 24.sp
            )

            Text(
                text = "Winner: $winner",
                color = Color.White,
                fontSize = 20.sp
            )

            winnings.forEach { (player, amount) ->
                Text("$player won $$amount",color = Color.White)
            }
            //players = null
        }

        // Event Log
        if (winner == null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp)
                    .verticalScroll(rememberScrollState())
                //.offset(y = (40).dp)
            ) {
                eventLog.takeLast(5).forEach { event ->
                    Text(
                        text = event,
                        modifier = Modifier.padding(4.dp),
                        color = Color.White
                    )
                }
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
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
                                if (!raceInProgress) {
                                    currentSelectedHorse = horse
                                    showBetDialog = true
                                }
                            },
                        colorFilter = ColorFilter.tint(horse.color)
                    )
                    Text(
                        text = horse.name,
                        color = horse.color,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(50.dp), // Limit width to image size
                        textAlign = TextAlign.Center
                    )
                    if (!raceInProgress) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    dao.deleteById(horse.id)
                                    horses.remove(horse)
                                    resetBettingState()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = "Delete Horse",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
        if (showBetDialog && currentSelectedHorse != null) {
            AlertDialog(
                onDismissRequest = {
                    showBetDialog = false
                    playerName = ""
                    betAmount = ""
                },
                title = {
                    Text("Place Bet on ${currentSelectedHorse?.name}")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = playerName,
                            onValueChange = { playerName = it },
                            label = { Text("Player Name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = betAmount,
                            onValueChange = { betAmount = it },
                            label = { Text("Bet Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val horse = currentSelectedHorse
                            val amount = betAmount.toIntOrNull()

                            val minimumBet = 10

                            if (amount!=null && amount < minimumBet) {
                                // Show error message to user
                                Toast.makeText(context, "Minimum bet is $$minimumBet", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (horse != null && playerName.isNotBlank() && amount != null) {
                                if (players.contains(playerName)) {
                                    Toast.makeText(context, "Player name already used. Choose a different name.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                players.add(playerName)
                                bets[playerName to horse.name] = amount
                               // eventLog = eventLog + "$playerName bet $betAmount \uD83D\uDCB0 on ${currentSelectedHorse?.name}"
                                showBetDialog = false
                                playerName = ""
                                betAmount = ""
                            }
                            //eventLog = eventLog + "$playerName bet $betAmount \uD83D\uDCB0 on ${currentSelectedHorse?.name}"
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showBetDialog = false
                            playerName = ""
                            betAmount = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (winner == null && horses.size >= 2) {
                Button(
                    onClick = {
                        scope.launch {
                            // Always stop race first
                            raceInProgress = !raceInProgress
                            delay(100) // Let the UI reset

                            // Reset game state
                            winner = null
                            eventLog = emptyList()
                            horses.forEach { it.progress.floatValue = 0f }

                            // Then start race again
                            //raceInProgress = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                ) {
                    if (raceInProgress) {
                        Text("End Race", color = Color.White)
                    } else {
                        Text("Start Race", color = Color.White)

                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!raceInProgress) {
                Button(
                    onClick = {
                        scope.launch {
                            val newHorse = generateRandomHorse()
                            dao.insertHorse(newHorse)
                            resetBettingState()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32CD32))
                ) {
                    Text("Add Horse", color = Color.White)
                }
            }
        }
        if (!raceInProgress) {HorseListPopupButton(horses = horses)}
        if (showBetsPopup) {
            AlertDialog(
                onDismissRequest = { showBetsPopup = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Current Bets", style = MaterialTheme.typography.titleLarge)
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Pool: $$totalPool", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (bets.isEmpty()) {
                            Text("No bets placed yet.")
                        } else {
                            // Header row
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Player", modifier = Modifier.weight(1f))
                                Text("Bet", modifier = Modifier.weight(1f))
                                Text("Horse", modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            bets.forEach { (playerHorsePair, amount) ->
                                val (player, horse) = playerHorsePair
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(player, modifier = Modifier.weight(1f))
                                    Text("$$amount", modifier = Modifier.weight(1f))
                                    Text(horse, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showBetsPopup = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
fun randomHexColor(): String {
    val randomColor = (0..0xFFFFFF).random() // Random number between 0 and 0xFFFFFF
    return String.format("#%06X", randomColor) // Convert the number to a hex color string
}

fun generateRandomHorse(): Horse {
    val names = NameList.names
    return Horse(
        name = names.random(),
        speed = Random.nextFloat() * 0.5f + 1.0f,
        stamina = Random.nextFloat(),
        luck = Random.nextFloat(),
        experience = Random.nextFloat(),
        colorHex = randomHexColor()
    )
}


@Composable
fun HorseListPopupButton(horses: List<HorseWithState>) {
    var showHorseListDialog by remember { mutableStateOf(false) }

    // Button to show the horse list dialog
    Button(
        onClick = { showHorseListDialog = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Horse Stats")
    }

    // Horse list dialog
    if (showHorseListDialog) {
        HorseListDialog(
            horses = horses,
            onDismiss = { showHorseListDialog = false }
        )
    }
}

@Composable
fun HorseListDialog(horses: List<HorseWithState>, onDismiss: () -> Unit) {
    val speedWeight = 0.4f
    val staminaWeight = 0.3f
    val experienceWeight = 0.2f
    val luckWeight = 0.1f

    // Calculate total score for each horse
    val horseScores = horses.map { horse ->
        val totalScore = (horse.speed * speedWeight) +
                (horse.stamina * staminaWeight) +
                (horse.experience * experienceWeight) +
                (horse.luck * luckWeight)
        horse to totalScore
    }
    val totalScoreSum = horseScores.sumOf { it.second.toDouble()}  // Sum of totalScore for each horse
    val horseProbabilities = horseScores.map { (horse, score) ->
        horse to (score / totalScoreSum)  // Probability of winning
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Horse List with Winning Probabilities") },
        text = {
            Column {
                horseProbabilities.forEach { (horse, probability) ->
                    Text(
                        text = "Name: ${horse.name}, Speed: ${"%.3f".format(horse.speed)}, Stamina: ${"%.3f".format(horse.stamina)}, Probability: ${(probability * 100).toInt()}%",
                                modifier = Modifier.padding(4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Close")
            }
        }
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
    val centerY = screenHeightPx / 4f
    val xRadius = screenWidthPx / 3f
    val yRadius = screenHeightPx / 4f

    Box(modifier = Modifier.fillMaxSize()) {

        // Draw the oval track

        Canvas(modifier = Modifier.fillMaxSize()) {
            val hexColor = Color(0xFFA52A2A)
            drawOval(
                color = hexColor,
                topLeft = Offset(centerX - xRadius, centerY - yRadius),
                size = Size(xRadius * 2, yRadius * 2)
            )
            val trackPadding = 20.dp.toPx() // Adjust for thickness
            drawOval(
                color = Color(0xFF006400),
                topLeft = Offset(centerX - xRadius + trackPadding, centerY - yRadius + trackPadding),
                size = Size((xRadius - trackPadding) * 2, (yRadius - trackPadding) * 2)
            )
            val rightX = centerX + xRadius - 25f // near the right edge
            val lineLength = 20.dp.toPx()

            drawLine(
                color = Color.White,
                start = Offset(rightX - lineLength / 2, centerY), // center horizontally at right edge
                end = Offset(rightX + lineLength / 2, centerY),
                strokeWidth = 4.dp.toPx()
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
    LaunchedEffect(raceInProgress) {
        if (!raceInProgress) {
            animatables.forEach { animatable ->
                launch {
                    animatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 600)
                    )
                }
            }
        }
    }
}
