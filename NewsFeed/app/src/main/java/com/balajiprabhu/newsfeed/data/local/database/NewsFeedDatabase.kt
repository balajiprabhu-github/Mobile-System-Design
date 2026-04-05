package com.balajiprabhu.newsfeed.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class NewsFeedDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}