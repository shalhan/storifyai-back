package com.storifyai.api.infra.adapter.driven.mongo

import com.storifyai.api.app.imagegeneration.port.driven.RepositoryDriven
import com.storifyai.api.app.imagegeneration.port.driven.SaveParam
import com.storifyai.api.app.imagegeneration.port.driven.SaveResult

class ImageGenerationMongo: RepositoryDriven {
    override fun save(param: SaveParam): SaveResult {
        TODO("Not yet implemented")
    }
}