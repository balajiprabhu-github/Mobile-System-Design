package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostPreview(
    val postId: Long,
    val contentSummary: String,
    val author: AuthorPreview,
    val createdAt: String,
    val liked: Boolean,
    val likeCount: Int,
    val attachmentCount: Int,
    val attachmentPreviewImageUrl: String?
)
