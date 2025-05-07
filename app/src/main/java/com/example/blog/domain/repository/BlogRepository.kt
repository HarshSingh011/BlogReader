package com.example.blog.domain.repository

import com.example.blog.domain.model.BlogPost

interface BlogRepository {
    suspend fun getPosts(page: Int = 1): List<BlogPost>
}