package com.premelc.templateproject.service

import android.util.Log
import com.premelc.templateproject.networking.ApiClient
import com.premelc.templateproject.service.apis.QuotesApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuotesService {

    private var quotesApi: QuotesApi = ApiClient.retrofit.create(QuotesApi::class.java)

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getQuotes() {
        GlobalScope.launch {
            val result = quotesApi.getQuotes()
            Log.i("premoDebug", result.count.toString())
        }
    }
}