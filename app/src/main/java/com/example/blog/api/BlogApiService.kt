package com.example.blog.data.api

import com.example.blog.data.model.BlogPostDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApiService {
    @GET("wp-json/wp/v2/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("_embed") embed: String = "true"
    ): List<BlogPostDto>
}