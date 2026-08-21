package com.mobile.app.di

import com.mobile.app.data.remote.api.SaleApi
import com.mobile.app.data.repository.SaleRepositoryImpl
import com.mobile.app.domain.repository.SaleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SaleModule {

    @Provides
    @Singleton
    fun provideSaleApi(retrofit: Retrofit): SaleApi {
        return retrofit.create(SaleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSaleRepository(
        saleApi: SaleApi
    ): SaleRepository {
        return SaleRepositoryImpl(saleApi)
    }
}
