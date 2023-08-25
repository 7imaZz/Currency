package com.shorbgy.currency.data.remote

import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.repo.MainRepo

class FakeMainRepo: MainRepo{

    private var shouldReturnNetworkError = false
    fun setShouldReturnNetworkError(value: Boolean){
        shouldReturnNetworkError = value
    }
    override suspend fun getLatestRates(): Resource<RatesResponse> {
        if (shouldReturnNetworkError)
            return Resource.Error(message = "Error")
        return Resource.Success(RatesResponse("EUR", "2023-08-25", hashMapOf(), true, 1, null))
    }
}