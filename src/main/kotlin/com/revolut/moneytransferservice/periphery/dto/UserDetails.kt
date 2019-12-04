package com.revolut.moneytransferservice.periphery.dto

data class UserDetails(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val dateCreated: String
)