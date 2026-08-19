package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.UUID

class ResourceCrudTests : TestBase() {
    @Test
    fun createGetDeleteFolder() {
        val uniqPath = "/autotest-${UUID.randomUUID()}"
        var response = diskClient.createFolder(uniqPath)
        assertEquals(201, response.statusCode)
        response = diskClient.getResource(uniqPath)
        assertAll(
            { assertEquals(200, response.statusCode) },
            { assertEquals("disk:/${uniqPath}", response.jsonPath().getString("path")) }
        )
        response = diskClient.deleteResource(uniqPath)
        assertEquals(204, response.statusCode)
        response = diskClient.getResource(uniqPath)
        assertEquals(404, response.statusCode)
    }
}