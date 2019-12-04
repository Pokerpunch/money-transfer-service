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

    fun executeTransfer(originAccountId: Long, destinationAccountId: Long, amountInMinor: Long) {
        TransferValidator.validateBeforeStartingTransfer(originAccountId, destinationAccountId, amountInMinor)

        transferDAO.updateAccountsAndSaveTransfer(
            Transfer(originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountInMinor = amountInMinor)
        )
    }
}