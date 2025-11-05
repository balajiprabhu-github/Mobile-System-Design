package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.local.source.PostsLocalDataSource
import com.balajiprabhu.newsfeed.data.mapper.toDetail
import com.balajiprabhu.newsfeed.data.mapper.toEntity
import com.balajiprabhu.newsfeed.data.mapper.toEntityWithAttachments
import com.balajiprabhu.newsfeed.data.mapper.toPreview
import com.balajiprabhu.newsfeed.data.model.FeedApiResponse
import com.balajiprabhu.newsfeed.data.model.PostDetail
import com.balajiprabhu.newsfeed.data.model.NewPostRequest
import com.balajiprabhu.newsfeed.data.model.PostInteractionRequest
import com.balajiprabhu.newsfeed.data.remote.NewsFeedApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val apiService: NewsFeedApiService,
    private val localDataSource: PostsLocalDataSource
) : FeedRepository {

    companion object {
        private const val CACHE_EXPIRY_MS = 5 * 60 * 1000L // 5 minutes
    }

    override suspend fun getFeed(page: Int): Flow<Result<FeedApiResponse>> = flow {
        try {
            // Cache-first strategy for first page only
            if (page == 0) {
                // 1. Try to load from cache first
                val cachedPosts = localDataSource.getAllPosts()
                if (cachedPosts.isNotEmpty()) {
                    // Emit cached data immediately for fast UI
                    val cachedFeed = FeedApiResponse(
                        feed = cachedPosts.map { it.toPreview() },
                        paging = com.balajiprabhu.newsfeed.data.model.PaginationMetadata(
                            nextPageUrl = null,
                            hasMore = false
                        )
                    )
                    emit(Result.success(cachedFeed))
                }
            }

            // 2. Fetch fresh data from network (mock server returns List<PostPreview>)
            val posts = apiService.getFeed(page)

            // 3. Update cache for first page
            if (page == 0) {
                // Clear old cache and insert new data
                localDataSource.clearAllPosts()
                val postEntities = posts.map { it.toEntity() }
                localDataSource.insertPosts(postEntities)
            }

            // 4. Emit network response wrapped in FeedApiResponse
            val response = FeedApiResponse(
                feed = posts,
                paging = com.balajiprabhu.newsfeed.data.model.PaginationMetadata(
                    nextPageUrl = null,
                    hasMore = posts.size >= 20 // Assume more if we got a full page
                )
            )
            emit(Result.success(response))

        } catch (e: Exception) {
            // If network fails and we have cache, we've already emitted it
            // Otherwise, emit the error
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getPostDetail(postId: Long): Flow<Result<PostDetail>> = flow {
        try {
            // 1. Try to load from cache first
            val cachedPost = localDataSource.getPostWithAttachments(postId)
            if (cachedPost != null) {
                // Emit cached data immediately
                emit(Result.success(cachedPost.toDetail()))
            }

            // 2. Fetch fresh data from network (mock server returns PostDetail directly)
            val post = apiService.getPostDetail(postId)

            // 3. Update cache
            val (postEntity, attachmentEntities) = post.toEntityWithAttachments()
            localDataSource.insertPost(postEntity)
            localDataSource.insertAttachments(attachmentEntities)

            // 4. Emit network response
            emit(Result.success(post))

        } catch (e: Exception) {
            // If network fails and we have cache, we've already emitted it
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createPost(request: NewPostRequest): Flow<Result<Unit>> = flow {
        try {
            apiService.createPost(request)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun interactWithPost(postId: Long, request: PostInteractionRequest): Flow<Result<Unit>> = flow {
        try {
            // Optimistic update: Update local cache first for instant UI feedback
            val cachedPost = localDataSource.getPostById(postId)
            if (cachedPost != null && request.type == "like") {
                val newLiked = !cachedPost.liked
                val newLikeCount = if (newLiked) cachedPost.likeCount + 1 else cachedPost.likeCount - 1
                localDataSource.updatePostLikeStatus(postId, newLiked, newLikeCount)
            }

            // Send request to server
            apiService.interactWithPost(postId, request)
            emit(Result.success(Unit))

        } catch (e: Exception) {
            // If network fails, revert the optimistic update
            val cachedPost = localDataSource.getPostById(postId)
            if (cachedPost != null && request.type == "like") {
                val revertLiked = !cachedPost.liked
                val revertLikeCount = if (revertLiked) cachedPost.likeCount + 1 else cachedPost.likeCount - 1
                localDataSource.updatePostLikeStatus(postId, revertLiked, revertLikeCount)
            }
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
