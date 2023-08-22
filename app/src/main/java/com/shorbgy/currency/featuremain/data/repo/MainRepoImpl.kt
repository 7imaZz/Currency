package com.shorbgy.currency.featuremain.data.repo

import com.shorbgy.currency.featuremain.data.remote.MainApiService
import com.shorbgy.currency.featuremain.domain.repo.MainRepo

class MainRepoImpl(private val api: MainApiService): MainRepo{
}