package com.revolut.moneytransferservice.core.service

import com.revolut.moneytransferservice.core.dao.TransferDAO
import com.revolut.moneytransferservice.core.domain.Transfer
import com.revolut.moneytransferservice.core.exception.NotFoundException
import com.revolut.moneytransferservice.core.service.validator.TransferValidator

class TransferService(
    private val transferDAO: TransferDAO
) {
    fun getAllTransfers(): List<Transfer> =
        transferDAO.fetchAll()

    fun getTransfer(id: Long): Transfer =
        transferDAO.findById(id) ?: throw NotFoundException("Transfer with ID: $id not found")

    fun executeTransfer(originAccountId: Long, destinationAccountId: Long, amountMinor: Long): Transfer =
        also {
            TransferValidator.validateBeforeStartingTransfer(originAccountId, destinationAccountId, amountMinor)
        }.run {
            transferDAO.updateAccountsAndSaveTransfer(
                Transfer(originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = amountMinor)
            )
        }
}