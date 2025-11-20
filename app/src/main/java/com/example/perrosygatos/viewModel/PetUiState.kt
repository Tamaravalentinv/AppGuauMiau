package com.example.perrosygatos.viewModel

import com.example.perrosygatos.data.model.Pet

/**
 * Sealed Interface que representa los diferentes estados de la UI para la lista de mascotas.
 */
sealed interface PetUiState {
    data class Success(val pets: List<Pet>) : PetUiState
    data class Error(val message: String) : PetUiState
    object Loading : PetUiState
}
