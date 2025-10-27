package com.example.perrosygatos.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState.registrationSuccess) {
        if (registerState.registrationSuccess) {
            navController.navigate("login") { 
                popUpTo("register") { inclusive = true } 
            }
        }
    }

    NavHost(navController = navController, startDestination = "register") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onRegisterClick = { navController.navigate("register") },
                viewModel = authViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { 
                    navController.navigate("login")
                },
                onLoginClick = { navController.navigate("login") },
                viewModel = authViewModel
            )
        }
    }
}