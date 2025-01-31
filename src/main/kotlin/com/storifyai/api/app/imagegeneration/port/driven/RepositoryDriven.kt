package com.storifyai.api.app.imagegeneration.port.driven

interface RepositoryDriven {
    fun save(param: SaveParam): SaveResult
}

data class SaveParam(val id: String)
data class SaveResult(val id: String)
