package com.example.learningpandaai.core.di

import android.content.Context
import com.example.learningpandaai.BuildConfig
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.network.AuthInterceptor
import com.example.learningpandaai.core.network.CertificatePinning
import com.example.learningpandaai.core.network.TokenAuthenticator
import com.example.learningpandaai.features.auth.data.remote.AuthTokenRefreshApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.example.learningpandaai.features.auth.data.remote.AuthApiService
import com.example.learningpandaai.features.profile.data.remote.ProfileApiService

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecurePreferences(@ApplicationContext context: Context): SecurePreferences =
        SecurePreferences(context)

    @Provides
    @Singleton
    @Named("apiKeyOnly")
    fun provideApiKeyOnlyOkHttpClient(): OkHttpClient {

        val builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Mobile-API-Key", BuildConfig.MOBILE_API_KEY)
                    .build()
                chain.proceed(request)
            }
        CertificatePinning.applyIfConfigured(builder)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
                redactHeader("Authorization")
                redactHeader("X-Mobile-API-Key")
                redactHeader("Set-Cookie")
                redactHeader("Cookie")
            }
            builder.addInterceptor(logging)
        }
        CertificatePinning.applyIfConfigured(builder)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        check(BuildConfig.DEBUG || BuildConfig.BASE_URL.startsWith("https://")) {
            "BASE_URL must use https:// in release builds (was: '${BuildConfig.BASE_URL}')"
        }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthTokenRefreshApi(
        @Named("apiKeyOnly") apiKeyOnlyClient: OkHttpClient
    ): AuthTokenRefreshApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(apiKeyOnlyClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthTokenRefreshApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)


    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)
}