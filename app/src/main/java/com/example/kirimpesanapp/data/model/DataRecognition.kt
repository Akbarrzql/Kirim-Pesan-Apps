package com.example.kirimpesanapp.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_recognition")
data class DataRecognition(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val emailUser: String,
    val phoneNumber: String,
    val dateRecognition: String,
    val imageUri: String
)