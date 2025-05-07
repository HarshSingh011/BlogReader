package com.example.blog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blog.data.network.BlogApi
import com.example.blog.data.repository.BlogRepositoryImpl

class BlogViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            val api = BlogApi.create()
            val repository = BlogRepositoryImpl(api)
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}