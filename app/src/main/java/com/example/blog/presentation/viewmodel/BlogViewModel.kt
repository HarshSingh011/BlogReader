// presentation/viewmodel/BlogViewModel.kt
package com.example.blog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog.data.di.NetworkModule
import com.example.blog.data.repository.BlogRepositoryImpl
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class BlogUiState {
    object Loading : BlogUiState()
    data class Success(val posts: List<BlogPost>) : BlogUiState()
    data class Error(val message: String) : BlogUiState()
}

class BlogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    private val repository: BlogRepository = BlogRepositoryImpl(NetworkModule.blogApiService)

    private val _selectedPost = MutableStateFlow<BlogPost?>(null)
    val selectedPost: StateFlow<BlogPost?> = _selectedPost.asStateFlow()

    init {
        loadBlogPosts()
    }

    private fun loadBlogPosts() {
        viewModelScope.launch {
            repository.getBlogPosts()
                .catch { e ->
                    _uiState.value = BlogUiState.Error(e.message ?: "Unknown error occurred")
                }
                .collectLatest { posts ->
                    _uiState.value = BlogUiState.Success(posts)
                }
        }
    }

    fun selectPost(post: BlogPost) {
        _selectedPost.value = post
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
    }
}