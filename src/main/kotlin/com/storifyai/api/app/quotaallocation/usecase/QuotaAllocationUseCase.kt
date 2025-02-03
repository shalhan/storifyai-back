package com.storifyai.api.app.quotaallocation.usecase

import com.storifyai.api.app.quotaallocation.port.driven.SaveParam as _SaveParam
import com.storifyai.api.app.quotaallocation.port.driven.UpdateParam as _UpdateParam

import com.storifyai.api.app.quotaallocation.port.driver.SaveParam
import com.storifyai.api.app.quotaallocation.port.driver.UpdateParam
import com.storifyai.api.app.quotaallocation.port.driver.UpdateResult
import com.storifyai.api.app.quotaallocation.port.driver.UseCaseDriver
import com.storifyai.api.app.quotaallocation.port.driven.RepositoryDriven

class QuotaAllocationUseCase(private val repoAdapter: RepositoryDriven): UseCaseDriver {
    override suspend fun save(userId: String, referenceId: String, param: SaveParam): String {
        val subscriptionId = repoAdapter.save(userId, referenceId, _SaveParam(param.type, param.quota))

        return subscriptionId
    }

    override suspend fun update(userId: String, referenceId: String, param: UpdateParam): UpdateResult {
        val updated = repoAdapter.update(userId, referenceId, _UpdateParam(param.status))

        return UpdateResult(updated.status, updated.status)
    }
}