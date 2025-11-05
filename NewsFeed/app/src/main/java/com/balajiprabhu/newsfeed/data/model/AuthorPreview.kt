package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthorPreview(
    val id: Long,
    val name: String,
    val profileImageThumbnailUrl: String
)
