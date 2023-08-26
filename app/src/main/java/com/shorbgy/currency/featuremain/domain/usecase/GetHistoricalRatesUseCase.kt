package com.shorbgy.currency.featuremain.domain.usecase

import com.shorbgy.currency.featuremain.domain.repo.MainRepo
import javax.inject.Inject

class GetHistoricalRatesUseCase
@Inject
constructor(
    private val repo: MainRepo
){
    suspend operator fun invoke(date: String, symbols: List<String>) = repo.getHistoricalRates(date, symbols)
}