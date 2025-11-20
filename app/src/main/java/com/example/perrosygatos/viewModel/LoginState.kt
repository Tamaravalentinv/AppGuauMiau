package com.example.perrosygatos.viewModel

/**
 * Data class que representa el estado de la UI relacionado con la sesión del usuario y la pantalla de login.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false, // <-- Propiedad añadida y corregida
    val sessionActive: Boolean? = null, // Para el arranque de la app
    val isLoading: Boolean = false
)
