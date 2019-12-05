package com.revolut.moneytransferservice.periphery.controller

import com.revolut.moneytransferservice.periphery.dto.AccountDetails
import com.revolut.moneytransferservice.periphery.dto.TransferDetails
import com.revolut.moneytransferservice.periphery.dto.TransferRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.servlet.http.HttpServletResponse

class TransferControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `should get existing transfer`() {
        // GIVEN the ID of an existing transfer from the dummy data set
        val existingTransferId = 1L

        // WHEN we try to get the transfer
        val response = submitGet("api/transfers/$existingTransferId")

        // THEN the response code is OK
        assertThat(response.code).isEqualTo(HttpServletResponse.SC_OK)

        // ... and the expected transfer is returned
        val returnedTransfer = gson.fromJson(response.body, TransferDetails::class.java)
        assertThat(returnedTransfer.id).isEqualTo(existingTransferId)
    }

    @Test
    fun `should get existing transfers`() {
        // GIVEN the IDs of existing transfers loaded from the dummy data set
        val existingTransferIds = listOf(1L, 2L, 3L)

        // WHEN we try to get all the transfers
        val response = submitGet("api/transfers")

        // THEN the response code is OK
        assertThat(response.code).isEqualTo(HttpServletResponse.SC_OK)

        // ... and the expected transfers are returned
        val returnedTransfers = gson.fromJson(response.body, Array<TransferDetails>::class.java)
        assertThat(returnedTransfers).extracting("id").containsAnyElementsOf(existingTransferIds)
    }

    @Test
    fun `should create transfer`() {
        // GIVEN an expected transfer based on the dummy data loaded on app startup
        val expectedTransferId = 3L
        val originAccountId = 2L
        val destinationAccountId = 1L
        val transferAmountMinor = 50L
        val transferToCreate = TransferRequest(originAccountId = 2, destinationAccountId = 1, amountMinor = transferAmountMinor)

        // ... and an existing origin account with a positive balance
        val initialOriginAccountBalance: Long
        with(getAccount(originAccountId)) {
            assertThat(this.id).isEqualTo(originAccountId)
            assertThat(this.balanceMinor).isPositive()
            initialOriginAccountBalance = this.balanceMinor
        }

        // ... along with an existing destination account
        val initialDestinationAccountBalance: Long
        with(getAccount(destinationAccountId)) {
            assertThat(this.id).isEqualTo(destinationAccountId)
            initialDestinationAccountBalance = this.balanceMinor
        }

        // WHEN we try to create the transfer
        val response = submitPost("api/transfers", transferToCreate)

        // THEN the response code is Created
        assertThat(response.code).isEqualTo(HttpServletResponse.SC_CREATED)

        // ... the transfer is created as expected
        val createdTransfer = gson.fromJson(response.body, TransferDetails::class.java)
        with(createdTransfer) {
            assertThat(this.id).isEqualTo(expectedTransferId)
            assertThat(this.originAccountId).isEqualTo(originAccountId)
            assertThat(this.destinationAccountId).isEqualTo(destinationAccountId)
            assertThat(this.amountMinor).isEqualTo(transferAmountMinor)
            assertThat(this.dateCreated).isNotBlank()
        }

        // ... and the origin and destination accounts have been updated correctly
        with(getAccount(originAccountId)) {
            assertThat(this.balanceMinor).isEqualTo(initialOriginAccountBalance - transferAmountMinor)
            assertThat(this.dateUpdated).isGreaterThan(this.dateCreated)
        }
        with(getAccount(destinationAccountId)) {
            assertThat(this.balanceMinor).isEqualTo(initialDestinationAccountBalance + transferAmountMinor)
            assertThat(this.dateUpdated).isGreaterThan(this.dateCreated)
        }
    }

    private fun getAccount(id: Long): AccountDetails {
        val response = submitGet("api/accounts/$id")
        return gson.fromJson(response.body, AccountDetails::class.java)
    }
}