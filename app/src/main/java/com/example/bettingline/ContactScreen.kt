package com.example.bettingline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Back Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onBack() }
                .padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFA500),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }

        // Header
        Text(
            text = "Contact the Developers",
            color = Color(0xFFFFA500),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dev 1
        ContactCard(
            name = "Keshawn Blakely",
            email = "Keshawn7b@gmail.com",
            github = "Keshawn7B"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dev 2
        ContactCard(
            name = "Matthew Tieman",
            email = "matthewtieman2@gmail.com",
            github = "mltieman"
        )
    }
}

@Composable
fun ContactCard(name: String, email: String, github: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text("Name: $name", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("Email: $email", color = Color.White, fontSize = 14.sp)
        Text("GitHub: $github", color = Color(0xFFFFA500), fontSize = 14.sp)
    }
}
