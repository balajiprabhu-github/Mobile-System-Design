package com.balajiprabhu.newsfeed.data.mapper

import com.balajiprabhu.newsfeed.data.local.entity.AttachmentEntity
import com.balajiprabhu.newsfeed.data.local.entity.AuthorEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostEntity
import com.balajiprabhu.newsfeed.data.local.entity.PostWithAttachments
import com.balajiprabhu.newsfeed.data.model.Attachment
import com.balajiprabhu.newsfeed.data.model.AuthorPreview
import com.balajiprabhu.newsfeed.data.model.PostDetail
import com.balajiprabhu.newsfeed.data.model.PostPreview

/**
 * Maps PostPreview (from API) to PostEntity (for database)
 * Used when caching feed data
 */
fun PostPreview.toEntity(): PostEntity {
    return PostEntity(
        postId = postId,
        content = contentSummary, // Use summary as content initially
        contentSummary = contentSummary,
        author = author.toEntity(),
        createdAt = createdAt,
        liked = liked,
        likeCount = likeCount,
        shareCount = 0, // Not available in preview
        attachmentCount = attachmentCount,
        attachmentPreviewImageUrl = attachmentPreviewImageUrl,
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Maps PostDetail (from API) to PostEntity (for database)
 * Used when caching detailed post data
 */
fun PostDetail.toEntity(): PostEntity {
    return PostEntity(
        postId = postId,
        content = content,
        contentSummary = content.take(200), // Create summary from content
        author = author.toEntity(),
        createdAt = createdAt,
        liked = liked,
        likeCount = likeCount,
        shareCount = shareCount,
        attachmentCount = attachments.size,
        attachmentPreviewImageUrl = attachments.firstOrNull()?.previewImageUrl,
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Maps PostEntity (from database) to PostPreview (for UI)
 */
fun PostEntity.toPreview(): PostPreview {
    return PostPreview(
        postId = postId,
        contentSummary = contentSummary,
        author = author.toModel(),
        createdAt = createdAt,
        liked = liked,
        likeCount = likeCount,
        attachmentCount = attachmentCount,
        attachmentPreviewImageUrl = attachmentPreviewImageUrl
    )
}

/**
 * Maps PostWithAttachments (from database) to PostDetail (for UI)
 */
fun PostWithAttachments.toDetail(): PostDetail {
    return PostDetail(
        postId = post.postId,
        content = post.content,
        author = post.author.toModel(),
        createdAt = post.createdAt,
        liked = post.liked,
        likeCount = post.likeCount,
        shareCount = post.shareCount,
        attachments = attachments.map { it.toModel() }
    )
}

/**
 * Maps AuthorPreview (from API) to AuthorEntity (for database)
 */
fun AuthorPreview.toEntity(): AuthorEntity {
    return AuthorEntity(
        id = id,
        name = name,
        profileImageThumbnailUrl = profileImageThumbnailUrl
    )
}

/**
 * Maps AuthorEntity (from database) to AuthorPreview (for UI)
 */
fun AuthorEntity.toModel(): AuthorPreview {
    return AuthorPreview(
        id = id,
        name = name,
        profileImageThumbnailUrl = profileImageThumbnailUrl
    )
}

/**
 * Maps Attachment (from API) to AttachmentEntity (for database)
 */
fun Attachment.toEntity(postId: Long): AttachmentEntity {
    return AttachmentEntity(
        id = id,
        postId = postId,
        type = type,
        contentUrl = contentUrl,
        previewImageUrl = previewImageUrl,
        caption = caption
    )
}

/**
 * Maps AttachmentEntity (from database) to Attachment (for UI)
 */
fun AttachmentEntity.toModel(): Attachment {
    return Attachment(
        id = id,
        type = type,
        contentUrl = contentUrl,
        previewImageUrl = previewImageUrl,
        caption = caption
    )
}

/**
 * Helper to convert a list of PostDetail to entities with their attachments
 */
fun PostDetail.toEntityWithAttachments(): Pair<PostEntity, List<AttachmentEntity>> {
    val postEntity = this.toEntity()
    val attachmentEntities = this.attachments.map { it.toEntity(postEntity.postId) }
    return Pair(postEntity, attachmentEntities)
}