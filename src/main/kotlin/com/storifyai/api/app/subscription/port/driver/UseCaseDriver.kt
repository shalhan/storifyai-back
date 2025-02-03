package com.storifyai.api.app.subscription.port.driver

import java.time.Instant

interface UseCaseDriver {
    suspend fun save(userId: String, param: SaveParam): String
    suspend fun update(userId: String, param: UpdateParam): UpdateResult
    suspend fun findOneActive(userId: String): FindResult
}

data class SaveParam(
    val type: String,
)

data class UpdateParam(
    val remainingQuota: Number,
)

data class UpdateResult(
    val remainingQuota: Number,
)

data class FindResult(
    val id: String,
    val userId: String,
    val type: String,
    val quota: Number,
    val remainingQuota: Number,

    val startDate: Instant,
    val endDate: Instant,
)

