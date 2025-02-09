package com.storifyai.api.infra.adapter.driven.http

import com.storifyai.api.app.imagegeneration.port.driven.GenerateParam
import com.storifyai.api.app.imagegeneration.port.driven.GenerateResult
import com.storifyai.api.app.imagegeneration.port.driven.OutboundDriven
import com.storifyai.api.infra.adapter.driven.http.config.LeonardoConfig
import com.storifyai.api.infra.adapter.driven.http.response.GenerateImageResponseLeonardo
import com.storifyai.api.infra.adapter.driven.http.response.SdGenerationJob
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import java.net.URLEncoder

class LeonardoHttp(
    @Autowired
    private val httpClient: RestClient,

    @Autowired
    private val config: LeonardoConfig
): OutboundDriven {
    override fun generateImage(param: GenerateParam): GenerateResult {
        val response = httpClient.post()
            .uri(URLEncoder.encode(config.baseUrl+"/generations"))
            .header("authorization", "Bearer ${config.apiKey}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(param)
            .exchange { _, response ->
                if (response.statusCode.is4xxClientError) {
                    throw IllegalStateException("fail generate response")
                } else {
                    GenerateImageResponseLeonardo(
                        sdGenerationJob = SdGenerationJob(
                            generationId = "",
                            apiCreditCost = 1
                        )
                    )
                }
            }

        if (response == null) throw IllegalStateException("fail generate response")

        return GenerateResult(response.sdGenerationJob.generationId)
    }
}