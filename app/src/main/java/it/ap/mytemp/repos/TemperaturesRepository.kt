package it.ap.mytemp.repos

import androidx.lifecycle.LiveData
import it.ap.mytemp.data.models.Temperature
import it.ap.mytemp.data.models.TemperatureDao

class TemperaturesRepository(private val temperatureDao: TemperatureDao) {
    val allTemperatures: LiveData<List<Temperature>> = temperatureDao.getTemperatures()

    suspend fun insert(temp: Temperature) {
        temperatureDao.insert(temp)
    }
}