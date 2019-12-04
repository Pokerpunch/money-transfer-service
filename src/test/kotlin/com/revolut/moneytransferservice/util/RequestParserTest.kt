package com.revolut.moneytransferservice.util

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.revolut.moneytransferservice.core.exception.BadRequestException
import com.revolut.moneytransferservice.periphery.dto.UserCreationRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import spark.Request

class RequestParserTest {

    @Test
    fun `should get a request parameter of type Long`() {
        // GIVEN
        val request = mock<Request>()
        val parameterName = "dummy_param"
        val parameterValue = "1001"

        whenever(request.params(parameterName)).thenReturn("1001")

        // WHEN
        val returnedParam: Long = RequestParser.getRequestParameter(request, parameterName)

        // THEN
        assertThat(returnedParam).isEqualTo(parameterValue.toLong())
    }

    @Test
    fun `should throw exception when trying to get request parameter that is not of a supported type`() {
        // GIVEN
        val request = mock<Request>()
        val parameterName = "dummy_param"
        val parameterValue = "1001"

        whenever(request.params(parameterName)).thenReturn(parameterValue)

        assertThatThrownBy {

            // WHEN
            RequestParser.getRequestParameter<Int>(request, parameterName)

            // THEN
        }.isInstanceOf(UnsupportedOperationException::class.java)
    }

    @Test
    fun `should throw exception when trying to get non-existing request parameter`() {
        // GIVEN
        val request = mock<Request>()
        val parameterName = "dummy_param"

        whenever(request.params(parameterName)).thenReturn(null)

        assertThatThrownBy {

            // WHEN
            RequestParser.getRequestParameter<Long>(request, parameterName)

            // THEN
        }.isInstanceOf(BadRequestException::class.java)
    }

    @Test
    fun `should get request body as specified type`() {
        // GIVEN
        val gson = Gson()
        val requestObject = UserCreationRequest(firstName = "Donald", lastName = "Duck")
        val requestBody = gson.toJson(requestObject)

        val request = mock<Request>()
        whenever(request.body()).thenReturn(requestBody)

        // WHEN
        val parsedBody: UserCreationRequest = RequestParser.getBody(request, gson)

        // THEN
        assertThat(parsedBody).isEqualTo(requestObject)
    }

    @Test
    fun `should throw exception when request body cannot be parsed as specified type`() {
        // GIVEN
        val gson = Gson()
        val requestObject = UserCreationRequest(firstName = "Donald", lastName = "Duck")
        val requestBody = gson.toJson(requestObject)

        val request = mock<Request>()
        whenever(request.body()).thenReturn(requestBody)

        assertThatThrownBy {

            // WHEN
            RequestParser.getBody<Int>(request, gson)

            // THEN
        }.isInstanceOf(BadRequestException::class.java)
    }
}