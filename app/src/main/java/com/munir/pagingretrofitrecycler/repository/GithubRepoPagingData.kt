package com.munir.pagingretrofitrecycler.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.munir.pagingretrofitrecycler.api.GitHubAPIs
import com.munir.pagingretrofitrecycler.data.model.Item
import com.munir.pagingretrofitrecycler.repository.GitHubPagingSource.Companion.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class GithubRepoPagingData(private val api : GitHubAPIs) {
    fun getResultStream(query: String): Flow<PagingData<Item>>{
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {GitHubPagingSource(api, query)}
        ).flow
    }
}