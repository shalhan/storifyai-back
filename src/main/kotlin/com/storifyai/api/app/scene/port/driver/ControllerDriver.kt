package com.storifyai.api.app.scene.port.driver

import java.time.Instant

interface ControllerDriver {
    suspend fun save(userId: String, projectId: String, request: SaveRequest): SaveResponse
    suspend fun update(userId: String, projectId: String, sceneId: String, request: UpdateRequest): UpdateResponse
    suspend fun delete(userId: String, projectId: String, sceneId: String): DeleteResponse
    suspend fun findByProjectId(userId: String, projectId: String): List<FindResponse>
}

data class SaveRequest(
    val setting: SettingRequest,
    val prompt: PromptRequest,
)

data class UpdateRequest(
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
    val id: String,
    val userId: String,
    val projectId: String,
    val setting: SettingResponse,
    val prompt: PromptResponse,

    val createdDate: Instant,
    val updatedDate: Instant,
    val deletedDate: Instant?
)

data class UpdateResponse(
    val id: String,
    val userId: String,
    val projectId: String,
    val setting: SettingResponse,
    val prompt: PromptResponse,

    val updatedDate: Instant,
)

