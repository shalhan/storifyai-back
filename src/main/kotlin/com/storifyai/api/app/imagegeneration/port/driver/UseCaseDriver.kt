package com.storifyai.api.app.imagegeneration.port.driver

interface UseCaseDriver {
    suspend fun generate(userId: String, projectId: String, sceneId: String, param: GenerateParam): GenerateResult
    suspend fun save(userId: String, param: SaveParam): SaveResult
}

data class GenerateParam(
    val generatedImageId: String,
    val prompt: String,
    val width: Int,
    val height: Int,
    val numOfImages: Int,
)
data class GenerateResult(val referenceId: String)

data class SaveParam(
    val referenceId: String,
    val status: String,
    val imageUrl: String
)

data class SaveResult(
    val referenceId: String,
    val status: String,
    val imageUrl: String
)