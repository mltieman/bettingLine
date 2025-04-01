package com.example.bettingline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.FilterList




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use the LandingPage as the root composable.
            LandingPage()
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage() {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("landing") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
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
        },
        containerColor = Color.Black,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    "landing" -> {
                        // Landing screen content
                        LandingScreen()
                    }

                    "race" -> {
                        RaceScreen()
                    }
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
                            currentScreen = "landing"
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


@Composable
fun LandingScreen() {
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
                    onClick = { selectedSport = sport },
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No bets created yet.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}