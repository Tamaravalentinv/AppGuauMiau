package com.example.perrosygatos.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onNavigateToPetManagement: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¡Bienvenido a GUAU&MIAU!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToPetManagement) {
            Text(text = "Administrar Mascotas")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onLogout) {
            Text(text = "Cerrar Sesión")
        }
    }
}