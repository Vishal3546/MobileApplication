package com.mobile.app.presentation.kyc

import com.mobile.app.domain.enums.IdType
import com.mobile.app.domain.enums.VerificationStatus
import com.mobile.app.domain.model.KycDocument
import com.mobile.app.domain.repository.KycRepository
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
class KycViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var kycRepository: KycRepository
    private lateinit var viewModel: KycViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        kycRepository = mockk(relaxed = true)
        viewModel = KycViewModel(kycRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDocuments success updates listState to Success`() = runTest(testDispatcher) {
        val customerId = UUID.randomUUID()
        val docs = listOf(
            KycDocument(UUID.randomUUID(), customerId, IdType.NATIONAL_ID, "XXXX1234", null, null, null, VerificationStatus.VERIFIED, null, null)
        )
        coEvery { kycRepository.getCustomerDocuments(customerId) } returns Result.success(docs)

        viewModel.loadDocuments(customerId)
        
        testScheduler.advanceUntilIdle()

        val state = viewModel.listState.value
        assertTrue(state is KycListState.Success)
        assertEquals(docs, (state as KycListState.Success).documents)
    }
}
