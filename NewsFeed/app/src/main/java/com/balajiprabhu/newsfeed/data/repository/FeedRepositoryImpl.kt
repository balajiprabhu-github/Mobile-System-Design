package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.mapper.toPost
import com.balajiprabhu.newsfeed.data.local.mapper.toPostEntity
import com.balajiprabhu.newsfeed.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

private const val CACHE_EXPIRY_TIME = 60 * 60 * 1000L
private const val MAX_CACHE_SIZE = 200
class FeedRepositoryImpl(private val postDao: PostDao) : FeedRepository {

    override fun getFeed(): Flow<List<Post>> =
        postDao.observePosts()
            .map { entities -> entities.map { it.toPost() } }
            .onStart { refreshFeed() }

    private suspend fun refreshFeed() {
        val freshPosts = fetchPostsFromNetwork()
        val now = System.currentTimeMillis()

        postDao.insertPosts(freshPosts.map { it.toPostEntity(cachedAt = now) })

        postDao.deletePostsOlderThan(now - CACHE_EXPIRY_TIME)

        val count = postDao.getPostsCount()

        if (count > MAX_CACHE_SIZE) {
            postDao.deleteOldestPosts(count - MAX_CACHE_SIZE)
        }
    }

    private suspend fun fetchPostsFromNetwork(): List<Post> = listOf(
        Post(id = "1", authorName = "Alice Johnson",
            content = "Just finished reading a fantastic book!", likeCount = 15,
            timeStamp = System.currentTimeMillis() - 1800000),
        Post(id = "2", authorName = "Bob Brown",
            content = "Can't wait for the weekend!", likeCount = 30,
            timeStamp = System.currentTimeMillis() - 5400000)
    )
}