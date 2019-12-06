package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.exception.BadRequestException
import com.revolut.moneytransferservice.core.exception.NotFoundException
import com.revolut.moneytransferservice.core.exception.TransferNotPossibleException
import com.revolut.moneytransferservice.periphery.dto.ResponseMessage
import org.apache.log4j.Logger
import spark.Request
import spark.Response
import spark.Spark.exception
import spark.Spark.notFound
import javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST
import javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND
import javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED
import javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED

class ErrorHandler(
    private val gson: Gson
) {
    companion object {
        val logger: Logger = Logger.getLogger(ErrorHandler::class.java)
    }

    init {
        initializeExceptionMappings()
    }

    private fun initializeExceptionMappings() {
        notFound(::handleNotFoundException)
        exception(NotFoundException::class.java, ::handleNotFoundException)
        exception(BadRequestException::class.java, ::handleBadRequestException)
        exception(TransferNotPossibleException::class.java, ::handleTransferNotPossibleException)
        exception(UnsupportedOperationException::class.java, ::handleUnsupportedOperationException)
        exception(Exception::class.java, ::handleAnyException)
    }

    private fun handleNotFoundException(exception: NotFoundException, request: Request, response: Response) {
        logger.error("Caught exception: ", exception)

        writeErrorResponse(exception.message, response).also {
            response.status(SC_NOT_FOUND)
        }
    }

    private fun handleNotFoundException(request: Request, response: Response) =
        also {
            logger.error("Caught a Not Found exception")
        }.let {
            writeErrorResponse("Not Found", response).also {
                response.status(SC_NOT_FOUND)
            }
        }

    private fun handleBadRequestException(exception: BadRequestException, request: Request, response: Response) {
        logger.error("Caught exception: ", exception)

        writeErrorResponse(exception.message, response).also {
            response.status(SC_BAD_REQUEST)
        }
    }

    private fun handleTransferNotPossibleException(exception: TransferNotPossibleException, request: Request, response: Response) {
        logger.error("Caught exception: ", exception)

        writeErrorResponse(exception.message, response).also {
            response.status(SC_PRECONDITION_FAILED)
        }
    }

    private fun handleUnsupportedOperationException(exception: UnsupportedOperationException, request: Request, response: Response) {
        logger.error("Caught exception: ", exception)

        writeErrorResponse(exception.message, response).also {
            response.status(SC_NOT_IMPLEMENTED)
        }
    }

    private fun handleAnyException(exception: Exception, request: Request, response: Response) {
        logger.error("Caught exception: ", exception)

        writeErrorResponse(exception.message, response).also {
            response.status(SC_INTERNAL_SERVER_ERROR)
        }
    }

    private fun writeErrorResponse(errorMessage: String?, response: Response) =
        ResponseMessage(
            success = false,
            errorMessage = errorMessage
        ).let {
            gson.toJson(it)
        }.also {
            response.body(it)
        }.also {
            logger.debug("Returning $it")
        }
}