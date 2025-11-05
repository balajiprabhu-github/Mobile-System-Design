package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.model.FeedApiResponse
import com.balajiprabhu.newsfeed.data.model.PostDetail
import com.balajiprabhu.newsfeed.data.model.NewPostRequest
import com.balajiprabhu.newsfeed.data.model.PostInteractionRequest
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeed(page: Int): Flow<Result<FeedApiResponse>>
    suspend fun getPostDetail(postId: Long): Flow<Result<PostDetail>>
    suspend fun createPost(request: NewPostRequest): Flow<Result<Unit>>
    suspend fun interactWithPost(postId: Long, request: PostInteractionRequest): Flow<Result<Unit>>
}
