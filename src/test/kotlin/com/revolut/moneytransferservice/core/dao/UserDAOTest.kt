package com.revolut.moneyuserservice.core.dao

import com.revolut.moneytransferservice.core.dao.UserDAO
import com.revolut.moneytransferservice.core.dao.util.HibernateMockHelper
import com.revolut.moneytransferservice.core.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserDAOTest {

    @Mock
    private lateinit var sessionFactory: SessionFactory

    @InjectMocks
    private lateinit var userDAO: UserDAO

    private lateinit var session: Session
    private lateinit var transaction: Transaction
    
    @Before
    fun setup() {
        session = HibernateMockHelper.mockSession(sessionFactory)
        transaction = HibernateMockHelper.mockTransaction(session)
    }

    @Test
    fun `should find user by ID if it exists`() {
        // GIVEN a user ID
        val userId = 101L

        // ... that corresponds to an existing user
        val existingUser = User(id = userId, firstName = "Amy", lastName = "Adams")
        HibernateMockHelper.mockFindById(session, userId, existingUser)

        // WHEN we try to find the user
        val returnedUser = userDAO.findById(userId)

        // THEN the returned user is the one we expect
        assertThat(returnedUser).isEqualTo(existingUser)
    }

    @Test
    fun `should return null when user cannot be found by ID`() {
        // GIVEN a user ID
        val userId = 101L

        // ... that corresponds to no existing user
        HibernateMockHelper.mockFindById<User>(session, userId, null)

        // WHEN we try to find the user
        val returnedUser = userDAO.findById(userId)

        // THEN the returned user is null
        assertThat(returnedUser).isNull()
    }

    @Test
    fun `should return all existing users`() {
        // GIVEN several existing users
        val existingUsers = listOf(
            User(id = 101, firstName = "Amy", lastName = "Adams"),
            User(id = 102, firstName = "Bilbo", lastName = "Baggins"),
            User(id = 103, firstName = "Charlie", lastName = "Chaplin")
        )
        HibernateMockHelper.mockFindAll(session, existingUsers)

        // WHEN we try to fetch all the users
        val fetchedUsers = userDAO.fetchAll()

        // THEN all the expected users are returned
        assertThat(fetchedUsers).containsExactlyInAnyOrderElementsOf(existingUsers)
    }

    @Test
    fun `should return empty list when there are no users`() {
        // GIVEN no existing users
        val existingUsers = emptyList<User>()
        HibernateMockHelper.mockFindAll(session, existingUsers)

        // WHEN we try to fetch all the users
        val fetchedUsers = userDAO.fetchAll()

        // THEN an empty list is returned
        assertThat(fetchedUsers).isEmpty()
    }

    @Test
    fun `should save new user`() {
        // GIVEN an unpersisted user
        val unpersistedUser = User(id = -1, firstName = "Amy", lastName = "Adams")

        // ... and an expected persisted user
        val persistedUser = User(id = 1, firstName = "Amy", lastName = "Adams")
        HibernateMockHelper.mockSave(session, unpersistedUser, persistedUser, persistedUser.id)

        // WHEN we try to save the user
        val createdUser = userDAO.save(unpersistedUser)

        // THEN the persisted user is returned
        assertThat(createdUser).isEqualTo(persistedUser)
    }
}