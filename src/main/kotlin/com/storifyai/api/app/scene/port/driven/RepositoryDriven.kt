package com.storifyai.api.app.scene.port.driven

import java.time.Instant

interface RepositoryDriven {
    suspend fun save(userId: String, projectId: String, params: List<SaveParam>): String
    suspend fun update(userId: String,  projectId: String, params: List<UpdateParam>): UpdateResult
    suspend fun delete(userId: String, projectId: String): String
    suspend fun findOne(userId: String, projectId: String): FindResult?
}

data class SaveParam(
    val number: Int,
    val imageURL: String? = null,
    val imageReferenceId: String? = null,
    val setting: SettingParam,
    val prompt: PromptParam,
)

data class UpdateParam(
    val number: Int,
    val imageURL: String? = null,
    val imageReferenceId: String? = null,
    val setting: SettingParam,
    val prompt: PromptParam,
)

data class UpdateResult(
    val details: List<SceneDetail>,

    val updatedDate: Instant,
)

data class FindResult(
    val id: String,
    val userId: String,
    val projectId: String,
    val details: List<SceneDetail>,

    val createdDate: Instant,
    val updatedDate: Instant,
    val deletedDate: Instant?
)

data class SceneDetail(val number: Int, val imageURL: String?, val imageReferenceId: String?, val setting: SettingResult, val prompt: PromptResult)

data class SettingParam(val isFull: Boolean?, val background: String?)

data class PromptParam(val characters: List<String>, val style: String?, val background: String?, val detail: String?)

data class SettingResult(val isFull: Boolean, val background: String)

data class PromptResult(val characters: List<String>, val style: String, val background: String, val detail: String)