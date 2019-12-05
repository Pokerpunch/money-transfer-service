package com.revolut.moneytransferservice.core.service.validator

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.TransferNotPossibleException

object TransferValidator {

    fun validateBeforeStartingTransfer(originAccountId: Long, destinationAccountId: Long, amountMinor: Long) {
        checkTransferAmountIsPositive(amountMinor)
        checkOriginAndDestinationAccountsAreDifferent(originAccountId, destinationAccountId)
    }

    fun validateBeforeFinishingTransfer(originAccount: Account, amountMinor: Long) {
        checkOriginAccountHasSufficientBalance(originAccount, amountMinor)
    }

    private fun checkTransferAmountIsPositive(amountMinor: Long) {
        if (amountMinor <= 0) {
            throw TransferNotPossibleException("Transfer amount: [$amountMinor] is not positive")
        }
    }

    private fun checkOriginAndDestinationAccountsAreDifferent(originAccountId: Long, destinationAccountId: Long) {
        if (originAccountId == destinationAccountId) {
            throw TransferNotPossibleException("Origin and destination accounts are the same")
        }
    }

    private fun checkOriginAccountHasSufficientBalance(originAccount: Account, amountMinor: Long) {
        if (originAccount.balanceMinor < amountMinor) {
            throw TransferNotPossibleException(
                "Origin account [id=${originAccount}] has a balance: ${originAccount.balanceMinor} that is less than the transfer amount: $amountMinor"
            )
        }
    }
}