package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.service.UserService
import com.revolut.moneytransferservice.periphery.dto.UserCreationRequest
import com.revolut.moneytransferservice.periphery.dto.UserDetails
import com.revolut.moneytransferservice.periphery.mapper.UserMapper.mapToUnpersistedUserEntity
import com.revolut.moneytransferservice.periphery.mapper.UserMapper.mapToUserDetails
import com.revolut.moneytransferservice.util.RequestParser.getBody
import com.revolut.moneytransferservice.util.RequestParser.getRequestParameter
import org.apache.log4j.Logger
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
    companion object {
        val logger: Logger = Logger.getLogger(UserController::class.java)
    }

    init {
        initialiseRoutes()
    }

    private fun initialiseRoutes() {
        path("api/users") {
            get("", ::getAllUsers, gson::toJson)
            get("/:id", ::getUser, gson::toJson)
            post("", APPLICATION_JSON, ::createUser, gson::toJson)
        }
    }

    private fun getAllUsers(request: Request, response: Response): List<UserDetails> =
        also {
            logger.debug("Received request to get all users")
        }.run {
            userService.getAllUsers()
        }.map {
            mapToUserDetails(it)
        }.also {
            logger.debug("Returning response: $it")
        }

    private fun getUser(request: Request, response: Response): UserDetails {
        val userId: Long = getRequestParameter(request = request, parameterName = "id")

        return also {
            logger.debug("Received request to get user with ID: $userId")
        }.run {
            userService.getUser(userId)
        }.let {
            mapToUserDetails(it)
        }.also {
            logger.debug("Returning response: $it")
        }
    }

    private fun createUser(request: Request, response: Response): UserDetails {
        val userCreationRequest: UserCreationRequest = getBody(request, gson)

        return userCreationRequest.also {
            logger.debug("Received: $it")
        }.let {
            mapToUnpersistedUserEntity(it)
        }.run {
            userService.create(this)
        }.let {
            mapToUserDetails(it)
        }.also {
            response.status(SC_CREATED)
        }.also {
            logger.debug("Returning response: $it")
        }
    }
}