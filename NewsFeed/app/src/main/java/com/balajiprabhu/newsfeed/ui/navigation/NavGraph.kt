package com.balajiprabhu.newsfeed.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

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
            // NewsFeedScreen will be implemented next
            // NewsFeedScreen(
            //     onPostClick = { postId ->
            //         navController.navigate(Screen.PostDetail.createRoute(postId))
            //     },
            //     onCreatePostClick = {
            //         navController.navigate(Screen.CreatePost.route)
            //     }
            // )
        }

        // Post Detail Screen
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(
                navArgument(Screen.PostDetail.POST_ID_ARG) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong(Screen.PostDetail.POST_ID_ARG) ?: 0L
            // PostDetailScreen will be implemented next
            // PostDetailScreen(
            //     postId = postId,
            //     onBackClick = { navController.popBackStack() }
            // )
        }

        // Create Post Screen
        composable(route = Screen.CreatePost.route) {
            // CreatePostScreen will be implemented later
            // CreatePostScreen(
            //     onBackClick = { navController.popBackStack() },
            //     onPostCreated = { navController.popBackStack() }
            // )
        }
    }
}
