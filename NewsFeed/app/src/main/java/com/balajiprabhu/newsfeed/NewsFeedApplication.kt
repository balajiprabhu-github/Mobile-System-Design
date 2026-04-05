package com.balajiprabhu.newsfeed

import android.app.Application
import androidx.room.Room
import com.balajiprabhu.newsfeed.data.local.database.NewsFeedDatabase
import com.balajiprabhu.newsfeed.data.repository.FeedRepositoryImpl

class NewsFeedApplication : Application() {

    val database : NewsFeedDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            NewsFeedDatabase::class.java,
            "news_feed_db"
        ).build()
    }

    val feedRepository by lazy {
        FeedRepositoryImpl(
            database.postDao()
        )
    }
}
