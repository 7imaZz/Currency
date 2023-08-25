package com.shorbgy.currency.data.remote

import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.repo.MainRepo

class FakeMainRepo : MainRepo {

    private var shouldReturnNetworkError = false
    private var shouldReturnLoadingState = false
    val rateResponse = RatesResponse("EUR", "2023-08-25", hashMapOf(), true, 1, null)
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
}