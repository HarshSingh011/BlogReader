package com.example.blog.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import com.example.blog.utils.NetworkConnectivityObserver
import com.example.blog.utils.NetworkStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BlogViewModel(
    private val repository: BlogRepository,
    private val networkObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val TAG = "BlogViewModel"

    data class UiState(
        val isLoading: Boolean = false,
        val posts: List<BlogPost> = emptyList(),
        val error: String? = null,
        val selectedPost: BlogPost? = null,
        val currentPage: Int = 1,
        val hasMorePages: Boolean = true,
        val isLoadingMore: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _networkStatus = MutableStateFlow(NetworkStatus.Available)
    val networkStatus = _networkStatus.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NetworkStatus.Available
    )

    init {
        observeNetworkStatus()
        loadDummyPosts()
        viewModelScope.launch {
            loadPosts()
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkObserver.observe().collectLatest { status ->
                _networkStatus.value = status
                if (status == NetworkStatus.Available && _uiState.value.error != null) {
                    refresh()
                }
            }
        }
    }

    fun selectPost(post: BlogPost) {
        _uiState.value = _uiState.value.copy(selectedPost = post)
    }

    fun clearSelectedPost() {
        _uiState.value = _uiState.value.copy(selectedPost = null)
    }

    fun loadPosts() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return

        val currentPage = _uiState.value.currentPage
        Log.d(TAG, "Loading posts for page: $currentPage")

        val currentPosts = _uiState.value.posts
        _uiState.value = _uiState.value.copy(
            isLoading = currentPosts.isEmpty(),
            isLoadingMore = currentPosts.isNotEmpty(),
            error = null
        )

        viewModelScope.launch {
            try {
                val posts = repository.getPosts(currentPage)
                Log.d(TAG, "Loaded ${posts.size} posts from repository")

                if (posts.isEmpty()) {
                    if (currentPage > 1) {
                        _uiState.value = _uiState.value.copy(
                            hasMorePages = false,
                            isLoading = false,
                            isLoadingMore = false
                        )
                    } else {
                        loadDummyPosts()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        posts = posts,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                        hasMorePages = posts.size >= 5
                    )
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e(TAG, "Error loading posts", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = if (currentPosts.isEmpty()) e.message ?: "Failed to load posts" else null
                    )
                    if (currentPosts.isEmpty()) {
                        loadDummyPosts()
                    }
                }
            }
        }
    }

    private fun loadDummyPosts() {
        val dummyPosts = List(5) { index ->
            BlogPost(
                id = index,
                title = "Sample Post #$index",
                content = "This is placeholder content for demonstration.",
                excerpt = "This is a sample excerpt for testing purposes.",
                date = "2023-05-08T12:00:00",
                authorId = 1,
                featuredImageUrl = null,
                link = "https://example.com/post/$index",
                categories = listOf(1),
                tags = listOf(1)
            )
        }

        _uiState.value = _uiState.value.copy(
            posts = dummyPosts,
            isLoading = false,
            isLoadingMore = false,
            error = null
        )
        Log.d(TAG, "Loaded dummy posts as fallback")
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || !_uiState.value.hasMorePages) {
            return
        }

        _uiState.value = _uiState.value.copy(
            currentPage = _uiState.value.currentPage + 1
        )
        loadPosts()
    }

    fun loadPreviousPage() {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || _uiState.value.currentPage <= 1) {
            return
        }

        _uiState.value = _uiState.value.copy(
            currentPage = _uiState.value.currentPage - 1,
            hasMorePages = true
        )
        loadPosts()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(
            currentPage = 1,
            hasMorePages = true,
            isLoading = true,
            isLoadingMore = false,
            error = null
        )
        loadPosts()
    }
}