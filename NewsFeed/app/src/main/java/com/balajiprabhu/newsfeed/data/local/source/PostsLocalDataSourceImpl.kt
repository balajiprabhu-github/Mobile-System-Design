package com.balajiprabhu.newsfeed.data.local.source

import com.balajiprabhu.newsfeed.data.local.dao.PostDao
import com.balajiprabhu.newsfeed.data.local.entity.AttachmentEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostWithAttachments
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of local data source using Room DAO
 */
class PostsLocalDataSourceImpl @Inject constructor(
    private val postDao: PostDao
) : PostsLocalDataSource {

    override suspend fun insertPosts(posts: List<PostEntity>) {
        postDao.insertPosts(posts)
    }

    override suspend fun insertPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    override suspend fun insertAttachments(attachments: List<AttachmentEntity>) {
        postDao.insertAttachments(attachments)
    }

    override fun getAllPostsFlow(): Flow<List<PostEntity>> {
        return postDao.getAllPostsFlow()
    }

    override suspend fun getAllPosts(): List<PostEntity> {
        return postDao.getAllPosts()
    }

    override suspend fun getPostWithAttachments(postId: Long): PostWithAttachments? {
        return postDao.getPostWithAttachments(postId)
    }

    override suspend fun getPostById(postId: Long): PostEntity? {
        return postDao.getPostById(postId)
    }

    override suspend fun updatePostLikeStatus(postId: Long, liked: Boolean, likeCount: Int) {
        postDao.updatePostLikeStatus(postId, liked, likeCount)
    }

    override suspend fun deletePostsOlderThan(timestamp: Long) {
        postDao.deletePostsOlderThan(timestamp)
    }

    override suspend fun clearAllPosts() {
        postDao.clearAllPosts()
    }

    override suspend fun getPostCount(): Int {
        return postDao.getPostCount()
    }
}