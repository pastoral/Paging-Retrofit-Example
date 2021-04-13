package com.munir.pagingretrofitrecycler.data.model

import com.google.gson.annotations.SerializedName

data class RepositorySearchResponse(
    val incomplete_results: Boolean,
    @SerializedName("items")  val items: List<Item>,
    @SerializedName("total_count") val total_count: Int,
    val nextPage: Int? = null
)