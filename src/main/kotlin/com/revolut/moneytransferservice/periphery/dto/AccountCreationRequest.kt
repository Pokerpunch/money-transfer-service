package com.revolut.moneytransferservice.periphery.dto

class AccountCreationRequest(
    val accountOwnerId: Long,
    val initialBalanceInMinor: Long
)
