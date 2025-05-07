package com.example.blog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import com.example.blog.utils.NetworkConnectivityObserver
import com.example.blog.utils.NetworkStatus
import com.example.blog.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BlogViewModel(
    private val repository: BlogRepository,
    private val networkObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _selectedPost = MutableStateFlow<BlogPost?>(null)
    val selectedPost: StateFlow<BlogPost?> = _selectedPost

    private val _networkStatus = MutableStateFlow(NetworkStatus.Available)
    val networkStatus = _networkStatus.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NetworkStatus.Available
    )

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _canLoadMore = MutableStateFlow(true)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore

    init {
        observeNetworkStatus()
        loadPosts()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkObserver.observe().collectLatest { status ->
                _networkStatus.value = status
                if (status == NetworkStatus.Available && uiState.value is UiState.Error) {
                    // Retry loading when network becomes available
                    loadPosts()
                }
            }
        }
    }

    fun selectPost(post: BlogPost) {
        _selectedPost.value = post
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPostsFlow(_currentPage.value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data.isEmpty() && _currentPage.value > 1) {
                            _canLoadMore.value = false
                        }
                        _uiState.value = UiState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        if (_uiState.value !is UiState.Success) {
                            // Only show error if we don't have success data
                            _uiState.value = UiState.Error(resource.message)
                        }
                    }
                    is Resource.Loading -> {
                        // Only show loading if we don't have any data yet
                        if (_uiState.value !is UiState.Success) {
                            _uiState.value = UiState.Loading
                        }
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if (_canLoadMore.value && _uiState.value !is UiState.Loading) {
            _currentPage.value++
            loadPosts()
        }
    }

    fun previousPage() {
        if (_currentPage.value > 1 && _uiState.value !is UiState.Loading) {
            _currentPage.value--
            loadPosts()
        }
    }

    fun refresh() {
        _currentPage.value = 1
        _canLoadMore.value = true
        loadPosts()
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val posts: List<BlogPost>) : UiState()
        data class Error(val message: String) : UiState()
    }
}