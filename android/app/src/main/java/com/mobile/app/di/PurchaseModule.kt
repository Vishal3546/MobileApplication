package com.mobile.app.di

import com.mobile.app.data.remote.api.PurchaseApi
import com.mobile.app.data.repository.PurchaseRepositoryImpl
import com.mobile.app.domain.repository.PurchaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PurchaseModule {

    @Provides
    @Singleton
    fun providePurchaseApi(retrofit: Retrofit): PurchaseApi {
        return retrofit.create(PurchaseApi::class.java)
    }

    @Provides
    @Singleton
    fun providePurchaseRepository(api: PurchaseApi): PurchaseRepository {
        return PurchaseRepositoryImpl(api)
    }
}
