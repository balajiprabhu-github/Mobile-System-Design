package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.remote.api.NewsFeedApiService
import com.balajiprabhu.newsfeed.data.remote.dto.FeedPostDto
import com.balajiprabhu.newsfeed.data.remote.dto.InteractionRequest
import com.balajiprabhu.newsfeed.data.remote.dto.InteractionResponse
import com.balajiprabhu.newsfeed.data.util.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FeedRepositoryImplTest {

    private lateinit var fakeDao: FakePostDao
    private lateinit var fakeApi: FakeNewsFeedApiService
    private lateinit var repository: FeedRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakePostDao()
        fakeApi = FakeNewsFeedApiService()
        val fakeTimeProvider = object : TimeProvider {
            override fun currentTimeMillis(): Long = 1_000_000L
        }
        repository = FeedRepositoryImpl(
            postDao = fakeDao,
            apiService = fakeApi,
            timeProvider = fakeTimeProvider
        )

        // Seed initial post: id=1, likeCount=5, isLiked=false
        fakeDao.posts["1"] = PostEntity(
            id = "1",
            authorName = "Alice",
            content = "Hello World",
            likeCount = 5,
            isLiked = false,
            syncStatus = "SYNCED",
            timeStamp = 100_000L,
            cachedAt = 100_000L
        )
    }

    @Test
    fun toggleLike_onSuccess_optimisticallyLikesAndSetsSynced() = runBlocking {
        val result = repository.toggleLike("1")

        assertTrue(result.isSuccess)
        val post = fakeDao.posts["1"]!!
        assertTrue(post.isLiked)
        assertEquals(6, post.likeCount)
        assertEquals("SYNCED", post.syncStatus)
    }

    @Test
    fun toggleLike_onFailure_revertsToOriginalStateAndSetsFailed() = runBlocking {
        fakeApi.shouldFail = true

        val result = repository.toggleLike("1")

        assertTrue(result.isFailure)
        val post = fakeDao.posts["1"]!!
        assertFalse(post.isLiked)
        assertEquals(5, post.likeCount)
        assertEquals("FAILED", post.syncStatus)
    }

    @Test
    fun toggleLike_consecutiveTaps_cancelsOutCleanlyViaMutex() = runBlocking {
        // Tap 1: Like
        repository.toggleLike("1")
        var post = fakeDao.posts["1"]!!
        assertTrue(post.isLiked)
        assertEquals(6, post.likeCount)

        // Tap 2: Unlike
        repository.toggleLike("1")
        post = fakeDao.posts["1"]!!
        assertFalse(post.isLiked)
        assertEquals(5, post.likeCount)
        assertEquals("SYNCED", post.syncStatus)
    }

    private class FakePostDao : PostDao {
        val posts = mutableMapOf<String, PostEntity>()
        private val postsFlow = MutableStateFlow<List<PostEntity>>(emptyList())

        override fun observePosts(): Flow<List<PostEntity>> = postsFlow

        override suspend fun insertPosts(newPosts: List<PostEntity>) {
            newPosts.forEach { posts[it.id] = it }
        }

        override suspend fun deletePostsOlderThan(cutoffTime: Long) {
            posts.entries.removeAll { it.value.cachedAt < cutoffTime }
        }

        override suspend fun getPostsCount(): Int = posts.size

        override suspend fun deleteOldestPosts(excessCount: Int) {
            val oldestKeys = posts.values.sortedBy { it.timeStamp }.take(excessCount).map { it.id }
            oldestKeys.forEach { posts.remove(it) }
        }

        override suspend fun getPostById(postId: String): PostEntity? = posts[postId]

        override suspend fun updatePostLikeStatus(
            postId: String,
            isLiked: Boolean,
            likeCount: Int,
            syncStatus: String
        ) {
            posts[postId]?.let {
                posts[postId] = it.copy(isLiked = isLiked, likeCount = likeCount, syncStatus = syncStatus)
            }
        }

        override suspend fun updateSyncStatus(postId: String, syncStatus: String) {
            posts[postId]?.let {
                posts[postId] = it.copy(syncStatus = syncStatus)
            }
        }
    }

    private class FakeNewsFeedApiService : NewsFeedApiService {
        var shouldFail = false

        override suspend fun getFeed(page: Int, limit: Int, sort: String, order: String): List<FeedPostDto> = emptyList()

        override suspend fun interactWithPost(postId: Int, request: InteractionRequest): InteractionResponse {
            if (shouldFail) {
                throw RuntimeException("Network Error")
            }
            return InteractionResponse(success = true)
        }
    }
}
