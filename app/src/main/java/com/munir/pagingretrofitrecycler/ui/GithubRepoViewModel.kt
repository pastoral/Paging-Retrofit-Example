package com.munir.pagingretrofitrecycler.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.munir.pagingretrofitrecycler.data.model.Item
import com.munir.pagingretrofitrecycler.repository.GithubRepoPagingData
import kotlinx.coroutines.flow.Flow

class GithubRepoViewModel(private val githubRepoPagingData: GithubRepoPagingData): ViewModel() {
    var currentQueryValue:String ? = null
    var currentResultValue: Flow<PagingData<Item>>? = null

    fun searchRepo(queryString:String):Flow<PagingData<Item>>{
        val lastResult = currentResultValue
        if(queryString == currentQueryValue && lastResult!=null){
            return lastResult
        }
        currentQueryValue = queryString
        val newResult : Flow<PagingData<Item>> =
            githubRepoPagingData.getResultStream(queryString).cachedIn(viewModelScope)

        currentResultValue = newResult
        return newResult
    }
}