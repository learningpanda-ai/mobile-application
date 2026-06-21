package com.example.learningpandaai.core.di

import com.example.learningpandaai.BuildConfig
import com.example.learningpandaai.features.auth.data.AuthRepositoryImpl
import com.example.learningpandaai.features.auth.data.MockAuthRepositoryImpl
import com.example.learningpandaai.features.auth.domain.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DevRepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        real: AuthRepositoryImpl,
        mock: MockAuthRepositoryImpl
    ): AuthRepository = if (BuildConfig.DEV_REAL_AUTH) real else mock
}