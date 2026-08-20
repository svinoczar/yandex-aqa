package com.disk.yandex.util

import com.disk.yandex.model.response.ErrorResponse
import io.restassured.response.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertAll

fun Response.assertApiError(expectedStatusCode: Int): ErrorResponse {
    assertEquals(expectedStatusCode, statusCode)
    val errorResponse = bodyAs<ErrorResponse>()

    assertAll(
        { assertTrue(errorResponse.message.isNotBlank()) },
        { assertTrue(errorResponse.description.isNotBlank()) },
        { assertTrue(errorResponse.error.isNotBlank()) }
    )

    return errorResponse
}
