package com.revolut.moneytransferservice

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.dao.AccountDAO
import com.revolut.moneytransferservice.core.dao.TransferDAO
import com.revolut.moneytransferservice.core.dao.UserDAO
import com.revolut.moneytransferservice.core.service.AccountService
import com.revolut.moneytransferservice.core.service.TransferService
import com.revolut.moneytransferservice.core.service.UserService
import com.revolut.moneytransferservice.periphery.controller.AccountController
import com.revolut.moneytransferservice.periphery.controller.ErrorHandler
import com.revolut.moneytransferservice.periphery.controller.TransferController
import com.revolut.moneytransferservice.periphery.controller.UserController
import org.apache.log4j.Logger
import org.hibernate.cfg.Configuration
import spark.Spark.afterAfter
import spark.Spark.port
import javax.ws.rs.core.MediaType.APPLICATION_JSON

class Application

const val DEFAULT_PORT = 8080

val logger: Logger = Logger.getLogger(Application::class.java)

fun main (args: Array<String>) {
    startApplication()
}

fun startApplication(port: Int = DEFAULT_PORT) {
    port(port)
    setup()
    registerResponseFilter()

    logger.info("Application started...")
}

private fun setup() {
    val gson = Gson()
    val sessionFactory = Configuration().configure("hibernate.cfg.xml").buildSessionFactory()

    val userDAO = UserDAO(sessionFactory)
    val accountDAO = AccountDAO(sessionFactory)
    val transferDAO = TransferDAO(sessionFactory, accountDAO)

    val userService = UserService(userDAO)
    val accountService = AccountService(accountDAO)
    val transferService = TransferService(transferDAO)

    UserController(userService, gson)
    AccountController(accountService, gson)
    TransferController(transferService, gson)

    ErrorHandler(gson)
}

private fun registerResponseFilter() {
    afterAfter { _, res ->
        res.type(APPLICATION_JSON)
    }
}
