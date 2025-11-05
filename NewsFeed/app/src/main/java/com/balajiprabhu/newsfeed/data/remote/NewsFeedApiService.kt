package com.balajiprabhu.newsfeed.data.remote

import com.balajiprabhu.newsfeed.data.model.FeedApiResponse
import com.balajiprabhu.newsfeed.data.model.PostDetailApiResponse
import com.balajiprabhu.newsfeed.data.model.NewPostRequest
import com.balajiprabhu.newsfeed.data.model.PostInteractionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service for News Feed
 *
 * Mock Server Endpoints (development):
 * - GET  /feed - Returns array of PostPreview
 * - GET  /posts/{id} - Returns single PostDetail
 * - POST /posts - Create new post
 * - POST /posts/{id}/interact - Like/share post
 */
interface NewsFeedApiService {

    @GET("feed")
    suspend fun getFeed(@Query("page") page: Int): List<com.balajiprabhu.newsfeed.data.model.PostPreview>

    @GET("posts/{postId}")
    suspend fun getPostDetail(@Path("postId") postId: Long): com.balajiprabhu.newsfeed.data.model.PostDetail

    @POST("posts")
    suspend fun createPost(@Body request: NewPostRequest)

    @POST("posts/{postId}/interact")
    suspend fun interactWithPost(@Path("postId") postId: Long, @Body request: PostInteractionRequest)
}
