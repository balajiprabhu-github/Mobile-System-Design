package com.balajiprabhu.newsfeed

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import com.balajiprabhu.newsfeed.ui.feed.NewsFeedScreen
import com.balajiprabhu.newsfeed.ui.feed.NewsFeedViewModel
import com.balajiprabhu.newsfeed.ui.feed.NewsFeedViewModelFactory

class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as NewsFeedApplication
        setContent {
            MaterialTheme {
                Scaffold { paddingValues ->
                    val viewModel: NewsFeedViewModel = viewModel(
                        factory = NewsFeedViewModelFactory(app.feedRepository)
                    )
                    NewsFeedScreen(paddingValues = paddingValues, viewModel)
                }
            }
        }
    }
}