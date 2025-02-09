package com.storifyai.api.infra.config

import com.mongodb.kotlin.client.coroutine.MongoClient
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
    fun mongoDb() = MongoClient.create("mongodb://localhost:27017").getDatabase("storifyai")

    @Bean
    fun httpClient() = RestClient.create()

    @Bean
    fun leonardoConfig() = LeonardoConfig("leonardo", "http://localhost:27017")

    @Bean
    fun leonardoHttp() = LeonardoHttp(httpClient(), leonardoConfig())

    @Bean
    fun imageGenerationMongo() = ImageGenerationMongo()

    @Bean
    fun imageGenerationUseCase() = ImageGenerationUseCase(leonardoHttp(), sceneUseCase())

    @Bean
    fun projectMongo() = ProjectMongo(mongoDb())

    @Bean
    fun sceneMongo() = SceneMongo(mongoDb())

    @Bean
    fun projectUseCase() = ProjectUseCase(projectMongo())

    @Bean
    fun sceneUseCase() = SceneUseCase(sceneMongo())
}