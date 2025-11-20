package com.example.perrosygatos.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore(private val context: Context) {

    private val sessionTokenKey = stringPreferencesKey("session_token")

    val sessionToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[sessionTokenKey]
        }

    suspend fun saveSessionToken(token: String) {
        context.dataStore.edit {
            it[sessionTokenKey] = token
        }
    }

    suspend fun clearSessionToken() {
        context.dataStore.edit {
            it.remove(sessionTokenKey)
        }
    }
}
