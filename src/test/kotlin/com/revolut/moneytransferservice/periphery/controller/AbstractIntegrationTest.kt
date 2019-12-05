package com.revolut.moneytransferservice.periphery.controller

import com.google.gson.Gson
import com.revolut.moneytransferservice.DEFAULT_PORT
import com.revolut.moneytransferservice.startApplication
import org.apache.commons.io.IOUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import spark.Spark
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

abstract class AbstractIntegrationTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setup() {
            startApplication()
            Thread.sleep(1_000)
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            Spark.stop()
        }
    }


    protected val gson = Gson()

    fun submitGet(path: String): SimpleHttpResponse {
        return submitRequest("GET", path)
    }

    fun submitPost(path: String, requestBody: Any): SimpleHttpResponse {
        return submitRequest("POST", path, requestBody)
    }

    private fun submitRequest(method: String, path: String, requestBody: Any? = null): SimpleHttpResponse {
        val url = URL("http://localhost:$DEFAULT_PORT/$path")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

        connection.requestMethod = method
        connection.doOutput = true

        if (method == "POST") {
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(gson.toJson(requestBody))
            writer.flush()
        } else {
            connection.connect()
        }

        return SimpleHttpResponse(
            code = connection.responseCode,
            body = IOUtils.toString(connection.inputStream, Charset.defaultCharset())
        )
    }
}

data class SimpleHttpResponse(
    val code: Int,
    val body: String
)