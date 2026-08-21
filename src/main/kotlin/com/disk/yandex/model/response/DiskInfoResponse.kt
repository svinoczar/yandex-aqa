package com.disk.yandex.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DiskInfoResponse(
    val totalSpace: Long,
    val usedSpace: Long,
    val trashSize: Long,
    val revision: Long,
    val systemFolders: Map<String, String>,
    val user: DiskUserResponse
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DiskUserResponse(
    val login: String,
    val displayName: String,
    val uid: String
)
