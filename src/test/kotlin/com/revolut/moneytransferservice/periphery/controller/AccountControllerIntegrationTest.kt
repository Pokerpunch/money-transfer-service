package com.revolut.moneytransferservice.periphery.controller

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.periphery.dto.AccountDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.servlet.http.HttpServletResponse

class AccountControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `should get existing account`() {
        // GIVEN the ID of an existing account from the dummy data set
        val existingAccountId = 1L

        // WHEN we try to get the account
        val response = submitGet("api/accounts/$existingAccountId")

        // THEN the response code is OK
        assertThat(response.code).isEqualTo(HttpServletResponse.SC_OK)

        // ... and the expected account is returned
        val returnedAccount = gson.fromJson(response.body, AccountDetails::class.java)
        assertThat(returnedAccount.id).isEqualTo(existingAccountId)
    }

    @Test
    fun `should get existing accounts`() {
        // GIVEN the IDs of existing accounts loaded from the dummy data set
        val existingAccountIds = listOf(1L, 2L, 3L)

        // WHEN we try to get all the accounts
        val response = submitGet("api/accounts")

        // THEN the response code is OK
        assertThat(response.code).isEqualTo(HttpServletResponse.SC_OK)

        // ... and the expected accounts are returned
        val returnedAccounts = gson.fromJson(response.body, Array<AccountDetails>::class.java)
        assertThat(returnedAccounts).extracting("id").containsAnyElementsOf(existingAccountIds)
    }

    @Test
    fun `should create account`() {
        // GIVEN the ID of an account that doesn't exist in the dummy data set
        val expectedNewAccountId = 4L
        val accountToCreate = Account(accountOwnerId = 4).apply { balanceMinor = 500 }

        // WHEN we try to create the account
        val response = submitPost("api/accounts", accountToCreate)

        // THEN the response code is Created
        assertThat(response.code).isEqualTo(HttpServletResponse.SC_CREATED)

        // ... and the account is created as expected
        val createdAccount = gson.fromJson(response.body, AccountDetails::class.java)
        assertThat(createdAccount.id).isEqualTo(expectedNewAccountId)
        assertThat(createdAccount.accountOwnerId).isEqualTo(accountToCreate.accountOwnerId)
        assertThat(createdAccount.balanceMinor).isEqualTo(accountToCreate.balanceMinor)
        assertThat(createdAccount.dateCreated).isNotBlank()
        assertThat(createdAccount.dateUpdated).isNotBlank()
    }
}