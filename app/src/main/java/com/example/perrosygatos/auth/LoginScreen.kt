package com.example.perrosygatos.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perrosygatos.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.loginState.collectAsState()
    var showForgotPasswordMessage by remember { mutableStateOf(false) }

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
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

        state.errorMessage?.let {
            Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (showForgotPasswordMessage) {
            Text("Funcionalidad de recuperación en desarrollo.", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Iniciar Sesión")
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