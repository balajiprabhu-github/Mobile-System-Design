package com.balajiprabhu.newsfeed.data.local.mapper

import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.model.Post

fun PostEntity.toPost(): Post {
    return Post(
        id = id,
        authorName = authorName,
        content = content,
        likeCount = likeCount,
        timeStamp = timeStamp
    )
}

fun Post.toPostEntity(cachedAt: Long): PostEntity {
    return PostEntity(
        id = id,
        authorName = authorName,
        content = content,
        likeCount = likeCount,
        timeStamp = timeStamp,
        cachedAt = cachedAt
    )
}