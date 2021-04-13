package com.munir.pagingretrofitrecycler.container

import androidx.lifecycle.ViewModelProvider
import com.munir.pagingretrofitrecycler.api.GitHubAPIs
import com.munir.pagingretrofitrecycler.repository.GithubRepoPagingData
import com.munir.pagingretrofitrecycler.ui.GithubRepoViewModelFactory

object ObjectContainer{
    private fun provideGithubRepository():GithubRepoPagingData{
        return GithubRepoPagingData(GitHubAPIs.invoke())
    }
    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return GithubRepoViewModelFactory(provideGithubRepository())
    }
}