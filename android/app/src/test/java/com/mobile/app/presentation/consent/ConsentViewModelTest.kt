package com.mobile.app.presentation.consent

import com.mobile.app.data.remote.dto.CaptureConsentRequestDto
import com.mobile.app.domain.enums.ConsentType
import com.mobile.app.domain.model.Consent
import com.mobile.app.domain.repository.ConsentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ConsentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var consentRepository: ConsentRepository
    private lateinit var viewModel: ConsentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        consentRepository = mockk(relaxed = true)
        viewModel = ConsentViewModel(consentRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `captureConsent success updates actionState to Success`() = runTest(testDispatcher) {
        val customerId = UUID.randomUUID()
        val request = CaptureConsentRequestDto("KYC_CONSENT", "v1", null, null, "1.2.3.4", "TestDevice")
        val consent = Consent(UUID.randomUUID(), customerId, ConsentType.KYC_CONSENT, "v1", null, null, null)
        
        coEvery { consentRepository.captureConsent(customerId, request) } returns Result.success(consent)
        // Mock loadConsents since captureConsent triggers a reload
        coEvery { consentRepository.getCustomerConsents(customerId) } returns Result.success(emptyList())

        viewModel.captureConsent(customerId, request)
        
        testScheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(state is ConsentActionState.Success)
    }
}
