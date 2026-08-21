package com.mobile.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.CustomerApi
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.data.remote.dto.UpdateCustomerRequestDto
import com.mobile.app.domain.model.Customer
import com.mobile.app.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerApi: CustomerApi
) : CustomerRepository {

    override fun getCustomers(search: String?): Flow<PagingData<Customer>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { CustomerPagingSource(customerApi, search) }
        ).flow
    }

    override suspend fun getCustomer(id: UUID): Result<Customer> {
        return try {
            val response = customerApi.getCustomer(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data.toDomain())
            } else {
                Result.failure(Exception(body?.message ?: "API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCustomer(request: CreateCustomerRequestDto): Result<Customer> {
        return try {
            val response = customerApi.createCustomer(request)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data.toDomain())
            } else {
                Result.failure(Exception(body?.message ?: "API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCustomer(id: UUID, request: UpdateCustomerRequestDto): Result<Customer> {
        return try {
            val response = customerApi.updateCustomer(id, request)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data.toDomain())
            } else {
                Result.failure(Exception(body?.message ?: "API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCustomerStatus(id: UUID, status: String): Result<Customer> {
        return try {
            val response = customerApi.updateCustomerStatus(id, status)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data.toDomain())
            } else {
                Result.failure(Exception(body?.message ?: "API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
