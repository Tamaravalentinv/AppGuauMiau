package com.example.perrosygatos.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.viewModel.AuthViewModel
import com.example.perrosygatos.viewModel.PetUiState
import com.example.perrosygatos.viewModel.RegisterState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsState<RegisterState>()
    val petState by viewModel.petState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.userEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Registro", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextField(
                    value = state.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.fullNameError != null,
                    enabled = !state.registrationSuccess
                )
                state.fullNameError?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.emailError != null,
                    enabled = !state.registrationSuccess
                )
                state.emailError?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.passwordError != null,
                    enabled = !state.registrationSuccess
                )
                state.passwordError?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.confirmPasswordError != null,
                    enabled = !state.registrationSuccess
                )
                state.confirmPasswordError?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TextField(
                    value = state.phone,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    label = { Text("Teléfono (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.registrationSuccess
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Mascotas", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val currentPetState = petState
            if (currentPetState is PetUiState.Loading) {
                item {
                    CircularProgressIndicator()
                }
            }

            if (currentPetState is PetUiState.Error) {
                item {
                    Text(currentPetState.message, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            }

            if (currentPetState is PetUiState.Success) {
                items(currentPetState.pets) { pet ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${pet.name} (${pet.type})")
                        Button(onClick = { viewModel.deletePet(pet.id) }) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            item {
                var petName by remember { mutableStateOf("") }
                var petType by remember { mutableStateOf("Perro") }
                var expanded by remember { mutableStateOf(false) }
                val petTypes = listOf("Perro", "Gato", "Ave", "Otro")

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = petName,
                    onValueChange = { petName = it },
                    label = { Text("Nombre de la Mascota") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.registrationSuccess
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = !state.registrationSuccess)
                            .fillMaxWidth(),
                        value = petType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Mascota") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        enabled = !state.registrationSuccess
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        petTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    petType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (petName.isNotBlank()) {
                            viewModel.addPet(Pet(id=0, name=petName, type=petType))
                            petName = ""
                        }
                    },
                    enabled = !state.registrationSuccess
                ) {
                    Text("Añadir Mascota")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                state.errorMessage?.let {
                    Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = {
                        viewModel.register()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.registrationSuccess && !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = "Registrarse")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "¿Ya tienes cuenta? Inicia Sesión")
                }
            }
        }
    }
}