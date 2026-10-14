package com.balajiprabhu.newsfeed.data.remote

import com.balajiprabhu.newsfeed.data.remote.api.NewsFeedApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    // 10.0.2.2 maps to host localhost:3000 inside Android Emulator
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/"

    fun createApiService(
        baseUrl: String = DEFAULT_BASE_URL,
        serverTimeInterceptor: ServerTimeInterceptor? = null
    ): NewsFeedApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        if (serverTimeInterceptor != null) {
            okHttpClientBuilder.addInterceptor(serverTimeInterceptor)
        }

        val okHttpClient = okHttpClientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsFeedApiService::class.java)
    }
}
