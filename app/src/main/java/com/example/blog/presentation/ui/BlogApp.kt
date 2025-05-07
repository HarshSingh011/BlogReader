package com.example.blog.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.blog.presentation.viewmodel.BlogUiState
import com.example.blog.presentation.viewmodel.BlogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogApp(viewModel: BlogViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPost by viewModel.selectedPost.collectAsState()

    if (selectedPost != null) {
        BlogDetailScreen(
            post = selectedPost!!,
            onBack = { viewModel.clearSelectedPost() }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Blog Reader") }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is BlogUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is BlogUiState.Success -> {
                        BlogPostList(
                            posts = state.posts,
                            onPostClick = { post -> viewModel.selectPost(post) }
                        )
                    }
                    is BlogUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

