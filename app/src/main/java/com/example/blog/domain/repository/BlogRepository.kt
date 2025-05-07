package com.example.blog.domain.repository

import com.example.blog.domain.model.BlogPost
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    fun getBlogPosts(): Flow<List<BlogPost>>
}