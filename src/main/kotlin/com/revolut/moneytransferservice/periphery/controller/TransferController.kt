package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.core.service.TransferService
import com.revolut.moneytransferservice.periphery.dto.ResponseMessage
import com.revolut.moneytransferservice.periphery.dto.TransferDetails
import com.revolut.moneytransferservice.periphery.mapper.TransferMapper
import com.revolut.moneytransferservice.util.RequestParser
import spark.Request
import spark.Response
import spark.Spark
import javax.ws.rs.core.MediaType

class TransferController(
    private val transferService: TransferService,
    private val gson: Gson
) {
    init {
        initialiseRoutes()
    }

    private fun initialiseRoutes() {
        Spark.path("api/v1/transfers") {
            Spark.get("/", ::getAllTransfers, gson::toJson)
            Spark.get("/:id", ::getTransfer, gson::toJson)
            Spark.post("/", MediaType.APPLICATION_JSON, ::createTransfer, gson::toJson)
        }
    }

    private fun getAllTransfers(request: Request, response: Response): List<TransferDetails> =
        also {
            println("Received request to get all transfers")
        }.run {
            transferService.getAllTransfers()
        }.map {
            TransferMapper.mapToTransferDetails(it)
        }.also {
            println("Processed request to get all transfers with response: $it")
        }

    private fun getTransfer(request: Request, response: Response): TransferDetails {
        val transferId: Long = RequestParser.getRequestParameter(request = request, parameterName = "id")

        return also {
            println("Received request to get transfer with ID: $transferId")
        }.run {
            transferService.getTransfer(transferId)
        }.let {
            TransferMapper.mapToTransferDetails(it)
        }.also {
            println("Processed request to get transfer by ID: [$transferId] with response: $it")
        }
    }

    private fun createTransfer(request: Request, response: Response): ResponseMessage {
        TODO()
    }
}