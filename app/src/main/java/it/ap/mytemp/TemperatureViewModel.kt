package it.ap.mytemp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
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