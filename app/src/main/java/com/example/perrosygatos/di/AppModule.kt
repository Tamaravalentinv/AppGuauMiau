package com.example.perrosygatos.di

import android.content.Context
import com.example.perrosygatos.data.datastore.UserDataStore
import com.example.perrosygatos.data.datastore.UserDataStoreImpl
import com.example.perrosygatos.data.network.AuthService
import com.example.perrosygatos.data.network.PetService
import com.example.perrosygatos.data.repository.AuthRepository
import com.example.perrosygatos.data.repository.PetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
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
        return UserDataStoreImpl(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authService: AuthService, userDataStore: UserDataStore): AuthRepository {
        return AuthRepository(authService, userDataStore)
    }

    @Provides
    @Singleton
    fun providePetRepository(petService: PetService): PetRepository {
        return PetRepository(petService)
    }

    @Provides
    @Singleton
    @Named("IO")
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
