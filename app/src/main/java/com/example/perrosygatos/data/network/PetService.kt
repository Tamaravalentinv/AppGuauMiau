package com.example.perrosygatos.data.network // <-- CORREGIDO

import com.example.perrosygatos.data.model.Pet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PetService {
    @GET("pets")
    suspend fun getPets(): Response<List<Pet>>

    @POST("pets")
    suspend fun addPet(@Body pet: Pet): Response<Pet>

    @PUT("pets/{id}")
    suspend fun updatePet(@Path("id") petId: Long, @Body pet: Pet): Response<Pet>

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") petId: Long): Response<Void>
}