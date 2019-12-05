package com.revolut.moneytransferservice.core.service

import com.nhaarman.mockito_kotlin.whenever
import com.revolut.moneytransferservice.core.dao.AccountDAO
import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AccountServiceTest {

    @Mock
    private lateinit var accountDAO: AccountDAO

    @InjectMocks
    private lateinit var accountService: AccountService

    @Test
    fun `should return list of existing accounts when trying to get all accounts`() {
        // GIVEN some existing accounts
        val existingaccounts = listOf(
            Account(id = 1).apply { balanceInMinor = 1001 },
            Account(id = 2).apply { balanceInMinor = 2002 }
        )
        whenever(accountDAO.fetchAll()).thenReturn(existingaccounts)

        // WHEN we try to fetch all accounts
        val allaccounts = accountService.getAllAccounts()

        // THEN all existing accounts are fetched
        assertThat(allaccounts).containsExactlyInAnyOrderElementsOf(existingaccounts)
    }

    @Test
    fun `should return empty list when trying to get all accounts when no accounts exist`() {
        // GIVEN no existing accounts
        val existingaccounts = emptyList<Account>()
        whenever(accountDAO.fetchAll()).thenReturn(existingaccounts)

        // WHEN we try to fetch all accounts
        val allaccounts = accountService.getAllAccounts()

        // THEN no accounts are fetched
        assertThat(allaccounts).isEmpty()
    }

    @Test
    fun `should return existing account when trying to find an account by ID`() {
        // GIVEN an account ID
        val accountId = 101L

        // ... that corresponds to an existing account
        val existingaccount = Account(id = 1).apply { balanceInMinor = 1001 }
        whenever(accountDAO.findById(accountId)).thenReturn(existingaccount)

        // WHEN the try to fetch the account
        val returnedAccount = accountService.getAccount(accountId)

        // THEN the expected account is returned
        assertThat(returnedAccount).isEqualTo(existingaccount)
    }

    @Test
    fun `should throw exception when trying to find an account with no matching ID`() {
        // GIVEN an account ID
        val accountId = 101L

        // ... that has no corresponding account
        whenever(accountDAO.findById(accountId)).thenReturn(null)

        // THEN an exception is thrown
        assertThatThrownBy {

            // WHEN we try to get the account
            accountService.getAccount(accountId)

        }.isInstanceOf(NotFoundException::class.java)
    }

    @Test
    fun `should save account`() {
        // GIVEN an unpersisted account
        val unpersistedAccount = Account(id = -1).apply { balanceInMinor = 1001 }

        val persistedAccount = Account(id = 1).apply { balanceInMinor = 1001 }
        whenever(accountDAO.save(unpersistedAccount)).thenReturn(persistedAccount)

        // WHEN we try to save the account
        val createdAccount = accountService.create(unpersistedAccount)

        // THEN the saved account is returned
        assertThat(createdAccount).isEqualTo(persistedAccount)
    }
}