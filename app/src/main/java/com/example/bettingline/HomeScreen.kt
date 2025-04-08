package com.example.bettingline

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    games: List<GameData.Game>,
    onSportSelected: (String) -> Unit,
    onDeleteGame: (GameData.Game) -> Unit // ✅ add this

) {
    val sports = listOf(
        "Custom" to "🎮",
        "MMA" to "🥊",
        "Basketball" to "🏀",
        "Football" to "🏈",
        "Soccer" to "⚽️",
        "Baseball" to "⚾️",
        "Hockey" to "🏒",
        "Tennis" to "🎾",
        "Golf" to "⛳️"
    )

    var selectedSport by remember { mutableStateOf("Custom") }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Heading
        Text(
            text = "Create New Game",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Scrollable sports selector
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

        // Search bar with filter icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text("Find Created Games", color = Color.LightGray)
                    }
                    innerTextField()
                }
            )

            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Game List or Empty Message
        if (games.isEmpty()) {
            Text(
                text = "No bets created yet.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                games.forEach { game ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) { // ✅ Must use Box here
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.CenterStart) // ✅ Aligns Column inside Box
                            ) {
                                Text(
                                    text = game.title,
                                    color = Color(0xFFFFA500),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "🕒 ${game.date} at ${game.time}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                if (game.notes.isNotBlank()) {
                                    Text(
                                        text = game.notes,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            // ✅ Top-right trash icon aligned inside Box
                            IconButton(
                                onClick = { onDeleteGame(game) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd) // ✅ Only works inside Box
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Game",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}


