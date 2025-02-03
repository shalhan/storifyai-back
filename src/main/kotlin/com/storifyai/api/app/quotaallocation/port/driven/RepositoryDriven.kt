package com.storifyai.api.app.quotaallocation.port.driven

interface RepositoryDriven {
    suspend fun save(userId: String, referenceId: String, param: SaveParam): String
    suspend fun update(userId: String, referenceId: String, param: UpdateParam): UpdateResult
}

data class SaveParam(
    val type: String,
    val quota: Number,
)

data class UpdateParam(
    val status: String,
)

data class UpdateResult(
    val status: String,
)