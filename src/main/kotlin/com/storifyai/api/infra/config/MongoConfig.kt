package com.storifyai.api.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component
@PropertySource("classpath:application.properties")
data class MongoConfig(
    @Value("\${spring.data.mongodb.uri}") val uri: String,
    @Value("\${spring.data.mongodb.database}") val database: String
)