package com.storifyai.api.app.imagegeneration.port.driven

interface OutboundDriven {
    fun generateImage(param: GenerateParam): GenerateResult
}

data class GenerateParam(
    val userId: String,
    val projectId: String,
    val sceneId: String,
    val generatedImageId: String,
    val prompt: String,
    val width: Int,
    val height: Int,
    val numOfImages: Int,
)
data class GenerateResult(val referenceId: String)