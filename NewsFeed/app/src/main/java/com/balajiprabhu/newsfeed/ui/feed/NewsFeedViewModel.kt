package com.balajiprabhu.newsfeed.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.newsfeed.data.model.Post
import com.balajiprabhu.newsfeed.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsFeedViewState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val refreshError: Boolean = false
)

class NewsFeedViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsFeedViewState())
    val uiState: StateFlow<NewsFeedViewState> = _uiState.asStateFlow()

    init {
        observeFeed()
        refreshFeed()
    }

    private fun observeFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            feedRepository.getFeed().collect { posts ->
                _uiState.update {
                    it.copy(
                        posts = posts,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, refreshError = false) }
            val result = feedRepository.refreshFeed()
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    refreshError = result.isFailure
                )
            }
        }
    }
}
