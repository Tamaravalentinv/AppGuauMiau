package com.example.perrosygatos.data.repository

import com.example.perrosygatos.data.datastore.UserDataStore
import com.example.perrosygatos.data.model.LoginRequest
import com.example.perrosygatos.data.model.User
import com.example.perrosygatos.data.network.AuthService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val userDataStore: UserDataStore
) {

    suspend fun checkSession(): Boolean {
        return userDataStore.sessionToken.first() != null
    }

    suspend fun logout() {
        userDataStore.clearSessionToken()
    }

    suspend fun registerUser(user: User): Result<Boolean> {
        return try {
            val response = authService.register(user)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error de registro (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(credentials: LoginRequest): Result<User> {
        return try {
            val response = authService.login(credentials)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    // Usamos el ID del usuario como token de sesión
                    val sessionToken = user.id?.toString() ?: "session_active"
                    userDataStore.saveSessionToken(sessionToken)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Respuesta de login vacía."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error de credenciales (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
