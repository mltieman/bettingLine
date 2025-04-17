package com.example.bettingline

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color

data class HorseWithState(
    val id: Int = 0,
    val name: String,
    val speed: Float,
    val stamina: Float,
    val luck: Float,
    val experience: Float,
    val colorHex: String,
    val progress: MutableFloatState = mutableFloatStateOf(0f)
) {
    val color: Color
        get() = try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color.Red
        }
}