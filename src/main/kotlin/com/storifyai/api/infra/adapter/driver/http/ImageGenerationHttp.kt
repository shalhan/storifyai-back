package com.storifyai.api.infra.adapter.driver.http

import com.storifyai.api.app.imagegeneration.port.driver.ControllerDriver
import com.storifyai.api.app.imagegeneration.port.driver.GenerateRequest
import com.storifyai.api.app.imagegeneration.port.driver.GenerateResponse
import com.storifyai.api.app.imagegeneration.port.driver.UseCaseDriver
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/image-generations")
class ImageGenerationHttp(private val useCase: UseCaseDriver): ControllerDriver {

    @PostMapping("/")
    override fun generate(@RequestBody param: GenerateRequest): GenerateResponse {
        TODO("Not yet implemented")
    }
}