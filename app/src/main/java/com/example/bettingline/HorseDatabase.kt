package com.example.bettingline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Horse::class], version = 1, exportSchema = false)
abstract class HorseDatabase : RoomDatabase() {

    abstract fun horseDao(): HorseDao

    companion object {
        @Volatile
        private var INSTANCE: HorseDatabase? = null

        fun getDatabase(context: Context): HorseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HorseDatabase::class.java,
                    "horse_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
