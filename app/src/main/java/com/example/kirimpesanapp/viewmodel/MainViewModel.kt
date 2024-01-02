package com.example.kirimpesanapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kirimpesanapp.preferences.ThemePreferences

class MainViewModel(private val pref: ThemePreferences) : ViewModel(){
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }
}