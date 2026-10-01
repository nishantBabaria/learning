package com.learning.app.core.network

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val message: String, val cause: Throwable? = null) : NetworkResult<Nothing>
}

object ApiErrorMapper {
    fun mapThrowableToErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "No internet connection available."
            is SocketTimeoutException -> "Server connection timed out. Please try again."
            is IOException -> "Network request failed. Please check your connection."
            is HttpException -> {
                when (throwable.code()) {
                    NetworkConstants.HTTP_UNAUTHORIZED -> "Invalid email or password."
                    NetworkConstants.HTTP_NOT_FOUND -> "Requested resource not found."
                    in 500..599 -> "Server error occurred. Please try again later."
                    else -> "HTTP error ${throwable.code()}: ${throwable.message()}"
                }
            }
            else -> throwable.localizedMessage ?: "An unexpected error occurred."
        }
    }
}
