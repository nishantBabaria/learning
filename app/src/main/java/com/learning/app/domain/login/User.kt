package com.learning.app.domain.login

data class User(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)
