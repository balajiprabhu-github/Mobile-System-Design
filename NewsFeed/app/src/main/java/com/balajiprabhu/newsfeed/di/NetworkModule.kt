package com.balajiprabhu.newsfeed.di

import com.balajiprabhu.newsfeed.data.remote.NetworkClient
import com.balajiprabhu.newsfeed.data.remote.ServerTimeInterceptor
import com.balajiprabhu.newsfeed.data.remote.api.NewsFeedApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNewsFeedApiService(
        serverTimeInterceptor: ServerTimeInterceptor
    ): NewsFeedApiService {
        return NetworkClient.createApiService(serverTimeInterceptor = serverTimeInterceptor)
    }
}
