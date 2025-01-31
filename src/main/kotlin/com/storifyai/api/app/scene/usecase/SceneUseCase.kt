package com.storifyai.api.app.scene.usecase

import com.storifyai.api.app.scene.port.driven.RepositoryDriven
import com.storifyai.api.app.scene.port.driver.*
import com.storifyai.api.app.scene.port.driven.SettingParam as _SettingParam
import com.storifyai.api.app.scene.port.driven.PromptParam as _PromptParam

import com.storifyai.api.app.scene.port.driven.FindResult as _FindResult
import com.storifyai.api.app.scene.port.driven.SaveParam as _SaveParam
import com.storifyai.api.app.scene.port.driven.UpdateParam as _UpdateParam

class SceneUseCase(private val repoAdapter: RepositoryDriven): UseCaseDriver {
    override suspend fun save(userId: String, projectId: String, param: SaveParam): String {
        validateSaveParam(param)

        return repoAdapter.save(userId, projectId, _SaveParam(
            setting = _SettingParam(
                isFull = param.setting.isFull,
                background = param.setting.background
            ),
            prompt = _PromptParam(
                characters = param.prompt.characters,
                style = param.prompt.style,
                background = param.prompt.background,
                detail = param.prompt.detail
            )
        ))
    }

    override suspend fun update(userId: String, projectId: String, sceneId: String, param: UpdateParam): UpdateResult {
        val result = repoAdapter.update(userId, projectId, sceneId, _UpdateParam(
            setting = _SettingParam(
                isFull = param.setting.isFull,
                background = param.setting.background
            ),
            prompt = _PromptParam(
                characters = param.prompt.characters,
                style = param.prompt.style,
                background = param.prompt.background,
                detail = param.prompt.detail
            )
        ))

        return UpdateResult(
            setting = SettingResult(
                isFull = result.setting.isFull,
                background = result.setting.background,
            ),
            prompt = PromptResult(
                characters = result.prompt.characters,
                style = result.prompt.style,
                background = result.prompt.background,
                detail = result.prompt.detail
            ),
            updatedDate = result.updatedDate,
        )
    }

    override suspend fun delete(userId: String, projectId: String, sceneId: String): String {
        return repoAdapter.delete(userId, projectId, sceneId)
    }

    override suspend fun findByProjectId(userId: String, projectId: String): List<FindResult> {
        val result: List<_FindResult> = repoAdapter.findByProjectId(userId, projectId)

        return result.map {
            FindResult(
                id = it.id,
                projectId = it.projectId,
                userId = userId,
                setting = SettingResult(
                    isFull = it.setting.isFull,
                    background = it.setting.background
                ),
                prompt = PromptResult(
                    characters = it.prompt.characters,
                    style = it.prompt.style,
                    background = it.prompt.background,
                    detail = it.prompt.detail
                ),
                createdDate = it.createdDate,
                deletedDate = it.deletedDate,
                updatedDate = it.updatedDate
            )
        }
    }

    private fun validateSaveParam(param: SaveParam) {

    }


}