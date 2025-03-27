package com.example.bettingline

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Horse(
    val name: String,
    val speed: Float,
    val stamina: Float,
    val luck: Float,  // 0.0 (unlucky) to 1.0 (very lucky)
    val experience: Float,
    val progress: MutableState<Float> = mutableFloatStateOf(0f),
    val color: Color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
)
