package com.example.perrosygatos.viewModel

import com.example.perrosygatos.MainDispatcherRule
import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.data.repository.AuthRepository
import com.example.perrosygatos.data.repository.PetRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PetsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @MockK
    lateinit var authRepository: AuthRepository

    @MockK
    lateinit var petRepository: PetRepository

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = AuthViewModel(authRepository, petRepository, testDispatcher)
    }

    @Test
    fun `loadPets success updates state correctly`() = runTest {
        // Given
        val pets = listOf(
            Pet(id = 1, name = "Firulais", type = "Perro", userId = 123),
            Pet(id = 2, name = "Misha", type = "Gato", userId = 456)
        )
        coEvery { petRepository.getPets() } returns Result.success(pets)

        // When
        viewModel.loadPets()

        // Then
        val state = viewModel.petState.value
        assertTrue("El estado debería ser Success pero es $state", state is PetUiState.Success)
        assertEquals(pets, (state as PetUiState.Success).pets)
    }

    @Test
    fun `loadPets failure updates state with error`() = runTest {
        // Given
        val errorMessage = "Error de conexión"
        coEvery { petRepository.getPets() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadPets()

        // Then
        val state = viewModel.petState.value
        assertTrue("El estado debería ser Error pero es $state", state is PetUiState.Error)
        assertEquals(errorMessage, (state as PetUiState.Error).message)
    }

    @Test
    fun `addPet success reloads pets`() = runTest {
        // Given
        val newPet = Pet(id = null, name = "Nuevito", type = "Perro", userId = 123)
        val addedPet = newPet.copy(id = 100)
        
        coEvery { petRepository.addPet(any()) } returns Result.success(addedPet)
        coEvery { petRepository.getPets() } returns Result.success(listOf(addedPet)) 

        // When
        viewModel.addPet(newPet)

        // Then
        val state = viewModel.petState.value
        assertTrue("El estado debería ser Success pero es $state", state is PetUiState.Success)
        assertEquals(1, (state as PetUiState.Success).pets.size)
        assertEquals("Nuevito", state.pets[0].name)
    }
}
