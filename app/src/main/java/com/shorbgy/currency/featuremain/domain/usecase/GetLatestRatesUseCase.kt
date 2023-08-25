package com.shorbgy.currency.featuremain.domain.usecase

import com.shorbgy.currency.featuremain.domain.repo.MainRepo
import javax.inject.Inject

class GetLatestRatesUseCase
@Inject
constructor(
    private val repo: MainRepo
){
    suspend operator fun invoke() = repo.getLatestRates()
}