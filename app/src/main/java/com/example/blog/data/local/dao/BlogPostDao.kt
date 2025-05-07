package com.example.blog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.blog.data.local.entity.BlogPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlogPostDao {
    @Query("SELECT * FROM blog_posts WHERE pageNumber = :page ORDER BY timestamp DESC")
    fun getBlogPostsByPage(page: Int): Flow<List<BlogPostEntity>>

    @Query("SELECT * FROM blog_posts WHERE id = :postId")
    suspend fun getBlogPostById(postId: Int): BlogPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<BlogPostEntity>)

    @Query("DELETE FROM blog_posts WHERE pageNumber = :page")
    suspend fun deleteByPage(page: Int)

    @Query("DELETE FROM blog_posts WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}