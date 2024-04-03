package com.app.kirimpesanapp.viewmodel.data_recognition

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.kirimpesanapp.source.DataRecognitionDatabase
import com.app.kirimpesanapp.source.model.DataRecognition
import com.app.kirimpesanapp.source.repository.DataRecognitionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataRecognitionViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<DataRecognition>>
    private val repository: DataRecognitionRepository

    init {
        val dataRecognitionDao = DataRecognitionDatabase.getDatabase(application).dataRecognitionDao()
        repository = DataRecognitionRepository(dataRecognitionDao)
        readAllData = repository.getAllDataRecognition
    }

    fun insertDataRecognition(dataRecognition: DataRecognition) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDataRecognition(dataRecognition)
        }
    }

    fun deleteShortcut(dataRecognition: DataRecognition) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteShortcut(dataRecognition)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}