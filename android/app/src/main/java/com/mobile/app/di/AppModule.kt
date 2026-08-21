package com.mobile.app.di

import android.content.Context
import com.mobile.app.core.security.CryptoManager
import com.mobile.app.core.security.SecureTokenStorage
import com.mobile.app.core.security.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCryptoManager(): CryptoManager {
        return CryptoManager()
    }

    @Provides
    @Singleton
    fun provideTokenStorage(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): TokenStorage {
        return SecureTokenStorage(context, cryptoManager)
    }
}
