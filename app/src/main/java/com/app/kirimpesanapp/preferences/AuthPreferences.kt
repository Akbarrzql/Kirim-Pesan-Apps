package com.app.kirimpesanapp.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.authStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")
class AuthPreferences private constructor(private val authStore: DataStore<Preferences>){

    private val AUTH_KEY = stringPreferencesKey("auth_token")
    private val USERNAME_KEY = stringPreferencesKey("username")

    fun getAuthToken(): Flow<String?>{
        return authStore.data.map { preferences ->
            preferences[AUTH_KEY]
        }
    }

    suspend fun saveAuthToken(authToken: String){
        authStore.edit { preferences ->
            preferences[AUTH_KEY] = authToken
        }
    }

    fun getUserName(): Flow<String?>{
        return authStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }
    }

    suspend fun saveUserName(userName: String){
        authStore.edit { preferences ->
            preferences[USERNAME_KEY] = userName
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(authStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(authStore)
                INSTANCE = instance
                instance
            }
        }
    }
}