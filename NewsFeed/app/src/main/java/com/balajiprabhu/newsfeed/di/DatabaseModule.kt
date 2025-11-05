package com.balajiprabhu.newsfeed.di

import android.content.Context
import androidx.room.Room
import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.database.NewsFeedDatabase
import com.balajiprabhu.newsfeed.data.local.source.PostsLocalDataSource
import com.balajiprabhu.newsfeed.data.local.source.PostsLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsFeedDatabase(
        @ApplicationContext context: Context
    ): NewsFeedDatabase {
        return Room.databaseBuilder(
            context,
            NewsFeedDatabase::class.java,
            NewsFeedDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For development; use proper migrations in production
            .build()
    }

    @Provides
    @Singleton
    fun providePostDao(database: NewsFeedDatabase): PostDao {
        return database.postDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindingModule {

    @Binds
    @Singleton
    abstract fun bindPostsLocalDataSource(
        impl: PostsLocalDataSourceImpl
    ): PostsLocalDataSource
}