package com.disk.yandex.tests

import com.disk.yandex.configuration.TestBase
import com.disk.yandex.model.response.LinkResponse
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
@Feature("File transfer")
@DisplayName("Disk - file transfer endpoints")
class FileTransferTest : TestBase() {

    @Test
    @DisplayName("(GET + PUT + GET) Получение ссылки и загрузка файла")
    fun uploadFile() {
        val filePath = "$uniqPath/test.txt"
        val fileContent = "autotest"

        val createFolderResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createFolderResponse.statusCode)

        val uploadLinkResponse = diskClient.getUploadLink(filePath)
        assertEquals(200, uploadLinkResponse.statusCode)
        val uploadLink = uploadLinkResponse.bodyAs<LinkResponse>()

        val uploadResponse = diskClient.uploadFile(uploadLink.href, fileContent)
        assertEquals(201, uploadResponse.statusCode)

        val getFileResponse = diskClient.getResource(filePath)
        assertEquals(200, getFileResponse.statusCode)
        val fileResource = getFileResponse.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals("disk:$filePath", fileResource.path) },
            { assertEquals("test.txt", fileResource.name) },
            { assertEquals("file", fileResource.type) },
            { assertEquals(fileContent.toByteArray().size.toLong(), fileResource.size) }
        )
    }

    @Test
    @DisplayName("(GET) Получение ссылки для скачивания файла")
    fun getDownloadLink() {
        val filePath = "$uniqPath/test.txt"
        val fileContent = "autotest"
        createFolderAndUploadFile(filePath, fileContent)

        val response = diskClient.getDownloadResource(filePath)
        assertEquals(200, response.statusCode)
        val downloadLink = response.bodyAs<LinkResponse>()

        assertAll(
            { assertTrue(downloadLink.href.isNotBlank()) },
            { assertEquals("GET", downloadLink.method) },
            { assertEquals(false, downloadLink.templated) }
        )
    }

    @Test
    @Tag("negative")
    @DisplayName("(GET) Получение ссылки для скачивания отсутствующего файла")
    fun getDownloadLinkForMissingFile() {
        val response = diskClient.getDownloadResource("$uniqPath/missing.txt")

        val error = response.assertApiError(404)

        assertEquals("DiskNotFoundError", error.error)
    }

    @Test
    @Tag("e2e")
    @DisplayName("Загрузка и скачивание файла")
    fun uploadAndDownloadFile() {
        val filePath = "$uniqPath/test.txt"
        val expectedContent = "end-to-end test"

        val createFolderResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createFolderResponse.statusCode)

        val uploadLinkResponse = diskClient.getUploadLink(filePath)
        assertEquals(200, uploadLinkResponse.statusCode)
        val uploadLink = uploadLinkResponse.bodyAs<LinkResponse>()

        val uploadResponse = diskClient.uploadFile(uploadLink.href, expectedContent)
        assertEquals(201, uploadResponse.statusCode)

        val downloadLinkResponse = diskClient.getDownloadResource(filePath)
        assertEquals(200, downloadLinkResponse.statusCode)
        val downloadLink = downloadLinkResponse.bodyAs<LinkResponse>()

        val downloadResponse = diskClient.downloadFile(downloadLink.href)

        assertAll(
            { assertEquals(200, downloadResponse.statusCode) },
            { assertEquals(expectedContent, downloadResponse.body.asString()) }
        )
    }

    private fun createFolderAndUploadFile(filePath: String, content: String) {
        val createFolderResponse = diskClient.createFolder(uniqPath)
        assertEquals(201, createFolderResponse.statusCode)

        val uploadLinkResponse = diskClient.getUploadLink(filePath)
        assertEquals(200, uploadLinkResponse.statusCode)
        val uploadLink = uploadLinkResponse.bodyAs<LinkResponse>()

        val uploadResponse = diskClient.uploadFile(uploadLink.href, content)
        assertEquals(201, uploadResponse.statusCode)
    }
}
