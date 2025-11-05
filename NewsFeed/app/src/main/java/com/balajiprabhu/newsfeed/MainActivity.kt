package com.balajiprabhu.newsfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.balajiprabhu.newsfeed.ui.navigation.NewsFeedNavGraph
import com.balajiprabhu.newsfeed.ui.theme.NewsFeedTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Entry point for the News Feed app
 * Uses Jetpack Compose and Hilt for dependency injection
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsFeedApp()
        }
    }
}

/**
 * Main app composable - Sets up theme and navigation
 */
@Composable
fun NewsFeedApp() {
    NewsFeedTheme {
        val navController = rememberNavController()
        NewsFeedNavGraph(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Extension function to add modifier parameter to NavGraph
 */
@Composable
private fun NewsFeedNavGraph(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {
    com.balajiprabhu.newsfeed.ui.navigation.NewsFeedNavGraph(
        navController = navController
    )
}
