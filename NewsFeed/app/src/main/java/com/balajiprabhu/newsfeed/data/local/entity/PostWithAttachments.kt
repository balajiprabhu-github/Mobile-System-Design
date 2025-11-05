package com.balajiprabhu.newsfeed.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Represents a post with its attachments using Room's relationship feature
 */
data class PostWithAttachments(
    @Embedded
    val post: PostEntity,

    @Relation(
        parentColumn = "postId",
        entityColumn = "postId"
    )
    val attachments: List<AttachmentEntity>
)