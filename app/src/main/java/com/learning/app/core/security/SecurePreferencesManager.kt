package com.learning.app.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.learning.app.core.logging.AppLogger

open class SecurePreferencesManager(context: Context? = null) {

    private val sharedPreferences: SharedPreferences? = context?.let {
        try {
            createEncryptedSharedPreferences(it)
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to create EncryptedSharedPreferences", e)
            null
        }
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_FILENAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            AppLogger.e(TAG, "Resetting EncryptedSharedPreferences due to error", e)
            try {
                context.deleteSharedPreferences(PREFS_FILENAME)
            } catch (delError: Exception) {
                AppLogger.e(TAG, "Failed to delete shared prefs file", delError)
            }

            val fallbackKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_FILENAME,
                fallbackKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    open fun saveAuthToken(token: String, email: String, userId: String? = null) {
        sharedPreferences?.edit {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ID, userId ?: kotlin.math.abs(email.hashCode()).toString())
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    open fun getAuthToken(): String? {
        return sharedPreferences?.getString(KEY_AUTH_TOKEN, null)
    }

    open fun getUserEmail(): String? {
        return sharedPreferences?.getString(KEY_USER_EMAIL, null)
    }

    open fun getUserId(): String? {
        return sharedPreferences?.getString(KEY_USER_ID, null)
    }

    open fun isLoggedIn(): Boolean {
        return sharedPreferences?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
    }

    open fun clearSession() {
        sharedPreferences?.edit {
            clear()
        }
    }

    private companion object {
        private const val TAG = "SecurePrefs"
        private const val PREFS_FILENAME = "secure_learning_prefs"
        private const val KEY_AUTH_TOKEN = "key_auth_token"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }
}
