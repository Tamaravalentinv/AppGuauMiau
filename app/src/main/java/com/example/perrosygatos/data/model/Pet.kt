package com.example.perrosygatos.data.model

// Se eliminó Parcelable para resolver el conflicto de compilación con Hilt/KSP.
// La arquitectura actual de la app (Single-Activity con ViewModel) no requiere que este objeto sea Parcelable.

data class Pet(
    val id: Long,
    val name: String,
    val type: String
)
