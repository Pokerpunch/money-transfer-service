package com.revolut.moneytransferservice.periphery.mapper

import com.revolut.moneytransferservice.core.domain.Transfer
import com.revolut.moneytransferservice.periphery.dto.TransferDetails

object TransferMapper {

    fun mapToTransferDetails(transfer: Transfer): TransferDetails =
        transfer.let {
            TransferDetails(
                id = it.id,
                originAccountId = it.originAccountId,
                destinationAccountId = it.destinationAccountId,
                amountInMinor = it.amountInMinor,
                dateCreated = it.dateCreated.toString()
            )
        }
}
