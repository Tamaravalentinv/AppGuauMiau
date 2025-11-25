package com.example.perrosygatos.data.model

/**
 * Data class que representa una mascota.
 */
data class Pet(
    val id: Long? = null,
    val name: String,
    val type: String,
    val userId: Long? = null
)
