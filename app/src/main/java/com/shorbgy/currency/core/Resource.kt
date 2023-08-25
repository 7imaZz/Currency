package com.shorbgy.currency.core

import java.io.Serializable

sealed class Resource<T>(val data: T ?= null, val message: String? = null): Serializable {
    class Success<T>(data: T): Resource<T>(data), Serializable
    class Error<T>(data: T? = null, message: String): Resource<T>(data, message), Serializable
    class Loading<T> : Resource<T>(), Serializable
}