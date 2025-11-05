package com.balajiprabhu.newsfeed.di

import com.balajiprabhu.newsfeed.data.remote.AuthInterceptor
import com.balajiprabhu.newsfeed.data.remote.NewsFeedApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://your.api.base.url/"

    @Provides
    @Singleton
    fun provideAuthToken(): String {
        // In a real app, you'd get this from a secure storage
        return "your-bearer-token"
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authToken: String): AuthInterceptor {
        return AuthInterceptor(authToken)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsFeedApiService(retrofit: Retrofit): NewsFeedApiService {
        return retrofit.create(NewsFeedApiService::class.java)
    }
}
