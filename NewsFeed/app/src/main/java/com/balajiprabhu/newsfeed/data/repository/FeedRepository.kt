package com.balajiprabhu.newsfeed.data.repository

import com.balajiprabhu.newsfeed.data.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(): Flow<List<Post>>

}