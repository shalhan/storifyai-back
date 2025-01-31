package com.storifyai.api.app.imagegeneration.port.driven

interface OutboundDriven {
    fun generate(param: GenerateParam): GenerateResult
}

data class GenerateParam(val id: String)
data class GenerateResult(val id: String)
