package com.example.bettingline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horses")
data class HorseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val speed: Float,
    val stamina: Float,
    val luck: Float,
    val experience: Float,
    val colorHex: String // Store color as hex like "#FFA500"
)
