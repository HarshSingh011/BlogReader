package com.example.blog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.blog.data.local.dao.BlogPostDao
import com.example.blog.data.local.entity.BlogPostEntity

@Database(entities = [BlogPostEntity::class], version = 1)
abstract class BlogDatabase : RoomDatabase() {
    abstract fun blogPostDao(): BlogPostDao

    companion object {
        @Volatile
        private var INSTANCE: BlogDatabase? = null

        fun getDatabase(context: Context): BlogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlogDatabase::class.java,
                    "blog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}