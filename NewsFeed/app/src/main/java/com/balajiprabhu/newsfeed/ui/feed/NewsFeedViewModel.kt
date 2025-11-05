package com.balajiprabhu.newsfeed.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.newsfeed.data.model.PostInteractionRequest
import com.balajiprabhu.newsfeed.data.model.PostPreview
import com.balajiprabhu.newsfeed.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for News Feed screen
 * Manages feed data, loading states, and user interactions
 */
@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsFeedUiState())
    val uiState: StateFlow<NewsFeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    /**
     * Load feed from repository
     * Uses cache-first strategy - cached data loads first, then fresh data
     */
    fun loadFeed(page: Int = 0) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = page == 0, error = null) }

            feedRepository.getFeed(page).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.update { currentState ->
                            val newPosts = if (page == 0) {
                                response.feed
                            } else {
                                currentState.posts + response.feed
                            }
                            currentState.copy(
                                posts = newPosts,
                                isLoading = false,
                                isRefreshing = false,
                                canLoadMore = response.paging.hasMore,
                                error = null
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = throwable.message ?: "Unknown error occurred"
                            )
                        }
                    }
                )
            }
        }
    }

    /**
     * Refresh feed (pull to refresh)
     */
    fun refreshFeed() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadFeed(page = 0)
    }

    /**
     * Load next page (infinite scroll)
     */
    fun loadNextPage() {
        val currentState = _uiState.value
        if (!currentState.isLoading && currentState.canLoadMore) {
            val nextPage = (currentState.posts.size / 20) // Assuming 20 items per page
            loadFeed(page = nextPage)
        }
    }

    /**
     * Like or unlike a post
     * Uses optimistic updates from repository
     */
    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            // Optimistic UI update
            _uiState.update { currentState ->
                val updatedPosts = currentState.posts.map { post ->
                    if (post.postId == postId) {
                        post.copy(
                            liked = !post.liked,
                            likeCount = if (post.liked) post.likeCount - 1 else post.likeCount + 1
                        )
                    } else {
                        post
                    }
                }
                currentState.copy(posts = updatedPosts)
            }

            // Send to repository
            feedRepository.interactWithPost(
                postId = postId,
                request = PostInteractionRequest(requestId = postId, type = "like")
            ).collect { result ->
                result.onFailure { throwable ->
                    // Revert optimistic update on failure
                    _uiState.update { currentState ->
                        val revertedPosts = currentState.posts.map { post ->
                            if (post.postId == postId) {
                                post.copy(
                                    liked = !post.liked,
                                    likeCount = if (post.liked) post.likeCount - 1 else post.likeCount + 1
                                )
                            } else {
                                post
                            }
                        }
                        currentState.copy(
                            posts = revertedPosts,
                            error = "Failed to like post: ${throwable.message}"
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for News Feed screen
 */
data class NewsFeedUiState(
    val posts: List<PostPreview> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val canLoadMore: Boolean = true,
    val error: String? = null
)
