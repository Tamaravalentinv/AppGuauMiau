package com.example.perrosygatos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.perrosygatos.auth.AuthScreen
import com.example.perrosygatos.home.HomeScreen
import com.example.perrosygatos.home.PetManagementScreen
import com.example.perrosygatos.viewModel.AuthViewModel
import com.example.perrosygatos.viewModel.LoginState

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val loginState by authViewModel.loginState.collectAsState<LoginState>()

    // Unificamos la lógica en un solo LaunchedEffect para evitar condiciones de carrera.
    LaunchedEffect(key1 = loginState.sessionActive) {
        // La primera vez que entra, sessionActive es null, así que llamamos a onAppStart()
        if (loginState.sessionActive == null) {
            authViewModel.onAppStart()
            return@LaunchedEffect // Salimos para esperar a que onAppStart() actualice el estado
        }

        // Una vez que onAppStart() ha terminado, sessionActive será true o false, y navegamos
        if (loginState.sessionActive == true) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        } else {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "loader") {
        composable("loader") {
            Loader()
        }
        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToPetManagement = { navController.navigate("pet_management") },
                onLogout = { authViewModel.logout() }
            )
        }
        composable("pet_management") {
            PetManagementScreen()
        }
    }
}

@Composable
fun Loader() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
    }
}
