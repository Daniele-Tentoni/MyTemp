package it.ap.mytemp.models

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ap.mytemp.models.Temperature

@Dao
interface TemperatureDao {
    @Query("SELECT *  FROM temperatures ORDER BY id DESC")
    fun getTemperatures(): LiveData<List<Temperature>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(temp: Temperature)

    @Query("DELETE FROM temperatures")
    suspend fun deleteAll()

    @Query("DELETE FROM temperatures WHERE 'temp' < 32 AND 'temp' > 42")
    suspend fun cleanFalseTemperatures()
}