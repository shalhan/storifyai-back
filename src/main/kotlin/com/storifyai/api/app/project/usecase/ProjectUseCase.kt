package com.storifyai.api.app.project.usecase

import com.storifyai.api.app.project.port.driven.RepositoryDriven
import com.storifyai.api.app.project.port.driver.FindResult
import com.storifyai.api.app.project.port.driven.FindResult as _FindResult
import com.storifyai.api.app.project.port.driver.UseCaseDriver
import com.storifyai.api.app.project.port.driver.UpdateParam
import com.storifyai.api.app.project.port.driven.UpdateParam as _UpdateParam
import com.storifyai.api.app.project.port.driver.UpdateResult


class ProjectUseCase(private val repoAdapter: RepositoryDriven): UseCaseDriver {
    override suspend fun save(userId: String, title: String): String {
        if (title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }

        return repoAdapter.save(userId, title)
    }

    override suspend fun update(userId: String, projectId: String, param: UpdateParam): UpdateResult {
        param.validate()

        val result = repoAdapter.update(userId, projectId, _UpdateParam(param.title))

        return UpdateResult(title = result.title, updatedDate = result.updatedDate)
    }

    override suspend fun delete(userId: String, projectId: String): String {
        return repoAdapter.delete(userId, projectId)
    }

    override suspend fun findAll(userId: String): List<FindResult> {
        val result: List<_FindResult> = repoAdapter.findAll(userId)

        return result.map {
            FindResult(it.id, it.userId, it.title, it.createdDate, it.updatedDate, it.deletedDate)
        }
    }

    override suspend fun findOneById(userId: String, projectId: String): FindResult? {
        val result = repoAdapter.findOneById(userId, projectId) ?: return null

        return FindResult(result.id, result.userId, result.title, result.createdDate, result.updatedDate, result.deletedDate)
    }

}