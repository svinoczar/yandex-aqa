package com.disk.yandex.util

import com.disk.yandex.client.DiskClient
import com.disk.yandex.model.response.LinkResponse
import com.disk.yandex.model.response.OperationResponse
import com.disk.yandex.model.response.OperationStatus
import io.restassured.response.Response

class OperationWaiter(
    private val diskClient: DiskClient,
    private val maxAttempts: Int = 60,
    private val pollIntervalMs: Long = 250
) {
    fun awaitCompletion(
        initialResponse: Response,
        vararg synchronousStatusCodes: Int
    ) {
        if (initialResponse.statusCode in synchronousStatusCodes) {
            return
        }

        if (initialResponse.statusCode != 202) {
            throw AssertionError(
                "Unexpected operation response: status=${initialResponse.statusCode}, " +
                    "body=${initialResponse.body.asString()}"
            )
        }

        val operationLink = initialResponse.bodyAs<LinkResponse>()
        val operationId = operationLink.operationId
            ?: operationLink.href
                .substringAfterLast('/')
                .substringBefore('?')

        if (operationId.isBlank()) {
            throw AssertionError(
                "Operation link does not contain an id: ${operationLink.href}"
            )
        }

        for (attempt in 1..maxAttempts) {
            val statusResponse = diskClient.getAsyncOperationStatus(operationId)

            if (statusResponse.statusCode != 200) {
                throw AssertionError(
                    "Failed to get operation status: status=${statusResponse.statusCode}, " +
                        "body=${statusResponse.body.asString()}"
                )
            }

            when (statusResponse.bodyAs<OperationResponse>().status) {
                OperationStatus.SUCCESS -> return
                OperationStatus.FAILED -> throw AssertionError(
                    "Operation $operationId failed: ${statusResponse.body.asString()}"
                )

                OperationStatus.IN_PROGRESS -> {
                    if (attempt < maxAttempts) {
                        waitBeforeNextAttempt()
                    }
                }
            }
        }

        throw AssertionError(
            "Operation $operationId did not complete after $maxAttempts attempts"
        )
    }

    private fun waitBeforeNextAttempt() {
        try {
            Thread.sleep(pollIntervalMs)
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            throw AssertionError("Operation polling was interrupted", exception)
        }
    }
}
