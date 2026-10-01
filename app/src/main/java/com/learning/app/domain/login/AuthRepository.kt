package com.learning.app.domain.login

import com.learning.app.core.network.NetworkResult

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): NetworkResult<User>
    fun isLoggedIn(): Boolean
    fun logout()
}
