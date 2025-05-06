package com.example.blog.domain.model

data class BlogPost(
    val id: Int,
    val date: String,
    val title: String,
    val content: String,
    val excerpt: String,
    val link: String,
    val authorId: Int,
    val featuredImageUrl: String?,
    val categories: List<Int>,
    val tags: List<Int>
)