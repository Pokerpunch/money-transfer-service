package com.revolut.moneytransferservice.periphery.dto

class AccountDetails(
    val id: Long,
    val accountOwnerId: Long,
    val balanceMinor: Long,
    val dateCreated: String,
    val dateUpdated: String
)