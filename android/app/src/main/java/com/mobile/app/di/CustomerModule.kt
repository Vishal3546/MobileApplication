package com.mobile.app.di

import com.mobile.app.data.remote.api.ConsentApi
import com.mobile.app.data.remote.api.CustomerApi
import com.mobile.app.data.remote.api.KycApi
import com.mobile.app.data.remote.api.MediaApi
import com.mobile.app.data.repository.ConsentRepositoryImpl
import com.mobile.app.data.repository.CustomerRepositoryImpl
import com.mobile.app.data.repository.KycRepositoryImpl
import com.mobile.app.data.repository.MediaRepositoryImpl
import com.mobile.app.domain.repository.ConsentRepository
import com.mobile.app.domain.repository.CustomerRepository
import com.mobile.app.domain.repository.KycRepository
import com.mobile.app.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {

    @Provides
    @Singleton
    fun provideCustomerApi(retrofit: Retrofit): CustomerApi {
        return retrofit.create(CustomerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideKycApi(retrofit: Retrofit): KycApi {
        return retrofit.create(KycApi::class.java)
    }

    @Provides
    @Singleton
    fun provideConsentApi(retrofit: Retrofit): ConsentApi {
        return retrofit.create(ConsentApi::class.java)
    }



    @Provides
    @Singleton
    fun provideCustomerRepository(api: CustomerApi): CustomerRepository {
        return CustomerRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideKycRepository(api: KycApi): KycRepository {
        return KycRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideConsentRepository(api: ConsentApi): ConsentRepository {
        return ConsentRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(api: MediaApi): MediaRepository {
        return MediaRepositoryImpl(api)
    }
}
