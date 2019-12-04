package com.revolut.moneytransferservice.core.dao

import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.NotFoundException
import org.hibernate.SessionFactory
import javax.persistence.LockModeType

class AccountDAO(
    sessionFactory: SessionFactory
) : HibernateDAO<Account, Long>(Account::class.java, sessionFactory) {

    fun findById(id: Long, lockModeType: LockModeType): Account =
        runInTransaction {
            getCurrentSession().find(Account::class.java, id, lockModeType) ?: throw NotFoundException("Account with ID: $id not found")
        }

    fun update(account: Account) {
        runInTransaction {
            getCurrentSession().update(account)
        }
    }
}