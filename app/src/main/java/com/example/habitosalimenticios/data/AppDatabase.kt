package com.example.habitosalimenticios.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Encuestado::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun encuestadoDao(): EncuestadoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tnt_survey.db"
                )
                    .fallbackToDestructiveMigration() // SOLO desarrollo; en prod usar migraciones
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
