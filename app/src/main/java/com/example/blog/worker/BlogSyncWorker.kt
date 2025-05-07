package com.example.blog.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.blog.data.local.BlogDatabase
import com.example.blog.data.local.datasource.LocalBlogDataSource
import com.example.blog.data.network.BlogApi
import com.example.blog.data.remote.datasource.RemoteBlogDataSource
import java.util.concurrent.TimeUnit

class BlogSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val api = BlogApi.create()
        val db = BlogDatabase.getDatabase(applicationContext)
        val remoteDataSource = RemoteBlogDataSource(api)
        val localDataSource = LocalBlogDataSource(db.blogPostDao())

        return try {
            val posts = remoteDataSource.getPostsEntities(1)
            localDataSource.cacheBlogPosts(posts)
            localDataSource.clearOldCache()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val SYNC_WORK_NAME = "blog_sync_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<BlogSyncWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}