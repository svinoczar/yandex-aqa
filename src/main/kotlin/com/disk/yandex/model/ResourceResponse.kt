package svinoczar.dev.com.disk.yandex.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResourceResponse(
    val path: String?,
    val name: String?,
    val type: String?,
    val size: Long?,
    val mime_type: String?,
    val custom_properties: Map<String, String>?
)