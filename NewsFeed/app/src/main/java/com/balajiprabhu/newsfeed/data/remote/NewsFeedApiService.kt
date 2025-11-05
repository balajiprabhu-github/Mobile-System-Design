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

interface NewsFeedApiService {

    @GET("v1/posts")
    suspend fun getFeed(@Query("page") page: Int): FeedApiResponse

    @GET("v1/posts/{postId}")
    suspend fun getPostDetail(@Path("postId") postId: Long): PostDetailApiResponse

    @POST("v1/posts")
    suspend fun createPost(@Body request: NewPostRequest)

    @POST("v1/posts/{postId}/interactions")
    suspend fun interactWithPost(@Path("postId") postId: Long, @Body request: PostInteractionRequest)
}
