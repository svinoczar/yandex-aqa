package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import com.disk.yandex.model.response.LinkResponse
import com.disk.yandex.model.response.OperationResponse
import com.disk.yandex.model.response.OperationStatus
import com.disk.yandex.model.response.ResourceResponse
import com.disk.yandex.util.assertApiError
import com.disk.yandex.util.bodyAs
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.UUID

@Epic("Yandex Disk API")
@Feature("Asynchronous operations")
@DisplayName("Disk - asynchronous operations")
class OperationTest : TestBase() {

    @Test
    @Tag("async")
    @DisplayName("Асинхронное копирование директории")
    fun copyFolderAsynchronously() {
        val sourcePath = "$uniqPath/source"
        val copyPath = "$uniqPath/copy"

        assertEquals(201, diskClient.createFolder(uniqPath).statusCode)
        assertEquals(201, diskClient.createFolder(sourcePath).statusCode)

        val copyResponse = diskClient.copyResource(
            from = sourcePath,
            path = copyPath,
            forceAsync = true
        )
        assertEquals(202, copyResponse.statusCode)
        val operationLink = copyResponse.bodyAs<LinkResponse>()
        val operationId = operationLink.href
            .substringAfterLast('/')
            .substringBefore('?')

        assertAll(
            { assertTrue(operationLink.href.isNotBlank()) },
            { assertEquals("GET", operationLink.method) },
            { assertFalse(operationLink.templated ?: true) },
            { assertTrue(operationId.isNotBlank()) }
        )

        val statusResponse = diskClient.getAsyncOperationStatus(operationId)
        assertEquals(200, statusResponse.statusCode)
        val operation = statusResponse.bodyAs<OperationResponse>()
        assertTrue(
            operation.status in setOf(
                OperationStatus.IN_PROGRESS,
                OperationStatus.SUCCESS
            )
        )

        operationWaiter.awaitCompletion(copyResponse)

        val copiedResourceResponse = diskClient.getResource(copyPath)
        assertEquals(200, copiedResourceResponse.statusCode)
        val copiedResource = copiedResourceResponse.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals("disk:$copyPath", copiedResource.path) },
            { assertEquals("copy", copiedResource.name) },
            { assertEquals("dir", copiedResource.type) }
        )
    }

    @Test
    @Tag("negative")
    @DisplayName("Получение статуса несуществующей операции")
    fun getMissingOperationStatus() {
        val response = diskClient.getAsyncOperationStatus(
            UUID.randomUUID().toString()
        )

        val error = response.assertApiError(404)

        assertEquals("DiskOperationNotFoundError", error.error)
    }
}
