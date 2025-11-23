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
fun AppNavigation(setTitle: (String) -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val loginState by authViewModel.loginState.collectAsState<LoginState>()

    LaunchedEffect(Unit) {
        authViewModel.onAppStart()
    }

    LaunchedEffect(loginState.sessionActive) {
        val route = when (loginState.sessionActive) {
            true -> "home"
            false -> "auth"
            null -> "loader"
        }
        if (route != "loader") { // Evita la navegación inicial si ya estamos en loader
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    // SOLUCIÓN: Se cambia la ruta inicial para saltar el loader que causa el crash.
    NavHost(navController = navController, startDestination = "auth") {
        composable("loader") {
            setTitle("Cargando...")
            Loader()
        }
        composable("auth") {
            setTitle("Autenticación")
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
            setTitle("Inicio")
            HomeScreen(
                onNavigateToPetManagement = { navController.navigate("pet_management") },
                onLogout = { authViewModel.logout() }
            )
        }
        composable("pet_management") {
            setTitle("Administrar Mascotas")
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
