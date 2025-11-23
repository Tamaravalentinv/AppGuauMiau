package com.example.perrosygatos.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class UserDataStoreImpl(private val context: Context) : UserDataStore {

    companion object {
        private val SESSION_TOKEN_KEY = stringPreferencesKey("session_token")
    }

    override val sessionToken: Flow<String?> = context.dataStore.data.map {
        it[SESSION_TOKEN_KEY]
    }

    override suspend fun saveSessionToken(token: String) {
        context.dataStore.edit {
            it[SESSION_TOKEN_KEY] = token
        }
    }

    override suspend fun clearSessionToken() {
        context.dataStore.edit {
            it.remove(SESSION_TOKEN_KEY)
        }
    }
}