package com.storifyai.api.app.imagegeneration.port.driver

interface ControllerDriver {
    fun generate(param: GenerateRequest): GenerateResponse
}

data class GenerateRequest(val id: String)
data class GenerateResponse(val id: String)