// LandingPage.kt
package com.example.bettingline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LandingPage() {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("home") }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedGame by remember { mutableStateOf<GameData.Game?>(null) }
    var gameToEdit by remember { mutableStateOf<GameData.Game?>(null) }

    Scaffold(
        topBar = {
            if (currentScreen != "viewGame" && currentScreen != "editGame"&& currentScreen != "settings" && currentScreen != "about" && currentScreen != "legal" && currentScreen != "contact") {
                TopAppBar(
                    title = { Text("Betting App", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFFFFA500))
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black)
                )
            }
        },
        containerColor = Color.Black,
        content = { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                var selectedSport by remember { mutableStateOf("Custom") }
                val games = remember { mutableStateListOf<GameData.Game>() }

                LaunchedEffect(Unit) {
                    GameStorage.getGames(context).collect { saved ->
                        games.clear()
                        games.addAll(saved)
                    }
                }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        games = games,
                        onSportSelected = { sport ->
                            selectedSport = sport
                            currentScreen = "createGame"
                        },
                        onDeleteGame = { game ->
                            games.removeAll { it.id == game.id } // ✅ precise deletion
                            scope.launch { GameStorage.saveGames(context, games) }
                        },

                                onViewGame = {
                            selectedGame = it
                            currentScreen = "viewGame"
                        },
                        onEdit = {
                            gameToEdit = it
                            currentScreen = "editGame"
                        }
                    )

                    "createGame" -> CreateGameScreen(
                        selectedSport = selectedSport,
                        onGameCreated = { newGame ->
                            games.add(newGame)
                            games.sortBy { "${it.date} ${it.time}" }
                            scope.launch { GameStorage.saveGames(context, games) }
                            currentScreen = "home"
                        }
                    )

                    "viewGame" -> selectedGame?.let { game ->
                        ViewGameScreen(
                            game = game,
                            games = games,
                            onBack = {
                                currentScreen = "home"
                                selectedGame = null
                            },
                            onEdit = {
                                gameToEdit = it
                                currentScreen = "editGame"
                            }
                        )
                    }

                    "editGame" -> gameToEdit?.let { game ->
                        EditGameScreen(
                            originalGame = game,

                            onSave = { updatedGame ->
                                // 1) update list & persist
                                val idx = games.indexOfFirst {
                                    it.title==game.title && it.date==game.date && it.time==game.time
                                }
                                if (idx != -1) {
                                    games[idx] = updatedGame
                                    scope.launch { GameStorage.saveGames(context, games) }
                                }
                            },
                            onCancel = {
                                gameToEdit = null
                                currentScreen = "race"
                            },
                        )
                    }
                    "settings" -> SettingsScreen(
                        onBack = { currentScreen = "home" },
                        onNavigateToAbout = { currentScreen = "about" },
                        onNavigateToLegal = { currentScreen = "legal" },
                        onNavigateToContact = { currentScreen = "contact" },
                        )
                    "about" -> AboutScreen(
                        onBack = { currentScreen = "settings" }
                    )

                    "legal" -> LegalScreen(
                        onBack = { currentScreen = "settings" }
                    )
                    "contact" -> ContactScreen(
                        onBack = { currentScreen = "settings" }
                    )





                    "race" -> RaceScreen()
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(0.dp, 56.dp),
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.9f))
                ) {
                    DropdownMenuItem(
                        text = { Text("Home", color = Color.White) },
                        onClick = {
                            currentScreen = "home"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Race Horses", color = Color.White) },
                        onClick = {
                            currentScreen = "race"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings", color = Color.White) }, // ⚙️ New Settings Option
                        onClick = {
                            currentScreen = "settings"
                            expanded = false
                        }
                    )
                }
            }
        }
    )
}
