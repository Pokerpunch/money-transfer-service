package com.revolut.moneytransferservice.core.dao

import com.revolut.moneytransferservice.core.domain.Transfer
import com.revolut.moneytransferservice.core.service.validator.TransferValidator
import org.hibernate.SessionFactory
import javax.persistence.LockModeType.PESSIMISTIC_WRITE

class TransferDAO(
    sessionFactory: SessionFactory,
    private val accountDAO: AccountDAO
) : HibernateDAO<Transfer, Long>(Transfer::class.java, sessionFactory) {

    fun updateAccountsAndSaveTransfer(transfer: Transfer) {
        runInTransaction {

            println("DEBUG - Fetching originAccount")
            val originAccount = accountDAO.findById(transfer.originAccountId, PESSIMISTIC_WRITE)
            println("DEBUG - Fetched originAccount: $originAccount")

            println("DEBUG - validateBeforeFinishingTransfer")
            TransferValidator.validateBeforeFinishingTransfer(originAccount, transfer.amountInMinor)

            println("DEBUG - Fetching destination Account")
            val destinationAccount = accountDAO.findById(transfer.destinationAccountId, PESSIMISTIC_WRITE)
            println("DEBUG - Fetched destination Account")

            originAccount.balanceInMinor -= transfer.amountInMinor
            destinationAccount.balanceInMinor += transfer.amountInMinor

            accountDAO.update(originAccount)
            accountDAO.update(destinationAccount)

            save(transfer)
        }
    }
}