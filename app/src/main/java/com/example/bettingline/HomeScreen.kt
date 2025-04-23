package com.example.bettingline

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    games: List<GameData.Game>,
    onSportSelected: (String) -> Unit,
    onDeleteGame: (GameData.Game) -> Unit,
    onViewGame: (GameData.Game) -> Unit,
    onEdit: (GameData.Game) -> Unit
) {
    val sports = listOf(
        "Custom" to "🎮", "MMA" to "🥊", "Basketball" to "🏀", "Football" to "🏈",
        "Soccer" to "⚽️", "Baseball" to "⚾️", "Hockey" to "🏒", "Tennis" to "🎾", "Golf" to "⛳️"
    )

    var selectedSport by remember { mutableStateOf("Custom") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredGames = games.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.sport.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Create New Game",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sports.forEach { (sport, icon) ->
                val isSelected = selectedSport == sport
                Button(
                    onClick = {
                        selectedSport = sport
                        onSportSelected(sport)
                    },
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    border = if (isSelected) BorderStroke(2.dp, Color(0xFFFFA500)) else null,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 80.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "$icon $sport",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Find Created Games", color = Color.LightGray)
                        }
                        innerTextField()
                    }
                )
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredGames.isEmpty()) {
            Text("No games found.", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredGames.forEach { game ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewGame(game) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${game.sport}: ${game.title}", color = Color(0xFFFFA500), style = MaterialTheme.typography.titleMedium)
                                Row {
                                    IconButton(onClick = { onEdit(game) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                    }
                                    IconButton(onClick = { onDeleteGame(game) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }

                            val timeText = if (game.date.isBlank() || game.time.isBlank()) {
                                "No time provided"
                            } else {
                                "${game.date} at ${game.time}"
                            }
                            Text(timeText, color = Color(0xFFFFA500))

                            val notesText = if (game.notes.isBlank()) {
                                "No description provided"
                            } else {
                                game.notes
                            }
                            Text(notesText, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
