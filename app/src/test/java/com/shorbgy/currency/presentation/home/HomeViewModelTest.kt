package com.shorbgy.currency.presentation.home

import com.google.common.truth.Truth.assertThat
import com.shorbgy.currency.MainDispatcherRule
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.data.remote.FakeMainRepo
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.domain.usecase.GetLatestRatesUseCase
import com.shorbgy.currency.featuremain.presentation.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeMainRepo: FakeMainRepo
    private lateinit var getLatestRatesUseCase: GetLatestRatesUseCase
    private lateinit var viewModel: HomeViewModel


    @Before
    fun setup(){
        fakeMainRepo = FakeMainRepo()
        getLatestRatesUseCase = GetLatestRatesUseCase(fakeMainRepo)
    }

    @Test
    fun apiLoadingState_isSetCorrectly() = runTest{
        fakeMainRepo.setShouldReturnLoadingState(true)
        viewModel = HomeViewModel(getLatestRatesUseCase)
        assertThat(viewModel.latestRatesFlow.value is Resource.Loading).isEqualTo(true)
    }

    @Test
    fun apiErrorState_isSetCorrectly() = runTest {
        fakeMainRepo.setShouldReturnNetworkError(true)
        viewModel = HomeViewModel(getLatestRatesUseCase)
        assertThat(viewModel.latestRatesFlow.value is Resource.Error).isEqualTo(true)
    }

    @Test
    fun apiSuccessState_isSetCorrectly() = runTest {
        viewModel = HomeViewModel(getLatestRatesUseCase)
        assertThat(viewModel.latestRatesFlow.value.data).isEqualTo(fakeMainRepo.rateResponse)
    }

    @Test
    fun convertCurrencyErrorState_isSetCorrectly() = runTest {
        viewModel = HomeViewModel(getLatestRatesUseCase)
        viewModel.convertCurrency("sdkmclsd")
        assertThat(viewModel.resultFlow.first() is Resource.Error).isEqualTo(true)
    }

    @Test
    fun convertCurrencySuccessState_isSetCorrectly() = runTest {
        viewModel = HomeViewModel(getLatestRatesUseCase)
        viewModel.setBaseCurrency(Currency("EUR", 0, 1.0))
        viewModel.setSelectedFromCurrency(Currency("USD", 2, 1.079443))
        viewModel.setSelectedToCurrency(Currency("EGP", 1, 33.394582))
        viewModel.convertCurrency("1.0")
        assertThat(viewModel.resultFlow.first().data).isEqualTo(30.937)
    }
}