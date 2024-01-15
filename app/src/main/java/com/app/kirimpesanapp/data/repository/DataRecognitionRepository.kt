package com.app.kirimpesanapp.data.repository

import androidx.lifecycle.LiveData
import com.app.kirimpesanapp.data.model.DataRecognition
import com.app.kirimpesanapp.data.dao.DataRecognitionDao

class DataRecognitionRepository(private val dataRecognitionDao: DataRecognitionDao) {

    val getAllDataRecognition: LiveData<List<DataRecognition>> = dataRecognitionDao.getAllDataRecognition()

    suspend fun insertDataRecognition(dataRecognition: DataRecognition) {
        dataRecognitionDao.insertDataRecognition(dataRecognition)
    }

    suspend fun deleteShortcut(dataRecognition: DataRecognition) {
        dataRecognitionDao.deleteShortcut(dataRecognition)
    }

    suspend fun deleteAll() {
        dataRecognitionDao.deleteAll()
    }
}