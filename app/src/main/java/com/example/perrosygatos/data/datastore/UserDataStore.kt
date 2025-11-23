package com.example.perrosygatos.data.datastore

import kotlinx.coroutines.flow.Flow

// Interfaz que usa Hilt para inyectar en los Repositorios
interface UserDataStore {
    val sessionToken: Flow<String?>
    suspend fun saveSessionToken(token: String)
    suspend fun clearSessionToken()
}