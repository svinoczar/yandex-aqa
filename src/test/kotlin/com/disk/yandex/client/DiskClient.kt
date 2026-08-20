package com.disk.yandex.client

import com.disk.yandex.model.request.UpdateResourceRequest
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

class DiskClient(
    token: String,
    baseUrl: String
) {
    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(baseUrl)
        .setAccept(ContentType.JSON)
        .addHeader("Authorization", "OAuth $token")
        .build()

    private fun request(): RequestSpecification =
        given().spec(requestSpecification)

    /*
        v1/disk/resources:
        - DELETE v1/disk/resources
        - GET v1/disk/resources
        - PATCH v1/disk/resources
        - PUT v1/disk/resources
        - POST v1/disk/resources/copy
        - GET v1/disk/resources/download
        - GET v1/disk/resources/files
        - GET v1/disk/resources/last-uploaded
        - POST v1/disk/resources/move
        - GET v1/disk/resources/public
        - PUT v1/disk/resources/publish
        - PUT v1/disk/resources/unpublish
        - GET v1/disk/resources/upload
        - POST v1/disk/resources/upload
     */
    fun deleteResource(
        path: String,
        permanently: Boolean = false,
        forceAsync: Boolean = false
    ): Response {
        return request()
            .queryParam("path", path)
            .queryParam("permanently", permanently)
            .queryParam("force_async", forceAsync)
            .delete("/resources")
    }

    fun getResource(path: String, fields: String? = null): Response {
        val request = request()
            .queryParam("path", path)

        fields?.let { request.queryParam("fields", it) }

        return request.get("/resources")
    }

    fun updateResource(path: String, body: UpdateResourceRequest): Response {
        return request()
            .contentType(ContentType.JSON)
            .queryParam("path", path)
            .body(body)
            .patch("/resources")
    }

    fun createFolder(path: String): Response {
        return request()
            .queryParam("path", path)
            .put("/resources")
    }

    fun copyResource(
        from: String,
        path: String,
        overwrite: Boolean = false,
        forceAsync: Boolean = false
    ): Response {
        return request()
            .queryParam("from", from)
            .queryParam("path", path)
            .queryParam("overwrite", overwrite)
            .queryParam("force_async", forceAsync)
            .post("/resources/copy")
    }

    fun getDownloadResource(path: String): Response {
        return request()
            .queryParam("path", path)
            .get("/resources/download")
    }

    fun getFilesResource(): Response {
        return request()
            .get("/resources/files")
    }

    fun getLastUploadedResources(): Response {
        return request()
            .get("/resources/last-uploaded")
    }

    fun moveResource(
        from: String,
        path: String,
        overwrite: Boolean = false,
        forceAsync: Boolean = false
    ): Response {
        return request()
            .queryParam("from", from)
            .queryParam("path", path)
            .queryParam("overwrite", overwrite)
            .queryParam("force_async", forceAsync)
            .post("/resources/move")
    }

    fun getPublicResources(): Response {
        return request()
            .get("/resources/public")
    }

    fun publishResource(path: String, body: String): Response {
        return request()
            .contentType(ContentType.JSON)
            .queryParam("path", path)
            .body(body)
            .put("/resources/publish")
    }

    fun unpublishResource(path: String): Response {
        return request()
            .queryParam("path", path)
            .put("/resources/unpublish")
    }

    fun getUploadLink(path: String): Response {
        return request()
            .queryParam("path", path)
            .get("/resources/upload")
    }

    fun uploadResource(path: String, url: String): Response {
        return request()
            .queryParam("path", path)
            .queryParam("url", url)
            .post("/resources/upload")
    }

    /*
        v1/disk/public/resources:
        - GET v1/disk/public/resources
        - GET v1/disk/public/resources/download
        - PATCH v1/disk/public/resources/public-settings
        - POST v1/disk/public/resources/save-to-disk
     */
    fun getPublicResource(publicKey: String): Response {
        return request()
            .queryParam("public_key", publicKey)
            .get("/public/resources")
    }

    fun getDownloadPublicResourceLink(publicKey: String): Response {
        return request()
            .queryParam("public_key", publicKey)
            .get("/public/resources/download")
    }

    fun updatePublicLinksSettings(publicKey: String): Response {
        return request()
            .queryParam("public_key", publicKey)
            .patch("/public/resources/public-settings")
    }

    fun savePublicResourceToDisk(publicKey: String): Response {
        return request()
            .queryParam("public_key", publicKey)
            .post("/public/resources/save-to-disk")
    }

    /*
        v1/disk/trash/resources:
        - DELETE v1/disk/trash/resources
        - GET v1/disk/trash/resources
        - POST v1/disk/public/resources/restore
     */
    fun cleanTrash(): Response {
        return request()
            .delete("/trash/resources")
    }

    fun getTrash(path: String): Response {
        return request()
            .queryParam("path", path)
            .get("/trash/resources")
    }

    fun restoreFromTrash(path: String): Response {
        return request()
            .queryParam("path", path)
            .post("/trash/resources/restore")
    }

    /*
        v1/disk/operations/{operation_id}:
        - GET v1/disk/operations/{operation_id}
     */
    fun getAsyncOperationStatus(operationId: String): Response {
        return request()
            .pathParam("operationId", operationId)
            .get("/operations/{operationId}")
    }

    /*
        v1/disk:
        - GET v1/disk
     */
    fun getDiskInfo(): Response {
        return request()
            .get("")
    }

    fun uploadFile(
        uploadLink: String,
        content: String,
        contentType: String = ContentType.TEXT.toString()
    ): Response {
        return given()
            .contentType(contentType)
            .body(content)
            .put(uploadLink)
    }

    fun uploadFile(
        uploadLink: String,
        content: ByteArray,
        contentType: String = ContentType.BINARY.toString()
    ): Response {
        return given()
            .contentType(contentType)
            .body(content)
            .put(uploadLink)
    }
}
