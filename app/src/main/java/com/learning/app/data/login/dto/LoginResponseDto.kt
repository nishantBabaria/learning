package com.learning.app.data.login.dto

data class LoginResponseDto(
    val token: String? = null,
    val userId: String? = null,
    val id: String? = null,
    val name: String? = null,
    val email: String? = null
)
