package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import com.disk.yandex.model.response.ResourceResponse
import com.disk.yandex.util.assertApiError
import com.disk.yandex.util.bodyAs
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@Epic("Yandex Disk API")
@Feature("Trash resources")
@DisplayName("Disk - trash resources")
class TrashResourceTest : TestBase() {
    @Test
    @DisplayName("(PUT + DELETE + GET) Получение удалённой директории из корзины")
    fun getDeletedFolderFromTrash() {
        createFolderAndMoveToTrash()

        val resource = findTestResourceInTrash()

        assertAll(
            { assertTrue(resource.path.startsWith("trash:/")) },
            { assertEquals(uniqName, resource.name) },
            { assertEquals("dir", resource.type) }
        )
    }

    @Test
    @DisplayName("(PUT + DELETE + PUT + GET) Восстановление директории из корзины")
    fun restoreFolderFromTrash() {
        createFolderAndMoveToTrash()

        val trashResource = findTestResourceInTrash()

        val restoreResponse = diskClient.restoreFromTrash(trashResource.path)
        operationWaiter.awaitCompletion(restoreResponse, 201)

        val resourceResponse = diskClient.getResource(uniqPath)
        assertEquals(200, resourceResponse.statusCode)
        val resource = resourceResponse.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals("disk:$uniqPath", resource.path) },
            { assertEquals(uniqName, resource.name) },
            { assertEquals("dir", resource.type) }
        )
    }

    @Test
    @DisplayName("(PUT + DELETE + DELETE + GET) Окончательное удаление директории из корзины")
    fun deleteFolderFromTrashPermanently() {
        createFolderAndMoveToTrash()

        val trashResource = findTestResourceInTrash()

        val deleteResponse = diskClient.deleteTrashResource(trashResource.path)
        operationWaiter.awaitCompletion(deleteResponse, 204)

        val response = diskClient.getTrashResource(trashResource.path)
        val error = response.assertApiError(404)

        assertEquals("DiskNotFoundError", error.error)
    }

    @Test
    @Tag("negative")
    @DisplayName("(PUT) Восстановление несуществующей директории из корзины")
    fun restoreMissingFolderFromTrash() {
        val response = diskClient.restoreFromTrash("trash:/missing-$uniqName")
        val error = response.assertApiError(404)

        assertEquals("DiskNotFoundError", error.error)
    }

    private fun createFolderAndMoveToTrash() {
        assertEquals(201, diskClient.createFolder(uniqPath).statusCode)

        val deleteResponse = diskClient.deleteResource(uniqPath)
        operationWaiter.awaitCompletion(deleteResponse, 204)
    }

    private fun findTestResourceInTrash(): ResourceResponse {
        val response = diskClient.getTrashResource(
            path = "trash:/",
            limit = 100,
            sort = "-deleted"
        )
        assertEquals(200, response.statusCode)

        return response.bodyAs<ResourceResponse>()
            .embedded
            ?.items
            ?.singleOrNull { it.name == uniqName }
            ?: throw AssertionError("Resource $uniqName was not found in trash")
    }
}
