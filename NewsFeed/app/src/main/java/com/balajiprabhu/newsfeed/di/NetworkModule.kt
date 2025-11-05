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

    /**
     * Base URL for the News Feed API
     *
     * For development with mock server:
     * - Android Emulator: http://10.0.2.2:3000/
     * - Physical Device: http://YOUR_LOCAL_IP:3000/ (e.g., http://192.168.1.100:3000/)
     * - Localhost (for tests): http://localhost:3000/
     *
     * Make sure to:
     * 1. Start the mock server: cd mock-server && npm start
     * 2. Use the appropriate URL for your testing environment
     */
    private const val BASE_URL = "http://10.0.2.2:3000/"

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
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsFeedApiService(retrofit: Retrofit): NewsFeedApiService {
        return retrofit.create(NewsFeedApiService::class.java)
    }
}
