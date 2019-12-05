package com.revolut.moneytransferservice.core.service

import com.revolut.moneytransferservice.core.domain.User
import com.revolut.moneytransferservice.core.exception.NotFoundException
import com.revolut.moneytransferservice.core.dao.UserDAO

class UserService(
    private val userDAO: UserDAO
) {
    fun getAllUsers(): List<User> =
        userDAO.fetchAll()

    fun getUser(id: Long): User =
        userDAO.findById(id) ?: throw NotFoundException("User with ID: $id not found")

    fun create(user: User): User =
        userDAO.save(user)
}