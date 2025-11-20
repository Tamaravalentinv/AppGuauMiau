package com.example.perrosygatos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perrosygatos.data.datastore.UserDataStore
import com.example.perrosygatos.data.model.LoginRequest
import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.data.model.User
import com.example.perrosygatos.data.repository.AuthRepository
import com.example.perrosygatos.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// La definición de PetUiState se ha movido a su propio archivo (PetUiState.kt) para evitar redeclaraciones.

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val petRepository: PetRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _petState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val petState = _petState.asStateFlow()

    fun onAppStart() {
        viewModelScope.launch {
            if (authRepository.checkSession()) {
                _loginState.update { it.copy(sessionActive = true) }
                loadPets()
            } else {
                _loginState.update { it.copy(sessionActive = false) }
            }
        }
    }

    fun loadPets() {
        viewModelScope.launch {
            _petState.value = PetUiState.Loading
            petRepository.getPets().onSuccess {
                _petState.value = PetUiState.Success(it)
            }.onFailure {
                _petState.value = PetUiState.Error(it.message ?: "Error al cargar mascotas")
            }
        }
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            _petState.value = PetUiState.Loading // Indica que se está cargando
            petRepository.addPet(pet).onSuccess {
                loadPets() // Recarga la lista si tiene éxito
            }.onFailure { exception ->
                _petState.value = PetUiState.Error(exception.message ?: "Error al añadir mascota")
            }
        }
    }

    fun deletePet(petId: Long) {
        viewModelScope.launch {
            _petState.value = PetUiState.Loading // Indica que se está cargando
            petRepository.deletePet(petId).onSuccess {
                loadPets() // Recarga la lista si tiene éxito
            }.onFailure { exception ->
                _petState.value = PetUiState.Error(exception.message ?: "Error al eliminar mascota")
            }
        }
    }

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
        // ... (validaciones)
        if (state.fullNameError == null && state.emailError == null && state.passwordError == null && state.confirmPasswordError == null) {
            viewModelScope.launch {
                _registerState.update { it.copy(isLoading = true, errorMessage = null) }
                val user = User(id = 0, name = state.fullName, email = state.email, password = state.password, phone = state.phone, pets = state.pets)
                authRepository.registerUser(user).onSuccess {
                    _registerState.update { it.copy(registrationSuccess = true, isLoading = false) }
                }.onFailure { exception ->
                    _registerState.update { it.copy(errorMessage = exception.message ?: "Error de registro", isLoading = false) }
                }
            }
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
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            val credentials = LoginRequest(email = _loginState.value.email, password = _loginState.value.password)
            authRepository.loginUser(credentials).onSuccess {
                // La lógica de guardar el token ya está en el repositorio
                _loginState.update { state -> state.copy(loginSuccess = true, isLoading = false, sessionActive = true) }
                loadPets()
            }.onFailure { exception ->
                _loginState.update { it.copy(errorMessage = exception.message ?: "Usuario o contraseña incorrectos", isLoading = false) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.update { LoginState(sessionActive = false) }
        }
    }

    // Validation Functions
    private fun validateFullName(fullName: String): String? {
        return if (fullName.isBlank()) "El nombre no puede estar vacío" else null
    }
    private fun validateEmail(email: String): String? {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Formato de correo inválido" else null
    }
    private fun validatePassword(password: String): String? {
        return if (password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null
    }
    private fun validateConfirmPassword(password: String, confirm: String): String? {
        return if (password != confirm) "Las contraseñas no coinciden" else null
    }
}
