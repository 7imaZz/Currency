package com.shorbgy.currency.featuremain.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.shorbgy.currency.R
import com.shorbgy.currency.core.Constants
import com.shorbgy.currency.core.Resource
import com.shorbgy.currency.databinding.FragmentHomeBinding
import com.shorbgy.currency.featuremain.domain.model.Currency
import com.shorbgy.currency.featuremain.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var parent: MainActivity
    private lateinit var fromAdapter: ArrayAdapter<String>
    private lateinit var toAdapter: ArrayAdapter<String>

    private val viewModel: HomeViewModel by viewModels()
    private var fromList = mutableListOf<String>()
    private var toList = mutableListOf<String>()
    private var ratesMap = hashMapOf<String, Double>()
    private var currencies = mutableListOf<Currency>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(requireView())
        parent = requireActivity() as MainActivity

        initSpinners()
        observeData()
    }

    private fun initSpinners(){
        fromAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fromList)
        toAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, toList)
        binding.apply {
            fromSp.adapter = fromAdapter
            toSp.adapter = toAdapter

            // Handle (From Spinner) Changes
            fromSp.onItemSelectedListener = object: OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.setSelectedFromCurrency(currencies[position])
                    viewModel.convertCurrency(binding.fromEt.text.toString().ifEmpty { "1.0" })
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }

            // Handle (To Spinner) Changes
            toSp.onItemSelectedListener = object: OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.setSelectedToCurrency(currencies[position])
                    viewModel.convertCurrency(binding.fromEt.text.toString().ifEmpty { "1.0" })
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }
        }
    }

    private fun observeData(){
        // Observe Latest Rates
        lifecycleScope.launch {
            viewModel.latestRatesFlow.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progress.isVisible = false
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> binding.progress.isVisible = true
                    is Resource.Success -> {
                        onEvents()
                        binding.progress.isVisible = false

                        ratesMap = it.data!!.rates

                        currencies.clear()
                        ratesMap.keys.forEachIndexed { index, s ->
                            currencies.add(Currency(s, index, ratesMap[s]!!))
                            if (s==it.data.base) viewModel.setSelectedFromCurrency(currencies[index])
                        }

                        Log.d("7imaZz", "observeData: $currencies")

                        fromList = ratesMap.keys.toMutableList()
                        toList = ratesMap.keys.toMutableList()

                        if (currencies.isNotEmpty()){
                            viewModel.setSelectedToCurrency(currencies[0])
                            viewModel.convertCurrency()
                            binding.fromSp.setSelection(viewModel.getSelectedFromCurrency().position)
                            binding.toSp.setSelection(viewModel.getSelectedToCurrency().position)
                        }

                        refreshFromSpinner()
                        refreshToSpinner()
                    }
                }
            }
        }

        // Observe Result Changes to Update toEt with Result
        lifecycleScope.launch {
            viewModel.resultFlow.collectLatest {
                when(it){
                    is Resource.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    is Resource.Loading -> {}
                    is Resource.Success -> binding.toEt.setText(it.data.toString())
                }
            }
        }
    }

    private fun onEvents(){
        binding.apply {
            // Listen to (To EditText) Changes
            fromEt.addTextChangedListener {
                viewModel.convertCurrency(fromEt.text.toString().ifEmpty { "1.0" })
            }

            // Handle (Swap Image) Click
            swapImg.setOnClickListener {
                fromSp.setSelection(viewModel.getSelectedToCurrency().position)
                toSp.setSelection(viewModel.getSelectedFromCurrency().position)
                viewModel.convertCurrency(fromEt.text.toString().ifEmpty { "1.0" })
            }

            detailsBtn.setOnClickListener {
                parent.navController.navigate(R.id.action_homeFragment_to_detailsFragment,
                    bundleOf(
                        Constants.FROM_CURRENCY to viewModel.getSelectedFromCurrency(),
                        Constants.TO_CURRENCY to viewModel.getSelectedToCurrency()
                    )
                )
            }
        }
    }

    private fun refreshFromSpinner(){
        fromAdapter.apply {
            clear()
            addAll(fromList)
            notifyDataSetChanged()
        }
    }

    private fun refreshToSpinner(){
        toAdapter.apply {
            clear()
            addAll(toList)
            notifyDataSetChanged()
        }
    }
}