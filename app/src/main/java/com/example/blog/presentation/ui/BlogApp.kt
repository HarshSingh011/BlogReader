package com.example.blog.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blog.presentation.viewmodel.BlogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogApp(viewModel: BlogViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.selectedPost != null) {
        BackHandler {
            viewModel.clearSelectedPost()
        }

        BlogDetailScreen(
            post = uiState.selectedPost!!,
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
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = { viewModel.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }
                    else -> {
                        BlogPostList(
                            posts = uiState.posts,
                            onPostClick = { post -> viewModel.selectPost(post) },
                            currentPage = uiState.currentPage,
                            onNextPage = { viewModel.loadNextPage() },
                            onPreviousPage = { viewModel.loadPreviousPage() },
                            hasMorePages = uiState.hasMorePages
                        )

                        if (uiState.isLoadingMore) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 76.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}