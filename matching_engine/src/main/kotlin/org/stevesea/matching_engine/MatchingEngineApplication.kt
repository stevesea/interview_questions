package org.stevesea.matching_engine

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class MatchingEngineApplication {
    @Bean
    open fun kotlinJacksonModule() = KotlinModule()
}

fun main(args: Array<String>) {
    SpringApplication.run(MatchingEngineApplication::class.java, *args)
}