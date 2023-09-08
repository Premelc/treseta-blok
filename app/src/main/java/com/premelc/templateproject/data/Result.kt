package com.premelc.templateproject.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("_id")
    val _id: String,
    @SerialName("author")
    val author: String,
    @SerialName("authorSlug")
    val authorSlug: String,
    @SerialName("content")
    val content: String,
    @SerialName("dateAdded")
    val dateAdded: String,
    @SerialName("dateModified")
    val dateModified: String,
    @SerialName("length")
    val length: Int,
    @SerialName("tags")
    val tags: List<String>,
)