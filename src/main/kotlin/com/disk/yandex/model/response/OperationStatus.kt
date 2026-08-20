package com.disk.yandex.model.response

import com.fasterxml.jackson.annotation.JsonProperty

enum class OperationStatus {
    @JsonProperty("in-progress")
    IN_PROGRESS,

    @JsonProperty("success")
    SUCCESS,

    @JsonProperty("failed")
    FAILED
}
