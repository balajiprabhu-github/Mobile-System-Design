package com.balajiprabhu.newsfeed.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedPostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("postId") val postId: Int,
    @SerializedName("contentSummary") val contentSummary: String,
    @SerializedName("author") val author: AuthorDto,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("liked") val liked: Boolean,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("attachmentCount") val attachmentCount: Int,
    @SerializedName("attachmentPreviewImageUrl") val attachmentPreviewImageUrl: String? = null
)

data class AuthorDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profileImageThumbnailUrl") val profileImageThumbnailUrl: String? = null
)
