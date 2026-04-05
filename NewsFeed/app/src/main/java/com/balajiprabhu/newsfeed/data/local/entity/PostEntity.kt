package com.balajiprabhu.newsfeed.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val content: String,
    val likeCount: Int,
    val timeStamp: Long,
    val cachedAt: Long
)