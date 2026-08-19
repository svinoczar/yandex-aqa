package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiskApiTests : TestBase() {

    @Test
    fun shouldGetDiskInfo() {
        val response = diskClient.getDiskInfo()

        assertEquals(200, response.statusCode)
    }
}