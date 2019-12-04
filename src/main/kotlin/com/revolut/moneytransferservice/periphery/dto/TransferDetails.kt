package com.revolut.moneytransferservice.periphery.dto

data class TransferDetails(
    val id: Long,
    val originAccountId: Long,
    val destinationAccountId: Long,
    val amountInMinor: Long,
    val dateCreated: String
)