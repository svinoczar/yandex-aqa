package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import io.qameta.allure.Step
import io.qameta.allure.Epic
import io.restassured.response.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.UUID

@Epic("Disk API")
@DisplayName("Resources ")
class ResourceCrudTests : TestBase() {

    @Step("Создать папку {path}")
    private fun createFolder(path: String): Response {
        return diskClient.createFolder(path)
    }

    @Step("Получить папку {path}")
    private fun getFolder(path: String): Response {
        return diskClient.getResource(path)
    }

    @Step("Удалить папку {path}")
    private fun deleteFolder(path: String): Response {
        return diskClient.deleteResource(path)
    }

    @Test
    @DisplayName("Создание, получение и удаление папки")
    fun createGetDeleteFolder() {
        val uniqPath = "/autotests/${UUID.randomUUID()}"

        var response = createFolder(uniqPath)
        assertEquals(201, response.statusCode)

        response = getFolder(uniqPath)
        assertAll(
            { assertEquals(200, response.statusCode) },
            { assertEquals("disk:${uniqPath}", response.jsonPath().getString("path")) }
        )

        response = deleteFolder(uniqPath)
        assertEquals(204, response.statusCode)

        response = getFolder(uniqPath)
        assertEquals(404, response.statusCode)
    }
}