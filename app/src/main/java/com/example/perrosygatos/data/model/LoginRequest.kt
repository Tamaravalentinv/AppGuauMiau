package com.example.perrosygatos.data.model

/**
 * Data class que representa la petición para el endpoint de login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)
