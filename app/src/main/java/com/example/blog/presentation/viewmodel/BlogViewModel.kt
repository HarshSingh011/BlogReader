package com.example.blog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlogViewModel(private val repository: BlogRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState

    private val _selectedPost = MutableStateFlow<BlogPost?>(null)
    val selectedPost = _selectedPost.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage = _currentPage.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts(page: Int = 1) {
        _currentPage.value = page
        _uiState.value = BlogUiState.Loading

        viewModelScope.launch {
            try {
                val posts = repository.getPosts(page = page)
                _uiState.value = BlogUiState.Success(posts)
                _hasMorePages.value = posts.isNotEmpty()
            } catch (e: Exception) {
                _uiState.value = BlogUiState.Error("Failed to load posts: ${e.message}")
            }
        }
    }

    fun nextPage() {
        if (_hasMorePages.value) {
            loadPosts(_currentPage.value + 1)
        }
    }

    fun previousPage() {
        if (_currentPage.value > 1) {
            loadPosts(_currentPage.value - 1)
        }
    }

    fun selectPost(post: BlogPost) {
        _selectedPost.value = post
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
    }
}

sealed class BlogUiState {
    data object Loading : BlogUiState()
    data class Success(val posts: List<BlogPost>) : BlogUiState()
    data class Error(val message: String) : BlogUiState()
}