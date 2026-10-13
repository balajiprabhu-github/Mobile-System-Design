package com.balajiprabhu.newsfeed.di

import android.content.Context
import androidx.room.Room
import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.database.NewsFeedDatabase
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
            "news_feed_db"
        ).build()
    }

    @Provides
    fun providePostDao(database: NewsFeedDatabase): PostDao {
        return database.postDao()
    }
}
