package com.example.perrosygatos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perrosygatos.data.model.LoginRequest
import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.data.model.User
import com.example.perrosygatos.data.repository.AuthRepository
import com.example.perrosygatos.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val petRepository: PetRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _petState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val petState = _petState.asStateFlow()

    private val _userEvents = MutableSharedFlow<String>()
    val userEvents = _userEvents.asSharedFlow()

    private suspend fun sendEvent(message: String) {
        _userEvents.emit(message)
    }

    fun onAppStart() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (authRepository.checkSession()) {
                    _loginState.update { it.copy(sessionActive = true) }
                    loadPets()
                } else {
                    _loginState.update { it.copy(sessionActive = false) }
                }
            } catch (e: Exception) {
                _loginState.update { it.copy(sessionActive = false) }
            }
        }
    }

    fun loadPets() {
        viewModelScope.launch(Dispatchers.IO) {
            _petState.value = PetUiState.Loading
            petRepository.getPets().onSuccess {
                _petState.value = PetUiState.Success(it)
            }.onFailure {
                _petState.value = PetUiState.Error(it.message ?: "Error al cargar mascotas")
            }
        }
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch(Dispatchers.IO) {
            _petState.value = PetUiState.Loading
            try {
                petRepository.addPet(pet).onSuccess {
                    sendEvent("Mascota agregada con éxito")
                    loadPets()
                }.onFailure { exception ->
                    sendEvent(exception.message ?: "No se pudo agregar la mascota")
                    loadPets()
                }
            } catch (e: Exception) {
                sendEvent("Error inesperado: ${e.localizedMessage}")
                loadPets()
            }
        }
    }

    fun deletePet(petId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // ... (Lógica de borrar mascota)
        }
    }

    // --- Registration Logic ---
    fun onFullNameChange(fullName: String) {
        _registerState.update { it.copy(fullName = fullName) }
    }

    fun onEmailChange(email: String) {
        _registerState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _registerState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onPhoneChange(phone: String) {
        _registerState.update { it.copy(phone = phone) }
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = registerState.value

            // VALIDACIONES LOCALES
            if (state.fullName.isBlank()) {
                sendEvent("Ingresa tu nombre completo")
                return@launch
            }
            if (state.email.isBlank()) {
                sendEvent("Ingresa un correo válido")
                return@launch
            }
            if (state.password.isBlank()) {
                sendEvent("Ingresa una contraseña")
                return@launch
            }
            if (state.password != state.confirmPassword) {
                sendEvent("Las contraseñas no coinciden")
                return@launch
            }

            try {
                _registerState.update { it.copy(isLoading = true) }
                // Construir el objeto User REAL para enviarlo al backend
                val user = User(
                    id = 0, // El ID lo genera el backend
                    name = state.fullName,
                    email = state.email,
                    password = state.password,
                    phone = state.phone,
                    pets = emptyList() // Las mascotas se añaden después
                )

                // Llamada al repositorio
                val result = authRepository.registerUser(user)

                if (result.isSuccess) {
                    sendEvent("Usuario registrado correctamente 🎉")
                    // Limpiar formulario y marcar éxito
                    _registerState.update { RegisterState(registrationSuccess = true) }

                } else {
                    sendEvent(result.exceptionOrNull()?.message ?: "Error desconocido al registrar")
                }

            } catch (e: Exception) {
                sendEvent("Error inesperado: ${e.localizedMessage}")
            } finally {
                _registerState.update { it.copy(isLoading = false) }
            }
        }
    }

    // --- Login Logic ---
    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            // ... (Lógica de login)
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            // ... (Lógica de logout)
        }
    }
}
