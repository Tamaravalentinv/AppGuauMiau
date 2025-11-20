package com.example.perrosygatos.data.repository

import com.example.perrosygatos.data.model.Pet
import com.example.perrosygatos.data.network.PetService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar las operaciones CRUD (Crear, Leer, Actualizar, Borrar) de mascotas.
 * Se encarga de la llamada al servicio y el manejo de errores.
 */
@Singleton
class PetRepository @Inject constructor(
    private val petService: PetService
) {

    /**
     * Obtiene la lista de mascotas asociadas al usuario actual.
     * @return Result<List<Pet>> Lista de mascotas o una excepción en caso de error.
     */
    suspend fun getPets(): Result<List<Pet>> {
        return try {
            val response = petService.getPets()
            if (response.isSuccessful) {
                // Asume que el cuerpo de la respuesta no será nulo si es exitosa
                Result.success(response.body() ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error al cargar mascotas (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Agrega una nueva mascota.
     * @param pet El objeto Pet a agregar.
     * @return Result<Pet> La mascota creada (con ID asignado por el servidor) o una excepción.
     */
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

    /**
     * Actualiza una mascota existente.
     * @param id ID de la mascota a actualizar.
     * @param pet Objeto Pet con los datos actualizados.
     * @return Result<Pet> La mascota actualizada o una excepción.
     */
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

    /**
     * Elimina una mascota por su ID.
     * @param id ID de la mascota a eliminar.
     * @return Result<Boolean> true si la eliminación fue exitosa, o una excepción.
     */
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