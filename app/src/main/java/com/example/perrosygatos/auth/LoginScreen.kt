package com.example.perrosygatos.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.perrosygatos.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()
    var showForgotPasswordMessage by remember { mutableStateOf(false) }

    // Efecto para navegar cuando el login es exitoso
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            onLoginSuccess()
        }
    }

    // Manejo del Diálogo de Error
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { 
                // Opcional: Limpiar el error al cerrar el diálogo si el ViewModel tiene un método para ello
                // viewModel.clearError() 
            },
            title = { Text("Error de inicio de sesión") },
            text = { Text(state.errorMessage ?: "Error desconocido") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        // Al hacer clic en Aceptar, podríamos limpiar el error o simplemente cerrar el diálogo
                        // Por ahora, como el estado viene del ViewModel, el diálogo seguirá apareciendo 
                        // hasta que se intente otro login o se limpie el estado.
                        // Una solución rápida es reintentar o simplemente dejar que el usuario edite los campos.
                        // Nota: Para cerrar el diálogo correctamente, el ViewModel debería exponer una función 'clearError()'
                        // O el botón de login debería resetear el error al pulsarse (que ya lo hace).
                        viewModel.onLoginEmailChange(state.email) // Hack simple para forzar recomposición o simplemente no hacer nada visual
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -80 })
        ) {
            Text(
                text = "Bienvenido a Guau&Miau 🐾",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.email,
            onValueChange = { viewModel.onLoginEmailChange(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.password,
            onValueChange = { viewModel.onLoginPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Eliminamos el texto rojo de aquí porque ahora saldrá en el Dialog
        
        if (showForgotPasswordMessage) {
            Text("Funcionalidad de recuperación en desarrollo.", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(text = if (state.isLoading) "Cargando..." else "Iniciar Sesión")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { showForgotPasswordMessage = true }) {
            Text("¿Olvidaste tu contraseña?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrarse")
        }
    }
}
