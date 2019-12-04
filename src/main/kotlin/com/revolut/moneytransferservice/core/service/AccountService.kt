package com.revolut.moneytransferservice.core.service

import com.revolut.moneytransferservice.core.dao.AccountDAO
import com.revolut.moneytransferservice.core.domain.Account
import com.revolut.moneytransferservice.core.exception.NotFoundException

class AccountService(
    private val accountDAO: AccountDAO
) {
    fun getAllAccounts(): List<Account> =
        accountDAO.fetchAll()

    fun getAccount(id: Long): Account =
        accountDAO.findById(id) ?: throw NotFoundException("Account with ID: $id not found")

    fun create(account: Account) {
        accountDAO.save(account)
    }
}