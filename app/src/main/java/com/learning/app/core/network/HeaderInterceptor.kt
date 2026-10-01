package com.learning.app.core.network

import com.learning.app.core.security.SecurePreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    private val securePrefs: SecurePreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")

        securePrefs.getAuthToken()?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}
