package com.storifyai.api.app.imagegeneration.usecase

import com.storifyai.api.app.imagegeneration.port.driven.OutboundDriven
import com.storifyai.api.app.imagegeneration.port.driver.*
import com.storifyai.api.app.imagegeneration.port.driven.GenerateParam as _GenerateParam
import com.storifyai.api.app.scene.port.driver.UseCaseDriver as _SceneUseCase
import com.storifyai.api.app.scene.port.driver.PatchParam as _ScenePatchParam

class ImageGenerationUseCase(
    private val outboundAdapter: OutboundDriven,
    private val sceneUseCase: _SceneUseCase

) : UseCaseDriver {
    override suspend fun generate(userId: String, projectId: String, sceneId: String, param: GenerateParam): GenerateResult {
        val scene = sceneUseCase.findOne(userId, projectId, sceneId) ?: throw IllegalStateException("scene not found")

        // generate image
        val result = outboundAdapter.generateImage(_GenerateParam(
            userId = userId,
            projectId = projectId,
            sceneId = sceneId,
            generatedImageId = param.generatedImageId,
            prompt = param.prompt,
            width = param.width,
            height = param.height,
            numOfImages = param.numOfImages,
        ))

        sceneUseCase.patch(userId, projectId, sceneId, _ScenePatchParam(
            imageReferenceId = result.referenceId,
        ))

        return GenerateResult(result.referenceId)
    }

    override suspend fun save(userId: String, param: SaveParam): SaveResult {
        val scene = sceneUseCase.findOneByReferenceId(userId, param.referenceId) ?: throw IllegalStateException("scene not found")

        sceneUseCase.patch(userId, scene.projectId, scene.id, _ScenePatchParam(
            imageURL = param.imageUrl,
        ))

        return SaveResult(param.referenceId, param.status, param.imageUrl)
    }
}