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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.perrosygatos.R
import com.example.perrosygatos.viewModel.AuthViewModel
import com.example.perrosygatos.viewModel.PetUiState
import com.example.perrosygatos.data.model.Pet
import com.google.android.gms.location.LocationServices

@Composable
fun PetManagementScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val petState by viewModel.petState.collectAsState()
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var location by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Launcher para capturar la imagen
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap -> imageBitmap = bitmap }
    )

    // Launcher para solicitar el permiso de la cámara y luego lanzar la cámara
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                takePictureLauncher.launch()
            }
        }
    )

    // Launcher para solicitar permisos de ubicación y obtener la ubicación
    @SuppressLint("MissingPermission")
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // Si se concedió el permiso, intentar obtener la última ubicación conocida
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
        // Muestra la foto capturada si existe
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                // Nota: R.string.pet_photo_description debe existir en tu proyecto
                contentDescription = "Foto de Mascota",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón para obtener la ubicación
        Button(onClick = { requestLocationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }) {
            Text("Obtener Ubicación")
        }
        // Muestra la ubicación si existe
        location?.let {
            Text("Latitud: ${it.latitude}, Longitud: ${it.longitude}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Título de la lista
        Text(text = "Mis Mascotas Registradas", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Contenido basado en el estado de las mascotas
        when (val state = petState) {
            is PetUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PetUiState.Success -> {
                // Si la lista está vacía
                if (state.pets.isEmpty()) {
                    Text("Aún no tienes mascotas registradas.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.pets) { pet ->
                            PetItem(
                                pet = pet,
                                onUpdateClick = { /* TODO: Implement update logic */ },
                                // La eliminación de mascotas ya está implementada en AuthViewModel
                                onDeleteClick = { viewModel.deletePet(pet.id) },
                                // Lanza la solicitud de permiso de cámara
                                onTakePhotoClick = { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                            )
                            HorizontalDivider()
                        }
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
        Column(modifier = Modifier.weight(1f)) {
            Text(text = pet.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = pet.type, style = MaterialTheme.typography.bodySmall)
        }
        Row {
            Button(onClick = onUpdateClick, modifier = Modifier.wrapContentWidth()) {
                Text("Actualizar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDeleteClick, modifier = Modifier.wrapContentWidth()) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onTakePhotoClick, modifier = Modifier.wrapContentWidth()) {
                Text("Foto")
            }
        }
    }
}