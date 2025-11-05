package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostDetail(
    val postId: Long,
    val content: String,
    val author: AuthorPreview,
    val createdAt: String,
    val liked: Boolean,
    val likeCount: Int,
    val shareCount: Int,
    val attachments: List<Attachment>
)
