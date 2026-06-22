package com.example.learningpandaai.core.di

import com.example.learningpandaai.features.askpanda.data.AgentRepositoryImpl
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.auth.data.AuthRepositoryImpl
import com.example.learningpandaai.features.auth.domain.AuthRepository
import com.example.learningpandaai.features.onboarding.data.OnboardingRepositoryImpl
import com.example.learningpandaai.features.onboarding.domain.OnboardingRepository
import com.example.learningpandaai.features.profile.data.ProfileRepositoryImpl
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.progress.data.ProgressRepositoryImpl
import com.example.learningpandaai.features.progress.domain.ProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProdRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindAgentRepository(impl: AgentRepositoryImpl): AgentRepository
}