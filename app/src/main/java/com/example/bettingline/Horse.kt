package com.example.bettingline

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Horse(
    val id: Int, // ID from the database
    val name: String,
    val speed: Float,
    val stamina: Float,
    val luck: Float,
    val experience: Float,
    var progress: Float = 0f,  // Progress during the race (not stored in the database)
    val colorHex: String // Store color as hex for consistency with database
) {
    val color: Color
        get() = Color(android.graphics.Color.parseColor(colorHex)) // Convert hex to Color
}