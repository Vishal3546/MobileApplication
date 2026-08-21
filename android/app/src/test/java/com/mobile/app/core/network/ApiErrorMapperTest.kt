package com.mobile.app.core.network

import com.mobile.app.domain.model.ApiErrorType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ApiErrorMapperTest {

    @Test
    fun `map IOException returns Unknown and Network error`() {
        val exception = IOException("No internet")
        val (type, message) = ApiErrorMapper.map(exception)
        assertEquals(ApiErrorType.Unknown, type)
        assertEquals("Network error occurred", message)
    }

    @Test
    fun `map HttpException 401 returns SessionExpired`() {
        val exception = HttpException(Response.error<Any>(401, "".toResponseBody()))
        val (type, _) = ApiErrorMapper.map(exception)
        assertEquals(ApiErrorType.SessionExpired, type)
    }

    @Test
    fun `map HttpException 403 returns PermissionDenied`() {
        val exception = HttpException(Response.error<Any>(403, "".toResponseBody()))
        val (type, _) = ApiErrorMapper.map(exception)
        assertEquals(ApiErrorType.PermissionDenied, type)
    }
}
