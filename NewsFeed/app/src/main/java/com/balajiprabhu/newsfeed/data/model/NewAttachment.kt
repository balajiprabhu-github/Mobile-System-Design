package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NewAttachment(
    val type: String,
    val contentUrl: String,
    val caption: String?
)
