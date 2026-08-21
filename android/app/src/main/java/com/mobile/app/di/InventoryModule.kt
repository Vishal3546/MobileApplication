package com.mobile.app.di

import com.mobile.app.data.remote.api.InventoryApi
import com.mobile.app.data.remote.api.StockTransferApi
import com.mobile.app.data.repository.InventoryRepositoryImpl
import com.mobile.app.domain.repository.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {

    @Provides
    @Singleton
    fun provideInventoryApi(retrofit: Retrofit): InventoryApi {
        return retrofit.create(InventoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStockTransferApi(retrofit: Retrofit): StockTransferApi {
        return retrofit.create(StockTransferApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(
        inventoryApi: InventoryApi,
        stockTransferApi: StockTransferApi
    ): InventoryRepository {
        return InventoryRepositoryImpl(inventoryApi, stockTransferApi)
    }
}
