package com.shorbgy.currency.featuremain.domain.repo

import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.RatesResponse

interface MainRepo {
    suspend fun getLatestRates(): Resource<RatesResponse>
}