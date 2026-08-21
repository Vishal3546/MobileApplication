package com.mobile.app.core.security

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SecureTokenStorageTest {

    private val context: Context = mockk()
    private val prefs: SharedPreferences = mockk(relaxed = true)
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private val cryptoManager: CryptoManager = mockk()

    private lateinit var tokenStorage: SecureTokenStorage

    @Before
    fun setup() {
        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        tokenStorage = SecureTokenStorage(context, cryptoManager)
    }

    @Test
    fun `saveAccessToken encrypts and saves`() {
        val token = "plain_access_token"
        val encrypted = "encrypted_access_token"
        
        every { cryptoManager.encrypt(token) } returns encrypted

        tokenStorage.saveAccessToken(token)

        verify { cryptoManager.encrypt(token) }
        verify { editor.putString("enc_access_token", encrypted) }
        verify { editor.apply() }
    }

    @Test
    fun `getAccessToken decrypts and returns`() {
        val encrypted = "encrypted_access_token"
        val token = "plain_access_token"

        every { prefs.getString("enc_access_token", null) } returns encrypted
        every { cryptoManager.decrypt(encrypted) } returns token

        val result = tokenStorage.getAccessToken()

        assertEquals(token, result)
        verify { cryptoManager.decrypt(encrypted) }
    }
    
    @Test
    fun `getAccessToken returns null if invalid key or corrupted`() {
        val encrypted = "invalid_encrypted_data"

        every { prefs.getString("enc_access_token", null) } returns encrypted
        every { cryptoManager.decrypt(encrypted) } returns null // Simulate exception internally handled

        val result = tokenStorage.getAccessToken()

        assertNull(result)
    }

    @Test
    fun `clearTokens removes both tokens`() {
        tokenStorage.clearTokens()

        verify { editor.remove("enc_access_token") }
        verify { editor.remove("enc_refresh_token") }
        verify { editor.apply() }
    }
}
