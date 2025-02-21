package com.storifyai.api.infra.adapter.driver.http

import com.storifyai.api.app.scene.port.driver.FindResult
import com.storifyai.api.app.scene.port.driver.UseCaseDriver
import com.storifyai.api.app.scene.port.driver.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/projects/{projectId}/scenes")
class SceneHttp(private val useCase: UseCaseDriver): ControllerDriver {

    @PostMapping("")
    override suspend fun save(@RequestHeader userId: String, @PathVariable projectId: String, @RequestBody request: SaveRequest): SaveResponse {
        val sceneId: String = useCase.save(userId, projectId, SaveParam(
            number = request.number,
            setting = SettingParam(
                isFull = request.setting.isFull,
                background = request.setting.background,
            ),
            prompt = PromptParam(
                characters = request.prompt.characters,
                style = request.prompt.style,
                background = request.prompt.background,
                detail = request.prompt.detail,
            )
        ))

        return SaveResponse(sceneId)
    }

    @PostMapping("/bulk")
    override suspend fun bulkSave(@RequestHeader userId: String, @PathVariable projectId: String, @RequestBody request: List<SaveRequest>): BulkSaveResponse {
        val sceneIds = useCase.bulkSave(userId, projectId, request.map {
            SaveParam(
                number = it.number,
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

        return BulkSaveResponse(sceneIds)
    }

    @PutMapping("")
    override suspend fun update(@RequestHeader userId: String, @PathVariable projectId: String, @PathVariable sceneId: String, @RequestBody request: UpdateRequest
    ): UpdateResponse {
        val result = useCase.update(userId, projectId, sceneId, UpdateParam(
            setting = SettingParam(
                isFull = request.setting.isFull,
                background = request.setting.background,
            ),
            prompt = PromptParam(
                characters = request.prompt.characters,
                style = request.prompt.style,
                background = request.prompt.background,
                detail = request.prompt.detail,
            ),
        ))

        return UpdateResponse(
            id = sceneId,
            userId = userId,
            projectId = projectId,
            number = result.number,
            setting = SettingResponse(
                isFull = result.setting.isFull,
                background = result.setting.background,
            ),
            prompt = PromptResponse(
                characters = result.prompt.characters,
                style = result.prompt.style,
                background = result.prompt.background,
                detail = result.prompt.detail,
            ),
            updatedDate = result.updatedDate,
        )
    }

    @DeleteMapping("/{sceneId}")
    override suspend fun delete(@RequestHeader userId: String, @PathVariable projectId: String, @PathVariable sceneId: String): DeleteResponse {
        useCase.delete(userId, projectId, sceneId)

        return DeleteResponse(sceneId)
    }

    @GetMapping("")
    override suspend fun findManyByProjectId(@RequestHeader userId: String, @PathVariable projectId: String): List<FindResponse> {
        val result: List<FindResult> = useCase.findOneByProjectId(userId, projectId)

        return result.map {
            FindResponse(
                id = it.id,
                userId = userId,
                projectId = projectId,
                number = it.number,
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
                createdDate = it.createdDate,
                updatedDate = it.updatedDate,
                deletedDate = it.deletedDate
            )
        }
    }

}