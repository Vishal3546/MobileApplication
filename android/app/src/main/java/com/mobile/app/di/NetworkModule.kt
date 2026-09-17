package com.mobile.app.di

import com.mobile.app.core.network.AuthInterceptor
import com.mobile.app.core.network.TokenRefreshAuthenticator
import com.mobile.app.core.security.TokenStorage
import com.mobile.app.data.remote.AuthApi
import com.mobile.app.data.remote.HealthApi
import com.mobile.app.BuildConfig
import com.mobile.app.data.remote.api.PhoneSpecsApi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import okhttp3.Interceptor
import okhttp3.Response

class RetryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        var tryCount = 0
        while (!response.isSuccessful && tryCount < 3 && (response.code == 502 || response.code == 503 || response.code == 504)) {
            tryCount++
            response.close()
            try {
                Thread.sleep(2000)
            } catch (_: InterruptedException) {
                // Ignore
            }
            response = chain.proceed(chain.request())
        }
        return response
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenStorage: TokenStorage): AuthInterceptor {
        return AuthInterceptor(tokenStorage)
    }

    @Provides
    @Singleton
    fun provideTokenRefreshAuthenticator(
        tokenStorage: TokenStorage,
        authApi: Lazy<AuthApi>,
    ): TokenRefreshAuthenticator {
        return TokenRefreshAuthenticator(tokenStorage, authApi)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .addInterceptor(RetryInterceptor())
            .authenticator(tokenRefreshAuthenticator)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHealthApi(retrofit: Retrofit): HealthApi {
        return retrofit.create(HealthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMediaApi(retrofit: Retrofit): com.mobile.app.data.remote.api.MediaApi {
        return retrofit.create(com.mobile.app.data.remote.api.MediaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideShopApi(retrofit: Retrofit): com.mobile.app.data.remote.api.ShopApi {
        return retrofit.create(com.mobile.app.data.remote.api.ShopApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): com.mobile.app.data.remote.api.UserApi {
        return retrofit.create(com.mobile.app.data.remote.api.UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportApi(retrofit: Retrofit): com.mobile.app.data.remote.api.ReportApi {
        return retrofit.create(com.mobile.app.data.remote.api.ReportApi::class.java)
    }

    @Provides
    @Singleton
    fun providePhoneSpecsApi(): PhoneSpecsApi {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://phone-specs-api.vercel.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhoneSpecsApi::class.java)
    }
}
