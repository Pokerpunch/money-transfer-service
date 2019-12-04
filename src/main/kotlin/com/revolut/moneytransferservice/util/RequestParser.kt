package com.revolut.moneytransferservice.util

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.exception.BadRequestException
import com.revolut.moneytransferservice.core.exception.NotFoundException
import spark.Request

object RequestParser {

    inline fun <reified T> getRequestParameter(request: Request, parameterName: String): T =
        request.params(parameterName).let { param ->
            when {
                param == null -> throw NotFoundException("Request does not contain parameter $parameterName")
                T::class == Long::class -> { param.toLong() as T }
                else -> throw UnsupportedOperationException("Parsing of request parameters of type ${T::class.simpleName} is not supported")
            }
        }

    inline fun <reified T> getBody(request: Request, gson: Gson): T =
        try {
            gson.fromJson(request.body(), T::class.java)
        } catch (e: Exception) {
            e.printStackTrace() // TODO
            throw BadRequestException("Invalid request body")
        }
}