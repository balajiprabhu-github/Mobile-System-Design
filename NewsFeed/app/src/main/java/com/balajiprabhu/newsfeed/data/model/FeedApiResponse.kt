package com.balajiprabhu.newsfeed.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedApiResponse(
    val feed: List<PostPreview>,
    val paging: PaginationMetadata
)
