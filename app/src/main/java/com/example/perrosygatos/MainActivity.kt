package com.example.perrosygatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.perrosygatos.ui.theme.PerrosygatosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PerrosygatosTheme {
                // 1. Guardar el título en una variable de estado
                var title by remember { mutableStateOf("Guau&Miau") } // Título inicial

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // 2. Usar el estado en la TopAppBar
                        TopAppBar(title = { Text(text = title) })
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // 3. Pasar la función para actualizar el estado
                        AppNavigation(setTitle = { newTitle ->
                            title = newTitle
                        })
                    }
                }
            }
        }
    }
}
