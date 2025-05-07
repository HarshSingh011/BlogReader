package com.example.blog.data.remote.datasource

import com.example.blog.data.local.entity.BlogPostEntity
import com.example.blog.data.mapper.toDomainModel
import com.example.blog.data.network.BlogApi
import com.example.blog.domain.model.BlogPost

class RemoteBlogDataSource(private val api: BlogApi) {
    suspend fun getPosts(page: Int): List<BlogPost> {
        return api.getPosts(page = page).map { it.toDomainModel() }
    }

    suspend fun getPostsEntities(page: Int): List<BlogPostEntity> {
        return api.getPosts(page = page).map { dto ->
            val post = dto.toDomainModel()
            BlogPostEntity(
                id = post.id,
                title = post.title,
                content = post.content,
                excerpt = post.excerpt,
                date = post.date,
                authorId = post.authorId,
                link = post.link,
                featuredImageUrl = post.featuredImageUrl,
                pageNumber = page
            )
        }
    }
}