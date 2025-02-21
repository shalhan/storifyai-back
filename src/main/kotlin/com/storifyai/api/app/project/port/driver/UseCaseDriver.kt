package com.storifyai.api.app.project.port.driver

import java.time.Instant

interface UseCaseDriver {
    suspend fun save(userId: String, title: String): String
    suspend fun update(userId: String, projectId: String, param: UpdateParam): UpdateResult
    suspend fun delete(userId: String, projectId: String): String
    suspend fun findAll(userId: String): List<FindResult>
    suspend fun findOneById(userId: String, projectId: String): FindResult?
}

data class UpdateParam(
    val title: String,
) {
    fun validate() {
        if (title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }
    }
}

data class UpdateResult(
    val title: String,

    val updatedDate: Instant,
)

data class FindResult(val id: String, val userId: String, val title: String, val createdDate: Instant, val updatedDate: Instant, val deletedDate: Instant?)
