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
import java.util.UUID
import kotlin.test.assertNotNull

@Epic("Yandex Disk API")
@Feature("Public resources")
@DisplayName("Disk - public resources")
class PublicResourceTest : TestBase() {

    @Test
    @DisplayName("(PUT + PUT + GET + GET) Публикация и получение публичной директории")
    fun publishAndGetFolder() {
        val publicKey = createAndPublishFolder()

        val response = diskClient.getPublicResource(publicKey)
        assertEquals(200, response.statusCode)
        val resource = response.bodyAs<ResourceResponse>()

        assertAll(
            { assertEquals(uniqName, resource.name) },
            { assertEquals("dir", resource.type) }
        )
    }

    @Test
    @DisplayName("(PUT + PUT + GET + PUT + GET) Снятие публикации с директории")
    fun unpublishFolder() {
        val publicKey = createAndPublishFolder()

        val unpublishResponse = diskClient.unpublishResource(uniqPath)
        assertEquals(200, unpublishResponse.statusCode)

        val response = diskClient.getPublicResource(publicKey)
        val error = response.assertApiError(404)

        assertEquals("DiskNotFoundError", error.error)
    }

    @Test
    @DisplayName("(GET) Получение ссылки для скачивания публичного файла")
    fun getPublicFileDownloadLink() {
        val filePath = "$uniqPath/public.txt"
        val publicKey = createUploadAndPublishFile(filePath)

        val response = diskClient.getDownloadPublicResourceLink(publicKey)
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
    @DisplayName("(GET) Получение несуществующего публичного ресурса")
    fun getMissingPublicResource() {
        val publicKey = "https://disk.yandex.ru/d/${UUID.randomUUID()}"

        val response = diskClient.getPublicResource(publicKey)
        val error = response.assertApiError(404)

        assertEquals("NotFoundError", error.error)
    }

    private fun createAndPublishFolder(): String {
        assertEquals(201, diskClient.createFolder(uniqPath).statusCode)

        val publishResponse = diskClient.publishResource(uniqPath)
        assertEquals(200, publishResponse.statusCode)
        val publicLink = publishResponse.bodyAs<LinkResponse>()

        assertAll(
            { assertTrue(publicLink.href.isNotBlank()) },
            { assertEquals("GET", publicLink.method) },
            { assertEquals(false, publicLink.templated) }
        )

        return getPublicKey(uniqPath)
    }

    private fun createUploadAndPublishFile(filePath: String): String {
        assertEquals(201, diskClient.createFolder(uniqPath).statusCode)

        val uploadLinkResponse = diskClient.getUploadLink(filePath)
        assertEquals(200, uploadLinkResponse.statusCode)
        val uploadLink = uploadLinkResponse.bodyAs<LinkResponse>()

        val uploadResponse = diskClient.uploadFile(uploadLink.href, "public content")
        assertEquals(201, uploadResponse.statusCode)

        val publishResponse = diskClient.publishResource(filePath)
        assertEquals(200, publishResponse.statusCode)

        return getPublicKey(filePath)
    }

    private fun getPublicKey(path: String): String {
        val response = diskClient.getResource(path)
        assertEquals(200, response.statusCode)
        val resource = response.bodyAs<ResourceResponse>()

        assertTrue(assertNotNull(resource.publicUrl).isNotBlank())
        return assertNotNull(resource.publicKey)
    }
}
