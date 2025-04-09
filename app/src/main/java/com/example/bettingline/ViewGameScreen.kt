package com.example.bettingline

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
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
    var isRunning by remember { mutableStateOf(false) }

    val lineStates = remember {
        game.bettingLines.associateWith { line ->
            mutableStateOf(game.lineValues[line] ?: 0f)
        }.toMutableMap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFFFA500))
                }
                Text(game.title, style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }

            Row {
                IconButton(onClick = { onEdit(game) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFFA500))
                }
                IconButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Betting Game: ${game.title}")
                        putExtra(Intent.EXTRA_TEXT, buildString {
                            append("Game: ${game.title}\n")
                            append("Sport: ${game.sport}\n")
                            append("Date: ${game.date} at ${game.time}\n")
                            if (game.notes.isNotBlank()) append("Notes: ${game.notes}\n")
                            if (lineStates.isNotEmpty()) {
                                append("Betting Lines:\n")
                                lineStates.forEach { (name, value) ->
                                    append("- $name: ${value.value}\n")
                                }
                            }
                        })
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Game"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFFFFA500))
                }
            }
        }

        Divider(color = Color.DarkGray)

        // Sport & Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                val sportIcon = when (game.sport.lowercase()) {
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

                Text(
                    text = "$sportIcon ${game.sport}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = Color(0xFFFFA500), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(game.date, color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Time", tint = Color(0xFFFFA500), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(game.time, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Run Game Button
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    if (isRunning) {
                        val updatedValues = lineStates.mapValues { it.value.value }
                        val updatedGame = game.copy(lineValues = updatedValues)
                        val index = games.indexOfFirst { it.title == game.title && it.date == game.date }
                        if (index != -1) {
                            games[index] = updatedGame
                            scope.launch {
                                GameStorage.saveGames(context, games)
                            }
                        }
                    }
                    isRunning = !isRunning
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text(if (isRunning) "Stop" else "Run Game", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Description
        Text("Description", color = Color(0xFFFFA500), style = MaterialTheme.typography.titleMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1C1C), shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Text(
                text = if (game.notes.isBlank()) "No notes provided." else game.notes,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Divider(color = Color.DarkGray)

        // Betting Lines
        if (lineStates.isNotEmpty()) {
            Text("Lines", color = Color(0xFFFFA500), style = MaterialTheme.typography.titleLarge)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lineStates.forEach { (lineName, state) ->
                    val lineValue = state.value

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1A1A), shape = MaterialTheme.shapes.medium)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            lineName,
                            color = Color(0xFFFFFFFF),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        if (isRunning) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { state.value -= 1f },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) { Text("–", color = Color.Black) }

                                OutlinedTextField(
                                    value = lineValue.toString(),
                                    onValueChange = {
                                        it.toFloatOrNull()?.let { newVal -> state.value = newVal }
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
                                    onClick = { state.value += 1f },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                                ) { Text("+", color = Color.Black) }
                            }
                        } else {
                            Text("Value: $lineValue", color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
