package it.ap.mytemp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.ap.mytemp.data.models.Temperature
import it.ap.mytemp.data.models.TemperatureDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Temperature::class], version = 2, exportSchema = false)
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

            val MIGRATION_1_2: Migration = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE temperatures ADD COLUMN cough INTEGER")
                    database.execSQL("ALTER TABLE temperatures ADD COLUMN cold INTEGER")
                    database.execSQL("ALTER TABLE temperatures ADD COLUMN notes TEXT")
                }
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TemperatureRoomDatabase::class.java,
                    "temperature_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(TemperatureDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private class TemperatureDatabaseCallback(
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

            suspend fun populateDatabase(temperatureDao: TemperatureDao) {
                // Delete all content here.
                temperatureDao.cleanFalseTemperatures()
            }
        }
    }
}