package com.app.kirimpesanapp.source.repository

import androidx.lifecycle.LiveData
import com.app.kirimpesanapp.source.model.DataRecognition
import com.app.kirimpesanapp.source.dao.DataRecognitionDao

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