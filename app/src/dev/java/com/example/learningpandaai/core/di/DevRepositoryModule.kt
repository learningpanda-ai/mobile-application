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
import com.example.learningpandaai.features.onboarding.data.MockOnboardingRepositoryImpl
import com.example.learningpandaai.features.onboarding.data.OnboardingRepositoryImpl
import com.example.learningpandaai.features.onboarding.domain.OnboardingRepository

@Module
@InstallIn(SingletonComponent::class)
object DevRepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        real: AuthRepositoryImpl,
        mock: MockAuthRepositoryImpl
    ): AuthRepository = if (BuildConfig.DEV_REAL_AUTH) real else mock

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        real: OnboardingRepositoryImpl,
        mock: MockOnboardingRepositoryImpl
    ): OnboardingRepository = if (BuildConfig.DEV_REAL_ONBOARDING) real else mock


}