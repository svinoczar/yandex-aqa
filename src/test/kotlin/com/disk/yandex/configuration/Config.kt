package com.disk.yandex.configuration

import io.github.cdimascio.dotenv.dotenv

object Config {
    private val env = dotenv {
        ignoreIfMissing = true
    }

    val token: String = System.getenv("YANDEX_DISK_TOKEN")
        ?: env["YANDEX_DISK_TOKEN"]
        ?: error("YANDEX_DISK_TOKEN is not set")

    val baseUrl: String = System.getenv("YANDEX_DISK_BASE_URL")
        ?: env["YANDEX_DISK_BASE_URL"]
        ?: "https://cloud-api.yandex.net/v1/disk"
}
