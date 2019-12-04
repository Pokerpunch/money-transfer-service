package com.revolut.moneytransferservice.periphery.dto

data class TransferRequest(
    val originAccountId: Long,
    val destinationAccountId: Long,
    val amountMinor: Long
)