package com.revolut.moneytransferservice.core.service

import com.nhaarman.mockito_kotlin.whenever
import com.revolut.moneytransferservice.core.dao.TransferDAO
import com.revolut.moneytransferservice.core.domain.Transfer
import com.revolut.moneytransferservice.core.exception.NotFoundException
import com.revolut.moneytransferservice.core.exception.TransferNotPossibleException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TransferServiceTest {

    @Mock
    private lateinit var transferDAO: TransferDAO

    @InjectMocks
    private lateinit var transferService: TransferService

    @Test
    fun `should return list of existing transfers when trying to get all transfers`() {
        // GIVEN some existing transfers
        val existingTransfers = listOf(
            Transfer(originAccountId = 101, destinationAccountId = 102, amountMinor = 103),
            Transfer(originAccountId = 1001, destinationAccountId = 1002, amountMinor = 1003)
        )
        whenever(transferDAO.fetchAll()).thenReturn(existingTransfers)

        // WHEN the try to fetch all existing transfers
        val allTransfers = transferService.getAllTransfers()

        // THEN the returned transfers are the ones we expect
        assertThat(allTransfers).containsExactlyInAnyOrderElementsOf(existingTransfers)
    }

    @Test
    fun `should return empty list when trying to get all transfers and no transfers exist`() {
        // GIVEN no existing transfer
        whenever(transferDAO.fetchAll()).thenReturn(emptyList())

        // WHEN we try to fetch all existing transfers
        val allTransfers = transferService.getAllTransfers()

        // THEN no transfers are returned
        assertThat(allTransfers).isEmpty()
    }

    @Test
    fun `should return existing transfer when trying to find transfer by ID`() {
        // GIVEN a transfer ID
        val transferId = 101L

        // ... that corresponds to an existing transfer
        val existingTransfer = Transfer(originAccountId = 101, destinationAccountId = 102, amountMinor = 103)
        whenever(transferDAO.findById(transferId)).thenReturn(existingTransfer)

        // WHEN the try to get the transfer
        val returnedTransfer = transferService.getTransfer(transferId)

        // THEN the returned transfer is the one we expected
        assertThat(returnedTransfer).isEqualTo(existingTransfer)
    }

    @Test
    fun `should throw exception when trying to find non-existent transfer`() {
        // GIVEN a transfer ID
        val transferId = 101L

        // ... that has no corresponding transfer
        whenever(transferDAO.findById(transferId)).thenReturn(null)

        // THEN an exception is thrown
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {

            // WHEN we try to get the transfer
            transferService.getTransfer(transferId)
        }
    }

    @Test
    fun `should initiate transfer if initial validation is successful`() {
        // GIVEN an origin an destination accounts that are different
        val originAccountId = 101L
        val destinationAccountId = 102L

        // ... a positive transfer amount
        val transferAmountMinor = 500L

        // ... and an expected transfer
        val unpersistedTransfer = Transfer(id = -1, originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmountMinor)
        val persistedTransfer = Transfer(id = 1, originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmountMinor)
        whenever(transferDAO.updateAccountsAndSaveTransfer(unpersistedTransfer)).thenReturn(persistedTransfer)

        // WHEN we try to initiate the transfer
        val transfer = transferService.executeTransfer(
            originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmountMinor
        )

        // THEN the saved transfer is returned
        assertThat(transfer).isEqualTo(persistedTransfer)
    }

    @Test
    fun `should throw exception when trying to initiate transfer if accounts are the same`() {
        // GIVEN an origin and destination accounts that are the same
        val originAccountId = 101L
        val destinationAccountId = originAccountId

        // ... and a positive transfer amount
        val transferAmountMinor = 500L

        // THEN an exception is thrown
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN we try to initiate the transfer
            transferService.executeTransfer(
                originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmountMinor
            )
        }
    }

    @Test
    fun `should throw exception when trying to initiate transfer and the amount is negative`() {
        // GIVEN an origin and destination accounts that are different
        val originAccountId = 101L
        val destinationAccountId = 102L

        // ... and a negative transfer amount
        val transferAmountMinor = -1L

        // THEN an exception is thrown
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN we try to initiate the transfer
            transferService.executeTransfer(
                originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmountMinor
            )
        }
    }

    @Test
    fun `should throw exception when trying to initiate transfer and the amount is zero`() {
        // GIVEN an origin and destination accounts that are different
        val originAccountId = 101L
        val destinationAccountId = 102L

        // ... and a zero transfer amount
        val transferAmountMinor = 0L

        // THEN an exception is thrown
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN we try to initiate the transfer
            transferService.executeTransfer(
                originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmountMinor
            )
        }
    }
}