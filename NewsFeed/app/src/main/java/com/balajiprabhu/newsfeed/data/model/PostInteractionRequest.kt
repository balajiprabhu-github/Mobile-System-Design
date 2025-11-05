package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostInteractionRequest(
    val requestId: Long,
    val type: String
)
