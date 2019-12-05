package com.revolut.moneytransferservice.periphery.mapper

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.periphery.dto.AccountCreationRequest
import com.revolut.moneytransferservice.periphery.dto.AccountDetails

object AccountMapper {

    fun mapToAccountEntity(accountCreationRequest: AccountCreationRequest): Account =
        Account(accountOwnerId = accountCreationRequest.accountOwnerId).apply {
            balanceMinor = accountCreationRequest.balanceMinor
        }

    fun mapToAccountDetails(account: Account): AccountDetails =
        account.let {
            AccountDetails(
                id = it.id,
                accountOwnerId = it.accountOwnerId,
                balanceMinor = it.balanceMinor,
                dateCreated = it.dateCreated.toString(),
                dateUpdated = it.dateUpdated.toString()
            )
        }
}