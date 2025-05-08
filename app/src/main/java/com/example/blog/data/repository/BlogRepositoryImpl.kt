package com.example.blog.data.repository

import com.example.blog.data.local.datasource.LocalBlogDataSource
import com.example.blog.data.remote.datasource.RemoteBlogDataSource
import com.example.blog.domain.model.BlogPost
import com.example.blog.domain.repository.BlogRepository
import com.example.blog.utils.NetworkConnectivityObserver
import com.example.blog.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class BlogRepositoryImpl(
    private val remoteBlogDataSource: RemoteBlogDataSource,
    private val localBlogDataSource: LocalBlogDataSource,
    private val networkObserver: NetworkConnectivityObserver
) : BlogRepository {

    override fun getPostsFlow(page: Int): Flow<Resource<List<BlogPost>>> = flow {
        emit(Resource.loading())

        val cachedData = localBlogDataSource.getBlogPostsByPage(page)
            .map { posts -> Resource.success(posts) }
        emitAll(cachedData)

        if (!networkObserver.isNetworkAvailable()) {
            emit(Resource.error("No internet connection. Showing cached data."))
            return@flow
        }

        try {
            val remotePosts = remoteBlogDataSource.getPostsEntities(page)
            if (remotePosts.isNotEmpty()) {
                localBlogDataSource.cacheBlogPosts(remotePosts)
                localBlogDataSource.clearOldCache()
            }
        } catch (e: Exception) {
            emit(Resource.error("Failed to refresh data: ${e.message}", e))
        }
    }

    override suspend fun getPosts(page: Int): List<BlogPost> {
        return try {
            if (networkObserver.isNetworkAvailable()) {
                val remotePosts = remoteBlogDataSource.getPosts(page)
                val entities = remoteBlogDataSource.getPostsEntities(page)
                localBlogDataSource.cacheBlogPosts(entities)
                localBlogDataSource.clearOldCache()
                remotePosts
            } else {
                localBlogDataSource.getBlogPostsByPage(page).first()
            }
        } catch (e: IOException) {
            localBlogDataSource.getBlogPostsByPage(page).first()
        }
    }
}