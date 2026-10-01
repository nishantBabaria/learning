package com.learning.app.data.login.remote

import com.learning.app.data.login.dto.LoginRequestDto
import com.learning.app.data.login.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
