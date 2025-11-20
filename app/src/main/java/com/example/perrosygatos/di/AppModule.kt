package com.example.perrosygatos.di

import android.content.Context
import com.example.perrosygatos.data.datastore.UserDataStore
import com.example.perrosygatos.data.network.AuthService
import com.example.perrosygatos.data.network.PetService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun providePetService(retrofit: Retrofit): PetService {
        return retrofit.create(PetService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): UserDataStore {
        return UserDataStore(context)
    }
    
    // Se eliminan las funciones @Provides para AuthRepository y PetRepository.
    // Hilt ya sabe cómo construirlas gracias a la anotación @Inject en sus constructores.
}
