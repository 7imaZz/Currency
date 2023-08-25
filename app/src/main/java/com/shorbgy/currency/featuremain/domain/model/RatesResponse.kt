package com.shorbgy.currency.featuremain.domain.model


import com.google.gson.annotations.SerializedName

data class RatesResponse(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: HashMap<String, Double>,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("timestamp")
    val timestamp: Int,
    val error: Error?
)