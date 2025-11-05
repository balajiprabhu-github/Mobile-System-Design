package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Long,
    val type: String,
    val contentUrl: String,
    val previewImageUrl: String?,
    val caption: String?
)
