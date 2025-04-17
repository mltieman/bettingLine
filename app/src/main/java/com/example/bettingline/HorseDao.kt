package com.example.bettingline

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HorseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHorse(horse: Horse)

    @Delete
    suspend fun deleteHorse(horse: Horse)

    @Query("SELECT * FROM horses")
    fun getAllHorses(): Flow<List<Horse>>
}