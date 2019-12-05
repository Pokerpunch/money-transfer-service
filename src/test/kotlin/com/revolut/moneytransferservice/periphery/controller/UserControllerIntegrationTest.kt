package com.revolut.moneytransferservice.periphery.controller

import com.revolut.moneytransferservice.core.domain.User
import com.revolut.moneytransferservice.periphery.dto.UserDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.servlet.http.HttpServletResponse.SC_CREATED
import javax.servlet.http.HttpServletResponse.SC_OK

class UserControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `should get existing user`() {
        // GIVEN the ID of an existing user from the dummy data set
        val existingUserId = 1L

        // WHEN we try to get the user
        val response = submitGet("api/users/$existingUserId")

        // THEN the response code is OK
        assertThat(response.code).isEqualTo(SC_OK)

        // ... and the expected user is returned
        val returnedUser = gson.fromJson(response.body, UserDetails::class.java)
        assertThat(returnedUser.id).isEqualTo(existingUserId)
    }

    @Test
    fun `should get existing users`() {
        // GIVEN the IDs of existing users loaded from the dummy data set
        val existingUserIds = listOf(1L, 2L, 3L)

        // WHEN we try to get all the users
        val response = submitGet("api/users")

        // THEN the response code is OK
        assertThat(response.code).isEqualTo(SC_OK)

        // ... and the expected users are returned
        val returnedUsers = gson.fromJson(response.body, Array<UserDetails>::class.java)
        assertThat(returnedUsers).extracting("id").containsAnyElementsOf(existingUserIds)
    }

    @Test
    fun `should create user`() {
        // GIVEN the ID of a user that doesn't exist in the dummy data set
        val expectedNewUserId = 4L
        val userToCreate = User(firstName = "Donald", lastName = "Duck")

        // WHEN we try to create the user
        val response = submitPost("api/users", userToCreate)

        // THEN the response code is Created
        assertThat(response.code).isEqualTo(SC_CREATED)

        // ... and the user is created as expected
        val createdUser = gson.fromJson(response.body, UserDetails::class.java)
        assertThat(createdUser.id).isEqualTo(expectedNewUserId)
        assertThat(createdUser.firstName).isEqualTo(userToCreate.firstName)
        assertThat(createdUser.lastName).isEqualTo(userToCreate.lastName)
        assertThat(createdUser.dateCreated).isNotBlank()
    }
}