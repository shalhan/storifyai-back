package com.storifyai.api.app.imagegeneration.port.driver

interface UseCaseDriver {
    fun generate(param: GenerateParam): GenerateResult
}

data class GenerateParam(val id: String)
data class GenerateResult(val id: String)
