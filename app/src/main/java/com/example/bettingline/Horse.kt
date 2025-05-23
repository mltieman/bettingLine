package com.example.bettingline

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horses")
data class Horse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val speed: Float,
    val stamina: Float,
    val luck: Float,
    val experience: Float,
    val colorHex: String,
)
