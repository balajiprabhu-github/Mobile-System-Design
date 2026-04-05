package com.balajiprabhu.newsfeed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY timeStamp DESC")
    fun observePosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE cachedAt < :cutoffTime")
    suspend fun deletePostsOlderThan(cutoffTime: Long)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostsCount(): Int

    @Query("DELETE FROM posts WHERE id IN (SELECT id FROM posts ORDER BY timeStamp ASC LIMIT :excessCount)")
    suspend fun deleteOldestPosts(excessCount: Int)
}