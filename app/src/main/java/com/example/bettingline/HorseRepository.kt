package com.example.bettingline

import kotlinx.coroutines.flow.Flow

class HorseRepository(private val horseDao: HorseDao) {
    val horses: Flow<List<Horse>> = horseDao.getAllHorses()

    suspend fun insertHorse(horse: Horse) {
        horseDao.insertHorse(horse)
    }

    suspend fun deleteHorse(horse: Horse) {
        horseDao.deleteHorse(horse)
    }
}