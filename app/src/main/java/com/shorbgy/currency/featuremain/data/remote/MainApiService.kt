package com.shorbgy.currency.featuremain.data.remote

import com.shorbgy.currency.BuildConfig
import com.shorbgy.currency.core.Constants
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MainApiService {

    @GET("api/latest")
    suspend fun getRates(
        @Query(Constants.ACCESS_KEY) accessKey: String = BuildConfig.ACCESS_KEY
    ): Response<RatesResponse>
}