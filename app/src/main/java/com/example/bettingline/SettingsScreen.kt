package com.example.bettingline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToContact: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Manual Back Button
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

        SettingButton("About", onClick = onNavigateToAbout)
        Divider(color = Color.DarkGray, thickness = 1.dp)
        SettingButton("Legal", onClick = onNavigateToLegal)
        Divider(color = Color.DarkGray, thickness = 1.dp)
        SettingButton("Contact", onClick = onNavigateToContact)
    }
}

@Composable
fun SettingButton(text: String, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        shape = RectangleShape
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium)
    }

}
