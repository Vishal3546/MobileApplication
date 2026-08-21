package com.mobile.app.presentation.auth

import app.cash.turbine.test
import com.mobile.app.domain.model.AuthState
import com.mobile.app.domain.model.CurrentUser
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var classUnderTest: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init checks session and sets Authenticated if valid`() = runTest {
        every { authRepository.hasValidSession() } returns true
        classUnderTest = AuthViewModel(authRepository)
        
        classUnderTest.authState.test {
            assertEquals(AuthState.Authenticated, awaitItem())
        }
    }

    @Test
    fun `init checks session and sets Unauthenticated if invalid`() = runTest {
        every { authRepository.hasValidSession() } returns false
        classUnderTest = AuthViewModel(authRepository)
        
        classUnderTest.authState.test {
            assertEquals(AuthState.Unauthenticated, awaitItem())
        }
    }

    @Test
    fun `login success sets Authenticated`() = runTest {
        every { authRepository.hasValidSession() } returns false
        classUnderTest = AuthViewModel(authRepository)
        
        val user = CurrentUser("1", "test", emptyList(), emptyList())
        coEvery { authRepository.login(any(), any()) } returns NetworkState.Success(user)
        
        var loadingState = false
        classUnderTest.login("test", "pass") { loadingState = it }
        testScheduler.advanceUntilIdle()
        
        classUnderTest.authState.test {
            assertEquals(AuthState.Authenticated, awaitItem())
        }
    }

    @Test
    fun `logout clears session and sets Unauthenticated`() = runTest {
        every { authRepository.hasValidSession() } returns true
        classUnderTest = AuthViewModel(authRepository)
        
        coEvery { authRepository.logout() } returns NetworkState.Success(Unit)
        
        classUnderTest.logout()
        testScheduler.advanceUntilIdle()
        
        classUnderTest.authState.test {
            assertEquals(AuthState.Unauthenticated, awaitItem())
        }
        coVerify { authRepository.logout() }
    }
}
