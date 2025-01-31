package com.storifyai.api.infra.adapter.driven.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult as _UpdateResult
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.scene.port.driven.*
import com.storifyai.api.infra.adapter.driven.mongo.model.Project
import com.storifyai.api.infra.adapter.driven.mongo.model.Prompt
import com.storifyai.api.infra.adapter.driven.mongo.model.Scene
import com.storifyai.api.infra.adapter.driven.mongo.model.Setting
import org.bson.types.ObjectId
import java.time.Instant

class SceneMongo(db: MongoDatabase): RepositoryDriven {
    private val col: MongoCollection<Scene> = db.getCollection<Scene>("scenes")

    override suspend fun save(userId: String, projectId: String, param: SaveParam): String {
        val scene =  Scene(
            userId = userId,
            projectId = projectId,
            setting = Setting(
                isFull = param.setting.isFull,
                background = param.setting.background
            ),
            prompt = Prompt(
                characters = param.prompt.characters,
                style = param.prompt.style,
                background = param.prompt.background,
                detail = param.prompt.detail
            ),
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            deletedDate = null,
        )

        val doc: InsertOneResult = col.insertOne(scene)

        return when {
            doc.wasAcknowledged() -> scene.id.toString()
            else -> throw IllegalStateException("Insert was not acknowledged")
        }
    }

    override suspend fun update(
        userId: String,
        projectId: String,
        sceneId: String,
        param: UpdateParam
    ): UpdateResult {
        val now = Instant.now()
        val filter = Filters.and(
            Filters.eq("_id", ObjectId(sceneId)),
            Filters.eq(Scene::userId.name, userId),
            Filters.eq(Scene::projectId.name, projectId),
            Filters.or(
                Filters.ne(Scene::deletedDate.name, null),
                Filters.exists(Scene::deletedDate.name, false),
            )
        )
        val updates = Updates.combine(
            Updates.set(Scene::setting.name, Setting(isFull = param.setting.isFull, background = param.setting.background)),
            Updates.set(Scene::prompt.name, Prompt(
                characters = param.prompt.characters,
                style = param.prompt.style,
                background = param.prompt.background,
                detail = param.prompt.detail
            )),
            Updates.set(Scene::lastModifiedDate.name, now)
        )

        val result: _UpdateResult = col.updateOne(filter, updates)

        return when {
            result.wasAcknowledged() -> UpdateResult(
                setting = SettingResult(
                    isFull = param.setting.isFull,
                    background = param.setting.background
                ),
                prompt = PromptResult(
                    characters = param.prompt.characters,
                    background = param.prompt.background,
                    style = param.prompt.style,
                    detail = param.prompt.detail
                ),
                updatedDate = now,
            )
            else -> throw IllegalStateException("Update was not acknowledged")
        }
    }

    override suspend fun delete(userId: String, projectId: String, sceneId: String): String {
        val filter = Filters.and(
            Filters.eq("_id", ObjectId(sceneId)),
            Filters.eq(Scene::userId.name, userId),
            Filters.eq(Scene::projectId.name, projectId),
            Filters.or(
                Filters.ne(Scene::deletedDate.name, null),
                Filters.exists(Scene::deletedDate.name, false),
            )
        )
        val update = Updates.set(Scene::deletedDate.name, Instant.now())

        val result: _UpdateResult = col.updateOne(filter, update)

        return when {
            result.wasAcknowledged() -> sceneId
            else -> throw IllegalStateException("Delete was not acknowledged")
        }
    }

    override suspend fun findByProjectId(
        userId: String,
        projectId: String
    ): List<FindResult> {
        val newResult = mutableListOf<FindResult>()
        val filter = Filters.and(
            Filters.eq(Scene::userId.name, userId),
            Filters.eq(Scene::projectId.name, projectId),
            Filters.or(
                Filters.ne(Scene::deletedDate.name, null),
                Filters.exists(Scene::deletedDate.name, false),
            )
        )

        val result: FindFlow<Scene> = col.find<Scene>(filter)
            .sort(Sorts.descending(Project::createdDate.name))


        result.collect {
            newResult.add(FindResult(
                it.id.toString(),
                it.userId,
                it.projectId,
                SettingResult(
                    isFull = it.setting.isFull,
                    background = it.setting.background
                ),
                PromptResult(
                    characters = it.prompt.characters,
                    style = it.prompt.style,
                    background = it.prompt.background,
                    detail = it.prompt.detail
                ),
                createdDate = it.createdDate,
                updatedDate = it.lastModifiedDate,
                deletedDate = it.deletedDate
            ))
        }

        return newResult
    }

}