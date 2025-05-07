package com.example.blog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blog.presentation.ui.BlogApp
import com.example.blog.presentation.viewmodel.BlogViewModel
import com.example.blog.presentation.viewmodel.BlogViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<BlogViewModel>(
                factory = BlogViewModelFactory()
            )
            BlogApp(viewModel)
        }
    }
}