package com.revolut.moneytransferservice.periphery.mapper

import com.revolut.moneytransferservice.core.domain.User
import com.revolut.moneytransferservice.periphery.dto.UserCreationRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UserMapperTest {

    @Test
    fun `should map to user details`() {
        // GIVEN
        val userEntity = User(id = 101, firstName = "Amy", lastName = "Adams")

        // WHEN
        val userDetails = UserMapper.mapToUserDetails(userEntity)

        // THEN
        with(userDetails) {
            assertThat(id).isEqualTo(userEntity.id)
            assertThat(firstName).isEqualTo(userEntity.firstName)
            assertThat(lastName).isEqualTo(userEntity.lastName)
            assertThat(dateCreated).isEqualTo(userEntity.dateCreated.toString())
        }
    }

    @Test
    fun `should map to user entity`() {
        // GIVEN
        val userDetails = UserCreationRequest(
            firstName = "Amy",
            lastName = "Adamms"
        )

        // WHEN
        val userEntity = UserMapper.mapToUnpersistedUserEntity(userDetails)

        // THEN
        with(userEntity) {
            assertThat(firstName).isEqualTo(userDetails.firstName)
            assertThat(lastName).isEqualTo(userDetails.lastName)
        }
    }
}