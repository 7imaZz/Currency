package com.shorbgy.currency.featuremain.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.shorbgy.currency.databinding.ItemOtherHistoricalRowBinding
import com.shorbgy.currency.featuremain.domain.model.Currency

class OtherCurrenciesAdapter: ListAdapter<Currency, OtherCurrenciesAdapter.OtherCurrencyViewHolder>(COMPARATOR){

    companion object{
        private val COMPARATOR = object: DiffUtil.ItemCallback<Currency>(){
            override fun areItemsTheSame(oldItem: Currency, newItem: Currency) =
                oldItem.symbol == newItem.symbol
            override fun areContentsTheSame(oldItem: Currency, newItem: Currency) =
                oldItem == newItem
        }
    }

    class OtherCurrencyViewHolder(private val binding: ItemOtherHistoricalRowBinding): ViewHolder(binding.root){
        fun bind(currency: Currency){
            binding.apply {
                currencyTv.text = currency.symbol
                priceTv.text = currency.value.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherCurrencyViewHolder {
        val binding = ItemOtherHistoricalRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherCurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OtherCurrencyViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem)
    }
}