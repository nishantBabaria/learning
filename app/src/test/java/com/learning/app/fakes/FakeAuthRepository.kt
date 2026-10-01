package com.learning.app.fakes

import com.learning.app.core.network.NetworkResult
import com.learning.app.domain.login.AuthRepository
import com.learning.app.domain.login.LoginCredentials
import com.learning.app.domain.login.User

class FakeAuthRepository : AuthRepository {
    override suspend fun login(credentials: LoginCredentials): NetworkResult<User> {
        return NetworkResult.Success(
            data = User(
                id = "usr_1",
                name = "Test User",
                email = credentials.email,
                token = "fake_jwt_token"
            )
        )
    }

    override fun isLoggedIn(): Boolean = false
    override fun logout() {}
}
