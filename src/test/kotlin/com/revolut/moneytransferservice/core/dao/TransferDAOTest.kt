package com.revolut.moneytransferservice.core.dao

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.moneytransferservice.core.dao.util.HibernateMockHelper
import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.domain.Transfer
import com.revolut.moneytransferservice.core.exception.NotFoundException
import com.revolut.moneytransferservice.core.exception.TransferNotPossibleException
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
class TransferDAOTest {

    @Mock
    private lateinit var sessionFactory: SessionFactory

    @Mock
    private lateinit var accountDAO: AccountDAO

    @InjectMocks
    private lateinit var transferDAO: TransferDAO

    private lateinit var session: Session
    private lateinit var transaction: Transaction

    @Before
    fun setup() {
        session = HibernateMockHelper.mockSession(sessionFactory)
        transaction = HibernateMockHelper.mockTransaction(session)
    }

    @Test
    fun `should find transfer by ID if it exists`() {
        // GIVEN a transfer ID
        val transferId = 101L

        // ... that corresponds to an existing transfer
        val existingTransfer = Transfer(id = transferId, originAccountId = 102, destinationAccountId = 103, amountMinor = 104)
        HibernateMockHelper.mockFindById(session, transferId, existingTransfer)

        // WHEN we try to find the transfer
        val returnedTransfer = transferDAO.findById(transferId)

        // THEN the returned transfer is the one we expect
        assertThat(returnedTransfer).isEqualTo(existingTransfer)
    }

    @Test
    fun `should return null when transfer cannot be found by ID`() {
        // GIVEN a transfer ID
        val transferId = 101L

        // ... that corresponds to no existing transfer
        HibernateMockHelper.mockFindById<Transfer>(session, transferId, null)

        // WHEN we try to find the transfer
        val returnedTransfer = transferDAO.findById(transferId)

        // THEN the returned transfer is null
        assertThat(returnedTransfer).isNull()
    }

    @Test
    fun `should return all existing transfers`() {
        // GIVEN several existing transfers
        val existingTransfers = listOf(
            Transfer(id = 101, originAccountId = 102, destinationAccountId = 103, amountMinor = 104),
            Transfer(id = 201, originAccountId = 202, destinationAccountId = 203, amountMinor = 204),
            Transfer(id = 301, originAccountId = 302, destinationAccountId = 303, amountMinor = 304)
        )
        HibernateMockHelper.mockFindAll(session, existingTransfers)

        // WHEN we try to fetch all the transfers
        val fetchedTransfers = transferDAO.fetchAll()

        // THEN all the expected transfers are returned
        assertThat(fetchedTransfers).containsExactlyInAnyOrderElementsOf(existingTransfers)
    }

    @Test
    fun `should return empty list when there are no transfers`() {
        // GIVEN no existing transfers
        val existingTransfers = emptyList<Transfer>()
        HibernateMockHelper.mockFindAll(session, existingTransfers)

        // WHEN we try to fetch all the transfers
        val fetchedTransfers = transferDAO.fetchAll()

        // THEN an empty list is returned
        assertThat(fetchedTransfers).isEmpty()
    }

    @Test
    fun `should throw exception if origin account does not exist`() {
        // GIVEN a valid unpersisted transfer
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmount = 500L
        val transfer = Transfer(
            originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmount
        )

        // ... but no origin account
        val notFoundException = NotFoundException("Origin account not found")
        whenever(accountDAO.findById(originAccountId, PESSIMISTIC_WRITE)).thenThrow(notFoundException)

        // THEN an exception is thrown
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {

            // WHEN a transfer is made
            transferDAO.updateAccountsAndSaveTransfer(transfer)
        }
        // ... and the transaction is rolled back
        verify(transaction).rollback()
    }

    @Test
    fun `should throw exception if destination account does not exist`() {
        // GIVEN a valid unpersisted transfer
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmount = 500L
        val transfer = Transfer(
            originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmount
        )

        // ... and an origin account with sufficient balance
        val initialBalanceOriginAccount = transferAmount + 1
        val originAccount = Account(id = originAccountId).apply { balanceMinor = initialBalanceOriginAccount }
        whenever(accountDAO.findById(originAccountId, PESSIMISTIC_WRITE)).thenReturn(originAccount)

        // ... but no destination account
        val notFoundException = NotFoundException("Destination account not found")
        whenever(accountDAO.findById(destinationAccountId, PESSIMISTIC_WRITE)).thenThrow(notFoundException)

        // THEN an exception is thrown
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {

            // WHEN a transfer is made
            transferDAO.updateAccountsAndSaveTransfer(transfer)
        }
        // ... and the transaction is rolled back
        verify(transaction).rollback()
    }

    @Test
    fun `should throw exception if validation fails due to insufficient balance`() {
        // GIVEN a valid unpersisted transfer
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmount = 500L
        val transfer = Transfer(
            originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmount
        )
        // ... and an origin account with insufficient balance
        val initialBalanceOriginAccount = transferAmount - 1
        val originAccount = Account(id = originAccountId).apply { balanceMinor = initialBalanceOriginAccount }
        whenever(accountDAO.findById(originAccountId, PESSIMISTIC_WRITE)).thenReturn(originAccount)

        // THEN an exception is thrown
        assertThatExceptionOfType(TransferNotPossibleException::class.java).isThrownBy {

            // WHEN a transfer is made
            transferDAO.updateAccountsAndSaveTransfer(transfer)
        }
        // ... and the transaction is rolled back
        verify(transaction).rollback()
    }

    @Test
    fun `should update accounts and save transfer`() {
        // GIVEN a valid unpersisted transfer
        val originAccountId = 101L
        val destinationAccountId = 102L
        val transferAmount = 500L
        val unpersistedTransfer = Transfer(
            originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmount
        )

        // ... an origin account with sufficient balance
        val initialBalanceOriginAccount = transferAmount + 1
        val originAccount = Account(id = originAccountId).apply { balanceMinor = initialBalanceOriginAccount }
        whenever(accountDAO.findById(originAccountId, PESSIMISTIC_WRITE)).thenReturn(originAccount)

        // ... an existing destination account
        val initialBalanceDestinationAccount = 500L
        val destinationAccount = Account(id = destinationAccountId).apply { balanceMinor = initialBalanceDestinationAccount }
        whenever(accountDAO.findById(destinationAccountId, PESSIMISTIC_WRITE)).thenReturn(destinationAccount)

        // ... and the expected persisted transfer TODO
        val persistedTransfer = Transfer(
            id = 1001, originAccountId = originAccountId, destinationAccountId = destinationAccountId, amountMinor = transferAmount
        )
        HibernateMockHelper.mockSave(session, unpersistedTransfer, persistedTransfer, persistedTransfer.id)

        // WHEN a transfer is made
        val createdTransfer = transferDAO.updateAccountsAndSaveTransfer(unpersistedTransfer)

        // THEN both accounts are updated
        verify(accountDAO).update(originAccount)
        verify(accountDAO).update(destinationAccount)

        // ... the origin account had the transfer amount deducted
        assertThat(originAccount.balanceMinor).isEqualTo(initialBalanceOriginAccount - transferAmount)

        // ... the destination account had the transfer amount added
        assertThat(originAccount.balanceMinor).isEqualTo(initialBalanceOriginAccount - transferAmount)

        // ... and the persisted transfer record is returned
        assertThat(createdTransfer).isEqualTo(persistedTransfer)
    }
}