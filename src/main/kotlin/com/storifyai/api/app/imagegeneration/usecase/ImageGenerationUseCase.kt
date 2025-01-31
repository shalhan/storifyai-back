package com.storifyai.api.app.imagegeneration.usecase

import com.storifyai.api.app.imagegeneration.port.driven.OutboundDriven
import com.storifyai.api.app.imagegeneration.port.driven.RepositoryDriven
import com.storifyai.api.app.imagegeneration.port.driver.*

class ImageGenerationUseCase(private val outboundAdapter: OutboundDriven, private val repoAdapter: RepositoryDriven) : UseCaseDriver {
    override fun generate(param: GenerateParam): GenerateResult {
        TODO("will be working on it later")
    }
}