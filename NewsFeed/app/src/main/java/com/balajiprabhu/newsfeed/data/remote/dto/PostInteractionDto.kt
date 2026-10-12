package com.balajiprabhu.newsfeed.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InteractionRequest(
    @SerializedName("type") val type: String
)

data class InteractionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("liked") val liked: Boolean? = null,
    @SerializedName("likeCount") val likeCount: Int? = null,
    @SerializedName("shareCount") val shareCount: Int? = null
)
