package com.storifyai.api.infra.adapter.driven.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult as _UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.scene.port.driven.*
import com.storifyai.api.app.scene.port.driven.SceneDetail
import com.storifyai.api.infra.adapter.driven.mongo.entity.*
import com.storifyai.api.infra.adapter.driven.mongo.entity.SceneDetail as _SceneDetail
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson
import java.time.Instant

class SceneMongo(db: MongoDatabase): RepositoryDriven {
    private val col: MongoCollection<Scene> = db.getCollection<Scene>("scenes")

    override suspend fun save(userId: String, projectId: String, params: List<SaveParam>): String {
        val scene =  Scene(
            userId = userId,
            projectId = projectId,
            details = params.map {
                _SceneDetail(
                    number = it.number,
                    imageURL = it.imageURL ?: "",
                    imageReferenceId = it.imageReferenceId ?: "",
                    setting = Setting(
                        isFull = it.setting.isFull ?: true,
                        background = it.setting.background ?: ""
                    ),
                    prompt = Prompt(
                        characters = it.prompt.characters,
                        style = it.prompt.style ?: "",
                        background = it.prompt.background ?: "",
                        detail = it.prompt.detail ?: ""
                    ),
                )
            },
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
        params: List<UpdateParam>
    ): UpdateResult {
        val now = Instant.now()

        val filter = filterByMainIds(userId, projectId)
        val updates = Updates.combine(
            Updates.set(Scene::details.name, params.map {
                _SceneDetail(
                    number = it.number,
                    imageURL = it.imageURL ?: "",
                    imageReferenceId = it.imageReferenceId ?: "",
                    setting = Setting(
                        isFull = it.setting.isFull ?: true,
                        background = it.setting.background ?: ""
                    ),
                    prompt = Prompt(
                        characters = it.prompt.characters,
                        style = it.prompt.style ?: "",
                        background = it.prompt.background ?: "",
                        detail = it.prompt.detail ?: ""
                    )
                )
            }),
            Updates.set(Scene::lastModifiedDate.name, Instant.now())
        )

        val result = col.updateOne(filter, updates)

        return when {
            result.wasAcknowledged() -> UpdateResult(
                details = params.map {
                    SceneDetail(
                        number = it.number,
                        setting = SettingResult(
                            isFull = it.setting.isFull ?: true,
                            background = it.setting.background ?: ""
                        ),
                        prompt = PromptResult(
                            characters = it.prompt.characters,
                            background = it.prompt.background ?: "",
                            style = it.prompt.style ?: "",
                            detail = it.prompt.detail ?: ""
                        ),
                        imageURL = it.imageURL,
                        imageReferenceId = it.imageReferenceId,
                    )
                },
                updatedDate = now
            )
            else -> throw IllegalStateException("Update was not acknowledged")
        }
    }

    override suspend fun delete(userId: String, projectId: String): String {
        val filter = filterByMainIds(userId, projectId)
        val update = Updates.set(Scene::deletedDate.name, Instant.now())

        val result: _UpdateResult = col.updateOne(filter, update)

        return when {
            result.wasAcknowledged() -> projectId
            else -> throw IllegalStateException("Delete was not acknowledged")
        }
    }

    override suspend fun findOne(userId: String, projectId: String): FindResult? {
        val filter = filterByMainIds(userId, projectId)

        val result: Scene? = col.find<Scene>(filter)
            .firstOrNull()

        return when {
            result != null -> newFindResult(result)
            else -> null
        }
    }

    private fun newFindResult(scene: Scene): FindResult {
        return FindResult(
            id = scene.id.toString(),
            userId = scene.userId,
            projectId = scene.projectId,
            details = scene.details.map {
                SceneDetail(
                    number = it.number,
                    setting = SettingResult(
                        isFull = it.setting.isFull ?: true,
                        background = it.setting.background ?: ""
                    ),
                    prompt = PromptResult(
                        characters = it.prompt.characters,
                        background = it.prompt.background ?: "",
                        style = it.prompt.style ?: "",
                        detail = it.prompt.detail ?: ""
                    ),
                    imageURL = it.imageURL,
                    imageReferenceId = it.imageReferenceId,
                )
            },
            createdDate = scene.createdDate,
            updatedDate = scene.lastModifiedDate,
            deletedDate = scene.deletedDate
        )
    }

    private fun filterByMainIds(userId: String, projectId: String): Bson {
        return Filters.and(
            Filters.eq(Scene::userId.name, userId),
            Filters.eq(Scene::projectId.name, projectId),
            Filters.or(
                Filters.ne(Scene::deletedDate.name, null),
                Filters.exists(Scene::deletedDate.name, false),
            )
        )
    }

}



