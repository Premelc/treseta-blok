package com.premelc.templateproject.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteListEntity(
    @SerialName("count")
    val count: Int,
    @SerialName("lastItemIndex")
    val lastItemIndex: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("results")
    val results: List<QuoteEntity>,
    @SerialName("totalCount")
    val totalCount: Int,
    @SerialName("totalPages")
    val totalPages: Int
)