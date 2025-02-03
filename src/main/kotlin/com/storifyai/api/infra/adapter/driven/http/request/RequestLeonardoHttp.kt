package com.storifyai.api.infra.adapter.driven.http.request

data class GenerateImageRequestLeonardoHttp (
    val model: String,
    val contrast: Number,
    val prompt: String,
    val num_images: Number,
    val width: Number,
    val height: Number,
    val alchemy: Boolean,
    val styleUUID: String,
    val enhancePrompt: Boolean,
)