package com.example.perrosygatos.auth

import android.content.Context
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.viewModel.AuthViewModel
import com.example.perrosygatos.viewModel.RegisterState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsState<RegisterState>()
    // Ya no observamos petState aquí porque usamos la lista local de mascotas en RegisterState
    
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var imagenMascota by remember { mutableStateOf<Uri?>(null) }
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagenMascota = uri
    }

    val buttonColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    )

    val buttonPadding by animateDpAsState(targetValue = if (isPressed) 10.dp else 8.dp)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(key1 = true) {
        viewModel.userEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -60 })
                ) {
                    Text(
                        text = "Crea tu cuenta en Guau&Miau 🐾",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { TextField(value = state.fullName, onValueChange = { viewModel.onFullNameChange(it) }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), isError = state.fullNameError != null, enabled = !state.registrationSuccess) }
            item { state.fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { TextField(value = state.email, onValueChange = { viewModel.onEmailChange(it) }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth(), isError = state.emailError != null, enabled = !state.registrationSuccess) }
            item { state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { TextField(value = state.password, onValueChange = { viewModel.onPasswordChange(it) }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), isError = state.passwordError != null, enabled = !state.registrationSuccess) }
            item { state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { TextField(value = state.confirmPassword, onValueChange = { viewModel.onConfirmPasswordChange(it) }, label = { Text("Confirmar Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), isError = state.confirmPasswordError != null, enabled = !state.registrationSuccess) }
            item { state.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { TextField(value = state.phone, onValueChange = { viewModel.onPhoneChange(it) }, label = { Text("Teléfono (Opcional)") }, modifier = Modifier.fillMaxWidth(), enabled = !state.registrationSuccess) }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
                ) {
                    Text(
                        text = "Registra a tus compañeros peludos 🐶🐱",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // LISTA DE MASCOTAS LOCALES (Antes de enviar al backend)
            items(state.pets) { pet ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🐾 ${pet.name} (${pet.type})")
                    Button(onClick = { viewModel.removePetLocal(pet) }, enabled = !state.registrationSuccess) {
                        Text("Eliminar")
                    }
                }
            }

            item {
                var petName by remember { mutableStateOf("") }
                var petType by remember { mutableStateOf("Perro") }
                var expanded by remember { mutableStateOf(false) }
                val petTypes = listOf("Perro", "Gato", "Ave", "Otro")

                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = petName, onValueChange = { petName = it }, label = { Text("Nombre de la Mascota") }, modifier = Modifier.fillMaxWidth(), enabled = !state.registrationSuccess)
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = !state.registrationSuccess).fillMaxWidth(),
                        value = petType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Mascota") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        enabled = !state.registrationSuccess
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        petTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { petType = type; expanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { launcherGaleria.launch("image/*") }) {
                    Text("Seleccionar Foto de la Mascota")
                }

                imagenMascota?.let { uri ->
                    Image(painter = rememberAsyncImagePainter(uri), contentDescription = "Foto mascota", modifier = Modifier.size(120.dp).padding(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (petName.isNotBlank()) {
                            // Añadir mascota LOCALMENTE
                            viewModel.addPetLocal(Pet(id = System.currentTimeMillis(), name = petName, type = petType))
                            vibrar(context)
                            petName = ""
                        }
                    },
                    enabled = !state.registrationSuccess
                ) {
                    Text("Añadir Mascota a la Lista")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                AnimatedVisibility(visible = state.registrationSuccess, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = "¡Registro completado exitosamente! 🎉",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = {
                        viewModel.register()
                        vibrar(context)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = buttonPadding),
                    enabled = !state.registrationSuccess && !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    interactionSource = interactionSource
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = "Registrarse")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth(), enabled = !state.registrationSuccess) {
                    Text(text = "¿Ya tienes cuenta? Inicia Sesión")
                }
            }
        }
    }
}

fun vibrar(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
