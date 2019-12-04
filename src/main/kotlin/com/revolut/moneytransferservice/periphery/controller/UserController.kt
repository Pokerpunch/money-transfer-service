package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.service.UserService
import com.revolut.moneytransferservice.periphery.dto.ResponseMessage
import com.revolut.moneytransferservice.periphery.dto.UserCreationRequest
import com.revolut.moneytransferservice.periphery.dto.UserDetails
import com.revolut.moneytransferservice.periphery.mapper.UserMapper.mapToUnpersistedEntity
import com.revolut.moneytransferservice.periphery.mapper.UserMapper.mapToUserDetails
import com.revolut.moneytransferservice.util.RequestParser.getBody
import com.revolut.moneytransferservice.util.RequestParser.getRequestParameter
import spark.Request
import spark.Response
import spark.Spark.get
import spark.Spark.path
import spark.Spark.post
import javax.servlet.http.HttpServletResponse.SC_CREATED
import javax.ws.rs.core.MediaType.APPLICATION_JSON

class UserController(
    private val userService: UserService,
    private val gson: Gson

) {
    init {
        initialiseRoutes()
    }

    private fun initialiseRoutes() {
        path("api/v1/users") {
            get("/", ::getAllUsers, gson::toJson)
            get("/:id", ::getUser, gson::toJson)
            post("/", APPLICATION_JSON, ::createUser, gson::toJson)
        }
    }

    private fun getAllUsers(request: Request, response: Response): List<UserDetails> =
        also {
            println("Received request to get all users")
        }.run {
            userService.getAllUsers()
        }.map {
            mapToUserDetails(it)
        }.also {
            println("Processed request to get all users with response: $it")
        }

    private fun getUser(request: Request, response: Response): UserDetails {
        val userId: Long = getRequestParameter(request = request, parameterName = "id")

        return also {
            println("Received request to get user with ID: $userId")
        }.run {
            userService.getUser(userId)
        }.let {
            mapToUserDetails(it)
        }.also {
            println("Processed request to get user by ID: [$userId] with response: $it")
        }
    }

    private fun createUser(request: Request, response: Response): ResponseMessage {
        val userCreationRequest: UserCreationRequest = getBody(request, gson)

        return userCreationRequest.also {
            println("Received request to create $it")
        }.let { mapToUnpersistedEntity(it) }
            .run {
            userService.create(this)
        }.let {
            ResponseMessage(success = true)
        }.also {
            response.status(SC_CREATED)
        }.also {
            println("Processed request to create $userCreationRequest with response: $it")
        }
    }
}