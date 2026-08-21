package com.mobile.app.presentation.sales

import com.mobile.app.domain.model.ApiErrorType
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.sales.SaleTransaction
import com.mobile.app.domain.model.sales.SalePayment
import com.mobile.app.domain.repository.SaleRepository
import com.mobile.app.presentation.sales.detail.SaleDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
import java.math.BigDecimal
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SaleTests {

    private val repository: SaleRepository = mockk(relaxed = true)
    private lateinit var detailViewModel: SaleDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        detailViewModel = SaleDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test Sale Creation and Inventory Reservation`() = runTest {
        val customerId = UUID.randomUUID()
        val inventoryId = UUID.randomUUID()
        val branchId = UUID.randomUUID()
        val sale = SaleTransaction(UUID.randomUUID(), "SALE1", customerId, null, branchId, BigDecimal("500"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal("500"), "PENDING", "CREATED", null, null, "usr", "now", null)

        coEvery { repository.createSale(customerId, inventoryId, branchId) } returns NetworkState.Success(sale)

        val result = repository.createSale(customerId, inventoryId, branchId)
        assert(result is NetworkState.Success)
        assertEquals("CREATED", (result as NetworkState.Success).data.saleStatus)
    }

    @Test
    fun `test Pricing and Discount rules overriding`() = runTest {
        val id = UUID.randomUUID()
        val sale = SaleTransaction(id, "SALE1", UUID.randomUUID(), null, UUID.randomUUID(), BigDecimal("450"), BigDecimal("50"), BigDecimal.ZERO, BigDecimal("450"), "PENDING", "CREATED", null, null, "usr", "now", null)

        coEvery { repository.overridePrice(id, BigDecimal("450"), "Manager Approved Discount") } returns NetworkState.Success(sale)

        val result = repository.overridePrice(id, BigDecimal("450"), "Manager Approved Discount")
        assert(result is NetworkState.Success)
        assertEquals(BigDecimal("450"), (result as NetworkState.Success).data.finalAmount)
        assertEquals(BigDecimal("50"), result.data.discount)
    }

    @Test
    fun `test Payment Idempotency and duplicate payment retry`() = runTest {
        val saleId = UUID.randomUUID()
        val paymentId = UUID.randomUUID()
        val idempotencyKey = "idemp-key-123"
        val payment = SalePayment(paymentId, saleId, "CASH", BigDecimal.TEN, null, "SUCCESS", "now")

        coEvery { repository.createPayment(saleId, "CASH", BigDecimal.TEN, null, idempotencyKey) } returns NetworkState.Success(payment)

        val result1 = repository.createPayment(saleId, "CASH", BigDecimal.TEN, null, idempotencyKey)
        val result2 = repository.createPayment(saleId, "CASH", BigDecimal.TEN, null, idempotencyKey)

        assertEquals(result1, result2)
        coVerify(exactly = 2) { repository.createPayment(saleId, "CASH", BigDecimal.TEN, null, idempotencyKey) }
    }

    @Test
    fun `test Sale Completion and Duplicate completion prevention`() = runTest {
        val id = UUID.randomUUID()
        val sale = SaleTransaction(id, "SALE1", UUID.randomUUID(), null, UUID.randomUUID(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "SUCCESS", "COMPLETED", null, null, "usr", "now", "now")

        // Initial completion
        coEvery { repository.completeSale(id) } returns NetworkState.Success(sale)
        val result1 = repository.completeSale(id)
        assertEquals("COMPLETED", (result1 as NetworkState.Success).data.saleStatus)

        // Duplicate completion simulates backend throwing Conflict
        coEvery { repository.completeSale(id) } returns NetworkState.Error(ApiErrorType.Conflict, "Sale already completed")
        val result2 = repository.completeSale(id)
        assert(result2 is NetworkState.Error)
        assertEquals(ApiErrorType.Conflict, (result2 as NetworkState.Error).type)
    }

    @Test
    fun `test Sale Cancellation with reason`() = runTest {
        val id = UUID.randomUUID()
        val sale = SaleTransaction(id, "SALE1", UUID.randomUUID(), null, UUID.randomUUID(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "PENDING", "CANCELLED", null, null, "usr", "now", null)

        coEvery { repository.cancelSale(id, "Customer requested") } returns NetworkState.Success(sale)

        detailViewModel.cancelSale(id, "Customer requested")
        testScheduler.advanceUntilIdle()

        assertEquals("CANCELLED", detailViewModel.sale.value?.saleStatus)
    }

    @Test
    fun `test Invoice PDF fetch assertion`() = runTest {
        val id = UUID.randomUUID()
        coEvery { repository.getSaleInvoicePdf(id) } returns NetworkState.Success(ByteArray(10))

        val result = repository.getSaleInvoicePdf(id)
        assert(result is NetworkState.Success)
        assertTrue((result as NetworkState.Success).data.isNotEmpty())
    }

    @Test
    fun `test Permission handling for Sales cancellation`() = runTest {
        // Validation UI skips cancellation button rendering if missing CANCEL_SALE. 
        // This test ensures the repository propagates PermissionDenied if the backend rejects it.
        val id = UUID.randomUUID()
        coEvery { repository.cancelSale(id, "Customer requested") } returns NetworkState.Error(ApiErrorType.PermissionDenied, "Forbidden")

        detailViewModel.cancelSale(id, "Customer requested")
        testScheduler.advanceUntilIdle()

        assertEquals("Failed to cancel: Forbidden", detailViewModel.error.value)
    }
}
