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
import com.example.learningpandaai.features.profile.data.MockProfileRepositoryImpl
import com.example.learningpandaai.features.profile.data.ProfileRepositoryImpl
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.progress.data.MockProgressRepositoryImpl
import com.example.learningpandaai.features.progress.data.ProgressRepositoryImpl
import com.example.learningpandaai.features.progress.domain.ProgressRepository
import com.example.learningpandaai.features.askpanda.data.AgentRepositoryImpl
import com.example.learningpandaai.features.askpanda.data.MockAgentRepositoryImpl
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
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

    @Provides
    @Singleton
    fun provideProfileRepository(
        real: ProfileRepositoryImpl,
        mock: MockProfileRepositoryImpl
    ): ProfileRepository = if (BuildConfig.DEV_REAL_PROFILE) real else mock


    @Provides
    @Singleton
    fun provideProgressRepository(
        real: ProgressRepositoryImpl,
        mock: MockProgressRepositoryImpl
    ): ProgressRepository = if (BuildConfig.DEV_REAL_PROGRESS) real else mock
    @Provides
    @Singleton
    fun provideAgentRepository(
        real: AgentRepositoryImpl,
        mock: MockAgentRepositoryImpl
    ): AgentRepository = if (BuildConfig.DEV_REAL_AGENT) real else mock

}