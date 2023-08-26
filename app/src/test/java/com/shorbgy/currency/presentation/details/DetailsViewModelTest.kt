package com.shorbgy.currency.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.shorbgy.currency.MainDispatcherRule
import com.shorbgy.currency.core.Constants
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.data.remote.FakeMainRepo
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.domain.usecase.GetHistoricalRatesUseCase
import com.shorbgy.currency.featuremain.presentation.details.DetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeMainRepo: FakeMainRepo
    private lateinit var getHistoricalRatesUseCase: GetHistoricalRatesUseCase
    private lateinit var viewModel: DetailsViewModel
    private lateinit var savedHandleStateHandle: SavedStateHandle

    @Before
    fun setup(){
        fakeMainRepo = FakeMainRepo()
        getHistoricalRatesUseCase = GetHistoricalRatesUseCase(fakeMainRepo)
        savedHandleStateHandle = SavedStateHandle()
        savedHandleStateHandle[Constants.TO_CURRENCY] = Currency("EUR", 0, 1.0)
        savedHandleStateHandle[Constants.FROM_CURRENCY] = Currency("USD", 1, 2.0)
    }

    @Test
    fun apiLoadingState_isSetCorrectly() = runTest{
        fakeMainRepo.setShouldReturnLoadingState(true)
        viewModel = DetailsViewModel(getHistoricalRatesUseCase, savedHandleStateHandle)
        Truth.assertThat(viewModel.prev1DayFlow.value is Resource.Loading).isEqualTo(true)
    }

    @Test
    fun apiErrorState_isSetCorrectly() = runTest {
        fakeMainRepo.setShouldReturnNetworkError(true)
        viewModel = DetailsViewModel(getHistoricalRatesUseCase, savedHandleStateHandle)
        Truth.assertThat(viewModel.prev1DayFlow.value is Resource.Error).isEqualTo(true)
    }

    @Test
    fun apiSuccessState_isSetCorrectly() = runTest {
        viewModel = DetailsViewModel(getHistoricalRatesUseCase, savedHandleStateHandle)
        Truth.assertThat(viewModel.prev1DayFlow.value.data).isEqualTo(fakeMainRepo.rateResponse)
    }

    @Test
    fun convertToRightCurrencyValue_isCorrectlyWork(){
        viewModel = DetailsViewModel(getHistoricalRatesUseCase, savedHandleStateHandle)
        val res = viewModel.convertToRightCurrencyValue(fakeMainRepo.rateResponse.rates)
        Truth.assertThat(res).isEqualTo(0.5)
    }

    @Test
    fun computeChange_isCorrectlyWork(){
        viewModel = DetailsViewModel(getHistoricalRatesUseCase, savedHandleStateHandle)
        val res = viewModel.computeChange(fakeMainRepo.rateResponse.rates)
        Truth.assertThat(res).isEqualTo(0.0)
    }
}