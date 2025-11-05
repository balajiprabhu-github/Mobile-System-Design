package com.balajiprabhu.newsfeed.ui.navigation

/**
 * Sealed class representing all navigation destinations in the app
 */
sealed class Screen(val route: String) {

    /**
     * News Feed screen - main feed list
     */
    data object NewsFeed : Screen("news_feed")

    /**
     * Post Detail screen - shows full post with attachments
     */
    data object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: Long): String = "post_detail/$postId"
        const val POST_ID_ARG = "postId"
    }

    /**
     * Create Post screen - create new post
     */
    data object CreatePost : Screen("create_post")
}
