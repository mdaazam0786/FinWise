package com.example.finwise.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    data object Idle : Result<Nothing>()

    data object Loading : Result<Nothing>()
}