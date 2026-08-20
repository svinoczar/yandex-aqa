package com.disk.yandex.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ResourceResponse(
    val path: String,
    val name: String,
    val type: String,
    val size: Long? = null,
    val mimeType: String? = null,
    val customProperties: Map<String, String>? = null
)
