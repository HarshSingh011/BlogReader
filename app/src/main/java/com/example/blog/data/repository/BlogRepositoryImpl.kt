package com.example.blog.data.repository

import com.example.blog.data.mapper.toDomainModel
import com.example.blog.data.network.BlogApi
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository

class BlogRepositoryImpl(private val api: BlogApi) : BlogRepository {
    override suspend fun getPosts(page: Int): List<BlogPost> {
        return api.getPosts(page = page).map { it.toDomainModel() }
    }
}