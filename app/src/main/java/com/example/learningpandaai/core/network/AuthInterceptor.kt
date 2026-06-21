package com.example.learningpandaai.core.network

import android.net.Uri
import com.example.learningpandaai.BuildConfig
import com.example.learningpandaai.core.data.SecurePreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val securePreferences: SecurePreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        if (isApiHost(request.url.host)) {
            requestBuilder.addHeader("X-Mobile-API-Key", BuildConfig.MOBILE_API_KEY)
            val token = securePreferences.getOAuthToken()
            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun isApiHost(host: String): Boolean {
        val apiHost = Uri.parse(BuildConfig.BASE_URL).host?.trim().orEmpty()
        return apiHost.isNotEmpty() && host.equals(apiHost, ignoreCase = true)
    }

}