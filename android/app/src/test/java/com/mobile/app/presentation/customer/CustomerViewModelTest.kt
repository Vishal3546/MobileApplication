package com.mobile.app.presentation.customer

import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.domain.enums.CustomerStatus
import com.mobile.app.domain.enums.VerificationStatus
import com.mobile.app.domain.model.Customer
import com.mobile.app.domain.repository.CustomerRepository
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
class CustomerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var customerRepository: CustomerRepository
    private lateinit var viewModel: CustomerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        customerRepository = mockk(relaxed = true)
        viewModel = CustomerViewModel(customerRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCustomer success updates detailState to Success`() = runTest(testDispatcher) {
        val customerId = UUID.randomUUID()
        val customer = Customer(customerId, "John", "Doe", "123", null, null, null, CustomerStatus.ACTIVE, VerificationStatus.VERIFIED)
        coEvery { customerRepository.getCustomer(customerId) } returns Result.success(customer)

        viewModel.loadCustomer(customerId)
        
        testScheduler.advanceUntilIdle()

        val state = viewModel.detailState.value
        assertTrue(state is CustomerDetailState.Success)
        assertEquals(customer, (state as CustomerDetailState.Success).customer)
    }

    @Test
    fun `createCustomer success updates actionState to Success`() = runTest(testDispatcher) {
        val request = CreateCustomerRequestDto("John", "Doe", "123", null, null, null)
        val customer = Customer(UUID.randomUUID(), "John", "Doe", "123", null, null, null, CustomerStatus.ACTIVE, VerificationStatus.VERIFIED)
        coEvery { customerRepository.createCustomer(request) } returns Result.success(customer)

        viewModel.createCustomer(request)
        
        testScheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(state is CustomerActionState.Success)
    }
}
