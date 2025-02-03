package com.storifyai.api.app.subscription.port.driver

import java.time.Instant

interface ControllerDriver {
    suspend fun save(userId: String, request: SaveRequest): String
    suspend fun update(userId: String, request: UpdateRequest): UpdateResponse
    suspend fun findOneActive(userId: String): FindResponse
}

data class SaveRequest(
    val type: String,
)

data class UpdateRequest(
    val remainingQuota: Number,
)

data class UpdateResponse(
    val remainingQuota: Number,
)

data class FindResponse(
    val id: String,
    val userId: String,
    val type: String,
    val quota: Number,
    val remainingQuota: Number,

    val startDate: Instant,
    val endDate: Instant,
)
