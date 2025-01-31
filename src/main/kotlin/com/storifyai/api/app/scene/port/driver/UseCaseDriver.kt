package com.storifyai.api.app.scene.port.driver

import java.time.Instant

interface UseCaseDriver {
    suspend fun save(userId: String, projectId: String, param: SaveParam): String
    suspend fun update(userId: String, projectId: String, sceneId: String, param: UpdateParam): UpdateResult
    suspend fun delete(userId: String, projectId: String, sceneId: String): String
    suspend fun findByProjectId(userId: String, projectId: String): List<FindResult>
}

data class SaveParam(
    val setting: SettingParam,
    val prompt: PromptParam
)

data class UpdateParam(
    val setting: SettingParam,
    val prompt: PromptParam,
)

data class UpdateResult(
    val setting: SettingResult,
    val prompt: PromptResult,

    val updatedDate: Instant,
)

data class FindResult(
    val id: String,
    val userId: String,
    val projectId: String,
    val setting: SettingResult,
    val prompt: PromptResult,

    val createdDate: Instant,
    val updatedDate: Instant,
    val deletedDate: Instant?
)

data class SettingParam(val isFull: Boolean, val background: String)

data class PromptParam(val characters: List<String>, val style: String, val background: String, val detail: String)

data class SettingResult(val isFull: Boolean, val background: String)

data class PromptResult(val characters: List<String>, val style: String, val background: String, val detail: String)

