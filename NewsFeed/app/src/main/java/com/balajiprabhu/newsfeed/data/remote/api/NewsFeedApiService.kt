package com.balajiprabhu.newsfeed.data.remote.api

import com.balajiprabhu.newsfeed.data.remote.dto.FeedPostDto
import com.balajiprabhu.newsfeed.data.remote.dto.InteractionRequest
import com.balajiprabhu.newsfeed.data.remote.dto.InteractionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsFeedApiService {

    @GET("api/feed")
    suspend fun getFeed(
        @Query("_page") page: Int = 1,
        @Query("_limit") limit: Int = 20,
        @Query("_sort") sort: String = "id",
        @Query("_order") order: String = "desc"
    ): List<FeedPostDto>

    @POST("api/posts/{postId}/interact")
    suspend fun interactWithPost(
        @Path("postId") postId: Int,
        @Body request: InteractionRequest
    ): InteractionResponse
}
