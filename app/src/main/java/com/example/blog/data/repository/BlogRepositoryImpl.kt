package com.example.blog.data.repository

import com.example.blog.data.api.BlogApiService
import com.example.blog.data.mapper.toDomainModel
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BlogRepositoryImpl(
    private val apiService: BlogApiService
) : BlogRepository {
    override fun getBlogPosts(): Flow<List<BlogPost>> = flow {
        try {
            val posts = apiService.getPosts().map { it.toDomainModel() }
            emit(posts)
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)
}