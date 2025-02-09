package com.storifyai.api.infra.config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.storifyai.api.app.imagegeneration.usecase.ImageGenerationUseCase
import com.storifyai.api.app.project.usecase.ProjectUseCase
import com.storifyai.api.app.scene.usecase.SceneUseCase
import com.storifyai.api.infra.adapter.driven.http.LeonardoHttp
import com.storifyai.api.infra.adapter.driven.http.config.LeonardoConfig
import com.storifyai.api.infra.adapter.driven.mongo.ImageGenerationMongo
import com.storifyai.api.infra.adapter.driven.mongo.ProjectMongo
import com.storifyai.api.infra.adapter.driven.mongo.SceneMongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class DIConfig {
    @Bean
    fun mongoClient(mongoConfig: MongoConfig) = MongoClient.create(mongoConfig.uri)

    @Bean
    fun mongoDb(mongoConfig: MongoConfig) = mongoClient(mongoConfig).getDatabase(mongoConfig.database)

    @Bean
    fun httpClient() = RestClient.create()

    @Bean
    fun leonardoConfig() = LeonardoConfig("leonardo", "http://localhost:27017")

    @Bean
    fun leonardoHttp() = LeonardoHttp(httpClient(), leonardoConfig())

    @Bean
    fun imageGenerationMongo() = ImageGenerationMongo()

    @Bean
    fun imageGenerationUseCase(sceneUseCase: SceneUseCase) = ImageGenerationUseCase(leonardoHttp(), sceneUseCase)

    @Bean
    fun projectMongo(mongoDb: MongoDatabase) = ProjectMongo(mongoDb)

    @Bean
    fun sceneMongo(mongoClient: MongoClient, mongoDb: MongoDatabase) = SceneMongo(mongoClient, mongoDb)

    @Bean
    fun projectUseCase(projectMongo: ProjectMongo) = ProjectUseCase(projectMongo)

    @Bean
    fun sceneUseCase(sceneMongo: SceneMongo, projectUseCase: ProjectUseCase) = SceneUseCase(projectUseCase, sceneMongo)
}
