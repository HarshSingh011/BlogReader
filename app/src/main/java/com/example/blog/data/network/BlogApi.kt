package com.example.blog.data.network

import com.example.blog.data.model.BlogPostDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApi {
    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("_embed") embed: Boolean = true
    ): List<BlogPostDto>

    companion object {
        private const val BASE_URL = "https://blog.vrid.in/wp-json/wp/v2/"

        fun create(): BlogApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BlogApi::class.java)
        }
    }
}