// File: GameStorage.kt
package com.example.bettingline

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Extension to access DataStore
val Context.gameDataStore by preferencesDataStore(name = "game_datastore")

object GameStorage {
    private val GAME_LIST_KEY = stringPreferencesKey("game_list")

    fun getGames(context: Context): Flow<List<GameData.Game>> {
        return context.gameDataStore.data.map { preferences ->
            val json = preferences[GAME_LIST_KEY]
            if (json.isNullOrEmpty()) emptyList()
            else Json.decodeFromString<List<GameData.Game>>(json) // ✅ specify the type
        }
    }

    suspend fun saveGames(context: Context, games: List<GameData.Game>) {
        context.gameDataStore.edit { preferences ->
            preferences[GAME_LIST_KEY] = Json.encodeToString(games)
        }
    }

}


