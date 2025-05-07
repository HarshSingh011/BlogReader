package com.example.blog.data.repository

import com.example.blog.data.local.datasource.LocalBlogDataSource
import com.example.blog.data.remote.datasource.RemoteBlogDataSource
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.io.IOException

class BlogRepositoryImpl(
    private val remoteBlogDataSource: RemoteBlogDataSource,
    private val localBlogDataSource: LocalBlogDataSource
) : BlogRepository {

    override suspend fun getPosts(page: Int): List<BlogPost> {
        return try {
            val remotePosts = remoteBlogDataSource.getPosts(page)

            val entities = remoteBlogDataSource.getPostsEntities(page)
            localBlogDataSource.cacheBlogPosts(entities)
            localBlogDataSource.clearOldCache()

            remotePosts
        } catch (e: IOException) {
            throw e
        }
    }

    override fun getPostsFlow(page: Int): Flow<List<BlogPost>> = flow {
        try {
            emitAll(localBlogDataSource.getBlogPostsByPage(page))

            val remotePosts = remoteBlogDataSource.getPostsEntities(page)
            if (remotePosts.isNotEmpty()) {
                localBlogDataSource.cacheBlogPosts(remotePosts)
                localBlogDataSource.clearOldCache()
            }
        } catch (e: Exception) {
        }
    }
}