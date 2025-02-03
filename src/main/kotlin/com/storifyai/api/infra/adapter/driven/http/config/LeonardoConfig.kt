package com.storifyai.api.infra.adapter.driven.http.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:application.properties")
data class LeonardoConfig (
    @Value("\${outbound.http.leonardoai.baseurl}")
    val baseUrl: String,

    @Value("\${outbound.http.leonardoai.apiKey}")
    val apiKey: String,
)