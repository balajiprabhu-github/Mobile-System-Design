package com.balajiprabhu.newsfeed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.balajiprabhu.newsfeed.data.local.entity.AttachmentEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostWithAttachments
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    /**
     * Insert posts. Replace if conflict (e.g., when refreshing data)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    /**
     * Insert a single post
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    /**
     * Insert attachments for a post
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<AttachmentEntity>)

    /**
     * Get all posts ordered by creation date (newest first)
     * Returns Flow for reactive updates
     */
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPostsFlow(): Flow<List<PostEntity>>

    /**
     * Get all posts as a one-time fetch
     */
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    suspend fun getAllPosts(): List<PostEntity>

    /**
     * Get a post with its attachments by ID
     */
    @Transaction
    @Query("SELECT * FROM posts WHERE postId = :postId")
    suspend fun getPostWithAttachments(postId: Long): PostWithAttachments?

    /**
     * Get a post by ID (without attachments)
     */
    @Query("SELECT * FROM posts WHERE postId = :postId")
    suspend fun getPostById(postId: Long): PostEntity?

    /**
     * Get attachments for a specific post
     */
    @Query("SELECT * FROM attachments WHERE postId = :postId")
    suspend fun getAttachmentsForPost(postId: Long): List<AttachmentEntity>

    /**
     * Update post like status
     */
    @Query("UPDATE posts SET liked = :liked, likeCount = :likeCount WHERE postId = :postId")
    suspend fun updatePostLikeStatus(postId: Long, liked: Boolean, likeCount: Int)

    /**
     * Update an existing post
     */
    @Update
    suspend fun updatePost(post: PostEntity)

    /**
     * Delete posts older than the specified timestamp
     * Useful for cache management
     */
    @Query("DELETE FROM posts WHERE cachedAt < :timestamp")
    suspend fun deletePostsOlderThan(timestamp: Long)

    /**
     * Delete a specific post (attachments will be cascade deleted)
     */
    @Query("DELETE FROM posts WHERE postId = :postId")
    suspend fun deletePost(postId: Long)

    /**
     * Clear all posts and attachments
     */
    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    /**
     * Get the count of cached posts
     */
    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    /**
     * Get paginated posts (for efficient loading)
     */
    @Query("SELECT * FROM posts ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPostsPaginated(limit: Int, offset: Int): List<PostEntity>
}