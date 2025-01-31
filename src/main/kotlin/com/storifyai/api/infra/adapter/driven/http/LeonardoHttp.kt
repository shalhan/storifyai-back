package com.storifyai.api.infra.adapter.driven.http

import com.storifyai.api.app.imagegeneration.port.driven.GenerateParam
import com.storifyai.api.app.imagegeneration.port.driven.GenerateResult
import com.storifyai.api.app.imagegeneration.port.driven.OutboundDriven

class LeonardoHttp: OutboundDriven {
    override fun generate(param: GenerateParam): GenerateResult {
        TODO("Not yet implemented")
    }
}