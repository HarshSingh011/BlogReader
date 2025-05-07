package com.example.blog

import android.app.Application
import com.example.blog.worker.BlogSyncWorker

class BlogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BlogSyncWorker.schedule(this)
    }
}