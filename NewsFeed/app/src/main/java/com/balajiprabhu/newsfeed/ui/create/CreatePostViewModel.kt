package com.balajiprabhu.newsfeed.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.newsfeed.data.model.NewPostRequest
import com.balajiprabhu.newsfeed.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Create Post Screen
 * Handles post creation logic
 */
@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun createPost(onSuccess: () -> Unit) {
        val content = _uiState.value.content.trim()

        if (content.isEmpty()) {
            _uiState.update { it.copy(error = "Post content cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val request = NewPostRequest(
                requestId = System.currentTimeMillis(),
                content = content,
                attachments = emptyList() // No attachments for now
            )

            feedRepository.createPost(request)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to create post: ${e.message}"
                        )
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.update { it.copy(isLoading = false) }
                            onSuccess()
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Failed to create post: ${e.message}"
                                )
                            }
                        }
                    )
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for Create Post Screen
 */
data class CreatePostUiState(
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
