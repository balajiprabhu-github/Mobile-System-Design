package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NewPostRequest(
    val requestId: Long,
    val content: String,
    val attachments: List<NewAttachment>
)
