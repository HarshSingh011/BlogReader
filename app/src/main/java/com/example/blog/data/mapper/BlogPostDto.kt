package com.example.blog.data.mapper

import com.example.blog.data.model.BlogPostDto
import com.example.blog.domain.model.BlogPost
import org.jsoup.Jsoup

fun BlogPostDto.toDomainModel(): BlogPost {
    val featuredImageUrl = embedded?.featuredMedia?.firstOrNull()?.sourceUrl

    val cleanExcerpt = Jsoup.parse(excerptObject.rendered).text()

    return BlogPost(
        id = id,
        date = date,
        title = titleObject.rendered,
        content = contentObject.rendered,
        excerpt = cleanExcerpt,
        link = link,
        authorId = author,
        featuredImageUrl = featuredImageUrl,
        categories = categories,
        tags = tags
    )
}