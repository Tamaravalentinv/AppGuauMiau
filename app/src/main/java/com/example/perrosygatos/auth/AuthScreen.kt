package com.example.perrosygatos.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.perrosygatos.viewModel.AuthViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    // SE ELIMINA EL LaunchedEffect QUE CAUSABA LA NAVEGACIÓN AUTOMÁTICA.
    // La navegación ahora es controlada por la interacción del usuario.

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onRegisterClick = { navController.navigate("register") },
                viewModel = authViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onLoginClick = { navController.navigate("login") },
                viewModel = authViewModel
            )
        }
    }
}