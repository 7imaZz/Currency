package com.shorbgy.currency.data.remote

import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.repo.MainRepo

class FakeMainRepo : MainRepo {

    private var shouldReturnNetworkError = false
    private var shouldReturnLoadingState = false
    private val rates = hashMapOf("EUR" to 1.0, "USD" to 2.0)
    val rateResponse = RatesResponse("EUR", "2023-08-25", rates, true, 1, null)
    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setShouldReturnLoadingState(value: Boolean) {
        shouldReturnLoadingState = value
    }

    override suspend fun getLatestRates(): Resource<RatesResponse> {
        if (shouldReturnLoadingState)
            return Resource.Loading()
        if (shouldReturnNetworkError)
            return Resource.Error(message = "Error")
        return Resource.Success(rateResponse)
    }

    override suspend fun getHistoricalRates(
        date: String,
        symbols: List<String>
    ): Resource<RatesResponse> {
        if (shouldReturnLoadingState)
            return Resource.Loading()
        if (shouldReturnNetworkError)
            return Resource.Error(message = "Error")
        return Resource.Success(rateResponse)
    }
}