package com.revolut.moneytransferservice.core.service

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.moneytransferservice.core.domain.User
import com.revolut.moneytransferservice.core.exception.NotFoundException
import com.revolut.moneytransferservice.core.dao.UserDAO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserServiceTest {

    @Mock
    private lateinit var userDAO: UserDAO

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `should return list of existing users when trying to get all users`() {
        // GIVEN some existing users
        val existingUsers = listOf(
            User(firstName = "Amy", lastName = "Adams"),
            User(firstName = "Bilbo", lastName = "Baggins")
        )
        whenever(userDAO.fetchAll()).thenReturn(existingUsers)

        // WHEN we try to fetch all existing users
        val allUsers = userService.getAllUsers()

        // THEN all existing users are fetched
        assertThat(allUsers).containsExactlyInAnyOrderElementsOf(existingUsers)
    }

    @Test
    fun `should return empty list when trying to get all users when no users exist`() {
        // GIVEN no existing users
        val existingUsers = emptyList<User>()

        whenever(userDAO.fetchAll()).thenReturn(existingUsers)

        // WHEN we try to fetch all users
        val allUsers = userService.getAllUsers()

        // THEN no users are fetched
        assertThat(allUsers).isEmpty()
    }

    @Test
    fun `should return existing user when trying to find user by ID`() {
        // GIVEN a user ID
        val userId = 101L

        // ... that corresponds to an existing user
        val existingUser = User(firstName = "Amy", lastName = "Adams")
        whenever(userDAO.findById(userId)).thenReturn(existingUser)

        // WHEN we try to fetch the user
        val returnedUser = userService.getUser(userId)

        // THEN the expected user is returned
        assertThat(returnedUser).isEqualTo(existingUser)
    }

    @Test
    fun `should throw exception when trying to find a user with no matching ID`() {
        // GIVEN a user ID
        val userId = 101L

        // ... that has no corresponding user
        whenever(userDAO.findById(userId)).thenReturn(null)

        // THEN an exception is thrown
        assertThatThrownBy {

            // WHEN we try to fetch the user
            userService.getUser(userId)

        }.isInstanceOf(NotFoundException::class.java)
    }

    @Test
    fun `should create user`() {
        // GIVEN an unpersisted user
        val userToSave = User(firstName = "Amy", lastName = "Adams")

        // WHEN we try to create the user
        userService.create(userToSave)

        // THEN the user is saved
        verify(userDAO).save(userToSave)
    }
}