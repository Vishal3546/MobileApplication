package com.mobile.app.di

import com.mobile.app.data.remote.api.DeviceApi
import com.mobile.app.data.remote.api.DeviceConditionApi
import com.mobile.app.data.remote.api.DeviceInspectionApi
import com.mobile.app.data.remote.api.DeviceMediaApi
import com.mobile.app.data.remote.api.MediaApi
import com.mobile.app.data.repository.device.DeviceConditionRepositoryImpl
import com.mobile.app.data.repository.device.DeviceInspectionRepositoryImpl
import com.mobile.app.data.repository.device.DeviceMediaRepositoryImpl
import com.mobile.app.data.repository.device.DeviceRepositoryImpl
import com.mobile.app.domain.repository.device.DeviceConditionRepository
import com.mobile.app.domain.repository.device.DeviceInspectionRepository
import com.mobile.app.domain.repository.device.DeviceMediaRepository
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {

    @Provides
    @Singleton
    fun provideDeviceApi(retrofit: Retrofit): DeviceApi {
        return retrofit.create(DeviceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceConditionApi(retrofit: Retrofit): DeviceConditionApi {
        return retrofit.create(DeviceConditionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceInspectionApi(retrofit: Retrofit): DeviceInspectionApi {
        return retrofit.create(DeviceInspectionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceMediaApi(retrofit: Retrofit): DeviceMediaApi {
        return retrofit.create(DeviceMediaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(api: DeviceApi): DeviceRepository {
        return DeviceRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDeviceConditionRepository(api: DeviceConditionApi): DeviceConditionRepository {
        return DeviceConditionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDeviceInspectionRepository(api: DeviceInspectionApi): DeviceInspectionRepository {
        return DeviceInspectionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDeviceMediaRepository(
        deviceMediaApi: DeviceMediaApi,
        mediaApi: MediaApi
    ): DeviceMediaRepository {
        return DeviceMediaRepositoryImpl(deviceMediaApi, mediaApi)
    }
}
