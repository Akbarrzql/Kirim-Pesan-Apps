package com.app.kirimpesanapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.kirimpesanapp.preferences.AuthPreferences
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: AuthPreferences) : ViewModel() {

    fun getAuthToken(): LiveData<String?> {
        return pref.getAuthToken().asLiveData()
    }

    fun saveAuthToken(authToken: String) {
        viewModelScope.launch {
            pref.saveAuthToken(authToken)
        }
    }

    fun getUserName(): LiveData<String?> {
        return pref.getUserName().asLiveData()
    }

    fun saveUserName(userName: String) {
        viewModelScope.launch {
            pref.saveUserName(userName)
        }
    }

}