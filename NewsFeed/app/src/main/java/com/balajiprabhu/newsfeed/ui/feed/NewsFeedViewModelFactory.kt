package com.balajiprabhu.newsfeed.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.balajiprabhu.newsfeed.data.repository.FeedRepository

class NewsFeedViewModelFactory(
    private val feedRepository: FeedRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsFeedViewModel::class.java)) {
            return NewsFeedViewModel(feedRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}