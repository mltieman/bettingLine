package com.example.bettingline
import kotlinx.serialization.Serializable

class GameData {
    @Serializable

    data class Game(
        val title: String,
        val sport: String,
        val date: String,   // format: YYYY-MM-DD
        val time: String,   // format: HH:MM AM/PM
        val notes: String,
        val bettingLines: List<String>
    )

}