package com.example.blog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.blog.domain.model.BlogPost

@Entity(tableName = "blog_posts")
data class BlogPostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val excerpt: String,
    val date: String,
    val authorId: Int,
    val link: String,
    val featuredImageUrl: String?,
    val pageNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): BlogPost {
        return BlogPost(
            id = id,
            title = title,
            content = content,
            excerpt = excerpt,
            date = date,
            authorId = authorId,
            link = link,
            categories = listOf(),
            tags = listOf(),
            featuredImageUrl = featuredImageUrl
        )
    }
}