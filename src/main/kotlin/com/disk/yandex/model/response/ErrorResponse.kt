package com.disk.yandex.model.response

data class ErrorResponse(
    val message: String,
    val description: String,
    val error: String
)
