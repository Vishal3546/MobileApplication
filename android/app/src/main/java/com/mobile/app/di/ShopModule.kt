package com.mobile.app.di

import com.mobile.app.data.repository.ShopRepositoryImpl
import com.mobile.app.domain.repository.ShopRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShopModule {

    @Binds
    @Singleton
    abstract fun bindShopRepository(
        shopRepositoryImpl: ShopRepositoryImpl
    ): ShopRepository
}
