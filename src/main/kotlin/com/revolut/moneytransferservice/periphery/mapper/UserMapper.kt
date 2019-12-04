package com.revolut.moneytransferservice.periphery.mapper

import com.revolut.moneytransferservice.core.domain.User
import com.revolut.moneytransferservice.periphery.dto.UserCreationRequest
import com.revolut.moneytransferservice.periphery.dto.UserDetails

object UserMapper {

    fun mapToUserDetails(user: User): UserDetails =
        user.let {
            UserDetails(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                dateCreated = user.dateCreated.toString()
            )
        }

    fun mapToUnpersistedEntity(userCreationRequest: UserCreationRequest): User =
        userCreationRequest.let {
            User(
                firstName = it.firstName,
                lastName = it.lastName
            )
        }
}