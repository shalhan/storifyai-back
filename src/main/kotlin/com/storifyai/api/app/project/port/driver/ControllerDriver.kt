package com.storifyai.api.app.project.port.driver

import java.time.Instant

interface ControllerDriver {
    suspend fun save(userId: String, request: SaveRequest): SaveResponse
    suspend fun update(userId: String, projectId: String, request: UpdateRequest): UpdateResponse
    suspend fun delete(userId: String, projectId: String): String
    suspend fun findAll(userId: String): List<FindResponse>
    suspend fun findOneById(userId: String, projectId: String): FindResponse?
}

data class UpdateRequest(
    val title: String,
)

data class UpdateResponse(
    val title: String,

    val updatedDate: Instant,
)

data class SaveRequest(val title: String)

data class SaveResponse(val id: String)

data class FindResponse(val id: String, val userId: String, val title: String, val createdDate: Instant, val updatedDate: Instant, val deletedDate: Instant?)