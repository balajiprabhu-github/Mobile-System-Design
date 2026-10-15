package com.balajiprabhu.newsfeed.data.local.mapper

import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.model.Post
import com.balajiprabhu.newsfeed.data.remote.dto.FeedPostDto
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun PostEntity.toPost(): Post {
    return Post(
        id = id,
        authorName = authorName,
        content = content,
        likeCount = likeCount,
        isLiked = isLiked,
        syncStatus = syncStatus,
        timeStamp = timeStamp
    )
}

fun Post.toPostEntity(cachedAt: Long): PostEntity {
    return PostEntity(
        id = id,
        authorName = authorName,
        content = content,
        likeCount = likeCount,
        isLiked = isLiked,
        syncStatus = syncStatus,
        timeStamp = timeStamp,
        cachedAt = cachedAt
    )
}

fun FeedPostDto.toPostEntity(cachedAt: Long): PostEntity {
    return PostEntity(
        id = id.toString(),
        authorName = author.name,
        content = contentSummary,
        likeCount = likeCount,
        isLiked = liked,
        syncStatus = "SYNCED",
        timeStamp = parseIsoTimestamp(createdAt),
        cachedAt = cachedAt
    )
}

private fun parseIsoTimestamp(isoString: String): Long {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        format.parse(isoString)?.time ?: System.currentTimeMillis()
    } catch (_: Exception) {
        System.currentTimeMillis()
    }
}