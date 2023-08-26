package com.shorbgy.currency.featuremain.presentation.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.shorbgy.currency.R
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.databinding.FragmentDetailsBinding
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.presentation.adapters.OtherCurrenciesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private lateinit var binding: FragmentDetailsBinding

    private val viewModel: DetailsViewModel by viewModels()
    private val otherCurrenciesAdapter1 by lazy { OtherCurrenciesAdapter() }
    private val otherCurrenciesAdapter2 by lazy { OtherCurrenciesAdapter() }
    private val otherCurrenciesAdapter3 by lazy { OtherCurrenciesAdapter() }
    private val otherCurrencies1 = mutableListOf<Currency>()
    private val otherCurrencies2 = mutableListOf<Currency>()
    private val otherCurrencies3 = mutableListOf<Currency>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailsBinding.bind(requireView())

        binding.apply {
            fromToTv.text = "${viewModel.fromCurrency.symbol} - ${viewModel.toCurrency.symbol}"
            otherCurrencies.text = "${viewModel.fromCurrency.symbol} to ${getString(R.string.other_currencies)}"
            // Initializing Recyclers Adapters
            otherCurrencies1Rv.adapter = otherCurrenciesAdapter1
            otherCurrencies2Rv.adapter = otherCurrenciesAdapter2
            otherCurrencies3Rv.adapter = otherCurrenciesAdapter3
        }

        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData(){
        // Observe Previous 1 Day Data
        lifecycleScope.launch {
            viewModel.prev1DayFlow.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progress1.isVisible = false
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> binding.progress1.isVisible = true
                    is Resource.Success -> {
                        val response = it.data!!
                        binding.progress1.isVisible = false
                        binding.otherDate1Tv.text = response.date

                        // Setup Other Currencies Data
                        response.rates.keys.forEach {k->
                            // Make sure that currency symbol which exist as key of map isn't
                            // equal to source or targeted currency, to avoid redundancy
                            if (k != viewModel.fromCurrency.symbol && k != viewModel.toCurrency.symbol)
                                // Fill first currencies list
                                otherCurrencies1.add(Currency(k, 0, viewModel.convertToRightCurrencyValue(response.rates, k)))
                        }
                        otherCurrenciesAdapter1.submitList(otherCurrencies1)

                        // Setup Historical Data For Chosen Currencies from the previous screen
                        binding.historical1Layout.apply {
                            dateTv.text = response.date
                            priceTv.text = viewModel.convertToRightCurrencyValue(response.rates).toString()
                            changeTv.text = String.format("%.8f", viewModel.computeChange(response.rates))
                        }
                    }
                }
            }
        }

        // Observe Previous 2 Day Data, Same Logic as above
        lifecycleScope.launch {
            viewModel.prev2DayFlow.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progress2.isVisible = false
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> binding.progress2.isVisible = true
                    is Resource.Success -> {
                        val response = it.data!!
                        binding.progress2.isVisible = false
                        binding.otherDate2Tv.text = response.date
                        response.rates.keys.forEach {k->
                            if (k != viewModel.fromCurrency.symbol && k != viewModel.toCurrency.symbol)
                                otherCurrencies2.add(Currency(k, 0, viewModel.convertToRightCurrencyValue(response.rates, k)))
                        }
                        otherCurrenciesAdapter2.submitList(otherCurrencies2)
                        binding.historical2Layout.apply {
                            dateTv.text = response.date
                            priceTv.text = viewModel.convertToRightCurrencyValue(response.rates).toString()
                            changeTv.text = String.format("%.8f", viewModel.computeChange(response.rates))
                        }
                    }
                }
            }
        }

        // Observe Previous 3 Day Data, Same Logic as above
        lifecycleScope.launch {
            viewModel.prev3DayFlow.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progress3.isVisible = false
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> binding.progress3.isVisible = true
                    is Resource.Success -> {
                        val response = it.data!!
                        binding.progress3.isVisible = false
                        binding.otherDate3Tv.text = response.date

                        response.rates.keys.forEach {k->
                            if (k != viewModel.fromCurrency.symbol && k != viewModel.toCurrency.symbol)
                                otherCurrencies3.add(Currency(k, 0, viewModel.convertToRightCurrencyValue(response.rates, k)))
                        }
                        otherCurrenciesAdapter3.submitList(otherCurrencies3)
                        binding.historical3Layout.apply {
                            dateTv.text = response.date
                            priceTv.text = viewModel.convertToRightCurrencyValue(response.rates).toString()
                            changeTv.text = String.format("%.8f", viewModel.computeChange(response.rates))
                        }
                    }
                }
            }
        }
    }
}