package com.example.bettingline

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ViewGameScreen(
    game: GameData.Game,
    games: SnapshotStateList<GameData.Game>,
    onBack: () -> Unit,
    onEdit: (GameData.Game) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showEditScreen by remember { mutableStateOf(false) }
    var currentGame by remember { mutableStateOf(game) }
    var isRunning by remember { mutableStateOf(false) }

    // Static target values - frozen
    val staticValues = remember(currentGame) {
        currentGame.playerLines.associate { (it.player to it.lineName) to it.value }
    }

// Live (running) values, based on the last saved liveValue
    val currentValues = remember(currentGame) {
        mutableStateMapOf<Pair<String, String>, Float>().apply {
            currentGame.playerLines.forEach {
                this[it.player to it.lineName] = it.liveValue
            }
        }
    }





    if (showEditScreen) {
        EditGameScreen(
            originalGame = currentGame,
            onSave = { updatedGame ->
                val index = games.indexOfFirst {
                    it.title == currentGame.title &&
                            it.date == currentGame.date &&
                            it.time == currentGame.time
                }
                if (index != -1) {
                    games[index] = updatedGame
                    scope.launch { GameStorage.saveGames(context, games) }
                }
                currentGame = updatedGame
                showEditScreen = false
            },
            onCancel = {
                showEditScreen = false
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFFFA500))
                }
                Text(currentGame.title, style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
            Row {
                IconButton(onClick = { showEditScreen = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFFA500))
                }
                IconButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Betting Game: ${currentGame.title}")
                        putExtra(Intent.EXTRA_TEXT, buildString {
                            append("Game: ${currentGame.title}\n")
                            append("Sport: ${currentGame.sport}\n")
                            append("Date: ${currentGame.date} at ${currentGame.time}\n")
                            if (currentGame.notes.isNotBlank()) append("Notes: ${currentGame.notes}\n")
                        })
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Game"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFFFFA500))
                }
            }
        }

        Divider(color = Color.DarkGray)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val icon = when (currentGame.sport.lowercase()) {
                "mma" -> "🥊"
                "basketball" -> "🏀"
                "football" -> "🏈"
                "soccer" -> "⚽️"
                "baseball" -> "⚾️"
                "hockey" -> "🏒"
                "tennis" -> "🎾"
                "golf" -> "⛳️"
                "custom" -> "🎮"
                else -> "🏅"
            }
            Text("$icon ${currentGame.sport}", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Column(horizontalAlignment = Alignment.End) {
                Row {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = Color(0xFFFFA500), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(currentGame.date, color = Color.White)
                }
                Row {
                    Icon(Icons.Default.AccessTime, contentDescription = "Time", tint = Color(0xFFFFA500), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(currentGame.time, color = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (isRunning) {
                        val updatedPlayerLines = buildList {
                            currentGame.playerLines.forEach {
                                val key = it.player to it.lineName
                                val newLiveValue = currentValues[key] ?: it.liveValue
                                add(it.copy(liveValue = newLiveValue))
                            }
                        }




                        val updatedGame = currentGame.copy(playerLines = updatedPlayerLines)
                        val index = games.indexOfFirst {
                            it.title == currentGame.title &&
                                    it.date == currentGame.date &&
                                    it.time == currentGame.time
                        }

                        if (index != -1) {
                            games[index] = updatedGame
                            scope.launch { GameStorage.saveGames(context, games) }
                        }
                        currentGame = updatedGame
                    }
                    isRunning = !isRunning
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text(if (isRunning) "Stop" else "Run Game", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Description", color = Color(0xFFFFA500), style = MaterialTheme.typography.titleMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1C1C), shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Text(
                if (currentGame.notes.isBlank()) "No notes provided." else currentGame.notes,
                color = Color.White
            )
        }

        Divider(color = Color.DarkGray)

        if (currentGame.playerLines.isNotEmpty()) {
            Text("Lines", color = Color(0xFFFFA500), style = MaterialTheme.typography.titleLarge)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentGame.playerLines.forEach { pl ->
                    val key = pl.player to pl.lineName
                    val current = currentValues[key] ?: pl.value

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1A1A), shape = MaterialTheme.shapes.medium)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pl.player, color = Color.White, style = MaterialTheme.typography.titleSmall)
                            Text(pl.lineName, color = Color(0xFFFFA500), style = MaterialTheme.typography.titleMedium)
                            Text("Target: ${staticValues[key] ?: 0f}", color = Color.LightGray)

                        }
                        if (isRunning) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { currentValues[key] = current - 1f },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("–", color = Color.Black)
                                }
                                OutlinedTextField(
                                    value = current.toString(),
                                    onValueChange = {
                                        it.toFloatOrNull()?.let { f -> currentValues[key] = f }
                                    },
                                    singleLine = true,
                                    modifier = Modifier.width(70.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFA500),
                                        unfocusedBorderColor = Color.DarkGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = Color.White
                                    )
                                )
                                Button(
                                    onClick = { currentValues[key] = current + 1f },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                                ) {
                                    Text("+", color = Color.Black)
                                }
                            }
                        } else {
                            Text("Current: $current", color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
