package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostDetailApiResponse(
    val post: PostDetail
)
