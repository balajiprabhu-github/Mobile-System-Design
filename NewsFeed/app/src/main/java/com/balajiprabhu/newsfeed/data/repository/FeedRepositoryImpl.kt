package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.mapper.toPost
import com.balajiprabhu.newsfeed.data.local.mapper.toPostEntity
import com.balajiprabhu.newsfeed.data.model.Post
import com.balajiprabhu.newsfeed.data.remote.api.NewsFeedApiService
import com.balajiprabhu.newsfeed.data.remote.dto.InteractionRequest
import com.balajiprabhu.newsfeed.data.util.TimeProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val CACHE_EXPIRY_TIME = 60 * 60 * 1000L
private const val MAX_CACHE_SIZE = 200

class FeedRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: NewsFeedApiService,
    private val timeProvider: TimeProvider
) : FeedRepository {

    private val postLocks = ConcurrentHashMap<String, Mutex>()

    private fun getLockForPost(postId: String): Mutex =
        postLocks.computeIfAbsent(postId) { Mutex() }

    override fun getFeed(): Flow<List<Post>> =
        postDao.observePosts()
            .map { entities -> entities.map { it.toPost() } }

    override suspend fun refreshFeed(): Result<Unit> = runCatching {
        val dtos = apiService.getFeed()
        val now = timeProvider.currentTimeMillis()

        postDao.insertPosts(dtos.map { it.toPostEntity(cachedAt = now) })

        postDao.deletePostsOlderThan(now - CACHE_EXPIRY_TIME)

        val count = postDao.getPostsCount()
        if (count > MAX_CACHE_SIZE) {
            postDao.deleteOldestPosts(count - MAX_CACHE_SIZE)
        }
    }

    override suspend fun toggleLike(postId: String): Result<Unit> {
        val lock = getLockForPost(postId)
        return lock.withLock {
            val currentPost = postDao.getPostById(postId)
                ?: return Result.failure(IllegalArgumentException("Post $postId not found in local cache"))

            val previousIsLiked = currentPost.isLiked
            val previousLikeCount = currentPost.likeCount

            val targetIsLiked = !previousIsLiked
            val targetLikeCount = if (targetIsLiked) previousLikeCount + 1 else maxOf(0, previousLikeCount - 1)

            // 1. Immediate optimistic write to Room (0ms UI latency)
            postDao.updatePostLikeStatus(
                postId = postId,
                isLiked = targetIsLiked,
                likeCount = targetLikeCount,
                syncStatus = "PENDING"
            )

            // 2. Background network call with exponential backoff retry loop
            val networkResult = syncLikeWithRetry(postId = postId, isLiked = targetIsLiked)

            if (networkResult.isSuccess) {
                postDao.updateSyncStatus(postId, syncStatus = "SYNCED")
                Result.success(Unit)
            } else {
                // Revert on failure
                postDao.updatePostLikeStatus(
                    postId = postId,
                    isLiked = previousIsLiked,
                    likeCount = previousLikeCount,
                    syncStatus = "FAILED"
                )
                Result.failure(networkResult.exceptionOrNull() ?: Exception("Failed to sync like"))
            }
        }
    }

    private suspend fun syncLikeWithRetry(
        postId: String,
        isLiked: Boolean,
        maxAttempts: Int = 3,
        initialDelayMs: Long = 1000L,
        maxDelayMs: Long = 30000L
    ): Result<Unit> {
        val action = if (isLiked) "like" else "unlike"
        val numericPostId = postId.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Invalid postId: $postId"))
        var currentDelay = initialDelayMs

        repeat(maxAttempts) { attempt ->
            try {
                apiService.interactWithPost(
                    postId = numericPostId,
                    request = InteractionRequest(type = action)
                )
                return Result.success(Unit)
            } catch (e: Exception) {
                if (attempt == maxAttempts - 1) {
                    return Result.failure(e)
                }
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMs)
            }
        }
        return Result.failure(Exception("Unknown retry failure"))
    }
}