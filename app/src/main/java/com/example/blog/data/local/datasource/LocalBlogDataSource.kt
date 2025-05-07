package com.example.blog.data.local.datasource

import com.example.blog.data.local.dao.BlogPostDao
import com.example.blog.data.local.entity.BlogPostEntity
import com.example.blog.domain.model.BlogPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class LocalBlogDataSource(private val blogPostDao: BlogPostDao) {
    fun getBlogPostsByPage(page: Int): Flow<List<BlogPost>> {
        return blogPostDao.getBlogPostsByPage(page).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun cacheBlogPosts(posts: List<BlogPostEntity>) {
        blogPostDao.insertAll(posts)
    }

    suspend fun clearOldCache() {
        val expireTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
        blogPostDao.deleteOlderThan(expireTime)
    }
}