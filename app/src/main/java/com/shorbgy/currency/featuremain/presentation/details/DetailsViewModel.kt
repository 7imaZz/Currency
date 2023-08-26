package com.shorbgy.currency.featuremain.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shorbgy.currency.core.Constants
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.domain.model.RatesResponse
import com.shorbgy.currency.featuremain.domain.usecase.GetHistoricalRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel
@Inject
constructor(
    private val getHistoricalRatesUseCase: GetHistoricalRatesUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    companion object{
        private val POPULAR_CURRENCIES = listOf("USD", "EUR", "KWD", "EGP", "SAR", "JPY", "SRD", "NOK", "AED", "JOD", "KYD")
    }

    private val _prev1DayFlow = MutableStateFlow<Resource<RatesResponse>>(Resource.Loading())
    val prev1DayFlow = _prev1DayFlow.asStateFlow()

    private val _prev2DayFlow = MutableStateFlow<Resource<RatesResponse>>(Resource.Loading())
    val prev2DayFlow = _prev2DayFlow.asStateFlow()

    private val _prev3DayFlow = MutableStateFlow<Resource<RatesResponse>>(Resource.Loading())
    val prev3DayFlow = _prev3DayFlow.asStateFlow()

    val fromCurrency = savedStateHandle.get<Currency>(Constants.FROM_CURRENCY)!!
    val toCurrency = savedStateHandle.get<Currency>(Constants.TO_CURRENCY)!!

    private val symbols = mutableListOf(fromCurrency.symbol, toCurrency.symbol)

    init {
        symbols.addAll(POPULAR_CURRENCIES)
        getHistoricalRates()
    }

    private fun getHistoricalRates() = viewModelScope.launch {
        _prev1DayFlow.emit(getHistoricalRatesUseCase(getPrevDayDate(1), symbols))
        _prev2DayFlow.emit(getHistoricalRatesUseCase(getPrevDayDate(2), symbols))
        _prev3DayFlow.emit(getHistoricalRatesUseCase(getPrevDayDate(3), symbols))
    }
    private fun getPrevDayDate(dayNumber: Int): String{
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, dayNumber*-1)
        return dateFormat.format(cal.time)
    }

    fun convertToRightCurrencyValue(rates: HashMap<String, Double>, toSymbol: String = toCurrency.symbol): Double{
        if (rates.containsKey(fromCurrency.symbol) && rates.containsKey(toSymbol))
            return (1/rates[fromCurrency.symbol]!!*rates[toSymbol]!!)
        throw Exception("Can't Find Currency In Rates Map")
    }

    fun computeChange(rates: HashMap<String, Double>): Double{
        return convertToRightCurrencyValue(rates)-(1/fromCurrency.value*toCurrency.value)
    }
}