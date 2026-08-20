package com.disk.yandex.tests

import com.disk.yandex.util.bodyAs
import com.disk.yandex.configuration.TestBase
import com.disk.yandex.model.request.UpdateResourceRequest
import com.disk.yandex.model.response.ResourceResponse
import io.qameta.allure.Epic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@Epic("Disk API")
@DisplayName("Disk - CRUD endpoints")
class ResourceCrudTest : TestBase() {

    @Test
    @DisplayName("(PUT + GET) Создание и чтение директории")
    fun createAndGetFolder() {
        val createResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createResponse.statusCode)

        val getResponse = diskClient.getResource(uniqPath)
        assertEquals(200, getResponse.statusCode)
        val resource = getResponse.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals("disk:$uniqPath", resource.path) },
            { assertEquals(uniqName, resource.name) },
            { assertEquals("dir", resource.type) }
        )
    }

    @Test
    @DisplayName("(PUT + DELETE) Создание и удаление директории")
    fun createAndDeleteFolder() {
        val createResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createResponse.statusCode)

        val deleteResponse = diskClient.deleteResource(uniqPath)
        assertEquals(204, deleteResponse.statusCode)

        val getResponse = diskClient.getResource(uniqPath)
        assertEquals(404, getResponse.statusCode)
    }

    @Test
    @DisplayName("(PUT + PATCH + GET) Создание директории, добавление пользовательского атрибута, чтение директории")
    fun addCustomPropertyOfFolder() {
        val patchRequest = UpdateResourceRequest(
            customProperties = mapOf(
                "environment" to "test"
            )
        )
        val createResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createResponse.statusCode)

        val updateResponse = diskClient.updateResource(uniqPath, patchRequest)
        assertEquals(200, updateResponse.statusCode)

        val getResponse = diskClient.getResource(uniqPath)
        assertEquals(200, getResponse.statusCode)
        val resource = getResponse.bodyAs<ResourceResponse>()

        assertEquals("test", resource.customProperties?.get("environment"))
    }

    @Test
    @DisplayName("(PUT + PATCH + PATCH + GET) Создание директории, добавление и изменение пользовательского атрибута, чтение директории")
    fun addAndUpdateCustomPropertyOfFolder() {
        val addPropertyRequest = UpdateResourceRequest(
            customProperties = mapOf(
                "environment" to "test"
            )
        )
        val updatePropertyRequest = UpdateResourceRequest(
            customProperties = mapOf(
                "environment" to "dev"
            )
        )
        val createResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createResponse.statusCode)

        val addPropertyResponse = diskClient.updateResource(uniqPath, addPropertyRequest)
        assertEquals(200, addPropertyResponse.statusCode)

        val updatePropertyResponse = diskClient.updateResource(uniqPath, updatePropertyRequest)
        assertEquals(200, updatePropertyResponse.statusCode)

        val getResponse = diskClient.getResource(uniqPath)
        assertEquals(200, getResponse.statusCode)
        val resource = getResponse.bodyAs<ResourceResponse>()

        assertEquals("dev", resource.customProperties?.get("environment"))
    }

    @Test
    @DisplayName("(PUT + PATCH + PATCH + GET) Создание директории, добавление и удаление пользовательского атрибута, чтение директории")
    fun addAndDeleteCustomPropertyOfFolder() {
        val addPropertyRequest = UpdateResourceRequest(
            customProperties = mapOf(
                "environment" to "test"
            )
        )
        val deletePropertyRequest = UpdateResourceRequest(
            customProperties = mapOf(
                "environment" to null
            )
        )
        val createResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createResponse.statusCode)

        val addPropertyResponse = diskClient.updateResource(uniqPath, addPropertyRequest)
        assertEquals(200, addPropertyResponse.statusCode)

        val deletePropertyResponse = diskClient.updateResource(uniqPath, deletePropertyRequest)
        assertEquals(200, deletePropertyResponse.statusCode)

        val getResponse = diskClient.getResource(uniqPath)
        assertEquals(200, getResponse.statusCode)
        val resource = getResponse.bodyAs<ResourceResponse>()

        assertFalse(resource.customProperties.orEmpty().containsKey("environment"))
    }
}
