package com.example.blog.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blog.domain.model.BlogPost
import com.example.blog.presentation.viewmodel.BlogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogApp(viewModel: BlogViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPost by viewModel.selectedPost.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val hasMorePages by viewModel.canLoadMore.collectAsStateWithLifecycle()

    if (selectedPost != null) {
        BackHandler {
            viewModel.clearSelectedPost()
        }

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
                    is BlogViewModel.UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is BlogViewModel.UiState.Success -> {
                        BlogPostList(
                            posts = state.posts,
                            onPostClick = { post -> viewModel.selectPost(post) },
                            currentPage = currentPage,
                            onNextPage = { viewModel.loadNextPage() },
                            onPreviousPage = { viewModel.previousPage() },
                            hasMorePages = hasMorePages
                        )
                    }
                    is BlogViewModel.UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = { viewModel.loadPosts() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}