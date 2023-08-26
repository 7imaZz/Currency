package com.shorbgy.currency.featuremain.data.repo

import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.data.remote.MainApiService
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.repo.MainRepo

class MainRepoImpl(private val api: MainApiService) : MainRepo {
    override suspend fun getLatestRates(): Resource<RatesResponse> {
        try {
            val response = api.getRates()
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.success)
                        return Resource.Success(it)
                    else
                        return Resource.Error(message = it.error?.type ?: "Unexpected Error Occurred")
                } ?: return Resource.Error(message = "Unexpected Error Occurred")
            } else {
                return Resource.Error(message = "Unexpected Error Occurred")
            }

        } catch (e: Exception) {
            return Resource.Error(message = "Couldn't reach the server. Check your internet connection")
        }

    }

    override suspend fun getHistoricalRates(
        date: String,
        symbols: List<String>
    ): Resource<RatesResponse> {
        try {
            val response = api.getHistoricalRates(date, java.lang.String.join(",", symbols))
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.success)
                        return Resource.Success(it)
                    else
                        return Resource.Error(message = it.error?.type ?: "Unexpected Error Occurred")
                } ?: return Resource.Error(message = "Unexpected Error Occurred")
            } else {
                return Resource.Error(message = "Unexpected Error Occurred")
            }

        } catch (e: Exception) {
            return Resource.Error(message = "Couldn't reach the server. Check your internet connection")
        }
    }
}