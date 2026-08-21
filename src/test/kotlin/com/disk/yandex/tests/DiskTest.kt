package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import com.disk.yandex.model.response.DiskInfoResponse
import com.disk.yandex.util.bodyAs
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@Epic("Yandex Disk API")
@Feature("Disk information")
@DisplayName("Disk - general information")
class DiskTest : TestBase() {

    @Test
    @DisplayName("(GET) Получение общей информации о диске")
    fun getDiskInfo() {
        val response = diskClient.getDiskInfo()

        assertEquals(200, response.statusCode)
        val diskInfo = response.bodyAs<DiskInfoResponse>()

        assertAll(
            { assertTrue(diskInfo.totalSpace > 0) },
            { assertTrue(diskInfo.usedSpace in 0..diskInfo.totalSpace) },
            { assertTrue(diskInfo.trashSize >= 0) },
            { assertTrue(diskInfo.revision >= 0) },
            { assertTrue(diskInfo.systemFolders.isNotEmpty()) },
            { assertTrue(diskInfo.user.login.isNotBlank()) },
            { assertTrue(diskInfo.user.displayName.isNotBlank()) },
            { assertTrue(diskInfo.user.uid.isNotBlank()) }
        )
    }

    @Test
    @DisplayName("(GET) Получение только выбранных полей информации о диске")
    fun getSelectedDiskInfoFields() {
        val response = diskClient.getDiskInfo(
            fields = "total_space,used_space"
        )

        assertEquals(200, response.statusCode)
        val fields = response.bodyAs<Map<String, Any>>()

        assertAll(
            { assertEquals(setOf("total_space", "used_space"), fields.keys) },
            { assertTrue((fields.getValue("total_space") as Number).toLong() > 0) },
            { assertTrue((fields.getValue("used_space") as Number).toLong() >= 0) }
        )
    }
}
