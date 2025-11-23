# AppGuauMiau 🐾

# link de trello: https://trello.com/b/iypQDvCz/appguaumiau

## Descripción

AppGuauMiau es una aplicación de Android construida con tecnologías modernas, sirviendo como un proyecto ejemplar que demuestra una arquitectura robusta, una interfaz de usuario limpia y una experiencia de usuario bien pensada. La aplicación utiliza **Kotlin** como lenguaje principal y está desarrollada siguiendo las últimas tendencias de **Jetpack**, con **Compose** para la UI y **Hilt** para la inyección de dependencias.

## Funcionalidades Detalladas

*   **Flujo de Autenticación Completo**: Registro, inicio de sesión y gestión de sesión persistente con DataStore.
*   **CRUD de Mascotas**: Funcionalidad completa para añadir, ver y eliminar mascotas, con comunicación a un backend a través de Retrofit.
*   **Integración con Recursos Nativos**: Acceso a la **Cámara** para tomar fotos y a la **Geolocalización** para obtener la ubicación del dispositivo.
*   **Feedback al Usuario**: Uso de Snackbars para comunicar el resultado de las operaciones (ej. "Usuario creado con éxito").
*   **Diseño Moderno con Material Design 3**: Implementación de una paleta de colores personalizada, una escala tipográfica jerárquica y una estructura de navegación clara.
*   **Animaciones**: Integración de Lottie para mostrar animaciones de carga, mejorando la experiencia visual.

## Arquitectura y Diseño

### A. Estructura del Proyecto (MVVM)

La aplicación sigue el patrón de arquitectura **Model-View-ViewModel (MVVM)**, que promueve una clara separación de responsabilidades, facilitando el mantenimiento, la escalabilidad y las pruebas.

*   **Justificación de MVVM**: Se eligió MVVM porque separa la lógica de la UI de la lógica de negocio. La Vista (Composable) solo se encarga de mostrar los datos y notificar las interacciones del usuario, mientras que el ViewModel maneja el estado y la lógica, sobreviviendo a los cambios de configuración. Esta separación hace que el código sea más limpio y fácil de depurar.

*   **Screens / Composables (`/auth`, `/home`)**: Contienen la interfaz de usuario, construida con Jetpack Compose. Son "tontos" en el sentido de que solo observan el estado del ViewModel y le notifican las acciones del usuario (ej. `viewModel.login()`).

*   **ViewModel (`/viewModel`)**: Contiene la lógica de presentación y el estado de la UI. El `AuthViewModel` utiliza `StateFlow` para exponer el estado (`LoginState`, `RegisterState`) a los Composables y un `SharedFlow` para eventos de una sola vez (como mostrar un Snackbar). No tiene conocimiento directo de la UI de Android, lo que facilita las pruebas unitarias.

*   **Repository (`/data/repository`)**: Actúa como una única fuente de verdad para los datos. Los repositorios (`AuthRepository`, `PetRepository`) encapsulan la lógica para acceder a los datos, ya sea desde la red (Retrofit) o desde el almacenamiento local (DataStore). Abstraen el origen de los datos del resto de la app.

*   **Model (`/data/model`)**: Son las clases de datos (`User`, `Pet`, `LoginRequest`) que definen la estructura de la información. Son simples contenedores de datos (POJOs/data classes).

*   **Backend (Teórico)**: Aunque no forma parte de este proyecto Android, el backend sería una aplicación de servidor (ej. construida con Spring Boot, Ktor, o Node.js) que expone una API REST. Contendría:
    *   **Entidades**: Las representaciones de los datos en la base de datos (ej. tablas `User`, `Pet`).
    *   **Controladores**: Las clases que reciben las peticiones HTTP (ej. `POST /auth/register`) y orquestan la respuesta.
    *   **Rutas**: La definición de los endpoints de la API (ej. `/auth/login`, `/pets/{id}`).

## Tecnologías Utilizadas

*   **Lenguaje**: Kotlin
*   **UI**: Jetpack Compose
*   **Arquitectura**: MVVM
*   **Inyección de Dependencias**: Hilt
*   **Navegación**: Jetpack Navigation Compose
*   **Networking**: Retrofit
*   **Persistencia Local**: Jetpack DataStore
*   **Animaciones**: Lottie
*   **Procesamiento de Anotaciones**: KSP
*   **Herramienta de Construcción**: Gradle

## Estructura del Proyecto Detallada

```
app/src/main/java/com/example/perrosygatos/
├── MainActivity.kt               # Actividad principal, contiene el Scaffold y TopAppBar.
├── GuauMiauApp.kt                # Clase Application para Hilt.
├── AppNavigation.kt              # Define el grafo de navegación de Compose.
├── auth/                         # Módulo de UI de autenticación.
│   ├── AuthScreen.kt             # Orquestador de las pantallas de Login y Registro.
│   ├── LoginScreen.kt
│   └── RegisterScreen.kt
├── data/                         # Módulo de datos.
│   ├── datastore/                # UserDataStore para la sesión.
│   ├── model/                    # Data classes (User, Pet, etc.).
│   ├── network/                  # Servicios de Retrofit (AuthService, PetService).
│   └── repository/               # Repositorios (AuthRepository, PetRepository).
├── di/                           # Módulo de Inyección de Dependencias (Hilt).
│   └── AppModule.kt
├── home/                         # Módulo de UI post-login.
│   ├── HomeScreen.kt
│   └── PetManagementScreen.kt
├── ui/                           # Módulo de UI y tema.
│   └── theme/                    # Color.kt, Theme.kt, Typography.kt.
└── viewModel/                    # ViewModels y clases de estado (State).
    ├── AuthViewModel.kt
    ├── LoginState.kt
    ├── RegisterState.kt
    └── PetUiState.kt
```

**Instalación y Ejecución:**

1.  **Clona el repositorio**.
2.  **Abre con Android Studio**.
3.  Espera a que **Gradle se sincronice**.
4.  **(Recomendado)** Asegúrate de que tu backend esté corriendo en `http://10.0.2.2:8080/` para que las llamadas de red funcionen.
5.  **Ejecuta (▶️)** la aplicación.
