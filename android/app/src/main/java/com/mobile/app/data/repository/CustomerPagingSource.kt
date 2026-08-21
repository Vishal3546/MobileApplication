package com.mobile.app.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.CustomerApi
import com.mobile.app.domain.model.Customer

class CustomerPagingSource(
    private val customerApi: CustomerApi,
    private val search: String?
) : PagingSource<Int, Customer>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Customer> {
        val page = params.key ?: 0
        return try {
            val response = customerApi.getCustomers(
                search = search,
                page = page,
                size = params.loadSize
            )
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val data = body.data
                val customers = data.content?.map { it.toDomain() } ?: emptyList()
                LoadResult.Page(
                    data = customers,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (data.last) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(body?.message ?: "Unknown API error"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Customer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
