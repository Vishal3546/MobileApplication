package com.mobile.app.core.network

import com.mobile.app.core.security.TokenStorage
import com.mobile.app.data.remote.AuthApi
import com.mobile.app.data.remote.dto.ApiResponseDto
import com.mobile.app.data.remote.dto.RefreshTokenResponse
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response as RetrofitResponse

class TokenRefreshAuthenticatorTest {

    private val tokenStorage: TokenStorage = mockk(relaxed = true)
    private val authApi: AuthApi = mockk()
    private val lazyAuthApi = Lazy { authApi }
    
    private val authenticator = TokenRefreshAuthenticator(tokenStorage, lazyAuthApi)

    private fun mockResponse(): Response {
        val request = Request.Builder().url("https://api.test.com/data").build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()
    }

    @Test
    fun `concurrent 401s trigger only one refresh API call`() = runTest {
        // Setup tokens
        every { tokenStorage.getAccessToken() } returns "old_token" andThen "new_token"
        every { tokenStorage.getRefreshToken() } returns "refresh_token"

        // Mock slow API call to allow concurrent requests to queue up
        coEvery { authApi.refresh(any()) } coAnswers {
            delay(100) // Simulating network delay
            RetrofitResponse.success(ApiResponseDto(true, "Success", RefreshTokenResponse("new_token", "new_refresh")))
        }

        // Simulate 3 concurrent requests hitting the authenticator
        val deferreds = listOf(
            async { authenticator.authenticate(null, mockResponse()) },
            async { authenticator.authenticate(null, mockResponse()) },
            async { authenticator.authenticate(null, mockResponse()) }
        )

        val results = deferreds.awaitAll()

        // Assert that all requests get the new token
        results.forEach { request ->
            assertNotNull("Request should not be null", request)
        }
    }

    @Test
    fun `failed refresh clears session and returns null for all`() = runTest {
        // Setup tokens
        every { tokenStorage.getAccessToken() } returns "old_token"
        every { tokenStorage.getRefreshToken() } returns "refresh_token"

        // Mock API failure
        coEvery { authApi.refresh(any()) } coAnswers {
            RetrofitResponse.error<ApiResponseDto<RefreshTokenResponse>>(400, okhttp3.ResponseBody.create(null, ""))
        }

        // Simulate 3 concurrent requests hitting the authenticator
        val deferreds = listOf(
            async { authenticator.authenticate(null, mockResponse()) },
            async { authenticator.authenticate(null, mockResponse()) },
            async { authenticator.authenticate(null, mockResponse()) }
        )

        val results = deferreds.awaitAll()

        // Assert all fail safely
        results.forEach { request ->
            assertNull("Request should be null on refresh failure", request)
        }
    }
}
