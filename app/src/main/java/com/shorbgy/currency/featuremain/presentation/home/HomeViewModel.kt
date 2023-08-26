package com.shorbgy.currency.featuremain.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.usecase.GetLatestRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    init {
        getLatestRates()
    }
    private fun getLatestRates() = viewModelScope.launch {
        _latestRatesFlow.emit(getLatestRatesUseCase())
    }

    fun setSelectedFromCurrency(currency: Currency){
        selectedFromCurrency = currency
    }

    fun setSelectedToCurrency(currency: Currency){
        selectedToCurrency = currency
    }
    fun getSelectedFromCurrency() = selectedFromCurrency
    fun getSelectedToCurrency() = selectedToCurrency

    fun convertCurrency(input: String = "1.0") = viewModelScope.launch(Dispatchers.IO){
        // Check if string came from edittext is valid or not
        val valueToConvert = input.toDoubleOrNull()
        if (valueToConvert != null) {
            // Convert current currency to it's base currency equivalent then convert the base currency to targeted currency
            val equivalent = convertToBaseCurrencyEquivalent(selectedFromCurrency.value)
            val result = valueToConvert * equivalent * selectedToCurrency.value
            _resultFlow.emit(Resource.Success(String.format("%.3f", result).toDouble()))
        }else{
            _resultFlow.emit(Resource.Error(message = "invalid number"))
        }
    }

    private fun convertToBaseCurrencyEquivalent(value: Double): Double{
        if (value == 0.0) return 0.0
        return 1/value
    }
}
