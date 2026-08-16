package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.IntercomEntry

@Database(
    entities = [IntercomEntry::class],
    version = 1,
    exportSchema = false
)
abstract class RccDatabase : RoomDatabase() {

    abstract fun intercomDao(): IntercomDao

    companion object {
        @Volatile
        private var INSTANCE: RccDatabase? = null

        fun getDatabase(context: Context): RccDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RccDatabase::class.java,
                    "rcc2000_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
