package it.ap.mytemp

import androidx.lifecycle.LiveData
import it.ap.mytemp.models.Temperature
import it.ap.mytemp.models.TemperatureDao

class TemperaturesRepository(private val temperatureDao: TemperatureDao) {
    val allTemperatures: LiveData<List<Temperature>> = temperatureDao.getTemperatures()

    suspend fun insert(temp: Temperature) {
        temperatureDao.insert(temp)
    }
}