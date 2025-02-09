package com.storifyai.api.app.scene.usecase

import com.storifyai.api.app.project.usecase.ProjectUseCase
import com.storifyai.api.app.scene.port.driven.PromptParam
import com.storifyai.api.app.scene.port.driven.RepositoryDriven
import com.storifyai.api.app.scene.port.driven.SettingParam
import com.storifyai.api.app.scene.port.driver.*
import com.storifyai.api.app.scene.port.driven.SettingParam as _SettingParam
import com.storifyai.api.app.scene.port.driven.PromptParam as _PromptParam

import com.storifyai.api.app.scene.port.driven.FindResult as _FindResult
import com.storifyai.api.app.scene.port.driven.SaveParam as _SaveParam
import com.storifyai.api.app.scene.port.driven.UpdateParam as _UpdateParam

open class SceneUseCase(private val projectScene: ProjectUseCase, private val repoAdapter: RepositoryDriven): UseCaseDriver {
    override suspend fun save(userId: String, projectId: String, param: SaveParam): String {
        projectScene.findOneById(userId, projectId) ?: throw IllegalStateException("Project not found")

        return repoAdapter.save(userId, projectId, _SaveParam(
            number = param.number,
            imageURL = param.imageURL,
            imageReferenceId = param.imageReferenceId,
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

    override suspend fun bulkSave(userId: String, projectId: String, params: List<SaveParam>): List<String> {
        projectScene.findOneById(userId, projectId) ?: throw IllegalStateException("Project not found")

        val result = repoAdapter.bulkSave(userId, projectId, params.map {
            com.storifyai.api.app.scene.port.driven.SaveParam(
                number = it.number,
                imageURL = it.imageURL,
                imageReferenceId = it.imageReferenceId,
                setting = SettingParam(
                    isFull = it.setting.isFull,
                    background = it.setting.background
                ),
                prompt = PromptParam(
                    characters = it.prompt.characters,
                    style = it.prompt.style,
                    background = it.prompt.background,
                    detail = it.prompt.detail
                )
            )
        })

        return result
    }

    override suspend fun update(userId: String, projectId: String, sceneId: String, param: UpdateParam): UpdateResult {
        projectScene.findOneById(userId, projectId) ?: throw IllegalStateException("Project not found")

        val result = repoAdapter.update(userId, projectId, sceneId, _UpdateParam(
            imageURL = param.imageURL,
            imageReferenceId = param.imageReferenceId,
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
            imageURL = param.imageURL,
            imageReferenceId = param.imageReferenceId,
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
            number = result.number,
        )
    }

    override suspend fun delete(userId: String, projectId: String, sceneId: String): String {
        projectScene.findOneById(userId, projectId) ?: throw IllegalStateException("Project not found")

        return repoAdapter.delete(userId, projectId, sceneId)
    }

    override suspend fun findOne(userId: String, projectId: String, sceneId: String): FindResult? {
        projectScene.findOneById(userId, projectId) ?: throw IllegalStateException("Project not found")

        val result = repoAdapter.findOne(userId, projectId, sceneId) ?: return null

        return newFindResult(userId, result)
    }

    override suspend fun findOneByReferenceId(userId: String, imageReferenceId: String): FindResult? {
        val result = repoAdapter.findOneByReferenceId(userId, imageReferenceId) ?: return null

        return newFindResult(userId, result)
    }

    override suspend fun findOneByProjectId(userId: String, projectId: String): List<FindResult> {
        projectScene.findOneById(userId, projectId) ?: throw IllegalStateException("Project not found")

        val result: List<_FindResult> = repoAdapter.findManyByProjectId(userId, projectId)

        return result.map {
            newFindResult(userId, it)
        }
    }

    private fun newFindResult(userId: String, result: _FindResult): FindResult {
        return FindResult(
            id = result.id,
            projectId = result.projectId,
            userId = userId,
            number = result.number,
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
            imageURL = result.imageURL,
            imageReferenceId = result.imageReferenceId,
            createdDate = result.createdDate,
            deletedDate = result.deletedDate,
            updatedDate = result.updatedDate
        )
    }

}