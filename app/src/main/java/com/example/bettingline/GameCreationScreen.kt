package com.example.bettingline

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CreateGameScreen(selectedSport: String = "Custom", onGameCreated: (GameData.Game) -> Unit) {
    var title by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf(selectedSport) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val bettingLines = remember { mutableStateListOf<String>() }
    var newLine by remember { mutableStateOf("") }

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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFFFA500),
        unfocusedBorderColor = Color.Gray,
        cursorColor = Color(0xFFFFA500),
        focusedLabelColor = Color(0xFFFFA500),
        unfocusedLabelColor = Color.LightGray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Create Game", style = MaterialTheme.typography.headlineMedium, color = Color.White)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Game Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )

        Text("Select Sport", color = Color.White)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sports.forEach { (name, icon) ->
                val isSelected = name == sport
                Button(
                    onClick = { sport = name },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFFA500) else Color.DarkGray
                    ),
                    shape = MaterialTheme.shapes.large,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("$icon $name", color = Color.White)
                }
            }
        }

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (e.g., 2024-04-10)") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time (e.g., 7:30 PM)") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = fieldColors
        )

        Divider(color = Color.Gray)

        Text("Custom Betting Lines", color = Color.White)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newLine,
                onValueChange = { newLine = it },
                label = { Text("Add Line") },
                modifier = Modifier.weight(1f),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newLine.isNotBlank()) {
                        bettingLines.add(newLine)
                        newLine = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text("Add", color = Color.White)
            }

        }

        bettingLines.forEachIndexed { index, line ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF222222))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = line, color = Color.White)
                IconButton(onClick = { bettingLines.removeAt(index) }) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val game = GameData.Game(
                    title = title,
                    sport = sport,
                    date = date,
                    time = time,
                    notes = notes,
                    bettingLines = bettingLines.toList()
                )
                onGameCreated(game)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("Create Game", color = Color.White)
        }
    }
}
