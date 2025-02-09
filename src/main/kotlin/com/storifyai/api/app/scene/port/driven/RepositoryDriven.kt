package com.storifyai.api.app.scene.port.driven

import java.time.Instant

interface RepositoryDriven {
    suspend fun save(userId: String, projectId: String, param: SaveParam): String
    suspend fun bulkSave(userId: String, projectId: String, params: List<SaveParam>): List<String>
    suspend fun update(userId: String,  projectId: String, sceneId: String, param: UpdateParam): UpdateResult
    suspend fun delete(userId: String, projectId: String, sceneId: String): String
    suspend fun findManyByProjectId(userId: String, projectId: String): List<FindResult>
    suspend fun findOne(userId: String, projectId: String, sceneId: String): FindResult?
    suspend fun findOneByReferenceId(userId: String, imageReferenceId: String): FindResult?
}

data class SaveParam(
    val imageURL: String? = null,
    val imageReferenceId: String? = null,
    val number: Int,
    val setting: SettingParam,
    val prompt: PromptParam,
)

data class UpdateParam(
    val imageURL: String? = null,
    val imageReferenceId: String? = null,
    val setting: SettingParam,
    val prompt: PromptParam,
)

data class UpdateResult(
    val number: Int,
    val imageURL: String? = null,
    val imageReferenceId: String? = null,
    val setting: SettingResult,
    val prompt: PromptResult,

    val updatedDate: Instant,
)

data class FindResult(
    val id: String,
    val userId: String,
    val projectId: String,
    val number: Int,
    val imageURL: String? = null,
    val imageReferenceId: String? = null,
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