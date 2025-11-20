package com.example.perrosygatos.data.model

// Se eliminó Parcelable para resolver el conflicto de compilación con Hilt/KSP.
// La arquitectura actual de la app (Single-Activity con ViewModel) no requiere que este objeto sea Parcelable.

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val pets: List<Pet>
)
