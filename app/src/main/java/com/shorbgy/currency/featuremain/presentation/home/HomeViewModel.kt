package com.shorbgy.currency.featuremain.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.usecase.GetLatestRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val getLatestRatesUseCase: GetLatestRatesUseCase
): ViewModel(){

    private val _latestRatesFlow = MutableStateFlow<Resource<RatesResponse>>(Resource.Loading())
    val latestRatesFlow = _latestRatesFlow.asStateFlow()

    private val _resultFlow = MutableSharedFlow<Resource<Double>>()
    val resultFlow = _resultFlow.asSharedFlow()

    private lateinit var selectedFromCurrency: Currency
    private lateinit var selectedToCurrency: Currency
    private lateinit var baseCurrency: Currency

    init {
        getLatestRates()
    }
    private fun getLatestRates() = viewModelScope.launch {
        _latestRatesFlow.emit(Resource.Loading())
        _latestRatesFlow.emit(getLatestRatesUseCase())
    }

    fun setSelectedFromCurrency(currency: Currency){
        selectedFromCurrency = currency
    }

    fun setSelectedToCurrency(currency: Currency){
        selectedToCurrency = currency
    }
    fun setBaseCurrency(currency: Currency){
        baseCurrency = currency
    }

    fun getBaseCurrency() = baseCurrency
    fun getSelectedFromCurrency() = selectedFromCurrency
    fun getSelectedToCurrency() = selectedToCurrency

    fun convertCurrency(valueToConvert: String = "1.0") = viewModelScope.launch{
        // Check if string came from edittext is valid or not
        val valueToConvertDouble = valueToConvert.toDoubleOrNull()
        if (valueToConvertDouble != null) {
            // Convert current currency to it's base currency equivalent then convert the base currency to targeted currency
            val equivalent = baseCurrency.value/selectedFromCurrency.value
            val result = valueToConvertDouble * equivalent * selectedToCurrency.value
            _resultFlow.emit(Resource.Success(String.format("%.3f", result).toDouble()))
        }else{
            _resultFlow.emit(Resource.Error(message = "invalid number"))
        }
    }
}
