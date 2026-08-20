package com.disk.yandex.tests

import com.disk.yandex.util.bodyAs
import com.disk.yandex.configuration.TestBase
import com.disk.yandex.model.response.ResourceResponse
import com.disk.yandex.util.assertApiError
import io.qameta.allure.Epic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@Epic("Disk API")
@DisplayName("Disk - additional resource endpoints")
class ResourceAdditionalTest : TestBase() {
    // Positive cases:
    @Test
    @DisplayName("(PUT + POST + GET) Создание, копирование и чтение директории")
    fun createAndCopyFolder() {
        val sourcePath = "${uniqPath}/source"
        val copyPath = "${uniqPath}/copy"

        val createRootResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createRootResponse.statusCode)

        val createSourceResponse = diskClient.createFolder(sourcePath)
        assertEquals(201, createSourceResponse.statusCode)

        val copyResponse = diskClient.copyResource(
            from = sourcePath,
            path = copyPath
        )
        operationWaiter.awaitCompletion(copyResponse, 201)

        val sourceResponse = diskClient.getResource(sourcePath)
        val copyResourceResponse = diskClient.getResource(copyPath)
        assertEquals(200, sourceResponse.statusCode)
        assertEquals(200, copyResourceResponse.statusCode)
        val sourceResource = sourceResponse.bodyAs<ResourceResponse>()
        val copiedResource = copyResourceResponse.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals("disk:$sourcePath", sourceResource.path) },
            { assertEquals("disk:$copyPath", copiedResource.path) },
            { assertEquals("dir", copiedResource.type) }
        )
    }

    @Test
    @DisplayName("(PUT + POST + GET) Создание, перемещение и чтение директории")
    fun createAndMoveFolder() {
        val sourcePath = "${uniqPath}/source"
        val destinationPath = "${uniqPath}/destination"

        val createRootResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createRootResponse.statusCode)

        val createSourceResponse = diskClient.createFolder(sourcePath)
        assertEquals(201, createSourceResponse.statusCode)

        val moveResponse = diskClient.moveResource(
            from = sourcePath,
            path = destinationPath
        )
        operationWaiter.awaitCompletion(moveResponse, 201)

        val sourceResponse = diskClient.getResource(sourcePath)
        val destinationResponse = diskClient.getResource(destinationPath)
        assertEquals(404, sourceResponse.statusCode)
        assertEquals(200, destinationResponse.statusCode)
        val destinationResource = destinationResponse.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals("disk:$destinationPath", destinationResource.path) },
            { assertEquals("destination", destinationResource.name) },
            { assertEquals("dir", destinationResource.type) }
        )
    }

    // Negative cases:
    @Test
    @DisplayName("(POST) Копирование в существующую директорию без перезаписи")
    fun copyFolderToExistingDestinationWithoutOverwrite() {
        val sourcePath = "$uniqPath/source"
        val destinationPath = "$uniqPath/destination"

        assertEquals(201, diskClient.createFolder(uniqPath).statusCode)
        assertEquals(201, diskClient.createFolder(sourcePath).statusCode)
        assertEquals(201, diskClient.createFolder(destinationPath).statusCode)

        val response = diskClient.copyResource(
            from = sourcePath,
            path = destinationPath,
            overwrite = false
        )

        val error = response.assertApiError(409)

        assertEquals("DiskResourceAlreadyExistsError", error.error)
    }

    @Test
    @DisplayName("(POST) Перемещение несуществующего ресурса")
    fun moveMissingResource() {
        val destinationPath = "$uniqPath/destination"

        val response = diskClient.moveResource(
            from = "$uniqPath/missing",
            path = destinationPath
        )

        val error = response.assertApiError(404)

        assertEquals("DiskNotFoundError", error.error)
    }
}
