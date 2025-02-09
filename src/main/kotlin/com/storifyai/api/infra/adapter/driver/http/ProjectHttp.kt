package com.storifyai.api.infra.adapter.driver.http

import com.storifyai.api.app.project.port.driver.*
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException.NotFound

@RestController
@RequestMapping("/v1/projects")
class ProjectHttp(private val useCase: UseCaseDriver): ControllerDriver {

    @PostMapping("")
    override suspend fun save(@RequestHeader userId: String, @RequestBody request: SaveRequest): SaveResponse {
        val projectId: String = useCase.save(userId, request.title)

        return SaveResponse(projectId)
    }

    @PutMapping("/{projectId}")
    override suspend fun update(@RequestHeader userId: String, @PathVariable projectId: String, @RequestBody request: UpdateRequest): UpdateResponse {
        val result: UpdateResult = useCase.update(userId, projectId, UpdateParam(
            title = request.title
        ))

        return UpdateResponse(title = result.title, updatedDate = result.updatedDate)
    }

    @DeleteMapping("/{projectId}")
    override suspend fun delete(@RequestHeader userId: String, @PathVariable projectId: String): String {
        useCase.delete(userId, projectId)

        return projectId
    }

    @GetMapping("")
    override suspend fun findAll(@RequestHeader userId: String): List<FindResponse> {
        val result: List<FindResult> = useCase.findAll(userId)

        return result.map {
            FindResponse(it.id, it.userId, it.title, it.createdDate, it.updatedDate, it.deletedDate)
        }
    }

    @GetMapping("/{projectId}")
    override suspend fun findOneById(@RequestHeader userId: String, @PathVariable projectId: String): FindResponse? {
        val result = useCase.findOneById(userId, projectId) ?: throw NotFoundException()

        return FindResponse(
            result.id, result.userId, result.title, result.createdDate, result.updatedDate, result.deletedDate
        )
    }

}