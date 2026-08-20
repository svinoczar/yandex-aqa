package com.disk.yandex.tests

import com.disk.yandex.client.DiskClient
import com.disk.yandex.configuration.Config
import com.disk.yandex.configuration.TestBase
import io.qameta.allure.Epic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import svinoczar.dev.com.disk.yandex.model.ResourceResponse
import svinoczar.dev.com.disk.yandex.model.UpdateResourceRequest

@Epic("Disk API")
@DisplayName("Disk - CRUD endpoints")
class ResourceCrudTests : TestBase() {

    private val diskClient = DiskClient(Config.token)

    @AfterEach
    fun tearDown() {
        val response = diskClient.deleteResource(
            uniqPath,
            permanently = true
        )
        if (response.statusCode != 204 && response.statusCode != 404) {
            throw AssertionError(
                "Failed to cleanup test directory: ${response.statusCode}"
            )
        }
    }

//    @Step("Создать директорию {path}")
//    private fun createFolder(path: String): Response {
//        return diskClient.createFolder(path)
//    }
//
//    @Step("Получить директорию {path}")
//    private fun getFolder(path: String): Response {
//        return diskClient.getResource(path)
//    }
//
//    @Step("Удалить директорию {path}")
//    private fun deleteFolder(path: String): Response {
//        return diskClient.deleteResource(path)
//    }


    @Test
    @DisplayName("(PUT + GET) Создание и чтение директории")
    fun createAndGetFolder() {
        var response = diskClient.createFolder(uniqPath)
        assertEquals(201, response.statusCode)

        response = diskClient.getResource(uniqPath)
        assertAll(
            { assertEquals(200, response.statusCode) },
            { assertEquals("disk:${uniqPath}", response.jsonPath().getString("path")) },
            { assertEquals(uniqName, response.jsonPath().getString("name")) }
        )
    }

    @Test
    @DisplayName("(PUT + DELETE) Создание и удаление директории")
    fun createAndDeleteFolder() {
        var response = diskClient.createFolder(uniqPath)
        assertEquals(201, response.statusCode)

        response = diskClient.deleteResource(uniqPath)
        assertEquals(204, response.statusCode)

        response = diskClient.getResource(uniqPath)
        assertEquals(404, response.statusCode)
    }

    @Test
    @DisplayName("(PUT + PATCH + GET) Создание директории, добавление пользовательского атрибута, чтение директории")
    fun addCustomPropertyOfFolder() {
        val patchRequest = UpdateResourceRequest(
            custom_properties = mapOf(
                "environment" to "test"
            )
        )
        var response = diskClient.createFolder(uniqPath)
        assertEquals(201, response.statusCode)

        response = diskClient.updateResource(uniqPath, patchRequest)
        assertEquals(200, response.statusCode)

        response = diskClient.getResource(uniqPath)
        val resource = response
            .then()
            .extract()
            .`as`(ResourceResponse::class.java)
        assertAll(
            { assertEquals(200, response.statusCode) },
            {
                assertEquals(
                    "test",
                    resource.custom_properties?.get("environment")
                )
            }
        )
    }

    @Test
    @DisplayName("(PUT + PATCH + PATCH + GET) Создание директории, добавление и изменение пользовательского атрибута, чтение директории")
    fun addAndUpdateCustomPropertyOfFolder() {
        var patchRequest = UpdateResourceRequest(
            custom_properties = mapOf(
                "environment" to "test"
            )
        )
        var response = diskClient.createFolder(uniqPath) // PUT
        assertEquals(201, response.statusCode)

        response = diskClient.updateResource(uniqPath, patchRequest) // PATCH 1
        assertEquals(200, response.statusCode)

        patchRequest = UpdateResourceRequest(
            custom_properties = mapOf(
                "environment" to "dev"
            )
        )
        response = diskClient.updateResource(uniqPath, patchRequest) // PATCH 2
        assertEquals(200, response.statusCode)

        response = diskClient.getResource(uniqPath) // GET
        val resource = response
            .then()
            .extract()
            .`as`(ResourceResponse::class.java)
        assertAll(
            { assertEquals(200, response.statusCode) },
            {
                assertEquals(
                    "dev",
                    resource.custom_properties?.get("environment")
                )
            }
        )
    }

    @Test
    @DisplayName("(PUT + PATCH + PATCH + GET) Создание директории, добавление и удаление пользовательского атрибута, чтение директории")
    fun addAndDeleteCustomPropertyOfFolder() {
        var patchRequest = UpdateResourceRequest(
            custom_properties = mapOf(
                "environment" to "test"
            )
        )
        var response = diskClient.createFolder(uniqPath) // PUT
        assertEquals(201, response.statusCode)

        response = diskClient.updateResource(uniqPath, patchRequest) // PATCH 1
        assertEquals(200, response.statusCode)

        patchRequest = UpdateResourceRequest(
            custom_properties = mapOf(
                "environment" to null
            )
        )
        response = diskClient.updateResource(uniqPath, patchRequest) // PATCH 2 (DELETE PROP)
        assertEquals(200, response.statusCode)

        response = diskClient.getResource(uniqPath) // GET
        val resource = response
            .then()
            .extract()
            .`as`(ResourceResponse::class.java)

        assertAll(
            { assertEquals(200, response.statusCode) },
            {
                assertEquals(
                    "null",
                    resource.custom_properties?.get("environment")
                )
            }
        )
    }
}