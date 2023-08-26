package com.shorbgy.currency.featuremain.data.remote

import com.shorbgy.currency.BuildConfig
import com.shorbgy.currency.core.Constants
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApiService {

    @GET("api/latest")
    suspend fun getRates(
        // Replace this with your accessKey or add your accessKey in local.properties
        @Query(Constants.ACCESS_KEY) accessKey: String = BuildConfig.ACCESS_KEY
    ): Response<RatesResponse>

    @GET("api/{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("symbols") symbols: String,
        // Replace this with your accessKey or add your accessKey in local.properties
        @Query(Constants.ACCESS_KEY) accessKey: String = BuildConfig.ACCESS_KEY
    ): Response<RatesResponse>
}