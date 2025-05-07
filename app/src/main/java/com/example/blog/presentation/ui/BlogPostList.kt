package com.example.blog.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.blog.domain.model.BlogPost

@Composable
fun BlogPostList(
    posts: List<BlogPost>,
    onPostClick: (BlogPost) -> Unit,
    currentPage: Int,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    hasMorePages: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            items(posts) { post ->
                BlogPostCard(post = post, onClick = { onPostClick(post) })
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousPage,
                enabled = currentPage > 1
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Page"
                )
            }

            Text("Page $currentPage")

            IconButton(
                onClick = onNextPage,
                enabled = hasMorePages
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Page"
                )
            }
        }
    }
}