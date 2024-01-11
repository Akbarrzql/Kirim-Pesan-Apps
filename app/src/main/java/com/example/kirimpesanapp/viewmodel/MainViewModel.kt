package com.example.kirimpesanapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kirimpesanapp.preferences.AuthPreferences
import com.example.kirimpesanapp.preferences.ThemePreferences

class MainViewModel(private val pref: ThemePreferences, private val authPreferences: AuthPreferences) : ViewModel(){
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun getAuthToken(): LiveData<String?> {
        return authPreferences.getAuthToken().asLiveData()
    }

    fun getUserName(): LiveData<String?> {
        return authPreferences.getUserName().asLiveData()
    }
}