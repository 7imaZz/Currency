package com.shorbgy.currency.featuremain.data.di

import com.shorbgy.currency.featuremain.data.remote.MainApiService
import com.shorbgy.currency.featuremain.data.repo.MainRepoImpl
import com.shorbgy.currency.featuremain.domain.repo.MainRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    fun provideMainRepo(api: MainApiService): MainRepo {
        return MainRepoImpl(api)
    }
}