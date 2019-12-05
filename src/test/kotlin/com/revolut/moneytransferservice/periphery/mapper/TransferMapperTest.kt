package com.revolut.moneytransferservice.periphery.mapper

import com.revolut.moneytransferservice.core.domain.Transfer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TransferMapperTest {

    @Test
    fun `should map to transfer details`() {
        // GIVEN
        val transfer = Transfer(
            id = 101,
            originAccountId = 1001,
            destinationAccountId = 1003,
            amountMinor = 500
        )

        // WHEN
        val transferDetails = TransferMapper.mapToTransferDetails(transfer)

        // THEN
        with(transferDetails) {
           assertThat(id).isEqualTo(transfer.id)
           assertThat(originAccountId).isEqualTo(transfer.originAccountId)
           assertThat(destinationAccountId).isEqualTo(transfer.destinationAccountId)
           assertThat(amountMinor).isEqualTo(transfer.amountMinor)
           assertThat(dateCreated).isEqualTo(transfer.dateCreated.toString())
        }
    }
}