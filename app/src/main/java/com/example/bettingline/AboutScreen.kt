package com.example.bettingline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFFA500)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("About", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        }

        Text(
            text = "Welcome to BettingLine, your all-in-one tool for creating and managing custom betting games.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Spacer(Modifier.height(16.dp))

        Text("With this app, you can:", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFA500))

        Spacer(Modifier.height(8.dp))
        val features = listOf(
            "📝 Create custom games for any sport (MMA, Football, etc.)",
            "🎯 Add players and betting lines to track predictions",
            "📊 Assign target values and track real-time performance",
            "📅 Use calendar and clock tools for game scheduling",
            "📈 Run games live and input real-time stats",
            "📂 Edit, delete, and view your saved games with ease"
        )
        features.forEach {
            Text("• $it", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(16.dp))

        Text("💥 Race Horse Mode", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFA500))
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Jump into a fun simulation where horses race with random speeds—great for casual betting and excitement!",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
