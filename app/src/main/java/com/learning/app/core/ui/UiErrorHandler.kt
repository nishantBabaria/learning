package com.learning.app.core.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

object UiErrorHandler {
    fun showErrorToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun HandleErrorMessage(
    errorMessage: String?,
    onMessageShown: (() -> Unit)? = null
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = errorMessage) {
        errorMessage?.let { msg ->
            UiErrorHandler.showErrorToast(context = context, message = msg)
            onMessageShown?.invoke()
        }
    }
}
