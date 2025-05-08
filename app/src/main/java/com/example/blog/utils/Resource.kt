package com.example.blog.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Exception? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(message: String, cause: Exception? = null): Resource<Nothing> = Error(message, cause)
        fun loading(): Resource<Nothing> = Loading
    }
}