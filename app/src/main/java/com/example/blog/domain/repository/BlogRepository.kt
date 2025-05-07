package com.example.blog.domain.repository

import com.example.blog.domain.model.BlogPost
import com.example.blog.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    suspend fun getPosts(page: Int): List<BlogPost>
    fun getPostsFlow(page: Int): Flow<Resource<List<BlogPost>>>
}