package com.balajiprabhu.newsfeed.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.entity.AttachmentEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity

@Database(
    entities = [
        PostEntity::class,
        AttachmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NewsFeedDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        const val DATABASE_NAME = "newsfeed_db"
    }
}