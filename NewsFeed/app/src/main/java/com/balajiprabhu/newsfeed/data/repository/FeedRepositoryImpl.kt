package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeedRepositoryImpl : FeedRepository {
    override fun getFeed(): Flow<List<Post>> = flow {

        emit(cachedPosts())

        delay(2000)

        emit(fetchPostsFromNetwork())
    }

    private fun cachedPosts(): List<Post> = listOf(
        Post(id = "1", authorName = "[CACHED] John Doe",
            content = "Hello, this is my first post!", likeCount = 10,
            timestamp = System.currentTimeMillis() - 3600000),
        Post(id = "2", authorName = "[CACHED] Jane Smith",
            content = "Had a great day at the park!", likeCount = 25,
            timestamp = System.currentTimeMillis() - 7200000)
    )

    private fun fetchPostsFromNetwork(): List<Post> = listOf(
        Post(id = "3", authorName = "[FRESH] Alice Johnson",
            content = "Just finished reading a fantastic book!", likeCount = 15,
            timestamp = System.currentTimeMillis() - 1800000),
        Post(id = "4", authorName = "[FRESH] Bob Brown",
            content = "Can't wait for the weekend!", likeCount = 30,
            timestamp = System.currentTimeMillis() - 5400000)
    )

}