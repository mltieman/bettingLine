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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LegalScreen(onBack: () -> Unit) {
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

        // Legal Content
        Text(
            text = "Legal Notice",
            color = Color(0xFFFFA500),
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = """
This application is provided for entertainment purposes only. It allows users to create custom betting lines, simulate race outcomes, and manage fantasy-based game data. No real money is involved, and this app does not facilitate or encourage real-world gambling.

**1. No Gambling or Real Money Transactions**
This app does not support any real-money betting or wagering. All simulated bets, odds, and race outcomes are fictional and hold no monetary value. Users must not use the app to conduct or promote gambling activity.

**2. No Liability**
The creators and maintainers of this app are not responsible for any decisions users make outside the app influenced by its content or simulations. The app is a tool for fun, strategy development, and data management only.

**3. Intellectual Property**
All code, UI components, icons, and race simulations are property of the app developers unless otherwise attributed. You may not reproduce or distribute any portion of the app without explicit permission.

**4. User Data**
The app does not collect or transmit personal user data. All saved data is stored locally on the user's device. By using the app, you acknowledge that you are responsible for the security of your own information.

**5. Modifications**
The app and its terms are subject to change at any time without notice. Continued use of the app after updates implies agreement with the new terms.

If you have any questions about this legal disclaimer, please contact the developers through the contact page.
""".trimIndent(),
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}
