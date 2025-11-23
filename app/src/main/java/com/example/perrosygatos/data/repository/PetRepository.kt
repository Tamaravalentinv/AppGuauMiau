package com.example.perrosygatos.data.repository

import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.data.network.PetService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petService: PetService
) {

    suspend fun getPets(): Result<List<Pet>> {
        return try {
            val response = petService.getPets()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error al cargar mascotas (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addPet(pet: Pet): Result<Pet> {
        return try {
            val response = petService.addPet(pet)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error al agregar mascota (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePet(id: Long, pet: Pet): Result<Pet> {
        return try {
            val response = petService.updatePet(id, pet)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error al actualizar mascota (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePet(id: Long): Result<Boolean> {
        return try {
            val response = petService.deletePet(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error al eliminar mascota (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}