package com.disk.yandex.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class LinkResponse(
    val href: String,
    val method: String? = null,
    val templated: Boolean? = null,
    val operationId: String? = null
)
