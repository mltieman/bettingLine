package com.example.bettingline

import kotlinx.serialization.Serializable
import java.util.UUID

object GameData {

    @Serializable
    data class PlayerLine(
        val player: String,
        val lineName: String,
        val value: Float,        // static target value (edit/create only)
        val liveValue: Float = 0f  // running value (view only)
    )


    @Serializable
    data class Game(
        val id: String = UUID.randomUUID().toString(),
        val title: String,
        val sport: String,
        val date: String,
        val time: String,
        val notes: String = "",

        // Shared across all players
        val bettingLines: List<String> = emptyList(),  // e.g., ["Points", "Rebounds"]

        val players: List<String> = emptyList(),
        val playerLines: List<PlayerLine> = emptyList(),
        val lineValues: Map<String, Float> = emptyMap()
    )


}
