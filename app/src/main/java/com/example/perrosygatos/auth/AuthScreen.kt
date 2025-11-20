package com.example.perrosygatos.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.perrosygatos.viewModel.AuthViewModel
import com.example.perrosygatos.viewModel.RegisterState

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val registerState by authViewModel.registerState.collectAsState<RegisterState>()

    LaunchedEffect(registerState.registrationSuccess) {
        if (registerState.registrationSuccess) {
            navController.navigate("login") { 
                popUpTo("register") { inclusive = true } 
            }
        }
    }

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