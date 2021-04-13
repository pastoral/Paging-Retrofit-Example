package com.munir.pagingretrofitrecycler.data.model

data class RepoContributionResponseItem(
    val author: Author,
    val total: Int,
    val weeks: List<Week>
)