package com.revolut.moneytransferservice.core.service.validator

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.TransferNotPossibleException
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test

class TransferValidatorTest {

    @Test
    fun `should throw exception if transfer amount is negative`() {
        // GIVEN
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmountInMinor = -1L

        // THEN
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN
            TransferValidator.validateBeforeStartingTransfer(originAccountId, destinationAccountId, transferAmountInMinor)

        }.withMessageContaining("Transfer amount: [$transferAmountInMinor] is not positive")
    }

    @Test
    fun `should throw exception if transfer amount is zero`() {
        // GIVEN
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmountInMinor = 0L

        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN
            TransferValidator.validateBeforeStartingTransfer(originAccountId, destinationAccountId, transferAmountInMinor)

            // THEN
        }.withMessageContaining("Transfer amount: [$transferAmountInMinor] is not positive")
    }

    @Test
    fun `should throw exception if origin and destination accounts are the same`() {
        // GIVEN
        val originAccountId = 101L
        val destinationAccountId = 101L
        val transferAmountInMinor = 103L

        // THEN
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN
            TransferValidator.validateBeforeStartingTransfer(originAccountId, destinationAccountId, transferAmountInMinor)

        }.withMessageContaining("Origin and destination accounts are the same")
    }

    @Test
    fun `should not throw any exception if accounts are different and transfer amount is positive before starting transfer`() {
        // GIVEN
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmountInMinor = 103L

        assertThatCode {

            // WHEN
            TransferValidator.validateBeforeStartingTransfer(originAccountId, destinationAccountId, transferAmountInMinor)

            // THEN
        }.doesNotThrowAnyException()
    }

    @Test
    fun `should throw exception if origin account does not have sufficient balance`() {
        // GIVEN
        val transferAmount = 100L
        val originAccount = Account(id = 1).apply { balanceInMinor = transferAmount - 1 }

        // THEN
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN
            TransferValidator.validateBeforeFinishingTransfer(originAccount, transferAmount)

        }.withMessageContaining("Origin account [id=${originAccount}] has a balance: ${originAccount.balanceInMinor} that is less than the transfer amount: $transferAmount")
    }

    @Test
    fun `should not throw any exception if origin account has balance equal to transfer amount before finishing transfer`() {
        // GIVEN
        val transferAmount = 100L
        val originAccount = Account(id = 1).apply { balanceInMinor = transferAmount }

        assertThatCode {

            // WHEN
            TransferValidator.validateBeforeFinishingTransfer(originAccount, transferAmount)

            // THEN
        }.doesNotThrowAnyException()
    }

    @Test
    fun `should not throw any exception if origin account has balance greater than the transfer amount before finishing transfer`() {
        // GIVEN
        val transferAmount = 100L
        val originAccount = Account(id = 1).apply { balanceInMinor = transferAmount }

        assertThatCode {

            // WHEN
            TransferValidator.validateBeforeFinishingTransfer(originAccount, transferAmount)

            // THEN
        }.doesNotThrowAnyException()
    }
}