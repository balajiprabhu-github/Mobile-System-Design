package com.balajiprabhu.newsfeed.di

import com.balajiprabhu.newsfeed.data.repository.FeedRepository
import com.balajiprabhu.newsfeed.data.repository.FeedRepositoryImpl
import com.balajiprabhu.newsfeed.data.util.ServerTimeProvider
import com.balajiprabhu.newsfeed.data.util.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFeedRepository(
        feedRepositoryImpl: FeedRepositoryImpl
    ): FeedRepository

    @Binds
    @Singleton
    abstract fun bindTimeProvider(
        serverTimeProvider: ServerTimeProvider
    ): TimeProvider
}
