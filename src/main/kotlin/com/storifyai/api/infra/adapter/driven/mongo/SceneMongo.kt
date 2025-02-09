package com.storifyai.api.infra.adapter.driven.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult as _UpdateResult
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.scene.port.driven.*
import com.storifyai.api.infra.adapter.driven.mongo.entity.Project
import com.storifyai.api.infra.adapter.driven.mongo.entity.Prompt
import com.storifyai.api.infra.adapter.driven.mongo.entity.Scene
import com.storifyai.api.infra.adapter.driven.mongo.entity.Setting
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import java.time.Instant

class SceneMongo(private val client: MongoClient, private val db: MongoDatabase): RepositoryDriven {
    private val col = db.getCollection<Scene>("scenes")

    override suspend fun save(userId: String, projectId: String, param: SaveParam): String {
        val scene = newSceneEntity(userId, projectId, param)

        val doc: InsertOneResult = col.insertOne(scene)

        return when {
            doc.wasAcknowledged() -> scene.id.toString()
            else -> throw IllegalStateException("Insert was not acknowledged")
        }
    }

    override suspend fun bulkSave(userId: String, projectId: String, params: List<SaveParam>): List<String> {
        val session = client.startSession()
        try {
            session.startTransaction()

            val filter = Filters.and(
                Filters.eq(Scene::userId.name, userId),
                Filters.eq(Scene::projectId.name, projectId),
                Filters.or(
                    Filters.ne(Scene::deletedDate.name, null),
                    Filters.exists(Scene::deletedDate.name, false),
                )
            )

            val scenes = params.map {
                newSceneEntity(userId, projectId, it)
            }

            col.deleteMany(filter)

            val doc = col.insertMany(scenes)

            session.commitTransaction()

             when {
                 doc.wasAcknowledged() -> return scenes.map { it.id.toString() }
                 else -> throw IllegalStateException("Bulk insert was not acknowledged")
            }
        } catch (e: Exception) {
            session.abortTransaction()
            throw IllegalStateException("Bulk insert failed")
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

        val result = col.findOneAndUpdate(filter, updates) ?: return throw IllegalStateException("Insert was not acknowledged")

        return UpdateResult(
            imageURL = result.imageUrl,
            imageReferenceId = result.imageReferenceId,
            setting = SettingResult(
                isFull = result.setting.isFull,
                background = result.setting.background
            ),
            prompt = PromptResult(
                characters = result.prompt.characters,
                background = result.prompt.background,
                style = result.prompt.style,
                detail = result.prompt.detail
            ),
            updatedDate = now,
        )
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

    override suspend fun findManyByProjectId(
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
            .sort(Sorts.descending(Scene::number.name))


        result.collect {
            newResult.add(newFindResult(it))
        }

        return newResult
    }

    override suspend fun findOne(userId: String, projectId: String, sceneId: String): FindResult? {
        val filter = Filters.and(
            Filters.eq(ObjectId(sceneId)),
            Filters.eq(Scene::userId.name, userId),
            Filters.eq(Scene::projectId.name, projectId),
            Filters.or(
                Filters.ne(Scene::deletedDate.name, null),
                Filters.exists(Scene::deletedDate.name, false),
            )
        )

        val result: Scene? = col.find<Scene>(filter)
            .firstOrNull()

        return when {
            result != null -> newFindResult(result)
            else -> null
        }
    }

    override suspend fun findOneByReferenceId(userId: String, imageReferenceId: String): FindResult? {
        val filter = Filters.and(
            Filters.eq(Scene::userId.name, userId),
            Filters.eq(Scene::imageReferenceId.name, imageReferenceId),
            Filters.or(
                Filters.ne(Scene::deletedDate.name, null),
                Filters.exists(Scene::deletedDate.name, false),
            )
        )

        val result: Scene? = col.find<Scene>(filter)
            .firstOrNull()

        return when {
            result != null -> newFindResult(result)
            else -> null
        }
    }

    private fun newSceneEntity(userId: String, projectId: String, param: SaveParam): Scene {
        return Scene(
            userId = userId,
            projectId = projectId,
            imageReferenceId = param.imageReferenceId,
            imageUrl = param.imageURL,
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
            number = param.number,
        )
    }

    private fun newFindResult(scene: Scene): FindResult {
        return FindResult(
            scene.id.toString(),
            scene.userId,
            scene.projectId,
            scene.imageUrl,
            scene.imageReferenceId,
            SettingResult(
                isFull = scene.setting.isFull,
                background = scene.setting.background
            ),
            PromptResult(
                characters = scene.prompt.characters,
                style = scene.prompt.style,
                background = scene.prompt.background,
                detail = scene.prompt.detail
            ),
            createdDate = scene.createdDate,
            updatedDate = scene.lastModifiedDate,
            deletedDate = scene.deletedDate
        )
    }

}