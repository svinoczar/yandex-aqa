package com.disk.yandex.configuration

import com.disk.yandex.client.DiskClient
import com.disk.yandex.model.response.ResourceResponse
import com.disk.yandex.util.OperationWaiter
import com.disk.yandex.util.bodyAs
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
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
        private const val TEST_RESOURCE_PREFIX = "autotest-"
        private const val TRASH_PAGE_SIZE = 100

        @JvmStatic
        @BeforeAll
        fun configure() {
            RestAssured.filters(AllureRestAssured())
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }

        @JvmStatic
        @AfterAll
        fun cleanUpTestTrash() {
            val diskClient = DiskClient(Config.token, Config.baseUrl)
            val operationWaiter = OperationWaiter(diskClient)
            val response = diskClient.getTrashResource(
                path = "trash:/",
                limit = TRASH_PAGE_SIZE,
                sort = "-deleted"
            )
            if (response.statusCode != 200) {
                throw AssertionError(
                    "Failed to read trash during cleanup: " +
                        "status=${response.statusCode}, body=${response.body.asString()}"
                )
            }

            val testResources = response.bodyAs<ResourceResponse>()
                .embedded
                ?.items
                .orEmpty()
                .filter { it.name.startsWith(TEST_RESOURCE_PREFIX) }

            testResources.forEach { resource ->
                val response = diskClient.deleteTrashResource(resource.path)
                operationWaiter.awaitCompletion(response, 204, 404)
            }
        }
    }

    @BeforeEach
    fun setUp() {
        uniqName = "$TEST_RESOURCE_PREFIX${UUID.randomUUID()}"
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

        if (response.statusCode != 404) {
            return
        }

        val trashRootResponse = diskClient.getTrashResource(
            path = "trash:/",
            limit = 100,
            sort = "-deleted"
        )
        if (trashRootResponse.statusCode != 200) {
            return
        }

        trashRootResponse.bodyAs<ResourceResponse>()
            .embedded
            ?.items
            ?.filter { it.name == uniqName }
            ?.forEach { resource ->
                val trashResponse = diskClient.deleteTrashResource(resource.path)
                operationWaiter.awaitCompletion(trashResponse, 204, 404)
            }
    }
}
