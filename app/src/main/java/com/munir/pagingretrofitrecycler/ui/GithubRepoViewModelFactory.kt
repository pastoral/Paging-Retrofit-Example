package com.munir.pagingretrofitrecycler.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.munir.pagingretrofitrecycler.repository.GithubRepoPagingData

class GithubRepoViewModelFactory(private val githubRepoPagingData: GithubRepoPagingData)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GithubRepoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GithubRepoViewModel(githubRepoPagingData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}