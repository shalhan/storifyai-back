package com.storifyai.api.app.imagegeneration.port.driver

interface ControllerDriver {
    fun generate(param: GenerateRequest): GenerateResponse
}

data class GenerateRequest(val userId: String, val projectId:String, val number: Int)
data class GenerateResponse(val id: String)