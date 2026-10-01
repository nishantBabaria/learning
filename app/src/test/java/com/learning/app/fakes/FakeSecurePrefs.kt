package com.learning.app.fakes

import com.learning.app.core.security.SecurePreferencesManager

class FakeSecurePrefs : SecurePreferencesManager() {
    override fun getUserEmail(): String = "test@learning.com"
    override fun isLoggedIn(): Boolean = true
}
