package com.example.blog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog.domain.model.BlogPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class BlogUiState {
    object Loading : BlogUiState()
    data class Success(val posts: List<BlogPost>) : BlogUiState()
    data class Error(val message: String) : BlogUiState()
}

class BlogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    init {
        loadBlogPosts()
    }

    private fun loadBlogPosts() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = BlogUiState.Success(generateSamplePosts())
        }
    }

    private fun generateSamplePosts(): List<BlogPost> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return List(10) { index ->
            BlogPost(
                id = index + 1,
                date = dateFormat.format(Date()),
                title = "Sample Blog Post ${index + 1}",
                content = "This is the full content of blog post ${index + 1}. It contains detailed information about the topic.",
                excerpt = "This is a short excerpt from blog post ${index + 1}...",
                link = "https://blog.vrid.in/sample-post-${index + 1}",
                authorId = 1,
                featuredImageUrl = "https://picsum.photos/seed/${index + 1}/500/300",
                categories = listOf(1, 2),
                tags = listOf(1, 3, 5)
            )
        }
    }
}