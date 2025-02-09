package com.storifyai.api.infra.adapter.driver.http

import com.storifyai.api.app.scene.port.driver.FindResult
import com.storifyai.api.app.scene.port.driver.UseCaseDriver
import com.storifyai.api.app.scene.port.driver.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/projects/{projectId}/scenes")
class SceneHttp(private val useCase: UseCaseDriver): ControllerDriver {

    @PostMapping("")
    override suspend fun save(@RequestHeader userId: String, @PathVariable projectId: String, @RequestBody request: List<SaveRequest>): SaveResponse {
        val sceneId: String = useCase.save(userId, projectId, request.map {
            SaveParam(
                imageURL = it.imageURL,
                imageReferenceId =  it.imageReferenceId,
                setting = SettingParam(
                    isFull = it.setting.isFull,
                    background = it.setting.background,
                ),
                prompt = PromptParam(
                    characters = it.prompt.characters,
                    style = it.prompt.style,
                    background = it.prompt.background,
                    detail = it.prompt.detail,
                )
            )
        })

        return SaveResponse(sceneId)
    }

    override suspend fun update(@RequestHeader userId: String, @PathVariable projectId: String,  @RequestBody request: List<UpdateRequest>
    ): UpdateResponse {
        val result = useCase.update(userId, projectId, request.map {
            UpdateParam(
                imageURL = it.imageURL,
                imageReferenceId =  it.imageReferenceId,
                setting = SettingParam(
                    isFull = it.setting.isFull,
                    background = it.setting.background,
                ),
                prompt = PromptParam(
                    characters = it.prompt.characters,
                    style = it.prompt.style,
                    background = it.prompt.background,
                    detail = it.prompt.detail,
                )
            )
        })

        return UpdateResponse(
            userId = userId,
            projectId = projectId,
            details = result.details.map {
                SceneDetailResponse(
                    imageURL = it.imageURL,
                    imageReferenceId = it.imageReferenceId,
                    setting = SettingResponse(
                        isFull = it.setting.isFull,
                        background = it.setting.background,
                    ),
                    prompt = PromptResponse(
                        characters = it.prompt.characters,
                        style = it.prompt.style,
                        background = it.prompt.background,
                        detail = it.prompt.detail,
                    ),
                )
            },
            updatedDate = result.updatedDate,
        )
    }

    override suspend fun delete(userId: String, projectId: String): DeleteResponse {
        useCase.delete(userId, projectId)

        return DeleteResponse(projectId)
    }

    @GetMapping("")
    override suspend fun findOne(@RequestHeader userId: String, @PathVariable projectId: String): FindResponse {
        val result = useCase.findOne(userId, projectId)

        if (result != null) {
            return FindResponse(
                userId = userId,
                projectId = projectId,
                details = result.details.map {
                    SceneDetailResponse(
                        imageURL = it.imageURL,
                        imageReferenceId = it.imageReferenceId,
                        setting = SettingResponse(
                            isFull = it.setting.isFull,
                            background = it.setting.background,
                        ),
                        prompt = PromptResponse(
                            characters = it.prompt.characters,
                            style = it.prompt.style,
                            background = it.prompt.background,
                            detail = it.prompt.detail,
                        ),
                    )
                },
                createdDate = result.createdDate,
                updatedDate = result.updatedDate,
                deletedDate = result.deletedDate,
            )
        } else {
            throw IllegalStateException("Not found")
        }
    }

}