package com.disk.yandex.util

import io.restassured.response.Response

inline fun <reified T> Response.bodyAs(): T =
    then()
        .extract()
        .`as`(T::class.java)
