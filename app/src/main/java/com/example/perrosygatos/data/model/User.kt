package com.example.perrosygatos.data.model

data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val phone: String,
    val pets: List<Pet> = emptyList()
)
