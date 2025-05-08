package com.example.blog.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blog.data.local.BlogDatabase
import com.example.blog.data.local.datasource.LocalBlogDataSource
import com.example.blog.data.network.BlogApi
import com.example.blog.data.remote.datasource.RemoteBlogDataSource
import com.example.blog.data.repository.BlogRepositoryImpl
import com.example.blog.utils.NetworkConnectivityObserver

class BlogViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            val api = BlogApi.create()
            val db = BlogDatabase.getDatabase(context)
            val remoteDataSource = RemoteBlogDataSource(api)
            val localDataSource = LocalBlogDataSource(db.blogPostDao())
            val networkObserver = NetworkConnectivityObserver(context)
            val repository = BlogRepositoryImpl(remoteDataSource, localDataSource, networkObserver)
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(repository, networkObserver) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}