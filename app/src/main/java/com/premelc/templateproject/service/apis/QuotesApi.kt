package com.premelc.templateproject.service.apis

import com.premelc.templateproject.data.QuoteListEntity
import retrofit2.http.GET

interface QuotesApi {
    @GET("/quotes")
    suspend fun getQuotes() : QuoteListEntity
}