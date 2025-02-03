package com.storifyai.api.infra.adapter.driven.http.response

data class GenerateImageResponseLeonardo (
    val sdGenerationJob: SdGenerationJob
)

data class SdGenerationJob (
    val generationId: String,
    val apiCreditCost: Number,
)