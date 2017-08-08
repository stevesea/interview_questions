package org.stevesea.sbe

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class SbeApplication {
    @Bean
    open fun kotlinJacksonModule() = KotlinModule()
}

fun main(args: Array<String>) {
    SpringApplication.run(SbeApplication::class.java, *args)
}