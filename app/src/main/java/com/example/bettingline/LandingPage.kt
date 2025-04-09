package com.example.bettingline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.AlignmentLine
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collect
import androidx.compose.ui.platform.LocalContext




@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LandingPage() {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("home") }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedGame by remember { mutableStateOf<GameData.Game?>(null) }



    Scaffold(
        topBar = {
            if (currentScreen != "viewGame") {
            TopAppBar(
                title = { Text("Betting App", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFFFFA500)
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black)
            )
                }
        },
        containerColor = Color.Black,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
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
                            games.removeAll {
                                it.title == game.title && it.date == game.date && it.time == game.time
                            }
                            scope.launch {
                                GameStorage.saveGames(context, games)
                            }
                        },
                        onViewGame = { game ->
                            selectedGame = game
                            currentScreen = "viewGame"
                        }
                    )

                    "createGame" -> CreateGameScreen(
                        selectedSport = selectedSport,
                        onGameCreated = { newGame ->
                            games.add(newGame)
                            games.sortBy { "${it.date} ${it.time}" }
                            scope.launch {
                                GameStorage.saveGames(context, games)
                            }
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
                                // TODO: Navigate to CreateGameScreen or EditGameScreen pre-filled
                            }
                        )
                    }

                    "race" -> RaceScreen()
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(0.dp, 56.dp),
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .align(Alignment.TopStart)
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
                }
            }
        }
    )
}
