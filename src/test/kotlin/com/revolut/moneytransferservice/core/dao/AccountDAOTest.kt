package com.revolut.moneytransferservice.core.dao

import com.nhaarman.mockito_kotlin.verify
import com.revolut.moneytransferservice.core.dao.util.HibernateMockHelper
import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import javax.persistence.LockModeType.PESSIMISTIC_WRITE

@RunWith(MockitoJUnitRunner::class)
class AccountDAOTest {

    @Mock
    private lateinit var sessionFactory: SessionFactory

    @InjectMocks
    private lateinit var accountDAO: AccountDAO

    private lateinit var session: Session
    private lateinit var transaction: Transaction

    @Before
    fun setup() {
        session = HibernateMockHelper.mockSession(sessionFactory)
        transaction = HibernateMockHelper.mockTransaction(session)
    }

    @Test
    fun `should find account by ID if it exists`() {
        // GIVEN an account ID
        val accountId = 101L

        // ... that corresponds to an existing account
        val existingAccount = Account(id = accountId)
        HibernateMockHelper.mockFindById(session, accountId, existingAccount)

        // WHEN we try to find the account
        val returnedAccount = accountDAO.findById(accountId)

        // THEN the returned account is the one we expect
        assertThat(returnedAccount).isEqualTo(existingAccount)
    }

    @Test
    fun `should return null when account cannot be found by ID`() {
        // GIVEN an account ID
        val accountId = 101L

        // ... that corresponds to no existing account
        HibernateMockHelper.mockFindById<Account>(session, accountId, null)

        // WHEN we try to find the account
        val returnedAccount = accountDAO.findById(accountId)

        // THEN the returned account is null
        assertThat(returnedAccount).isNull()
    }

    @Test
    fun `should return all existing accounts`() {
        // GIVEN several existing accounts
        val existingAccounts = listOf(
            Account(id = 101),
            Account(id = 102),
            Account(id = 103)
        )
        HibernateMockHelper.mockFindAll(session, existingAccounts)

        // WHEN we try to fetch all the accounts
        val fetchedAccounts = accountDAO.fetchAll()

        // THEN all the expected accounts are returned
        assertThat(fetchedAccounts).containsExactlyInAnyOrderElementsOf(existingAccounts)
    }

    @Test
    fun `should return empty list when there are no accounts`() {
        // GIVEN no existing accounts
        val existingAccounts = emptyList<Account>()
        HibernateMockHelper.mockFindAll(session, existingAccounts)

        // WHEN we try to fetch all the accounts
        val fetchedAccounts = accountDAO.fetchAll()

        // THEN an empty list is returned
        assertThat(fetchedAccounts).isEmpty()
    }

    @Test
    fun `should save new account`() {
        // GIVEN an unpersisted account
        val unpersistedAccount = Account(id = -1).apply { balanceInMinor = 200 }

        // ... and an expected persisted account
        val persistedAccount = Account(id = 1).apply { balanceInMinor = 200 }
        HibernateMockHelper.mockSave(session, unpersistedAccount, persistedAccount, persistedAccount.id)

        // WHEN we try to save the account
        val createdAccount = accountDAO.save(unpersistedAccount)

        // THEN the persisted account is returned
        assertThat(createdAccount).isEqualTo(persistedAccount)
    }

    @Test
    fun `should update existing account`() {
        // GIVEN an existing account we want to update
        val existingAccount = Account(id = 101)

        // WHEN we try to update the account
        accountDAO.update(existingAccount)

        // THEN the account is updated
        verify(session).update(existingAccount)
    }

    @Test
    fun `should find existing account with specified lock mode`() {
        // GIVEN an existing account we want to fetch with a pessimistic lock
        val existingAccountId = 101L
        val lockModeType = PESSIMISTIC_WRITE

        val existingAccount = Account(id = existingAccountId)
        HibernateMockHelper.mockFindByIdWithLock(session, existingAccountId, lockModeType, existingAccount)

        // WHEN we try to find the account
        val returnedAccount = accountDAO.findById(existingAccountId, lockModeType)

        // THEN the returned account is the one we expect
        assertThat(returnedAccount).isEqualTo(existingAccount)
    }

    @Test
    fun `should throw exception when trying to find non-existent account with specified lock mode`() {
        // GIVEN an account ID
        val accountId = 101L
        val lockModeType = PESSIMISTIC_WRITE

        // ... that corresponds to no existing account
        HibernateMockHelper.mockFindByIdWithLock<Account>(session, accountId, lockModeType, null)

        // THEN an exception is thrown
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {

            // WHEN we try to find the account
            accountDAO.findById(accountId, lockModeType)
        }

        // ... and the transaction is rolled back
        verify(transaction).rollback()
    }
}