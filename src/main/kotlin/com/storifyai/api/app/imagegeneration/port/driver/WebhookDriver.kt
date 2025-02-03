package com.storifyai.api.app.imagegeneration.port.driver

interface WebhookDriver {
    fun save(request: SaveRequest): SaveResponse
}

data class SaveRequest(
    val referenceId: String,
    val status: String,
    val imageUrl: String
)

data class SaveResponse(
    val referenceId: String,
    val status: String,
    val imageUrl: String
)
