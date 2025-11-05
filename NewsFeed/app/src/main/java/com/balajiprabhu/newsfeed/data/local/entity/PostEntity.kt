package com.balajiprabhu.newsfeed.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val postId: Long,
    val content: String,
    val contentSummary: String,
    @Embedded(prefix = "author_")
    val author: AuthorEntity,
    val createdAt: String,
    val liked: Boolean,
    val likeCount: Int,
    val shareCount: Int,
    val attachmentCount: Int,
    val attachmentPreviewImageUrl: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

data class AuthorEntity(
    val id: Long,
    val name: String,
    val profileImageThumbnailUrl: String
)