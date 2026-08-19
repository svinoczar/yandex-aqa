package com.disk.yandex.client

import io.restassured.RestAssured
import io.restassured.response.Response

class DiskClient(
    private val token: String
) {
    private val baseUrl = "https://cloud-api.yandex.net/v1"

    fun getDiskInfo(): Response {
        return RestAssured
            .given()
            .baseUri(baseUrl)
            .header("Authorization", "OAuth $token")
            .get("/disk")
    }
}