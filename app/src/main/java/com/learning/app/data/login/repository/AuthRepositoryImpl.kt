package com.learning.app.data.login.repository

import com.learning.app.core.network.ApiErrorMapper
import com.learning.app.core.network.NetworkResult
import com.learning.app.core.security.SecurePreferencesManager
import com.learning.app.data.login.dto.LoginRequestDto
import com.learning.app.data.login.remote.AuthApiService
import com.learning.app.domain.login.AuthRepository
import com.learning.app.domain.login.LoginCredentials
import com.learning.app.domain.login.User

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val securePrefs: SecurePreferencesManager
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): NetworkResult<User> {
        return try {
            val responseDto = authApiService.login(
                request = LoginRequestDto(
                    email = credentials.email,
                    password = credentials.password
                )
            )

            val token = responseDto.token ?: "bearer_token_${System.currentTimeMillis()}"
            val actualUserId = responseDto.id ?: responseDto.userId ?: kotlin.math.abs(credentials.email.hashCode()).toString()

            val user = User(
                id = actualUserId,
                name = responseDto.name ?: credentials.email.substringBefore("@"),
                email = responseDto.email ?: credentials.email,
                token = token
            )

            securePrefs.saveAuthToken(token = user.token, email = user.email, userId = user.id)
            NetworkResult.Success(data = user)
        } catch (e: Exception) {
            val errorMessage = ApiErrorMapper.mapThrowableToErrorMessage(throwable = e)
            NetworkResult.Error(message = errorMessage, cause = e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return securePrefs.isLoggedIn()
    }

    override fun logout() {
        securePrefs.clearSession()
    }
}
