package it.ap.mytemp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Temperature::class], version = 1, exportSchema = false)
abstract class TemperatureRoomDatabase : RoomDatabase() {
    abstract fun temperatureDao(): TemperatureDao

    companion object {
        @Volatile
        private var INSTANCE: TemperatureRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): TemperatureRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TemperatureRoomDatabase::class.java,
                    "temperature_database"
                ).addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.temperatureDao())
                    }
                }
            }

            suspend fun populateDatabase(wordDao: TemperatureDao) {
                // Delete all content here.
                // wordDao.deleteAll()
            }
        }
    }
}