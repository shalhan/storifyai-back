package com.storifyai.api.infra.adapter.driven.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.project.port.driven.FindResult
import com.storifyai.api.app.project.port.driven.RepositoryDriven
import com.storifyai.api.app.project.port.driven.UpdateResult
import com.storifyai.api.app.project.port.driven.UpdateParam
import com.mongodb.client.result.UpdateResult as _UpdateResult
import com.storifyai.api.infra.adapter.driven.mongo.entity.Project
import com.storifyai.api.infra.adapter.driven.mongo.entity.Scene
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import java.time.Instant

class ProjectMongo(db: MongoDatabase) : RepositoryDriven {
    private val col: MongoCollection<Project> = db.getCollection<Project>("projects")

    override suspend fun save(userId: String, title: String): String {
        val project =  Project(
            userId = userId,
            title = title,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            deletedDate = null,
        )

        val doc: InsertOneResult = col.insertOne(project)
        return when {
            doc.wasAcknowledged() -> project.id.toString()
            else -> throw IllegalStateException("Insert was not acknowledged")
        }
    }

    override suspend fun update(userId: String, projectId: String, param: UpdateParam): UpdateResult {
        val now = Instant.now()
        val filter = Filters.and(
            Filters.eq("_id", ObjectId(projectId)),
            Filters.eq(Project::userId.name, userId),
            Filters.exists(Scene::deletedDate.name, false),
        )
        val updates = Updates.combine(
            Updates.set(Project::title.name, param.title),
            Updates.set(Project::lastModifiedDate.name, now),
        )

        val result: _UpdateResult = col.updateOne(filter, updates)

        return when {
            result.wasAcknowledged() -> UpdateResult(title = param.title, updatedDate = now)
            else -> throw IllegalStateException("Update was not acknowledged")
        }
    }

    override suspend fun delete(userId: String, projectId: String): String {
        val filter = Filters.and(
            Filters.eq("_id", ObjectId(projectId)),
            Filters.eq(Project::userId.name, userId),
            Filters.exists(Scene::deletedDate.name, false),
        )

        val update = Updates.set(Project::deletedDate.name, Instant.now())

        val result: _UpdateResult = col.updateOne(filter, update)

        return when {
            result.wasAcknowledged() -> projectId
            else -> throw IllegalStateException("Update was not acknowledged")
        }
    }

    override suspend fun findAll(userId: String): List<FindResult> {
        val newResult = mutableListOf<FindResult>()
        val filter = Filters.and(
            Filters.eq(Project::userId.name, userId),
            Filters.exists(Scene::deletedDate.name, false),
        )
        val projection = Projections.include(
            Project::title.name,
            Project::userId.name,
            Project::createdDate.name,
            Project::lastModifiedDate.name,
            Project::deletedDate.name
        )

        val result: FindFlow<Project> = col.find<Project>(filter)
            .projection(projection)
            .sort(Sorts.descending(Project::createdDate.name))

        result.collect {
            newResult.add(FindResult(it.id.toString(), it.userId, it.title, it.createdDate, it.lastModifiedDate, it.deletedDate))
        }

        return newResult
    }

    override suspend fun findOneById(userId: String, projectId: String): FindResult? {
        val filter = Filters.and(
            Filters.eq(ObjectId(projectId)),
            Filters.eq(Project::userId.name, userId),
            Filters.exists(Scene::deletedDate.name, false),
        )
        val projection = Projections.include(
            Project::title.name,
            Project::userId.name,
            Project::createdDate.name,
            Project::lastModifiedDate.name,
            Project::deletedDate.name
        )

        val result = col.find<Project>(filter)
            .projection(projection)
            .firstOrNull() ?: return null

        return FindResult(
            result.id.toString(), result.userId, result.title, result.createdDate, result.lastModifiedDate, result.deletedDate
        )
    }
}