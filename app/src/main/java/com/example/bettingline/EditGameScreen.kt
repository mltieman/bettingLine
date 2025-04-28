package com.example.bettingline

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bettingline.GameData.Game
import com.example.bettingline.GameData.PlayerLine
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import android.app.PendingIntent


@Composable
fun EditGameScreen(
    originalGame: Game,
    onSave: (Game) -> Unit,
    onCancel: () -> Unit
) {
    // --- Game fields ---
    var title by remember { mutableStateOf(originalGame.title) }
    var sport by remember { mutableStateOf(originalGame.sport) }
    var date by remember { mutableStateOf(originalGame.date) }
    var time by remember { mutableStateOf(originalGame.time) }
    var notes by remember { mutableStateOf(originalGame.notes) }

    // --- Players list ---
    var newPlayer by remember { mutableStateOf("") }
    val players = remember { mutableStateListOf(*originalGame.players.toTypedArray()) }

    // --- Lines list ---
    var newLineName by remember { mutableStateOf("") }
    val globalLines = remember { mutableStateListOf(*originalGame.bettingLines.toTypedArray()) }

    // Preserve the original playerLines so we can fall back
    var playerLines by remember { mutableStateOf(originalGame.playerLines) }

    // Track any newly‐entered values
    val valueInputs = remember { mutableStateMapOf<Pair<String, String>, String>() }

    // Toggle between editing players vs. lines
    var editMode by remember { mutableStateOf("Players") }


    // Your existing styling
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = Color(0xFFFFA500),
        unfocusedBorderColor = Color.Gray,
        cursorColor          = Color(0xFFFFA500),
        focusedLabelColor    = Color(0xFFFFA500),
        unfocusedLabelColor  = Color.LightGray,
        focusedTextColor     = Color.White,
        unfocusedTextColor   = Color.White
    )

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Game", style = MaterialTheme.typography.headlineMedium, color = Color.White)

        // --- Game info fields ---
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )
        OutlinedTextField(
            value = sport,
            onValueChange = { sport = it },
            label = { Text("Sport") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )
        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        date = "${year}-${month + 1}-${day}"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Pick Date: ${if (date.isBlank()) "None" else date}", color = Color.White)
        }

        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        time = String.format("%02d:%02d", hour, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Pick Time: ${if (time.isBlank()) "None" else time}", color = Color.White)
        }
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )

        Divider(color = Color.Gray)

        // --- Edit mode buttons ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { editMode = "Players" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (editMode == "Players") Color(0xFFFFA500) else Color.DarkGray
                )
            ) {
                Text("Edit Players", color = Color.White)
            }
            Button(
                onClick = { editMode = "Lines" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (editMode == "Lines") Color(0xFFFFA500) else Color.DarkGray
                )
            ) {
                Text("Edit Lines", color = Color.White)
            }
        }

        // --- Players editing UI ---
        if (editMode == "Players") {
            Text("Players", color = Color.White, style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newPlayer,
                    onValueChange = { newPlayer = it },
                    label = { Text("New Player") },
                    modifier = Modifier.weight(1f),
                    colors = fieldColors
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val np = newPlayer.trim()
                        if (np.isNotEmpty() && !players.contains(np)) {
                            players.add(np)
                            newPlayer = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                ) {
                    Text("Add", color = Color.White)
                }
            }

            players.forEach { player ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(player, color = Color.White, modifier = Modifier.weight(1f))
                    Button(
                        onClick = { players.remove(player) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }
            }
        }

        // --- Lines editing & value‐assignment UI ---
        if (editMode == "Lines") {
            Text("Lines", color = Color.White, style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newLineName,
                    onValueChange = { newLineName = it },
                    label = { Text("New Line") },
                    modifier = Modifier.weight(1f),
                    colors = fieldColors
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val nl = newLineName.trim()
                        if (nl.isNotEmpty() && !globalLines.contains(nl)) {
                            globalLines.add(nl)
                            newLineName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                ) {
                    Text("Add", color = Color.White)
                }
            }

            globalLines.forEach { line ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(line, color = Color.White, modifier = Modifier.weight(1f))
                    Button(
                        onClick = { globalLines.remove(line) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }
            }

            if (players.isNotEmpty() && globalLines.isNotEmpty()) {
                Text("Assign Line Values", color = Color.White)
                players.forEach { player ->
                    Text(player, style = MaterialTheme.typography.titleSmall, color = Color(0xFFFFA500))
                    globalLines.forEach { line ->
                        val key = player to line
                        val existing = playerLines
                            .find { it.player == player && it.lineName == line }
                            ?.value
                            ?.toString()
                        val inputValue = valueInputs.getOrPut(key) { existing.orEmpty() }

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
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- SAVE button: save and navigate back to home ---
        Button(
            onClick = {

                //cancel old reminders
                val origMs = parseGameDateTime("${originalGame.date} ${originalGame.time}")
                cancelNotification(context, origMs - 24L*60*60*1000)
                cancelNotification(context, origMs - 30L*60*1000)
                // build updated playerLines in the same order as UI
                val updatedPL = mutableListOf<PlayerLine>()
                players.forEach { p ->
                    globalLines.forEach { l ->
                        val key = p to l
                        val raw = valueInputs[key]
                        val value = raw?.toFloatOrNull()
                            ?: playerLines.find { it.player == p && it.lineName == l }?.value
                        if (value != null) {
                            val oldLive = playerLines.find { it.player == p && it.lineName == l }?.liveValue ?: 0f
                            updatedPL.add(PlayerLine(p, l, value, oldLive))

                        }
                    }
                }
                val updatedGame = Game(
                    title = title,
                    sport = sport,
                    date = date,
                    time = time,
                    notes = notes,
                    players = players.toList(),
                    bettingLines = globalLines.toList(),
                    playerLines = updatedPL
                )
                val now = System.currentTimeMillis()
                val updMs = parseGameDateTime("$date $time")
                val oneDay = updMs - 24L*60*60*1000
                val thirtyM = updMs - 30L*60*1000
                if (oneDay>now)    scheduleNotification(context, oneDay,    "Game Tomorrow!", "Don't forget: ${updatedGame.title}")
                if (thirtyM>now)   scheduleNotification(context, thirtyM,   "Game Soon!",     "Get ready: ${updatedGame.title} starts soon!")

                onSave(updatedGame)
                onCancel()
            },
            Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("Save", color = Color.White)
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Cancel")
        }
    }
}

// Cancels an alarm previously set with scheduleNotification
fun cancelNotification(context: Context, triggerTimeMillis: Long) {
    val intent = Intent(context, GameReminderReceiver::class.java)
    val pi = PendingIntent.getBroadcast(
        context,
        (triggerTimeMillis % Int.MAX_VALUE).toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.cancel(pi)
}