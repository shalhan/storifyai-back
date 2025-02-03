package com.storifyai.api.app.subscription.port.driven

import java.time.Instant

interface RepositoryDriven {
    suspend fun save(userId: String, param: SaveParam): String
    suspend fun update(userId: String, param: UpdateParam): UpdateResult
    suspend fun findOneByDate(userId: String, date: Instant): FindResult
}

data class SaveParam(
    val type: String,
    val quota: Number,
    val duration: Number,
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
