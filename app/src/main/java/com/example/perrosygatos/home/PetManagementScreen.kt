package com.example.perrosygatos.home

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.perrosygatos.viewModel.AuthViewModel
import com.example.perrosygatos.viewModel.PetUiState
import com.example.perrosygatos.data.model.Pet
import com.google.android.gms.location.LocationServices

@Composable
fun PetManagementScreen(
    viewModel: AuthViewModel = viewModel()
) {
    val petState by viewModel.petState.collectAsState()
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var location by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap -> imageBitmap = bitmap }
    )

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                takePictureLauncher.launch()
            }
        }
    )

    @SuppressLint("MissingPermission")
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    location = loc
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Administrar Mascotas",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { requestLocationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }) {
            Text("Obtener Ubicación")
        }
        location?.let {
            Text("Latitud: ${it.latitude}, Longitud: ${it.longitude}")
        }

        imageBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "Foto de la mascota", modifier = Modifier.size(150.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (val state = petState) {
            is PetUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PetUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.pets) { pet ->
                        PetItem(
                            pet = pet,
                            onUpdateClick = { /* TODO: Implement update logic */ },
                            onDeleteClick = { viewModel.deletePet(pet.id) },
                            onTakePhotoClick = { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                        )
                        HorizontalDivider()
                    }
                }
            }
            is PetUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun PetItem(pet: Pet, onUpdateClick: () -> Unit, onDeleteClick: () -> Unit, onTakePhotoClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = pet.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = pet.type, style = MaterialTheme.typography.bodySmall)
        }
        Row {
            Button(onClick = onUpdateClick) {
                Text("Actualizar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDeleteClick) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onTakePhotoClick) {
                Text("Foto")
            }
        }
    }
}