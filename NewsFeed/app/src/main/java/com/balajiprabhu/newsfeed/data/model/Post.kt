package com.balajiprabhu.newsfeed.data.model

data class Post(
    val id: String,
    val authorName: String,
    val content: String,
    val likeCount: Int,
    val timestamp: Long
)
