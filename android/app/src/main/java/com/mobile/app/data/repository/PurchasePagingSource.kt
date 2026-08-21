package com.mobile.app.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.PurchaseApi
import com.mobile.app.domain.model.purchase.Purchase

class PurchasePagingSource(
    private val api: PurchaseApi,
    private val search: String?,
    private val status: String?,
    private val startDate: String?,
    private val endDate: String?
) : PagingSource<Int, Purchase>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Purchase> {
        return try {
            val page = params.key ?: 0
            val response = api.getPurchases(page, params.loadSize, search, status, startDate, endDate)
            
            LoadResult.Page(
                data = response.content.map { it.toDomain() },
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (page < response.totalPages - 1) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Purchase>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
