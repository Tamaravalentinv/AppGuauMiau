package com.example.perrosygatos.viewModel

import com.example.perrosygatos.data.model.Pet

/**
 * Data class que representa el estado de la pantalla de Registro.
 */
data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",
    val pets: List<Pet> = emptyList(),
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val registrationSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
    // Se ha eliminado el campo userMessage para usar un SharedFlow en su lugar.
)
