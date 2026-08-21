package com.mobile.app.domain.repository

import androidx.paging.PagingData
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.data.remote.dto.UpdateCustomerRequestDto
import com.mobile.app.domain.model.Customer
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CustomerRepository {
    fun getCustomers(search: String?): Flow<PagingData<Customer>>
    suspend fun getCustomer(id: UUID): Result<Customer>
    suspend fun createCustomer(request: CreateCustomerRequestDto): Result<Customer>
    suspend fun updateCustomer(id: UUID, request: UpdateCustomerRequestDto): Result<Customer>
    suspend fun updateCustomerStatus(id: UUID, status: String): Result<Customer>
}
