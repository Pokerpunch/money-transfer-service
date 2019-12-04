package com.revolut.moneytransferservice.periphery.dto

class AccountDetails(
    val id: Long,
    val accountOwnerId: Long,
    val balanceInMinor: Long,
    val dateCreated: String,
    val dateUpdated: String
)