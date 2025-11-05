package com.balajiprabhu.newsfeed.data.local.source

import com.balajiprabhu.newsfeed.data.local.entity.AttachmentEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostWithAttachments
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for posts
 * Provides an abstraction over the Room DAO
 */
interface PostsLocalDataSource {

    suspend fun insertPosts(posts: List<PostEntity>)

    suspend fun insertPost(post: PostEntity)

    suspend fun insertAttachments(attachments: List<AttachmentEntity>)

    fun getAllPostsFlow(): Flow<List<PostEntity>>

    suspend fun getAllPosts(): List<PostEntity>

    suspend fun getPostWithAttachments(postId: Long): PostWithAttachments?

    suspend fun getPostById(postId: Long): PostEntity?

    suspend fun updatePostLikeStatus(postId: Long, liked: Boolean, likeCount: Int)

    suspend fun deletePostsOlderThan(timestamp: Long)

    suspend fun clearAllPosts()

    suspend fun getPostCount(): Int
}