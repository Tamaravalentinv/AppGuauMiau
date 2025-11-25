package com.example.perrosygatos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perrosygatos.data.model.LoginRequest
import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.data.model.User
import com.example.perrosygatos.data.repository.AuthRepository
import com.example.perrosygatos.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val petRepository: PetRepository,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
        viewModelScope.launch(ioDispatcher) {
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
        viewModelScope.launch(ioDispatcher) {
            _petState.value = PetUiState.Loading
            petRepository.getPets().onSuccess {
                _petState.value = PetUiState.Success(it)
            }.onFailure {
                _petState.value = PetUiState.Error(it.message ?: "Error al cargar mascotas")
            }
        }
    }

    // Agrega una mascota al backend directamente (Para PetManagementScreen)
    fun addPet(pet: Pet) {
        viewModelScope.launch(ioDispatcher) {
            _petState.value = PetUiState.Loading
            try {
                // Nos aseguramos de enviar ID null para crear
                val newPet = pet.copy(id = null)
                petRepository.addPet(newPet).onSuccess {
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

    fun updatePet(id: Long, pet: Pet) {
        viewModelScope.launch(ioDispatcher) {
            _petState.value = PetUiState.Loading
            try {
                petRepository.updatePet(id, pet).onSuccess {
                    sendEvent("Mascota actualizada")
                    loadPets()
                }.onFailure {
                    sendEvent("Error al actualizar: ${it.message}")
                    loadPets()
                }
            } catch (e: Exception) {
                sendEvent("Error: ${e.localizedMessage}")
                loadPets()
            }
        }
    }

    fun deletePet(petId: Long) {
        viewModelScope.launch(ioDispatcher) {
             try {
                petRepository.deletePet(petId).onSuccess {
                    sendEvent("Mascota eliminada")
                    loadPets()
                }.onFailure {
                    sendEvent("Error al eliminar: ${it.message}")
                }
            } catch (e: Exception) {
                sendEvent("Error: ${e.localizedMessage}")
            }
        }
    }

    // --- Registration Logic ---
    
    // Gestión local de mascotas para el registro
    fun addPetLocal(pet: Pet) {
        val currentPets = _registerState.value.pets.toMutableList()
        currentPets.add(pet)
        _registerState.update { it.copy(pets = currentPets) }
    }

    fun removePetLocal(pet: Pet) {
        val currentPets = _registerState.value.pets.toMutableList()
        currentPets.remove(pet)
        _registerState.update { it.copy(pets = currentPets) }
    }

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
        viewModelScope.launch(ioDispatcher) {
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
                
                // Limpiamos los IDs locales (temporales) para que el backend genere los suyos
                val petsForBackend = state.pets.map { it.copy(id = null) }

                // Construir el objeto User REAL
                val user = User(
                    id = null, 
                    name = state.fullName,
                    email = state.email,
                    password = state.password,
                    phone = state.phone,
                    pets = petsForBackend
                )

                // Llamada al repositorio
                val result = authRepository.registerUser(user)

                if (result.isSuccess) {
                    sendEvent("Usuario registrado correctamente 🎉")
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
        viewModelScope.launch(ioDispatcher) {
            val state = _loginState.value
            if (state.email.isBlank() || state.password.isBlank()) {
                sendEvent("Completa todos los campos")
                _loginState.update { it.copy(errorMessage = "Completa todos los campos") }
                return@launch
            }

            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val request = LoginRequest(state.email, state.password)
                val result = authRepository.loginUser(request)
                
                if (result.isSuccess) {
                    _loginState.update { it.copy(loginSuccess = true, sessionActive = true) }
                    sendEvent("Bienvenido de nuevo!")
                    loadPets() // Cargar mascotas después del login exitoso
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error de credenciales"
                    _loginState.update { it.copy(errorMessage = errorMsg) }
                    sendEvent(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.localizedMessage}"
                _loginState.update { it.copy(errorMessage = errorMsg) }
                sendEvent(errorMsg)
            } finally {
                _loginState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(ioDispatcher) {
            authRepository.logout()
            _loginState.update { LoginState(sessionActive = false) }
            _registerState.value = RegisterState() // Reset registro
        }
    }
}
