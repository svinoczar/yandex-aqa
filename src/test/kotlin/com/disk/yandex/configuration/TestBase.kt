package com.disk.yandex.configuration

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import io.qameta.allure.restassured.AllureRestAssured
import java.util.UUID

abstract class TestBase {
    protected lateinit var uniqName: String
    protected lateinit var uniqPath: String

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
    }
}