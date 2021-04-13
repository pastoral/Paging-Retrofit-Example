package com.munir.pagingretrofitrecycler.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.munir.pagingretrofitrecycler.api.GitHubAPIs
import com.munir.pagingretrofitrecycler.api.IN_QUALIFIER
import com.munir.pagingretrofitrecycler.data.model.Item
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val STARTING_PAGE_INDEX = 1

class GitHubPagingSource(
    private val api:GitHubAPIs,
    private val query:String
):PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val position = params.key ?: STARTING_PAGE_INDEX
        val apiQuery = query + IN_QUALIFIER

        return try {
            val response = api.searchRepos(apiQuery, position, params.loadSize)
            val repos = response.items
            // temp code//

            //val contributorResponse = api.contributorStat("guofudong", "KotlinAndroid")

//            for(r in repos){
//                //getTopContributors(api,r.owner.login,r.name)
//                getTopContributors(api,"line","armeria")
//            }

            // temp code//
            val nextPageNumber = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                    data = repos,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextPageNumber
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 12
    }

    //api: GitHubAPIs, owner:String, repoName:String
    suspend fun getTopContributors(api: GitHubAPIs,owner:String, repoName:String) {
        val contributorResponse = api.contributorStat(owner,repoName)
        //val conRepos = contributorResponse.size
        var maxTotal = contributorResponse[0].total
        var mostContributor = contributorResponse[0].author.login
        for (i in contributorResponse.indices-1){
            if (contributorResponse[i].total >= maxTotal){
                maxTotal = contributorResponse[i].total
                mostContributor = contributorResponse[i].author.login
            }
            //Log.d("Contributors: $i  ", contributorResponse[i].author.login + "  "+contributorResponse[i].total.toString())
        }
        Log.d("Contributors most", mostContributor.toString())
    }

}