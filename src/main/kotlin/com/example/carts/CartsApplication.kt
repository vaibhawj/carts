package com.example.carts

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Carts API", version = "1.0", description = "API for managing shopping carts"))
class CartsApplication

fun main(args: Array<String>) {
	runApplication<CartsApplication>(*args)
}
