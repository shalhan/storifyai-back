package com.storifyai.api.infra.adapter.driven.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.client.result.InsertOneResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.subscription.port.driven.*
import com.storifyai.api.infra.adapter.driven.mongo.entity.Subscription
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.temporal.ChronoUnit

class SubscriptionMongo(db: MongoDatabase): RepositoryDriven {
    private val col: MongoCollection<Subscription> = db.getCollection<Subscription>("subscriptions")

    override suspend fun save(userId: String, param: SaveParam): String {
        val subscription =  Subscription(
            userId = userId,
            type = param.type,
            quota = param.quota,
            remainingQuota = param.quota,
            startDate = Instant.now(),
            endDate = Instant.now().plus(1, ChronoUnit.MONTHS),
            lastModifiedDate = Instant.now(),
        )

        val doc: InsertOneResult = col.insertOne(subscription)

        return when {
            doc.wasAcknowledged() -> subscription.id.toString()
            else -> throw IllegalStateException("Insert was not acknowledged")
        }
    }

    override suspend fun update(userId: String, param: UpdateParam): UpdateResult {
        val filter = Filters.and(
            Filters.eq(Subscription::userId.name, userId),
            Filters.and(
                Filters.gte(Subscription::startDate.name, Instant.now()),
                Filters.lte(Subscription::endDate.name, Instant.now()),
            )
        )

        val updates = Updates.combine(
            Updates.set(Subscription::remainingQuota.name, param.remainingQuota),
        )

        val result: com.mongodb.client.result.UpdateResult = col.updateOne(filter, updates)

        return when {
            result.wasAcknowledged() -> UpdateResult(
                remainingQuota = param.remainingQuota,
            )
            else -> throw IllegalStateException("Update was not acknowledged")
        }
    }

    override suspend fun findOneByDate(userId: String, date: Instant): FindResult {
        val filter = Filters.and(
            Filters.eq(Subscription::userId.name, userId),
            Filters.and(
                Filters.gte(Subscription::startDate.name, Instant.now()),
                Filters.lte(Subscription::endDate.name, Instant.now()),
            )
        )

        val result: Subscription? = col.find<Subscription>(filter)
            .sort(Sorts.ascending(Subscription::endDate.name))
            .firstOrNull()

        return when {
            result != null -> FindResult(
                id = result.id.toString(),
                userId = userId,
                type = result.type,
                quota = result.quota,
                remainingQuota = result.remainingQuota,
                startDate = result.startDate,
                endDate = result.endDate,
            )
            else -> throw IllegalStateException("Subscription not found")
        }
    }
}