package com.storifyai.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StorifyaiCoreApplication

fun main(args: Array<String>) {
    runApplication<StorifyaiCoreApplication>(*args)
}
