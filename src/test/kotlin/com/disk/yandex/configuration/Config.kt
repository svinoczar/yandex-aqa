package com.disk.yandex.configuration

import io.github.cdimascio.dotenv.dotenv

object Config {

    val env = dotenv()
    val token = env["YANDEX_DISK_TOKEN"]
        ?: error("YANDEX_DISK_TOKEN is not set in .env")
    const val baseUrl = "https://cloud-api.yandex.net/v1/disk"
}