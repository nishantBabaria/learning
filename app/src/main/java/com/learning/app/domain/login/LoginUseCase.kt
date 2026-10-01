package com.learning.app.domain.login

import com.learning.app.core.network.NetworkResult

data class ValidationResult(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = true
)

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    fun validateInput(credentials: LoginCredentials): ValidationResult {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val emailValid = credentials.email.isNotBlank() && emailRegex.matches(credentials.email)
        val passwordValid = credentials.password.length >= 6

        return ValidationResult(
            emailError = if (!emailValid) "Please enter a valid email address" else null,
            passwordError = if (!passwordValid) "Password must be at least 6 characters" else null,
            isValid = emailValid && passwordValid
        )
    }

    suspend operator fun invoke(credentials: LoginCredentials): NetworkResult<User> {
        return authRepository.login(credentials = credentials)
    }
}
