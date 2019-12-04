package com.revolut.moneytransferservice.core.service.validator

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.TransferNotPossibleException

object TransferValidator {

    fun validateBeforeStartingTransfer(originAccountId: Long, destinationAccountId: Long, amountInMinor: Long) {
        checkTransferAmountIsPositive(amountInMinor)
        checkOriginAndDestinationAccountsAreDifferent(originAccountId, destinationAccountId)
    }

    fun validateBeforeFinishingTransfer(originAccount: Account, amountInMinor: Long) {
        checkOriginAccountHasSufficientBalance(originAccount, amountInMinor)
    }

    private fun checkTransferAmountIsPositive(amountInMinor: Long) {
        if (amountInMinor <= 0) {
            throw TransferNotPossibleException("Transfer amount: [$amountInMinor] is not positive")
        }
    }

    private fun checkOriginAndDestinationAccountsAreDifferent(originAccountId: Long, destinationAccountId: Long) {
        if (originAccountId == destinationAccountId) {
            throw TransferNotPossibleException("Origin and destination accounts are the same")
        }
    }

    private fun checkOriginAccountHasSufficientBalance(originAccount: Account, amountInMinor: Long) {
        if (originAccount.balanceInMinor < amountInMinor) {
            throw TransferNotPossibleException(
                "Origin account [id=${originAccount}] has a balance: ${originAccount.balanceInMinor} that is less than the transfer amount: $amountInMinor"
            )
        }
    }
}