package it.ap.mytemp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TemperatureDao {
    @Query("SELECT *  FROM temperatures ORDER BY id DESC")
    fun getTemperatures(): LiveData<List<Temperature>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(temp: Temperature)

    @Query("DELETE FROM temperatures")
    suspend fun deleteAll()
}