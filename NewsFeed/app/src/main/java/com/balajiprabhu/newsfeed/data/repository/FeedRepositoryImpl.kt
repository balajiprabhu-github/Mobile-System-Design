package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.mapper.toPost
import com.balajiprabhu.newsfeed.data.local.mapper.toPostEntity
import com.balajiprabhu.newsfeed.data.model.Post
import com.balajiprabhu.newsfeed.data.remote.api.NewsFeedApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val CACHE_EXPIRY_TIME = 60 * 60 * 1000L
private const val MAX_CACHE_SIZE = 200

class FeedRepositoryImpl(
    private val postDao: PostDao,
    private val apiService: NewsFeedApiService
) : FeedRepository {

    override fun getFeed(): Flow<List<Post>> =
        postDao.observePosts()
            .map { entities -> entities.map { it.toPost() } }

    override suspend fun refreshFeed(): Result<Unit> = runCatching {
        val dtos = apiService.getFeed()
        val now = System.currentTimeMillis()

        postDao.insertPosts(dtos.map { it.toPostEntity(cachedAt = now) })

        postDao.deletePostsOlderThan(now - CACHE_EXPIRY_TIME)

        val count = postDao.getPostsCount()
        if (count > MAX_CACHE_SIZE) {
            postDao.deleteOldestPosts(count - MAX_CACHE_SIZE)
        }
    }
}