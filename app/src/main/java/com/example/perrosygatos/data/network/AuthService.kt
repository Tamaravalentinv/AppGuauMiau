package com.example.perrosygatos.data.network // <-- CORREGIDO

import com.example.perrosygatos.data.model.LoginRequest
import com.example.perrosygatos.data.model.LoginResponse
import com.example.perrosygatos.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz que define los endpoints de la API para autenticación.
 * Retrofit usará esta interfaz para generar el código de red.
 */
interface AuthService {

    @POST("auth/register")
    suspend fun register(@Body user: User): Response<Void>

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>
}