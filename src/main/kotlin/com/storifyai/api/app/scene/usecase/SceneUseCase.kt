package com.storifyai.api.app.scene.usecase

import com.storifyai.api.app.scene.port.driven.RepositoryDriven
import com.storifyai.api.app.scene.port.driver.*
import com.storifyai.api.app.scene.port.driven.SettingParam as _SettingParam
import com.storifyai.api.app.scene.port.driven.PromptParam as _PromptParam

import com.storifyai.api.app.scene.port.driven.SaveParam as _SaveParam
import com.storifyai.api.app.scene.port.driven.UpdateParam as _UpdateParam
import com.storifyai.api.app.scene.port.driven.FindResult as _FindResult

class SceneUseCase(private val repoAdapter: RepositoryDriven): UseCaseDriver {
    override suspend fun save(userId: String, projectId: String, params: List<SaveParam>): String {
        return repoAdapter.save(userId, projectId, params.map {
            _SaveParam(
                number = it.number,
                imageURL = it.imageURL,
                imageReferenceId = it.imageReferenceId,
                setting = _SettingParam(
                    isFull = it.setting.isFull,
                    background = it.setting.background
                ),
                prompt = _PromptParam(
                    characters = it.prompt.characters,
                    style = it.prompt.style,
                    background = it.prompt.background,
                    detail = it.prompt.detail
                )
            )
        })
    }


    override suspend fun update(userId: String, projectId: String, params: List<UpdateParam>): UpdateResult {
        val result = repoAdapter.update(userId, projectId, params.map { _UpdateParam(
            number = it.number,
            setting = _SettingParam(
                isFull = it.setting.isFull,
                background = it.setting.background
            ),
            prompt = _PromptParam(
                characters = it.prompt.characters,
                style = it.prompt.style,
                background = it.prompt.background,
                detail = it.prompt.detail
            )
        )})

        return UpdateResult(
            details = result.details.map {
                SceneDetail(
                    number = it.number,
                    setting = SettingResult(
                        isFull = it.setting.isFull,
                        background = it.setting.background,
                    ),
                    prompt = PromptResult(
                        characters = it.prompt.characters,
                        style = it.prompt.style,
                        background = it.prompt.background,
                        detail = it.prompt.detail
                    ),
                    imageURL = it.imageURL,
                    imageReferenceId = it.imageReferenceId,
                )
            },
            updatedDate = result.updatedDate,
        )
    }

    override suspend fun delete(userId: String, projectId: String): String {
        return repoAdapter.delete(userId, projectId)
    }

    override suspend fun findOne(userId: String, projectId: String): FindResult? {
        val result = repoAdapter.findOne(userId, projectId) ?: return null

        return newFindResult(userId, result)
    }

    private fun newFindResult(userId: String, result: _FindResult): FindResult {
        return FindResult(
            id = result.id,
            projectId = result.projectId,
            userId = userId,
            details = result.details.map {
                SceneDetail(
                    number = it.number,
                    setting = SettingResult(
                        isFull = it.setting.isFull,
                        background = it.setting.background,
                    ),
                    prompt = PromptResult(
                        characters = it.prompt.characters,
                        style = it.prompt.style,
                        background = it.prompt.background,
                        detail = it.prompt.detail
                    ),
                    imageURL = it.imageURL,
                    imageReferenceId = it.imageReferenceId,
                )
            },
            createdDate = result.createdDate,
            deletedDate = result.deletedDate,
            updatedDate = result.updatedDate
        )
    }

}