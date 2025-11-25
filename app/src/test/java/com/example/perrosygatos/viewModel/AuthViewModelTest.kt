package com.example.perrosygatos.viewModel

import com.example.perrosygatos.MainDispatcherRule
import com.example.perrosygatos.data.model.LoginRequest
import com.example.perrosygatos.data.model.User
import com.example.perrosygatos.data.repository.AuthRepository
import com.example.perrosygatos.data.repository.PetRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

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
    fun `login success updates state correctly`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "password123"
        val user = User(1, "Test User", email, password, "123456789", emptyList())
        
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPasswordChange(password)

        coEvery { authRepository.loginUser(any()) } returns Result.success(user)
        coEvery { petRepository.getPets() } returns Result.success(emptyList())

        // When
        viewModel.login()
        
        // Then
        assertTrue("LoginSuccess debería ser true", viewModel.loginState.value.loginSuccess)
        assertTrue("SessionActive debería ser true", viewModel.loginState.value.sessionActive == true)
        assertEquals(null, viewModel.loginState.value.errorMessage)
    }

    @Test
    fun `login failure updates state with error message`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "wrongpassword"
        
        viewModel.onLoginEmailChange(email)
        viewModel.onLoginPasswordChange(password)

        val errorMessage = "Credenciales inválidas"
        coEvery { authRepository.loginUser(any()) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.login()

        // Then
        assertFalse(viewModel.loginState.value.loginSuccess)
        assertEquals(errorMessage, viewModel.loginState.value.errorMessage)
    }

    @Test
    fun `login with empty fields shows error`() = runTest {
        // Given
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPasswordChange("")

        // When
        viewModel.login()

        // Then
        assertEquals("Completa todos los campos", viewModel.loginState.value.errorMessage)
    }
}
