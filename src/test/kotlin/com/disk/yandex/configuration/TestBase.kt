package com.disk.yandex.configuration

import com.disk.yandex.client.DiskClient
import io.github.cdimascio.dotenv.dotenv
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class TestBase {

    protected lateinit var diskClient: DiskClient

    @BeforeEach
    fun setUp() {
        val env = dotenv()

        val token = env["YANDEX_DISK_TOKEN"]
            ?: error("YANDEX_DISK_TOKEN is not set in .env")

        diskClient = DiskClient(token)
    }

    @AfterEach
    fun tearDown() {

    }
}