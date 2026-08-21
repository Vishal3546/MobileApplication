package com.mobile.app.core.security

import android.content.Context
import android.content.SharedPreferences

class SecureTokenStorage(
    context: Context,
    private val cryptoManager: CryptoManager
) : TokenStorage {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveAccessToken(token: String) {
        val encrypted = cryptoManager.encrypt(token)
        prefs.edit().putString(KEY_ACCESS_TOKEN, encrypted).apply()
    }

    override fun getAccessToken(): String? {
        val encrypted = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        return cryptoManager.decrypt(encrypted)
    }

    override fun saveRefreshToken(token: String) {
        val encrypted = cryptoManager.encrypt(token)
        prefs.edit().putString(KEY_REFRESH_TOKEN, encrypted).apply()
    }

    override fun getRefreshToken(): String? {
        val encrypted = prefs.getString(KEY_REFRESH_TOKEN, null) ?: return null
        return cryptoManager.decrypt(encrypted)
    }

    override fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "secure_token_prefs"
        private const val KEY_ACCESS_TOKEN = "enc_access_token"
        private const val KEY_REFRESH_TOKEN = "enc_refresh_token"
    }
}
