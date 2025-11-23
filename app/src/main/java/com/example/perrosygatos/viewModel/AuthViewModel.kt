package com.example.perrosygatos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            // Lógica de loadPets
        }
    }

    fun addPet(pet: com.example.perrosygatos.data.model.Pet) {
        viewModelScope.launch(Dispatchers.IO) {
            // Lógica de addPet
        }
    }

    fun deletePet(petId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Lógica de deletePet
        }
    }

    // --- Registration Logic ---
    fun onFullNameChange(fullName: String) {
        _registerState.update { it.copy(fullName = fullName, fullNameError = null) }
    }

    fun onEmailChange(email: String) {
        _registerState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun onPhoneChange(phone: String) {
        _registerState.update { it.copy(phone = phone) }
    }

    fun register() {
        // Lógica de registro
    }

    // --- Login Logic ---
    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun login() {
        // Lógica de login
    }

    fun logout() {
        // Lógica de logout
    }
}
