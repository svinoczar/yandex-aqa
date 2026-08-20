package com.disk.yandex.configuration

import com.disk.yandex.client.DiskClient
import com.disk.yandex.util.OperationWaiter
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.util.UUID

abstract class TestBase {
    protected lateinit var diskClient: DiskClient
        private set

    protected lateinit var operationWaiter: OperationWaiter
        private set

    protected lateinit var uniqName: String
        private set

    protected lateinit var uniqPath: String
        private set

    companion object {
        @JvmStatic
        @BeforeAll
        fun configure() {
            RestAssured.filters(AllureRestAssured())
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }
    }

    @BeforeEach
    fun setUp() {
        uniqName = "autotest-${UUID.randomUUID()}"
        uniqPath = "/${uniqName}"
        diskClient = DiskClient(Config.token, Config.baseUrl)
        operationWaiter = OperationWaiter(diskClient)
    }

    @AfterEach
    fun cleanUp() {
        if (!::diskClient.isInitialized) {
            return
        }

        val response = diskClient.deleteResource(
            path = uniqPath,
            permanently = true
        )
        operationWaiter.awaitCompletion(response, 204, 404)
    }
}
