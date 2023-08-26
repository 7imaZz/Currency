package com.shorbgy.currency.featuremain.domain.model

import java.io.Serializable

data class Currency(
    val symbol: String,
    val position: Int,
    val value: Double
): Serializable
