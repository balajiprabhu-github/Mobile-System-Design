package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginationMetadata(
    val nextPageUrl: String?,
    val hasMore: Boolean,
    val pageSize: Int = 20,
    val totalCount: Int? = null
)
