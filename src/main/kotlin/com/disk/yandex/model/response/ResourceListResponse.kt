package com.disk.yandex.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResourceListResponse(
    val items: List<ResourceResponse> = emptyList()
)
