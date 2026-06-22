package com.example.learningpandaai.core.di

import com.example.learningpandaai.features.askpanda.data.MockAgentRepositoryImpl
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.auth.data.MockAuthRepositoryImpl
import com.example.learningpandaai.features.auth.domain.AuthRepository
import com.example.learningpandaai.features.onboarding.data.MockOnboardingRepositoryImpl
import com.example.learningpandaai.features.onboarding.domain.OnboardingRepository
import com.example.learningpandaai.features.profile.data.MockProfileRepositoryImpl
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.progress.data.MockProgressRepositoryImpl
import com.example.learningpandaai.features.progress.domain.ProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MockRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: MockAuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: MockOnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: MockProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: MockProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindAgentRepository(impl: MockAgentRepositoryImpl): AgentRepository
}