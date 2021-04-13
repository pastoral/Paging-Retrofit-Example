package com.munir.pagingretrofitrecycler.api

import com.munir.pagingretrofitrecycler.data.model.RepoContributionResponse
import com.munir.pagingretrofitrecycler.data.model.RepoContributionResponseItem
import com.munir.pagingretrofitrecycler.data.model.RepositorySearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val IN_QUALIFIER = "in:name,description"
interface GitHubAPIs {
    @GET("search/repositories?sort=stars")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepositorySearchResponse

    @GET("/repos/{owner}/{repo}/stats/contributors")
    suspend fun contributorStat(
        @Path("owner")owner: String,
        @Path("repo")repo: String,
    ) : List<RepoContributionResponseItem>

    companion object {
        private const val BASE_URL = "https://api.github.com/"
        operator fun invoke(): GitHubAPIs = Retrofit.Builder()
            .baseUrl(GitHubAPIs.BASE_URL)
            .client(OkHttpClient.Builder().also { client ->
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                client.addInterceptor(logging)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubAPIs::class.java)

    }
}