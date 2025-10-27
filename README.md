# AppGuauMiau 🐾
# link de trello: https://trello.com/b/iypQDvCz/appguaumiau
## Descripción

AppGuauMiau es una aplicación de Android que sirve como un excelente ejemplo de una implementación moderna de un flujo de autenticación de usuarios. La aplicación está construida completamente en **Kotlin** y utiliza las últimas librerías de **Jetpack**, incluyendo **Compose** para la interfaz de usuario y **Navigation** para gestionar las transiciones entre pantallas.

El proyecto demuestra una arquitectura limpia y modular, separando la lógica de la interfaz de usuario, lo que lo convierte en una base sólida para proyectos más complejos.

## Funcionalidades Detalladas

*   **Flujo de Autenticación Completo**:
    *   **Pantalla de Registro**: Permite a los nuevos usuarios crear una cuenta para acceder a la aplicación.
    *   **Pantalla de Inicio de Sesión**: Permite a los usuarios existentes iniciar sesión de forma segura.
    *   **Navegación Protegida**: Tras un inicio de sesión exitoso, el usuario es redirigido a la pantalla principal y la pantalla de autenticación es eliminada del historial de navegación para prevenir retornos no deseados.

*   **Interfaz de Usuario Moderna y Reactiva**:
    *   Desarrollada 100% con **Jetpack Compose**, el toolkit de UI declarativo de Android.
    *   Uso de `NavHost` de **Jetpack Navigation** para una navegación fluida y desacoplada entre los diferentes `Composables`.

*   **Arquitectura Sólida**:
    *   Implementación del patrón de arquitectura **MVVM (Model-View-ViewModel)**, separando la lógica de negocio (`ViewModel`) de la lógica de la interfaz de usuario (`View`). Esto se puede observar en la estructura del paquete `auth`.

## Tecnologías Utilizadas

*   **Lenguaje**: Kotlin
*   **Interfaz de Usuario**: Jetpack Compose
*   **Navegación**: Jetpack Navigation Compose
*   **Arquitectura**: MVVM
*   **Herramienta de Construcción**: Gradle

## Estructura del Proyecto

El código fuente está organizado de manera modular para facilitar su comprensión y mantenimiento:

```
app/
└── src/
    └── main/
        └── java/
            └── com/example/perrosygatos/
                ├── MainActivity.kt         # Actividad principal y punto de entrada de la app.
                ├── AppNavigation.kt        # Define el grafo de navegación principal.
                ├── auth/                   # Módulo de autenticación.
                │   ├── AuthScreen.kt       # Pantalla que ofrece opción de login o registro.
                │   ├── LoginScreen.kt      # Composable para el inicio de sesión.
                │   ├── RegisterScreen.kt   # Composable para el registro de usuarios.
                │   └── AuthViewModel.kt    # ViewModel que gestiona la lógica de autenticación.
                ├── home/                   # Módulo principal post-login.
                │   └── HomeScreen.kt       # Pantalla de bienvenida.
                └── ui/
                    └── theme/              # Tema de la app (colores, tipografía, formas).
```


**Instalación y Ejecución:**

1.  **Clona el repositorio** en tu máquina local.
2.  **Abre el proyecto** con Android Studio.
3.  Espera a que la **sincronización de Gradle** se complete para descargar todas las dependencias.
4.  **Selecciona la configuración de ejecución `app`** en la barra superior.
5.  **Elige un emulador o conecta un dispositivo físico** con Android.
6.  **Presiona "Run" (▶️)** para compilar y ejecutar la aplicación.
7.  Una vez en la aplicación, **regístrate** con un nuevo usuario o **inicia sesión** para ver la pantalla de bienvenida.
