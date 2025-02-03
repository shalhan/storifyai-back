package com.storifyai.api.infra.adapter.driven.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.result.InsertOneResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.quotaallocation.port.driven.RepositoryDriven
import com.storifyai.api.app.quotaallocation.port.driven.SaveParam
import com.storifyai.api.app.quotaallocation.port.driven.UpdateParam
import com.storifyai.api.app.quotaallocation.port.driven.UpdateResult
import com.storifyai.api.common.enum.QuotaAllocationStatus
import com.storifyai.api.infra.adapter.driven.mongo.entity.QuotaAllocation
import java.time.Instant

class QuotaAllocationMongo(db: MongoDatabase): RepositoryDriven {
    private val col: MongoCollection<QuotaAllocation> = db.getCollection<QuotaAllocation>("quota_allocations")

    override suspend fun save(userId: String, referenceId: String, param: SaveParam): String {
        val quotaAllocation =  QuotaAllocation(
            userId = userId,
            referenceId = referenceId,
            status = QuotaAllocationStatus.PROCESSING.toString(),

            type = param.type,
            quota = param.quota,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
        )

        val doc: InsertOneResult = col.insertOne(quotaAllocation)

        return when {
            doc.wasAcknowledged() -> quotaAllocation.id.toString()
            else -> throw IllegalStateException("Insert was not acknowledged")
        }
    }

    override suspend fun update(userId: String, referenceId: String, param: UpdateParam): UpdateResult {
        val filter = Filters.and(
            Filters.eq(QuotaAllocation::userId.name, userId),
            Filters.eq(QuotaAllocation::referenceId.name, referenceId),
        )

        val updates = Updates.combine(
            Updates.set(QuotaAllocation::status.name, param.status),
        )

        val result: com.mongodb.client.result.UpdateResult = col.updateOne(filter, updates)

        return when {
            result.wasAcknowledged() -> UpdateResult(
                status = param.status,
            )
            else -> throw IllegalStateException("Update was not acknowledged")
        }
    }
}