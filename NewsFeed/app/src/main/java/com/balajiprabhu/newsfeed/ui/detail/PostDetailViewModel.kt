package com.balajiprabhu.newsfeed.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.newsfeed.data.model.PostDetail
import com.balajiprabhu.newsfeed.data.model.PostInteractionRequest
import com.balajiprabhu.newsfeed.data.repository.FeedRepository
import com.balajiprabhu.newsfeed.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Post Detail screen
 * Manages detailed post data, attachments, and interactions
 */
@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Long = savedStateHandle.get<Long>(Screen.PostDetail.POST_ID_ARG) ?: 0L

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        loadPostDetail()
    }

    /**
     * Load post detail from repository
     * Uses cache-first strategy
     */
    private fun loadPostDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            feedRepository.getPostDetail(postId).collect { result ->
                result.fold(
                    onSuccess = { post ->
                        _uiState.update {
                            it.copy(
                                post = post,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = throwable.message ?: "Failed to load post"
                            )
                        }
                    }
                )
            }
        }
    }

    /**
     * Refresh post detail
     */
    fun refresh() {
        loadPostDetail()
    }

    /**
     * Like or unlike the post
     * Uses optimistic updates
     */
    fun toggleLike() {
        val currentPost = _uiState.value.post ?: return

        viewModelScope.launch {
            // Optimistic UI update
            _uiState.update { currentState ->
                currentState.copy(
                    post = currentPost.copy(
                        liked = !currentPost.liked,
                        likeCount = if (currentPost.liked) currentPost.likeCount - 1 else currentPost.likeCount + 1
                    )
                )
            }

            // Send to repository
            feedRepository.interactWithPost(
                postId = postId,
                request = PostInteractionRequest(requestId = postId, type = "like")
            ).collect { result ->
                result.onFailure { throwable ->
                    // Revert optimistic update on failure
                    _uiState.update { currentState ->
                        currentState.copy(
                            post = currentPost,
                            error = "Failed to like post: ${throwable.message}"
                        )
                    }
                }
            }
        }
    }

    /**
     * Share the post (placeholder - would integrate with Android share sheet)
     */
    fun sharePost() {
        // TODO: Implement share functionality
        viewModelScope.launch {
            feedRepository.interactWithPost(
                postId = postId,
                request = PostInteractionRequest(requestId = postId, type = "share")
            ).collect { result ->
                result.onFailure { throwable ->
                    _uiState.update {
                        it.copy(error = "Failed to share post: ${throwable.message}")
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
 * UI State for Post Detail screen
 */
data class PostDetailUiState(
    val post: PostDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
