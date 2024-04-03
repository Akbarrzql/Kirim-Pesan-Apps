package com.app.kirimpesanapp.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.kirimpesanapp.source.dao.DataRecognitionDao
import com.app.kirimpesanapp.source.model.DataRecognition

@Database(entities = [DataRecognition::class], version = 1, exportSchema = false)
abstract class DataRecognitionDatabase : RoomDatabase() {

    abstract fun dataRecognitionDao(): DataRecognitionDao

    companion object {
        @Volatile
        private var INSTANCE: DataRecognitionDatabase? = null

        fun getDatabase(context: Context): DataRecognitionDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataRecognitionDatabase::class.java,
                    "data_recognition_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}