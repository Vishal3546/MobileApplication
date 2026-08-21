package com.mobile.app.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.StockTransferApi
import com.mobile.app.domain.model.inventory.StockTransfer
import java.util.UUID

class StockTransferPagingSource(
    private val api: StockTransferApi,
    private val status: String?,
    private val branchId: UUID?
) : PagingSource<Int, StockTransfer>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StockTransfer> {
        val page = params.key ?: 0
        return try {
            val response = api.getTransfers(
                page = page,
                size = params.loadSize,
                status = status,
                branchId = branchId
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val items = body.content.map { it.toDomain() }
                    LoadResult.Page(
                        data = items,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (body.last) null else page + 1
                    )
                } else {
                    LoadResult.Error(Exception("Empty response body"))
                }
            } else {
                LoadResult.Error(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StockTransfer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
