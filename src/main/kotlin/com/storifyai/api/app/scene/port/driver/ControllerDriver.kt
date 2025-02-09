package com.storifyai.api.app.scene.port.driver

import java.time.Instant

interface ControllerDriver {
    suspend fun save(userId: String, projectId: String, request: List<SaveRequest>): SaveResponse
    suspend fun update(userId: String, projectId: String, request: List<UpdateRequest>): UpdateResponse
    suspend fun delete(userId: String, projectId: String): DeleteResponse
    suspend fun findOne(userId: String, projectId: String): FindResponse
}

data class SaveRequest(
    val imageURL: String?,
    val imageReferenceId: String?,
    val number: Int,
    val setting: SettingRequest,
    val prompt: PromptRequest,
)

data class UpdateRequest(
    val number: Int,
    val imageURL: String?,
    val imageReferenceId: String?,
    val setting: SettingRequest,
    val prompt: PromptRequest,
)

data class SettingRequest(val isFull: Boolean, val background: String)

data class PromptRequest(val characters: List<String>, val style: String, val background: String, val detail: String)

data class SettingResponse(val isFull: Boolean, val background: String)

data class PromptResponse(val characters: List<String>, val style: String, val background: String, val detail: String)

data class SaveResponse(val id: String)

data class DeleteResponse(val id: String)

data class FindResponse(
    val userId: String,
    val projectId: String,
    val details: List<SceneDetailResponse>,

    val createdDate: Instant,
    val updatedDate: Instant,
    val deletedDate: Instant?
)

data class UpdateResponse(
    val userId: String,
    val projectId: String,
    val details: List<SceneDetailResponse>,

    val updatedDate: Instant,
)

data class SceneDetailResponse(val number: Int, val imageURL: String?, val imageReferenceId: String?, val setting: SettingResponse, val prompt: PromptResponse)
