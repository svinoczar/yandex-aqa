package com.disk.yandex.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class OperationResponse(
    val status: OperationStatus
)