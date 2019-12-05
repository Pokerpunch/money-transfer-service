package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.service.AccountService
import com.revolut.moneytransferservice.periphery.dto.AccountCreationRequest
import com.revolut.moneytransferservice.periphery.dto.AccountDetails
import com.revolut.moneytransferservice.periphery.mapper.AccountMapper
import com.revolut.moneytransferservice.periphery.mapper.AccountMapper.mapToAccountDetails
import com.revolut.moneytransferservice.util.RequestParser
import spark.Request
import spark.Response
import spark.Spark
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.MediaType

class AccountController(
    private val accountService: AccountService,
    private val gson: Gson
) {
    init {
        initialiseRoutes()
    }

    private fun initialiseRoutes() {
        Spark.path("api/accounts") {
            Spark.get("", ::getAllAccounts, gson::toJson)
            Spark.get("/:id", ::getAccount, gson::toJson)
            Spark.post("", MediaType.APPLICATION_JSON, ::createAccount, gson::toJson)
        }
    }

    private fun getAllAccounts(request: Request, response: Response): List<AccountDetails> =
        also {
            println("Received request to get all accounts")
        }.run {
            accountService.getAllAccounts()
        }.map {
            mapToAccountDetails(it)
        }.also {
            println("Returning response: $it")
        }

    private fun getAccount(request: Request, response: Response): AccountDetails {
        val accountId: Long = RequestParser.getRequestParameter(request = request, parameterName = "id")

        return also {
            println("Received request to get account with ID: $accountId")
        }.run {
            accountService.getAccount(accountId)
        }.let {
            mapToAccountDetails(it)
        }.also {
            println("Returning response: $it")
        }
    }

    private fun createAccount(request: Request, response: Response): AccountDetails {
        val accountCreationRequest: AccountCreationRequest = RequestParser.getBody(request, gson)

        return accountCreationRequest.also {
            println("Received: $it")
        }.let {
            AccountMapper.mapToAccountEntity(it)
        }.run {
            accountService.create(this)
        }.let {
            mapToAccountDetails(it)
        }.also {
            response.status(HttpServletResponse.SC_CREATED)
        }.also {
            println("Returning response: $it")
        }
    }
}