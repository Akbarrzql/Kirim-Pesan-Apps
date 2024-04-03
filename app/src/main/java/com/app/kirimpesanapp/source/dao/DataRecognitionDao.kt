package com.app.kirimpesanapp.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.kirimpesanapp.source.model.DataRecognition

@Dao
interface DataRecognitionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDataRecognition(dataRecognition: DataRecognition)

    @Query("SELECT * FROM data_recognition ORDER BY id ASC")
    fun getAllDataRecognition(): LiveData<List<DataRecognition>>

    @Delete
    suspend fun deleteShortcut(dataRecognition: DataRecognition)

    @Query("DELETE FROM data_recognition")
    suspend fun deleteAll()
}