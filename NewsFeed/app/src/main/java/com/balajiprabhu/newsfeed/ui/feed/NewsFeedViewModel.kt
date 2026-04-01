package com.balajiprabhu.newsfeed.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.newsfeed.data.model.Post
import com.balajiprabhu.newsfeed.data.repository.FeedRepository
import com.balajiprabhu.newsfeed.data.repository.FeedRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class NewsFeedViewState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false
)

class NewsFeedViewModel(
    private val feedRepository: FeedRepository = FeedRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsFeedViewState())
    val uiState: StateFlow<NewsFeedViewState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            feedRepository.getFeed()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .onEach { posts ->
                    _uiState.value = _uiState.value.copy(posts = posts, isLoading = false)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect()
        }
    }
}
