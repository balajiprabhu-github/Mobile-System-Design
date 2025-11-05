package com.balajiprabhu.newsfeed.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.balajiprabhu.newsfeed.ui.create.CreatePostScreen
import com.balajiprabhu.newsfeed.ui.detail.PostDetailScreen
import com.balajiprabhu.newsfeed.ui.feed.NewsFeedScreen

/**
 * Main navigation graph for the app
 * Sets up all navigation routes and their destinations
 */
@Composable
fun NewsFeedNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.NewsFeed.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // News Feed Screen
        composable(route = Screen.NewsFeed.route) {
            NewsFeedScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onCreatePostClick = {
                    navController.navigate(Screen.CreatePost.route)
                }
            )
        }

        // Post Detail Screen
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(
                navArgument(Screen.PostDetail.POST_ID_ARG) {
                    type = NavType.LongType
                }
            )
        ) {
            PostDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Create Post Screen
        composable(route = Screen.CreatePost.route) {
            CreatePostScreen(
                onBackClick = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }
    }
}
