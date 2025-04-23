package com.example.bettingline

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bettingline.GameData.Game
import com.example.bettingline.GameData.PlayerLine
import java.util.*

@Composable
fun CreateGameScreen(selectedSport: String = "Custom", onGameCreated: (Game) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var title by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf(selectedSport) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var newPlayer by remember { mutableStateOf("") }
    val players = remember { mutableStateListOf<String>() }

    var newLineName by remember { mutableStateOf("") }
    val globalLines = remember { mutableStateListOf<String>() }

    val staticPlayerLines = remember { mutableStateListOf<PlayerLine>() }
    val staticValues = remember { mutableMapOf<String, Float>() }
    val valueInputs = remember { mutableStateMapOf<Pair<String, String>, String>() }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFFFA500),
        unfocusedBorderColor = Color.Gray,
        cursorColor = Color(0xFFFFA500),
        focusedLabelColor = Color(0xFFFFA500),
        unfocusedLabelColor = Color.LightGray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    val sports = listOf(
        "Custom" to "🎮", "MMA" to "🥊", "Basketball" to "🏀", "Football" to "🏈",
        "Soccer" to "⚽️", "Baseball" to "⚾️", "Hockey" to "🏒", "Tennis" to "🎾", "Golf" to "⛳️"
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            time = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
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
            value = title, onValueChange = { title = it },
            label = { Text("Game Title") }, modifier = Modifier.fillMaxWidth(), colors = fieldColors
        )

        Text("Select Sport", color = Color.White)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sports.forEach { (name, icon) ->
                Button(
                    onClick = { sport = name },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (name == sport) Color(0xFFFFA500) else Color.DarkGray
                    ),
                    shape = MaterialTheme.shapes.large
                ) { Text("$icon $name", color = Color.White) }
            }
        }

        Button(
            onClick = { datePickerDialog.show() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text(if (date.isBlank()) "Select Date" else "Date: $date", color = Color.White)
        }

        Button(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text(if (time.isBlank()) "Select Time" else "Time: $time", color = Color.White)
        }

        OutlinedTextField(
            value = notes, onValueChange = { notes = it },
            label = { Text("Notes") }, maxLines = 3, modifier = Modifier.fillMaxWidth(), colors = fieldColors
        )

        Divider(color = Color.Gray)

        Text("Add Players", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newPlayer, onValueChange = { newPlayer = it },
                label = { Text("Player Name") }, modifier = Modifier.weight(1f), colors = fieldColors
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newPlayer.isNotBlank()) {
                        players.add(newPlayer.trim())
                        newPlayer = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) { Text("Add", color = Color.White) }
        }

        if (players.isNotEmpty()) {
            Text("Add Lines", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newLineName, onValueChange = { newLineName = it },
                    label = { Text("Line Name") }, modifier = Modifier.weight(1f), colors = fieldColors
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newLineName.isNotBlank()) {
                            globalLines.add(newLineName.trim())
                            newLineName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                ) { Text("Add", color = Color.White) }
            }
        }

        if (players.isNotEmpty() && globalLines.isNotEmpty()) {
            Text("Assign Line Values", style = MaterialTheme.typography.titleMedium, color = Color.White)
            players.forEach { player ->
                Text(player, style = MaterialTheme.typography.titleSmall, color = Color(0xFFFFA500))
                globalLines.forEach { line ->
                    val key = player to line
                    val inputValue = valueInputs[key] ?: ""
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(line, color = Color.LightGray, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { valueInputs[key] = it },
                            modifier = Modifier.width(100.dp),
                            label = { Text("Value") },
                            colors = fieldColors,
                            singleLine = true
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val validatedLines = valueInputs.mapNotNull { (key, value) ->
                    val (player, line) = key
                    value.toFloatOrNull()?.let { PlayerLine(player, line, it) }
                }
                val game = Game(
                    title = title,
                    sport = sport,
                    date = date,
                    time = time,
                    notes = notes,
                    players = players.toList(),
                    bettingLines = globalLines.toList(),
                    playerLines = validatedLines
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
