package com.example.blog.data.model

import com.google.gson.annotations.SerializedName

data class BlogPostDto(
    val id: Int,
    val date: String,
    @SerializedName("title")
    val titleObject: TitleDto,
    @SerializedName("content")
    val contentObject: ContentDto,
    @SerializedName("excerpt")
    val excerptObject: ExcerptDto,
    val link: String,
    val author: Int,
    @SerializedName("featured_media")
    val featuredMedia: Int,
    val categories: List<Int>,
    val tags: List<Int>,
    @SerializedName("_embedded")
    val embedded: EmbeddedDto? = null
)

data class TitleDto(
    val rendered: String
)

data class ContentDto(
    val rendered: String
)

data class ExcerptDto(
    val rendered: String
)

data class EmbeddedDto(
    @SerializedName("wp:featuredmedia")
    val featuredMedia: List<FeaturedMediaDto>? = null
)

data class FeaturedMediaDto(
    @SerializedName("source_url")
    val sourceUrl: String
)