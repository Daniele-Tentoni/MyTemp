package it.ap.mytemp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.ap.mytemp.data.TemperatureRoomDatabase
import it.ap.mytemp.data.models.Temperature
import it.ap.mytemp.data.models.TemperatureDao
import it.ap.mytemp.repos.TemperaturesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TemperatureViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TemperaturesRepository
    val allTemperatures: LiveData<List<Temperature>>

    init {
        val temperatureDao: TemperatureDao = TemperatureRoomDatabase.getDatabase(application, viewModelScope).temperatureDao()
        repository = TemperaturesRepository(temperatureDao)
        allTemperatures = repository.allTemperatures
    }

    fun insert(temperature: Temperature) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(temperature)
    }
}