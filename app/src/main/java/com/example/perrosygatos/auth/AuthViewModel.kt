package com.example.perrosygatos.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


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
    val registrationSuccess: Boolean = false
)

data class LoginState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    // Registration Logic
    fun onFullNameChange(fullName: String) {
        _registerState.update { it.copy(fullName = fullName, fullNameError = validateFullName(fullName)) }
    }

    fun onEmailChange(email: String) {
        _registerState.update { it.copy(email = email, emailError = validateEmail(email)) }
    }

    fun onPasswordChange(password: String) {
        _registerState.update { it.copy(password = password, passwordError = validatePassword(password)) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = validateConfirmPassword(_registerState.value.password, confirmPassword)) }
    }

    fun onPhoneChange(phone: String) {
        _registerState.update { it.copy(phone = phone) }
    }

    fun onAddPet(pet: Pet) {
        _registerState.update { it.copy(pets = it.pets + pet) }
    }

    fun register() {
        val state = _registerState.value
        val fullNameError = validateFullName(state.fullName)
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(state.password, state.confirmPassword)

        _registerState.update {
            it.copy(
                fullNameError = fullNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        if (fullNameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
            // For now, just simulate a successful registration
            _registerState.update { it.copy(registrationSuccess = true) }
        }
    }

    // Login Logic
    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun login() {
        // This is a dummy login validation. In a real app, you'd check against a server or database.
        val registeredUser = _registerState.value // Simulate checking against the registered user
        val currentState = _loginState.value

        if (currentState.email == registeredUser.email && currentState.password == registeredUser.password && registeredUser.registrationSuccess) {
            _loginState.update { it.copy(loginSuccess = true, errorMessage = null) }
        } else {
            _loginState.update { it.copy(errorMessage = "Usuario o contraseña incorrectos") }
        }
    }

    // Validation Functions
    private fun validateFullName(fullName: String): String? {
        if (fullName.isBlank()) return "El nombre no puede estar vacío"
        if (!fullName.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras y espacios"
        if (fullName.length > 50) return "El nombre no puede tener más de 50 caracteres"
        return null
    }

    private fun validateEmail(email: String): String? {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Formato de correo inválido"
        if (!email.endsWith("@duoc.cl")) return "Solo se aceptan correos con dominio @duoc.cl"
        // In a real app, you would check for uniqueness in your user database.
        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (!password.any { it.isUpperCase() }) return "Debe contener al menos una mayúscula"
        if (!password.any { it.isLowerCase() }) return "Debe contener al menos una minúscula"
        if (!password.any { it.isDigit() }) return "Debe contener al menos un número"
        if (password.none { !it.isLetterOrDigit() }) return "Debe contener al menos un caracter especial"
        return null
    }

    private fun validateConfirmPassword(password: String, confirm: String): String? {
        if (password != confirm) return "Las contraseñas no coinciden"
        return null
    }
}