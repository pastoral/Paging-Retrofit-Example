package com.munir.pagingretrofitrecycler.data

import com.munir.pagingretrofitrecycler.data.model.Item

sealed class RepositorySearchResult {
    data class Success(val data : List<Item>):RepositorySearchResult()
    data class Error(val data : Exception):RepositorySearchResult()
}