package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.service.TransferService
import com.revolut.moneytransferservice.periphery.dto.TransferDetails
import com.revolut.moneytransferservice.periphery.dto.TransferRequest
import com.revolut.moneytransferservice.periphery.mapper.TransferMapper.mapToTransferDetails
import com.revolut.moneytransferservice.util.RequestParser
import spark.Request
import spark.Response
import spark.Spark
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.MediaType

class TransferController(
    private val transferService: TransferService,
    private val gson: Gson
) {
    init {
        initialiseRoutes()
    }

    private fun initialiseRoutes() {
        Spark.path("api/transfers") {
            Spark.get("", ::getAllTransfers, gson::toJson)
            Spark.get("/:id", ::getTransfer, gson::toJson)
            Spark.post("", MediaType.APPLICATION_JSON, ::createTransfer, gson::toJson)
        }
    }

    private fun getAllTransfers(request: Request, response: Response): List<TransferDetails> =
        also {
            println("Received request to get all transfers")
        }.run {
            transferService.getAllTransfers()
        }.map {
            mapToTransferDetails(it)
        }.also {
            println("Returning response: $it")
        }

    private fun getTransfer(request: Request, response: Response): TransferDetails {
        val transferId: Long = RequestParser.getRequestParameter(request = request, parameterName = "id")

        return also {
            println("Received request to get transfer with ID: $transferId")
        }.run {
            transferService.getTransfer(transferId)
        }.let {
            mapToTransferDetails(it)
        }.also {
            println("Returning response: $it")
        }
    }

    private fun createTransfer(request: Request, response: Response): TransferDetails {
        val transferRequest: TransferRequest = RequestParser.getBody(request, gson)

        return transferRequest.also {
            println("Received: $it")
        }.run {
            transferService.executeTransfer(originAccountId, destinationAccountId, amountMinor)
        }.let {
            mapToTransferDetails(it)
        }.also {
            response.status(HttpServletResponse.SC_CREATED)
        }.also {
            println("Returning response: $it")
        }
    }
}