package com.revolut.moneytransferservice.periphery.dto

data class ResponseMessage(
    val success: Boolean,
    val errorMessage: String? = null
)