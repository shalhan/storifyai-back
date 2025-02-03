package com.storifyai.api.app.subscription.usecase

import com.storifyai.api.app.subscription.port.driven.RepositoryDriven
import com.storifyai.api.app.subscription.port.driver.*
import com.storifyai.api.common.enum.SubscriptionTypeDetail
import java.time.Instant
import com.storifyai.api.app.subscription.port.driven.SaveParam as _SaveParam
import com.storifyai.api.app.subscription.port.driven.UpdateParam as _UpdateParam


class SubscriptionUseCase(private val repoAdapter: RepositoryDriven): UseCaseDriver {
    override suspend fun save(userId: String, param: SaveParam): String {
        val detail = SubscriptionTypeDetail.valueOf(param.type).getDetail()

        val subscriptionId = repoAdapter.save(userId, _SaveParam(param.type, detail.quota, detail.duration))

        return subscriptionId
    }

    override suspend fun update(userId: String, param: UpdateParam): UpdateResult {
        val updated = repoAdapter.update(userId, _UpdateParam(param.remainingQuota))

        return UpdateResult(updated.remainingQuota)
    }

    override suspend fun findOneActive(userId: String): FindResult {
        val result = repoAdapter.findOneByDate(userId, Instant.now())

        return FindResult(
            id = result.id,
            userId = result.userId,
            type = result.type,
            quota = result.quota,
            remainingQuota = result.remainingQuota,
            startDate = result.startDate,
            endDate = result.endDate,
        )
    }

}