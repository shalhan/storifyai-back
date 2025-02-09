package com.storifyai.api.infra.config.wrapper

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.util.*

class CustomHttpServletRequestWrapper(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {
    private val customHeaders: MutableMap<String, String> = HashMap()

    fun addHeader(name: String, value: String) {
        customHeaders[name] = value
    }

    override fun getHeader(name: String): String {
        return if (customHeaders.containsKey(name)) customHeaders[name]!! else super.getHeader(name)
    }

    override fun getHeaders(name: String): Enumeration<String> {
        return if (customHeaders.containsKey(name)) Collections.enumeration(
            listOf(
                customHeaders[name]
            )
        ) else super.getHeaders(name)
    }

    override fun getHeaderNames(): Enumeration<String> {
        val allHeaders = Collections.list(super.getHeaderNames())
        allHeaders.addAll(customHeaders.keys)
        return Collections.enumeration(allHeaders)
    }
}