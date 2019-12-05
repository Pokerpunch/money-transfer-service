package com.revolut.moneytransferservice.periphery.mapper

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.periphery.dto.AccountCreationRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AccountMapperTest {

    @Test
    fun `should map to account entity`() {
        // GIVEN
        val accountCreationRequest = AccountCreationRequest(accountOwnerId = 101, initialBalanceInMinor = 200)

        // WHEN
        val accountEntity = AccountMapper.mapToAccountEntity(accountCreationRequest)

        // THEN
        assertThat(accountEntity.accountOwnerId).isEqualTo(accountCreationRequest.accountOwnerId)
        assertThat(accountEntity.balanceInMinor).isEqualTo(accountCreationRequest.initialBalanceInMinor)
    }

    @Test
    fun `should map to account details`() {
        // GIVEN
        val accountEntity = Account(id = 101, accountOwnerId = 102).apply { balanceInMinor = 103 }

        // WHEN
        val accountDetails = AccountMapper.mapToAccountDetails(accountEntity)

        // THEN
        with(accountDetails) {
            assertThat(id).isEqualTo(accountEntity.id)
            assertThat(accountOwnerId).isEqualTo(accountEntity.accountOwnerId)
            assertThat(dateCreated).isEqualTo(accountEntity.dateCreated.toString())
            assertThat(dateUpdated).isEqualTo(accountEntity.dateUpdated.toString())
        }
    }
}